package com.hzlz.aviation.feature.feed.view.recommend;

import static com.hzlz.aviation.kernel.stat.stat.StatPid.HOME_RECOMMEND;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.feed.R;
import com.hzlz.aviation.feature.feed.frame.recycler.FeedPageViewModel;
import com.hzlz.aviation.feature.feed.repository.FeedRepository;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleJoinStatus;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.utils.DefaultPageNumberUtil;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


/**
 * 首页推荐界面 ViewModel
 *
 * @since 2020-02-10 15:27
 */
public final class HomeRecommendViewModel extends FeedPageViewModel {

    // 横向圈子列表是分页拉取的，第一页的pageNumber
    public final int circleDefaultPage = 0;

    // 当前横向圈子列表已加载的页数
    public final CheckThreadLiveData<Integer> hotCircleListPage = new CheckThreadLiveData<>(circleDefaultPage);

    // 标识横向圈子列表是否还有下一页，在下拉refresh的时候会置为true
    public boolean hasNext = false;

    // 当前页请求的横向圈子列表，并非全量横向圈子列表
    // 需要根据 hasNext、当前页数以及 hotCircleListPage
    // 判断是向数据源里添加此列表，还是替换数据源
    public List<Circle> hotCircleList = new ArrayList<>();

    // 通知观测者需要重置横向圈子列表的Loading状态，否则不能正常加载下一页
    public final CheckThreadLiveData<Boolean> resetHotCircleLayoutStatus = new CheckThreadLiveData<>();

    // 通知观测者需要整体全部更新
    public final CheckThreadLiveData<Boolean> mAutoRefreshLiveData = new CheckThreadLiveData<>();

    // 通知观测者需要更新Banner
    public final CheckThreadLiveData<BannerModel> mBannerList = new CheckThreadLiveData<>();

    // HomeRecommendRepository
    private final FeedRepository repository = FeedRepository.getInstance();

    // 用来控制每次拉取信息流的页码
    private final DefaultPageNumberUtil defaultPageNumberUtil = new DefaultPageNumberUtil();

    //
    private boolean isRefresh=true;

    public HomeRecommendViewModel(@NonNull Application application) {
        super(application);
        defaultPageNumberUtil.initSp(HOME_RECOMMEND);
    }

