package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.annotation.PrivacyRange;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.AbstractDialogItemView;
import com.hzlz.aviation.library.widget.widget.AviationImageView;
import com.hzlz.aviation.library.widget.widget.AviationLinearLayout;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 有范围的选择弹窗 Item 视图
 *
 *
 * @since 2020-02-06 14:15
 */
public final class PrivacyRangeSelectionDialogItemView extends AbstractDialogItemView<Integer>
    implements View.OnClickListener {
  //<editor-fold desc="属性">
  @PrivacyRange
  private int mCurrentValue;
  @PrivacyRange
  private int mOldValue;

  // title
  @Nullable
  private String mTitle;
  @StyleRes
  private int mTileStyle;
  // item
  @NonNull
  private List<PrivacyItem> mPrivacyItemList;
  private int mItemStyle;
  private int mLastItemStyle;
  private int mItemTextStyle;
  private int mItemCheckStyle;
  // driver
  private int mDriverStyle;

  @Nullable
  private List<View> mItemCheckViewList;

  //
  //</editor-fold>

  //<editor-fold desc="构造函数">
  private PrivacyRangeSelectionDialogItemView(@NonNull Builder builder) {
    // title
    mTitle = builder.mTitle;
    mTileStyle = builder.mTileStyle;
    // item
    mPrivacyItemList = builder.mPrivacyItemList;
    mItemStyle = builder.mItemStyle;
    mLastItemStyle = builder.mLastItemStyle;
    mItemTextStyle = builder.mItemTextStyle;
    mItemCheckStyle = builder.mItemCheckStyle;
    // driver
    mDriverStyle = builder.mDriverStyle;
    //
    mCurrentValue = builder.mPrivacyRange;
    mOldValue = builder.mPrivacyRange;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
    return generateView(context);
  }

  private View generateView(@NonNull Context context) {
    AviationLinearLayout container = new AviationLinearLayout(context);
    container.setOrientation(LinearLayout.VERTICAL);
    container.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ));
    // title
    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, mTileStyle);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
    AviationTextView textViewTitle = new AviationTextView(contextThemeWrapper);
    textViewTitle.setText(mTitle);
    container.addView(textViewTitle, params);
    // item
    int size = mPrivacyItemList.size();
    for (int i = 0; i < size; i++) {
      // item container
      int itemStyle = getItemStyle(i, size);
      contextThemeWrapper = new ContextThemeWrapper(context, itemStyle);
      params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
      AviationLinearLayout itemContainer = new AviationLinearLayout(contextThemeWrapper);
      itemContainer.setOrientation(LinearLayout.HORIZONTAL);
      itemContainer.setTag(R.integer.privacy_range_position_tag, i);
      itemContainer.setTag(
          R.integer.privacy_range_value_tag,
          mPrivacyItemList.get(i).mPrivacyRange
      );
      itemContainer.setOnClickListener(this);
      container.addView(itemContainer, params);
      // text
      contextThemeWrapper = new ContextThemeWrapper(context, mItemTextStyle);
      params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
      AviationTextView textViewRange = new AviationTextView(contextThemeWrapper);
      textViewRange.setText(mPrivacyItemList.get(i).mPrivacy);
      itemContainer.addView(textViewRange, params);
      // image check
      contextThemeWrapper = new ContextThemeWrapper(context, mItemCheckStyle);
      params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
      AviationImageView imageViewCheck = new AviationImageView(contextThemeWrapper);
      imageViewCheck.setVisibility(
          mPrivacyItemList.get(i).mPrivacyRange == mCurrentValue ? View.VISIBLE : View.GONE
      );
      itemContainer.addView(imageViewCheck, params);
      if (mItemCheckViewList == null) {
        mItemCheckViewList = new ArrayList<>();
      }
      mItemCheckViewList.add(imageViewCheck);
      // diver
      if (i + 1 != size) {
        contextThemeWrapper = new ContextThemeWrapper(context, mDriverStyle);
        params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
        AviationTextView driver = new AviationTextView(contextThemeWrapper);
        container.addView(driver, params);
      }
    }
    //
    return container;
  }

  private int getItemStyle(int index, int size) {
    if (size == 1 || index + 1 == size) {
      return mLastItemStyle;
    }
    return mItemStyle;
  }

  public void updateOldValue(@PrivacyRange int value) {
    mOldValue = value;
  }

  @Override
  public Integer getCurrentValue() {
    return mCurrentValue;
  }

  @Override
  public boolean isValueChanged() {
    return mCurrentValue != mOldValue;
  }

  @Override
  public boolean isCurrentValueValid() {
    return mCurrentValue != PrivacyRange.NONE;
  }
  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onClick(@NonNull View v) {
    if (mItemCheckViewList == null || mItemCheckViewList.isEmpty()) {
      return;
    }
    Object position = v.getTag(R.integer.privacy_range_position_tag);
    Object value = v.getTag(R.integer.privacy_range_value_tag);
    if (position instanceof Integer && value instanceof Integer) {
      for (int i = 0; i < mItemCheckViewList.size(); i++) {
        mItemCheckViewList.get(i).setVisibility(i == (int) position ? View.VISIBLE : View.GONE);
      }
      mCurrentValue = (int) value;
      // 通知值发生变化
      dispatchValueChanged();
    }
  }
  //</editor-fold>

  //<editor-fold desc="隐私 Item">
  private static final class PrivacyItem {
    //<editor-fold desc="属性">
    @NonNull
    private String mPrivacy;
    @PrivacyRange
    private int mPrivacyRange;
    //</editor-fold>

    //<editor-fold desc="构造函数">

    private PrivacyItem(@NonNull String privacy, @PrivacyRange int privacyRange) {
      mPrivacy = privacy;
      mPrivacyRange = privacyRange;
    }

    //</editor-fold>
  }
  //</editor-fold>

  //<editor-fold desc="Builder">
  public static final class Builder {
    //<editor-fold desc="属性">
    // title
    @Nullable
    private String mTitle;
    @StyleRes
    private int mTileStyle;
    // item
    @NonNull
    private List<PrivacyItem> mPrivacyItemList;
    private int mItemTextStyle;
    private int mItemCheckStyle;
    private int mItemStyle;
    private int mLastItemStyle;
    // driver
    private int mDriverStyle;
    //
    @PrivacyRange
    private int mPrivacyRange;
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public Builder() {
      mPrivacyItemList = new ArrayList<>();
      mTileStyle = R.style.PrivacyRangeTileStyle;
      mItemStyle = R.style.PrivacyRangeItemStyle;
      mLastItemStyle = R.style.PrivacyRangeLastItemStyle;
      mItemTextStyle = R.style.PrivacyRangeItemTextStyle;
      mItemCheckStyle = R.style.PrivacyRangeItemCheckStyle;
      mDriverStyle = R.style.PrivacyRangeDriverStyle;
      mPrivacyRange = PrivacyRange.NONE;
    }
    //</editor-fold>

    //<editor-fold desc="Setter">

    @NonNull
    public Builder title(@StringRes int resId, Object... args) {
      return title(ResourcesUtils.getString(resId, args));
    }

    @NonNull
    public Builder title(@Nullable String title) {
      mTitle = title;
      return this;
    }

    @NonNull
    public Builder tileStyle(@StyleRes int tileStyle) {
      mTileStyle = tileStyle;
      return this;
    }

    @NonNull
    public Builder itemTextStyle(@StyleRes int itemTextStyle) {
      mItemTextStyle = itemTextStyle;
      return this;
    }

    @NonNull
    public Builder itemCheckStyle(@StyleRes int itemCheckStyle) {
      mItemCheckStyle = itemCheckStyle;
      return this;
    }

    @NonNull
    public Builder addPublicItem() {
      mPrivacyItemList.add(new PrivacyItem(
          ResourcesUtils.getString(R.string.privacy_range_public),
          PrivacyRange.PUBLIC
      ));
      return this;
    }

    @NonNull
    public Builder addOnlySelfItem() {
      mPrivacyItemList.add(new PrivacyItem(
          ResourcesUtils.getString(R.string.privacy_range_only_self),
          PrivacyRange.ONLY_SELF
      ));
      return this;
    }

    @NonNull
    public Builder itemStyle(@StyleRes int itemStyle) {
      mItemStyle = itemStyle;
      return this;
    }

    @NonNull
    public Builder lastItemStyle(@StyleRes int lastItemStyle) {
      mLastItemStyle = lastItemStyle;
      return this;
    }

    @NonNull
    public Builder driverStyle(@ColorInt int driverStyle) {
      mDriverStyle = driverStyle;
      return this;
    }

    @NonNull
    public Builder privacyRange(@PrivacyRange int privacyRange) {
      mPrivacyRange = privacyRange;
      return this;
    }
    //</editor-fold>

    @NonNull
    public PrivacyRangeSelectionDialogItemView build() {
      if (mPrivacyItemList.isEmpty()) {
        throw new IllegalStateException("no item");
      }
      return new PrivacyRangeSelectionDialogItemView(this);
    }
  }
  //</editor-fold>
}
