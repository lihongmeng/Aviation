package com.hzlz.aviation.library.widget.dialog;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import com.hzlz.aviation.library.widget.R;
import com.hzlz.aviation.library.widget.widget.AviationLinearLayout;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 从底部弹出的选项弹窗
 *
 *
 * @since 2020-02-04 16:23
 */
public class GVideoBottomSheetItemDialog extends GVideoBottomSheetDialog
    implements View.OnClickListener {
  //<editor-fold desc="常量">
  private static final Integer CANCEL_TAG = Integer.MAX_VALUE;
  //</editor-fold>

  //<editor-fold desc="属性">
  // 静态属性，用于设置全局样式
  private static int sFirstItemStyleResId;
  private static int sMiddleItemStyleResId;
  private static int sLastItemStyleResId;
  private static int sOnlyOneItemStyleResId;
  private static int sDriverColor;
  private static int sCancelStyleResId;
  //
  private List<String> mItemList;
  private int mFirstItemStyleResId;
  private int mMiddleItemStyleResId;
  private int mLastItemStyleResId;
  private int mOnlyOneItemStyleResId;
  private boolean mShowItemDriver;
  private int mItemDriverColor;
  private int mItemDriverHeight;
  private int mCancelStyleResId;
  private String mCancelText;
  private int mCancelMarginTop;
  private int mCancelMarginBottom;
  @Nullable
  private OnItemSelectedListener mListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  private GVideoBottomSheetItemDialog(@NonNull Builder builder) {
    super(builder.mContext);
    processParameters(builder);
    generateView();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 处理参数
   *
   * @param builder Builder
   */
  private void processParameters(@NonNull Builder builder) {
    // item
    mItemList = builder.mItemList;
    mFirstItemStyleResId = builder.mFirstItemStyleResId;
    if (mFirstItemStyleResId == 0) {
      mFirstItemStyleResId = sFirstItemStyleResId;
    }
    if (mFirstItemStyleResId == 0) {
      mFirstItemStyleResId = R.style.BottomSheetItemDialogFirstItemStyle;
    }
    mMiddleItemStyleResId = builder.mMiddleItemStyleResId;
    if (mMiddleItemStyleResId == 0) {
      mMiddleItemStyleResId = sMiddleItemStyleResId;
    }
    if (mMiddleItemStyleResId == 0) {
      mMiddleItemStyleResId = R.style.BottomSheetItemDialogMiddleItemStyle;
    }
    mLastItemStyleResId = builder.mLastItemStyleResId;
    if (mLastItemStyleResId == 0) {
      mLastItemStyleResId = sLastItemStyleResId;
    }
    if (mLastItemStyleResId == 0) {
      mLastItemStyleResId = R.style.BottomSheetItemDialogLastItemStyle;
    }
    mOnlyOneItemStyleResId = builder.mOnlyOneItemStyleResId;
    if (mOnlyOneItemStyleResId == 0) {
      mOnlyOneItemStyleResId = sOnlyOneItemStyleResId;
    }
    if (mOnlyOneItemStyleResId == 0) {
      mOnlyOneItemStyleResId = R.style.BottomSheetItemDialogOnlyOneItemItemStyle;
    }
    // driver
    mShowItemDriver = builder.mShowItemDriver;
    mItemDriverColor = builder.mItemDriverColor;
    if (mItemDriverColor == 0) {
      mItemDriverColor = sDriverColor;
    }
    if (mItemDriverColor == 0) {
      mItemDriverColor = getContext().getResources().getColor(
          R.color.color_cccccc
      );
    }
    mItemDriverHeight = builder.mItemDriverHeight;
    // cancel
    mCancelStyleResId = builder.mCancelStyleResId;
    if (mCancelStyleResId == 0) {
      mCancelStyleResId = sCancelStyleResId;
    }
    if (mCancelStyleResId == 0) {
      mCancelStyleResId = R.style.BottomSheetItemDialogCancelStyle;
    }
    String text = builder.mCancelText;
    if (text == null) {
      text = builder.mContext.getString(R.string.dialog_cancel);
    }
    mCancelText = text;
    mCancelMarginTop = builder.mCancelMarginTop;
    mCancelMarginBottom = builder.mCancelMarginBottom;
    // 接口
    mListener = builder.mListener;
  }

  /**
   * 生成视图
   */
  private void generateView() {
    AviationLinearLayout linearLayout = new AviationLinearLayout(getContext());
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    // 循环列表添加 Item 和 分割线
    int size = mItemList.size();
    for (int i = 0; i < size; i++) {
      // 添加 item
      int style = getItemStyle(i, size);
      ContextThemeWrapper contextWrapper = new ContextThemeWrapper(getContext(), style);
      AviationTextView item = new AviationTextView(contextWrapper, null, 0);
      item.setText(mItemList.get(i));
      item.setTag(R.integer.bottom_sheet_item_dialog_tag, i);
      item.setOnClickListener(this);
      LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(contextWrapper, null);
      linearLayout.addView(item, itemParams);
      // 添加分割线
      if (mShowItemDriver && i != size - 1) {
        View driver = new View(getContext());
        LinearLayout.LayoutParams driverParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, mItemDriverHeight
        );
        driver.setBackgroundColor(mItemDriverColor);
        linearLayout.addView(driver, driverParams);
      }
    }
    // 添加取消
    ContextThemeWrapper contextWrapper = new ContextThemeWrapper(getContext(), mCancelStyleResId);
    AviationTextView cancel = new AviationTextView(contextWrapper, null, 0);
    cancel.setText(mCancelText);
    cancel.setTag(R.integer.bottom_sheet_item_dialog_tag, CANCEL_TAG);
    cancel.setOnClickListener(this);
    LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(contextWrapper, null);
    cancelParams.topMargin = mCancelMarginTop;
    cancelParams.bottomMargin = mCancelMarginBottom;
    linearLayout.addView(cancel, cancelParams);
    // 设置视图
    setContentView(linearLayout,
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    );
  }

  /**
   * 获取 Item 样式
   *
   * @param index 下标
   * @param size 大小
   * @return Item 样式
   */
  private int getItemStyle(int index, int size) {
    if (size == 1) {
      return mOnlyOneItemStyleResId;
    }
    if (index == 0) {
      return mFirstItemStyleResId;
    }
    if (index + 1 == size) {
      return mLastItemStyleResId;
    }
    return mMiddleItemStyleResId;
  }
  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onClick(@NonNull View v) {
    Object tag = v.getTag(R.integer.bottom_sheet_item_dialog_tag);
    if (tag instanceof Integer) {
      dismiss();
      if (tag.equals(CANCEL_TAG)) {
        return;
      }
      if (mListener != null) {
        mListener.onItemSelected(this, (Integer) tag);
      }
    }
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 设置全局第一个 item 样式
   *
   * @param resId 样式资源 id
   */
  public static void setFirstItemStyleResId(@StyleRes int resId) {
    sFirstItemStyleResId = resId;
  }

  /**
   * 设置全局中间 item 样式
   *
   * @param resId 样式资源 id
   */
  public static void setMiddleItemStyleResId(@StyleRes int resId) {
    sMiddleItemStyleResId = resId;
  }

  /**
   * 设置全局最后一个 item 样式
   *
   * @param resId 样式资源 id
   */
  public static void setLastItemStyleResId(@StyleRes int resId) {
    sLastItemStyleResId = resId;
  }

  /**
   * 设置全局只有一个 item 样式
   *
   * @param resId 样式资源 id
   */
  public static void setOnlyOneItemStyleResId(@StyleRes int resId) {
    sOnlyOneItemStyleResId = resId;
  }

  /**
   * 设置全局分割线颜色
   *
   * @param color 分割线颜色
   */
  public static void setDriverColor(@ColorInt int color) {
    sDriverColor = color;
  }

  /**
   * 设置全局取消样式
   *
   * @param resId 样式资源 id
   */
  public static void setCancelStyleResId(@StyleRes int resId) {
    sCancelStyleResId = resId;
  }

  //</editor-fold>

  //<editor-fold desc="接口">

  /**
   * Item 选择接口
   */
  public interface OnItemSelectedListener {
    /**
     * 当 Item 被选择回调
     *
     * @param dialog 弹窗
     * @param position 位置，从 0 开始
     */
    void onItemSelected(
        @NonNull GVideoBottomSheetItemDialog dialog,
        @IntRange(from = 0) int position
    );
  }
  //</editor-fold>

  //<editor-fold desc="Builder">
  public static final class Builder {
    //<editor-fold desc="属性">
    @NonNull
    private Context mContext;
    // item
    @NonNull
    private List<String> mItemList;
    private int mFirstItemStyleResId;
    private int mMiddleItemStyleResId;
    private int mLastItemStyleResId;
    private int mOnlyOneItemStyleResId;
    // driver
    private boolean mShowItemDriver;
    private int mItemDriverColor;
    private int mItemDriverHeight;
    // cancel
    private int mCancelStyleResId;
    @Nullable
    private String mCancelText;
    private int mCancelMarginTop;
    private int mCancelMarginBottom;
    // 接口
    @Nullable
    private OnItemSelectedListener mListener;
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public Builder(@NonNull Context context) {
      mContext = context;
      mItemList = new ArrayList<>();
      mShowItemDriver = true;
      mItemDriverHeight = 1;
      mCancelMarginTop = mContext.getResources().getDimensionPixelSize(
          R.dimen.bottom_sheet_item_dialog_cancel_margin_top
      );
      mCancelMarginBottom = mContext.getResources().getDimensionPixelSize(
          R.dimen.bottom_sheet_item_dialog_cancel_margin_bottom
      );
    }
    //</editor-fold>

    //<editor-fold desc="Setter">

    /**
     * 添加 item
     *
     * @param resId 文本资源 id
     * @param args 参数
     */
    @NonNull
    public Builder addItem(@StringRes int resId, Object... args) {
      return addItem(mContext.getString(resId, args));
    }

    /**
     * 添加 item
     *
     * @param text 文本
     */
    @NonNull
    public Builder addItem(@NonNull String text) {
      mItemList.add(text);
      return this;
    }

    /**
     * 第一个 item 样式
     *
     * @param resId 样式资源 id
     */
    @NonNull
    public Builder fistItemStyle(@StyleRes int resId) {
      mFirstItemStyleResId = resId;
      return this;
    }

    /**
     * 中间 item 样式
     *
     * @param resId 样式资源 id
     */
    @NonNull
    public Builder middleItemStyle(@StyleRes int resId) {
      mMiddleItemStyleResId = resId;
      return this;
    }

    /**
     * 最后一个 item 样式
     *
     * @param resId 样式资源 id
     */
    @NonNull
    public Builder lastItemStyle(@StyleRes int resId) {
      mLastItemStyleResId = resId;
      return this;
    }

    /**
     * 只有一个 item 样式
     *
     * @param resId 样式资源 id
     */
    @NonNull
    public Builder onlyOneItemStyle(@StyleRes int resId) {
      mOnlyOneItemStyleResId = resId;
      return this;
    }

    /**
     * 是否显示分割线
     *
     * @param show true : 显示；false : 不显示
     */
    @NonNull
    public Builder showItemDriver(boolean show) {
      mShowItemDriver = show;
      return this;
    }

    /**
     * 设置分割线颜色
     *
     * @param color 颜色
     */
    @NonNull
    public Builder itemDriverColor(@ColorInt int color) {
      mItemDriverColor = color;
      return this;
    }

    /**
     * 设置分割线高度
     *
     * @param height 高度
     */
    @NonNull
    public Builder itemDriverHeight(int height) {
      mItemDriverHeight = height;
      return this;
    }

    /**
     * 设置取消的样式
     *
     * @param resId 样式资源 id
     */
    @NonNull
    public Builder cancelStyle(@StyleRes int resId) {
      mCancelStyleResId = resId;
      return this;
    }

    /**
     * 设置取消文本
     *
     * @param resId 文本资源 id
     * @param args 参数
     */
    @NonNull
    public Builder cancel(@StringRes int resId, Object... args) {
      return cancel(mContext.getString(resId, args));
    }

    /**
     * 设置取消文本
     *
     * @param text 文本
     */
    @NonNull
    public Builder cancel(@NonNull String text) {
      mCancelText = text;
      return this;
    }

    /**
     * 设置取消按钮的 marginTop (单位 px)
     *
     * @param marginTop marginTop
     */
    @NonNull
    public Builder cancelMarginTop(int marginTop) {
      mCancelMarginTop = marginTop;
      return this;
    }

    /**
     * 设置 Item 选择接口
     *
     * @param listener 设置 Item 选择接口 {@link OnItemSelectedListener}
     */
    @NonNull
    public Builder itemSelectedListener(@NonNull OnItemSelectedListener listener) {
      mListener = listener;
      return this;
    }
    //</editor-fold>

    //<editor-fold desc="API">
    @NonNull
    public GVideoBottomSheetItemDialog build() {
      if (mItemList.isEmpty()) {
        throw new IllegalStateException("item is empty !!!");
      }
      return new GVideoBottomSheetItemDialog(this);
    }
    //</editor-fold>
  }
  //</editor-fold>
}
