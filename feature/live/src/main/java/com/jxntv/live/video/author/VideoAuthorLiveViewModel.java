package com.jxntv.live.video.author;

import static com.jxntv.base.Constant.MICRO_CONNECT.OFF;
import static com.jxntv.base.Constant.MICRO_CONNECT.ON;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.ObservableField;

import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.dialog.DefaultEnsureCancelDialog;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.Constants;
import com.jxntv.live.LiveManager;
import com.jxntv.live.R;
import com.jxntv.live.adapter.AudienceAvatarAdapter;
import com.jxntv.live.adapter.LiveChatAdapter;
import com.jxntv.live.callback.OnCommentAddListener;
import com.jxntv.live.databinding.ViewAnchorPlayerListBinding;
import com.jxntv.live.dialog.CommentInputWhiteDialog;
import com.jxntv.live.liveroom.IMLVBLiveRoomListener;
import com.jxntv.live.liveroom.MLVBLiveRoom;
import com.jxntv.live.liveroom.MLVBLiveRoomImpl;
import com.jxntv.live.liveroom.live.entity.TCChatEntity;
import com.jxntv.live.liveroom.live.entity.TCSimpleUserInfo;
import com.jxntv.live.liveroom.live.net.TCHTTPMgr;
import com.jxntv.live.liveroom.live.utils.TCConstants;
import com.jxntv.live.liveroom.live.utils.TCUtils;
import com.jxntv.live.liveroom.roomutil.commondef.AnchorInfo;
import com.jxntv.live.liveroom.roomutil.commondef.AudienceInfo;
import com.jxntv.live.liveroom.roomutil.commondef.MLVBCommonDef;
import com.jxntv.live.model.LiveDetailModel;
import com.jxntv.live.repository.LiveRepository;
import com.jxntv.live.video.widget.BeautyParams;
import com.jxntv.live.video.widget.TCVideoView;
import com.jxntv.live.video.widget.TCVideoViewMgr;
import com.jxntv.network.exception.GVideoAPIException;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.LogUtils;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
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
public class VideoAuthorLiveViewModel extends BaseViewModel {

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

    // 当前是否是连麦状态
    private boolean isMicroConnect = false;

    private TCVideoViewMgr mPlayerVideoViewList;   // 主播视频列表的View

    public Handler mMainHandler = new Handler(Looper.getMainLooper());

    // 直播间详情信息
    public final CheckThreadLiveData<LiveDetailModel> liveDetailLiveData = new CheckThreadLiveData<>();

    // 主播是否正在处理请求
    private boolean mPendingRequest = false;

    public VideoAuthorLiveViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData(Context context, ViewAnchorPlayerListBinding binding) {
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLvbLiveRoom = MLVBLiveRoom.sharedInstance(context);
        mLvbLiveRoom.setListener(imlvbLiveRoomListener);
        mCurrentAudienceString.set(0 + "人");

        // 监听踢出的回调
        mPlayerVideoViewList = new TCVideoViewMgr(binding, userID -> {
            String tips = "";
            for (AnchorInfo item : getCurrentAnchorInfoList()) {
                if (userID.equalsIgnoreCase(item.userid)) {
                    tips = "确定要把 "+ item.userName + " 踢出连麦？";
                    break;
                }
            }
            DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(AppManager.getAppManager().currentActivity());
            dialog.init( v -> dialog.dismiss(), v2 -> {
                if (userID != null) {
                    for (AnchorInfo item : getCurrentAnchorInfoList()) {
                        if (userID.equalsIgnoreCase(item.userid)) {
                            onDoAnchorExit(item);
                            break;
                        }
                    }
                    mLvbLiveRoom.kickoutJoinAnchor(userID);
                    dialog.dismiss();
                }
            },"提示",tips,"取消","确定");
            dialog.show();
        });
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

        }

        @Override
        public void onRequestJoinAnchor(AnchorInfo anchorInfo, String reason) {

            if (anchorInfo == null) {
                return;
            }
            LogUtils.d("onRequestJoinAnchor -->> " + anchorInfo.userName);

            mMainHandler.post(() -> {
                if (mPendingRequest) {
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "主播正在处理其他连麦申请，请稍后重新申请");
                    return;
                }

                if (getCurrentAnchorInfoList().size() >= MLVBLiveRoomImpl.MAX_JOIN_ANCHOR) {
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "连麦人数已满，请稍后重试");
                    return;
                }

                String tip = anchorInfo.userName + "向你发起连麦申请，是否接受？";
                DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(AppManager.getAppManager().currentActivity());
                dialog.setCanceledOnTouchOutside(false);

                Runnable runnable = () -> {
                    dialog.dismiss();
                    mPendingRequest = false;
                    ToastUtils.showLong("因长时间未处理连麦申请，系统已经自动拒绝");
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "主播暂未处理您的连麦申请，请重新申请");
                };

