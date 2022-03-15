package com.hzlz.aviation.feature.test.splashdata;

import com.hzlz.aviation.feature.home.splash.db.SplashAdRepository;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;
import com.hzlz.aviation.kernel.network.response.APIPage;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

class SplashDataRepository {
  public Observable<ListWithPage<SplashData>> loadList() {
    return Observable.create(new ObservableOnSubscribe<ListWithPage<SplashData>>() {
      @Override public void subscribe(ObservableEmitter<ListWithPage<SplashData>> e)
          throws Exception {

        List<SplashData> resultList = new ArrayList<>();
        List<SplashAdEntity> list = SplashAdRepository.getInstance().loadSplashList();
        if (list != null && !list.isEmpty()) {
          for (SplashAdEntity ad : list) {
            SplashData data = new SplashData(ad);
            resultList.add(data);
          }
        }

        ListWithPage<SplashData> result = new ListWithPage<>(resultList, new APIPage() {
          @Override public boolean hasNextPage() {
            return false;
          }

          @Override
          public int getTotal() {
            return 0;
          }

          @Override
          public int getPageNum() {
            return 0;
          }
        });
        e.onNext(result);
      }
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND);
  }
}
