package com.hzlz.aviation.feature.search;

import static com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_NORMAL;
import static com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_USER;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hzlz.aviation.feature.search.databinding.SearchBarDataBinding;
import com.hzlz.aviation.feature.search.databinding.SearchHistoryDataBinding;
import com.hzlz.aviation.feature.search.db.entity.SearchHistoryEntity;
import com.hzlz.aviation.feature.search.model.SearchModel;
import com.hzlz.aviation.feature.search.repository.SearchHistoryRepository;
import com.hzlz.aviation.feature.search.repository.SearchRepository;
import com.hzlz.aviation.feature.search.repository.SearchResultRepository;
import com.hzlz.aviation.feature.search.widget.SearchRemoveHistoryDialog;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.SoftInputUtils;
import com.hzlz.aviation.library.util.async.GlobalExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索数据模型
 */
public class SearchViewModel extends BaseViewModel {

    /** 搜索词长度限制 */
    private static final int SEARCH_WORD_LIMIT = 30;
    /** 搜索data binding */
    private SearchBarDataBinding mSearchDataBinding;
    /** 搜索历史data binding */
    private SearchHistoryDataBinding mSearchHistoryDataBinding;
    /** 搜索仓库类 */
    private SearchRepository mRepository;
    /** 搜索历史仓库类 */
    private SearchHistoryRepository mHistoryRepository;
    /** 搜索历史最大限制 */
    private static final int HISTORY_MAX_LIMIT = 10;
    /** 搜索词live data */
    private final MutableLiveData<String> mSearchLiveData = new MutableLiveData<>();
    /** 搜索错误live data */
    private final MutableLiveData<Integer> mSearchErrorLiveData = new MutableLiveData<>();
    /** 是否在搜索状态 */
    private boolean mIsOnSearchState = false;

    /**
     * 构造函数
     */
    public SearchViewModel(@NonNull Application application) {
        super(application);
        mHistoryRepository = new SearchHistoryRepository();
        mRepository = new SearchRepository();
        mSearchDataBinding = new SearchBarDataBinding();
        mSearchHistoryDataBinding = new SearchHistoryDataBinding();
    }

    /**
     * 获取搜索bar data binding
     *
     * @return 搜素data binding
     */
    public SearchBarDataBinding getSearchBarDataBinding() {
        return mSearchDataBinding;
    }

    /**
     * 获取搜索历史data binding
     *
     * @return 搜素历史data binding
     */
    public SearchHistoryDataBinding getSearchHistoryDataBinding() {
        return mSearchHistoryDataBinding;
    }

    /**
     * 读取搜索历史
     *
     * @return 搜索历史列表
     */
    public LiveData<List<SearchHistoryEntity>> loadSearchHistory() {
        return mHistoryRepository.loadSearchHistory();
    }

    /**
     * 删除所有历史数据
     */
    public void removeAllSearchHistory() {
        GlobalExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mHistoryRepository.removeAll();
            }
        }, "searchTask", PRIORITY_USER);
    }

    /**
     * 删除历史数据
     */
    public void removeSearchHistory(SearchHistoryEntity entities) {
        GlobalExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mHistoryRepository.deteleSulplus(entities);
            }
        }, "searchTask", PRIORITY_USER);
    }

    /**
     * 开始搜索
     *
     * @param searchWord 搜索词
     */
    public void startSearch(String searchWord) {
        if (TextUtils.isEmpty(searchWord)) {
            return;
        }
        if (searchWord.length() > SEARCH_WORD_LIMIT) {
            showToast(R.string.search_history_text_limit);
            return;
        }

        if (!NetworkUtils.isNetworkConnected()) {
            mSearchErrorLiveData.postValue(PlaceholderType.NETWORK_NOT_AVAILABLE);
            return;
        }

        mSearchErrorLiveData.postValue(PlaceholderType.LOADING);

        mIsOnSearchState = true;

        GlobalExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String hintSearchWord = mSearchDataBinding.hintSearchWord.get();
                mRepository.searchAll(searchWord,hintSearchWord).subscribe(
                    new GVideoResponseObserver<SearchModel>() {
                        @Override protected void onSuccess(@NonNull SearchModel searchModel) {
                            if (!mIsOnSearchState) {
                                return;
                            }
                            mSearchLiveData.postValue(searchWord);
                            SearchResultRepository.getInstance().refreshSearchModel(searchModel);

                            GVideoSensorDataManager.getInstance().sendSearchRequest(
                                    !searchModel.isResultAllEmpty(),
                                    searchWord,
                                    searchModel.getResultNumber(),
                                    hintSearchWord
                            );
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            LogUtils.e(throwable.getMessage());
                        }
                    });
                mHistoryRepository.onSearch(searchWord);
            }
        }, "searchTask", PRIORITY_USER);

    }

    public void onBackToSearchHistoryPage() {
        mIsOnSearchState = false;
        SearchResultRepository.getInstance().changeToEmptyResult();
    }

    /**
     * 取消item点击事件
     */
    public void onCancelItemClick() {
        mSearchDataBinding.updateSearchWord("");
    }

    /**
     * 搜索item点击事件
     */
    public void onSearchItemClick() {
        String searchWord = mSearchDataBinding.searchWord.get();
        if (TextUtils.isEmpty(searchWord)) {
            searchWord = mSearchDataBinding.hintSearchWord.get();
            if (!TextUtils.isEmpty(searchWord)) {
                mSearchDataBinding.searchWord.set(searchWord);
            }
        }
        if (!TextUtils.isEmpty(searchWord)) {
            startSearch(searchWord);
        }
    }

    /**
     * 取消搜索页面点击事件
     */
    public void onCancelSearchFragmentClick() {

    }
    /**
     * 历史词点击事件
     */
    public void onHistoryWordItemClick(String searchWord) {
        mSearchDataBinding.updateSearchWord(searchWord);
        startSearch(searchWord);
    }

    /**
     * 布局默认点击时间
     */
    public void onLayoutClick(View view) {
        SoftInputUtils.hideSoftInput(view.getWindowToken(), view.getContext());
    }

    /**
     * 历史删除item点击事件
     */
    public void onHistoryRemoveItemClick(View view) {
        if (view == null) {
            return;
        }
        final Context context = view.getContext();
        if (context == null) {
            return;
        }
        SearchRemoveHistoryDialog dialog = new SearchRemoveHistoryDialog(context);
        dialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                removeAllSearchHistory();
            }
        });
        SoftInputUtils.hideSoftInput(view.getWindowToken(), view.getContext());
        dialog.show();
    }

    /**
     * 获取搜索live data
     *
     * @return 搜索词Live data
     */
    @NonNull
    public LiveData<String> getSearchLiveData() {
        return mSearchLiveData;
    }

    /**
     * 获取搜索错误信息live data，用于显示错误页
     *
     * @return 搜索错误信息Live data
     */
    @NonNull
    public LiveData<Integer> getSearchErrorLiveData() {
        return mSearchErrorLiveData;
    }

    /**
     * 检查历史list
     */
    public List<SearchHistoryEntity> handleCheckHistory(@NonNull List<SearchHistoryEntity> list) {
        if (list.size() <= HISTORY_MAX_LIMIT) {
            return list;
        }
        GlobalExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mHistoryRepository.deteleSulplus(list.subList(HISTORY_MAX_LIMIT, list.size()));
            }
        }, "searchTask", PRIORITY_NORMAL);
        return new ArrayList<>(list.subList(0, HISTORY_MAX_LIMIT));
    }
}
