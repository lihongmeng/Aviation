package com.hzlz.aviation.feature.video.ui.vlive;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.model.LiveDetailModel;
import com.hzlz.aviation.feature.video.model.anotation.AtyLiveStatus;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.anotation.PlayerType;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.AtyLiveFragmentBinding;
import com.ruffian.library.widget.helper.RBaseHelper;
import com.ruffian.library.widget.helper.RTextViewHelper;

/**
 * @author huangwei
 * date : 2021/2/8
 * desc : 活动直播
 **/
public class AtyLiveFragment extends BaseFragment<AtyLiveFragmentBinding> {

    private AtyLiveViewModel viewModel;

    private int color;
    private int dp2;
    private int dp3;
    private int dp4;
    private int dp5;
    private int dp10;
    private int dp12;
    private int dp18;
    private int dp20;

    private boolean isInitCommentFlag;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_live_fragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBinding.detail.setPadding(0, 0, 0, 0);
        } else {
            mBinding.detail.setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
        }
    }


    @Override
    protected void initView() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        isInitCommentFlag = true;
        Resources resources = getResources();
        color = resources.getColor(R.color.color_3a3a4d_25);
        dp2 = resources.getDimensionPixelOffset(R.dimen.DIMEN_2DP);
        dp3 = resources.getDimensionPixelOffset(R.dimen.DIMEN_3DP);
        dp4 = resources.getDimensionPixelOffset(R.dimen.DIMEN_4DP);
        dp5 = resources.getDimensionPixelOffset(R.dimen.DIMEN_5DP);
        dp10 = resources.getDimensionPixelOffset(R.dimen.DIMEN_10DP);
        dp12 = resources.getDimensionPixelOffset(R.dimen.DIMEN_12DP);
        dp18 = resources.getDimensionPixelOffset(R.dimen.DIMEN_18DP);
        dp20 = resources.getDimensionPixelOffset(R.dimen.DIMEN_20DP);

        // 状态栏沉浸，全屏展示
        ImmersiveUtils.enterImmersiveFullTransparent(activity);

        mBinding.detail.setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
        //设置标题滚动
        mBinding.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mBinding.title.setSingleLine(true);
        mBinding.title.setSelected(true);
        mBinding.title.setFocusable(true);
        mBinding.title.setFocusableInTouchMode(true);

        viewModel = bingViewModel(AtyLiveViewModel.class);
        mBinding.setViewModel(viewModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerComment.setLayoutManager(linearLayoutManager);
        mBinding.recyclerComment.setAdapter(viewModel.adapter);

    }

    @Override
    protected void bindViewModels() {
        Bundle bundle = getArguments();
        VideoModel videoModel = bundle != null ? bundle.getParcelable(Constants.EXTRA_VIDEO_MODEL) : null;
        String mFromPid = bundle != null ? bundle.getString(Constant.EXTRA_FROM_PID) : "";
        String mFromChannelId = bundle != null ? bundle.getString(Constant.EXTRA_FROM_CHANNEL_ID) : "";

        String mediaId;
        if (bundle == null) {
            mediaId = "";
        } else {
            mediaId = bundle.getString(Constants.EXTRA_MEDIA_ID);
            if (TextUtils.isEmpty(mediaId)) {
                mediaId = (videoModel == null) ? "" : videoModel.getId();
            }
        }

        StatFromModel mStatFromModel = new StatFromModel(
                mediaId,
                getPid(),
                "",
                mFromPid,
                mFromChannelId
        );

        viewModel.initViewModel(mediaId, mStatFromModel);

        viewModel.liveDetailModelLiveData.observe(this, o -> {
            if (viewModel.liveDetailModel == null) {
                return;
            }
            mBinding.setLiveDetailModel(viewModel.liveDetailModel);
            setPlayStyle(viewModel.liveDetailModel);
        });

        viewModel.mCommentListLiveData.observe(this, commentModel -> {
            viewModel.adapter.addData(commentModel.getCommentLst());
            if (isInitCommentFlag) {
                isInitCommentFlag = false;
                mBinding.recyclerComment.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.recyclerComment.smoothScrollToPosition(viewModel.adapter.getItemCount() - 1);
                    }
                }, 1000);
            } else {
                mBinding.recyclerComment.scrollToPosition(viewModel.adapter.getItemCount() - 1);
            }
        });

    }

    @Override
    protected void loadData() {

    }

    @Override
    public String getPid() {
        return StatPid.ACTIVITY_LIVE;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
        if (viewModel != null) {
            viewModel.onResume();
        }
    }

    @Override
    public void onPause() {
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
        super.onPause();
        if (viewModel != null) {
            viewModel.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.onDestroy();
        }
    }

    /**
     * 设置竖屏直播样式
     */
    private GVideoView gVideoView;

    private void setPlayStyle(LiveDetailModel liveDetailModel) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        LiveDetailModel.BroadcastDTOBean broadcastDTOBean = liveDetailModel.getBroadcastDTO();
        int status = broadcastDTOBean.getStatus();
        RBaseHelper titleLayoutHelper = mBinding.cTitleLayout.getHelper();
        RBaseHelper platformLayoutHelper = mBinding.cPlatformLayout.getHelper();
        RTextViewHelper liveTagHelper = mBinding.liveTag.getHelper();
        if (gVideoView == null) {
            gVideoView = new GVideoView(activity, PlayerType.GVIDEO);
            if (broadcastDTOBean.isVerticalPlayStyle()) {
                //竖屏模式
                mBinding.rootView.addView(gVideoView, 1, mBinding.videoFull.getLayoutParams());
                int paddingBottom = 0;
                if (status != AtyLiveStatus.LIVING) {
                    paddingBottom = dp20;
                }
                mBinding.recyclerComment.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.DIMEN_80DP), 0, paddingBottom);
                mBinding.detail.setBackgroundColor(ContextCompat.getColor(activity, R.color.color_000000_0));
                titleLayoutHelper.setBackgroundColorNormal(color);
                titleLayoutHelper.setCornerRadius(dp12);
                mBinding.cTitleLayout.setPadding(dp5, dp4, dp5, dp4);
                liveTagHelper.setCornerRadius(dp18);
                platformLayoutHelper.setBackgroundColorNormal(color);
                platformLayoutHelper.setCornerRadius(dp10);
                mBinding.cPlatformLayout.setPadding(dp3, dp2, dp3, dp2);
                mBinding.platformName.setTextColor(ContextCompat.getColor(activity, R.color.color_ffffff));
            } else {
                mBinding.detail.addView(gVideoView, mBinding.videoNormal.getLayoutParams());
            }
            viewModel.setGVideoView(gVideoView, liveDetailModel);
        } else {
            viewModel.resetGVideoView(liveDetailModel);
        }
        if (status == AtyLiveStatus.OFF_SHELF) {
            ToastUtils.showShort("直播已结束 感谢您的关注");
            activity.finish();
        }
    }

    /**
     * 返回监听
     */
    private final BackPressHandler mBackPressHandler = () -> viewModel.onBackPress();

}
