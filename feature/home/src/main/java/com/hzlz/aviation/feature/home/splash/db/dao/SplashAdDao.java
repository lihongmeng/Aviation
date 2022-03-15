package com.hzlz.aviation.feature.home.splash.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;

import java.util.List;

/**
 * 闪屏dao
 */
@Dao
public interface SplashAdDao {

    /**
     * 获取闪屏ad数据
     *
     * @return 历史数据
     */
    @Query("SELECT * FROM splash_ad_table order by weight desc")
    List<SplashAdEntity> getSplashAdModels();

    /**
     * 插入数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<SplashAdEntity> entities);

    /**
     * 删除数据列表
     */
    @Delete
    int delete(List<SplashAdEntity> entities);
}
