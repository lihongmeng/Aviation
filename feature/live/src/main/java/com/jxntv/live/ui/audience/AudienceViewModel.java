package com.jxntv.live.ui.audience;

import static com.jxntv.base.Constant.LIVE_ROOM_TYPE.VIDEO;
import static com.jxntv.live.liveroom.live.utils.TCConstants.IMCMD_CONNECT_ONLINE_ANCHOR_POSITION;
import static com.jxntv.live.liveroom.live.utils.TCConstants.IMCMD_CONNECT_ONLINE_ANCHOR_VOLUME;
import static com.jxntv.live.liveroom.live.utils.TCConstants.IMCMD_MICRO_CONNECT_STATUS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.Constant;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.LiveManager;
import com.jxntv.live.R;
import com.jxntv.live.adapter.LiveChatAdapter;
import com.jxntv.live.callback.OnCommentAddListener;
import com.jxntv.live.dialog.CommentInputBlackDialog;
import com.jxntv.live.dialog.CommentInputWhiteDialog;
import com.jxntv.live.liveroom.IMLVBLiveRoomListener;
import com.jxntv.live.liveroom.MLVBLiveRoom;
import com.jxntv.live.liveroom.live.entity.TCChatEntity;
import com.jxntv.live.liveroom.live.entity.TCVideoInfo;
import com.jxntv.live.liveroom.live.entity.TCVideoListMgr;
import com.jxntv.live.liveroom.live.utils.TCConstants;
import com.jxntv.live.liveroom.live.utils.TCUtils;
import com.jxntv.live.liveroom.roomutil.commondef.AnchorInfo;
import com.jxntv.live.liveroom.roomutil.commondef.AudienceInfo;
import com.jxntv.live.liveroom.roomutil.commondef.MLVBCommonDef;
import com.jxntv.live.liveroom.roomutil.commondef.OnlineAnchorPosition;
import com.jxntv.live.liveroom.roomutil.commondef.OnlineAnchorVolume;
import com.jxntv.live.model.LiveDetailModel;
import com.jxntv.live.repository.LiveRepository;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.utils.LogUtils;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.List;


/**
 * @author huangwei
 * date : 2021/3/3
 * desc :
 **/
public class AudienceViewModel extends BaseViewModel implements IMLVBLiveRoomListener {

    private final String TAB = "AudienceViewModel";

    // 错误信息
    public final CheckThreadLiveData<String> errorMsgLiveData = new CheckThreadLiveData<>();

    // 聊天信息
    public final CheckThreadLiveData<Integer> chatMessageLiveData = new CheckThreadLiveData<>();

    // 直播间详情信息
    public final CheckThreadLiveData<LiveDetailModel> liveDetailLiveData = new CheckThreadLiveData<>();

    // 当前是否是连麦状态
    public final CheckThreadLiveData<Integer> isMicroConnect = new CheckThreadLiveData<>(-1);

    // LiveRepository
    private final LiveRepository liveRepository = new LiveRepository();

    // 腾讯云移动直播 - 连麦直播间
    public MLVBLiveRoom mLvbLiveRoom;

    // 评论列表适配器
    private LiveChatAdapter mLiveChatAdapter;

    // 媒体Id
    public String mediaId = "";

    // 分享地址
    private String mPlayUrl = "";

    // 当前用户的UserID
    public String userID;

    // 是否关注
    public final ObservableBoolean isFollow = new ObservableBoolean();

    // 是否显示背景
    public ObservableField<Boolean> isShowBackground = new ObservableField<>();

    // 直播类型
    public final CheckThreadLiveData<Integer> liveType = new CheckThreadLiveData<>(-1);

    // 是否填写过申请理由
    public boolean hasFillReason = false;

    // 当前已连麦的数据
    public final ArrayList<AnchorInfo> currentAnchorInfoList = new ArrayList<>();

    // 通知观测者更新列表的位置
    // 如果Value小于零，代表更新整个列表
    // 如果Value大于零，代表更新指定位置
    public final CheckThreadLiveData<Integer> updateIndex = new CheckThreadLiveData<>();

    // 直播间相关信息
    public final CheckThreadLiveData<TCVideoInfo> tcVideoInfo = new CheckThreadLiveData<>();

    // 音量信息发生变化
    public final CheckThreadLiveData<OnlineAnchorVolume> onlineAnchorVolumeLiveData = new CheckThreadLiveData<>();

    public final CheckThreadLiveData<Boolean> agreeJoinAnchor = new CheckThreadLiveData<>();

    // 需要非常频繁转换数据，故保存一个变量
    public final Gson gson = new Gson();

    public boolean mute = false;

