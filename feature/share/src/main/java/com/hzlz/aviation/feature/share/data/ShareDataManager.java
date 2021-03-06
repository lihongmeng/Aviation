package com.hzlz.aviation.feature.share.data;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.share.ShareRuntime;
import com.hzlz.aviation.feature.share.adapter.ShareSlideAdapter;
import com.hzlz.aviation.feature.share.dialog.CreateBillDialog;
import com.hzlz.aviation.feature.share.model.ShareItemModel;
import com.hzlz.aviation.feature.share.strategy.CopyShareStrategy;
import com.hzlz.aviation.feature.share.strategy.DeleteShareStrategy;
import com.hzlz.aviation.feature.share.strategy.FavoriteShareStrategy;
import com.hzlz.aviation.feature.share.strategy.FollowShareStrategy;
import com.hzlz.aviation.feature.share.strategy.FontSettingStrategy;
import com.hzlz.aviation.feature.share.strategy.QQShareStrategy;
import com.hzlz.aviation.feature.share.strategy.ReportShareStrategy;
import com.hzlz.aviation.feature.share.strategy.WeChatShareStrategy;
import com.hzlz.aviation.feature.share.strategy.WeiBoShareStrategy;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.sensordata.utils.InteractType;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.share.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ????????????Manager
 */
public class ShareDataManager {

    private static boolean isCanShareQQ, isCanShareWeiXin, isCanShareWeibo;

    public static void setCanShareConfig(
            boolean shareWeiXin,
            boolean shareQQ,
            boolean shareWeibo
    ) {
        isCanShareWeiXin = shareWeiXin;
        isCanShareQQ = shareQQ;
        isCanShareWeibo = shareWeibo;
    }

