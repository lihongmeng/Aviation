package com.jxntv.stat.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.jxntv.stat.db.entity.StatGroupEntity;
import java.util.List;

/**
 * 通用埋点Dao
 */
@Dao
public interface StatGroupDao {
  @Query("SELECT * FROM statGroup WHERE id = :groupId")
  StatGroupEntity loadGroup(long groupId);

  @Query("SELECT * FROM statGroup")
  List<StatGroupEntity> loadGroupList();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(StatGroupEntity statEntity);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  int update(StatGroupEntity statEntity);

  @Delete
  int delete(StatGroupEntity statEntity);

  @Query("DELETE FROM statGroup WHERE id = :id")
  int deleteByGroup(long id);
}
