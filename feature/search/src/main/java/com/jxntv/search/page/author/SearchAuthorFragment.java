package com.jxntv.search.page.author;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.recycler.BaseRecyclerFragment;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.ioc.PluginManager;
import com.jxntv.search.R;
import com.jxntv.search.databinding.SearchRecyclerLayoutBinding;
import com.jxntv.search.model.SearchAuthorModel;
import com.jxntv.search.model.SearchModel;
import com.jxntv.search.page.SearchBlankRefreshFooterView;
import com.jxntv.search.repository.SearchResultRepository;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * 搜索作者fragment
 */
public class SearchAuthorFragment extends BaseRecyclerFragment<SearchAuthorModel, SearchRecyclerLayoutBinding> {
    private SearchAuthorViewModel mSearchAuthorViewModel;
    @NonNull
    @Override
    protected BaseRecyclerAdapter<SearchAuthorModel, BaseRecyclerViewHolder> createAdapter() {
        return new SearchAuthorRecyclerAdapter(getContext());
    }

    protected RefreshFooter createRefreshFooterView() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return new SearchBlankRefreshFooterView(activity);
    }

    @Override
    protected boolean enableRefresh() {
        return false;
    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler_view;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.empty_container;
    }

    @Override
    protected int initRefreshViewId() {
        return R.id.refresh_layout;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_recycler_layout;
    }

    @Override
    protected void bindViewModels() {
        mSearchAuthorViewModel = bingViewModel(SearchAuthorViewModel.class);
    }

    @Override
    protected void loadData() {
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        SearchResultRepository.getInstance().getLiveData().observe(this,
            new Observer<SearchModel>() {
                @Override public void onChanged(SearchModel model) {
                    mSearchAuthorViewModel.setQuery(model.query);
//                    mSearchAuthorViewModel.loadSuccess(model.mAuthors);
                    mSearchAuthorViewModel.loadComplete();
                }
            });
        SearchModel model = SearchResultRepository.getInstance().getLiveData().getValue();
        if (model != null) {
            mSearchAuthorViewModel.setQuery(model.query);
            if (mAdapter instanceof SearchAuthorRecyclerAdapter) {
                ((SearchAuthorRecyclerAdapter) mAdapter).setQuery(model.query);
            }
//            mSearchAuthorViewModel.loadSuccess(model.mAuthors);
            mSearchAuthorViewModel.loadComplete();
        }

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mSearchAuthorViewModel.loadMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }
}
