package com.jxntv.account.model;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

/**
 * 可观察的头像信息
 *
 *
 * @since 2020-02-24 20:52
 */
public final class AvatarInfoObservable {
  //<editor-fold desc="属性">
  @NonNull
  public ObservableField<String> url = new ObservableField<>();
  //
  @NonNull
  public ObservableInt checkVisibility = new ObservableInt(View.GONE);
  //</editor-fold>
}
