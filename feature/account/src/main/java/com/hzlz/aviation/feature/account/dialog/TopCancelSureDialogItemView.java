package com.hzlz.aviation.feature.account.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.AbstractDialogItemView;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.hzlz.aviation.library.widget.widget.AviationLinearLayout;

/**
 * 顶部有取消和确定的弹窗 item View
 *
 *
 * @since 2020-02-06 10:41
 */
public final class TopCancelSureDialogItemView extends AbstractDialogItemView
    implements View.OnClickListener {
  //<editor-fold desc="属性">
  @StyleRes
  private int mContainerStyle;
  // title
  @StyleRes
  private int mTitleStyle;
  @Nullable
  private String mTile;
  // cancel
  @StyleRes
  private int mCancelStyle;
  @Nullable
  private String mCancel;
  // sure
  @StyleRes
  private int mSureStyle;
  @Nullable
  private String mSure;

  private View mViewCancel;
  private View mViewSure;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  private TopCancelSureDialogItemView(@NonNull Builder builder) {
    mContainerStyle = builder.mContainerStyle;
    // title
    mTitleStyle = builder.mTitleStyle;
    mTile = builder.mTile;
    // cancel
    mCancelStyle = builder.mCancelStyle;
    mCancel = builder.mCancel;
    // sure
    mSureStyle = builder.mSureStyle;
    mSure = builder.mSure;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
    // 生成 View
    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, mContainerStyle);
    AviationLinearLayout container = new AviationLinearLayout(contextThemeWrapper);
    ViewGroup.LayoutParams containerParams = new ViewGroup.LayoutParams(contextThemeWrapper, null);
    container.setLayoutParams(containerParams);
    // cancel
    contextThemeWrapper = new ContextThemeWrapper(context, mCancelStyle);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
    AviationTextView textViewCancel = new AviationTextView(contextThemeWrapper);
    textViewCancel.setOnClickListener(this);
    textViewCancel.setText(mCancel);
    container.addView(textViewCancel, params);
    mViewCancel = textViewCancel;
    // title
    contextThemeWrapper = new ContextThemeWrapper(context, mTitleStyle);
    params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
    AviationTextView textViewTitle = new AviationTextView(contextThemeWrapper);
    textViewTitle.setText(mTile);
    container.addView(textViewTitle, params);
    // sure
    contextThemeWrapper = new ContextThemeWrapper(context, mSureStyle);
    params = new LinearLayout.LayoutParams(contextThemeWrapper, null);
    AviationTextView textViewSure = new AviationTextView(contextThemeWrapper);
    textViewSure.setOnClickListener(this);
    textViewSure.setText(mSure);
    container.addView(textViewSure, params);
    mViewSure = textViewSure;
    mViewSure.setEnabled(false);
    //
    return container;
  }

  @Nullable
  @Override
  public Object getCurrentValue() {
    return null;
  }

  @Override
  public void onValueChanged(boolean changed) {
    super.onValueChanged(changed);
    if (mViewSure != null) {
      mViewSure.setEnabled(changed);
    }
  }
  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onClick(@NonNull View v) {
    if (v.equals(mViewCancel)) {
      cancel();
    } else if (v.equals(mViewSure)) {
      sure();
    }
  }
  //</editor-fold>

  //<editor-fold desc="Builder">
  public static final class Builder {
    //<editor-fold desc="属性">
    @StyleRes
    private int mContainerStyle;
    // title
    @StyleRes
    private int mTitleStyle;
    @Nullable
    private String mTile;
    // cancel
    @StyleRes
    private int mCancelStyle;
    @Nullable
    private String mCancel;
    // sure
    @StyleRes
    private int mSureStyle;
    @Nullable
    private String mSure;
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public Builder() {
      mContainerStyle = R.style.TopCancelSureStyle;
      mTitleStyle = R.style.TopCancelSureTileStyle;
      mCancelStyle = R.style.TopCancelSureCancelStyle;
      mSureStyle = R.style.TopCancelSureSureStyle;
      mCancel = ResourcesUtils.getString(R.string.dialog_cancel);
      mSure = ResourcesUtils.getString(R.string.dialog_sure);
    }
    //</editor-fold>

    //<editor-fold desc="Setter">

    public Builder containerStyle(@StyleRes int containerStyle) {
      mContainerStyle = containerStyle;
      return this;
    }

    public Builder titleStyle(@StyleRes int titleStyle) {
      mTitleStyle = titleStyle;
      return this;
    }

    public Builder tile(@NonNull String tile) {
      mTile = tile;
      return this;
    }

    public Builder tile(@StringRes int tileRes, Object... args) {
      return tile(ResourcesUtils.getString(tileRes, args));
    }

    public Builder cancelStyle(@StyleRes int cancelStyle) {
      mCancelStyle = cancelStyle;
      return this;
    }

    public Builder cancel(@NonNull String cancel) {
      mCancel = cancel;
      return this;
    }

    public Builder cancel(@StringRes int cancelRes, Object... args) {
      return cancel(ResourcesUtils.getString(cancelRes, args));
    }

    public Builder sureStyle(@StyleRes int sureStyle) {
      mSureStyle = sureStyle;
      return this;
    }

    public Builder sure(@Nullable String sure) {
      mSure = sure;
      return this;
    }

    public Builder sure(@StringRes int sureRes, Object... args) {
      return sure(ResourcesUtils.getString(sureRes, args));
    }
    //</editor-fold>

    @NonNull
    public TopCancelSureDialogItemView build() {
      return new TopCancelSureDialogItemView(this);
    }
  }
  //</editor-fold>
}
