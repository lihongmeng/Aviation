package com.hzlz.aviation.feature.account.ui.follow;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.UgcAuthorModel;
import com.hzlz.aviation.feature.account.repository.AuthorRepository;
import com.hzlz.aviation.feature.account.repository.InteractionRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HomeFollowContentListViewModel extends MediaPageViewModel {

    public static final int LOAD_DATA_TIME_LIMIT = 10;

    private AuthorRepository authorRepository;
    private InteractionRepository iFollowRepository;

    public CheckThreadLiveData<Boolean> noMediaListData = new CheckThreadLiveData<>();

    // 观测这个
    public CheckThreadLiveData<Boolean> isNeedLoadData = new CheckThreadLiveData<>();

    public List<UgcAuthorModel> myFollowList = new ArrayList<>();
    public CheckThreadLiveData<List<UgcAuthorModel>> myFollowListLiveData = new CheckThreadLiveData<>();

    public HomeFollowContentListViewModel(@NonNull Application application) {
        super(application);
        authorRepository = new AuthorRepository();
        iFollowRepository = new InteractionRepository();
    }

    public void loadContentLastPageData(int pageNum) {
        iFollowRepository.getAlreadyFollowContentList(pageNum, DEFAULT_PAGE_COUNT)
                .timeout(LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
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

                        if(mAdapter==null||mAdapter.getItemCount()- mAdapter.getHeaderViewCount()==0){
                            noMediaListData.setValue(true);
                        }
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


    public void loadMyFollowList() {
        new AuthorRepository()
                .getMyFollowList(1, LOAD_DATA_TIME_LIMIT)
                .subscribe(new BaseViewModel.GVideoResponseObserver<ListWithPage<UgcAuthorModel>>() {

                    @Override
                    protected void onRequestStart() {
                        super.onRequestStart();
                        if (mMediaModelList.isEmpty()) {
                            updatePlaceholderLayoutType(PlaceholderType.LOADING);
                        }
                    }

                    @Override
                    protected void onSuccess(@NonNull ListWithPage<UgcAuthorModel> listWithPage) {
                        myFollowList = listWithPage.getList();
                        myFollowListLiveData.setValue(myFollowList);
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
            loadMyFollowList();
            loadContentLastPageData(page);
        };
    }

    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        loadRefreshData();
    }

    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        loadMoreData();
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
            isNeedLoadData.setValue(true);
        }
    }


}
