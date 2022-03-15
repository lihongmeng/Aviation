package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.DialogItemViewGenderBinding;
import com.hzlz.aviation.kernel.base.model.anotation.Gender;
import com.hzlz.aviation.library.widget.dialog.AbstractDialogItemView;

/**
 * 性别弹窗 item 视图
 *
 *
 * @since 2020-02-05 21:41
 */
public final class GenderDialogItemView extends AbstractDialogItemView<Integer> {
  //<editor-fold desc="属性">
  private DialogItemViewGenderBinding mBinding;
  @Gender
  private int mCurrentValue;
  @Gender
  private int mOldValue;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public GenderDialogItemView(@Gender int gender) {
    mCurrentValue = gender;
    mOldValue = gender;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
    mBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_item_view_gender,
        parent,
        false
    );
    mBinding.setView(this);
    onGenderSelected(mCurrentValue);
    return mBinding.getRoot();
  }

  public void updateOldValue(@Gender int value) {
    mOldValue = value;
  }

  @Override
  public Integer getCurrentValue() {
    return mCurrentValue;
  }

  @Override
  public boolean isCurrentValueValid() {
    return mCurrentValue != Gender.NONE;
  }

  @Override
  public boolean isValueChanged() {
    return mOldValue != mCurrentValue;
  }

  //</editor-fold>

  //<editor-fold desc="数据绑定">

  /**
   * 当男被选择
   */
  public void onMaleSelected() {
    onGenderSelected(Gender.MALE);
  }

  /**
   * 当女被选择
   */
  public void onFemaleSelected() {
    onGenderSelected(Gender.FEMALE);
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void onGenderSelected(@Gender int gender) {
    mCurrentValue = gender;
    // male
    boolean maleSelected = gender == Gender.MALE;
    mBinding.imageViewMale.setSelected(maleSelected);
    mBinding.textViewMale.setSelected(maleSelected);
    // female
    boolean femaleSelected = gender == Gender.FEMALE;
    mBinding.imageViewFemale.setSelected(femaleSelected);
    mBinding.textViewFemale.setSelected(femaleSelected);
    // 通知值发生变化
    dispatchValueChanged();
  }
  //</editor-fold>
}
