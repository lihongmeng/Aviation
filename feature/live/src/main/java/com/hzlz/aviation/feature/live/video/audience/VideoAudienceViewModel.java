package com.hzlz.aviation.feature.live.video.audience;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.feature.live.LiveManager;
import com.hzlz.aviation.feature.live.adapter.LiveChatAdapter;
import com.hzlz.aviation.feature.live.callback.OnCommentAddListener;
import com.hzlz.aviation.feature.live.databinding.ViewAnchorPlayerListBinding;
import com.hzlz.aviation.feature.live.dialog.CommentInputWhiteDialog;
import com.hzlz.aviation.feature.live.liveroom.IMLVBLiveRoomListener;
import com.hzlz.aviation.feature.live.liveroom.MLVBLiveRoom;
import com.hzlz.aviation.feature.live.liveroom.MLVBLiveRoomImpl;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCChatEntity;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCVideoInfo;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCVideoListMgr;
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
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AppManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.feature.live.R;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author huangwei
 * date : 2021/3/3
 * desc :
 **/
public class VideoAudienceViewModel extends BaseViewModel implements IMLVBLiveRoomListener {

    // ????????????
    public final CheckThreadLiveData<String> errorMsgLiveData = new CheckThreadLiveData<>();
    // ????????????
    public final CheckThreadLiveData<Integer> chatMessageLiveData = new CheckThreadLiveData<>();
    // ?????????????????????
    public final CheckThreadLiveData<LiveDetailModel> liveDetailLiveData = new CheckThreadLiveData<>();
    // ????????????
    public final ObservableBoolean isFollow = new ObservableBoolean();
    // ????????????????????????
    public final List<AnchorInfo> currentAnchorInfoList = new ArrayList<>();
    // ?????????????????????
    public final CheckThreadLiveData<TCVideoInfo> tcVideoInfo = new CheckThreadLiveData<>();
    // LiveRepository
    private final LiveRepository liveRepository = new LiveRepository();
    // ????????????????????? - ???????????????
    private MLVBLiveRoom mLvbLiveRoom;
    // ??????Id
    public String mediaId = "";
    //??????id
    private String authorId;
    // ???????????????UserID
    public String userID;
    // ??????????????????
    public ObservableField<Boolean> isShowBackground = new ObservableField<>();
    // ?????????????????????
    private LiveChatAdapter mLiveChatAdapter;
    public boolean hasJoinAnchor = false;
    // ???????????????????????????
    private boolean isMicroConnect = false;

    // ????????????
    private String mPlayUrl = "";
    private TCVideoViewMgr mVideoViewMgr;

    public VideoAudienceViewModel(@NonNull Application application) {
        super(application);
    }

    public void initData(Context context, String mediaId, TXCloudVideoView txCloudVideoView) {
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLvbLiveRoom = MLVBLiveRoom.sharedInstance(context);
        mLvbLiveRoom.setListener(this);
        this.mediaId = mediaId;
        isShowBackground.set(true);
        getLiveMessage(context, txCloudVideoView);

    }

    public void initVideoViewConnect(ViewAnchorPlayerListBinding binding){
        mVideoViewMgr = new TCVideoViewMgr(binding, userId -> {
            DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(AppManager.getAppManager().currentActivity());
            dialog.init(v -> dialog.dismiss(), v2->{
                dialog.dismiss();
                stopLinkMic();
            },"??????","????????????????????????","??????","??????");
            dialog.show();
        });
    }

    public boolean hasInitData() {
        return ! TextUtils.isEmpty(mediaId);
    }

    /**
     * ??????????????????
     */
    private void getLiveMessage(Context context, TXCloudVideoView txCloudVideoView) {
        liveRepository.getLiveMessage(mediaId).subscribe(new GVideoResponseObserver<LiveDetailModel>() {
            @Override
            protected void onSuccess(@NonNull LiveDetailModel model) {

                liveDetailLiveData.setValue(model);
                LiveDetailModel value = liveDetailLiveData.getValue();
                if (value == null) {
                    return;
                }

                LiveDetailModel.CerDTOBean cerDTOBean = model.getCerDTO();
                boolean isFollowValue = false;
                if (cerDTOBean != null) {
                    isFollowValue = value.getCerDTO().getFollower() == 1;
                }
                isFollow.set(isFollowValue);

                LiveDetailModel.DetailVOBean detailVOBean = model.getDetailVO();
                if (detailVOBean == null) {
                    mPlayUrl = "";
                    isMicroConnect = false;
                } else {
                    mPlayUrl = detailVOBean.getLiveUrl();
                    isMicroConnect = detailVOBean.connectVideo == 1;
                }
                //?????????????????????
                TCChatEntity chatEntity = new TCChatEntity();
                chatEntity.setSenderName("");
                chatEntity.setContent(detailVOBean == null ? "" : detailVOBean.getDefaultDesc());
                addCommentData(chatEntity);
                audienceEnterRoom(txCloudVideoView);

                initLiveRoomList(context, model);

            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                errorMsgLiveData.setValue(throwable.getMessage());
                LogUtils.e(throwable.getMessage());
            }
        });

    }

