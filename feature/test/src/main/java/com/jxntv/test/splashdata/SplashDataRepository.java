package com.jxntv.test.splashdata;

import com.jxntv.home.splash.db.SplashAdRepository;
import com.jxntv.home.splash.db.entitiy.SplashAdEntity;
import com.jxntv.network.response.APIPage;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.network.schedule.GVideoSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import java.util.ArrayList;
import java.util.List;

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
