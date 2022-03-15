package com.hzlz.aviation.feature.search.page;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.search.api.SearchApiConstants;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.repository.SearchRepository;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.feature.search.R;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * search  page页面数据模型
 */
public class SearchPageViewModel extends BaseRecyclerViewModel<SearchDetailModel> {

    /** 最长等待时间 */
    public static final int LOAD_DATA_TIME_LIMIT = 10;
    /** 搜索数据仓库 */
    protected SearchRepository mSearchRepository = new SearchRepository();

    protected int mCategory;
    protected String mQuery;

    public SearchPageViewModel(@NonNull Application application) {
        super(application);
    }

    public void setType(int type) {
        mCategory = type;
    }


    public void setQuery(String query) {
        mQuery = query;
    }

    @Override
    protected IRecyclerModel<SearchDetailModel> createModel() {

        return new IRecyclerModel<SearchDetailModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<SearchDetailModel> loadListener) {
                mSearchRepository.searchMedia(mQuery, mCategory, page,
                    SearchApiConstants.DEFAULT_PAGE_COUNT)
                    .timeout(LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS).subscribe(
                    new GVideoResponseObserver<ListWithPage<SearchDetailModel>>() {
                        @Override protected void onSuccess(
                            @NonNull
                                ListWithPage<SearchDetailModel> searchModel) {
                            loadSuccess(searchModel.getList());
                            if (!searchModel.getPage().hasNextPage()) {
                                updateNoMoreDataText(R.string.all_nor_more_data);
                            } /*else {
                                if (mCategory == SearchType.CATEGORY_MOMENT) {
                                    updateNoMoreDataText(R.string.all_nor_more_data);
                                } else {
                                    hideNoMoreData();
                                }
                            }*/
                            loadComplete();
                        }

                        @Override protected void onRequestStart() {
                            if (mAdapter.getItemCount() > 0) {
                                showNoMoreData(R.string.all_try_load_more);
                            }
                        }

                        @Override protected void onFailed(@NonNull Throwable throwable) {
                            if (throwable instanceof TimeoutException ||
                                throwable instanceof SocketTimeoutException) {
                                showToast(R.string.all_network_not_available);
                                return;
                            }
                            hideNoMoreData();
                            loadComplete();
                        }
                    });
            }
        };
    }

    /**
     * 显示没有更多数据
     *
     * @param stringRes 待更新的字符串资源
     */
    public void showNoMoreData(int stringRes) {
        if (mAdapter instanceof SearchRecyclerAdapter) {
            ((SearchRecyclerAdapter) mAdapter).showNoMoreData(stringRes);
        }
    }

    /**
     * 隐藏没有更多数据
     */
    public void hideNoMoreData() {
        if (mAdapter instanceof SearchRecyclerAdapter) {
            ((SearchRecyclerAdapter) mAdapter).hideNoMoreData();
        }
    }

    /**
     * 更新没有更多数据
     *
     * @param stringRes 待更新的字符串资源
     */
    public void updateNoMoreDataText(int stringRes) {
        if (mAdapter instanceof SearchRecyclerAdapter) {
            ((SearchRecyclerAdapter) mAdapter).updateNoMoreDataText(stringRes);
        }
    }
}
