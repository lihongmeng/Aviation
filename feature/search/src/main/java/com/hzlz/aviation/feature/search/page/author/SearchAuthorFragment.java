package com.hzlz.aviation.feature.search.page.author;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.search.databinding.SearchRecyclerLayoutBinding;
import com.hzlz.aviation.feature.search.model.SearchAuthorModel;
import com.hzlz.aviation.feature.search.model.SearchModel;
import com.hzlz.aviation.feature.search.page.SearchBlankRefreshFooterView;
import com.hzlz.aviation.feature.search.repository.SearchResultRepository;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.recycler.BaseRecyclerFragment;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.search.R;
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
