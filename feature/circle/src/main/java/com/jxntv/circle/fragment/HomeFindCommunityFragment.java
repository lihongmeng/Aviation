package com.jxntv.circle.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.jxntv.account.presistence.UserManager;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.decoration.GapItemDecoration;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.CircleJoinStatus;
import com.jxntv.base.model.circle.CircleTag;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.view.recyclerview.RecyclerViewVideoOnScrollListener;
import com.jxntv.circle.CirclePluginImpl;
import com.jxntv.circle.R;
import com.jxntv.circle.adapter.FindCircleItemAdapter;
import com.jxntv.circle.databinding.FragmentFindCircleItemBinding;
import com.jxntv.circle.viewmodel.HomeFindCommunityViewModel;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.utils.SizeUtils;

import java.util.List;

/**
 * 首页发现社区页
 */
public final class HomeFindCommunityFragment extends BaseFragment<FragmentFindCircleItemBinding> {

    // ViewModel
    private HomeFindCommunityViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find_circle_item;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(HomeFindCommunityViewModel.class);
        mBinding.setViewModel(viewModel);

        viewModel.findCircleItemAdapter.setListener(listener);

        Bundle bundle = getArguments();
        if (bundle != null) {
            viewModel.circleTag = bundle.getParcelable(CircleTag.TAG);
        }

        initListener();

        viewModel.refreshData.observe(
                this, o -> viewModel.onRefresh(mBinding.refreshLayout)
        );

        GapItemDecoration gapItemDecoration=new GapItemDecoration();
        gapItemDecoration.setVerticalGap(SizeUtils.dp2px(7));
        gapItemDecoration.setFistRowTopGap(SizeUtils.dp2px(7));
        mBinding.recyclerView.addItemDecoration(gapItemDecoration);

        mBinding.recyclerView.addOnScrollListener(new RecyclerViewVideoOnScrollListener(mBinding.recyclerView,
                new RecyclerViewVideoOnScrollListener.onScrolledPositionListener() {
                    @Override
                    public void onScrolled(int fistVisible, int lastVisible) {
                        GVideoEventBus.get(Constant.EVENT_MSG.COLLAPSE_AUDIO_FLOAT_VIEW).post(null);
                    }

                    @Override
                    public void onScrollStateChanged(int fistVisible, int lastVisible) {
                        mFistVisible = fistVisible;
                        mLastVisible = lastVisible;
                    }

                    @Override
                    public void onItemEnter(int position) {
                        postExposure(position, position, true);
                    }

                    @Override
                    public void onItemExit(int position) {
                        postExposure(position, position, false);
                    }
                }));
    }

    private void initListener() {

        // 登录
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
                this, o -> viewModel.onRefresh(mBinding.refreshLayout)
        );

        // 刷新页面
        GVideoEventBus.get(CirclePluginImpl.UPDATE_FIND_CIRCLE_LIST)
                .observe(this, o -> {
                    // if (viewModel == null || viewModel.getCurrentPageNumber() == 1) {
                    //     return;
                    // }
                    // viewModel.onRefresh(mBinding.refreshLayout);
                });

        // 双击Tab回到顶部并刷新
        GVideoEventBus.get(Constant.EVENT_MSG.BACK_TOP).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (isUIVisible()) {
                    mBinding.recyclerView.scrollToPosition(0);
                    mBinding.refreshLayout.autoRefresh();
                }
            }
        });

        // 社区关注状态发生变化
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class)
                .observe(this, circleJoinStatus -> {
                    if (circleJoinStatus == null) {
                        return;
                    }
                    List<Circle> dataSource = viewModel.findCircleItemAdapter.getDataSource();
                    if (dataSource == null || dataSource.isEmpty()) {
                        return;
                    }
                    int dataPosition = -1;
                    for (int index = 0; index < dataSource.size(); index++) {
                        Circle circle = dataSource.get(index);
                        if (circle == null || circle.groupId != circleJoinStatus.groupId) {
                            continue;
                        }
                        dataPosition = index;
                        circle.setJoin(circleJoinStatus.isJoin);
                        break;
                    }
                    viewModel.findCircleItemAdapter.notifyItemChanged(dataPosition);
                });

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getPid() {
        return StatPid.CIRCLE_FIND;
    }

    @Override
    protected void loadData() {

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus();

    }

    @Override
    public void onReload(@NonNull View view) {
        if (viewModel != null) {
            viewModel.onRefresh(mBinding.refreshLayout);
        }
    }

    private final FindCircleItemAdapter.Listener listener = (view, circle) -> {
        Context context = getContext();
        if (context == null) {
            return;
        }
        if (circle == null) {
            showToast(getString(R.string.join_failed));
            return;
        }
        if (!UserManager.hasLoggedIn()) {
            AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
            if (plugin != null) {
                plugin.startLoginActivity(view.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(getPageName()),
                        ResourcesUtils.getString(com.jxntv.account.R.string.follow)
                );
            }
            return;
        }
        if (!circle.isJoin()) {
            viewModel.joinCircle(getContext(), circle, view);
        } else {
            new CirclePluginImpl().startCircleDetailWithActivity(context, circle, null);
        }

    };


    //------- 埋点相关  --------
    private int mFistVisible = -1, mLastVisible = -1;

    /**
     * 内容曝光处处理
     *
     * @param start  起始位置
     * @param end    结束位置
     * @param isShow 是否显示
     */
    private void postExposure(int start, int end, boolean isShow) {
        if (start == -1 && end == -1 && viewModel != null && viewModel.findCircleItemAdapter != null) {
            start = 0;
            end = viewModel.findCircleItemAdapter.getLastPosition();
        }
        if (start < 0 || end < 0 || end < start) {
            return;
        }
        for (int i = start; i <= end; i++) {
            if (isShow) {
                viewModel.findCircleItemAdapter.enterPosition(i, getPid());
            } else {
                viewModel.findCircleItemAdapter.exitPosition(i, getPid());
            }
        }
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        initListener();
        postExposure(mFistVisible, mLastVisible, true);
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        postExposure(mFistVisible, mLastVisible, false);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        initListener();
        postExposure(mFistVisible, mLastVisible, true);
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        postExposure(mFistVisible, mLastVisible, false);

    }

}
