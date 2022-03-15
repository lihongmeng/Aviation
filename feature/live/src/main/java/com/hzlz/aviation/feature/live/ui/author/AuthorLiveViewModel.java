package com.hzlz.aviation.feature.live.ui.author;

import static com.hzlz.aviation.feature.live.liveroom.live.utils.TCConstants.IMCMD_CONNECT_ONLINE_ANCHOR_VOLUME;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_ROOM_TYPE.SOUND;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_ROOM_TYPE.VIDEO;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import com.hzlz.aviation.feature.live.Constants;
import com.hzlz.aviation.feature.live.LiveManager;
import com.hzlz.aviation.feature.live.adapter.AudienceAvatarAdapter;
import com.hzlz.aviation.feature.live.adapter.LiveChatAdapter;
import com.hzlz.aviation.feature.live.callback.OnCommentAddListener;
import com.hzlz.aviation.feature.live.dialog.CommentInputBlackDialog;
import com.hzlz.aviation.feature.live.dialog.CommentInputWhiteDialog;
import com.hzlz.aviation.feature.live.liveroom.IMLVBLiveRoomListener;
import com.hzlz.aviation.feature.live.liveroom.MLVBLiveRoom;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCChatEntity;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCSimpleUserInfo;
import com.hzlz.aviation.feature.live.liveroom.live.net.TCHTTPMgr;
import com.hzlz.aviation.feature.live.liveroom.live.utils.TCConstants;
import com.hzlz.aviation.feature.live.liveroom.live.utils.TCUtils;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AudienceInfo;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.MLVBCommonDef;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.OnlineAnchorVolume;
import com.hzlz.aviation.feature.live.model.LiveDetailModel;
import com.hzlz.aviation.feature.live.repository.LiveRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.feature.live.R;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author huangwei
 * date : 2021/3/3
 * desc :
 **/
public class AuthorLiveViewModel extends BaseViewModel {

    // LiveRepository
    public final LiveRepository liveRepository = new LiveRepository();

    // 计算开播时间
    private Disposable timeObservable;

    // 点赞数量统计
    public int mHeartCount;

    // 进入房间人次统计
    public int mEnterAudienceCount = 0;

    // 当前直播间人数
    public final ObservableField<String> mCurrentAudienceString = new ObservableField<>();

    // 错误信息
    public final CheckThreadLiveData<String> showErrorMessageDialog = new CheckThreadLiveData<>();

    // 退出登录弹窗
    public final CheckThreadLiveData<String> showExitDialog = new CheckThreadLiveData<>();

    // 聊天信息
    public final CheckThreadLiveData<Integer> updateChatRecyclerView = new CheckThreadLiveData<>();

    // 当前是否是连麦状态
    public final CheckThreadLiveData<Boolean> isMicroConnect = new CheckThreadLiveData<>();

    // 开启连麦之前的提示弹窗
    public final CheckThreadLiveData<Object> showStartMicroConnectTipDialog = new CheckThreadLiveData<>();

    // 连麦申请、邀请弹窗
    public final CheckThreadLiveData<Integer> showMicroConnectApplyInviteDialog = new CheckThreadLiveData<>();

    // 音量信息发生变化
    public final CheckThreadLiveData<OnlineAnchorVolume> updateVolume = new CheckThreadLiveData<>();

    // 需要打开远程音视频数据传输
    public final CheckThreadLiveData<AnchorInfo> startRemoteView = new CheckThreadLiveData<>();

    // 腾讯云移动直播 - 连麦直播间
    public MLVBLiveRoom mLvbLiveRoom;

    // 当前用户的UserID
    public String userID;

    // 封面图片地址
    private String mCoverPicUrl;

    // 评论列表适配器
    private LiveChatAdapter mLiveChatAdapter;

    // 观众列表适配器
    public AudienceAvatarAdapter mAvatarAdapter;

    // 直播总时长
    private long mLiveTimeTotal;

    // 媒体Id
    public String mediaId = "";

    // 分享地址
    public String shareUrl = "";

    // 标题
    public String title = "";

    // 当前观众数量
    private int mCurrentAudienceNum = 0;