    /**
     * ?????????????????????
     */
    private void initLiveRoomList(Context context, LiveDetailModel liveDetailModel) {
        TCVideoListMgr.getInstance().fetchLiveList(context, new TCVideoListMgr.Listener() {
            @Override
            public void onVideoList(int retCode, ArrayList<TCVideoInfo> result, boolean refresh) {
                if (liveDetailModel == null) {
                    return;
                }
                LiveDetailModel.DetailVOBean detailVOBean = liveDetailModel.getDetailVO();
                if (detailVOBean == null) {
                    return;
                }
                if (result == null || result.isEmpty()) {
                    return;
                }
                for (TCVideoInfo temp : result) {
                    if (temp == null || ! TextUtils.equals(temp.groupId, detailVOBean.getMediaId())) {
                        continue;
                    }
                    tcVideoInfo.setValue(temp);
                    return;
                }
            }
        });
    }

    public LiveChatAdapter getLiveChatAdapter() {
        if (mLiveChatAdapter == null) {
            mLiveChatAdapter = new LiveChatAdapter();
            mLiveChatAdapter.hidePlaceholder();
        }
        return mLiveChatAdapter;
    }

    public boolean isOpenConnect(){
        return isMicroConnect;
    }

    /**
     * ??????
     */
    @SuppressLint("WrongConstant")
    public void onFollowClicked(View v) {
        if (!LiveManager.getInstance().checkOrLogin()) {
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(v.getContext())) {
            return;
        }
        LiveDetailModel liveDetailModel = liveDetailLiveData.getValue();
        if (liveDetailModel == null) {
            return;
        }
        LiveDetailModel.CerDTOBean cerDTOBean = liveDetailModel.getCerDTO();
        String id = cerDTOBean.getId();
        int authorType = cerDTOBean.getAuthorType();
        String name = cerDTOBean.getName();
        boolean followValue = isFollow.get();

        PluginManager.get(AccountPlugin.class).getFollowRepository().followAuthor(id, authorType, name,
                        ! followValue).subscribe(new GVideoResponseObserver<Boolean>() {
            @Override
            protected void onSuccess(@NonNull Boolean result) {
                isFollow.set(result);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                GVideoSensorDataManager.getInstance().followAccount(! followValue, id, name, authorType,
                                throwable.getMessage());
            }
        });
    }

    /**
     * ??????
     */
    public void onHeartClicked() {
        if (! LiveManager.getInstance().checkOrLogin()) {
            return;
        }
        mLvbLiveRoom.setCustomInfo(MLVBCommonDef.CustomFieldOp.INC, "praise", 1, null);
        //???ChatRoom??????????????????
        mLvbLiveRoom.sendRoomCustomMsg(String.valueOf(TCConstants.IMCMD_PRAISE), "", null);
        TCChatEntity chatEntity = new TCChatEntity();
        chatEntity.setSenderName("??????");
        chatEntity.setContent(LiveManager.getInstance().getNickName() + "  ?????????");
        addCommentData(chatEntity);
    }

