package com.hzlz.aviation.feature.community.viewmodel;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.NEED_HIDE_COMMUNITY_LAYOUT;
import static com.hzlz.aviation.kernel.stat.stat.StatPid.CIRCLE_DETAIL;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.utils.DefaultPageNumberUtil;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.util.AsyncUtils;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class CircleDetailListViewModel extends MediaPageViewModel {

    // CircleRepository
    private final CircleRepository circleRepository;

    public long gatherId;
    public long groupId;
    //显示类型，用于控制视频显示
    public int type;
    public StatFromModel statFromModel;

    /**
     * 热聊默认的排序规则
     */
    public int sortType = CircleDetailViewModel.sortType;

    /**
     * 标识本次刷新是否是由下拉触发
     */
    public boolean isNeedAddTopLive = true;

    // 社区名称
    public String communityName;

    // 信息流分页工具
    private final DefaultPageNumberUtil defaultPageNumberUtil = new DefaultPageNumberUtil();

    // 信息流分页工具需要对页面进行标记
    public String key;

    // 所属MCN
    public Long tenantId;

    // 所属MCN名称
    public String tenantName;

    private List<MediaModel> topLiveList = new ArrayList<>();

    public CircleDetailListViewModel(@NonNull Application application) {
        super(application);
        circleRepository = new CircleRepository();
        defaultPageNumberUtil.initSp(CIRCLE_DETAIL);
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else if (!UserManager.hasLoggedIn()) {
            updatePlaceholderLayoutType(PlaceholderType.UN_LOGIN);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
            loadRefreshData();
        }
    }

    private final GVideoResponseObserver<ListWithPage<MediaModel>> gVideoResponseObserver = new GVideoResponseObserver<ListWithPage<MediaModel>>() {

        @Override
        protected void onRequestStart() {
            super.onRequestStart();
            if (mMediaModelList.isEmpty()) {
                updatePlaceholderLayoutType(PlaceholderType.LOADING);
            }
        }

        @Override
        protected void onSuccess(@NonNull ListWithPage<MediaModel> mediaListWithPage) {

            if (gatherId == -999
                    && CircleDetailViewModel.sortType == Constant.CircleSortType.DEFAULT_RECOMMEND_SORT) {
                defaultPageNumberUtil.saveDefaultPageNumber(key, currPage);
                currPage = defaultPageNumberUtil.getDefaultPageNumber(key);
            }

            List<MediaModel> list = mediaListWithPage.getList();
            List<MediaModel> modelList = new ArrayList<>();

            if (gatherId == -999 && isNeedAddTopLive) {
                modelList.addAll(topLiveList);
            }

            for (MediaModel media : list) {
                if (media.isValid()) {
                    MediaModel model = new MediaModel(media);
                    model.tabId = mTabId;
                    model.setPid(getPid());
                    if (type == 5) {
                        //显示pgc视频样式
                        model.showMediaPageSource = MediaPageSource.PageSource.TV_COLLECTION;
                        model.setStatFromModel(statFromModel);
                    }
                    // model.playState = 0;
                    // model.viewPosition = 0;
                    model.saveValueToBundle(NEED_HIDE_COMMUNITY_LAYOUT,true);
                    model.correspondVideoModelAddress = media.getMemoryAddress();
                    modelList.add(model);
                }
            }
            if (gatherId == -999
                    && loadType == LOAD_DATA_TYPE_REFRESH
                    && !mediaListWithPage.getPage().hasNextPage()) {
                defaultPageNumberUtil.saveDefaultPageNumber(key, DefaultPageNumberUtil.MIN);
            }

            if (modelList.size() <= 0) {
                if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                    showToast(R.string.all_nor_more_data);
                    if (gatherId == -999) {
                        defaultPageNumberUtil.saveDefaultPageNumber(key, DefaultPageNumberUtil.MIN);
                    }
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
    };

    @Override
    protected IRecyclerModel<MediaModel> createModel() {
        return (page, loadListener) -> {
            if (gatherId == -999) {
                if (groupId == -999) {
                    return;
                }
                if (isNeedAddTopLive) {
                    circleRepository.circleTopLive(groupId)
                            .subscribe(
                                    new GVideoResponseObserver<List<MediaModel>>() {
                                        @Override
                                        protected void onSuccess(List<MediaModel> mediaModels) {
                                            topLiveList = mediaModels;
                                            if (topLiveList == null) {
                                                topLiveList = new ArrayList<>();
                                            }
                                            for (MediaModel media : topLiveList) {
                                                if (media.isValid()) {
                                                    media.tabId = mTabId;
                                                    media.setPid(getPid());
                                                    // model.playState = 0;
                                                    // model.viewPosition = 0;
                                                }
                                            }
                                            circleDetailHot(page);
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            circleDetailHot(page);
                                        }
                                    }
                            );
                } else {
                    circleDetailHot(page);
                }
            } else {
                isNeedAddTopLive = false;
                circleRepository.circleModuleListContent(
                        gatherId,
                        page,
                        DEFAULT_PAGE_COUNT
                ).subscribe(gVideoResponseObserver);
            }
        };

    }

    public void circleDetailHot(int page) {
        circleRepository.circleDetailHot(
                groupId,
                CircleDetailViewModel.sortType,
                page,
                DEFAULT_PAGE_COUNT
        ).subscribe(gVideoResponseObserver);
    }

    @Override
    public void loadFailure(Throwable throwable) {
        if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
            // 加载失败后的提示
            if (currPage > 1) {
                //加载失败需要回到加载之前的页数
                currPage--;
                if (CircleDetailViewModel.sortType == Constant.CircleSortType.DEFAULT_RECOMMEND_SORT) {
                    defaultPageNumberUtil.saveDefaultPageNumber(key, currPage);
                }
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
        if (CircleDetailViewModel.sortType == Constant.CircleSortType.DEFAULT_RECOMMEND_SORT) {
            currPage = defaultPageNumberUtil.getDefaultPageNumber(key) + 1;
        } else {
            currPage = 1;
        }
        isNeedAddTopLive = true;
        mModel.initialData(this);
    }

    /**
     * 刷新数据
     */
    public void loadRefreshData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        isNeedAddTopLive = true;
        loadType = LOAD_DATA_TYPE_REFRESH;
        if (CircleDetailViewModel.sortType == Constant.CircleSortType.DEFAULT_RECOMMEND_SORT) {
            currPage = defaultPageNumberUtil.getDefaultPageNumber(key) + 1;
        } else {
            currPage = 1;
        }
        mModel.loadData(currPage, this);
    }

    /**
     * 上拉加载更多数据
     */
    public void loadMoreData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        isNeedAddTopLive = false;
        loadType = LOAD_DATA_TYPE_LOAD_MORE;
        currPage++;
        mModel.loadData(currPage, this);
    }

}
