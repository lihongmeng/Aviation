package com.jxntv.sensordata;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.jxntv.base.Constant;
import com.jxntv.base.entity.ExitPageData;
import com.jxntv.base.environment.EnvironmentManager;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.GroupInfo;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.sp.SharedPrefsWrapper;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.sensordata.utils.InteractType;
import com.jxntv.stat.BuildConfig;
import com.jxntv.stat.R;
import com.jxntv.stat.StatPid;
import com.jxntv.stat.StatUtils;
import com.jxntv.utils.DeviceId;
import com.jxntv.utils.LogUtils;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataGPSLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * @author huangwei
 * date : 2021/7/6
 * desc : 神策埋点工具
 **/
public class GVideoSensorDataManager {

    private final String CHANNEL = "YL_channel";

    private final HashMap<String, Long> pageBrowsingTime = new HashMap<>();
    private Observer<String> enterPageObservable;
    private Observer<ExitPageData> exitPageObservable;

    private GVideoSensorDataManager() {
    }

    private static final class INNER {
        private static final GVideoSensorDataManager MANAGER = new GVideoSensorDataManager();
    }

    public static GVideoSensorDataManager getInstance() {
        return INNER.MANAGER;
    }

    public String getAnonymousId() {
        return SensorsDataAPI.sharedInstance().getAnonymousId();
    }

    /**
     * 初始化
     */
    public void init() {
        SAConfigOptions saConfigOptions = new SAConfigOptions(getServerUrl());
        // 开启全埋点
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK |
                SensorsAnalyticsAutoTrackEventType.APP_START |
                SensorsAnalyticsAutoTrackEventType.APP_END |
                SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN)
                //开启 Log
                .enableLog(BuildConfig.DEBUG);
        if (!BuildConfig.DEBUG) {
            saConfigOptions.enableTrackAppCrash();
        }
        saConfigOptions.enableTrackPush(true);
        saConfigOptions.enableTrackScreenOrientation(true);
        SensorsDataAPI.startWithConfigOptions(GVideoRuntime.getAppContext(), saConfigOptions);

        SensorsDataAPI.sharedInstance().clearSuperProperties();

