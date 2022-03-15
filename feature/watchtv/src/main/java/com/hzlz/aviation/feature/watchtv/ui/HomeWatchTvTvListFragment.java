package com.hzlz.aviation.feature.watchtv.ui;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CHANNEL_ID;
import static com.hzlz.aviation.kernel.stat.stat.StatPid.WATCH_TV_HOME_LIST;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.hzlz.aviation.feature.watchtv.WatchTvPluginImpl;
import com.hzlz.aviation.feature.watchtv.adapter.HomeWatchTvTvListAdapter;
import com.hzlz.aviation.feature.watchtv.callback.SmartRefreshListener;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.widget.decoration.SpaceItemDecoration;
import com.hzlz.aviation.feature.watchtv.R;
import com.hzlz.aviation.feature.watchtv.databinding.FragmentHomeWatchTvTvListBinding;

/**
 * 首页看电视Fragment，下方ViewPager中的子Fragment
 */
public class HomeWatchTvTvListFragment extends BaseFragment<FragmentHomeWatchTvTvListBinding> {

    // HomeWatchTvTvListViewModel
    private HomeWatchTvTvListViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_watch_tv_tv_list;
    }

    @Override
    protected void initView() {
        mBinding.netError.retry.setOnClickListener(view -> {
            onReload(view);
        });
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void bindViewModels() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        viewModel = bingViewModel(HomeWatchTvTvListViewModel.class);

        viewModel.homeWatchTvTvListAdapter = new HomeWatchTvTvListAdapter(activity);
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 2);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.addItemDecoration(new SpaceItemDecoration(
                SizeUtils.dp2px(5),
                0,
                2)
        );
        mBinding.recyclerView.setAdapter(viewModel.homeWatchTvTvListAdapter);
        viewModel.homeWatchTvTvListAdapter.setItemOnClickListener(
                watchTvChannel -> {
                    if (watchTvChannel == null || watchTvChannel.id == null) {
                        return;
                    }
                    new WatchTvPluginImpl().startWatchTvWholePeriodDetailWithActivity(
                            activity,
                            watchTvChannel.id,
                            getPageName()
                    );
                }
        );

        viewModel.holderLiveData.observe(this, this::updatePlaceholderLayoutType);

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        viewModel.channelId = bundle.getLong(CHANNEL_ID);

    }

    @Override
    protected void loadData() {
        viewModel.refreshData();
    }

    @Override
    public String getPid() {
        return WATCH_TV_HOME_LIST;
    }

    @Override
    protected void updatePlaceholderLayoutType(int type) {
        switch (type) {
            case PlaceholderType.EMPTY:
                mBinding.placeHolder.setVisibility(View.VISIBLE);
                mBinding.empty.root.setVisibility(View.VISIBLE);
                mBinding.netError.root.setVisibility(View.GONE);
                mBinding.loading.root.setVisibility(View.GONE);
                break;
            case PlaceholderType.LOADING:
                mBinding.placeHolder.setVisibility(View.VISIBLE);
                mBinding.loading.root.setVisibility(View.VISIBLE);
                mBinding.empty.root.setVisibility(View.GONE);
                mBinding.netError.root.setVisibility(View.GONE);
                break;
            case PlaceholderType.NETWORK_NOT_AVAILABLE:
                mBinding.placeHolder.setVisibility(View.VISIBLE);
                mBinding.netError.root.setVisibility(View.VISIBLE);
                mBinding.empty.root.setVisibility(View.GONE);
                mBinding.loading.root.setVisibility(View.GONE);
                break;
            case PlaceholderType.NONE:
                mBinding.placeHolder.setVisibility(View.GONE);
                break;
            case PlaceholderType.EMPTY_COMMENT:
            case PlaceholderType.EMPTY_SEARCH:
            case PlaceholderType.ERROR:
            case PlaceholderType.UN_LOGIN:
                break;
        }
    }

    @Override
    public void onReload(@NonNull View view) {
        if (viewModel == null) {
            return;
        }
        viewModel.holderLiveData.observe(this, this::updatePlaceholderLayoutType);
        viewModel.refreshData();
    }

    public void loadMore(SmartRefreshListener smartRefreshListener) {
        if (viewModel == null) {
            return;
        }
        viewModel.smartRefreshListener = smartRefreshListener;
        viewModel.loadMoreData();
    }

}
