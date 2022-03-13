package com.jxntv.account.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.R;
import com.jxntv.account.model.annotation.PrivacyRange;
import com.jxntv.base.model.anotation.Gender;
import com.jxntv.dialog.AbstractBottomSheetCompositeDialog;
import com.jxntv.dialog.AbstractDialogItemViewGroup;
import com.jxntv.utils.ResourcesUtils;

/**
 * 性别弹窗
 *
 *
 * @since 2020-02-05 23:09
 */
public final class GenderBottomSheetDialog extends AbstractBottomSheetCompositeDialog {
  //<editor-fold desc="属性">
  @Nullable
  private OnGenderSelectedListener mListener;
  @NonNull
  private GenderDialogItemViewGroup mGenderView;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public GenderBottomSheetDialog(
      @NonNull Context context,
      @Gender int gender,
      @PrivacyRange int privacyRange) {
    super(context);
    // 添加集合
    mGenderView = new GenderDialogItemViewGroup(gender, privacyRange);
    setDialogItemViewGroup(mGenderView);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 设置性别选择接口
   *
   * @param listener 性别选择接口
   */
  public void setOnGenderSelectedListener(@Nullable OnGenderSelectedListener listener) {
    mListener = listener;
  }

  /**
   * 设置性别
   *
   * @param gender 性别
   * @param privacyRange 性别范围
   */
  public void setGender(@Gender int gender, @PrivacyRange int privacyRange) {
    GenderDialogItemView genderView = (GenderDialogItemView) mGenderView.getChild(2);
    genderView.updateOldValue(gender);
    PrivacyRangeSelectionDialogItemView privacyRangeView =
        (PrivacyRangeSelectionDialogItemView) mGenderView.getChild(3);
    privacyRangeView.updateOldValue(privacyRange);
    mGenderView.dispatchValueChanged();
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public void onSure(@NonNull Object[] values) {
    dismiss();
    if (mListener != null) {
      int gender = (int) values[2];
      int range = (int) values[3];
      mListener.onSelected(this, gender, range);
    }
  }
  //</editor-fold>

  //<editor-fold desc="子类">
  private static final class GenderDialogItemViewGroup extends AbstractDialogItemViewGroup {
    private GenderDialogItemViewGroup(@Gender int gender, @PrivacyRange int privacyRange) {
      addItemView(new TopCancelSureDialogItemView.Builder().tile(R.string.select_gender).build());
      addItemView(new DriverDialogItemView());
      addItemView(new GenderDialogItemView(gender));
      addItemView(new PrivacyRangeSelectionDialogItemView.Builder()
          .title(R.string.privacy_gender)
          .addPublicItem()
          .addOnlySelfItem()
          .privacyRange(privacyRange)
          .build()
      );
    }

    //<editor-fold desc="方法实现">
    @NonNull
    @Override
    public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
      return getView(
          parent, context, ResourcesUtils.getIntDimens(R.dimen.margin_bottom_all_account_dialog)
      );
    }
    //</editor-fold>
  }
  //</editor-fold>

  //<editor-fold desc="接口">

  /**
   * 性别选择结果
   */
  public interface OnGenderSelectedListener {
    /**
     * 当选择成功回调
     *
     * @param dialog 弹窗
     * @param gender 性别 {@link Gender}
     * @param privacyRange 隐私范围{@link PrivacyRange}
     */
    void onSelected(
        @NonNull GenderBottomSheetDialog dialog,
        @Gender int gender,
        @PrivacyRange int privacyRange
    );
  }
  //</editor-fold>
}
