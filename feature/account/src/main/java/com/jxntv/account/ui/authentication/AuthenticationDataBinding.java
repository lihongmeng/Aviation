package com.jxntv.account.ui.authentication;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

/**
 * 身份认证界面数据绑定
 *
 *
 * @since 2020-02-03 17:32
 */
@SuppressWarnings("FieldCanBeLocal")
public final class AuthenticationDataBinding {
  //<editor-fold desc="属性">
  // 身份证号码
  @NonNull
  public ObservableField<String> idCardNumber = new ObservableField<>();
  // 身份证正面
  @NonNull
  public ObservableField<Uri> idCardFrontUri = new ObservableField<>();
  @NonNull
  public ObservableInt idCardFrontVisibility = new ObservableInt(View.GONE);
  // 身份证背面
  @NonNull
  public ObservableField<Uri> idCardBackUri = new ObservableField<>();
  @NonNull
  public ObservableInt idCardBackVisibility = new ObservableInt(View.GONE);
  // 身份证手持
  @NonNull
  public ObservableField<Uri> idCardSelfUri = new ObservableField<>();
  @NonNull
  public ObservableInt idCardSelfVisibility = new ObservableInt(View.GONE);
  // 启用提交按钮
  @NonNull
  public ObservableBoolean enableSubmit = new ObservableBoolean();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  AuthenticationDataBinding() {
    idCardNumber.addOnPropertyChangedCallback(mPropertyChangedCallback);
    idCardFrontUri.addOnPropertyChangedCallback(mPropertyChangedCallback);
    idCardBackUri.addOnPropertyChangedCallback(mPropertyChangedCallback);
    idCardSelfUri.addOnPropertyChangedCallback(mPropertyChangedCallback);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  void setIdCardFrontUri(@NonNull Uri uri) {
    idCardFrontVisibility.set(View.VISIBLE);
    // 使用 Uri.parse 为了避免加载相同 uri(内容已经发生变化) 导致加载失败
    idCardFrontUri.set(Uri.parse(uri.toString()));
    checkSubmit();
  }

  void clearIdCardFront() {
    idCardFrontUri.set(null);
    idCardFrontVisibility.set(View.GONE);
    checkSubmit();
  }

  void setIdCardBackUri(@NonNull Uri uri) {
    idCardBackVisibility.set(View.VISIBLE);
    // 使用 Uri.parse 为了避免加载相同 uri(内容已经发生变化) 导致加载失败
    idCardBackUri.set(Uri.parse(uri.toString()));
    checkSubmit();
  }

  void clearIdCardBack() {
    idCardBackUri.set(null);
    idCardBackVisibility.set(View.GONE);
    checkSubmit();
  }

  void setIdCardSelfUri(@NonNull Uri uri) {
    idCardSelfVisibility.set(View.VISIBLE);
    // 使用 Uri.parse 为了避免加载相同 uri(内容已经发生变化) 导致加载失败
    idCardSelfUri.set(Uri.parse(uri.toString()));
    checkSubmit();
  }

  void clearIdCardSelf() {
    idCardSelfUri.set(null);
    idCardSelfVisibility.set(View.GONE);
    checkSubmit();
  }
  //</editor-fold>

  //<editor-fold desc="属性监听">
  private final Observable.OnPropertyChangedCallback mPropertyChangedCallback =
      new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
          if (idCardNumber.equals(sender)
              || idCardFrontUri.equals(sender)
              || idCardBackUri.equals(sender)
              || idCardSelfUri.equals(sender)) {
            checkSubmit();
          }
        }
      };

  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 检测提交
   */
  private void checkSubmit() {
    enableSubmit.set(enableSubmit());
  }

  /**
   * 是否启用提交
   *
   * @return true : 启用
   */
  private boolean enableSubmit() {
    return idCardNumber.get() != null
        && !TextUtils.isEmpty(idCardNumber.get())
        && idCardFrontUri.get() != null
        && idCardBackUri.get() != null
        && idCardSelfUri.get() != null;
  }
  //</editor-fold>
}