    // 可以重试的最大数量，每次推流并创建直播间失败
    // 此值减一，当等于0时，退出Activity
    // 成功时，此值置为0
    private int retryCount = 2;

    // 直播类型
    public int liveType = VIDEO;

    // 直播间详情信息
    public final CheckThreadLiveData<LiveDetailModel> liveDetailLiveData = new CheckThreadLiveData<>();

    // 当前连麦申请数据
    public final List<AnchorInfo> currentApplyAnchorInfoList = new ArrayList<>();
    //无效连麦
    public final CheckThreadLiveData<String> invalidMiroConnectLiveData = new CheckThreadLiveData<>();

    public final CheckThreadLiveData<AnchorInfo> addMiroConnectLiveData = new CheckThreadLiveData<>();

    public final CheckThreadLiveData<Boolean> clearMicroConnectDataLiveData = new CheckThreadLiveData<>();

    // 当前存在的连麦申请的Id数据，用于查重，显示数量applyAnchorCount
    public final ObservableBoolean applyAnchorCountVisibility = new ObservableBoolean(false);
    public final ObservableField<String> applyAnchorCountString = new ObservableField<>();

    // 所有的邀请列表，决定了列表共有多少条
    public final List<String> applyAnchorList = new ArrayList<>();
    // 当前还未接受邀请的列表，决定了按钮上显示的申请数字
    public final List<String> applyAnchorUnInviteList = new ArrayList<>();

    // 需要非常频繁转换数据，故保存一个变量
    public final Gson gson = new Gson();

    public AuthorLiveViewModel(@NonNull Application application) {
        super(application);
    }


    public void setData(Context context) {
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLvbLiveRoom = MLVBLiveRoom.sharedInstance(context);
        mLvbLiveRoom.setListener(imlvbLiveRoomListener);
        mCurrentAudienceString.set(0 + "人");
    }

