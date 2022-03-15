package com.hzlz.aviation.feature.watchtv.ui.select;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COLUMN_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.NEED_SEND_VIDEO_DATA_ONCE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.PROGRAM_ID;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.CircleJoinStatus;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.recycler.BaseRecyclerFragment;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.databinding.MediaRecyclerLayoutBinding;
import com.hzlz.aviation.feature.watchtv.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

public class SelectCollectionFragment extends BaseRecyclerFragment<VideoModel, MediaRecyclerLayoutBinding> {

    // SelectCollectionViewModel
    private SelectCollectionViewModel viewModel;

    public SelectCollectionAdapter selectCollectionAdapter;

    @Override
    protected boolean enableRefresh() {
        return true;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @NonNull
    @Override
    protected BaseRecyclerAdapter<VideoModel, ?> createAdapter() {
        selectCollectionAdapter = new SelectCollectionAdapter(requireContext());
//        selectCollectionAdapter.mActionLiveData.observe(
//                this,
//                recommendModelActionModel -> {
//                    if (recommendModelActionModel.type == ACTION_ITEM) {
//                        Bundle bundle = getArguments();
//                        String fromPid = bundle != null ? bundle.getString(Constant.EXTRA_FROM_PID) : null;
//                        viewModel.navigateToVideoFragment(
//                                mRecyclerView,
//                                recommendModelActionModel.model,
//                                fromPid
//                        );
//                    }
//                });
        return selectCollectionAdapter;
    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler_view;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.empty_container;
    }

    @Override protected int initRefreshViewId() {
        return R.id.refresh_layout;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.media_recycler_layout;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(SelectCollectionViewModel.class);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        viewModel.columnId = bundle.getString(COLUMN_ID);
        viewModel.programId =  bundle.getString(PROGRAM_ID);
        viewModel.needSendFirstVideoDataOnce = bundle.getBoolean(NEED_SEND_VIDEO_DATA_ONCE, false);

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN, String.class).observe(
                this,
                o -> {
                    viewModel.needSendGroupInfo = true;
                    viewModel.needSendGroupInfoVideoId = selectCollectionAdapter.getCurrentPlayVideoId();
                    onRefresh(mBinding.refreshLayout);
                }
        );

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT, String.class).observe(
                this,
                o -> {
                    viewModel.needSendGroupInfo = true;
                    viewModel.needSendGroupInfoVideoId = selectCollectionAdapter.getCurrentPlayVideoId();
                    onRefresh(mBinding.refreshLayout);
                }
        );

        //关注状态变化
        //可能从粉丝列表新关注了用户，需要刷新关注列表
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class)
                .observe(this, circleJoinStatus -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.needSendGroupInfo = true;
                    viewModel.needSendGroupInfoVideoId = selectCollectionAdapter.getCurrentPlayVideoId();
                    onRefresh(mBinding.refreshLayout);
                });

    }

    @Override
    protected void loadData() {
        onReload(mBinding.refreshLayout);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        viewModel.loadMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        viewModel.refreshData();
    }

    @Override
    public void onReload(@NonNull View view) {
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
        onRefresh(mBinding.refreshLayout);
    }

}
