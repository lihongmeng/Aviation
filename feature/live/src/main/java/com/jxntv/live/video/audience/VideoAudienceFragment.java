package com.jxntv.live.video.audience;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jxntv.base.BackPressHandler;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.dialog.DefaultEnsureCancelDialog;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.Constants;
import com.jxntv.live.LiveManager;
import com.jxntv.live.R;
import com.jxntv.live.databinding.FragmentVideoAudienceLiveBinding;
import com.jxntv.live.dialog.MessageConfirmDialog;
import com.jxntv.utils.AppManager;

/**
 * @author huangwei
 * date : 2022/1/20
 * desc :
 **/
public class VideoAudienceFragment extends BaseFragment<FragmentVideoAudienceLiveBinding> {

    // AudienceViewModel
    private VideoAudienceViewModel viewModel;
    // 错误信息弹窗
    private MessageConfirmDialog errorMessageDialog;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_audience_live;
    }

    @Override
    protected void initView() {

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ImmersiveUtils.enterImmersiveFullTransparent(activity);
        viewModel = bingViewModel(VideoAudienceViewModel.class);
        mBinding.detail.setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
        mBinding.anchorVideoView.setLogMargin(10, 10, 45, 55);
        mBinding.recyclerChat.setLayoutManager(new LinearLayoutManager(activity));
        mBinding.audienceBackground.setVisibility(View.VISIBLE);
        //设置标题滚动
        mBinding.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mBinding.title.setSingleLine(true);
        mBinding.title.setSelected(true);
        mBinding.title.setFocusable(true);
        mBinding.title.setFocusableInTouchMode(true);
        mBinding.back.setOnClickListener(view -> back());

        viewModel.initVideoViewConnect(mBinding.layoutAnchor);

    }

    private void back(){
        if (viewModel != null) {
            if (viewModel.hasJoinAnchor) {
                DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(AppManager.getAppManager().currentActivity());
                dialog.init(view1 -> dialog.dismiss(), view12 -> {
                    viewModel.stopLinkMic();
                    dialog.dismiss();
                    }, "退出连麦", "是否确认关闭连麦？", "取消", "确定");
                dialog.show();
            } else {
                viewModel.onDestroy(mBinding.anchorVideoView);
                getActivity().finish();
            }
        }

    }
    @Override
    protected void bindViewModels() {

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Bundle arguments = getArguments();
        String mediaId = arguments != null ? arguments.getString(Constants.INTENT_ID) : null;
        mBinding.setViewModel(viewModel);

        //检查IM是否登录
        if (LiveManager.getInstance().checkOrInitSuccess()) {
            viewModel.initData(activity, mediaId, mBinding.anchorVideoView);
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
            viewModel.onDestroy(mBinding.anchorVideoView);
        });

        viewModel.liveDetailLiveData.observe(this, model -> mBinding.setDetailModel(model));

        viewModel.tcVideoInfo.observe(this, tcVideoInfo -> {
            String avatar = tcVideoInfo == null ? "" : tcVideoInfo.avatar;
            String name = tcVideoInfo == null ? "" : tcVideoInfo.nickname;
            if (! TextUtils.isEmpty(avatar)) {
                //                Glide.with(activity).load(avatar).centerCrop().into(mBinding.soundLiveAnchorHeader);
            } else {
                //                Glide.with(activity).load(ContextCompat.getDrawable(activity, R.drawable.ic_default_avatar)).centerCrop().into(
                //                                mBinding.soundLiveAnchorHeader);
            }
        });

        GVideoEventBus.get(Constants.TX_LOGIN_EVENT_BEFORE).observe(this, o -> {
            //用户重新登录后，在登录小直播前，先退出房间
            viewModel.onExitRoom();
            viewModel.userID = PluginManager.get(AccountPlugin.class).getUserId();
        });

        GVideoEventBus.get(Constants.TX_LOGIN_EVENT_AFTER).observe(this, o -> {
            if (!viewModel.hasInitData()) {
                viewModel.initData(activity, mediaId, mBinding.anchorVideoView);
            } else {
                viewModel.audienceEnterRoom(mBinding.anchorVideoView);
            }
        });

        mBinding.microConnect.setOnClickListener((View) -> {
            if (viewModel == null) {
                return;
            }
            dealRequestJoinAnchor();
        });
    }

    @Override
    protected void loadData() {

    }

    /**
     * 向主播端申请加入连麦
     */
    private void dealRequestJoinAnchor() {

        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            PluginManager.get(AccountPlugin.class).startLoginActivity(getContext());
            return;
        }

        if (viewModel.hasJoinAnchor) {
            back();
            return;
        }

        if (!viewModel.isOpenConnect()) {
            showToast(R.string.anchor_already_close_micro_connect);
            return;
        }
        PermissionManager.requestPermissions(AppManager.getAppManager().currentActivity(), new PermissionCallback() {
            @Override
            public void onPermissionGranted(@NonNull Context context) {
                viewModel.requestJoinAnchor(context);
            }

            @Override
            public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                Toast.makeText(context, "没有录音权限", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
        if (viewModel != null)
            viewModel.onResume(mBinding.anchorVideoView);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
        if (viewModel != null)
            viewModel.onPause(mBinding.anchorVideoView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            PluginManager.get(RecordPlugin.class).destroy();
            if (mBinding != null && mBinding.anchorVideoView != null) {
                viewModel.onDestroy(mBinding.anchorVideoView);
            }
        }
    }

    /**
     * 返回监听
     */
    private final BackPressHandler mBackPressHandler = new BackPressHandler() {
        @Override
        public boolean onBackPressed() {
            if (viewModel != null) back();
            return true;
        }
    };

}
