package com.hzlz.aviation.feature.search.page.fragment;

import android.os.Bundle;

import com.hzlz.aviation.feature.search.api.SearchApiConstants;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.model.SearchModel;
import com.hzlz.aviation.feature.search.model.SearchType;

import java.util.List;

/**
 * 搜索公共 fragment
 */
public class SearchCommonFragment extends SearchBasePageFragment {

    int tabType;
    private boolean hasMore;

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        tabType = bundle.getInt(SearchApiConstants.SEARCH_TYPE);
    }

    @Override
    protected List<SearchDetailModel> getFromModel(SearchModel model) {
        return model != null ? getData(model) : null;
    }

    @Override
    protected boolean hasMoreData() {
        return hasMore;
    }

    @Override
    protected int getTabType() {
        return tabType;
    }

    private List<SearchDetailModel> getData(SearchModel model){
        switch (getTabType()){
            case SearchType.CATEGORY_ALL:
                hasMore = model.hasMoreAll;
                return model.mAll;
            case SearchType.CATEGORY_AUTHORS:
                hasMore = model.hasMoreAuthors;
                return model.mAuthors;
            case SearchType.CATEGORY_COMMUNITY:
                hasMore = model.hasMoreCommunities;
                return model.mCommunities;
            case SearchType.CATEGORY_PROGRAM:
                hasMore = model.hasMoreProgrammes;
                return model.mProgrammes;
            case SearchType.CATEGORY_NEWS:
                hasMore = model.hasMoreNews;
                return model.mNews;
            case SearchType.CATEGORY_MOMENT:
                hasMore = model.hasMoreMoment;
                return model.mMoment;
            default:
                return null;
        }
    }
}
