package com.hzlz.aviation.feature.test.splashdata;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

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
