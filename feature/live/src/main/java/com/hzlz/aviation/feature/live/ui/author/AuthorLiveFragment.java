package com.hzlz.aviation.feature.live.ui.author;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_LIVE_TYPE;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_ROOM_TYPE.VIDEO;
import static com.hzlz.aviation.kernel.base.Constant.MICRO_CONNECT.ON;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.live.Constants;
import com.hzlz.aviation.feature.live.bean.AudienceOperation;
import com.hzlz.aviation.feature.live.callback.OnMicroConnectRequestListListener;
import com.hzlz.aviation.feature.live.databinding.FragmentAuthorLiveBinding;
import com.hzlz.aviation.feature.live.dialog.AudienceOperationDialog;
import com.hzlz.aviation.feature.live.dialog.LiveEndMessageDialog;
import com.hzlz.aviation.feature.live.dialog.MessageConfirmDialog;
import com.hzlz.aviation.feature.live.dialog.MicroConnectRequestListDialog;
import com.hzlz.aviation.feature.live.liveroom.live.utils.TCConstants;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.OnlineAnchorPosition;
import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.OnlineAnchorVolume;
import com.hzlz.aviation.feature.live.model.LiveDetailModel;
import com.hzlz.aviation.feature.live.utils.MicroConnectAnchorViewHelper;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.network.exception.GVideoAPIException;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.AviationImageView;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/3/3
 * desc : ??????????????????
 */
public class AuthorLiveFragment extends BaseFragment<FragmentAuthorLiveBinding> {

    // AuthorLiveViewModel
    private AuthorLiveViewModel viewModel;

    // ??????????????????
    private MessageConfirmDialog errorMessageDialog;

    // ????????????
    private MessageConfirmDialog exitDialog;

    // ??????????????????
    private LiveEndMessageDialog liveMessageDialog;

    // ?????????????????????????????????
    private DefaultEnsureCancelDialog startMicroConnectTipDialog;

    // ???????????????????????????
    private MicroConnectRequestListDialog microConnectRequestListDialog;

    // ???????????????????????????
    private DefaultEnsureCancelDialog closeMicroConnectDialog;

    // ???????????????RecyclerView
    private FrameLayout audienceRecyclerView;

    // ??????????????????
    private AudienceOperationDialog clickAudienceDialog;

    // MicroConnectAnchorViewHelper
    private MicroConnectAnchorViewHelper microConnectAnchorViewHelper;

