package com.hzlz.aviation.feature.search.page.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.model.SearchModel;
import com.hzlz.aviation.feature.search.model.SearchType;
import com.hzlz.aviation.feature.search.page.SearchShortVideoViewModel;
import com.hzlz.aviation.feature.search.utils.SearchFragmentHelper;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * 搜索短剧集fragment
 */
public class SearchShortVideoFragment extends SearchBasePageFragment {
    private SearchShortVideoViewModel mSearchShortVideoViewModel;
    @Override
    protected void initView() {
        super.initView();
        SearchFragmentHelper.getInstance().addShortVideoFragmentRef(this);
    }

    @Override protected void bindViewModels() {
        super.bindViewModels();
        mSearchShortVideoViewModel = bingViewModel(SearchShortVideoViewModel.class);
        mSearchShortVideoViewModel.setType(getTabType());
    }

    /**
     * 获取layoutManager
     *
     * @return 默认为线性manager
     */
    @NonNull
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }
    @Override protected List<SearchDetailModel> getFromModel(SearchModel model) {
        return model != null ? model.mNews : null;
    }

    private boolean hasMore;
    @Override
    protected boolean hasMoreData() {
        return hasMore;
    }

    @Override
    protected int getTabType() {
        return SearchType.CATEGORY_NEWS;
    }

    /**
     * 加载更多短视频/音频数据
     *
     * @param refresh 首次进入详情页加载数据时会清空掉历史cursor
     * @return  对应的observable
     */
    public Observable<ShortVideoListModel> loadMoreShortData(boolean refresh) {
        return mSearchShortVideoViewModel.loadMoreShortData(refresh);
    }
}
