package com.hzlz.aviation.feature.live.ui.audience;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_ROOM_TYPE.VIDEO;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hzlz.aviation.feature.live.Constants;
import com.hzlz.aviation.feature.live.LiveManager;
import com.hzlz.aviation.feature.live.bean.AudienceOperation;
import com.hzlz.aviation.feature.live.callback.OnMicroConnectRequestListener;
import com.hzlz.aviation.feature.live.databinding.FragmentAudienceBinding;
import com.hzlz.aviation.feature.live.dialog.AudienceOperationDialog;
import com.hzlz.aviation.feature.live.dialog.MessageConfirmDialog;
import com.hzlz.aviation.feature.live.dialog.MicroConnectRequestDialog;
import com.hzlz.aviation.feature.live.liveroom.IMLVBLiveRoomListener;
import com.hzlz.aviation.feature.live.liveroom.live.utils.TCConstants;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.OnlineAnchorVolume;
import com.hzlz.aviation.feature.live.model.LiveDetailModel;
import com.hzlz.aviation.feature.live.utils.MicroConnectAnchorViewHelper;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AppManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/3/3
 * desc : 互动直播观众端
 **/
public class AudienceFragment extends BaseFragment<FragmentAudienceBinding> {

    // AudienceViewModel
    private AudienceViewModel viewModel;

    // 错误信息弹窗
    private MessageConfirmDialog errorMessageDialog;

    // 观众列表的RecyclerView
    private FrameLayout audienceRecyclerView;

    // 申请连麦的窗口
    private MicroConnectRequestDialog microConnectRequestDialog;

    // 点击观众头像
    private AudienceOperationDialog clickAudienceDialog;

    // MicroConnectAnchorViewHelper
    private MicroConnectAnchorViewHelper microConnectAnchorViewHelper;

    private long recordTime;

