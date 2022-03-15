package com.hzlz.aviation.kernel.stat.stat.db;

import com.hzlz.aviation.kernel.stat.stat.StatCommonInfo;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.dao.StatDao;
import com.hzlz.aviation.kernel.stat.stat.db.dao.StatGroupDao;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatGroupEntity;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 缓存埋点数据仓库
 */
public class StatPersistenceRepository {
  private static final int MAX_COUNT = StatConstants.STAT_MAX_COUNT;
  public final StatDao mStatDao;
  public final StatGroupDao mGroupDao;
  private StatRoomDatabase db;

  public StatPersistenceRepository() {
    db = StatRoomDatabase.getDatabase();
    mStatDao = db.statDao();
    mGroupDao = db.groupDao();
  }

  public List<StatEntity> getUploadStatList() {
    db.runInTransaction(new Callable<List<StatEntity>>() {
      @Override public List<StatEntity> call() throws Exception {
        List<StatEntity> list = db.statDao().loadByGroup(StatGroupEntity.EMPTY_ID);
        return list;
      }
    });
    return null;
  }

  /**
   * 实时埋点；
   * @param statEntity
   * @return
   */
  public long statAndPrepareDataRealtime(StatEntity statEntity) {
    return db.runInTransaction(new Callable<Long>() {
      @Override public Long call() throws Exception {
        //0. 获取基础信息，生成groupId
        StatGroupEntity group = StatCommonInfo.create(true);
        long groupId = mGroupDao.insert(group);
        //1. 具体业务点入库
        statEntity.groupId = groupId;
        mStatDao.insert(statEntity);
        return groupId;
      }
    });
  }

  /**
   * 非实时埋点
   * @param statEntity
   * @return
   */
  public long stat(StatEntity statEntity) {
    return mStatDao.insert(statEntity);
  }

  /**
   * 准备本地未上传数据
   *
   * @param force 强制上传，有多少缓存数据都上传；否则会检查100条限制
   * @return groupId. -1 标识entity入库失败；
   */
  public long prepareData(boolean force) {
    return db.runInTransaction(new Callable<Long>() {
      @Override public Long call() throws Exception {
        //1. 寻找本地数据库中尚未上传过的点
        List<StatEntity> list = mStatDao.loadByGroup(StatGroupEntity.EMPTY_ID);
        if (list != null && !list.isEmpty()) {
          if (!force) {
            int size = list.size();
            if (size < MAX_COUNT) {
              return -10L;
            }
          }

          //2. 获取基础信息，保存到group表中
          StatGroupEntity group = StatCommonInfo.create(false);
          long groupId = mGroupDao.insert(group);
          if (groupId > 0) {
            //3. 更新stat表中groupId字段，相同groupId认为是一次上传内容；
            //后续上传时相同groupId上传一次；
            for (StatEntity stat : list) {
              stat.groupId = groupId;
            }
            int updateCount = mStatDao.update(list.toArray(new StatEntity[0]));
            if (updateCount > 0) {
              return groupId;
            }
          }
        }
        return -11L;
      }
    });
  }

  public boolean delete(long groupId) {
    return db.runInTransaction(new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        int statCount = mStatDao.deleteByGroup(groupId);
        int groupCount = mGroupDao.deleteByGroup(groupId);
        return statCount > 0 && groupCount > 0;
      }
    });
  }
}
