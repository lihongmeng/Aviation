package com.hzlz.aviation.kernel.stat.stat.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.db.dao.StatDao;
import com.hzlz.aviation.kernel.stat.stat.db.dao.StatGroupDao;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatGroupEntity;

/**
 * 缓存埋点数据库
 */
@Database(entities = { StatEntity.class, StatGroupEntity.class }, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
abstract class StatRoomDatabase extends RoomDatabase {
  private static final String DB_NAME = "stat.db";

  abstract StatDao statDao();
  abstract StatGroupDao groupDao();

  static StatRoomDatabase getDatabase() {
    return Inner.INSTANCE;
  }

  private static class Inner {
    private static StatRoomDatabase INSTANCE = Room.databaseBuilder(GVideoRuntime.getAppContext(),
        StatRoomDatabase.class, DB_NAME)
        //.allowMainThreadQueries()
        //.enableMultiInstanceInvalidation()
        //.addCallback(sRoomDatabaseCallback)
        .build();
  }
}
