package com.jxntv.search.page.author;

import android.app.Application;
import androidx.annotation.NonNull;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewModel;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.search.R;
import com.jxntv.search.api.SearchApiConstants;
import com.jxntv.search.model.SearchAuthorModel;
import com.jxntv.search.page.SearchPageViewModel;
import com.jxntv.search.repository.SearchRepository;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * search  page页面数据模型
 */
public class SearchAuthorViewModel extends BaseRecyclerViewModel<SearchAuthorModel> {
    private SearchRepository mSearchRepository = new SearchRepository();
    private String mQuery;

    public SearchAuthorViewModel(@NonNull Application application) {
        super(application);
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    @Override
    protected IRecyclerModel<SearchAuthorModel> createModel() {

        return new IRecyclerModel<SearchAuthorModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<SearchAuthorModel> loadListener) {
                mSearchRepository.searchAuthor(mQuery, page, SearchApiConstants.DEFAULT_PAGE_COUNT)
                    .timeout(SearchPageViewModel.LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS).subscribe(
                    new GVideoResponseObserver<ListWithPage<SearchAuthorModel>>() {
                        @Override protected void onSuccess(
                            @NonNull
                                ListWithPage<SearchAuthorModel> searchModel) {
                                loadSuccess(searchModel.getList());
                            if (!searchModel.getPage().hasNextPage()) {
                                updateNoMoreDataText(R.string.all_nor_more_data);
                            } else {
                                hideNoMoreData();
                            }
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
    void showNoMoreData(int stringRes) {
        if (mAdapter instanceof SearchAuthorRecyclerAdapter) {
            ((SearchAuthorRecyclerAdapter) mAdapter).showNoMoreData(stringRes);
        }
    }

    /**
     * 隐藏没有更多数据
     */
    void hideNoMoreData() {
        if (mAdapter instanceof SearchAuthorRecyclerAdapter) {
            ((SearchAuthorRecyclerAdapter) mAdapter).hideNoMoreData();
        }
    }

    /**
     * 更新没有更多数据
     *
     * @param stringRes 待更新的字符串资源
     */
    void updateNoMoreDataText(int stringRes) {
        if (mAdapter instanceof SearchAuthorRecyclerAdapter) {
            ((SearchAuthorRecyclerAdapter) mAdapter).updateNoMoreDataText(stringRes);
        }
    }

}
