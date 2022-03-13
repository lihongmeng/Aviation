package com.jxntv.test.splashdata;

import android.app.Application;
import androidx.annotation.NonNull;
import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public final class SplashDataViewModel extends BaseRefreshLoadMoreViewModel {

  private SplashDataRepository splashDataRepository = new SplashDataRepository();
  public SplashDataViewModel(@NonNull Application application) {
    super(application);
  }

  @NonNull @Override protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
    BaseDataBindingAdapter<SplashData> adapter = new SplashDataAdapter();
    return adapter;
  }

  @Override public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onRefresh(view);
    splashDataRepository.loadList()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new GVideoRefreshObserver<>(view));
  }

  @Override public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onLoadMore(view);
  }
}
