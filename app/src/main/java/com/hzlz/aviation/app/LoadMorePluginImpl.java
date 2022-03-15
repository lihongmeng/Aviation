package com.hzlz.aviation.app;

import android.os.Bundle;
import android.text.TextUtils;

import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.LoadMorePlugin;
import com.hzlz.aviation.kernel.base.plugin.SearchPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

/**
 * 详情页中获取列表下一页推荐数据接口
 */
public class LoadMorePluginImpl implements LoadMorePlugin {

  @Override
  public Observable<ShortVideoListModel> loadMore(boolean refresh, Bundle entryBundle) {
    //getKey, getParam, getState?
    return Observable.create(new ObservableOnSubscribe<ShortVideoListModel>() {
      @Override
      public void subscribe(ObservableEmitter<ShortVideoListModel> e) throws Exception {
        if (entryBundle == null) {
          e.onComplete();
          return;
        }
        String model = entryBundle.getString(KEY_MODULE);
        if (TextUtils.isEmpty(model)) {
          e.onComplete();
          return;
        }
        Observable<ShortVideoListModel> observable = getObservable(refresh, model, entryBundle);

        if (observable == null) {
          e.onComplete();
          return;
        }

        observable.subscribe(new BaseViewModel.BaseGVideoResponseObserver<ShortVideoListModel>() {
          @Override protected void onRequestData(ShortVideoListModel shortVideoListModel) {
            e.onNext(shortVideoListModel);
          }

          @Override protected void onRequestError(Throwable throwable) {
            e.onError(throwable);
          }
        });
      }
    });
  }

  /**
   * 根据model获取对应的observable
   *
   * @param refresh 详情页首次加载数据时，会清空掉历史cursor；
   * @param model         对应的model名
   * @param entryBundle   中转数据
   * @return 对应的observable
   */
  private Observable<ShortVideoListModel> getObservable(boolean refresh, String model, Bundle entryBundle) {
    switch (model) {
      case MODULE_FEED:
        return PluginManager.get(FeedPlugin.class).loadMoreFeedData(refresh, entryBundle);
      case MODULE_SEARCH:
        return PluginManager.get(SearchPlugin.class).loadMoreSearchData(refresh, entryBundle);
      default:
        return null;
    }
  }
}
