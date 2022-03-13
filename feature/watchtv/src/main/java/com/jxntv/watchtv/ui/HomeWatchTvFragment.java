package com.jxntv.watchtv.ui;

import static android.view.View.GONE;
import static com.jxntv.base.Constant.BUNDLE_KEY.CHANNEL_ID;
import static com.jxntv.stat.StatPid.WATCH_TV_HOME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.adapter.BaseFragmentVpAdapter;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.utils.BannerUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;
import com.jxntv.watchtv.R;
import com.jxntv.watchtv.WatchTvPluginImpl;
import com.jxntv.watchtv.adapter.HomeWatchTvHotTvAdapter;
import com.jxntv.watchtv.adapter.HomeWatchTvTvChannelAdapter;
import com.jxntv.watchtv.adapter.HomeWatchTvTvChannelListTabAdapter;
import com.jxntv.watchtv.callback.SmartRefreshListener;
import com.jxntv.watchtv.databinding.FragmentHomeWatchTvBinding;
import com.jxntv.base.model.video.WatchTvChannel;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页的看电视功能父级Fragment
 */
public class HomeWatchTvFragment extends BaseFragment<FragmentHomeWatchTvBinding> {

    // HomeWatchTvViewModel
    private HomeWatchTvViewModel viewModel;

    // 频道节目页面数组
    private List<BaseFragment> baseFragmentList;

    // 当前的Fragment
    private HomeWatchTvTvListFragment currentFragment;

    // 看电视下方频道文字标题Tab列表的LayoutManager
    // 因为下方ViewPager需要和文字标题Tab列表保持联动
    // 需要用此类获取文字标题Tab列表可见Item的位置
    private LinearLayoutManager channelListTabLinearManager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_watch_tv;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        setupPlaceholderLayout(R.id.empty_container);
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindViewModels() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        viewModel = bingViewModel(HomeWatchTvViewModel.class);
        mBinding.setViewModel(viewModel);

        viewModel.updateChannelList.observe(
                this,
                o -> {
                    if (viewModel.watchTvChannelList == null || viewModel.watchTvChannelList.isEmpty()) {
                        mBinding.tvChannelTitle.setVisibility(GONE);
                        mBinding.tvChannel.setVisibility(GONE);
                    } else {
                        mBinding.tvChannelTitle.setVisibility(View.VISIBLE);
                        mBinding.tvChannel.setVisibility(View.VISIBLE);
                        viewModel.tvChannelAdapter.updateDataSource(viewModel.watchTvChannelList);
                    }
                }
        );

        viewModel.updateChannelTabList.observe(
                this,
                o -> {
                    if (viewModel.watchTvChannelTabList == null || viewModel.watchTvChannelTabList.isEmpty()) {
                        mBinding.tvListTab.setVisibility(GONE);
                    } else {
                        mBinding.tvListTab.setVisibility(View.VISIBLE);
                        viewModel.tvChannelListTabAdapter.updateDataSource(viewModel.watchTvChannelTabList);

                        baseFragmentList.clear();
                        currentFragment = null;

                        if (viewModel.watchTvChannelTabList != null && !viewModel.watchTvChannelTabList.isEmpty()) {
                            for (WatchTvChannel watchTvChannel : viewModel.watchTvChannelTabList) {
                                if (watchTvChannel == null) {
                                    continue;
                                }
                                HomeWatchTvTvListFragment homeWatchTvTvListFragment = new HomeWatchTvTvListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putLong(CHANNEL_ID, watchTvChannel.id);
                                homeWatchTvTvListFragment.setArguments(bundle);
                                baseFragmentList.add(homeWatchTvTvListFragment);
                            }
                            currentFragment = (HomeWatchTvTvListFragment) baseFragmentList.get(0);
                        }
                        viewModel.channelTvAdapter.updateSource(baseFragmentList);
                    }
                }
        );

        viewModel.currentLoadMode.observe(
                this,
                o -> {
                    if (currentFragment == null) {
                        return;
                    }
                    currentFragment.loadMore(new SmartRefreshListener() {
                        @Override
                        public void loadMoreSuccess() {
                            mBinding.refreshLayout.finishGVideoLoadMore();
                        }

                        @Override
                        public void loadMoreFailed() {
                            mBinding.refreshLayout.finishGVideoLoadMore();
                        }
                    });
                }
        );

