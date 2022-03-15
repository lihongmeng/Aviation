package com.hzlz.aviation.feature.live.video.author;

import static com.hzlz.aviation.kernel.base.Constant.EXTRA_LIVE_TYPE;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.feature.live.Constants;
import com.hzlz.aviation.feature.live.databinding.FragmentVideoAuthorLiveBinding;
import com.hzlz.aviation.feature.live.dialog.AudienceOperationDialog;
import com.hzlz.aviation.feature.live.dialog.LiveEndMessageDialog;
import com.hzlz.aviation.feature.live.dialog.MessageConfirmDialog;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.feature.live.R;

/**
 * @author huangwei
 * date : 2022/1/20
 * desc : 视频直播主播端
 *
 *
 **/
public class VideoAuthorLiveFragment extends BaseFragment<FragmentVideoAuthorLiveBinding> {

    private VideoAuthorLiveViewModel viewModel;
    // 错误信息弹窗
    private MessageConfirmDialog errorMessageDialog;
    // 退出弹窗
    private MessageConfirmDialog exitDialog;
    // 直播信息弹窗
    private LiveEndMessageDialog liveMessageDialog;
    // 点击观众头像
    private AudienceOperationDialog clickAudienceDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_author_live;
    }

    @Override
    protected void initView() {

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        viewModel = bingViewModel(VideoAuthorLiveViewModel.class);
        ImmersiveUtils.enterImmersiveFullTransparent(activity);
        mBinding.detail.setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
        mBinding.anchorVideoView.setLogMargin(10, 10, 45, 55);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.audienceAvatarRecyclerView.setLayoutManager(layoutManager);
        //设置标题滚动
        mBinding.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mBinding.title.setSingleLine(true);
        mBinding.title.setSelected(true);
        mBinding.title.setFocusable(true);
        mBinding.title.setFocusableInTouchMode(true);

        mBinding.setViewModel(viewModel);
        viewModel.setData(activity,mBinding.layoutAnchor);
    }

    @Override
    protected void bindViewModels() {

        viewModel.showErrorMessageDialog.observe(this, this::showErrorMessageDialog);
        viewModel.updateChatRecyclerView.observe(this, integer -> mBinding.recyclerChat.smoothScrollToPosition(integer));
        viewModel.showExitDialog.observe(this, message -> showExitDialog());
        viewModel.liveDetailLiveData.observe(this, model -> {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            mBinding.setDetailModel(model);
            viewModel.setPublishConfig(mBinding.anchorVideoView);
        });

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
//        mBinding.title.setText(viewModel.title);
        // 主播创建直播时，可以将参数直接传进来
        // 观众端进入直播间时，需要通过接口查询相关信息
        // 故二者对视图的初始化时机不同
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(EXTRA_LIVE_TYPE)) {
            viewModel.getLiveMessage();
        } else {
            viewModel.setPublishConfig(mBinding.anchorVideoView);
        }
    }

    private void showErrorMessageDialog(String error) {
        if (errorMessageDialog == null) {
            errorMessageDialog = new MessageConfirmDialog(getContext());
            errorMessageDialog.setSingleButton("退出直播间", view -> finishActivity());
        }
        viewModel.uploadMessage();
        errorMessageDialog.setMessage(error);
        errorMessageDialog.show();
    }

    private void showExitDialog() {
        if (exitDialog == null) {
            exitDialog = new MessageConfirmDialog(getContext());
            exitDialog.setMessage("结束直播", "是否确认结束直播？");
            exitDialog.setDoubleButton("取消", null, "确认", v -> {
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
//                mHandler.removeCallbacks(runnable);
            });
        }
        exitDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
     * 返回监听
     */
    private final BackPressHandler mBackPressHandler = new BackPressHandler() {
        @Override
        public boolean onBackPressed() {
            viewModel.onExitClicked();
            return true;
        }
    };

}