    /**
     * ?????????
     */
    public void onCommentClicked(View view) {
        Context context = view.getContext();
        if (context == null) {
            return;
        }
        if (! LiveManager.getInstance().checkOrLogin()) {
            return;
        }
        if (! NetworkTipUtils.checkNetworkOrTip(context)) {
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
        addCommentData(entity);
        mLvbLiveRoom.sendRoomTextMsg(comment, new SendRoomTextMsgCallback() {
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

    @SuppressLint("WrongConstant")
    public void shareClicked(View v) {
        LiveDetailModel liveDetailModel = liveDetailLiveData.getValue();
        if (liveDetailModel == null) {
            return;
        }
        LiveDetailModel.DetailVOBean detailVOBean = liveDetailModel.getDetailVO();
        if (detailVOBean == null) {
            return;
        }
        LiveDetailModel.CerDTOBean cerDTOBean = liveDetailModel.getCerDTO();
        if (cerDTOBean == null) {
            return;
        }
        String reportUrl = PluginManager.get(H5Plugin.class).getReportUrl() + "?mid=" + cerDTOBean.getId();
        ShareDataModel shareDataModel = new ShareDataModel.Builder().setUrl(detailVOBean.getShareUrl()).setTitle(
                        detailVOBean.getTitle()).setMediaId(detailVOBean.getMediaId()).setAuthorName(
                        cerDTOBean.getName()).setAuthorType(cerDTOBean.getAuthorType()).setImage(
                        detailVOBean.getThumbUrl()).setFollow(isFollow.get()).setReportUrl(reportUrl).setFavorite(
                        false).build();
        StatFromModel statFromModel = new StatFromModel(cerDTOBean.getId(), "", "", "", "");
        PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), true, true, shareDataModel, statFromModel);
    }

    /**
     * ?????????????????????
     */
    public void audienceEnterRoom(TXCloudVideoView txCloudVideoView) {
        mLvbLiveRoom.setListener(this);
        mLvbLiveRoom.setSelfProfile(LiveManager.getInstance().getNickName(), LiveManager.getInstance().getUserAvatar());
        mLvbLiveRoom.enterRoom(mediaId, authorId, txCloudVideoView, mPlayUrl, new EnterRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                errorMsgLiveData.setValue(errInfo);
            }

            @Override
            public void onSuccess() {
                isShowBackground.set(false);
                mLvbLiveRoom.sendRoomCustomMsg(String.valueOf(TCConstants.IMCMD_ENTER_LIVE), "", null);
            }
        });

    }

    public void onPause(TXCloudVideoView txCloudVideoView) {
        txCloudVideoView.onPause();
    }

    public void onResume(TXCloudVideoView txCloudVideoView) {
        txCloudVideoView.onResume();

    }

