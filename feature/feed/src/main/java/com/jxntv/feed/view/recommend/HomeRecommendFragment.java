package com.jxntv.feed.view.recommend;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.jxntv.base.Constant;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.CircleJoinStatus;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.utils.BannerUtils;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.feed.FeedFragmentManager;
import com.jxntv.feed.R;
import com.jxntv.feed.adapter.HotCircleAdapter;
import com.jxntv.feed.databinding.FragmentHomeRecommendBinding;
import com.jxntv.feed.databinding.LayoutHomeRecommendHeaderBinding;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.media.recycler.MediaRecyclerAdapter;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public final class HomeRecommendFragment extends MediaPageFragment<FragmentHomeRecommendBinding> {

    private HomeRecommendViewModel viewModel;
    private LayoutHomeRecommendHeaderBinding headerBinding;

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_recommend;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.empty_container;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getChannelId() {
        return "";
    }

    @Override
    public String getPid() {
        return StatPid.HOME_RECOMMEND;
    }

    @Override
    protected void initView() {
        super.initView();
        init(getGvFragmentId(), false);

        headerBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_home_recommend_header, null, false);
        ((MediaRecyclerAdapter) mAdapter).addHeaderView(headerBinding.getRoot());

        FeedFragmentManager.getInstance().addFragmentRef(this);
        headerBinding.hotCircleLayout.setPid(getPid());
        headerBinding.hotCircleLayout.setListener(
                new HotCircleAdapter.Listener() {
                    @Override
                    public void clickJoinEnter(View view, Circle circle) {
                        viewModel.dealJoinEnterClick(view, circle);
                    }

                    @Override
                    public void clickCircleItem(View view, Circle circle) {
                        PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(
                                getContext(), circle, null
                        );
                    }
                },
                (circle, findCircleContent) -> {
                    Activity activity = getActivity();
                    if (activity == null || findCircleContent == null) {
                        return;
                    }
                    switch (findCircleContent.type) {
                        case Constant.FindCircleContentType.TOPIC:
                            PluginManager.get(CirclePlugin.class).startTopicDetailWithActivity(
                                    activity,
                                    circle,
                                    findCircleContent.topic
                            );
                            break;
                        case Constant.FindCircleContentType.LIVE:
                        default:
                            MediaModel media = new MediaModel(null);
                            media.setId(findCircleContent.mediaId + "");
                            media.setMediaType(findCircleContent.mediaType);
                            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(activity, media, null);
                    }
                }
        );

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void bindViewModels() {
        viewModel = super.bingViewModel(HomeRecommendViewModel.class);
        viewModel.updateTabId(getTabId());

        mBinding.setViewModel(viewModel);

        // 整体刷新
        viewModel.mAutoRefreshLiveData.observe(this, mAutoRefreshLiveData -> {
            viewModel.refreshDataReal();
        });

        // 横向圈子列表相关监听
        headerBinding.hotCircleLayout.setListener(() -> viewModel.updateRecommendContentList(false));

        // 横向圈子列表数据变化，但是需要结合当前页数进行判断如何使用横向圈子列表数据
        viewModel.hotCircleListPage.observe(this, hotCircleListPage -> {
            if (hotCircleListPage == null
                    || (viewModel.hasNext && hotCircleListPage == viewModel.circleDefaultPage + 1)
                    || (!viewModel.hasNext && hotCircleListPage == viewModel.circleDefaultPage)) {
                headerBinding.hotCircleLayout.replaceDataSource(viewModel.hotCircleList);
            } else {
                headerBinding.hotCircleLayout.updateDataSource(viewModel.hotCircleList);
            }
            headerBinding.hotCircleLayout.setVisibility(
                    headerBinding.hotCircleLayout.isDataSourceEmpty() ? View.GONE : View.VISIBLE);
        });

        // 重置横向圈子列表的Loading状态
        viewModel.resetHotCircleLayoutStatus.observe(
                this,
                o -> headerBinding.hotCircleLayout.updateLoadingStatus()
        );

        viewModel.mBannerList.observe(this, model -> {
            if (BannerModel.isDataValid(model) && model.getHeight() > 0 && model.getWidth() > 0) {
                headerBinding.topBanner.rootLayout.setVisibility(View.VISIBLE);
                int dp10 = getResources().getDimensionPixelOffset(R.dimen.DIMEN_10DP);
                headerBinding.topBanner.rootLayout.setPadding(dp10, 0, dp10, 0);
                int width = ScreenUtils.getAppScreenWidth(getContext()) - dp10 * 2;
                CardView cardView =  headerBinding.topBanner.cardView;
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = width * model.getHeight() / model.getWidth();
                cardView.setLayoutParams(layoutParams);
                BannerUtils.initDefaultBanner(
                        model,
                        headerBinding.topBanner.banner,
                        getPid()
                );
            } else {
                headerBinding.topBanner.rootLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        List<VideoModel> list = new ArrayList<>();
        list.add(mediaModel);
        return ShortVideoListModel.Builder.aFeedModel()
                .withList(list)
                .build();
    }

    @Override
    protected void loadData() {

        // 监听登录和登出
        listenEvent();

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus();

    }

    @Override
    public void onReload(@NonNull View view) {
        viewModel.checkNetworkAndLoginStatus();
    }

    /**
     * 监听时间
     */
    protected void listenEvent() {
        super.listenEvent();

        // 用户加入或退出圈子
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class)
                .observe(this, o -> viewModel.updateRecommendContentList(true));

    }

    @Override
    protected void checkLoginStatus() {
        viewModel.checkNetworkAndLoginStatus();
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        if (viewModel != null) {
            viewModel.updateRecommendContentList(true);
        }
        if (headerBinding != null) {
            headerBinding.hotCircleLayout.onViewResume();
        }
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        if (headerBinding != null) {
            headerBinding.hotCircleLayout.onViewPause();
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if (headerBinding != null) {
            headerBinding.hotCircleLayout.onViewPause();
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (headerBinding != null) {
            headerBinding.hotCircleLayout.onViewResume();
        }
    }
}