    public void startRemoteView(AnchorInfo anchorInfo, TXCloudVideoView view) {
        mLvbLiveRoom.startRemoteView(anchorInfo, view, new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onError(int errCode, String errInfo) {
                onDoAnchorExit(anchorInfo);
            }

            @Override
            public void onEvent(int event, Bundle param) {

            }
        });
    }

    private void onDoAnchorExit(AnchorInfo anchorInfo) {
        ArrayList<AnchorInfo> anchorInfoList = getCurrentAnchorInfoList();
        Iterator<AnchorInfo> it = anchorInfoList.iterator();
        while (it.hasNext()) {
            AnchorInfo item = it.next();
            if (TextUtils.equals(item.userid, anchorInfo.userid)) {
                item.init();
                break;
            }
        }
        setCurrentAnchorInfoList(anchorInfoList);
        mLvbLiveRoom.stopRemoteView(anchorInfo);
    }

    private final IMLVBLiveRoomListener imlvbLiveRoomListener = new IMLVBLiveRoomListener() {
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            switch (errCode) {
                case MLVBCommonDef.LiveRoomErrorCode.ERROR_IM_FORCE_OFFLINE:
                    // IM被强制下线
                    onPause();
                    TCUtils.showKickOut(true);
                    break;
                case TXLiveConstants.PUSH_WARNING_RECONNECT:
                    //网络断连，且连续三次无法重新连接，需要自行重启推流
                    showErrorMessageDialog.setValue(GVideoRuntime.getAppContext().getResources().getString(R.string.all_network_not_available_action_tip));
                    break;
                default:
                    showErrorMessageDialog.setValue(errMsg);
            }
        }

        @Override
        public void onWarning(int warningCode, String warningMsg, Bundle extraInfo) {

        }

        @Override
        public void onDebugLog(String log) {
            LogUtils.d(log);
        }

        @Override
        public void onRoomDestroy(String roomID) {

        }

        @Override
        public void onAnchorEnter(AnchorInfo anchorInfo) {
            LogUtils.d("onAnchorEnter -->> " + anchorInfo.userName);
            dealAnchorEnterReal(anchorInfo);
            // 先开启远程视频传输。在成功中的回调 dealAnchorEnterReal(AnchorInfo anchorInfo) 中处理数据源
            startRemoteView.setValue(anchorInfo);
        }

        @Override
        public void onAnchorExit(AnchorInfo anchorInfo) {
            LogUtils.d("onAnchorExit -->> " + anchorInfo.userName);
            onDoAnchorExit(anchorInfo);
        }

        @Override
        public void onAudienceEnter(AudienceInfo audienceInfo) {
            LogUtils.d("onAudienceEnter -->> " + audienceInfo.userName);
        }

        @Override
        public void onAudienceExit(AudienceInfo audienceInfo) {
            LogUtils.d("onAudienceEnter -->> " + audienceInfo.userName);

            if (TextUtils.isEmpty(audienceInfo.userID)) {
                return;
            }
            if (!applyAnchorList.contains(audienceInfo.userID)) {
                return;
            }
            // 更新列表数据
            for (AnchorInfo temp : currentApplyAnchorInfoList) {
                if (temp == null
                        || TextUtils.isEmpty(temp.userid)
                        || !temp.userid.equals(audienceInfo.userID)) {
                    continue;
                }
                applyAnchorUnInviteList.remove(audienceInfo.userID);
                currentApplyAnchorInfoList.remove(temp);
                break;
            }
            invalidMiroConnectLiveData.setValue(audienceInfo.userID);
            updateInviteCount();
        }

        @Override
        public void onRequestJoinAnchor(AnchorInfo anchorInfo, String reason) {
            if (anchorInfo == null) {
                return;
            }

            LogUtils.d("onRequestJoinAnchor -->> " + anchorInfo.userName);

            anchorInfo.applyRequestReason = reason;
            anchorInfo.time = System.currentTimeMillis();

            // 更新列表数据
            if (applyAnchorList.contains(anchorInfo.userid)) {
                for (AnchorInfo temp : currentApplyAnchorInfoList) {
                    if (temp == null
                            || TextUtils.isEmpty(temp.userid)
                            || !temp.userid.equals(anchorInfo.userid)) {
                        continue;
                    }
                    currentApplyAnchorInfoList.remove(temp);
                    break;
                }
            } else {
                applyAnchorList.add(anchorInfo.userid);
            }

            if (!applyAnchorUnInviteList.contains(anchorInfo.userid)) {
                applyAnchorUnInviteList.add(anchorInfo.userid);
            }

            currentApplyAnchorInfoList.add(anchorInfo);
            addMiroConnectLiveData.setValue(anchorInfo);
            updateInviteCount();
        }

        @Override
        public void onKickoutJoinAnchor() {
            LogUtils.d("onKickOutJoinAnchor");
        }

        @Override
        public void onRequestRoomPK(AnchorInfo anchorInfo) {

        }

        @Override
        public void onQuitRoomPK(AnchorInfo anchorInfo) {

        }

        @Override
        public void onReceiveRoomTextMsg(
                String roomID,
                String userid,
                String userName,
                String userAvatar,
                String message
        ) {
            TCChatEntity chatEntity = new TCChatEntity();
            chatEntity.setSenderName(userName);
            chatEntity.setContent(message);
            chatEntity.setType(TCConstants.IMCMD_PAILN_TEXT);
            addChatData(chatEntity);
        }

        @Override
        public void onReceiveRoomCustomMsg(
                String roomID,
                String userid,
                String userName,
                String userAvatar,
                String cmd,
                String message
        ) {
            int type = Integer.parseInt(cmd);
            if (TextUtils.isEmpty(userName)) {
                userName = userid;
            }
            TCChatEntity chatEntity = new TCChatEntity();
            chatEntity.setType(type);
            switch (type) {
                case TCConstants.IMCMD_ENTER_LIVE:
                    chatEntity.setSenderName(userName + "  来了");
                    mEnterAudienceCount++;
                    mCurrentAudienceNum++;
                    mCurrentAudienceString.set(mCurrentAudienceNum + "人");
                    TCSimpleUserInfo userInfo = new TCSimpleUserInfo();
                    userInfo.userid = userid;
                    userInfo.nickname = userName;
                    userInfo.avatar = userAvatar;
                    mAvatarAdapter.addItem(userInfo);
                    break;
                case TCConstants.IMCMD_EXIT_LIVE:
                    chatEntity.setSenderName(userName + "  离开了");
                    mCurrentAudienceNum--;
                    mCurrentAudienceNum = Math.max(mCurrentAudienceNum, 0);
                    mCurrentAudienceString.set(mCurrentAudienceNum + "人");
                    TCSimpleUserInfo userInfo1 = new TCSimpleUserInfo();
                    userInfo1.userid = userid;
                    userInfo1.nickname = userName;
                    userInfo1.avatar = userAvatar;
                    mAvatarAdapter.removeItem(userInfo1);
                    break;
                case TCConstants.IMCMD_PRAISE:
                    chatEntity.setSenderName("通知");
                    chatEntity.setContent(userName + "  点了赞");
                    mHeartCount++;
                    break;
                case TCConstants.IMCMD_FOBIDEN:
                    showErrorMessageDialog.setValue("您已被禁播，无法开启直播！");
                    return;
                case TCConstants.IMCMD_OFF_SHELF:
                    showErrorMessageDialog.setValue("您的直播已被下架！");
                    return;
                case TCConstants.IMCMD_PAILN_TEXT:
                    chatEntity.setContent(message);
                    chatEntity.setSenderName(userName);
                    break;
                case TCConstants.IMCMD_AUDIENCE_CANCEL_REQUEST:
                    dealAudienceRevert(message);
                    break;
                case IMCMD_CONNECT_ONLINE_ANCHOR_VOLUME:
                    dealVolumeUpdate(gson, message);
                    break;
                default:
                    break;
            }
            addChatData(chatEntity);
        }
    };

    private void dealVolumeUpdate(Gson gson, String message) {
        OnlineAnchorVolume onlineAnchorVolume = gson.fromJson(
                message,
                OnlineAnchorVolume.class
        );
        if (onlineAnchorVolume == null) {
            return;
        }
        updateVolume.setValue(onlineAnchorVolume);
    }

    public void dealAnchorEnterReal(AnchorInfo anchorInfo) {
        if (anchorInfo ==null || anchorInfo.index == null) {
            resumeAnchorIndex(anchorInfo);
        }
        ArrayList<AnchorInfo> anchorInfoList = getCurrentAnchorInfoList();
        if (anchorInfo.index == null || anchorInfo.index > 5) {
            for (int i = 0; i < anchorInfoList.size(); i++) {
                if (anchorInfoList.get(i) == null || TextUtils.isEmpty(anchorInfoList.get(i).userid)) {
                    anchorInfo.index = i;
                    break;
                }
            }
        }
        if (anchorInfo == null || anchorInfo.index == null || anchorInfo.index > 5){
            return;
        }
        anchorInfoList.set(anchorInfo.index, anchorInfo);
        setCurrentAnchorInfoList(anchorInfoList);
    }

    public LiveChatAdapter getLiveChatAdapter() {
        if (mLiveChatAdapter == null) {
            mLiveChatAdapter = new LiveChatAdapter();
        }
        return mLiveChatAdapter;
    }

    public AudienceAvatarAdapter getAudienceAvatarAdapter() {
        if (mAvatarAdapter == null) {
            mAvatarAdapter = new AudienceAvatarAdapter();
        }
        return mAvatarAdapter;
    }

    /**
     * 停止直播
     */
    public void onExitClicked() {
        showExitDialog.setValue("");
    }

    /**
     * 切换摄像头
     */
    public void onChangeCameraClicked(View v) {
        mLvbLiveRoom.switchCamera();
    }

    /**
     * 切换摄像头
     */
    public void onMicroConnectClicked(View v) {
        Boolean value = isMicroConnect.getValue();
        if (value == null) {
            value = false;
            isMicroConnect.setValue(false);
        }
        if (value) {
            showMicroConnectApplyInviteDialog.setValue(0);
            return;
        }
        showStartMicroConnectTipDialog.setValue(null);
    }

    /**
     * 发评论
     */
    public void onCommentClicked(View view) {
        Context context = view.getContext();
        if (context == null) {
            return;
        }
        if (!LiveManager.getInstance().checkOrLogin()) {
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(context)) {
            return;
        }
        if (liveType == VIDEO) {
            CommentInputWhiteDialog commentInputWhiteDialog = new CommentInputWhiteDialog(context);
            commentInputWhiteDialog.setUp(onCommentAddListener);
            commentInputWhiteDialog.show();
        } else {
            CommentInputBlackDialog commentInputBlackDialog = new CommentInputBlackDialog(context);
            commentInputBlackDialog.setUp(onCommentAddListener);
            commentInputBlackDialog.show();
        }
    }

    private final OnCommentAddListener onCommentAddListener = comment -> {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我");
        entity.setContent(comment);
        entity.setType(TCConstants.TEXT_TYPE);
        addChatData(entity);
        mLvbLiveRoom.sendRoomTextMsg(comment, new IMLVBLiveRoomListener.SendRoomTextMsgCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.e("sendRoomTextMsg error:" + errInfo);
                showToast(R.string.send_message_fail);
            }

            @Override
            public void onSuccess() {

            }
        });
    };

    /**
     * 分享
     */
    public void shareClicked(View v) {
        String reportUrl = PluginManager.get(H5Plugin.class).getReportUrl() + "?mid=" + mediaId;
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setUrl(shareUrl)
                .setTitle(title)
                .setMediaId(mediaId)
                .setAuthorType(AuthorType.UGC)
                .setImage(mCoverPicUrl)
                .setReportUrl(reportUrl)
                .setFavorite(false)
                .build();
        StatFromModel statFromModel = new StatFromModel(
                mediaId,
                "",
                "",
                "",
                ""
        );
        PluginManager.get(SharePlugin.class).showShareDialog(
                v.getContext(),
                true,
                true,
                shareDataModel,
                statFromModel
        );
    }

    /**
     * 推流参数设置
     */
    public void setPublishConfig(TXCloudVideoView mTXCloudVideoView) {

        // 设置美颜参数
        // BeautyParams beautyParams = new BeautyParams();
        // mLvbLiveRoom.getBeautyManager().setBeautyStyle(beautyParams.mBeautyStyle);
        // mLvbLiveRoom.getBeautyManager().setBeautyLevel(beautyParams.mBeautyLevel);
        // mLvbLiveRoom.getBeautyManager().setWhitenessLevel(beautyParams.mWhiteLevel);
        // mLvbLiveRoom.getBeautyManager().setRuddyLevel(beautyParams.mRuddyLevel);
        // 设置瘦脸参数
        // mLvbLiveRoom.getBeautyManager().setFaceSlimLevel(beautyParams.mFaceSlimLevel);
        // 设置大眼参数
        // mLvbLiveRoom.getBeautyManager().setEyeScaleLevel(beautyParams.mBigEyeLevel);

        if (liveType == SOUND) {
            PermissionManager.requestPermissions(
                    mTXCloudVideoView.getContext(),
                    new PermissionCallback() {
                        @Override
                        public void onPermissionGranted(@NonNull Context context) {
                            mLvbLiveRoom.startLocalPreview(false, true, mTXCloudVideoView);
                            startPublish(true);
                        }

                        @Override
                        public void onPermissionDenied(
                                @NonNull Context context,
                                @Nullable String[] grantedPermissions,
                                @NonNull String[] deniedPermission
                        ) {
                            dealPermissionDenied(deniedPermission);
                        }
                    },
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            );
            return;
        }

        PermissionManager.requestPermissions(
                mTXCloudVideoView.getContext(),
                new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        mLvbLiveRoom.startLocalPreview(true, false, mTXCloudVideoView);
                        // 打开本地预览，传入预览的 View
                        mLvbLiveRoom.setCameraMuteImage(BitmapFactory.decodeResource(mTXCloudVideoView.getResources(), R.drawable.pause_publish));
                        startPublish(false);
                    }

                    @Override
                    public void onPermissionDenied(
                            @NonNull Context context,
                            @Nullable String[] grantedPermissions,
                            @NonNull String[] deniedPermission
                    ) {
                        dealPermissionDenied(deniedPermission);
                    }
                },
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        );

    }

    private void dealPermissionDenied(String[] deniedPermission) {
        for (String s : deniedPermission) {
            switch (s) {
                case Manifest.permission.CAMERA:
                    showErrorMessageDialog.setValue("没有相机相关权限");
                    return;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    showErrorMessageDialog.setValue("没有存储相关权限");
                    return;
                case Manifest.permission.RECORD_AUDIO:
                    showErrorMessageDialog.setValue("没有录音相关权限");
                    return;

            }
        }
    }


    /**
     * 开始推流并创建直播间
     */
    private void startPublish(boolean isPureAudio) {
        String roomInfo = "";
        try {
            roomInfo = new JSONObject()
                    .put("title", title)
                    .put("frontcover", mCoverPicUrl)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String finalRoomInfo = roomInfo;
        mLvbLiveRoom.createRoom(
                isPureAudio,
                mediaId,
                finalRoomInfo,
                new IMLVBLiveRoomListener.CreateRoomCallback() {
                    @Override
                    public void onSuccess(String roomId) {
                        retryCount = 0;
                        startTimer();
                        LogUtils.d(String.format("创建直播间%s成功", roomId));
                        resetLiveMessage();
                    }

                    @Override
                    public void onError(int errCode, String error) {
                        if (retryCount == 0) {
                            onDestroy();
                            showErrorMessageDialog.setValue(error);
                        } else {
                            retryCount--;
                            startPublish(isPureAudio);
                        }
                    }
                });


    }

    /**
     * 重新拉取直播数据
     */
    private void resetLiveMessage() {

        //获取房间信息
        mLvbLiveRoom.getCustomInfo(new IMLVBLiveRoomListener.GetCustomInfoCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onGetCustomInfo(Map<String, Object> customInfo) {
                Object o = customInfo.get("praise");
                if (o != null) {
                    mHeartCount = (int) o;
                }
            }
        });

        //获取观众信息
        mLvbLiveRoom.getAudienceList(new IMLVBLiveRoomListener.GetAudienceListCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(ArrayList<AudienceInfo> audienceInfoList) {
                if (audienceInfoList == null || audienceInfoList.isEmpty()) {
                    mCurrentAudienceNum = 0;
                    mCurrentAudienceString.set(mCurrentAudienceNum + "人");
                    return;
                }
                AudienceInfo me = null;
                for (AudienceInfo audienceInfo : audienceInfoList) {
                    if (audienceInfo == null) {
                        continue;
                    }
                    if (LiveManager.getInstance().getUserId().equals(audienceInfo.userID)) {
                        me = audienceInfo;
                    } else {
                        TCSimpleUserInfo simpleUserInfo = new TCSimpleUserInfo();
                        simpleUserInfo.avatar = audienceInfo.userAvatar;
                        simpleUserInfo.userid = audienceInfo.userID;
                        simpleUserInfo.nickname = audienceInfo.userName;
                        mAvatarAdapter.addItem(simpleUserInfo);
                    }
                }
                if (me != null) {
                    //去除自己
                    audienceInfoList.remove(me);
                }
                mCurrentAudienceNum = audienceInfoList.size();
                mCurrentAudienceString.set(mCurrentAudienceNum + "人");
            }
        });
    }

    /**
     * 直播计时
     */
    private void startTimer() {
        // 填写了后台服务器地址
        if (!TextUtils.isEmpty(Constants.APP_SVR_URL)) {
            try {
                JSONObject body = new JSONObject()
                        .put("userId", LiveManager.getInstance().getUserId())
                        .put("title", title)
                        .put("frontCover", mCoverPicUrl);
                TCHTTPMgr.getInstance().requestWithSign(
                        Constants.APP_SVR_URL + "/upload_room",
                        body,
                        null
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        timeObservable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> mLiveTimeTotal = aLong);
    }

    private void stopTimer() {
        if (timeObservable != null) {
            timeObservable.dispose();
            timeObservable = null;
        }
    }

    public void onPause() {
        if (mLvbLiveRoom != null) {
            mLvbLiveRoom.onPausePusher();
        }
    }

    public void onResume() {
        if (mLvbLiveRoom != null) {
            mLvbLiveRoom.onResumePusher();
        }
    }

    public void onDestroy() {
        mLvbLiveRoom.stopLocalPreview();
        stopTimer();
        onExitRoom();
    }

    public void uploadMessage() {
        LiveManager.getInstance().upLoadLiveMessage(mEnterAudienceCount, mHeartCount);
        onDestroy();
    }

    private void dealAudienceRevert(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        if (!applyAnchorList.contains(userId)) {
            return;
        }
        // 更新列表数据
        for (AnchorInfo temp : currentApplyAnchorInfoList) {
            if (temp == null
                    || TextUtils.isEmpty(temp.userid)
                    || !temp.userid.equals(userId)) {
                continue;
            }
            applyAnchorList.remove(userId);
            applyAnchorUnInviteList.remove(userId);
            currentApplyAnchorInfoList.remove(temp);
            break;
        }

        invalidMiroConnectLiveData.setValue(userId);

        updateInviteCount();
    }

    /**
     * 评论列表添加数据
     *
     * @param chatEntity 评论内容
     */
    private void addChatData(TCChatEntity chatEntity) {
        updateChatRecyclerView.setValue(mLiveChatAdapter.getItemCount());
        mLiveChatAdapter.addData(chatEntity);
    }

    public void onExitRoom() {
        mLvbLiveRoom.exitRoom(new IMLVBLiveRoomListener.ExitRoomCallback() {
            @Override
            public void onSuccess() {
                LogUtils.d("exitRoom Success");
            }

            @Override
            public void onError(int errCode, String e) {
                LogUtils.e("exitRoom failed, errorCode = " + errCode + " errMessage = " + e);
            }
        });
        mLvbLiveRoom.setListener(null);
    }

    public void initAudienceList() {
        ArrayList<AnchorInfo> anchorInfoList = getCurrentAnchorInfoList();
        if (anchorInfoList.size() == 0) {
            for (int index = 0; index < 6; index++) {
                anchorInfoList.add(new AnchorInfo(index));
            }
        } else {
            for (AnchorInfo anchorInfo : anchorInfoList) {
                anchorInfo.init();
            }
        }
        setCurrentAnchorInfoList(anchorInfoList);
    }

    /**
     * 关闭连麦
     * <p>
     * 遍历当前上麦的用户，踢掉
     * 再发送一个IM消息，通知观众，直播间连麦状态已关闭
     */
    public void closeMicroConnect() {
        ArrayList<AnchorInfo> anchorInfoList = currentAnchorInfoList.getValue();
        if (anchorInfoList == null) {
            anchorInfoList = new ArrayList<>();
        }
        for (AnchorInfo anchorInfo : anchorInfoList) {
            if (anchorInfo == null
                    || TextUtils.isEmpty(anchorInfo.userid)) {
                continue;
            }
            mLvbLiveRoom.kickoutJoinAnchor(anchorInfo.userid);
        }
        anchorInfoList.clear();
        setCurrentAnchorInfoList(anchorInfoList);

        currentApplyAnchorInfoList.clear();
        clearMicroConnectDataLiveData.setValue(true);

        applyAnchorList.clear();
        applyAnchorUnInviteList.clear();
        applyAnchorCountVisibility.set(false);
        applyAnchorCountString.set("0");

        isMicroConnect.setValue(false);
        mLvbLiveRoom.sendRoomCustomMsg(
                String.valueOf(TCConstants.IMCMD_MICRO_CONNECT_STATUS),
                Constant.MICRO_CONNECT.OFF + "",
                null
        );

    }

    public void getLiveMessage() {
        liveRepository.getLiveMessage(mediaId)
                .subscribe(new GVideoResponseObserver<LiveDetailModel>() {
                    @Override
                    protected void onSuccess(@NonNull LiveDetailModel model) {
                        liveDetailLiveData.setValue(model);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        showErrorMessageDialog.setValue(throwable.getMessage());
                        LogUtils.e(throwable.getMessage());
                    }
                });

    }

    public void updateInviteCount() {
        int count = 0;
        for (String temp : applyAnchorList) {
            if (applyAnchorUnInviteList.contains(temp)) {
                count++;
            }
        }
        applyAnchorCountVisibility.set(count > 0);
        if (count > 99) {
            applyAnchorCountString.set("···");
        } else {
            applyAnchorCountString.set(count + "");
        }
    }

    /**
     * {@link IMLVBLiveRoomListener#onAnchorEnter(AnchorInfo)}回调中的值
     * 是从获取直播间主播列表的接口中取出，里面不会有index
     * 所以需要根据申请列表中的数据，进行恢复
     *      *
     * @param anchorInfo 主播信息
     */
    public void resumeAnchorIndex(AnchorInfo anchorInfo) {
        if (currentApplyAnchorInfoList.isEmpty()) {
            return;
        }
        for (AnchorInfo apply : currentApplyAnchorInfoList) {
            if (apply == null
                    || !TextUtils.equals(apply.userid, anchorInfo.userid)) {
                continue;
            }
            anchorInfo.index = apply.index;
        }
    }

    public void setAnchorMuteStatus(AnchorInfo anchorInfo, boolean mute) {
        ArrayList<AnchorInfo> anchorInfoArrayList = getCurrentAnchorInfoList();
        if (anchorInfoArrayList.isEmpty()) {
            return;
        }
        for (AnchorInfo temp : anchorInfoArrayList) {
            if (temp == null
                    || !TextUtils.equals(temp.userid, anchorInfo.userid)) {
                continue;
            }
            temp.mute = mute;
        }
    }

    public boolean getMuteStatus(AnchorInfo anchorInfo) {
        ArrayList<AnchorInfo> anchorInfoArrayList = getCurrentAnchorInfoList();
        if (anchorInfoArrayList.isEmpty()) {
            return false;
        }
        for (AnchorInfo temp : anchorInfoArrayList) {
            if (temp == null
                    || !TextUtils.equals(temp.userid, anchorInfo.userid)) {
                continue;
            }
            return temp.mute;
        }
        return false;
    }


    // 当前真实已连麦的数据
    public final CheckThreadLiveData<ArrayList<AnchorInfo>> currentAnchorInfoList = new CheckThreadLiveData<>();
    // 临时连麦数据，每次主播同意连麦都放入临时数据中，解决
    private final ArrayList<AnchorInfo> tempAnchorInfoList = new ArrayList<>();

    public ArrayList<AnchorInfo> getCurrentAnchorInfoList() {
        ArrayList<AnchorInfo> anchorInfoList = currentAnchorInfoList.getValue();
        if (anchorInfoList == null) {
            anchorInfoList = new ArrayList<>();
        }
        return anchorInfoList;
    }

    private void setCurrentAnchorInfoList(ArrayList<AnchorInfo> anchorInfoList){
        currentAnchorInfoList.setValue(anchorInfoList);
        tempAnchorInfoList.clear();
        tempAnchorInfoList.addAll(anchorInfoList);
    }

    public ArrayList<AnchorInfo> getTempAnchorInfoList() {
        return tempAnchorInfoList;
    }

    private boolean isNeedRefreshTempList = true;
    /**
     * 添加临时数据
     */
    public void addTempAnchor(AnchorInfo anchorInfo){
        if (anchorInfo == null || anchorInfo.index > tempAnchorInfoList.size() - 1 ){
            return;
        }
        boolean needAddData = true;
        for (AnchorInfo info : getTempAnchorInfoList()){
            if (TextUtils.equals(info.userid,anchorInfo.userid)){
                needAddData = false;
            }
        }
        if (needAddData){
            getTempAnchorInfoList().remove(anchorInfo.index);
            getTempAnchorInfoList().add(anchorInfo.index, anchorInfo);
        }
        if (isNeedRefreshTempList){
            isNeedRefreshTempList = false;
            AsyncUtils.runOnUIThread(() -> {
                tempAnchorInfoList.clear();
                tempAnchorInfoList.addAll(getCurrentAnchorInfoList());
                isNeedRefreshTempList = true;
            },5000);
        }
    }

}
