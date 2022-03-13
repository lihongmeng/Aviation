package com.jxntv.account.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.R;
import com.jxntv.account.model.annotation.PrivacyRange;
import com.jxntv.dialog.AbstractBottomSheetCompositeDialog;
import com.jxntv.dialog.AbstractDialogItemViewGroup;
import com.jxntv.utils.ResourcesUtils;
import java.util.Date;

/**
 * 日期选择弹窗
 *
 *
 * @since 2020-02-20 20:28
 */
public final class DatePickerBottomSheetDialog extends AbstractBottomSheetCompositeDialog {
  //<editor-fold desc="属性">
  @Nullable
  private Listener mListener;
  @NonNull
  private DatePickerItemViewGroup mDatePickerView;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public DatePickerBottomSheetDialog(
      @NonNull Context context,
      @Nullable Date date,
      @PrivacyRange int privacyRange) {
    super(context);
    mDatePickerView = new DatePickerItemViewGroup(date, privacyRange);
    setDialogItemViewGroup(mDatePickerView);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  public void setListener(@Nullable Listener listener) {
    mListener = listener;
  }

  public void setDate(@NonNull Date date, @PrivacyRange int privacyRange) {
    DatePickerDialogItemView datePickerItemView =
        (DatePickerDialogItemView) mDatePickerView.getChild(2);
    datePickerItemView.updateOldValue(date);
    PrivacyRangeSelectionDialogItemView privacyRangeItemView =
        (PrivacyRangeSelectionDialogItemView) mDatePickerView.getChild(3);
    privacyRangeItemView.updateOldValue(privacyRange);
    mDatePickerView.dispatchValueChanged();
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public void onSure(@NonNull Object[] values) {
    dismiss();
    if (mListener != null) {
      mListener.onDateAndPrivacySelected(this, (Date) values[2], (int) values[3]);
    }
  }
  //</editor-fold>

  //<editor-fold desc="弹窗视图">
  private static final class DatePickerItemViewGroup extends AbstractDialogItemViewGroup {
    //<editor-fold desc="构造函数">
    private DatePickerItemViewGroup(@Nullable Date date, @PrivacyRange int privacyRange) {
      addItemView(new TopCancelSureDialogItemView.Builder().tile(R.string.select_birthday).build());
      addItemView(new DriverDialogItemView());
      addItemView(new DatePickerDialogItemView(date));
      addItemView(new PrivacyRangeSelectionDialogItemView.Builder()
          .title(R.string.privacy_birthday)
          .addPublicItem()
          .addOnlySelfItem()
          .privacyRange(privacyRange)
          .build()
      );
    }
    //</editor-fold>

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
  public interface Listener {
    void onDateAndPrivacySelected(@NonNull DatePickerBottomSheetDialog dialog,
        @NonNull Date date,
        @PrivacyRange int privacyRange
    );
  }
  //</editor-fold>
}
