package com.hzlz.aviation.feature.search.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hzlz.aviation.feature.search.SearchRuntime;
import com.hzlz.aviation.feature.search.db.dao.SearchHistoryDao;
import com.hzlz.aviation.feature.search.db.entity.SearchHistoryEntity;

/**
 * 搜索数据库
 */
@Database(entities = { SearchHistoryEntity.class }, version = 1, exportSchema = false)
public abstract class SearchHistoryDatabase extends RoomDatabase {

  /** 搜索数据库名 */
  private static final String DB_NAME = "searchHistory.db";
  /** 数据库instance */
  private static volatile SearchHistoryDatabase INSTANCE;

  /**
   * 获取数据库接口
   */
  public static SearchHistoryDatabase getDatabase() {
    if (INSTANCE == null) {
      synchronized (SearchHistoryDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(SearchRuntime.getAppContext(),
                  SearchHistoryDatabase.class, DB_NAME).build();
        }
      }
    }
    return INSTANCE;
  }

  /**
   * 搜索历史数据
   */
  public abstract SearchHistoryDao searchHistoryDao();

}
