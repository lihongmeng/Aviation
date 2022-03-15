package com.hzlz.aviation.library.widget.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import com.hzlz.aviation.library.widget.R;

/**
 * 标签 Item
 *
 *
 * @since 2020-01-20 12:00
 */
public final class GVideoCellView extends GVideoConstraintLayout {
  //<editor-fold desc="属性">
  @Nullable
  private GVideoImageView mImageViewLeftIcon;
  @Nullable
  private GVideoTextView mTextViewLeftText;
  @Nullable
  private GVideoTextView mTextViewRightTopText;
  @Nullable
  private GVideoTextView mTextViewRightBottomText;
  @Nullable
  private GVideoTextView mTextViewRightText;
  @Nullable
  private GVideoImageView mImageViewRightIcon;
  @Nullable
  private GVideoImageView mImageViewLeftIconPoint;

  private int mDriverColor;
  private float mDriverHeight;
  private boolean mShowTopDriver;
  private float mTopDriverMarginLeft;
  private float mTopDriverMarginRight;
  private boolean mShowBottomDriver;
  private float mBottomDriverMarginLeft;
  private float mBottomDriverMarginRight;
  private Paint mPaint;
  private boolean mShowLeftIconPoint;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public GVideoCellView(@NonNull Context context) {
    super(context);
    init(null, 0);
  }

