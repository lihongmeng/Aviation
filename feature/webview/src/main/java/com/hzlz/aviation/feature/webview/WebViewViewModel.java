package com.hzlz.aviation.feature.webview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;

/**
 * WebView 界面 ViewModel
 *
 *
 * @since 2020-02-20 11:09
 */
public final class WebViewViewModel extends BaseViewModel {
  //<editor-fold desc="属性">

  // LiveData
  @NonNull
  private CheckThreadLiveData<String> mTitleLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<String> mUrlLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<String> mShowTitleLiveData = new CheckThreadLiveData<>();

  //</editor-fold>

  //<editor-fold desc="构造函数">
  public WebViewViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  // LiveData

  @NonNull
  LiveData<String> getTitleLiveData() {
    return mTitleLiveData;
  }

  @NonNull
  LiveData<String> getUrlLiveData() {
    return mUrlLiveData;
  }

  // other
  void loadData(@NonNull WebViewFragmentArgs args) {
    mTitleLiveData.setValue(args.getTitle() == null?"":args.getTitle());
    mUrlLiveData.setValue(args.getUrl());
  }
  //</editor-fold>
}