        viewModel.updateHotTvList.observe(
                this,
                o -> {
                    if (viewModel.hotTvList == null || viewModel.hotTvList.isEmpty()) {
                        mBinding.hotTvLayout.setVisibility(GONE);
                        return;
                    }
                    mBinding.hotTvLayout.setVisibility(View.VISIBLE);
                    viewModel.tvHotTvAdapter.updateDataSource(viewModel.hotTvList);
                }
        );

        viewModel.bannerList.observe(
                this,
                model -> {
                    if (!BannerModel.isDataValid(model)) {
                        mBinding.bannerLayout.setVisibility(GONE);
                        return;
                    }
                    mBinding.bannerLayout.setVisibility(View.VISIBLE);
                    BannerUtils.initDefaultBanner(
                            model,
                            mBinding.banner,
                            getPid()
                    );
                });

        GVideoEventBus.get(Constant.EVENT_MSG.BACK_TOP).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (isUIVisible()) {
                    mBinding.refreshLayout.autoRefresh();
                }
            }
        });

        viewModel.tvChannelAdapter = new HomeWatchTvTvChannelAdapter(activity);
        LinearLayoutManager tvChannelLinearManager = new LinearLayoutManager(activity);
        tvChannelLinearManager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.tvChannel.setLayoutManager(tvChannelLinearManager);
        mBinding.tvChannel.setAdapter(viewModel.tvChannelAdapter);
        viewModel.tvChannelAdapter.setItemOnClickListener(
                watchTvChannel -> {
                    if (watchTvChannel == null || watchTvChannel.id == null) {
                        return;
                    }
                    new WatchTvPluginImpl().startWatchTvChannelDetailWithActivity(
                            activity,
                            new VideoModel(String.valueOf(watchTvChannel.id),watchTvChannel.name),
                            null
                    );
                }
        );

        viewModel.tvHotTvAdapter = new HomeWatchTvHotTvAdapter(activity);
        LinearLayoutManager hotTvLinearManager = new LinearLayoutManager(activity);
        hotTvLinearManager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.hotTv.setLayoutManager(hotTvLinearManager);
        mBinding.hotTv.setAdapter(viewModel.tvHotTvAdapter);
        viewModel.tvHotTvAdapter.setItemOnClickListener(watchTvChannel -> {
            if (watchTvChannel == null || watchTvChannel.id == null) {
                return;
            }
            new WatchTvPluginImpl().startWatchTvWholePeriodDetailWithActivity(
                    activity,
                    watchTvChannel.columnId,
                    getPageName()
            );
        });

        viewModel.tvChannelListTabAdapter = new HomeWatchTvTvChannelListTabAdapter(activity);
        viewModel.tvChannelListTabAdapter.setOnItemClickListener(position -> {
            mBinding.tvChannelTab.setCurrentItem(position);
            currentFragment = (HomeWatchTvTvListFragment) baseFragmentList.get(position);
        });
        channelListTabLinearManager = new LinearLayoutManager(activity);
        channelListTabLinearManager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.tvListTab.setLayoutManager(channelListTabLinearManager);
        mBinding.tvListTab.setAdapter(viewModel.tvChannelListTabAdapter);

        baseFragmentList = new ArrayList<>();
        viewModel.channelTvAdapter = new BaseFragmentVpAdapter(getChildFragmentManager());
        mBinding.tvChannelTab.setAdapter(viewModel.channelTvAdapter);
        mBinding.tvChannelTab.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewModel.tvChannelListTabAdapter.updatePosition(position);
                smoothRecyclerViewToVisible(position);
                currentFragment = (HomeWatchTvTvListFragment) baseFragmentList.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void smoothRecyclerViewToVisible(int position) {
        int firstVisibleIndex = channelListTabLinearManager.findFirstVisibleItemPosition();
        int lastVisibleIndex = channelListTabLinearManager.findLastVisibleItemPosition();
        if (position < firstVisibleIndex + 1 || position > lastVisibleIndex - 1) {
            mBinding.tvListTab.smoothScrollToPosition(position);
        }
    }

    @Override
    protected void loadData() {
        viewModel.onRefresh(mBinding.refreshLayout);
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getPid() {
        return WATCH_TV_HOME;
    }

}
