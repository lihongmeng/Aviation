package com.hzlz.aviation.kernel.stat.stat.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;

import java.util.List;

/**
 * 普通埋点Dao
 */
@Dao
public interface StatDao {
  @Query("SELECT * FROM stat WHERE groupId = :groupId ORDER BY timestamp DESC")
  List<StatEntity> loadByGroup(long groupId);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(StatEntity statEntity);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  int update(StatEntity... statEntities);

  @Delete
  int delete(StatEntity... statEntities);

  @Query("DELETE FROM stat WHERE groupId = :groupId")
  int deleteByGroup(long groupId);
}
