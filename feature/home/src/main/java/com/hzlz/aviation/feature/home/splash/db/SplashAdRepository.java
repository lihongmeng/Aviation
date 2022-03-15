package com.hzlz.aviation.feature.home.splash.db;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.home.splash.db.dao.SplashAdDao;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;

import java.util.List;

/**
 * splash ad 仓库类
 */
public class SplashAdRepository {
    /** 持有的单例 */
    private volatile static SplashAdRepository sInstance = null;
    /** 持有的splash dao */
    private SplashAdDao mSplashAdDao;

    /**
     * 获取单例
     */
    public static SplashAdRepository getInstance() {
        if (sInstance == null) {
            synchronized (SplashAdRepository.class) {
                if (sInstance == null) {
                    sInstance = new SplashAdRepository();
                }
            }
        }
        return sInstance;
    }

    /**
     * 构造函数
     */
    private SplashAdRepository() {
        SplashAdDatabase db = SplashAdDatabase.getDatabase();
        mSplashAdDao = db.splashAdDao();
    }

    /**
     * 从数据库读取splash list
     */
    public List<SplashAdEntity> loadSplashList() {
        return mSplashAdDao.getSplashAdModels();
    }

    /**
     * 增加splash list
     *
     * @param list  待处理的list
     */
    public void addList(@NonNull List<SplashAdEntity> list) {
        mSplashAdDao.insert(list);
    }

    /**
     *  删除splash 数据 list
     *
     *  @param entities  待处理的list
     */
    public int removeList(@NonNull List<SplashAdEntity> entities) {
        int result = mSplashAdDao.delete(entities);
        return result;
    }

    /**
     *  释放资源，供外部调用
     */
    public static void release() {
        if (sInstance != null) {
            sInstance.realRelease();
        }
    }

    /**
     *  释放资源
     */
    private void realRelease() {
        mSplashAdDao = null;
    }

}