    public static boolean isCanShare() {
        return isCanShareQQ || isCanShareWeiXin || isCanShareWeibo;
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @return ????????????
     */
    @NonNull
    public static List<ShareItemModel> getDefaultShareModel(
            Context context,
            ShareDataModel model,
            StatFromModel stat
    ) {
        List<ShareItemModel> list = new ArrayList<>();
        if (isCanShareWeiXin) {
            list.add(makeWeixinFriendItem(context, model, stat));
            list.add(makeWeixinFriendCircleItem(context, model, stat));
        }
        if (isCanShareQQ) {
            list.add(makeQQFriendCircleItem(context, model, stat));
            list.add(makeQQSpaceCircleItem(context, model, stat));
        }
        if (isCanShareWeibo) {
            list.add(makeWeiBoItem(context, model, stat));
        }
        if (isCanShareQQ || isCanShareWeiXin || isCanShareWeibo) {
            list.add(makeCopyLinkItem(context, model, stat));
        }
        return list;
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @return ??????????????????
     */
    @NonNull
    public static List<ShareItemModel> getDefaultOtherModel(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        List<ShareItemModel> list = new ArrayList<>();
        if (dataModel.isShowFavorite()) {
            list.add(makeShareOtherFavoriteItem(context, dataModel, stat));
        }
        if (dataModel.isShowFollow()) {
            list.add(makeShareOtherFollowItem(context, dataModel, stat));
        }
        if (dataModel.isShowReport()) {
            list.add(makeShareOtherReportItem(context, dataModel, stat));
        }

        if (dataModel.isShowFontSetting()) {
            list.add(makeShareOtherFontSettingItem(context, dataModel, stat));
        }
        if (dataModel.isShowDelete()) {
            list.add(makeShareOtherDeleteItem(context, dataModel, stat));
        }
        // if (dataModel.isShowCreateBill()) {
        //     list.add(makeShareOtherCreateBillItem(context, dataModel, stat));
        // }
        return list;
    }

    /**
     * ???????????????????????????
     *
     * @return ?????????????????????
     */
    private static ShareItemModel makeWeixinFriendCircleItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_weixin_friends_circle);
        model.drawableRes = R.drawable.share_item_weixin_friends_circle;
        model.shareStrategy = new WeChatShareStrategy(context, WXSceneTimeline);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxFriend(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    private static ShareItemModel makeWeixinFriendItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_weixin_friend);
        model.drawableRes = R.drawable.share_item_weixin_friends;
        model.shareStrategy = new WeChatShareStrategy(context, WXSceneSession);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    public static void startWXShare(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        new WeChatShareStrategy(context, WXSceneSession).share(dataModel);
        statWxMoment(
                dataModel,
                stat,
                ResourcesUtils.getString(R.string.share_weixin_friend)
        );
    }

    public static void startWXCircleShare(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        new WeChatShareStrategy(context, WXSceneTimeline).share(dataModel);
        statWxFriend(
                dataModel,
                stat,
                ResourcesUtils.getString(R.string.share_weixin_friends_circle)
        );
    }


    /**
     * ??????????????????
     *
     * @return ????????????
     */
    private static ShareItemModel makeWeiBoItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_weibo);
        model.drawableRes = R.drawable.share_item_weibo;
        model.shareStrategy = new WeiBoShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * ??????QQ????????????
     *
     * @return QQ????????????
     */
    private static ShareItemModel makeQQFriendCircleItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_qq_friend);
        model.drawableRes = R.drawable.share_item_qq_friend;
        model.shareStrategy = new QQShareStrategy(context, 0);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * ??????QQ????????????
     *
     * @return QQ????????????
     */
    private static ShareItemModel makeQQSpaceCircleItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_qq_space);
        model.drawableRes = R.drawable.share_item_qq_space;
        model.shareStrategy = new QQShareStrategy(context, 1);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statWxMoment(dataModel, stat, model.title);
            }
        };
        return model;
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    private static ShareItemModel makeCopyLinkItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.shareDataModel = dataModel;
        model.title =
                ShareRuntime.getAppContext().getResources().getString(R.string.share_copy_link);
        model.drawableRes = R.drawable.share_item_copy_link;
        model.shareStrategy = new CopyShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
                statCopy(dataModel, stat);
            }
        };
        return model;
    }

    /**
     * ????????????item??????
     *
     * @return ??????item??????
     */
    private static ShareItemModel makeShareOtherFavoriteItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = dataModel.isFavorite() ?
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_favorited) :
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_collect);
        model.drawableRes = dataModel.isFavorite() ?
                R.drawable.share_other_favorited :
                R.drawable.share_other_favorite;
        model.shareStrategy = new FavoriteShareStrategy(context);
        model.clickListener = view -> {
            Context viewContext = view.getContext();
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (TextUtils.isEmpty(accountPlugin.getToken())) {
                accountPlugin.startLoginActivity(viewContext);
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(dataModel.getPid()),
                        ResourcesUtils.getString(R.string.like)
                );
                return;
            }
            if (!NetworkTipUtils.checkNetworkOrTip(viewContext)) {
                return;
            }
            if (stat != null) {
                ((FavoriteShareStrategy) model.shareStrategy).share(dataModel, stat.pid);
            } else {
                model.shareStrategy.share(dataModel);
            }
            statFavorite(dataModel, stat);
        };
        return model;
    }

    /**
     * ??????????????????
     *
     * @return ????????????item??????
     */
    private static ShareItemModel makeShareOtherFollowItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = dataModel.isFollow() ?
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_followed) :
                ShareRuntime.getAppContext().getResources().getString(R.string.share_other_attention);
        model.drawableRes = dataModel.isFollow() ?
                R.drawable.share_other_followed :
                R.drawable.share_other_follow;
        model.shareStrategy = new FollowShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context viewContext = view.getContext();
                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                if (TextUtils.isEmpty(accountPlugin.getToken())) {
                    accountPlugin.startLoginActivity(viewContext);
                    GVideoSensorDataManager.getInstance().enterRegister(
                            StatPid.getPageName(dataModel.getPid()),
                            ResourcesUtils.getString(R.string.follow)
                    );
                    return;
                }
                if (!NetworkTipUtils.checkNetworkOrTip(viewContext)) {
                    return;
                }

                model.shareStrategy.share(dataModel);
            }
        };
        return model;
    }

    /**
     * ??????????????????
     *
     * @return ????????????item??????
     */
    private static ShareItemModel makeShareOtherDeleteItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_other_delete);
        model.drawableRes = R.drawable.share_other_delete;
        model.shareStrategy = new DeleteShareStrategy(context);
        model.clickListener = view -> {
            Context viewContext = view.getContext();
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (TextUtils.isEmpty(accountPlugin.getToken())) {
                accountPlugin.startLoginActivity(viewContext);
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(dataModel.getPid()),
                        ResourcesUtils.getString(R.string.delete)
                );
                return;
            }
            if (!NetworkTipUtils.checkNetworkOrTip(viewContext)) {
                return;
            }
            model.shareStrategy.share(dataModel);
        };
        return model;
    }

    /**
     * ??????????????????
     *
     * @return ??????????????????
     */
    private static ShareItemModel makeShareOtherCreateBillItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share);
        model.drawableRes = R.drawable.share_other_save_pic;
        model.clickListener = v -> {
            new CreateBillDialog(context).show();
        };
        return model;
    }

    /**
     * ??????????????????
     *
     * @return ????????????item??????
     */
    private static ShareItemModel makeShareOtherReportItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_other_report);
        model.drawableRes = R.drawable.share_other_report;
        model.shareStrategy = new ReportShareStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkTipUtils.checkNetworkOrTip(v.getContext())) {
                    return;
                }
                model.shareStrategy.share(dataModel);
            }
        };
        return model;
    }

    /**
     * ????????????????????????
     *
     * @return ????????????????????????item??????
     */
    private static ShareItemModel makeShareOtherFontSettingItem(
            Context context,
            ShareDataModel dataModel,
            StatFromModel stat
    ) {
        ShareItemModel model = new ShareItemModel();
        model.title = ShareRuntime.getAppContext().getResources().getString(R.string.share_other_font_setting);
        model.drawableRes = R.drawable.share_other_font;
        model.shareStrategy = new FontSettingStrategy(context);
        model.clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.shareStrategy.share(dataModel);
            }
        };
        return model;
    }

    private static void statWxFriend(
            ShareDataModel shareDataModel,
            StatFromModel stat,
            String title
    ) {
        if (stat == null) return;
        String method = "1";
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(createShareDs(shareDataModel, stat, method).toString())
                .withEv(StatConstants.EV_SHARE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
        if (shareDataModel.getVideoModel() != null) {
            GVideoSensorDataManager.getInstance().clickContent(shareDataModel.getVideoModel(), stat.pid,
                    InteractType.SHARE, title, null);
        }
    }

    private static void statWxMoment(
            ShareDataModel shareDataModel,
            StatFromModel stat,
            String title
    ) {
        if (stat == null) return;
        String method = "2";
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(createShareDs(shareDataModel, stat, method).toString())
                .withEv(StatConstants.EV_SHARE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
        VideoModel videoModel;
        if (shareDataModel.getVideoModel() == null) {
            videoModel = new VideoModel();
            videoModel.setId(shareDataModel.getMediaId());
            videoModel.setTitle(shareDataModel.getTitle());
        } else {
            videoModel = shareDataModel.getVideoModel();
        }
        GVideoSensorDataManager.getInstance().clickContent(videoModel, stat.pid,
                InteractType.SHARE, title, null);
    }

    private static void statCopy(ShareDataModel shareDataModel, StatFromModel stat) {
        if (stat == null) return;
        String method = "100";
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(createShareDs(shareDataModel, stat, method).toString())
                .withEv(StatConstants.EV_SHARE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    private static void statFavorite(ShareDataModel shareDataModel, StatFromModel stat) {
        if (stat == null) return;
        String fav = shareDataModel.isFavorite() ? "0" : "1";
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
        ds.addProperty(StatConstants.DS_KEY_FAVORITE, fav);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withDs(ds.toString())
                .withEv(StatConstants.EV_FAVORITE)
                .withPid(stat.pid)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    private static JsonObject createShareDs(
            ShareDataModel shareDataModel,
            StatFromModel stat,
            String method
    ) {
        String module = "";
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
        ds.addProperty(StatConstants.DS_KEY_METHOD, method);
        ds.addProperty(StatConstants.DS_KEY_MODULE, module);
        return ds;
    }

    public static View getShareView(
            Context context,
            ShareDataModel model,
            StatFromModel stat
    ) {
        if (!isCanShare()) {
            return null;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.layout_share_recyclerview, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.share_recycler);
        recyclerView.setFocusable(false);
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        ShareSlideAdapter adapter = new ShareSlideAdapter(context, false, false, null);
        recyclerView.setAdapter(adapter);

        List<ShareItemModel> list = ShareDataManager.getDefaultShareModel(context, model, stat);
        if (list != null && list.size() > 0) {
            list.remove(list.size() - 1);
            adapter.refreshData(list);
        }
        return view;
    }

}
