package com.hzlz.aviation.feature.community.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.MyCircle;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class HomeMyCommunityViewModel extends MediaPageViewModel {

    // CircleRepository
    private final CircleRepository circleRepository;

    // 当前的社区列表
    public CheckThreadLiveData<List<Circle>> circleList = new CheckThreadLiveData<>();

    public HomeMyCommunityViewModel(@NonNull Application application) {
        super(application);
        circleRepository = new CircleRepository();
    }

    public void loadContentLastPageData(int pageNum) {
        circleRepository.myCircleUnderContent(pageNum, DEFAULT_PAGE_COUNT)
                .timeout(CircleRepository.LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {
                    @Override
                    protected void onRequestStart() {
                        super.onRequestStart();
                        if (mMediaModelList.isEmpty()) {
                            updatePlaceholderLayoutType(PlaceholderType.LOADING);
                        }
                    }

                    @Override
                    protected void onSuccess(@NonNull ListWithPage<MediaModel> mediaListWithPage) {
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
                        if (modelList.size() <= 0) {
                            if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                                showToast(R.string.all_nor_more_data);
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

    public void loadMyCircle() {
        circleRepository.myCircle()
                .timeout(CircleRepository.LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new GVideoResponseObserver<MyCircle>() {

                    @Override
                    protected void onRequestStart() {
                        super.onRequestStart();
                        if (mMediaModelList.isEmpty()) {
                            updatePlaceholderLayoutType(PlaceholderType.LOADING);
                        }
                    }

                    @Override
                    protected void onSuccess(@NonNull MyCircle myCircle) {
                        circleList.setValue(myCircle.groupList);
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


    @Override
    protected IRecyclerModel<MediaModel> createModel() {
        return (page, loadListener) -> {
            loadMyCircle();
            loadContentLastPageData(page);
        };
    }

    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        loadRefreshData();
    }

    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        loadMoreData();
    }

    public void checkNetworkAndLoginStatus(IGVideoRefreshLoadMoreView view) {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            circleList.setValue(null);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            updatePlaceholderLayoutType(PlaceholderType.UN_LOGIN);
            circleList.setValue(null);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else {
            if (view != null) {
                updatePlaceholderLayoutType(PlaceholderType.LOADING);
                onRefresh(view);
            }
        }
    }


}