  public GVideoCellView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public GVideoCellView(
      @NonNull Context context,
      @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, defStyleAttr);
  }
  //</editor-fold>

  //<editor-fold desc="初始化">
  private void init(@Nullable AttributeSet attrs, int defStyleAttr) {
    // 获取属性
    TypedArray ta = getContext().obtainStyledAttributes(
        attrs, R.styleable.GVideoCellView, defStyleAttr, 0
    );
    // 添加控件
    ContextThemeWrapper contextThemeWrapper;
    ConstraintLayout.LayoutParams params;
    // left icon
    boolean addLeftIcon = ta.getBoolean(R.styleable.GVideoCellView_cvAddLeftIcon, true);
    if (addLeftIcon) {
      contextThemeWrapper = new ContextThemeWrapper(
          getContext(),
          ta.getResourceId(R.styleable.GVideoCellView_cvLeftIconStyle, 0)
      );
      mImageViewLeftIcon = new GVideoImageView(contextThemeWrapper);
      mImageViewLeftIcon.setId(ViewCompat.generateViewId());
      mImageViewLeftIcon.setVisibility(
          ta.getBoolean(R.styleable.GVideoCellView_cvShowLeftIcon, true) ? VISIBLE : GONE
      );
      mImageViewLeftIcon.setImageResource(ta.getResourceId(
          R.styleable.GVideoCellView_cvLeftIcon, 0
      ));
      params = new ConstraintLayout.LayoutParams(contextThemeWrapper, null);
      params.leftToLeft = LayoutParams.PARENT_ID;
      params.topToTop = LayoutParams.PARENT_ID;
      params.bottomToBottom = LayoutParams.PARENT_ID;
      addView(mImageViewLeftIcon, params);
    }
    // left icon point
    boolean addLeftIconPoint = ta.getBoolean(R.styleable.GVideoCellView_cvAddLeftIconPoint, false);
    if (addLeftIconPoint && mImageViewLeftIcon != null) {
      contextThemeWrapper = new ContextThemeWrapper(
          getContext(),
          ta.getResourceId(R.styleable.GVideoCellView_cvLeftIconPointStyle, 0)
      );
      mImageViewLeftIconPoint = new GVideoImageView(contextThemeWrapper);
      mImageViewLeftIconPoint.setId(ViewCompat.generateViewId());
      mImageViewLeftIconPoint.setVisibility(
          ta.getBoolean(R.styleable.GVideoCellView_cvShowLeftIconPoint, false) ? VISIBLE : GONE
      );
      params = new ConstraintLayout.LayoutParams(contextThemeWrapper, null);
      params.leftToLeft = mImageViewLeftIcon.getId();
      params.topToTop = mImageViewLeftIcon.getId();
      addView(mImageViewLeftIconPoint, params);
    }
    // left text
    boolean addLeftText = ta.getBoolean(R.styleable.GVideoCellView_cvAddLeftText, true);
    if (addLeftText) {
      contextThemeWrapper = new ContextThemeWrapper(
          getContext(),
          ta.getResourceId(R.styleable.GVideoCellView_cvLeftTextStyle, 0)
      );
      mTextViewLeftText = new GVideoTextView(contextThemeWrapper);
      mTextViewLeftText.setId(ViewCompat.generateViewId());
      mTextViewLeftText.setVisibility(
          ta.getBoolean(R.styleable.GVideoCellView_cvShowLeftText, true) ? VISIBLE : GONE
      );
      mTextViewLeftText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
      mTextViewLeftText.setText(ta.getText(R.styleable.GVideoCellView_cvLeftText));
      params = new ConstraintLayout.LayoutParams(contextThemeWrapper, null);
      if (mImageViewLeftIcon != null) {
        params.leftToRight = mImageViewLeftIcon.getId();
      } else {
        params.leftToLeft = LayoutParams.PARENT_ID;
      }
      params.topToTop = LayoutParams.PARENT_ID;
      params.bottomToBottom = LayoutParams.PARENT_ID;
      addView(mTextViewLeftText, params);
    }
    // right icon
    boolean addRightIcon = ta.getBoolean(R.styleable.GVideoCellView_cvAddRightIcon, true);
    if (addRightIcon) {
      contextThemeWrapper = new ContextThemeWrapper(
          getContext(),
          ta.getResourceId(R.styleable.GVideoCellView_cvRightIconStyle, 0)
      );
      mImageViewRightIcon = new GVideoImageView(contextThemeWrapper);
      mImageViewRightIcon.setId(ViewCompat.generateViewId());
      mImageViewRightIcon.setVisibility(
          ta.getBoolean(R.styleable.GVideoCellView_cvShowRightIcon, true) ? VISIBLE : GONE
      );
      mImageViewRightIcon.setImageResource(ta.getResourceId(
          R.styleable.GVideoCellView_cvRightIcon, 0
      ));
      params = new ConstraintLayout.LayoutParams(contextThemeWrapper, null);
      params.rightToRight = LayoutParams.PARENT_ID;
      params.topToTop = LayoutParams.PARENT_ID;
      params.bottomToBottom = LayoutParams.PARENT_ID;
      addView(mImageViewRightIcon, params);
    }
    // right text
    boolean addRightText = ta.getBoolean(R.styleable.GVideoCellView_cvAddRightText, true);
    if (addRightText) {
      contextThemeWrapper = new ContextThemeWrapper(
          getContext(),
          ta.getResourceId(R.styleable.GVideoCellView_cvRightTextStyle, 0)
      );
      mTextViewRightText = new GVideoTextView(contextThemeWrapper);
      mTextViewRightText.setId(ViewCompat.generateViewId());
      mTextViewRightText.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
      mTextViewRightText.setVisibility(
          ta.getBoolean(R.styleable.GVideoCellView_cvShowRightText, true) ? VISIBLE : GONE
      );
      mTextViewRightText.setText(ta.getText(R.styleable.GVideoCellView_cvRightText));
      params = new ConstraintLayout.LayoutParams(contextThemeWrapper, null);
      if (mTextViewLeftText != null) {
        params.leftToRight = mTextViewLeftText.getId();
      } else if (mImageViewLeftIcon != null) {
        params.leftToRight = mImageViewLeftIcon.getId();
      } else {
        params.leftToLeft = LayoutParams.PARENT_ID;
      }
      if (mImageViewRightIcon != null) {
        params.rightToLeft = mImageViewRightIcon.getId();
      } else {
        params.rightToRight = LayoutParams.PARENT_ID;
      }
      params.topToTop = LayoutParams.PARENT_ID;
      params.bottomToBottom = LayoutParams.PARENT_ID;
      addView(mTextViewRightText, params);
    }
    // right top text
    boolean addRightTopText = ta.getBoolean(
        R.styleable.GVideoCellView_cvAddRightTopText, true
    );
    if (addRightTopText) {
      contextThemeWrapper = new ContextThemeWrapper(
          getContext(),
          ta.getResourceId(R.styleable.GVideoCellView_cvRightTopTextStyle, 0)
      );
      mTextViewRightTopText = new GVideoTextView(contextThemeWrapper);
      mTextViewRightTopText.setId(ViewCompat.generateViewId());
      mTextViewRightTopText.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
      mTextViewRightTopText.setVisibility(
          ta.getBoolean(R.styleable.GVideoCellView_cvShowRightTopText, true) ? VISIBLE : GONE
      );
      mTextViewRightTopText.setText(ta.getText(R.styleable.GVideoCellView_cvRightTopText));
      params = new ConstraintLayout.LayoutParams(contextThemeWrapper, null);
      if (mTextViewLeftText != null) {
        params.leftToRight = mTextViewLeftText.getId();
      } else if (mImageViewLeftIcon != null) {
        params.leftToRight = mImageViewLeftIcon.getId();
      } else {
        params.leftToLeft = LayoutParams.PARENT_ID;
      }
      if (mImageViewRightIcon != null) {
        params.rightToLeft = mImageViewRightIcon.getId();
      } else {
        params.rightToRight = LayoutParams.PARENT_ID;
      }
      params.topToTop = LayoutParams.PARENT_ID;
      params.bottomToBottom = LayoutParams.PARENT_ID;
      addView(mTextViewRightTopText, params);
    }
    // right bottom text
    boolean addRightBottomText = ta.getBoolean(
        R.styleable.GVideoCellView_cvAddRightBottomText, true
    );
    if (addRightBottomText) {
      contextThemeWrapper = new ContextThemeWrapper(
          getContext(),
          ta.getResourceId(R.styleable.GVideoCellView_cvRightBottomTextStyle, 0)
      );
      mTextViewRightBottomText = new GVideoTextView(contextThemeWrapper);
      mTextViewRightBottomText.setId(ViewCompat.generateViewId());
      mTextViewRightBottomText.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
      mTextViewRightBottomText.setVisibility(ta.getBoolean(
          R.styleable.GVideoCellView_cvShowRightBottomText, true) ? VISIBLE : GONE
      );
      mTextViewRightBottomText.setText(ta.getText(R.styleable.GVideoCellView_cvRightBottomText));
      params = new ConstraintLayout.LayoutParams(contextThemeWrapper, null);
      if (mTextViewRightTopText != null) {
        params.leftToLeft = mTextViewRightTopText.getId();
        params.rightToRight = mTextViewRightTopText.getId();
        params.topToBottom = mTextViewRightTopText.getId();
        params.bottomToBottom = LayoutParams.PARENT_ID;
        // 改变右上方文本布局
        ConstraintLayout.LayoutParams topParams =
            (LayoutParams) mTextViewRightTopText.getLayoutParams();
        topParams.bottomToBottom = LayoutParams.UNSET;
        topParams.bottomToTop = mTextViewRightBottomText.getId();
        topParams.verticalChainStyle = LayoutParams.CHAIN_SPREAD_INSIDE;
        mTextViewRightTopText.setLayoutParams(topParams);
      } else {
        if (mTextViewLeftText != null) {
          params.leftToRight = mTextViewLeftText.getId();
        } else if (mImageViewLeftIcon != null) {
          params.leftToRight = mImageViewLeftIcon.getId();
        } else {
          params.leftToLeft = LayoutParams.PARENT_ID;
        }
        if (mImageViewRightIcon != null) {
          params.rightToLeft = mImageViewRightIcon.getId();
        } else {
          params.rightToRight = LayoutParams.PARENT_ID;
        }
        params.bottomToTop = LayoutParams.PARENT_ID;
        params.bottomToBottom = LayoutParams.PARENT_ID;
      }
      addView(mTextViewRightBottomText, params);
    }
    // driver
    mDriverColor = ta.getColor(R.styleable.GVideoCellView_cvDriverColor, Color.TRANSPARENT);
    mDriverHeight = ta.getDimension(R.styleable.GVideoCellView_cvDriverHeight, 0f);
    mShowTopDriver = ta.getBoolean(R.styleable.GVideoCellView_cvShowTopDriver, false);
    mTopDriverMarginLeft = ta.getDimension(R.styleable.GVideoCellView_cvTopDriverMarginLeft, 0f);
    mTopDriverMarginRight = ta.getDimension(
        R.styleable.GVideoCellView_cvTopDriverMarginRight, 0f
    );
    mShowBottomDriver = ta.getBoolean(R.styleable.GVideoCellView_cvShowBottomDriver, false);
    mBottomDriverMarginLeft = ta.getDimension(
        R.styleable.GVideoCellView_cvBottomDriverMarginLeft, 0f
    );
    mBottomDriverMarginRight = ta.getDimension(
        R.styleable.GVideoCellView_cvBottomDriverMarginRight, 0f
    );
    ta.recycle();
    // 画笔
    setWillNotDraw(false);
    mPaint = new Paint();
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setAntiAlias(true);
    mPaint.setDither(true);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  // left icon
  @Nullable
  public ImageView getLeftIconImageView() {
    return mImageViewLeftIcon;
  }

  public void setLeftIcon(@DrawableRes int resId) {
    if (mImageViewLeftIcon != null) {
      mImageViewLeftIcon.setImageResource(resId);
    }
  }

  public void showLeftIcon(boolean show) {
    if (mImageViewLeftIcon != null) {
      mImageViewLeftIcon.setVisibility(show ? VISIBLE : GONE);
    }
  }

  // left text
  @Nullable
  public TextView getLeftTextTextView() {
    return mTextViewLeftText;
  }

  public void showLeftText(boolean show) {
    if (mTextViewLeftText != null) {
      mTextViewLeftText.setVisibility(show ? VISIBLE : GONE);
    }
  }

  public void setLeftText(@StringRes int resId, Object... args) {
    setLeftText(getContext().getString(resId, args));
  }

  public void setLeftText(@Nullable CharSequence text) {
    if (mTextViewLeftText != null) {
      mTextViewLeftText.setText(text);
    }
  }

  // right text
  @Nullable
  public TextView getRightTextTextView() {
    return mTextViewRightText;
  }

  public void showRightText(boolean show) {
    if (mTextViewRightText != null) {
      mTextViewRightText.setVisibility(show ? VISIBLE : GONE);
    }
  }

  public void setRightText(@StringRes int resId, Object... args) {
    setRightText(getContext().getString(resId, args));
  }

  public void setRightText(@Nullable CharSequence text) {
    if (mTextViewRightText != null) {
      mTextViewRightText.setText(text);
    }
  }

  // right top text
  @Nullable
  public TextView getRightTopTextTextView() {
    return mTextViewRightTopText;
  }

  public void showRightTopText(boolean show) {
    if (mTextViewRightTopText != null) {
      mTextViewRightTopText.setVisibility(show ? VISIBLE : GONE);
    }
  }

  public void setRightTopText(@StringRes int resId, Object... args) {
    setRightTopText(getContext().getString(resId, args));
  }

  public void setRightTopText(@Nullable CharSequence text) {
    if (mTextViewRightTopText != null) {
      mTextViewRightTopText.setText(text);
    }
  }

  // right bottom text
  @Nullable
  public TextView getRightBottomTextTextView() {
    return mTextViewRightBottomText;
  }

  public void showRightBottomText(boolean show) {
    if (mTextViewRightBottomText != null) {
      mTextViewRightBottomText.setVisibility(show ? VISIBLE : GONE);
    }
  }

  public void setRightBottomText(@StringRes int resId, Object... args) {
    setRightBottomText(getContext().getString(resId, args));
  }

  public void setRightBottomText(@Nullable CharSequence text) {
    if (mTextViewRightBottomText != null) {
      mTextViewRightBottomText.setText(text);
    }
  }

  // right icon
  @Nullable
  public ImageView getRightIconImageView() {
    return mImageViewRightIcon;
  }

  public void setRightIcon(@DrawableRes int resId) {
    if (mImageViewRightIcon != null) {
      mImageViewRightIcon.setImageResource(resId);
    }
  }

  public void showRightIcon(boolean show) {
    if (mImageViewRightIcon != null) {
      mImageViewRightIcon.setVisibility(show ? VISIBLE : GONE);
    }
  }

  public void showLeftIconPoint(boolean show) {
    if (mImageViewLeftIconPoint != null) {
      mImageViewLeftIconPoint.setVisibility(show ? VISIBLE : GONE);
    }
  }
  //</editor-fold>

  //<editor-fold desc="绘制">

  @Override
  protected void onDraw(@NonNull Canvas canvas) {
    super.onDraw(canvas);
    if (mDriverHeight <= 0) {
      return;
    }
    mPaint.setColor(mDriverColor);
    int width = getWidth();
    int height = getHeight();
    if (mShowTopDriver) {
      canvas.drawRect(
          mTopDriverMarginLeft,
          0,
          width - mTopDriverMarginRight,
          mDriverHeight,
          mPaint
      );
    }
    if (mShowBottomDriver) {
      canvas.drawRect(
          mBottomDriverMarginLeft,
          height - mDriverHeight,
          width - mBottomDriverMarginRight,
          height,
          mPaint
      );
    }
  }

  //</editor-fold>
}
