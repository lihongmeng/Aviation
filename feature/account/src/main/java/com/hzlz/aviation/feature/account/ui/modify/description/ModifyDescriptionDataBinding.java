package com.hzlz.aviation.feature.account.ui.modify.description;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.library.util.ResourcesUtils;

/**
 * 修改个人介绍界面数据绑定
 *
 *
 * @since 2020-02-05 16:42
 */
public final class ModifyDescriptionDataBinding {
  //<editor-fold desc="属性">
  public ObservableField<String> description = new ObservableField<>();
  public ObservableField<String> counter = new ObservableField<>();
  public ObservableBoolean counterMax = new ObservableBoolean();

  private int mDescriptionMaxLength;
  @Nullable
  private String mOldDescription;

  @NonNull
  private CheckThreadLiveData<Boolean> mEnableModifyDescriptionLiveData =
      new CheckThreadLiveData<>(false);
  //</editor-fold>

  //<editor-fold desc="构造函数">
  ModifyDescriptionDataBinding() {
    mDescriptionMaxLength = ResourcesUtils.getInt(R.integer.description_max_length);
    description.addOnPropertyChangedCallback(mPropertyChangedCallback);
    counter.set("0/" + mDescriptionMaxLength);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull
  LiveData<Boolean> getEnableModifyDescriptionLiveData() {
    return mEnableModifyDescriptionLiveData;
  }

  /**
   * 设置介绍
   *
   * @param description 介绍
   */
  void setDescription(@Nullable String description) {
    mOldDescription = description;
    this.description.set(description);
  }
  //</editor-fold>

  //<editor-fold desc="数据属性变化监听">
  @SuppressWarnings("FieldCanBeLocal")
  private final Observable.OnPropertyChangedCallback mPropertyChangedCallback =
      new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
          if (description.equals(sender)) {
            updateCounter();
            checkWeatherCanModifyDescription();
          }
        }
      };

  /**
   * 更新计数
   */
  private void updateCounter() {
    String d = description.get();
    int length = d == null ? 0 : d.length();
    counter.set(length + "/" + mDescriptionMaxLength);
    counterMax.set(length >= mDescriptionMaxLength);
  }

  /**
   * 检查是否可以修改介绍
   */
  private void checkWeatherCanModifyDescription() {
    String d = description.get();
    mEnableModifyDescriptionLiveData.setValue(!TextUtils.isEmpty(d) && !d.equals(mOldDescription));
  }
  //</editor-fold>
}