    void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            loadComplete();
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
            showHotCircleNoData();
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
            mAutoRefreshLiveData.setValue(true);
        }
    }

    private void showHotCircleNoData() {
        hasNext = false;
        hotCircleList.clear();
        resetHotCircleLayoutStatus.setValue(true);
        hotCircleListPage.setValue(circleDefaultPage);
    }

    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        checkNetworkAndLoginStatus();
    }

    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        loadMoreData();
    }

    public void refreshDataReal() {
        getBannerList();
//        updateRecommendContentList(true);
        loadRefreshData();
    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {
        return (page, loadListener) -> repository.getRecommendContentList(page, DEFAULT_PAGE_COUNT,isRefresh)
                .subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {

                    @Override
                    protected boolean isShowPlaceholderLayout() {
                        return mMediaModelList.isEmpty();
                    }

                    @Override
                    protected void onSuccess(@NonNull ListWithPage<MediaModel> mediaListWithPage) {
                        defaultPageNumberUtil.saveDefaultPageNumber(HOME_RECOMMEND, currPage);
                        currPage = defaultPageNumberUtil.getDefaultPageNumber(HOME_RECOMMEND);

                        List<MediaModel> list = mediaListWithPage.getList();
                        List<MediaModel> modelList = new ArrayList<>();
                        for (MediaModel media : list) {
                            if (media.isValid()) {
                                MediaModel model = new MediaModel(media);
                                model.tabId = mTabId;
                                model.setPid(getPid());
                                //model.playState = 0;
                                //model.viewPosition = 0;
                                model.correspondVideoModelAddress = media.getMemoryAddress();
                                modelList.add(model);
                            }
                        }
                        if (loadType == LOAD_DATA_TYPE_REFRESH && !mediaListWithPage.getPage().hasNextPage()) {
                            defaultPageNumberUtil.saveDefaultPageNumber(HOME_RECOMMEND, DefaultPageNumberUtil.MIN);
                        }
                        if (modelList.size() <= 0) {
                            if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                                showToast(R.string.all_nor_more_data);
                                defaultPageNumberUtil.saveDefaultPageNumber(HOME_RECOMMEND, DefaultPageNumberUtil.MIN);
                            }
                        } else {
                            loadSuccess(modelList);
                        }
                        loadComplete();
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (throwable instanceof TimeoutException ||
                                throwable instanceof SocketTimeoutException) {
                            showToast(R.string.all_network_not_available);
                            loadComplete();
                            return;
                        }
                        showToast(R.string.all_nor_more_data);
                        loadComplete();
                    }
                });
    }

    public void updateRecommendContentList(final boolean newData) {
        Integer hotCirclePageNumber = hotCircleListPage.getValue();
        if (hotCirclePageNumber == null) {
            hotCirclePageNumber = circleDefaultPage;
        }
        if (newData) {
            hasNext = true;
            hotCirclePageNumber = circleDefaultPage;
        }
        if (!hasNext) {
            return;
        }
        repository.getHotCircleList(hotCirclePageNumber + 1, DEFAULT_PAGE_COUNT)
                .subscribe(new GVideoResponseObserver<ListWithPage<Circle>>() {
                    @Override
                    protected void onRequestStart() {
                        super.onRequestStart();
                    }

                    @Override
                    protected void onSuccess(@NonNull ListWithPage<Circle> circleListWithPage) {
                        resetHotCircleLayoutStatus.setValue(true);
                        List<Circle> circleList = circleListWithPage.getList();
                        if (circleList == null || circleList.isEmpty()) {
                            return;
                        }
                        Integer hotCirclePageNumber = hotCircleListPage.getValue();
                        if (hotCirclePageNumber == null) {
                            hotCirclePageNumber = circleDefaultPage;
                        }
                        if (newData) {
                            hotCirclePageNumber = circleDefaultPage;
                        }
                        hotCircleList = circleListWithPage.getList();
                        if (circleListWithPage.getPage().hasNextPage()) {
                            hotCirclePageNumber++;
                            hasNext = true;
                        } else {
                            hasNext = false;
                        }
                        hotCircleListPage.setValue(hotCirclePageNumber);
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        resetHotCircleLayoutStatus.setValue(true);
                        hasNext = false;
                        if (throwable instanceof TimeoutException ||
                                throwable instanceof SocketTimeoutException) {
                            hotCircleList.clear();
                        }
                    }
                });

    }

    public void dealJoinEnterClick(final View view, Circle circle) {
        // 未登录需要跳转到登录页面
        AccountPlugin accountPlugin=PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()) {
            accountPlugin.startLoginActivity(view.getContext());
            GVideoSensorDataManager.getInstance().enterRegister(
                    getPid(),
                    ResourcesUtils.getString(R.string.enter_circle)
            );
        } else {
            if (circle == null) {
                return;
            }
            CirclePlugin circlePlugin = PluginManager.get(CirclePlugin.class);
            if (!circle.isJoin()) {
                circlePlugin.joinCircle(
                        circle.groupId,
                        "",
                        new BaseGVideoResponseObserver<Object>() {
                            @Override
                            protected void onRequestData(Object o) {
                                showToast(ResourcesUtils.getString(R.string.join_success));
                                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class).post(null);
                                circle.setJoin(true);
                                view.setBackgroundResource(R.drawable.icon_enter_circle_long);
                                circlePlugin.startCircleDetailWithActivity(view.getContext(), circle, null);
                            }

                            @Override
                            protected void onRequestError(Throwable throwable) {
                                showToast(ResourcesUtils.getString(R.string.join_failed));
                            }
                        });
            } else {
                circlePlugin.startCircleDetailWithActivity(view.getContext(), circle, null);
            }
        }
    }

    private void getBannerList() {
        repository.getBannerList(Integer.parseInt(HOME_RECOMMEND)).subscribe(new GVideoResponseObserver<BannerModel>() {
            @Override
            protected void onSuccess(@NonNull BannerModel model) {
                mBannerList.setValue(model);
                if (BannerModel.isDataValid(model)) {
                }
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                super.onFailed(throwable);
                LogUtils.e(throwable.getMessage());
                mBannerList.setValue(null);
            }
        });

    }


    @Override
    public void loadFailure(Throwable throwable) {
        if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
            // 加载失败后的提示
            if (currPage > 1) {
                //加载失败需要回到加载之前的页数
                currPage--;
                defaultPageNumberUtil.saveDefaultPageNumber(HOME_RECOMMEND, currPage);
            }
        }
        AsyncUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mView.loadFailure(throwable);
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initialData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_INITIAL;
        currPage = defaultPageNumberUtil.getDefaultPageNumber(HOME_RECOMMEND) + 1;
        mModel.initialData(this);
    }

    /**
     * 刷新数据
     */
    public void loadRefreshData() {
        isRefresh=true;
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        getBannerList();
        loadType = LOAD_DATA_TYPE_REFRESH;
        currPage = defaultPageNumberUtil.getDefaultPageNumber(HOME_RECOMMEND) + 1;
        mModel.loadData(currPage, this);
    }

    /**
     * 上拉加载更多数据
     */
    public void loadMoreData() {
        isRefresh=false;
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_LOAD_MORE;
        currPage++;
        mModel.loadData(currPage, this);
    }

}
