package com.hzlz.aviation.feature.search;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.hzlz.aviation.feature.search.adapter.history.SearchHistoryNewAdapter;
import com.hzlz.aviation.feature.search.api.SearchApiConstants;
import com.hzlz.aviation.feature.search.databinding.FragmentSearchBinding;
import com.hzlz.aviation.feature.search.db.entity.SearchHistoryEntity;
import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.model.SearchModel;
import com.hzlz.aviation.feature.search.repository.SearchResultRepository;
import com.hzlz.aviation.feature.search.tab.TabItemInfo;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.util.SoftInputUtils;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.List;

/**
 * 搜索fragment
 */
public class SearchFragment extends BaseFragment<FragmentSearchBinding> {

    /** 持有的搜索模型 */
    private SearchViewModel mSearchViewModel;
    /** 首页page 适配器 */
    protected FragmentPagerItemAdapter mPagerAdapter;
    /** 搜索adapter */
    private SearchHistoryNewAdapter mHistoryAdapter;
    /** 是否已初始化 */
    private boolean hasInit = false;

    private int mTabSize = -1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getPid() {
        return StatPid.SEARCH;
    }

    @Override
    public String getGvFragmentId() {
        return StatPid.SEARCH;
    }

    @Override
    protected void initView() {
        MediaFragmentManager.getInstance().addFragmentRef(this);
        mBinding.resultInclude.searchResultLayout.setVisibility(View.GONE);
        mBinding.searchBarInclude.searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                return startSearch();
            }
            return false;
        });
        mBinding.searchBarInclude.searchEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mSearchViewModel.onBackToSearchHistoryPage();
                mBinding.resultInclude.searchResultLayout.setVisibility(View.GONE);
                mBinding.historyInclude.searchHistoryLayout.setVisibility(View.VISIBLE);
            }
        });
        mBinding.searchBarInclude.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }
                mSearchViewModel.getSearchBarDataBinding().updateSearchBtnState(!TextUtils.isEmpty(s.toString()));
            }
        });

        mBinding.searchBarInclude.searchScan.setOnClickListener(v -> {
            SoftInputUtils.hideSoftInput(getActivity());
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_SCAN_SEARCH).post("");
        });

        mBinding.searchBarInclude.searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftInputUtils.hideSoftInput(getActivity());
                getActivity().finish();
            }
        });

        mBinding.searchBarInclude.searchEdit.setOnClickListener(v -> GVideoSensorDataManager.getInstance().clickSearchEditText());

        initHistoryRecyclerView();
        setupPlaceholderLayout(R.id.empty_container);
        SoftInputUtils.showSoftInput(mBinding.searchBarInclude.searchEdit,
            SearchRuntime.getAppContext(),200L);
    }

    @Override
    public void onReload(@NonNull View view) {
        startSearch();
    }

    /**
     * 显示占位页面
     */
    private void showPlaceHolderView(@PlaceholderType int type) {
        if (mBinding == null) {
            return;
        }
        SoftInputUtils.hideSoftInput(getActivity());
        mBinding.searchBarInclude.searchEdit.clearFocus();
        mBinding.historyInclude.searchHistoryLayout.setVisibility(View.GONE);
        mBinding.emptyContainer.setVisibility(View.VISIBLE);
        updatePlaceholderLayoutType(type);
    }

    /**
     * 隐藏占位页面
     */
    private void hidePlaceHolderView() {
        if (mBinding == null) {
            return;
        }
        mBinding.emptyContainer.setVisibility(View.GONE);
    }

    /**
     * 初始化搜索历史recycler view
     */
    private void initHistoryRecyclerView() {
        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
        manager.setFlexWrap(FlexWrap.WRAP);
        mBinding.historyInclude.searchFlexBox.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryAdapter = new SearchHistoryNewAdapter(getContext());
        mBinding.historyInclude.searchFlexBox.setAdapter(mHistoryAdapter);
    }

    @Override
    protected void bindViewModels() {
        mSearchViewModel =  bingViewModel(SearchViewModel.class);
        mHistoryAdapter.updateViewModel(mSearchViewModel);
        mSearchViewModel.getSearchLiveData().observe(this, s -> onSearchResult());
        mSearchViewModel.getSearchErrorLiveData().observe(this, this::showPlaceHolderView);
        mBinding.searchBarInclude.setBinding(mSearchViewModel.getSearchBarDataBinding());
        mBinding.searchBarInclude.setViewModel(mSearchViewModel);
        mBinding.historyInclude.setBinding(mSearchViewModel.getSearchHistoryDataBinding());
        mBinding.historyInclude.setViewModel(mSearchViewModel);
        mBinding.setViewModel(mSearchViewModel);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String hintSearchWord = SearchFragmentArgs.fromBundle(arguments).getSearchWord();
            if (!TextUtils.isEmpty(hintSearchWord)) {
                mBinding.searchBarInclude.searchEdit.setHint(hintSearchWord);
                mSearchViewModel.getSearchBarDataBinding().updateHintSearchWord(hintSearchWord);
                mSearchViewModel.getSearchBarDataBinding().updateSearchBtnState(true);
            }
        }

    }

    /**
     * 开始搜索
     */
    private void onSearchResult() {
        mBinding.resultInclude.searchResultLayout.setVisibility(View.VISIBLE);
        mBinding.historyInclude.searchHistoryLayout.setVisibility(View.GONE);
        hidePlaceHolderView();
        initResult();
        SoftInputUtils.hideSoftInput(getActivity());
        mBinding.searchBarInclude.searchEdit.clearFocus();
    }

    @Override
    protected void loadData() {
        SearchResultRepository.getInstance().getLiveData().observe(this,
            new Observer<SearchModel>() {
                @Override public void onChanged(SearchModel searchModel) {
                    List<TabItemInfo> list = TabItemInfo.getDefaultSearchResultTabList();
                    int authorIndex = 1;
                    int position = 0;
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (i == authorIndex) {
                                if (!searchModel.mAuthors.isEmpty()) {
                                    mBinding.resultInclude.searchResultViewPager.setCurrentItem(i);
                                    position = i;
                                    break;
                                }
                            } else {
                                TabItemInfo tab = list.get(i);
                                List<SearchDetailModel> result =
                                    searchModel.getResultByType(tab.tabType);
                                if (result != null && !result.isEmpty()) {
                                    mBinding.resultInclude.searchResultViewPager.setCurrentItem(i);
                                    position = i;
                                    break;
                                }
                            }

                        }
                    }
                    if (mTabSize > position) {
                        changeTabTextStyle(mTabSize, position);
                    }

                }
            });
        mSearchViewModel.loadSearchHistory().observe(this, new Observer<List<SearchHistoryEntity>>() {
            @Override
            public void onChanged(List<SearchHistoryEntity> entities) {
                if (entities == null) {
                    return;
                }
                List<SearchHistoryEntity> list = mSearchViewModel.handleCheckHistory(entities);
                if (mBinding.resultInclude.searchResultLayout.getVisibility() != View.VISIBLE) {
                    if (entities.size() > 0) {
//                        mBinding.historyInclude.searchHistoryLayout.setVisibility(View.VISIBLE);
                        mBinding.historyInclude.clear.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.historyInclude.clear.setVisibility(View.INVISIBLE);
                    }
                    if (mHistoryAdapter != null) {
                        mHistoryAdapter.refreshData(list);
                    }
                }
            }
        });
    }

    /**
     * 初始化搜索结果页
     */
    private void initResult() {
        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getContext());
        List<TabItemInfo> tabList = TabItemInfo.getDefaultSearchResultTabList();
        TabItemInfo info;
        mTabSize = tabList.size();
        for (int i = 0; i < mTabSize; i++) {
            info = tabList.get(i);
            Bundle bundle = new Bundle();
            bundle.putInt(SearchApiConstants.SEARCH_TYPE,info.tabType);
            creator.add(info.tabName, TabItemInfo.getFragmentClassByType(info.tabType),bundle);
        }
        mPagerAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), creator.create());

        mBinding.resultInclude.searchResultViewPager.setAdapter(mPagerAdapter);
        mBinding.resultInclude.searchResultViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabTextStyle(mTabSize, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mBinding.resultInclude.searchResultTabs.setViewPager(mBinding.resultInclude.searchResultViewPager);
    }

    /**
     * 更改tab text样式
     *
     * @param size      tab item容量
     * @param position  位置
     */
    private void changeTabTextStyle(int size, int position) {
        for (int i = 0; i < size; i++) {
            View tab = mBinding.resultInclude.searchResultTabs.getTabAt(i);
            if (tab instanceof TextView) {
                ((TextView) tab).setTypeface(Typeface.defaultFromStyle(
                    i == position ? Typeface.BOLD : Typeface.NORMAL));
            }
        }
    }

    /**
     * 开始刷新
     */
    private boolean startSearch() {
        Editable text = mBinding.searchBarInclude.searchEdit.getText();
        if (text == null) {
            return false;
        }
        String searchWord = text.toString();
        if (TextUtils.isEmpty(searchWord)) {
            String hintSearchWord = mSearchViewModel.getSearchBarDataBinding().hintSearchWord.get();
            if (TextUtils.isEmpty(hintSearchWord)) {
                showToast(getResources().getString(R.string.search_bar_hint_toast));
                return false;
            }
            mSearchViewModel.getSearchBarDataBinding().updateSearchWord(hintSearchWord);
            searchWord = hintSearchWord;
        }
        mSearchViewModel.startSearch(searchWord);
        return true;
    }
}
