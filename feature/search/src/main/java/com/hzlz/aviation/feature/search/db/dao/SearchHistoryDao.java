package com.hzlz.aviation.feature.search.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hzlz.aviation.feature.search.db.entity.SearchHistoryEntity;

import java.util.List;

/**
 * 搜索历史dao
 */
@Dao
public interface SearchHistoryDao {

    /**
     * 获取历史数据
     *
     * @return 历史数据
     */
    @Query("SELECT * FROM search_history_table order by searchTime desc")
    LiveData<List<SearchHistoryEntity>> getSearchHistoryModels();

    /**
     * 插入数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchHistoryEntity entity);

    /**
     * 删除数据列表
     */
    @Delete
    void delete(List<SearchHistoryEntity> entities);

    /**
     * 删除数据
     */
    @Delete
    void delete(SearchHistoryEntity entity);

    /**
     * 删除全部
     */
    @Query("DELETE FROM search_history_table")
    void deleteAll();
}
