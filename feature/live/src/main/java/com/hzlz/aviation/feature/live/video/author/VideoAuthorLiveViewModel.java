package com.hzlz.aviation.feature.live.video.author;

import static com.hzlz.aviation.kernel.base.Constant.MICRO_CONNECT.OFF;
import static com.hzlz.aviation.kernel.base.Constant.MICRO_CONNECT.ON;

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
import androidx.databinding.ObservableField;

import com.hzlz.aviation.feature.live.Constants;
import com.hzlz.aviation.feature.live.LiveManager;
import com.hzlz.aviation.feature.live.adapter.AudienceAvatarAdapter;
import com.hzlz.aviation.feature.live.adapter.LiveChatAdapter;
import com.hzlz.aviation.feature.live.callback.OnCommentAddListener;
import com.hzlz.aviation.feature.live.databinding.ViewAnchorPlayerListBinding;
import com.hzlz.aviation.feature.live.dialog.CommentInputWhiteDialog;
import com.hzlz.aviation.feature.live.liveroom.IMLVBLiveRoomListener;
import com.hzlz.aviation.feature.live.liveroom.MLVBLiveRoom;
import com.hzlz.aviation.feature.live.liveroom.MLVBLiveRoomImpl;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCChatEntity;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCSimpleUserInfo;
import com.hzlz.aviation.feature.live.liveroom.live.net.TCHTTPMgr;
import com.hzlz.aviation.feature.live.liveroom.live.utils.TCConstants;
import com.hzlz.aviation.feature.live.liveroom.live.utils.TCUtils;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AudienceInfo;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.MLVBCommonDef;
import com.hzlz.aviation.feature.live.model.LiveDetailModel;
import com.hzlz.aviation.feature.live.repository.LiveRepository;
import com.hzlz.aviation.feature.live.video.widget.BeautyParams;
import com.hzlz.aviation.feature.live.video.widget.TCVideoView;
import com.hzlz.aviation.feature.live.video.widget.TCVideoViewMgr;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.network.exception.GVideoAPIException;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AppManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.feature.live.R;
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

    // ??????????????????
    private Disposable timeObservable;

    // ??????????????????
    public int mHeartCount;

    // ????????????????????????
    public int mEnterAudienceCount = 0;

    // ?????????????????????
    public final ObservableField<String> mCurrentAudienceString = new ObservableField<>();

    // ????????????
    public final CheckThreadLiveData<String> showErrorMessageDialog = new CheckThreadLiveData<>();

    // ??????????????????
    public final CheckThreadLiveData<String> showExitDialog = new CheckThreadLiveData<>();

    // ????????????
    public final CheckThreadLiveData<Integer> updateChatRecyclerView = new CheckThreadLiveData<>();

    // ????????????????????? - ???????????????
    public MLVBLiveRoom mLvbLiveRoom;

    // ???????????????UserID
    public String userID;

    // ??????????????????
    private String mCoverPicUrl;

    // ?????????????????????
    private LiveChatAdapter mLiveChatAdapter;

    // ?????????????????????
    public AudienceAvatarAdapter mAvatarAdapter;

    // ???????????????
    private long mLiveTimeTotal;

    // ??????Id
    public String mediaId = "";

    // ????????????
    public String shareUrl = "";

    // ??????
    public String title = "";

    // ??????????????????
    private int mCurrentAudienceNum = 0;

    // ??????????????????????????????????????????????????????????????????
    // ????????????????????????0????????????Activity
    // ????????????????????????0
    private int retryCount = 2;

    // ???????????????????????????
    private boolean isMicroConnect = false;

    private TCVideoViewMgr mPlayerVideoViewList;   // ?????????????????????View

    public Handler mMainHandler = new Handler(Looper.getMainLooper());

    // ?????????????????????
    public final CheckThreadLiveData<LiveDetailModel> liveDetailLiveData = new CheckThreadLiveData<>();

    // ??????????????????????????????
    private boolean mPendingRequest = false;

    public VideoAuthorLiveViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData(Context context, ViewAnchorPlayerListBinding binding) {
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLvbLiveRoom = MLVBLiveRoom.sharedInstance(context);
        mLvbLiveRoom.setListener(imlvbLiveRoomListener);
        mCurrentAudienceString.set(0 + "???");

        // ?????????????????????
        mPlayerVideoViewList = new TCVideoViewMgr(binding, userID -> {
            String tips = "";
            for (AnchorInfo item : getCurrentAnchorInfoList()) {
                if (userID.equalsIgnoreCase(item.userid)) {
                    tips = "???????????? "+ item.userName + " ???????????????";
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
            },"??????",tips,"??????","??????");
            dialog.show();
        });
    }


    private final IMLVBLiveRoomListener imlvbLiveRoomListener = new IMLVBLiveRoomListener() {
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            switch (errCode) {
                case MLVBCommonDef.LiveRoomErrorCode.ERROR_IM_FORCE_OFFLINE:
                    // IM???????????????
                    onPause();
                    TCUtils.showKickOut(true);
                    break;
                case TXLiveConstants.PUSH_WARNING_RECONNECT:
                    //???????????????????????????????????????????????????????????????????????????
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
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "????????????????????????????????????????????????????????????");
                    return;
                }

                if (getCurrentAnchorInfoList().size() >= MLVBLiveRoomImpl.MAX_JOIN_ANCHOR) {
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "????????????????????????????????????");
                    return;
                }

                String tip = anchorInfo.userName + "??????????????????????????????????????????";
                DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(AppManager.getAppManager().currentActivity());
                dialog.setCanceledOnTouchOutside(false);

                Runnable runnable = () -> {
                    dialog.dismiss();
                    mPendingRequest = false;
                    ToastUtils.showLong("????????????????????????????????????????????????????????????");
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "??????????????????????????????????????????????????????");
                };

                dialog.init(view -> {
                    //????????????
                    dialog.dismiss();
                    mMainHandler.removeCallbacks(runnable);
                    mPendingRequest = false;
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,false, "?????????????????????????????????????????????");
                }, view -> {
                    //????????????
                    dialog.dismiss();
                    mMainHandler.removeCallbacks(runnable);
                    mPendingRequest = false;
                    mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, 0,true, "");
                },"????????????",tip,"??????","??????");

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
                    chatEntity.setSenderName(userName + "  ??????");
                    mEnterAudienceCount++;
                    mCurrentAudienceNum++;
                    mCurrentAudienceString.set(mCurrentAudienceNum + "???");
                    TCSimpleUserInfo userInfo = new TCSimpleUserInfo();
                    userInfo.userid = userid;
                    userInfo.nickname = userName;
                    userInfo.avatar = userAvatar;
                    mAvatarAdapter.addItem(userInfo);
                    break;
                case TCConstants.IMCMD_EXIT_LIVE:
                    chatEntity.setSenderName(userName + "  ?????????");
                    mCurrentAudienceNum--;
                    mCurrentAudienceNum = Math.max(mCurrentAudienceNum, 0);
                    mCurrentAudienceString.set(mCurrentAudienceNum + "???");
                    TCSimpleUserInfo userInfo1 = new TCSimpleUserInfo();
                    userInfo1.userid = userid;
                    userInfo1.nickname = userName;
                    userInfo1.avatar = userAvatar;
                    mAvatarAdapter.removeItem(userInfo1);
                    break;
                case TCConstants.IMCMD_PRAISE:
                    chatEntity.setSenderName("??????");
                    chatEntity.setContent(userName + "  ?????????");
                    mHeartCount++;
                    break;
                case TCConstants.IMCMD_FOBIDEN:
                    showErrorMessageDialog.setValue("???????????????????????????????????????");
                    return;
                case TCConstants.IMCMD_OFF_SHELF:
                    showErrorMessageDialog.setValue("???????????????????????????");
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
     * ????????????????????????
     * @param anchorInfo  ????????????
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
        //????????????????????????
        mLvbLiveRoom.startRemoteView(anchorInfo, videoView.videoView, new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {
                mMainHandler.postDelayed(() -> {
                    if (videoView!=null) {
                        //???????????????stopLoading ???????????????????????????button
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
     * ??????????????????
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
        //????????????????????????
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
     * ????????????
     */
    public void onExitClicked() {
        showExitDialog.setValue("");
    }

    /**
     * ????????????
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
                    ToastUtils.showShort(isOpen? "??????????????????" : "???????????????");
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
     * ???????????????
     */
    public void onChangeCameraClicked(View v) {
        mLvbLiveRoom.switchCamera();
    }

    /**
     * ?????????
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
        entity.setSenderName("???");
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
     * ??????
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
     * ??????????????????
     */
    public void setPublishConfig(TXCloudVideoView mTXCloudVideoView) {

        PermissionManager.requestPermissions(mTXCloudVideoView.getContext(), new PermissionCallback() {
            @Override
            public void onPermissionGranted(@NonNull Context context) {
                // ??????????????????
                BeautyParams beautyParams = new BeautyParams();
                mLvbLiveRoom.getBeautyManager().setBeautyStyle(beautyParams.mBeautyStyle);
                mLvbLiveRoom.getBeautyManager().setBeautyLevel(beautyParams.mBeautyLevel);
                mLvbLiveRoom.getBeautyManager().setWhitenessLevel(beautyParams.mWhiteLevel);
                mLvbLiveRoom.getBeautyManager().setRuddyLevel(beautyParams.mRuddyLevel);
                //??????????????????
                mLvbLiveRoom.getBeautyManager().setFaceSlimLevel(beautyParams.mFaceSlimLevel);
                //??????????????????
                mLvbLiveRoom.getBeautyManager().setEyeScaleLevel(beautyParams.mBigEyeLevel);

                mTXCloudVideoView.setVisibility(View.VISIBLE);
                mLvbLiveRoom.startLocalPreview(true, false, mTXCloudVideoView);
                // ???????????????????????????????????? View
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
                    showErrorMessageDialog.setValue("????????????????????????");
                    return;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    showErrorMessageDialog.setValue("????????????????????????");
                    return;
                case Manifest.permission.RECORD_AUDIO:
                    showErrorMessageDialog.setValue("????????????????????????");
                    return;

            }
        }
    }


    /**
     * ??????????????????????????????
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
                LogUtils.d(String.format("???????????????%s??????", roomId));
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
     * ????????????????????????
     */
    private void resetLiveMessage() {
        //??????????????????
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

        //??????????????????
        mLvbLiveRoom.getAudienceList(new IMLVBLiveRoomListener.GetAudienceListCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(ArrayList<AudienceInfo> audienceInfoList) {
                if (audienceInfoList == null || audienceInfoList.isEmpty()) {
                    mCurrentAudienceNum = 0;
                    mCurrentAudienceString.set(mCurrentAudienceNum + "???");
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
                    //????????????
                    audienceInfoList.remove(me);
                }
                mCurrentAudienceNum = audienceInfoList.size();
                mCurrentAudienceString.set(mCurrentAudienceNum + "???");
            }
        });
    }

    /**
     * ????????????
     */
    private void startTimer() {
        // ??????????????????????????????
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
     * ????????????????????????
     *
     * @param chatEntity ????????????
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
     * ??????????????????
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

    // ??????????????????????????????
    private ArrayList<AnchorInfo> currentAnchorInfoList = new ArrayList<>();

    public ArrayList<AnchorInfo> getCurrentAnchorInfoList() {
        return currentAnchorInfoList;
    }

    private void setCurrentAnchorInfoList(ArrayList<AnchorInfo> anchorInfoList){
        currentAnchorInfoList.clear();
        currentAnchorInfoList.addAll(anchorInfoList);
    }

}