        if (isCanUploadMessage()) {
            try {
                JSONObject object = new JSONObject();
                object.put("AppName", GVideoRuntime.getAppContext().getResources().getString(R.string.app_name));
                object.put("platform_type", "Android");
                object.put("is_login", PluginManager.get(AccountPlugin.class).hasLoggedIn());
                object.put("jslogin_id", DeviceId.get());
                object.put("app_version", StatUtils.getAppVersionName(GVideoRuntime.getAppContext()));

                SensorsDataAPI.sharedInstance().registerSuperProperties(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SensorsDataAPI.sharedInstance().trackAppInstall();
        }

        enterPageObservable = this::enterPage;
        exitPageObservable = this::dealExitPage;

        GVideoEventBus.get(Constant.EVENT_MSG.PAGE_ENTER, String.class).observeForever(enterPageObservable);

        GVideoEventBus.get(Constant.EVENT_MSG.PAGE_EXIT, ExitPageData.class).observeForever(exitPageObservable);

    }

    private void dealExitPage(ExitPageData exitPageData) {
        if (exitPageData == null) {
            return;
        }

        String communityName = exitPageData.communityName;
        if (TextUtils.isEmpty(communityName) && exitPageData.videoModel != null) {
            exitPageData.communityName = exitPageData.videoModel.getGroupName();
        }

        Log.d("exitPage", "tenantId -->>" + exitPageData.tenantId);
        Log.d("exitPage", "communityName -->>" + exitPageData.communityName);

        if (exitPageData.videoModel != null) {
            exitPostView(exitPageData.videoModel);
            return;
        }

        exitPage(exitPageData);

    }

    public void onHomeRelease() {

        if (enterPageObservable != null) {
            GVideoEventBus.get(Constant.EVENT_MSG.PAGE_ENTER, String.class).removeObserver(enterPageObservable);
            enterPageObservable = null;
        }
        if (exitPageObservable != null) {
            GVideoEventBus.get(Constant.EVENT_MSG.PAGE_EXIT, ExitPageData.class).removeObserver(exitPageObservable);
        }
    }

    public void setLocation(final double latitude, final double longitude) {
        if (isCanUploadMessage()) {
            SensorsDataAPI.sharedInstance().setGPSLocation(latitude, longitude, SensorsDataGPSLocation.CoordinateType.GCJ02);
        }
    }

    /**
     * 设置登录信息
     */
    public void login() {
        try {
            if (PluginManager.get(AccountPlugin.class).hasLoggedIn() && isCanUploadMessage()) {
                JSONObject object = new JSONObject();
                SensorsDataAPI.sharedInstance().login(PluginManager.get(AccountPlugin.class).getUserId());
                object.put("nick_name", PluginManager.get(AccountPlugin.class).getNickName());
                object.put("tel_number", PluginManager.get(AccountPlugin.class).getPhoneNumber());
                object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
                object.put("community_id", PluginManager.get(AccountPlugin.class).getJoinGroup());
                object.put("jslogin_id", DeviceId.get());
                object.put("province", PluginManager.get(AccountPlugin.class).getProvince());
                object.put("city", PluginManager.get(AccountPlugin.class).getCity());
                SharedPrefsWrapper prefsWrapper = new SharedPrefsWrapper(CHANNEL);
                String channelId = prefsWrapper.getString(CHANNEL, "");
                if (!TextUtils.isEmpty(channelId)) {
                    object.put("YL_channel", channelId);
                }
                SensorsDataAPI.sharedInstance().profileSet(object);

                JSONObject object1 = new JSONObject();
                object1.put("is_login", true);
                SensorsDataAPI.sharedInstance().registerSuperProperties(object1);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置渠道
     */
    public void setChannel(String channel) {
        if (TextUtils.isEmpty(channel)) {
            return;
        }
        SharedPrefsWrapper prefsWrapper = new SharedPrefsWrapper(CHANNEL);
        prefsWrapper.putString(CHANNEL, channel);
        try {
            if (isCanUploadMessage()) {
                JSONObject object = new JSONObject();
                object.put("YL_channel", channel);
                SensorsDataAPI.sharedInstance().profileSet(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登出
     */
    public void logout() {
        SensorsDataAPI.sharedInstance().logout();
//        SensorsDataAPI.sharedInstance().profileDelete();
        try {
            JSONObject object = new JSONObject();
            object.put("is_login", false);
            SensorsDataAPI.sharedInstance().registerSuperProperties(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布内容
     *
     * @param communityId       圈子id
     * @param communityName     圈子名称
     * @param userInstitutionId 所属频道频率Id
     * @param userInstitution   所属频道频率名称
     * @param conversationId    话题id
     * @param conversationName  话题名称
     * @param contentId         内容id
     * @param contentName       内容名称
     * @param shareLink         分享链接主域名
     * @param contentType       内容类型  1、视频 2、图文 3、语音
     * @param isQaType          是否是问答
     * @param publishResult     发布结果
     * @param failReason        失败原因
     */
    public void publish(
            Long communityId,
            String communityName,
            Long userInstitutionId,
            String userInstitution,
            Long conversationId,
            String conversationName,
            int contentId,
            String contentName,
            String shareLink,
            int contentType,
            boolean isQaType,
            String publishResult,
            String failReason
    ) {
        try {
            JSONObject object = new JSONObject();
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            object.put("account", accountPlugin.getUserId());
            object.put("nick_name", accountPlugin.getNickName());

            if (communityId != null) {
                object.put("community_id", communityId);
            }

            if (!TextUtils.isEmpty(communityName)) {
                object.put("community_name", communityName);
            }

            if (userInstitutionId != null) {
                object.put("user_institution_id", userInstitutionId);
            }

            if (!TextUtils.isEmpty(userInstitution)) {
                object.put("user_institution", userInstitution);
            }

            if (conversationId != null) {
                object.put("conversation_id", conversationId);
            }

            if (!TextUtils.isEmpty(conversationName)) {
                object.put("conversation_name", conversationName);
            }

            object.put("content_id", contentId);
            object.put("content_name", contentName);
            object.put("share_link", shareLink);
            object.put("content_type", contentType);
            object.put("post_type", isQaType ? 2 : 1);
            object.put("publish_result", publishResult);
            object.put("is_success", TextUtils.isEmpty(failReason));
            object.put("fail_reason", failReason);
            object.put("publish_time", System.currentTimeMillis());
            track("PostContent", object);
            LogUtils.printSALog("发帖：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addLinkFail(String shareLink, CharSequence failReason) {
        Log.d("addLinkFail", "share_link -->>" + shareLink);
        Log.d("addLinkFail", "fail_reason -->>" + failReason);
        try {
            JSONObject object = new JSONObject();
            object.put("share_link", shareLink);
            object.put("fail_reason", failReason.toString());
            track("AddLink_fail", object);
            LogUtils.d("添加链接异常原因");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入圈子
     *
     * @param communityId   圈子id
     * @param communityName 圈子名称
     * @param communityTag  圈子标签
     * @param contentSource 关注按钮所在的页面
     * @param failReason    失败原因
     */
    public void followCommunity(
            Long communityId,
            String communityName,
            Long tenantId,
            String tenantName,
            List<String> communityTag,
            String contentSource,
            String failReason
    ) {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());

            if (communityId != null) {
                object.put("community_id", communityId);
            }

            if (!TextUtils.isEmpty(communityName)) {
                object.put("community_name", communityName);
            }

            if (tenantId != null && tenantId > 0) {
                object.put("user_institution_id", tenantId);
            }

            if (!TextUtils.isEmpty(tenantName)) {
                object.put("user_institution", tenantName);
            }

            object.put("community_tag", communityTag);
            object.put("community_source", contentSource);
            object.put("is_success", TextUtils.isEmpty(failReason));
            object.put("fail_reason", failReason);
            object.put("follow_time", System.currentTimeMillis());
            track("FollowCommunity", object);
            LogUtils.printSALog("关注社区：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消关注圈子
     *
     * @param community_id   圈子id
     * @param community_name 圈子名称
     * @param community_tag  圈子标签
     * @param fail_reason    失败原因
     */
    public void unFollowCommunity(
            long community_id,
            String community_name,
            Long tenantId,
            String tenantName,
            List<String> community_tag,
            String fail_reason
    ) {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("unfollowcommunity_id", community_id);
            object.put("unfollowcommunity_name", community_name);

            if (tenantId != null && tenantId > 0) {
                object.put("user_institution_id", tenantId);
            }

            if (!TextUtils.isEmpty(tenantName)) {
                object.put("user_institution", tenantName);
            }

            object.put("community_tag", community_tag);
            object.put("is_success", TextUtils.isEmpty(fail_reason));
            object.put("fail_reason", fail_reason);
            object.put("unfollow_time", System.currentTimeMillis());
            track("UnFollowCommunity", object);
            LogUtils.printSALog("取消关注圈子：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关注用户
     *
     * @param isFollow     是否关注
     * @param account_name 账号名称
     * @param authorType   作者类型
     * @param fail_reason  失败原因
     */
    public void followAccount(
            boolean isFollow,
            String authorId,
            String account_name,
            @AuthorType int authorType,
            String fail_reason
    ) {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            if (isFollow) {
                object.put("followaccount_name", account_name);
                object.put("followaccount_type", authorType == AuthorType.PGC ? 1 : 2);
                object.put("follow_time", System.currentTimeMillis());
            } else {
                object.put("unfollowaccount_name", account_name);
                object.put("unfollowaccount_type", authorType == AuthorType.PGC ? 1 : 2);
                object.put("unfollow_time", System.currentTimeMillis());
            }
            object.put("is_success", TextUtils.isEmpty(fail_reason));
            object.put("fail_reason", fail_reason);
            track(isFollow ? "FollowAccount" : "UnFollowAccount", object);
            LogUtils.printSALog((isFollow ? "关注用户：" : "取消关注用户：") + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 页面浏览
     */
    public void enterPage(String pid) {
        pageBrowsingTime.put(pid, System.currentTimeMillis());
        String name = StatPid.getPageName(pid);
        if (!TextUtils.isEmpty(name)) {
            LogUtils.printSALog("进入页面：" + name);
        }
    }

    /**
     * 页面浏览
     */
    public void exitPage(ExitPageData exitPageData) {
        if (exitPageData == null) {
            return;
        }
        String pageName = StatPid.getPageName(exitPageData.pageName);
        Long time = pageBrowsingTime.get(exitPageData.pageName);
        if (time == null || TextUtils.isEmpty(pageName)) {
            return;
        }

        if (TextUtils.isEmpty(exitPageData.communityName)
                || exitPageData.tenantId == null
                || TextUtils.isEmpty(exitPageData.tenantName)) {
            exitPageData.communityName = "";
            exitPageData.tenantId = null;
            exitPageData.tenantName = "";
        }

        pageBrowsingTime.remove(exitPageData.pageName);

        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());

            // 针对圈子详情页，社区名称作为页面名称
            if (StatPid.getPageName(StatPid.CIRCLE_DETAIL).equals(pageName)) {
                object.put("source_page", exitPageData.communityName);
            } else {
                object.put("source_page", TextUtils.isEmpty(pageName) ? "" : pageName.trim());
            }

            Log.d("dealExitPage", "pageName -->> " + (TextUtils.isEmpty(pageName) ? "" : pageName.trim()));


            if (!TextUtils.isEmpty(exitPageData.communityName)) {
                object.put("community_name", exitPageData.communityName);
            }
            if (!TextUtils.isEmpty(exitPageData.tenantName)) {
                object.put("user_institution", exitPageData.tenantName);
            }
            if (exitPageData.tenantId != null && exitPageData.tenantId > 0) {
                object.put("user_institution_id", exitPageData.tenantId);
            }
            if (!TextUtils.isEmpty(exitPageData.sourcePage)) {
                object.put("source_scenePage", StatPid.getPageName(exitPageData.sourcePage));
            }

            object.put("start_browsing_time", time);
            object.put("exit_browsing_time", System.currentTimeMillis());
            track("PageBrowsing", object);

            Log.d("GVideoSensorDataManager", "PageBrowsing  ============  Source page -->>" + object.get("source_page"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 当需要对三方SDK的页面埋点时，就不能交由BaseFragment处理,需要自主上报
     *
     * @param sourcePage        当前页面
     * @param communityName     社区名称
     * @param tenantName        频率频道名称
     * @param tenantId          频率频道Id
     * @param startBrowsingTime 开始浏览页面的时间
     */
    public void exitPage(
            String sourcePage,
            String communityName,
            String tenantName,
            Integer tenantId,
            long startBrowsingTime
    ) {
        if (TextUtils.isEmpty(sourcePage)) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("source_page", sourcePage);
            if (!TextUtils.isEmpty(communityName)) {
                object.put("community_name", communityName);
            }
            if (!TextUtils.isEmpty(tenantName)) {
                object.put("user_institution", tenantName);
            }
            if (tenantId != null && tenantId > 0) {
                object.put("user_institution_id", tenantId);
            }
            object.put("source_scenePage", StatPid.getPageName(sourcePage));
            object.put("start_browsing_time", startBrowsingTime);
            object.put("exit_browsing_time", System.currentTimeMillis());
            track("PageBrowsing", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 喜欢内容
     *
     * @param videoModel       内容
     * @param pid              页面id
     * @param isCancelFavorite 是否取消喜欢
     */
    public void favoriteContent(
            VideoModel videoModel,
            String pid,
            boolean isCancelFavorite,
            String fail_reason
    ) {
        clickContent(
                videoModel,
                pid,
                isCancelFavorite ? InteractType.FAVORITE_CANCEL : InteractType.FAVORITE,
                null,
                fail_reason
        );
    }

    /**
     * 内容互动
     *
     * @param videoModel       内容
     * @param pid              页面pid
     * @param interact_type    互动类型
     * @param interact_content 互动内容， 评论时为评论内容， 举报时为举报内容， 其他为null
     * @param fail_reason      失败原因
     */
    public void clickContent(
            VideoModel videoModel,
            String pid,
            @InteractType int interact_type,
            String interact_content,
            String fail_reason
    ) {
        if (videoModel == null
                || (TextUtils.isEmpty(videoModel.getPid()) && TextUtils.isEmpty(pid))) {
            return;
        }
        String nameResult = "";
        AuthorModel authorModel = videoModel.getAuthor();
        if (authorModel != null) {
            nameResult = authorModel.getName();
        }
        if (TextUtils.isEmpty(nameResult)) {
            nameResult = PluginManager.get(AccountPlugin.class).getNickName();
        }
        nameResult = TextUtils.isEmpty(nameResult) ? "" : nameResult;

        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("author_name", nameResult);
            object.put("content_id", videoModel.getId());
            object.put("content_name", videoModel.getTitle());
            object.put("content_type", videoModel.getMediaType());
            object.put("post_type", videoModel.isNotNormalType() ? 2 : 1);
            object.put("content_source", StatPid.getPageName(getVideoModelPidThanOther(videoModel, pid)));
            Log.d("GVideoSensorDataManager", "ClickContent  ============  Content source -->>" + object.get("content_source"));

            object.put("content_tag", videoModel.getLabels());

            GroupInfo groupInfo = videoModel.getGroupInfo();
            if (groupInfo != null) {
                object.put("community_name", videoModel.getGroupName());
                object.put("conversation_name", videoModel.getTopicName());
                object.put("user_institution_id", groupInfo.tenantId);
                object.put("user_institution", groupInfo.tenantName);
            }

            if (null != videoModel.getCreateDate()) {
                object.put("publish_time", videoModel.getCreateDate().getTime());
            }
            object.put("interact_type", interact_type);

            if (authorModel != null
                    && authorModel.tenantIdList != null
                    && !authorModel.tenantIdList.isEmpty()) {
                object.put("author_institution_id", authorModel.tenantIdList);
            }

            if (authorModel != null
                    && authorModel.tenantNameList != null
                    && !authorModel.tenantNameList.isEmpty()) {
                object.put("author_institution", authorModel.tenantNameList);
            }

            object.put("interact_time", System.currentTimeMillis());
            object.put("interact_content", interact_content);
            object.put("is_success", TextUtils.isEmpty(fail_reason));
            object.put("source_page", getSourcePageFromVideoModel(videoModel));
            Log.d("GVideoSensorDataManager", "ClickContent  ============  Source page -->>" + object.get("source_page"));

            object.put("fail_reason", fail_reason);
            track("ClickContent", object);
            LogUtils.printSALog("内容互动：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出详情页
     *
     * @param videoModel 内容
     */
    public void exitPostView(VideoModel videoModel) {
        if (videoModel == null
                || TextUtils.isEmpty(videoModel.getPid())) {
            return;
        }
        String pageName = StatPid.getPageName(videoModel.getPid());

        Long time = pageBrowsingTime.get(videoModel.getPid());
        if (TextUtils.isEmpty(pageName) || time == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());

            GroupInfo groupInfo = videoModel.getGroupInfo();
            if (groupInfo != null) {
                object.put("community_id", groupInfo.getGroupId());

                if (!TextUtils.isEmpty(groupInfo.getGroupName())
                        && groupInfo.tenantId != null
                        && !TextUtils.isEmpty(groupInfo.tenantName)) {
                    object.put("community_name", groupInfo.getGroupName());
                    object.put("user_institution_id", groupInfo.tenantId);
                    object.put("user_institution", groupInfo.tenantName);
                }
                object.put("conversation_id", groupInfo.getTopicId());
                object.put("conversation_name", groupInfo.getTopicName());
                object.put("community_tag", groupInfo.getLabels());
            }
            object.put("content_id", videoModel.getId());
            object.put("content_name", videoModel.getTitle());
            object.put("content_type", videoModel.getMediaType());
            object.put("content_tag", videoModel.getLabels());
            object.put("post_type", videoModel.isNotNormalType() ? 2 : 1);
            object.put("start_browsing_time", time);
            object.put("source_page", StatPid.getPageName(videoModel.getStatFromModel().fromPid));
            Log.d("GVideoSensorDataManager", "PostView  ============  Source page -->>" + object.get("source_page"));

            object.put("exit_browsing_time", System.currentTimeMillis());
            track("PostView", object);
            LogUtils.printSALog("退出页面：+" + pageName + "   帖子浏览：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 帖子曝光
     *
     * @param videoModel 内容
     * @param pid        页面id
     * @param isShow     是否进度页面
     */
    public void postExposure(VideoModel videoModel, String pid, boolean isShow) {
        if (videoModel == null
                || (TextUtils.isEmpty(videoModel.getPid()) && TextUtils.isEmpty(pid))) {
            return;
        }
        String key = getClass().getName() + pid + videoModel.getId() + "postExposure";
        String time = getTime(key, isShow);
        if (TextUtils.isEmpty(time)) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            GroupInfo groupInfo = videoModel.getGroupInfo();
            if (groupInfo != null) {
                object.put("community_id", groupInfo.getGroupId());
                object.put("community_name", groupInfo.getGroupName());
                object.put("user_institution_id", groupInfo.tenantId);
                object.put("user_institution", groupInfo.tenantName);
                object.put("conversation_id", groupInfo.getTopicId());
                object.put("conversation_name", groupInfo.getTopicName());
                object.put("community_tag", groupInfo.getLabels());
            }
            object.put("content_id", videoModel.getId());
            object.put("content_name", videoModel.getTitle());
            object.put("content_type", videoModel.getMediaType());
            object.put("content_tag", videoModel.getLabels());
            object.put("post_type", videoModel.isNotNormalType() ? 2 : 1);
            object.put("exposure_time", time);
            object.put("content_source", StatPid.getPageName(getVideoModelPidThanOther(videoModel, pid)));
            Log.d("GVideoSensorDataManager", "PostExposure  ============  Content source -->>" + object.get("content_source"));

            object.put("exposure_date", System.currentTimeMillis() - (long) Double.parseDouble(time));
            track("PostExposure", object);
            LogUtils.printSALog("帖子曝光：" + new Gson().toJson(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 圈子曝光
     *
     * @param circle 内容
     * @param pid    页面id
     * @param isShow 是否显示
     */
    public void circleExposure(Circle circle, String pid, boolean isShow) {
        String pageName = StatPid.getPageName(pid);
        if (circle == null || TextUtils.isEmpty(pageName)) {
            return;
        }
        String key = getClass().getName() + pid + circle.groupId + "postExposure";
        String time = getTime(key, isShow);
        if (TextUtils.isEmpty(time)) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());


            object.put("community_id", circle.groupId);
            if (!TextUtils.isEmpty(circle.name)) {
                object.put("community_name", circle.name);
            }

            if (circle.tenantId != null) {
                object.put("user_institution_id", circle.tenantId);
            }

            if (!TextUtils.isEmpty(circle.tenantName)) {
                object.put("user_institution", circle.tenantName);
            }

            object.put("exposure_time", time);
            track("CommunityExposure", object);
            LogUtils.printSALog("圈子曝光：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取时间
     */
    private String getTime(String key, boolean isShow) {
        if (isShow) {
            //缓存显示时间
            pageBrowsingTime.put(key, System.currentTimeMillis());
            return null;
        }
        Long cache = pageBrowsingTime.get(key);
        if (cache == null) {
            return null;
        }
        pageBrowsingTime.remove(key);
        float t = System.currentTimeMillis() - cache;
        if (t < 1000) {
            return null;
        }
        DecimalFormat format = new DecimalFormat("#0.0");
        return format.format((long) (t / 1000));
    }

    /**
     * 视频播放失败
     *
     * @param videoModel   内容
     * @param progressTime 进度
     * @param errorMessage 错误
     */
    public void videoPlayError(VideoModel videoModel, long progressTime, String errorMessage) {
        videoPlay(videoModel, progressTime, false, errorMessage);
    }

    /**
     * 视频播放
     *
     * @param videoModel   内容
     * @param progressTime 进度
     */
    public void videoPlay(VideoModel videoModel, long progressTime) {
        videoPlay(videoModel, progressTime, true, null);
    }

    /**
     * 视频播放
     *
     * @param videoModel   内容
     * @param progress     播放百分比
     * @param isSuccess    是否播放成功
     * @param errorMessage 错误信息
     */
    private void videoPlay(VideoModel videoModel, long progress, boolean isSuccess, String errorMessage) {
        if (videoModel == null) {
            return;
        }
        String key = getClass().getName() + videoModel.getId() + "videoPlay";
        if (progress <= 0 && TextUtils.isEmpty(errorMessage)) {
            //没有进度时间说明是播放事件
            pageBrowsingTime.put(key, System.currentTimeMillis());
        } else {
            if (!pageBrowsingTime.containsKey(key)) {
                return;
            }
            Long time = pageBrowsingTime.get(key);
            //有错误信息时上报播放状态
            if (time != null || (!isSuccess && TextUtils.isEmpty(errorMessage))) {
                pageBrowsingTime.remove(key);
                try {

                    JSONObject object = new JSONObject();
                    object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
                    object.put("content_id", videoModel.getId());
                    object.put("content_name", videoModel.getTitle());
                    object.put("content_type", videoModel.getMediaType());
                    object.put("content_tag", videoModel.getLabels());
                    object.put("post_type", videoModel.isNotNormalType() ? 2 : 1);

                    GroupInfo groupInfo = videoModel.getGroupInfo();
                    if (groupInfo != null) {
                        object.put("community_id", groupInfo.getGroupId());
                        object.put("community_name", groupInfo.getGroupName());
                        object.put("user_institution_id", groupInfo.tenantId);
                        object.put("user_institution", groupInfo.tenantName);
                    }

                    if (null != videoModel.getCreateDate()) {
                        object.put("publish_time", videoModel.getCreateDate().getTime());
                    }

                    AuthorModel authorModel = videoModel.getAuthor();
                    if (authorModel != null) {
                        object.put("author_name", authorModel.getName());
                    }

                    if (videoModel.totalPlayDuration != null) {
                        object.put("video_seconds", videoModel.totalPlayDuration);
                        Log.d("TAG", "videoModel.totalPlayDuration -->>" + videoModel.totalPlayDuration);
                    }

                    object.put("is_success", isSuccess);
                    object.put("fail_reason", errorMessage);
                    object.put("play_schedule", progress);
                    object.put("Start_play_time", time);
                    object.put("source_page", getSourcePageFromVideoModel(videoModel));
                    Log.d("GVideoSensorDataManager", "VideoPlay  ============  Source page -->>" + object.get("source_page"));
                    object.put("Exit_play_time", System.currentTimeMillis());
                    track("VideoPlay", object);
                    LogUtils.printSALog("视频播放：" + new Gson().toJson(object));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getSourcePageFromVideoModel(VideoModel videoModel) {
        if (videoModel == null) {
            return "";
        }

        // 为首页视频做单独处理
        if (!TextUtils.isEmpty(videoModel.tabName)) {
            if (videoModel.tabName.equals("视频")) {
                return "首页-视频";
            }
            return videoModel.tabName;
        }

        String nameFromPid = StatPid.getPageName(videoModel.getPid());
        if (!TextUtils.isEmpty(nameFromPid)) {
            return nameFromPid;
        }
        return "";
    }

    private String getVideoModelPidThanOther(VideoModel videoModel, String pid) {
        return (videoModel == null || TextUtils.isEmpty(videoModel.getPid()))
                ? pid : videoModel.getPid();
    }

    /**
     * 获取验证码
     */
    public void clickGetSMS(boolean isSuccess) {
        try {
            JSONObject object = new JSONObject();
            object.put("receivecode_time", System.currentTimeMillis());
            object.put("is_success", isSuccess);
            track("ReceiveCode", object);
            LogUtils.printSALog("获取验证码：" + isSuccess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击热门新闻
     */
    public void clickHotNews() {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("enterpage_time", System.currentTimeMillis());
            track("EnterHotspotPage", object);
            LogUtils.printSALog("点击热门新闻");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击搜索框
     */
    public void clickSearchEditText() {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("Search_content", System.currentTimeMillis());
            track("searchClick", object);
            LogUtils.printSALog("点击搜索框");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送搜索请求
     *
     * @param hasResult          是否有结果
     * @param keyword            搜索的关键词
     * @param searchResultNumber 搜索结果数量
     * @param hintKeyword        搜索的预制关键词
     *                           <p>记录是自定义关键词还是预制关键词：1、自定义文案、2、预制文案</p>
     */
    public void sendSearchRequest(boolean hasResult, String keyword, int searchResultNumber, String hintKeyword) {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("has_result", hasResult);
            object.put("keyword", keyword);
            object.put("keyword_type", getKeyWordType(keyword, hintKeyword));
            object.put("search_result_num", searchResultNumber);
            track("search", object);
            LogUtils.printSALog("发送搜索请求");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getKeyWordType(String keyword, String hintKeyword) {
        if (TextUtils.isEmpty(keyword) || TextUtils.isEmpty(hintKeyword)) {
            return "自定义文案";
        }
        return keyword.equalsIgnoreCase(hintKeyword) ? "预制文案" : "自定义文案";
    }

    /**
     * 点击搜索结果
     *
     * @param contentId      内容Id
     * @param contentName    内容名称
     * @param keyword        关键词
     * @param positionNumber 位置序号
     * @param hintKeyword    搜索的预制关键词
     *                       <p>记录是自定义关键词还是预制关键词：1、自定义文案、2、预制文案</p>
     */
    public void clickSearchResult(
            String contentId,
            String contentName,
            String contentType,
            String keyword,
            int positionNumber,
            String hintKeyword
    ) {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("content_id", contentId);
            object.put("content_name", contentName);
            object.put("click_target_type", contentType);
            object.put("keyword", keyword);
            object.put("keyword_type", getKeyWordType(keyword, hintKeyword));
            object.put("position_number", positionNumber);
            track("searchResult", object);
            LogUtils.printSALog("点击搜索结果");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送c2c消息
     *
     * @param receiveUserId   接收消息用户id
     * @param receiveUserName 接收消息用户昵称
     * @param messageType     消息类型
     * @param failReason      失败原因
     */
    public void sendC2CMessage(String receiveUserId, String receiveUserName, String messageType,
                               String relationShip, String failReason) {
        String sendUserId = PluginManager.get(AccountPlugin.class).getUserId();
        String sendUserName = PluginManager.get(AccountPlugin.class).getNickName();
        try {
            JSONObject object = new JSONObject();
            object.put("send_ID", sendUserId);
            object.put("send_name", sendUserName);
            object.put("messageget_id", receiveUserId);
            object.put("messageget_name", receiveUserName);
            object.put("fail_reason", failReason);
            object.put("is_success", TextUtils.isEmpty(failReason));
            object.put("message_type", messageType);
            object.put("relationship", relationShip);
            object.put("send_message_position", System.currentTimeMillis());
            track("SendMessage", object);
            LogUtils.printSALog("发送c2c消息" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开官方系统消息
     *
     * @param content   消息内容
     * @param messageId 消息id
     * @param msgType   消息类型
     */
    public void openSystemMessage(String content, String messageId, String msgType) {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("system_message_content", content);
            object.put("system_message_id", messageId);
            object.put("system_message_type", msgType);
            object.put("open_time", System.currentTimeMillis());
            track("SystemMessageDelivery", object);
            LogUtils.printSALog("打开官方站内信：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击看电视直播
     *
     * @param anchorId    直播ID
     * @param anchorName  直播标题
     * @param enterSource 频道名称
     */
    public void clickWatchTvLive(
            String anchorId,
            String anchorName,
            String enterSource

    ) {
        anchorId = TextUtils.isEmpty(anchorId) ? "" : anchorId;
        anchorName = TextUtils.isEmpty(anchorName) ? "" : anchorName;
        enterSource = TextUtils.isEmpty(enterSource) ? "" : enterSource;
        Log.i("clickWatchTvLive", "anchorName -->> " + anchorName);
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("anchor_ID", anchorId);
            object.put("anchor_name", anchorName);
            object.put("publish_time", System.currentTimeMillis());
            object.put("enter_source", enterSource);
            track("ClickLive", object);
            LogUtils.d("点击看电视直播");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击看电视直播
     *
     * @param anchorId   直播ID
     * @param anchorName 直播标题
     * @param pgc        认证号名称
     */
    public void clickAtyTvLive(
            String anchorId,
            String anchorName,
            String pgc,
            List<String> tenantNameList
    ) {
        anchorId = TextUtils.isEmpty(anchorId) ? "" : anchorId;
        anchorName = TextUtils.isEmpty(anchorName) ? "" : anchorName;
        pgc = TextUtils.isEmpty(pgc) ? "" : pgc;
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("anchor_ID", anchorId);
            object.put("anchor_name", anchorName);
            object.put("publish_time", System.currentTimeMillis());
            object.put("PGC", pgc);
            object.put("channel", tenantNameList);
            track("ClickAtyLive", object);
            LogUtils.d("点击看电视直播");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出节目视频播放
     *
     * @param isFinish     是否已完成
     * @param playDuration 观看时长
     * @param watchSource  开始观看时间
     * @param scene        观看场景
     */
    public void programPlay(
            VideoModel videoModel,
            boolean isFinish,
            long playDuration,
            String watchSource,
            String scene,
            long videoTotalTime
    ) {
        if (videoModel == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();

            GroupInfo groupInfo = videoModel.getGroupInfo();
            if (groupInfo != null) {
                object.put("channel_id", groupInfo.tenantId);
                object.put("channel_name", groupInfo.tenantName);
            }

            object.put("content_id", videoModel.getId());
            object.put("content_name", videoModel.getTitle());
            object.put("content_tag", videoModel.getLabels());
            object.put("is_finish", isFinish);
            object.put("play_duration", playDuration);

            Date date = videoModel.getCreateDate();
            if (date != null) {
                object.put("publish_time", date.getTime());
            }

            object.put("video_time", videoTotalTime);
            object.put("watch_source", watchSource);
            object.put("scene", scene);

            track("Ove_programPlay", object);
            LogUtils.printSALog("退出节目：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 开始节目视频播放
     *
     * @param watchSource      开始观看时间
     * @param pid              pid
     * @param source_scenePage 场景来源页面
     * @param isContinuePlay   是否续播
     */
    public void startProgramPlay(VideoModel videoModel, String watchSource, String pid, String source_scenePage,
                                 boolean isContinuePlay, long videoDuration) {
        if (videoModel == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();

            GroupInfo groupInfo = videoModel.getGroupInfo();
            if (groupInfo != null) {
                object.put("channel_id", groupInfo.tenantId);
                object.put("channel_name", groupInfo.tenantName);
            }
            object.put("content_id", videoModel.getId());
            object.put("content_name", videoModel.getTitle());
            object.put("content_tag", videoModel.getLabels());
            Date date = videoModel.getCreateDate();
            if (date != null) {
                object.put("publish_time", date.getTime());
            }
            object.put("video_time", videoDuration);
            object.put("watch_source", watchSource);
            object.put("scene", StatPid.getPageName(pid));
            object.put("source_scenePage", StatPid.getPageName(source_scenePage));
            object.put("play_scene", isContinuePlay ? "继续播放" : "开始播放");
            track("Start_programPlay", object);
            LogUtils.printSALog("开始播放节目视频：" + new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 点击提问
     */
    public void clickAskQuestion() {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("publish_time", System.currentTimeMillis());
            track("clikask_q", object);
            LogUtils.d("点击提问");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击回答按钮
     */
    public void clickCommentButton(VideoModel videoModel) {
        if (videoModel == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("publish_time", System.currentTimeMillis());
            object.put("content_source", StatPid.getPageName(getVideoModelPidThanOther(videoModel, "")));
            Log.d("GVideoSensorDataManager", "cli_combutton  ============  Content source -->>" + object.get("content_source"));

            track("cli_combutton", object);
            LogUtils.d("点击回答按钮");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内容播放异常原因
     *
     * @param videoModel 视频数据
     * @param failReason 失败原因
     */
    public void videoPlayFail(
            VideoModel videoModel,
            String failReason
    ) {
        if (videoModel == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("content_id", videoModel.getId());
            object.put("content_name", videoModel.getTitle());
            object.put("content_type", videoModel.getMediaType());
            object.put("content_tag", videoModel.getLabels());

            Date date = videoModel.getCreateDate();
            if (date != null) {
                object.put("publish_time", date.getTime());
            }

            AuthorModel authorModel = videoModel.getAuthor();
            if (authorModel != null) {
                object.put("author_name", authorModel.getName());
            }

            object.put("fail_reason", failReason);

            track("VideoPlay_fail", object);
            LogUtils.printSALog(new Gson().toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void activityLiveSendComment(
            String anchorId,
            String anchorName,
            String PGC,
            List<String> channel,
            boolean isFollowAnchor
    ) {
        try {
            JSONObject object = new JSONObject();
            object.put("anchor_ID", anchorId);
            object.put("anchor_name", anchorName);
            object.put("PGC", PGC);
            object.put("channel", channel);
            object.put("is_follow_anchor", isFollowAnchor);
            track("CommentSend", object);
            LogUtils.d("发送评论");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void activityLiveLike(
            String anchorId,
            String anchorName,
            String PGC
    ) {
        try {
            JSONObject object = new JSONObject();
            object.put("anchor_ID", anchorId);
            object.put("anchor_name", anchorName);
            object.put("PGC", PGC);
            track("like", object);
            LogUtils.d("点赞");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击Banner
     *
     * @param imagesBean    Banner的数据
     * @param index         Banner在列表中的Index
     * @param dataSourcePid Banner所属页面Pid
     * @param bannerType    Banner类型
     */
    public void bannerClick(
            BannerModel.ImagesBean imagesBean,
            int index,
            String dataSourcePid,
            String bannerType
    ) {
        if (imagesBean == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("banner_id", imagesBean.getBannerId());
            object.put("banner_name", imagesBean.getBannerName());
            object.put("banner_location", index);
            object.put("banner_page", StatPid.getPageName(dataSourcePid));
            object.put("banner_type", bannerType);
            object.put("jump_type", imagesBean.getJumpMediaType());
            object.put("jump_adress", imagesBean.getJumpAddress());
            track("banner_Click", object);
            LogUtils.d("点赞");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹窗展示埋点
     */
    public void dialogShow(Circle circle) {
        try {
            JSONObject object = new JSONObject();
            object.put("communityName", circle == null ? "" : circle.getName());
            object.put("communityId", circle == null ? "" : circle.getGroupId());
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            track("dialog_show", object);
            LogUtils.d("弹窗展示");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹窗点击埋点
     */
    public void dialogClick(Circle circle) {
        try {
            JSONObject object = new JSONObject();
            object.put("communityName", circle == null ? "" : circle.getName());
            object.put("communityId", circle == null ? "" : circle.getGroupId());
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            track("dialog_click", object);
            LogUtils.d("弹窗点击");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹窗关闭埋点
     */
    public void dialogClose(Circle circle) {
        try {
            JSONObject object = new JSONObject();
            object.put("communityName", circle == null ? "" : circle.getName());
            object.put("communityId", circle == null ? "" : circle.getGroupId());
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            track("dialog_close", object);
            LogUtils.d("弹窗展示");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入注册-登录页面
     *
     * @param sourceFragmentPid 启动登录界面的页面Pid
     * @param element           启动时用户的操作，例如关注、发布、喜欢、评论、分享、用户反馈、直播喜欢
     */
    public void enterRegister(String sourceFragmentPid, String element) {
        try {
            JSONObject object = new JSONObject();
            object.put("register_adname", sourceFragmentPid);
            object.put("element", element);
            track("enter_register", object);
            LogUtils.d("点赞");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clickCommunity(VideoModel videoModel) {
        try {
            JSONObject object = new JSONObject();
            object.put("account", PluginManager.get(AccountPlugin.class).getUserId());
            object.put("content_source", StatPid.getPageName(videoModel.getPid()));
            object.put("publish_time", System.currentTimeMillis());
            object.put("community_name", videoModel.getGroupName());

            GroupInfo groupInfo = videoModel.getGroupInfo();
            if (groupInfo != null) {

                if (groupInfo.tenantId != null) {
                    object.put("user_institution_id", groupInfo.tenantId);
                }

                if (!TextUtils.isEmpty(groupInfo.tenantName)) {
                    object.put("user_institution", groupInfo.tenantName);
                }
                object.put("community_id", groupInfo.getGroupId());
            }
            track("cli_coumunity", object);
            LogUtils.d("点击首页推荐的社区按钮");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void screencastResult(
            VideoModel videoModel,
            int contentType,
            String liveType,
            String deviceName,
            boolean isSuccess
    ) {
        try {
            JSONObject object = new JSONObject();
            dealCommonVideoModelData(object, videoModel, "");
            object.put("content_type", contentType);
            object.put("live_type", liveType);
            object.put("content_source", StatPid.getPageName(videoModel.getPid()));
            object.put("Linksuccessful_device", TextUtils.isEmpty(deviceName) ? "" : deviceName);
            object.put("is_success", isSuccess);
            track("Screencast_result", object);
            LogUtils.d("链接结果返回");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void enterDeviceSelectPage(
            VideoModel videoModel,
            int contentType,
            String liveType
    ) {
        try {
            JSONObject object = new JSONObject();
            dealCommonVideoModelData(object, videoModel, "");
            object.put("content_type", contentType);
            object.put("live_type", liveType);
            track("Enter_deselectpage", object);
            LogUtils.d("进入设备选择页时上报");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void enterContainsPage(
            VideoModel videoModel,
            int contentType,
            String liveType
    ) {
        // try {
        //     JSONObject object = new JSONObject();
        //     dealCommonVideoModelData(object, videoModel, "");
        //     object.put("content_type", contentType);
        //     object.put("live_type", liveType);
        //     track("Enter_contailspage", object);
        //     LogUtils.d("进入设备选择页时上报");
        // } catch (JSONException e) {
        //     e.printStackTrace();
        // }
    }

    public void endScreencast(
            VideoModel videoModel,
            VideoModel changeVideoModel,
            int contentType,
            String liveType,
            String playSchedule,
            long playTime,
            String pid,
            String endReason
    ) {
        // try {
        //     JSONObject object = new JSONObject();
        //     dealCommonVideoModelData(object, videoModel, "");
        //     dealCommonVideoModelData(object, changeVideoModel, "change");
        //     object.put("content_type", contentType);
        //     object.put("live_type", liveType);
        //     object.put("play_schedule", playSchedule);
        //     object.put("play_time", playTime);
        //     object.put("content_source", StatPid.getPageName(pid));
        //     object.put("end_reason", endReason);
        //     track("end_Screencast", object);
        //     LogUtils.d("结束投屏");
        // } catch (JSONException e) {
        //     e.printStackTrace();
        // }
    }

    public void exitDeviceSelectPage(
            long stayTime,
            List<String> deviceNameList,
            String selectDevice,
            boolean isSuccess,
            String failReason
    ) {
        // try {
        //     JSONObject object = new JSONObject();
        //     object.put("stay_time", stayTime);
        //     object.put("Searchable_devices", deviceNameList);
        //     object.put("Linksuccessful_device", selectDevice);
        //     object.put("is_success", isSuccess);
        //     object.put("fail_reason", failReason);
        //     track("exit_Screencast", object);
        //     LogUtils.d("结束投屏");
        // } catch (JSONException e) {
        //     e.printStackTrace();
        // }
    }

    public void dealCommonVideoModelData(JSONObject object, VideoModel videoModel, String prefix) throws JSONException {
        if (videoModel == null) {
            return;
        }
        object.put(prefix + "content_id", videoModel.getId());
        object.put(prefix + "content_name", videoModel.getTitle());
        object.put(prefix + "content_tag", videoModel.getLabels());
        GroupInfo groupInfo = videoModel.getGroupInfo();
        if (groupInfo != null) {
            object.put(prefix + "community_id", groupInfo.getGroupId());
            object.put(prefix + "community_name", groupInfo.getGroupName());
            if (groupInfo.tenantId != null) {
                object.put(prefix + "user_institution_id", groupInfo.tenantId);
            }
            if (!TextUtils.isEmpty(groupInfo.tenantName)) {
                object.put(prefix + "user_institution", groupInfo.tenantName);
            }
        }
        AuthorModel authorModel = videoModel.getAuthor();
        if (authorModel != null) {
            object.put(prefix + "author_name", authorModel.getName());
            if (authorModel.tenantIdList != null
                    && !authorModel.tenantIdList.isEmpty()) {
                object.put(prefix + "author_institution_id", authorModel.tenantIdList);
            }
            if (authorModel.tenantNameList != null
                    && !authorModel.tenantNameList.isEmpty()) {
                object.put(prefix + "author_institution", authorModel.tenantNameList);
            }
        }
        Date createDate = videoModel.getCreateDate();
        if (createDate != null) {
            object.put(prefix + "publish_time", createDate.getTime());
        }
    }

    /**
     * 上报数据
     */
    private void track(String eventName, JSONObject object) {
        if (isCanUploadMessage()) {
            printFromJsonObject(object, "埋点_" + eventName);
            SensorsDataAPI.sharedInstance().track(eventName, object);
        }
    }

    private boolean isCanUploadMessage() {
//        return !BuildConfig.DEBUG;
//        && !EnvironmentManager.getInstance().getIsTestProductFlavors();
        return true;
    }

    /**
     * 获取埋点数据上报地址
     */
    private String getServerUrl() {
        if (BuildConfig.DEBUG || EnvironmentManager.getInstance().getIsTestProductFlavors()) {
            //测试环境埋点
            return "https://sensorsin.jxgdw.com/sa?project=default";
        } else {
            //正式环境埋点
            return "https://sensorsin.jxgdw.com/sa?project=production";
        }
    }

    public static void printFromJsonObject(JSONObject jsonObject, String extra) {
        if (!EnvironmentManager.getInstance().getIsTestProductFlavors()) {
            return;
        }
        StringBuilder result;
        try {
            result = new StringBuilder(" "
                    + "\n"
                    + "================================"
                    + (TextUtils.isEmpty(extra) ? "  未知  " : ("  " + extra + "  "))
                    + "==============================="
                    + "\n"
            );
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                result.append(key)
                        .append(" -->> ")
                        .append(jsonObject.get(key))
                        .append("\n");
            }
            result.append("=================================================================================");
            Log.d("printFromJsonObject", result.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}


