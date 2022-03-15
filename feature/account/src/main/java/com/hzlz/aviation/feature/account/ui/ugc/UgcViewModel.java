package com.hzlz.aviation.feature.account.ui.ugc;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * UGC 页面 ViewModel
 *
 */
public final class UgcViewModel extends UgcBaseViewModel {


  public UgcViewModel(@NonNull Application application) {
    super(application);
  }

  public void back(@NonNull View view) {
    ((Activity)view.getContext()).finish();
  }

  public UgcDataBinding getDataBinding() {
    mUgcDataBinding.setEditVisible(false);
    return mUgcDataBinding;
  }


}
