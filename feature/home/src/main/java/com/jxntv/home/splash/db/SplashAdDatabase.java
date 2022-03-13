package com.jxntv.home.splash.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.jxntv.home.splash.db.dao.SplashAdDao;
import com.jxntv.home.splash.db.entitiy.SplashAdEntity;
import com.jxntv.runtime.GVideoRuntime;

/**
 * 闪屏数据库
 */
@Database(entities = { SplashAdEntity.class }, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class SplashAdDatabase extends RoomDatabase {

  /** 闪屏数据库名 */
  private static final String DB_NAME = "splashAd.db";
  /** 数据库instance */
  private static volatile SplashAdDatabase INSTANCE;

  /**
   * 获取数据库接口
   */
  public static SplashAdDatabase getDatabase() {
    if (INSTANCE == null) {
      synchronized (SplashAdDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(GVideoRuntime.getAppContext(),
                  SplashAdDatabase.class, DB_NAME).build();
        }
      }
    }
    return INSTANCE;
  }

  /**
   * 搜索历史数据
   */
  public abstract SplashAdDao splashAdDao();

}
