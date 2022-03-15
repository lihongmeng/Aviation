package com.hzlz.aviation.feature.search.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hzlz.aviation.feature.search.db.SearchHistoryDatabase;
import com.hzlz.aviation.feature.search.db.dao.SearchHistoryDao;
import com.hzlz.aviation.feature.search.db.entity.SearchHistoryEntity;

import java.util.List;

/**
 * 搜索仓库类
 */
public class SearchHistoryRepository {

    /** 持有的历史dao */
    private SearchHistoryDao mHistoryDao;

    /**
     * 构造函数
     */
    public SearchHistoryRepository() {
        SearchHistoryDatabase db = SearchHistoryDatabase.getDatabase();
        mHistoryDao = db.searchHistoryDao();
    }

    /**
     * 读取搜索历史
     *
     * @return 搜索历史
     */
    public LiveData<List<SearchHistoryEntity>> loadSearchHistory() {
        return mHistoryDao.getSearchHistoryModels();
    }

    /**
     * 搜索状态
     *
     * @param searchWord  搜索词
     */
    public void onSearch(String searchWord) {
        if (TextUtils.isEmpty(searchWord)) {
            return;
        }
        SearchHistoryEntity entity = new SearchHistoryEntity(searchWord, System.currentTimeMillis());
        mHistoryDao.insert(entity);
    }

    /**
     * 删除所有搜索历史词条
     */
    public void removeAll() {
        mHistoryDao.deleteAll();
    }

    /**
     * 删除多余的词条
     *
     * @param entities  多余的词条
     */
    public void deteleSulplus(@NonNull List<SearchHistoryEntity> entities) {
        mHistoryDao.delete(entities);
    }

    /**
     * 删除多余的词条
     *
     * @param entities  多余的词条
     */
    public void deteleSulplus(@NonNull SearchHistoryEntity entities) {
        mHistoryDao.delete(entities);
    }
}