    @Override
    protected boolean enableImmersive() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_audience;
    }

    @Override
    protected void initView() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ImmersiveUtils.enterImmersiveFullTransparent(activity);
        microConnectAnchorViewHelper = MicroConnectAnchorViewHelper.getInstance();
        mBinding.detail.setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
        mBinding.videoView.setLogMargin(10, 10, 45, 55);
        mBinding.recyclerChat.setLayoutManager(new LinearLayoutManager(activity));
        mBinding.audienceBackground.setVisibility(View.VISIBLE);
        //设置标题滚动
        mBinding.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mBinding.title.setSingleLine(true);
        mBinding.title.setSelected(true);
        mBinding.title.setFocusable(true);
        mBinding.title.setFocusableInTouchMode(true);
        mBinding.back.setOnClickListener(view -> {
            if (viewModel != null) viewModel.onDestroy(mBinding.videoView);
            activity.finish();
        });

        mBinding.microConnect.setOnClickListener((View) -> {
            if (viewModel == null) {
                return;
            }

            if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()){
                PluginManager.get(AccountPlugin.class).startLoginActivity(getContext());
                return;
            }

            Integer isMicroConnect = viewModel.isMicroConnect.getValue();
            if (isMicroConnect != null && isMicroConnect <= 0) {
                showToast(R.string.micro_connect_has_not_start);
                return;
            }

            int position = -1;
            for (int index = 0; index < viewModel.currentAnchorInfoList.size(); index++) {
                AnchorInfo anchorInfo = viewModel.currentAnchorInfoList.get(index);
                if (!TextUtils.isEmpty(anchorInfo.userid)) {
                    continue;
                }
                position = index;
                break;
            }

            if (position < 0) {
                showToast("当前连麦人数已达上限");
                return;
            }

            dealRequestJoinAnchor(activity, position);
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindViewModels() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Bundle arguments = getArguments();
        String mediaId = arguments != null ? arguments.getString(Constants.INTENT_ID) : null;

        viewModel = bingViewModel(AudienceViewModel.class);
        mBinding.setViewModel(viewModel);

        //检查IM是否登录
        if (LiveManager.getInstance().checkOrInitSuccess()) {
            viewModel.initData(activity, mediaId, mBinding.videoView);
        }

        viewModel.userID = PluginManager.get(AccountPlugin.class).getUserId();
        if (TextUtils.isEmpty(viewModel.userID)) {
            viewModel.userID = "";
        }

        viewModel.chatMessageLiveData.observe(this, integer -> mBinding.recyclerChat.smoothScrollToPosition(integer));

        viewModel.errorMsgLiveData.observe(this, error -> {
            if (errorMessageDialog == null) {
                errorMessageDialog = new MessageConfirmDialog(activity);
                errorMessageDialog.setSingleButton("退出直播间", view -> finishActivity());
            }
            errorMessageDialog.setMessage(error);
            errorMessageDialog.show();
            viewModel.onDestroy(mBinding.videoView);
        });

        viewModel.liveDetailLiveData.observe(this, model -> {
            mBinding.setDetailModel(model);

            LiveDetailModel.DetailVOBean detailVOBean = model == null ? null : model.getDetailVO();
            String title = detailVOBean == null ? "" : detailVOBean.getTitle();
            mBinding.title.setText(title);

        });

        viewModel.tcVideoInfo.observe(this, tcVideoInfo -> {
            String avatar = tcVideoInfo == null ? "" : tcVideoInfo.avatar;
            String name = tcVideoInfo == null ? "" : tcVideoInfo.nickname;
            if (! TextUtils.isEmpty(avatar)) {
                Glide.with(activity).load(avatar).centerCrop().into(mBinding.soundLiveAnchorHeader);
            } else {
                Glide.with(activity).load(
                                ContextCompat.getDrawable(activity, R.drawable.ic_default_avatar)).centerCrop().into(
                                mBinding.soundLiveAnchorHeader);
            }
            mBinding.soundLiveAnchorName.setText("主播 " + name);
        });

        viewModel.updateIndex.observe(this, updateIndex -> {
            if (updateIndex < 0) {
                microConnectAnchorViewHelper.updateDataSource(viewModel.currentAnchorInfoList);
            } else {
                microConnectAnchorViewHelper.updateDataSourceWithPosition(
                                viewModel.currentAnchorInfoList.get(updateIndex));
            }
        });

        viewModel.isMicroConnect.observe(this, value -> {
            if (audienceRecyclerView == null || value < 0) {
                return;
            }
            if (value == 1) {
                audienceRecyclerView.setVisibility(VISIBLE);
                microConnectAnchorViewHelper.updateDataSource(viewModel.currentAnchorInfoList);
            } else {
                viewModel.initAudienceList();
                audienceRecyclerView.setVisibility(GONE);
            }
            initSoundRecord();
        });

        viewModel.onlineAnchorVolumeLiveData.observe(this, onlineAnchorVolume -> {
            //                    LogUtils.d(
            //                            "来自推送的音量"
            //                                    + "\n"
            //                                    + "User id -->>"
            //                                    + onlineAnchorVolume.userid
            //                                    + "\n"
            //                                    + "Volume -->>"
            //                                    + onlineAnchorVolume.volume
            //                    );
            dealVolumeChange(onlineAnchorVolume.userid, onlineAnchorVolume.volume);
        });

        // 主播创建直播时，可以将参数直接传进来
        // 观众端进入直播间时，需要通过接口查询相关信息
        // 故二者对视图的初始化时机不同
        viewModel.liveType.observe(this, liveType -> {
            if (liveType < 0) {
                return;
            }
            mBinding.audienceBackgroundBg.setVisibility((liveType == VIDEO) ? GONE : VISIBLE);
            initVideoView(liveType);
            initSoundHeaderView(liveType);
            initAudienceRecyclerView(liveType);
            initSoundRecord();
        });


        // GVideoEventBus.get(Constants.EVENT_LOGIN, String.class).observe(this,
        //         s -> PluginManager.get(AccountPlugin.class).startLoginActivity(activity));

        GVideoEventBus.get(Constants.TX_LOGIN_EVENT_BEFORE).observe(this, o -> {
            //用户重新登录后，在登录小直播前，先退出房间
            viewModel.onExitRoom();
            viewModel.userID = PluginManager.get(AccountPlugin.class).getUserId();
        });

        GVideoEventBus.get(Constants.TX_LOGIN_EVENT_AFTER).observe(this, o -> {
            if (!viewModel.hasInitData()) {
                viewModel.initData(activity, mediaId, mBinding.videoView);
            } else {
                viewModel.audienceEnterRoom(mBinding.videoView);
            }
        });

        viewModel.agreeJoinAnchor.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ToastUtils.showShort("连麦成功");
                if (microConnectRequestDialog!=null){
                    microConnectRequestDialog.dismiss();
                }
                viewModel.mLvbLiveRoom.enableAudioVolumeEvaluation(300);
                viewModel.mLvbLiveRoom.setAudioVolumeEvaluationListener(volume -> sendVolumeData((int) volume));
            }
        });
    }

    /**
     * 小主播端会周期收到音量数据
     * 小主播在需要结合大主播传递数据中的mute（通过userId去ViewModel中查询）、本地ViewModel的mute、以及音量数据更新UI
     * 其中大主播传递数据中的mute具有最高优先级，其次是本地的mute
     *
     * @param userId 用户Id
     * @param volume 音量
     */
    private void dealVolumeChange(String userId, int volume) {

        // 通过userid查询列表数据源中对应的那一项数据
        AnchorInfo anchorInfo = microConnectAnchorViewHelper.getItemDataWithUseId(userId);

        if (anchorInfo == null) {
            return;
        }
        // 再通过那一项的index获取对应的View
        View target = microConnectAnchorViewHelper.getChildAt(anchorInfo.index);
        GVideoImageView microStatus = target.findViewById(R.id.micro_status);

        if (TextUtils.isEmpty(anchorInfo.userid)) {
            microStatus.setVisibility(View.GONE);
        } else {
            if (anchorInfo.mute) {
                microStatus.setVisibility(View.VISIBLE);
                microStatus.setImageResource(R.drawable.icon_micro_mute);
            } else {
                if (viewModel.mute) {
                    microStatus.setVisibility(View.VISIBLE);
                    microStatus.setImageResource(R.drawable.icon_micro_mute);
                } else {
                    if (volume > 18) {
                        microStatus.setVisibility(View.VISIBLE);
                        microStatus.setImageResource(R.drawable.icon_micro_talking);
                    } else {
                        microStatus.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void initSoundRecord() {
        Integer liveType = viewModel.liveType.getValue();
        if (liveType == null || liveType < 0) {
            return;
        }
        Integer isMicroConnect = viewModel.isMicroConnect.getValue();
        if (isMicroConnect == null || isMicroConnect <= 0) {
            return;
        }
        // if (liveType == VIDEO) {
        //     recordTime = System.currentTimeMillis();
        //     PluginManager.get(RecordPlugin.class).startRecord(new AudioRecordListener() {
        //         @Override
        //         public void volumeSize(double volume) {
        //             long currentTime = System.currentTimeMillis();
        //             if (currentTime - recordTime < 300) {
        //                 return;
        //             }
        //             mBinding.back.post(() -> {
        //                 recordTime = System.currentTimeMillis();
        //                 sendVolumeData((int) volume);
        //             });
        //         }
        //
        //         @Override
        //         public void result(String filePath, String contentTxt) {
        //
        //         }
        //
        //         @Override
        //         public void error(String errorMessage) {
        //
        //         }
        //
        //         @Override
        //         public void onStop() {
        //
        //         }
        //
        //         @Override
        //         public void onLoadingStart(String loadingMessage) {
        //
        //         }
        //
        //         @Override
        //         public void onLoadingEnd() {
        //
        //         }
        //     });
        // } else {
        //     viewModel.mLvbLiveRoom.setAudioVolumeEvaluationListener(
        //             volume -> sendVolumeData((int) volume)
        //     );
        // }
    }

    private void sendVolumeData(int volume) {
        LogUtils.d("麦克风监测的音量 Volume -->>" + volume);
        dealVolumeChange(viewModel.userID, volume);
        viewModel.mLvbLiveRoom.sendRoomCustomMsg(String.valueOf(TCConstants.IMCMD_CONNECT_ONLINE_ANCHOR_VOLUME),
                        new Gson().toJson(new OnlineAnchorVolume(viewModel.userID, volume), OnlineAnchorVolume.class),
                        null);
    }

    private void initVideoView(int liveType) {
        mBinding.videoView.setVisibility((liveType == VIDEO) ? VISIBLE : GONE);
    }

    @SuppressLint("SetTextI18n")
    private void initSoundHeaderView(int liveType) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (liveType == VIDEO) {
            mBinding.headerNameLayout.setVisibility(GONE);
        } else {
            mBinding.headerNameLayout.setVisibility(VISIBLE);
        }

        String headerUrl = "";
        String name = "";
        LiveDetailModel liveDetailModel = viewModel.liveDetailLiveData.getValue();
        if (liveDetailModel != null) {
            LiveDetailModel.CerDTOBean cerDTOBean = liveDetailModel.getCerDTO();
            if (cerDTOBean != null) {
                headerUrl = cerDTOBean.getAvatar();
                name = cerDTOBean.getName();
            }
        }

        if (!TextUtils.isEmpty(headerUrl)) {
            Glide.with(activity).load(headerUrl).centerCrop().into(mBinding.thumb);
        } else {
            Glide.with(activity).load(
                            ContextCompat.getDrawable(activity, R.drawable.ic_default_avatar)).centerCrop().into(
                            mBinding.thumb);
        }

        mBinding.platformName.setText(name);
    }

    private void initAudienceRecyclerView(int liveType) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        audienceRecyclerView = (liveType == VIDEO) ? mBinding.videoAudienceList : mBinding.soundAudienceList;
        microConnectAnchorViewHelper.init(audienceRecyclerView, liveType);

        viewModel.initAudienceList();

        initAudienceListClick(activity);

        Integer isMicroConnectValue = viewModel.isMicroConnect.getValue();
        audienceRecyclerView.setVisibility((isMicroConnectValue == null || isMicroConnectValue <= 0) ? GONE : VISIBLE);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
        if (viewModel != null) viewModel.onResume(mBinding.videoView);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
        if (viewModel != null) viewModel.onPause(mBinding.videoView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel!=null){
            viewModel.mLvbLiveRoom.stopLocalPreview();
            PluginManager.get(RecordPlugin.class).destroy();
            if (mBinding!=null && mBinding.videoView!=null) {
                viewModel.onDestroy(mBinding.videoView);
            }
        }
        // 未知原因finish后,10s左右才会调用onDestroy,暂时不在此处处理
        // if (viewModel != null) viewModel.onDestroy(mBinding.videoView);
    }

    /**
     * 返回监听
     */
    private final BackPressHandler mBackPressHandler = new BackPressHandler() {
        @Override
        public boolean onBackPressed() {
            if (viewModel != null) viewModel.onDestroy(mBinding.videoView);
            return false;
        }
    };

    private void initAudienceListClick(Activity activity) {
        microConnectAnchorViewHelper.setOperationListener(new MicroConnectAnchorViewHelper.OperationListener() {
            @Override
            public void onAudienceClick(AnchorInfo anchorInfo) {
                if (activity == null) {
                    return;
                }
                if (clickAudienceDialog == null) {
                    clickAudienceDialog = new AudienceOperationDialog(activity);
                }
                List<AudienceOperation> audienceOperationList = new ArrayList<>();
                if (PluginManager.get(AccountPlugin.class).isMe(anchorInfo.userid)) {
                    if (! anchorInfo.mute) {
                        if (! viewModel.mute) {
                            audienceOperationList.add(new AudienceOperation(getString(R.string.close_micro), v -> {
                                viewModel.mute = true;
                                clickAudienceDialog.dismiss();
                                viewModel.mLvbLiveRoom.muteLocalAudio(true);
                            }));
                        } else {
                            audienceOperationList.add(new AudienceOperation(getString(R.string.open_micro), v -> {
                                viewModel.mute = false;
                                clickAudienceDialog.dismiss();
                                viewModel.mLvbLiveRoom.muteLocalAudio(false);
                            }));
                        }

                    }
                    audienceOperationList.add(new AudienceOperation(getString(R.string.close_micro_connect), v -> {
                        viewModel.mLvbLiveRoom.quitJoinAnchor(null);
                        clickAudienceDialog.dismiss();
                    }));
                } else {
                    audienceOperationList.add(new AudienceOperation(getString(R.string.see_he_page), view -> {
                        clickAudienceDialog.dismiss();
                        PluginManager.get(AccountPlugin.class).startPgcActivity(view,
                                        new AuthorModel(anchorInfo.userid));
                    }));
                }
                clickAudienceDialog.update(anchorInfo.userName, audienceOperationList);
                clickAudienceDialog.show();
            }

            @Override
            public void onEmptyPositionClick(final int position) {
                dealRequestJoinAnchor(activity, position);
            }
        });
    }

    /**
     * 向主播端申请加入连麦
     *
     * @param activity Activity
     * @param position 申请连麦的位置
     */
    private void dealRequestJoinAnchor(Activity activity, int position) {

        if (viewModel.isAlreadyOnline()) {
            showToast(R.string.you_already_connect_micro);
            return;
        }

        PermissionManager.requestPermissions(
                AppManager.getAppManager().currentActivity(),
                new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        // mLvbLiveRoom内部会对RequestJoinAnchorCallback进行置空操作
                        // 所以需要使用匿名RequestJoinAnchorCallback对象
                        viewModel.mLvbLiveRoom.requestJoinAnchor(
                                "",
                                position,
                                new IMLVBLiveRoomListener.RequestJoinAnchorCallback() {
                                    @Override
                                    public void onAccept() {
                                        Activity activity = getActivity();
                                        if (activity == null) {
                                            return;
                                        }
                                        viewModel.startJoinAnchor(activity, mBinding.videoView);
                                    }

                                    @Override
                                    public void onReject(String reason) {

                                    }

                                    @Override
                                    public void onTimeOut() {

                                    }

                                    @Override
                                    public void onError(int errCode, String errInfo) {

                                    }
                                });

                if (microConnectRequestDialog == null) {
                    microConnectRequestDialog = new MicroConnectRequestDialog(activity);
                }
                microConnectRequestDialog.setUp(viewModel.hasFillReason, new OnMicroConnectRequestListener() {
                    @Override
                    public void onCancelRequestClick() {

                        viewModel.hasFillReason = false;

                        // 点击空位就发送申请，并携带位置
                        viewModel.mLvbLiveRoom.sendRoomCustomMsg(
                                        String.valueOf(TCConstants.IMCMD_AUDIENCE_CANCEL_REQUEST),
                                        viewModel.userID + "", null);

                        microConnectRequestDialog.dismiss();
                    }

                    @Override
                    public void onSubmitClick(String content) {
                        viewModel.hasFillReason = true;
                        // mLvbLiveRoom内部会对RequestJoinAnchorCallback进行置空操作
                        // 所以需要使用匿名RequestJoinAnchorCallback对象
                        viewModel.mLvbLiveRoom.requestJoinAnchor(content, position,
                                        new IMLVBLiveRoomListener.RequestJoinAnchorCallback() {
                                            @Override
                                            public void onAccept() {
                                                Activity activity = getActivity();
                                                if (activity == null) {
                                                    return;
                                                }
                                                viewModel.startJoinAnchor(activity, mBinding.videoView);
                                            }

                                            @Override
                                            public void onReject(String reason) {

                                            }

                                            @Override
                                            public void onTimeOut() {

                                            }

                                            @Override
                                            public void onError(int errCode, String errInfo) {

                                            }
                                        });
                        microConnectRequestDialog.dismiss();
                    }
                });
                microConnectRequestDialog.show();
            }

            @Override
            public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                Toast.makeText(context, "没有录音权限", Toast.LENGTH_SHORT).show();
            }
        },RECORD_AUDIO);
    }


}