    // ?????????????????????????????????????????????????????????????????????????????????????????????
    private final int TIMING_DURATION = 1000;
    private final Runnable runnable = () -> {
        viewModel.mLvbLiveRoom.sendRoomCustomMsg(
                String.valueOf(TCConstants.IMCMD_CONNECT_ONLINE_ANCHOR_POSITION),
                new Gson().toJson(
                        OnlineAnchorPosition.change(viewModel.getCurrentAnchorInfoList()),
                        new TypeToken<ArrayList<OnlineAnchorPosition>>() {
                        }.getType()),
                null
        );
        mHandler.postDelayed(this.runnable, TIMING_DURATION);
    };

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
        return R.layout.fragment_author_live;
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
        viewModel = bingViewModel(AuthorLiveViewModel.class);
        mBinding.videoView.setLogMargin(10, 10, 45, 55);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.audienceAvatarRecyclerView.setLayoutManager(layoutManager);
        //??????????????????
        mBinding.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mBinding.title.setSingleLine(true);
        mBinding.title.setSelected(true);
        mBinding.title.setFocusable(true);
        mBinding.title.setFocusableInTouchMode(true);
        microConnectRequestListDialog = new MicroConnectRequestListDialog(activity);
    }

    @Override
    protected void bindViewModels() {
        mBinding.setViewModel(viewModel);

        viewModel.userID = PluginManager.get(AccountPlugin.class).getUserId();

        viewModel.showErrorMessageDialog.observe(this, this::showErrorMessageDialog);
        viewModel.updateChatRecyclerView.observe(this, integer -> mBinding.recyclerChat.smoothScrollToPosition(integer));
        viewModel.showExitDialog.observe(this, message -> showExitDialog());
        viewModel.showStartMicroConnectTipDialog.observe(this, index -> showStartMicroConnectTipDialog());
        viewModel.showMicroConnectApplyInviteDialog.observe(this, index -> showMicroConnectApplyInviteDialog());

        viewModel.addMiroConnectLiveData.observe(this, anchorInfo -> {
            microConnectRequestListDialog.addData(anchorInfo);
        });

        viewModel.invalidMiroConnectLiveData.observe(this, id -> {
            microConnectRequestListDialog.setInvalidAnchorInfo(id);
        });

        viewModel.clearMicroConnectDataLiveData.observe(this, f -> {
            microConnectRequestListDialog.clear();
        });
        viewModel.currentAnchorInfoList.observe(
                this,
                currentAnchorInfoList -> {
                    microConnectAnchorViewHelper.updateDataSource(currentAnchorInfoList);
                }
        );
        viewModel.isMicroConnect.observe(
                this,
                value -> {
                    audienceRecyclerView.setVisibility(value ? VISIBLE : View.GONE);
                    if (value) {
                        mHandler.postDelayed(runnable, TIMING_DURATION);
                    }
                }
        );
        viewModel.liveDetailLiveData.observe(
                this,
                model -> {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    LiveDetailModel.DetailVOBean detailVOBean = model.getDetailVO();
                    if (detailVOBean == null) {
                        viewModel.liveType = VIDEO;
                    } else {
                        viewModel.liveType = detailVOBean.type;
                    }

                    initVideoView(viewModel.liveType);
                    initSoundHeaderView(viewModel.liveType);
                    initAudienceRecyclerView(viewModel.liveType);

                    // ?????????RecyclerView????????????RecyclerView?????????Adapter
                    if (detailVOBean == null) {
                        viewModel.isMicroConnect.setValue(false);
                    } else {
                        viewModel.isMicroConnect.setValue(detailVOBean.connectVideo == 1);
                    }

                    viewModel.setData(activity);
                    viewModel.setPublishConfig(mBinding.videoView);
                    initLiveListener();
                }
        );
        viewModel.updateVolume.observe(
                this,
                onlineAnchorVolume -> {
                    // ??????userid????????????????????????????????????????????????
                    AnchorInfo anchorInfo = microConnectAnchorViewHelper.getItemDataWithUseId(onlineAnchorVolume.userid);
                    if (anchorInfo == null) {
                        return;
                    }
                    // ?????????????????????index???????????????View
                    View target = microConnectAnchorViewHelper.getChildAt(anchorInfo.index);
                    AviationImageView microStatus = target.findViewById(R.id.micro_status);

                    if (TextUtils.isEmpty(anchorInfo.userid)) {
                        microStatus.setVisibility(View.GONE);
                    } else {
                        if (anchorInfo.mute) {
                            microStatus.setVisibility(View.VISIBLE);
                            microStatus.setImageResource(R.drawable.icon_micro_mute);
                        } else {
                            if (onlineAnchorVolume.volume > 18) {
                                microStatus.setVisibility(View.VISIBLE);
                                microStatus.setImageResource(R.drawable.icon_micro_talking);
                            } else {
                                microStatus.setVisibility(View.GONE);
                            }
                        }
                    }
                }
        );

        viewModel.startRemoteView.observe(this,
                anchorInfo -> viewModel.startRemoteView(anchorInfo,mBinding.videoView));

    }

    private void initLiveListener() {
        viewModel.mLvbLiveRoom.enableAudioVolumeEvaluation(300);
        viewModel.mLvbLiveRoom.setAudioVolumeEvaluationListener(
                volume -> viewModel.mLvbLiveRoom.sendRoomCustomMsg(
                        String.valueOf(TCConstants.IMCMD_CONNECT_ONLINE_ANCHOR_VOLUME),
                        new Gson().toJson(
                                new OnlineAnchorVolume(viewModel.userID, volume),
                                OnlineAnchorVolume.class
                        ),
                        null
                )
        );

    }

    private void initVideoView(int liveType) {
        mBinding.videoView.setVisibility((liveType == VIDEO) ? VISIBLE : GONE);
    }

    private void initAudienceRecyclerView(int liveType) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        mBinding.audienceBackgroundBg.setVisibility((liveType == VIDEO) ? GONE : VISIBLE);

        audienceRecyclerView = (liveType == VIDEO) ? mBinding.videoAudienceList : mBinding.soundAudienceList;
        microConnectAnchorViewHelper.init(audienceRecyclerView, liveType);

        microConnectAnchorViewHelper.setOperationListener(new MicroConnectAnchorViewHelper.OperationListener() {
            @Override
            public void onAudienceClick(AnchorInfo anchorInfo) {
                if (clickAudienceDialog == null) {
                    clickAudienceDialog = new AudienceOperationDialog(activity);
                }
                List<AudienceOperation> audienceOperationList = new ArrayList<>();
                audienceOperationList.add(new AudienceOperation(getString(R.string.see_he_page), view -> {
                    clickAudienceDialog.dismiss();
                    PluginManager.get(AccountPlugin.class).startPgcActivity(view, new AuthorModel(anchorInfo.userid));
                }));

                if (viewModel.getMuteStatus(anchorInfo)) {
                    audienceOperationList.add(new AudienceOperation(getString(R.string.open_micro), v -> {
                        clickAudienceDialog.dismiss();
                        viewModel.setAnchorMuteStatus(anchorInfo, false);
                        viewModel.mLvbLiveRoom.muteRemoteAudio(anchorInfo.userid, false);
                    }));
                } else {
                    audienceOperationList.add(new AudienceOperation(getString(R.string.close_micro), v -> {
                        clickAudienceDialog.dismiss();
                        viewModel.setAnchorMuteStatus(anchorInfo, true);
                        viewModel.mLvbLiveRoom.muteRemoteAudio(anchorInfo.userid, true);
                    }));
                }

                audienceOperationList.add(new AudienceOperation(getString(R.string.close_micro_connect), v -> {
                    viewModel.mLvbLiveRoom.kickoutJoinAnchor(anchorInfo.userid);
                    clickAudienceDialog.dismiss();
                }));
                clickAudienceDialog.update(anchorInfo.userName, audienceOperationList);
                clickAudienceDialog.show();
            }

            @Override
            public void onEmptyPositionClick(final int position) {

            }
        });

        viewModel.initAudienceList();
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

            String headerUrl = PluginManager.get(AccountPlugin.class).getUserAvatar();
            headerUrl = TextUtils.isEmpty(headerUrl) ? "" : headerUrl;

            String name = PluginManager.get(AccountPlugin.class).getNickName();
            name = TextUtils.isEmpty(name) ? "" : name;

            if (!TextUtils.isEmpty(headerUrl)) {
                Glide.with(activity).load(headerUrl).centerCrop().into(mBinding.soundLiveAnchorHeader);
            } else {
                Glide.with(activity).load(
                                ContextCompat.getDrawable(activity, R.drawable.ic_default_avatar)).centerCrop().into(
                                mBinding.soundLiveAnchorHeader);
            }

            mBinding.soundLiveAnchorName.setText("?????? " + name);
        }
    }

    private void showErrorMessageDialog(String error) {
        if (errorMessageDialog == null) {
            errorMessageDialog = new MessageConfirmDialog(getContext());
            errorMessageDialog.setSingleButton("???????????????", view -> finishActivity());
        }
        viewModel.uploadMessage();
        errorMessageDialog.setMessage(error);
        errorMessageDialog.show();
    }

    private void showExitDialog() {
        if (exitDialog == null) {
            exitDialog = new MessageConfirmDialog(getContext());
            exitDialog.setMessage("????????????", "???????????????????????????");
            exitDialog.setDoubleButton("??????", null, "??????", v -> {
                viewModel.uploadMessage();
                if (liveMessageDialog == null) {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    liveMessageDialog = new LiveEndMessageDialog(activity, view -> finishActivity());
                }
                liveMessageDialog.setData(viewModel.mEnterAudienceCount, viewModel.mHeartCount);
                liveMessageDialog.show();

                mHandler.removeCallbacks(runnable);
            });
        }
        exitDialog.show();
    }

    private void showStartMicroConnectTipDialog() {
        if (startMicroConnectTipDialog == null) {
            startMicroConnectTipDialog = new DefaultEnsureCancelDialog(getContext());
            startMicroConnectTipDialog.init(v -> startMicroConnectTipDialog.dismiss(),
                            v -> viewModel.liveRepository.startConnectMicro(ON, viewModel.mediaId).subscribe(
                                            new BaseResponseObserver<Object>() {
                                                @Override
                                                protected void onRequestData(Object result) {
                                                    viewModel.isMicroConnect.setValue(true);

                                                    initAudienceRecyclerView(viewModel.liveType);

                                                    startMicroConnectTipDialog.dismiss();

                                                    viewModel.mLvbLiveRoom.sendRoomCustomMsg(
                                                                    String.valueOf(TCConstants.IMCMD_MICRO_CONNECT_STATUS),
                                                                    ON + "", null);

                                                    mHandler.postDelayed(runnable, TIMING_DURATION);
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
                                            }), getString(R.string.connect_micro_confirm),
                            getString(R.string.connect_micro_confirm_tip), getString(R.string.cancel),
                            getString(R.string.confirm_start));

        }
        startMicroConnectTipDialog.show();
    }

    private void showMicroConnectApplyInviteDialog() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (microConnectRequestListDialog == null) {
            microConnectRequestListDialog = new MicroConnectRequestListDialog(activity);
        }

        microConnectRequestListDialog.setConnectListener(new OnMicroConnectRequestListListener() {
            @Override
            public void onCloseClick() {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                if (closeMicroConnectDialog == null) {
                    closeMicroConnectDialog = new DefaultEnsureCancelDialog(activity);
                    closeMicroConnectDialog.init(v -> closeMicroConnectDialog.dismiss(), v -> {
                                        viewModel.closeMicroConnect();
                                        microConnectRequestListDialog.dismiss();
                                        mHandler.removeCallbacks(runnable);
                                        closeMicroConnectDialog.dismiss();
                                    }, getString(R.string.close_micro_connect), getString(R.string.close_micro_connect_tip),
                                    getString(R.string.cancel), getString(R.string.close_micro_connect));
                }
                closeMicroConnectDialog.show();
            }

            @Override
            public void onAcceptClick(AnchorInfo anchorInfo, int position) {
                boolean isCanJoin = false;
                if (anchorInfo.index < viewModel.getTempAnchorInfoList().size()) {
                    AnchorInfo currentAnchor = viewModel.getTempAnchorInfoList().get(anchorInfo.index);
                    if (currentAnchor == null || TextUtils.isEmpty(currentAnchor.userid)) {
                        isCanJoin = true;
                    } else {
                        //???????????????????????????????????????????????????
                        for (int i = anchorInfo.index; i < viewModel.getTempAnchorInfoList().size(); i++) {
                            AnchorInfo anchor = viewModel.getTempAnchorInfoList().get(i);
                            if (anchor == null || TextUtils.isEmpty(anchor.userid)) {
                                anchorInfo.index = i;
                                isCanJoin = true;
                                break;
                            }
                        }
                        //???????????????????????????????????????????????????????????????
                        if (!isCanJoin) {
                            for (int i = 0; i < anchorInfo.index; i++) {
                                AnchorInfo anchor = viewModel.getTempAnchorInfoList().get(i);
                                if (anchor == null || TextUtils.isEmpty(anchor.userid)) {
                                    anchorInfo.index = i;
                                    isCanJoin = true;
                                    break;
                                }
                            }
                        }
                    }
                }else {
                    isCanJoin = true;
                }
                if (isCanJoin){
                    int result = viewModel.mLvbLiveRoom.responseJoinAnchor(anchorInfo.userid, anchorInfo.index, true, "");
                    viewModel.addTempAnchor(anchorInfo);
                    if (result == 0) {
                        anchorInfo.connectState = AnchorInfo.CONNECT_AGREE;
                        microConnectRequestListDialog.updateAnchorInfo(anchorInfo);
                        viewModel.applyAnchorUnInviteList.remove(anchorInfo.userid);
                        viewModel.updateInviteCount();
                    }
                }else {
                    ToastUtils.showShort("????????????????????????");
                }
            }
        });
        microConnectRequestListDialog.show();
    }

    @Override
    protected void loadData() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Bundle bundle = getArguments();
        viewModel.title = (bundle == null) ? "" : bundle.getString(Constants.INTENT_LIVE_TITLE);
        viewModel.mediaId = (bundle == null) ? "" : bundle.getString(Constants.INTENT_LIVE_ID);
        viewModel.shareUrl = (bundle == null) ? "" : bundle.getString(Constants.INTENT_LIVE_SHARE);
        mBinding.title.setText(viewModel.title);

        // ??????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(EXTRA_LIVE_TYPE)) {
            viewModel.getLiveMessage();
        } else {
            viewModel.liveType = arguments.getInt(EXTRA_LIVE_TYPE, VIDEO);
            viewModel.setData(activity);
            initLiveListener();
            viewModel.setPublishConfig(mBinding.videoView);

            initVideoView(viewModel.liveType);
            initSoundHeaderView(viewModel.liveType);
            initAudienceRecyclerView(viewModel.liveType);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        if (viewModel != null) viewModel.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
        if (viewModel != null) viewModel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
        if (viewModel != null) viewModel.onResume();
    }

    /**
     * ????????????
     */
    private final BackPressHandler mBackPressHandler = new BackPressHandler() {
        @Override
        public boolean onBackPressed() {
            viewModel.onExitClicked();
            return true;
        }
    };

}