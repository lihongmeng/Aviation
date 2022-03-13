package com.jxntv.search.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 搜索数据库实体
 */
@Entity(tableName = "search_history_table")
public class SearchHistoryEntity {

    /**
     * 搜索词
     */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "searchWord")
    private String mSearchWord;

    /**
     * 搜索时间
     */
    @ColumnInfo(name = "searchTime")
    private long mSearchTime;

    /**
     * 构造函数
     */
    public SearchHistoryEntity(@NonNull String searchWord, long searchTime) {
        mSearchWord = searchWord;
        mSearchTime = searchTime;
    }

    /**
     * 获取搜索词
     */
    @NonNull
    public String getSearchWord() {
        return mSearchWord;
    }

    /**
     * 设置搜索词
     */
    public void setSearchWord(@NonNull String searchWord) {
        this.mSearchWord = searchWord;
    }

    /**
     * 获取搜索时间
     */
    public long getSearchTime() {
        return mSearchTime;
    }

    /**
     * 设置收缩时间
     */
    public void setSearchTime(long searchTime) {
        this.mSearchTime = searchTime;
    }
}