    public AudienceViewModel(@NonNull Application application) {
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

    public boolean hasInitData() {
        return !TextUtils.isEmpty(mediaId);
    }

    /**
     * 获取直播消息
     */
    private void getLiveMessage(Context context, TXCloudVideoView txCloudVideoView) {
        liveRepository.getLiveMessage(mediaId)
                .subscribe(new GVideoResponseObserver<LiveDetailModel>() {
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
                            liveType.setValue(VIDEO);
                            mPlayUrl = "";
                            isMicroConnect.setValue(0);
                        } else {
                            liveType.setValue(detailVOBean.type);
                            mPlayUrl = detailVOBean.getLiveUrl();
                            isMicroConnect.setValue(detailVOBean.connectVideo == 1 ? 1 : 0);
                        }

                        //直播间公告通知
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
                    if (temp == null
                            || !TextUtils.equals(temp.groupId, detailVOBean.getMediaId())) {
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


    /**
     * 关注
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

        PluginManager.get(AccountPlugin.class)
                .getFollowRepository()
                .followAuthor(
                        id,
                        authorType,
                        name,
                        !followValue
                )
                .subscribe(new GVideoResponseObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@NonNull Boolean result) {
                        isFollow.set(result);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        GVideoSensorDataManager.getInstance().followAccount(
                                !followValue,
                                id,
                                name,
                                authorType,
                                throwable.getMessage()
                        );
                    }
                });
    }

    /**
     * 点赞
     */
    public void onHeartClicked() {
        if (!LiveManager.getInstance().checkOrLogin()) {
            return;
        }
        mLvbLiveRoom.setCustomInfo(MLVBCommonDef.CustomFieldOp.INC, "praise", 1, null);
        //向ChatRoom发送点赞消息
        mLvbLiveRoom.sendRoomCustomMsg(String.valueOf(TCConstants.IMCMD_PRAISE), "", null);
        TCChatEntity chatEntity = new TCChatEntity();
        chatEntity.setSenderName("通知");
        chatEntity.setContent(LiveManager.getInstance().getNickName() + "  点了赞");
        addCommentData(chatEntity);
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
        Integer liveTypeValue = liveType.getValue();
        if (liveTypeValue == null) {
            liveTypeValue = VIDEO;
        }
        if (liveTypeValue == VIDEO) {
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
        addCommentData(entity);
        mLvbLiveRoom.sendRoomTextMsg(
                comment,
                new SendRoomTextMsgCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LogUtils.e("sendRoomTextMsg error:" + errInfo);
                        showToast(R.string.send_message_fail);
                    }

                    @Override
                    public void onSuccess() {

                    }
                }
        );
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
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setUrl(detailVOBean.getShareUrl())
                .setTitle(detailVOBean.getTitle())
                .setMediaId(detailVOBean.getMediaId())
                .setAuthorName(cerDTOBean.getName())
                .setAuthorType(cerDTOBean.getAuthorType())
                .setImage(detailVOBean.getThumbUrl())
                .setFollow(isFollow.get())
                .setReportUrl(reportUrl)
                .setFavorite(false)
                .build();
        StatFromModel statFromModel = new StatFromModel(cerDTOBean.getId(), "", "", "", "");
        PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), true, true, shareDataModel, statFromModel);
    }

    /**
     * 观众进入直播间
     */
    public void audienceEnterRoom(TXCloudVideoView txCloudVideoView) {
        mLvbLiveRoom.setListener(this);
        mLvbLiveRoom.setSelfProfile(LiveManager.getInstance().getNickName(), LiveManager.getInstance().getUserAvatar());
        mLvbLiveRoom.enterRoom(mediaId, txCloudVideoView, mPlayUrl, new EnterRoomCallback() {
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
    }

    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {

        switch (errCode) {
            case MLVBCommonDef.LiveRoomErrorCode.ERROR_IM_FORCE_OFFLINE:
                // IM 被强制下线
                TCUtils.showKickOut(true);
                break;
            case TXLiveConstants.PUSH_WARNING_RECONNECT:
                // 网络断连，且连续三次无法重新连接，需要自行重启推流
                errorMsgLiveData.setValue(GVideoRuntime.getAppContext().getResources().getString(R.string.all_network_not_available_action_tip));
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
        errorMsgLiveData.setValue("直播已结束");
    }

    @Override
    public void onAnchorEnter(AnchorInfo anchorInfo) {
        LogUtils.d("onAnchorEnter -->> " + anchorInfo.userName);
    }

    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {
        LogUtils.d("onAnchorExit -->> " + anchorInfo.userName);
        //关闭远端视频渲染
        mLvbLiveRoom.stopRemoteView(anchorInfo);
        if (currentAnchorInfoList != null) {
            boolean exist = false;
            for (AnchorInfo item: currentAnchorInfoList) {
                if (item.userid.equalsIgnoreCase(anchorInfo.userid)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                currentAnchorInfoList.add(anchorInfo);
            }
        }
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
        mLvbLiveRoom.quitJoinAnchor(null);
        showToast(R.string.anchor_already_close_micro_connect);
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
    public void onReceiveRoomCustomMsg(String roomID, String userid, String userName, String userAvatar,
                                       String cmd, String message) {
        int type = Integer.parseInt(cmd);
        if (TextUtils.isEmpty(userName)) {
            userName = userid;
        }
        TCChatEntity chatEntity = new TCChatEntity();
        chatEntity.setType(type);
        switch (type) {
            case TCConstants.IMCMD_ENTER_LIVE:
                chatEntity.setSenderName(userName + "  来了");
                break;
            case TCConstants.IMCMD_EXIT_LIVE:
                chatEntity.setSenderName(userName + "  离开了");
                break;
            case TCConstants.IMCMD_PRAISE:
                chatEntity.setSenderName("通知");
                chatEntity.setContent(userName + "  点了赞");
                break;
            case TCConstants.IMCMD_PAILN_TEXT:
                chatEntity.setContent(message);
                chatEntity.setSenderName(userName);
                break;
            case TCConstants.IMCMD_COUNT:
                //获取当前正在观看直播的人数
                break;
            case IMCMD_MICRO_CONNECT_STATUS:
                // 主播是否开启连麦
                isMicroConnect.setValue((Constant.MICRO_CONNECT.ON + "").equals(message) ? 1 : 0);
                break;
            case IMCMD_CONNECT_ONLINE_ANCHOR_POSITION:
                // 连麦列表更新
                dealListUpdate(gson, message);
                break;
            case IMCMD_CONNECT_ONLINE_ANCHOR_VOLUME:
                dealVolumeUpdate(gson, message);
                break;
            default:
                break;
        }
        addCommentData(chatEntity);
    }

    private void dealVolumeUpdate(Gson gson, String message) {
        OnlineAnchorVolume onlineAnchorVolume = gson.fromJson(message, OnlineAnchorVolume.class);
        if (onlineAnchorVolume == null) {
            return;
        }
        onlineAnchorVolumeLiveData.setValue(onlineAnchorVolume);
    }

    private void dealListUpdate(Gson gson, String message) {
        ArrayList<OnlineAnchorPosition> onlineAnchorPositionArrayList = gson.fromJson(message,
                new TypeToken<ArrayList<OnlineAnchorPosition>>() {
                }.getType());
        if (onlineAnchorPositionArrayList == null || onlineAnchorPositionArrayList.isEmpty()) {
            initAudienceList();
        } else {

            // boolean isAnyUseful = false;
            //
            // // 判断是否存在需要更新的数据
            // // 若无，直接return
            // // 若有，更新数据源，并通知UI更新
            // for (OnlineAnchorPosition onlineAnchorPosition : onlineAnchorPositionArrayList) {
            //     AnchorInfo anchorInfo = currentAnchorInfoList.get(onlineAnchorPosition.index);
            //     if (anchorInfo.isUseful(onlineAnchorPosition)) {
            //         isAnyUseful = true;
            //     }
            // }
            //
            // if (!isAnyUseful) {
            //     return;
            // }

            List<Integer> positionList = new ArrayList<>();
            // 将接收到的信息，更新到本地数据源
            for (OnlineAnchorPosition onlineAnchorPosition : onlineAnchorPositionArrayList) {
                currentAnchorInfoList.set(
                        onlineAnchorPosition.index,
                        new AnchorInfo(onlineAnchorPosition)
                );
                positionList.add(onlineAnchorPosition.index);
            }

            for (int index = 0; index < 6; index++) {
                if (!positionList.contains(index)) {
                    currentAnchorInfoList.get(index).init();
                }
            }

            updateIndex.setValue(-1);
        }


    }

    /**
     * 评论列表添加数据
     *
     * @param chatEntity 评论内容
     */
    private void addCommentData(TCChatEntity chatEntity) {
        mLiveChatAdapter.addData(chatEntity);
        chatMessageLiveData.setValue(mLiveChatAdapter.getItemCount());
    }


    /**
     * 退出直播间
     */
    public void onExitRoom() {
        if (mLvbLiveRoom != null) {
            mLvbLiveRoom.quitJoinAnchor(null);
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

    public void initAudienceList() {
        if (currentAnchorInfoList.isEmpty()) {
            for (int index = 0; index < 6; index++) {
                currentAnchorInfoList.add(new AnchorInfo(index));
            }
        } else {
            for (AnchorInfo anchorInfo : currentAnchorInfoList) {
                anchorInfo.init();
            }
        }
        updateIndex.setValue(-1);
    }


    public void startJoinAnchor(Context context, TXCloudVideoView mTXCloudVideoView) {
        mLvbLiveRoom.startLocalPreview(false, true, mTXCloudVideoView);
        mLvbLiveRoom.joinAnchor(new JoinAnchorCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.e("errCode:" + errCode + ",  errInfo:" + errInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.e("startJoinAnchor onSuccess");
                agreeJoinAnchor.setValue(true);
            }
        });
    }

    /**
     * @return
     */
    public boolean isAlreadyOnline() {
        if (currentAnchorInfoList.isEmpty()) {
            return false;
        }
        for (AnchorInfo anchorInfo : currentAnchorInfoList) {
            if (anchorInfo == null || !TextUtils.equals(anchorInfo.userid, userID)) {
                continue;
            }
            return true;
        }
        return false;
    }

}
