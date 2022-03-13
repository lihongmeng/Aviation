package com.jxntv.stat.net;

import com.jxntv.base.BaseViewModel;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.schedule.GVideoSchedulers;
import com.jxntv.stat.BuildConfig;
import com.jxntv.stat.db.StatPersistenceRepository;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.stat.db.entity.StatGroupEntity;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import java.util.List;

/**
 * 埋点数据仓库
 */
public class StatRepository extends BaseDataRepository {
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TAG = StatRepository.class.getSimpleName();

  private StatPersistenceRepository mStatRepository = new StatPersistenceRepository();

  /**
   * 实时埋点，埋点后立即上传该点；如有未上传完成点，也会上传
   */
  public void statRealtime(StatEntity statEntity) {
    Observable.create(new ObservableOnSubscribe<Long>() {
      @Override public void subscribe(ObservableEmitter<Long> e) throws Exception {
        long groupId = mStatRepository.statAndPrepareDataRealtime(statEntity);
        e.onNext(groupId);
      }
    }).flatMap(new Function<Long, ObservableSource<Boolean>>() {
      @Override public ObservableSource<Boolean> apply(Long groupId) throws Exception {
        return upload(true, false);
      }
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND).subscribe(new BaseViewModel.BaseGVideoResponseObserver<>());
  }

  /**
   * 非实时埋点，埋点后缓存到数据库中；如果数据库中已缓存点超过阈值（缺省100），则进行批量上传；如有未上传完成点，也会上传；
   */
  public void stat(StatEntity statEntity) {
    Observable.create(new ObservableOnSubscribe<Long>() {
      @Override public void subscribe(ObservableEmitter<Long> e) throws Exception {
        long groupId = mStatRepository.mStatDao.insert(statEntity);
        e.onNext(groupId);
      }
    }).flatMap(new Function<Long, ObservableSource<Boolean>>() {
      @Override public ObservableSource<Boolean> apply(Long groupId) throws Exception {
        return upload(false, false);
      }
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND).subscribe(new BaseViewModel.BaseGVideoResponseObserver<>());
  }

  /**
   * 强制上传缓存埋点
   */
  public void upload() {
    upload(false, true).subscribe(new BaseViewModel.BaseGVideoResponseObserver<>());
  }

  /**
   * 上传埋点；寻找本地数据库中缓存点
   */
  private Observable<Boolean> upload(boolean realtime, boolean forceUpload) {
    return Observable.create(new ObservableOnSubscribe<Long>() {
      @Override public void subscribe(ObservableEmitter<Long> e) throws Exception {
        if (realtime) {
          e.onNext(0L);
          return;
        }
        // 准备本地未上传数据，打标签为相同groupId为一个上传包；
        long groupId = mStatRepository.prepareData(forceUpload);
        if (groupId > 0) {
          e.onNext(groupId);
        } else {
          e.onComplete();
        }
      }
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND)
        .flatMap(new Function<Long, ObservableSource<StatGroupEntity>>() {
          @Override public ObservableSource<StatGroupEntity> apply(Long groupId) throws Exception {
            // 获取未上传group
            List<StatGroupEntity> groupEntityList = mStatRepository.mGroupDao.loadGroupList();
            return Observable.fromArray(groupEntityList.toArray(new StatGroupEntity[0]));
          }
        }).flatMap(new Function<StatGroupEntity, ObservableSource<StatResponse>>() {
          @Override public ObservableSource<StatResponse> apply(StatGroupEntity group)
              throws Exception {
            // 对每个group内容进行上传
            List<StatEntity> list = mStatRepository.mStatDao.loadByGroup(group.id);
            return upload(list, group);
          }
        }).observeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND).map(new Function<StatResponse, Boolean>() {
          @Override public Boolean apply(StatResponse s) throws Exception {
            //上传成功，删除本地数据库
            boolean result = mStatRepository.delete(s.groupId);
            return result;
          }
        });
  }

  private Observable<StatResponse> upload(List<StatEntity> list, StatGroupEntity group) {
    return new OneTimeNetworkData<StatResponse>(mEngine) {
      @Override
      protected BaseRequest<StatResponse> createRequest() {
        StatRequest request = new StatRequest(list, group);
        return request;
      }

      @Override protected void saveData(StatResponse statResponse) {
        statResponse.groupId = group.id;
      }
    }.asObservable();
  }
}