    public void onDestroy(TXCloudVideoView txCloudVideoView) {
        txCloudVideoView.onDestroy();
        isShowBackground.set(true);
        onExitRoom();
        if (mLvbLiveRoom != null) {
            mLvbLiveRoom.stopPlay();
            mLvbLiveRoom = null;
        }
    }

    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {

        switch (errCode) {
            case MLVBCommonDef.LiveRoomErrorCode.ERROR_IM_FORCE_OFFLINE:
                // IM ???????????????
                TCUtils.showKickOut(true);
                break;
            case TXLiveConstants.PUSH_WARNING_RECONNECT:
                // ???????????????????????????????????????????????????????????????????????????
                errorMsgLiveData.setValue(GVideoRuntime.getAppContext().getResources().getString(
                                R.string.all_network_not_available_action_tip));
                break;
            default:
                errorMsgLiveData.setValue(errMsg);
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
        stopLinkMic();
        errorMsgLiveData.setValue("???????????????");
    }

    @Override
    public void onAnchorEnter(AnchorInfo anchorInfo) {
        LogUtils.d("onAnchorEnter -->> " + anchorInfo.userName);
        if (anchorInfo == null || anchorInfo.userid == null) {
            return;
        }
        final TCVideoView videoView = mVideoViewMgr.applyVideoView(anchorInfo.userid);
        if (videoView == null) {
            return;
        }
        if (currentAnchorInfoList != null) {
            boolean exist = false;
            for (AnchorInfo item: currentAnchorInfoList) {
                if (anchorInfo.userid.equalsIgnoreCase(item.userid)) {
                    exist = true;
                    break;
                }
            }
            if (exist == false) {
                currentAnchorInfoList.add(anchorInfo);
            }
        }
        videoView.startLoading();
        //????????????????????????
        mLvbLiveRoom.startRemoteView(anchorInfo, videoView.videoView, new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {
                //???????????????stopLoading ????????????????????????button
                videoView.stopLoading(false);
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

    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {
        LogUtils.d("onAnchorExit -->> " + anchorInfo.userName);
        onDoAnchorExit(anchorInfo);
    }


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
        mVideoViewMgr.recycleVideoView(pusherInfo.userid);
    }

    /**
     * ????????????
     */
    public void stopLinkMic(){
        mLvbLiveRoom.stopLocalPreview();
        mLvbLiveRoom.quitJoinAnchor(new IMLVBLiveRoomListener.QuitAnchorCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess() {
                hasJoinAnchor = false;
            }
        });

        if (mVideoViewMgr != null) {
            mVideoViewMgr.recycleVideoView(userID);
            currentAnchorInfoList.clear();
        }
    }

    /**
     * ????????????
     */
    private long requestTime = 0;
    public void requestJoinAnchor(Context activity){
        //??????????????????????????????????????????
        if (System.currentTimeMillis() - requestTime < MLVBLiveRoomImpl.JOIN_ANCHOR_TIME_OUT){
            ToastUtils.showShort("???????????????????????????????????????...");
            return;
        }
        requestTime = System.currentTimeMillis();
        ToastUtils.showShort("???????????????????????????????????????...");
        mLvbLiveRoom.requestJoinAnchor("", 0, new IMLVBLiveRoomListener.RequestJoinAnchorCallback() {
            @Override
            public void onAccept() {
                if (activity == null || ((Activity)activity).isFinishing()){
                    return;
                }
                requestTime = 0;
                startJoinAnchor(activity);
            }

            @Override
            public void onReject(String reason) {
                requestTime = 0;
                ToastUtils.showShort(reason);
            }

            @Override
            public void onTimeOut() {
                ToastUtils.showShort("??????????????????????????????????????????????????????");
            }

            @Override
            public void onError(int errCode, String errInfo) {
                requestTime = 0;
                ToastUtils.showShort(errInfo);
            }
        });
    }

    @Override
    public void onAudienceEnter(AudienceInfo audienceInfo) {
        LogUtils.d("onAudienceEnter:" + audienceInfo.userName);
    }

    @Override
    public void onAudienceExit(AudienceInfo audienceInfo) {
        LogUtils.d("onAudienceExit:" + audienceInfo.userName);
    }

    @Override
    public void onRequestJoinAnchor(AnchorInfo anchorInfo, String reason) {
        LogUtils.d("onRequestJoinAnchor -->> " + anchorInfo.userName);
    }

    @Override
    public void onKickoutJoinAnchor() {
        LogUtils.d("onKickOutJoinAnchor");
        stopLinkMic();
        ToastUtils.showShort("???????????????????????????");
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
        addCommentData(chatEntity);
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
                break;
            case TCConstants.IMCMD_EXIT_LIVE:
                chatEntity.setSenderName(userName + "  ?????????");
                break;
            case TCConstants.IMCMD_PRAISE:
                chatEntity.setSenderName("??????");
                chatEntity.setContent(userName + "  ?????????");
                break;
            case TCConstants.IMCMD_PAILN_TEXT:
                chatEntity.setContent(message);
                chatEntity.setSenderName(userName);
                break;
            case TCConstants.IMCMD_COUNT:
                //???????????????????????????????????????
                break;
            case TCConstants.IMCMD_MICRO_CONNECT_STATUS:
                // ????????????????????????
                isMicroConnect = (Constant.MICRO_CONNECT.ON + "").equals(message.trim());
                break;
            default:
                break;
        }
        addCommentData(chatEntity);
    }

    /**
     * ????????????????????????
     *
     * @param chatEntity ????????????
     */
    private void addCommentData(TCChatEntity chatEntity) {
        mLiveChatAdapter.addData(chatEntity);
        chatMessageLiveData.setValue(mLiveChatAdapter.getItemCount());
    }


    /**
     * ???????????????
     */
    public void onExitRoom() {
        if (mLvbLiveRoom != null) {
            mLvbLiveRoom.stopLocalPreview();
            stopLinkMic();
            mLvbLiveRoom.sendRoomCustomMsg(String.valueOf(TCConstants.IMCMD_EXIT_LIVE), "", null);
            mLvbLiveRoom.exitRoom(new ExitRoomCallback() {
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
    }

    /**
     * ????????????
     */
    public void startJoinAnchor(Context context) {

        TCVideoView videoView = mVideoViewMgr.getFirstRoomView();
        videoView.setUsed(true);
        videoView.userID = userID;
        videoView.kickButton.setVisibility(View.VISIBLE);

        mLvbLiveRoom.startLocalPreview(true, false, videoView.videoView);
        mLvbLiveRoom.setCameraMuteImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_anchor_pause_publish));

        BeautyParams beautyParams = new BeautyParams();
        mLvbLiveRoom.getBeautyManager().setBeautyStyle(beautyParams.mBeautyStyle);
        mLvbLiveRoom.getBeautyManager().setBeautyLevel(beautyParams.mBeautyLevel);
        mLvbLiveRoom.getBeautyManager().setWhitenessLevel(beautyParams.mWhiteLevel);
        mLvbLiveRoom.getBeautyManager().setRuddyLevel(beautyParams.mRuddyLevel);

        mLvbLiveRoom.joinAnchor(new IMLVBLiveRoomListener.JoinAnchorCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                stopLinkMic();
                LogUtils.e("errCode:" + errCode + ",  errInfo:" + errInfo);
                ToastUtils.showShort( "???????????????" + errInfo);
            }

            @Override
            public void onSuccess() {
                ToastUtils.showShort("????????????");
                hasJoinAnchor = true;
            }

        });
    }

}