                dialog.init(view -> {
                    //拒绝连麦
                    dialog.dismiss();
                    mMainHandler.removeCallbacks(runnable);
                    mPendingRequest = false;
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "您的连麦申请被拒绝，请稍后重试");
                }, view -> {
                    //接受连麦
                    dialog.dismiss();
                    mMainHandler.removeCallbacks(runnable);
                    mPendingRequest = false;
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,true, "");
                },"连麦申请",tip,"拒绝","接受");

                dialog.show();
                mPendingRequest = true;
                mMainHandler.postDelayed(runnable, MLVBLiveRoomImpl.JOIN_ANCHOR_TIME_OUT);
            });
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
        public void onReceiveRoomTextMsg(String roomID, String userid, String userName, String userAvatar, String message) {
            TCChatEntity chatEntity = new TCChatEntity();
            chatEntity.setSenderName(userName);
            chatEntity.setContent(message);
            chatEntity.setType(TCConstants.IMCMD_PAILN_TEXT);
            addChatData(chatEntity);
        }

        @Override
        public void onReceiveRoomCustomMsg(String roomID, String userid, String userName, String userAvatar, String cmd, String message) {
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
//                    dealAudienceRevert(message);
                    break;
                default:
                    break;
            }
            addChatData(chatEntity);
        }
    };

    /**
     * 处理观众加入连麦
     * @param anchorInfo  观众数据
     */
    public void dealAnchorEnterReal(AnchorInfo anchorInfo) {
        if (anchorInfo == null || anchorInfo.userid == null) {
            return;
        }
        final TCVideoView videoView = mPlayerVideoViewList.applyVideoView(anchorInfo.userid);
        if (videoView == null) {
            return;
        }

        if (currentAnchorInfoList != null) {
            boolean exist = false;
            for (AnchorInfo item : currentAnchorInfoList) {
                if (anchorInfo.userid.equalsIgnoreCase(item.userid)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                currentAnchorInfoList.add(anchorInfo);
            }
        }
        videoView.startLoading();
        //开启远端视频渲染
        mLvbLiveRoom.startRemoteView(anchorInfo, videoView.videoView, new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {
                mMainHandler.postDelayed(() -> {
                    if (videoView!=null) {
                        //推流成功，stopLoading 大主播显示出踢人的button
                        videoView.stopLoading(true);
                    }
                },1000);

            }

            @Override
            public void onError(int errCode, String errInfo) {
                videoView.stopLoading(false);
                onDoAnchorExit(anchorInfo);
            }

            @Override
            public void onEvent(int event, Bundle param) {

            }
        });
    }

    /**
     * 观众退出连麦
     */
    private void onDoAnchorExit(AnchorInfo pusherInfo) {
        if (currentAnchorInfoList != null) {
            Iterator<AnchorInfo> it = currentAnchorInfoList.iterator();
            while (it.hasNext()) {
                AnchorInfo item = it.next();
                if (pusherInfo.userid.equalsIgnoreCase(item.userid)) {
                    it.remove();
                    break;
                }
            }
        }
        //关闭远端视频渲染
        mLvbLiveRoom.stopRemoteView(pusherInfo);
        mPlayerVideoViewList.recycleVideoView(pusherInfo.userid);
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
     * 开启连麦
     */
    public void onMicroConnectClicked(View v) {

        boolean isOpen = !isMicroConnect;
        String title = "";
        String tip = "";
        if (isOpen){
            title = v.getContext().getResources().getString(R.string.connect_micro_confirm);
            tip =  v.getContext().getResources().getString(R.string.connect_micro_confirm_tip);
        }else {
            title = v.getContext().getResources().getString(R.string.close_micro_connect);
            tip =  v.getContext().getResources().getString(R.string.close_micro_connect_tip);
        }
        String cancel = v.getContext().getResources().getString(R.string.cancel);
        String confirm = v.getContext().getResources().getString(R.string.confirm);
        DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(v.getContext());
        dialog.init(view -> dialog.dismiss(), view -> {
            liveRepository.startConnectMicro(isOpen ? ON : OFF , mediaId).subscribe(new BaseResponseObserver<Object>() {
                @Override
                protected void onRequestData(Object o) {
                    dialog.dismiss();
                    mLvbLiveRoom.sendRoomCustomMsg(String.valueOf(TCConstants.IMCMD_MICRO_CONNECT_STATUS),
                                    isOpen ? String.valueOf(ON) : String.valueOf(OFF), null);
                    isMicroConnect = isOpen;
                    ToastUtils.showShort(isOpen? "打开连麦成功" : "已关闭连麦");
                    if (!isOpen){
                        for (AnchorInfo item : getCurrentAnchorInfoList()) {
                            onDoAnchorExit(item);
                            mLvbLiveRoom.kickoutJoinAnchor(userID);
                        }
                    }
                }

                @Override
                protected void onRequestError(Throwable throwable) {
                    if (throwable instanceof GVideoAPIException) {
                        String errorMessage = throwable.getMessage();
                        if (errorMessage == null || TextUtils.isEmpty(errorMessage)) {
                            return;
                        }
                        showToast(errorMessage);
                    }
                }
            });
            }, title, tip, cancel, confirm);
        dialog.show();
    }

    /**
     * 切换摄像头
     */
    public void onChangeCameraClicked(View v) {
        mLvbLiveRoom.switchCamera();
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
        CommentInputWhiteDialog commentInputWhiteDialog = new CommentInputWhiteDialog(context);
        commentInputWhiteDialog.setUp(onCommentAddListener);
        commentInputWhiteDialog.show();
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

        PermissionManager.requestPermissions(mTXCloudVideoView.getContext(), new PermissionCallback() {
            @Override
            public void onPermissionGranted(@NonNull Context context) {
                // 设置美颜参数
                BeautyParams beautyParams = new BeautyParams();
                mLvbLiveRoom.getBeautyManager().setBeautyStyle(beautyParams.mBeautyStyle);
                mLvbLiveRoom.getBeautyManager().setBeautyLevel(beautyParams.mBeautyLevel);
                mLvbLiveRoom.getBeautyManager().setWhitenessLevel(beautyParams.mWhiteLevel);
                mLvbLiveRoom.getBeautyManager().setRuddyLevel(beautyParams.mRuddyLevel);
                //设置瘦脸参数
                mLvbLiveRoom.getBeautyManager().setFaceSlimLevel(beautyParams.mFaceSlimLevel);
                //设置大眼参数
                mLvbLiveRoom.getBeautyManager().setEyeScaleLevel(beautyParams.mBigEyeLevel);

                mTXCloudVideoView.setVisibility(View.VISIBLE);
                mLvbLiveRoom.startLocalPreview(true, false, mTXCloudVideoView);
                // 打开本地预览，传入预览的 View
                mLvbLiveRoom.setCameraMuteImage(BitmapFactory.decodeResource(mTXCloudVideoView.getResources(), R.drawable.pause_publish));
                startPublish(false);
            }

            @Override
            public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                dealPermissionDenied(deniedPermission);
            }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
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
        mLvbLiveRoom.createRoom(isPureAudio, mediaId, finalRoomInfo, new IMLVBLiveRoomListener.CreateRoomCallback() {
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
                TCHTTPMgr.getInstance().requestWithSign(Constants.APP_SVR_URL + "/upload_room", body, null);
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
        stopTimer();
        onExitRoom();
        if(mPlayerVideoViewList!=null) {
            mPlayerVideoViewList.recycleVideoView();
            mPlayerVideoViewList = null;
        }
    }

    public void uploadMessage() {
        LiveManager.getInstance().upLoadLiveMessage(mEnterAudienceCount, mHeartCount);
        onDestroy();
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
        if (mLvbLiveRoom!=null) {
            mLvbLiveRoom.stopLocalPreview();
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
            mLvbLiveRoom = null;
        }
    }

    /**
     * 获取直播数据
     */
    public void getLiveMessage() {
        liveRepository.getLiveMessage(mediaId)
                .subscribe(new GVideoResponseObserver<LiveDetailModel>() {
                    @Override
                    protected void onSuccess(@NonNull LiveDetailModel model) {
                        LiveDetailModel.DetailVOBean detailVOBean = model.getDetailVO();
                        if (detailVOBean == null) {
                            isMicroConnect = false;
                        } else {
                            isMicroConnect = detailVOBean.connectVideo == 1;
                        }
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

    // 当前真实已连麦的数据
    private ArrayList<AnchorInfo> currentAnchorInfoList = new ArrayList<>();

    public ArrayList<AnchorInfo> getCurrentAnchorInfoList() {
        return currentAnchorInfoList;
    }

    private void setCurrentAnchorInfoList(ArrayList<AnchorInfo> anchorInfoList){
        currentAnchorInfoList.clear();
        currentAnchorInfoList.addAll(anchorInfoList);
    }

}
