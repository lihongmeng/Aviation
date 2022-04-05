package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;
import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.DialogItemViewDatePickerBinding;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.AbstractDialogItemView;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期选择弹窗 Item 视图
 *
 *
 * @since 2020-02-20 20:20
 */
public final class DatePickerDialogItemView extends AbstractDialogItemView<Date> implements
    WheelDatePicker.OnDateSelectedListener {
  //<editor-fold desc="属性">
  @NonNull
  private Date mCurrentValue;
  @NonNull
  private Date mOldValue;
  @NonNull
  private Calendar mCurrentCalendar = Calendar.getInstance();
  @NonNull
  private Calendar mOldCalendar = Calendar.getInstance();
  //</editor-fold>

  public DatePickerDialogItemView(@Nullable Date date) {
    if (date != null) {
      mCurrentValue = date;
    } else {
      mCurrentValue = new Date();
    }
    mOldValue = mCurrentValue;
  }

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
    DialogItemViewDatePickerBinding binding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_item_view_date_picker,
        parent,
        false
    );
    // 设置样式
    binding.datePicker.setItemTextSize(
        (int) ResourcesUtils.getDimens(R.dimen.date_picker_item_text_size)
    );
    binding.datePicker.setItemTextColor(ResourcesUtils.getColor(R.color.color_1d1f29));
    binding.datePicker.setSelectedItemTextColor(
        ResourcesUtils.getColor(R.color.color_fc284d)
    );
    binding.datePicker.setItemSpace(
        (int) ResourcesUtils.getDimens(R.dimen.date_picker_item_space)
    );
    binding.datePicker.setAtmospheric(true);
    binding.datePicker.setCurved(true);
    // 隐藏年月份
    int betweenSpace = (int) ResourcesUtils.getDimens(R.dimen.date_picker_item_between_space);
    // year
    View viewYear = binding.datePicker.getTextViewYear();
    viewYear.setVisibility(View.INVISIBLE);
    ViewGroup.LayoutParams params = viewYear.getLayoutParams();
    params.width = betweenSpace;
    viewYear.setLayoutParams(params);
    // month
    View viewMoth = binding.datePicker.getTextViewMonth();
    viewMoth.setVisibility(View.INVISIBLE);
    params = viewMoth.getLayoutParams();
    params.width = betweenSpace;
    viewMoth.setLayoutParams(params);
    // day
    binding.datePicker.getTextViewDay().setVisibility(View.GONE);
    // 设置数据
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(mCurrentValue);
    binding.datePicker.setSelectedYear(calendar.get(Calendar.YEAR));
    binding.datePicker.setSelectedMonth(calendar.get(Calendar.MONTH) + 1);
    binding.datePicker.setSelectedDay(calendar.get(Calendar.DAY_OF_MONTH));
    // 设置监听
    binding.datePicker.setOnDateSelectedListener(this);
    return binding.getRoot();
  }

  public void updateOldValue(@NonNull Date value) {
    mOldValue = value;
  }

  @Override
  public Date getCurrentValue() {
    return mCurrentValue;
  }

  @Override
  public boolean isCurrentValueValid() {
    return true;
  }

  @Override
  public boolean isValueChanged() {
    mCurrentCalendar.setTime(mCurrentValue);
    mOldCalendar.setTime(mOldValue);
    return mCurrentCalendar.get(Calendar.YEAR) != mOldCalendar.get(Calendar.YEAR)
        || mCurrentCalendar.get(Calendar.MONTH) != mOldCalendar.get(Calendar.MONTH)
        || mCurrentCalendar.get(Calendar.DAY_OF_MONTH) != mOldCalendar.get(Calendar.DAY_OF_MONTH);
  }

  @Override
  public void onDateSelected(@NonNull WheelDatePicker picker, @NonNull Date date) {
    mCurrentValue = date;
    dispatchValueChanged();
  }
  //</editor-fold>
}
