package com.jxntv.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 可清除的 EditText
 *
 *
 * @since 2020-01-16 11:20
 */
public class GVideoClearableEditText extends GVideoEditText implements TextWatcher {
  //<editor-fold desc="属性">
  @DrawableRes
  private int mDrawableClearResourceId;
  private int mDrawableClearSize;
  @Nullable
  private BitmapDrawable mDrawableClear;
  private boolean isClearDrawableShown;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public GVideoClearableEditText(@NonNull Context context) {
    super(context);
    init(null);
  }

  public GVideoClearableEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public GVideoClearableEditText(
      @NonNull Context context,
      @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 初始化
   */
  private void init(@Nullable AttributeSet attrs) {
    // 获取属性
    TypedArray ta = getContext().obtainStyledAttributes(
        attrs, R.styleable.GVideoClearableEditText
    );
    mDrawableClearResourceId = ta.getResourceId(
        R.styleable.GVideoClearableEditText_cet_clearIcon, 0
    );
    mDrawableClearSize = ta.getDimensionPixelSize(
        R.styleable.GVideoClearableEditText_cet_clearIconSize, 0
    );
    ta.recycle();
    // 设置监听
    addTextChangedListener(this);
  }

  /**
   * 显示清除按钮
   *
   * @param show 是否显示
   */
  private void showClearDrawable(boolean show) {
    if (mDrawableClearResourceId != 0) {
      if (mDrawableClear == null) {
        mDrawableClear = createClearDrawable(mDrawableClearResourceId, mDrawableClearSize);
      }
      Drawable[] drawables = getCompoundDrawables();
      if (show) {
        setCompoundDrawablesWithIntrinsicBounds(
            drawables[0],
            drawables[1],
            mDrawableClear,
            drawables[3]
        );
      } else {
        setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
      }
    }
    isClearDrawableShown = show;
  }

  /**
   * 创建清除图标
   *
   * @param drawableResourceId 图标资源 id
   * @param size 图标大小
   * @return 清除图标
   */
  private BitmapDrawable createClearDrawable(int drawableResourceId, int size) {
    if (size <= 0) {
      return null;
    }
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResourceId);
    if (bitmap.getWidth() != size || bitmap.getHeight() != size) {
      bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
    }
    return new BitmapDrawable(getResources(), bitmap);
  }
  //</editor-fold>

  //<editor-fold desc="触摸事件处理">

  @Override
  @SuppressLint("ClickableViewAccessibility")
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    if (isClearDrawableTouched(event)) {
      // 设置文本为空
      setText(null);
      // 消费此事件
      return true;
    }
    return super.onTouchEvent(event);
  }

  /**
   * 是否触摸到清除按钮
   *
   * @param event 触摸事件
   * @return true : 触摸到了
   */
  private boolean isClearDrawableTouched(@NonNull MotionEvent event) {
    if (!isClearDrawableShown) {
      return false;
    }
    int width = getWidth();
    int paddingRight = getCompoundPaddingEnd();
    return event.getX() >= width - paddingRight;
  }

  //</editor-fold>

  //<editor-fold desc="文本时间监听">

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    showClearDrawable(!TextUtils.isEmpty(s));
  }

  @Override
  public void afterTextChanged(Editable s) {

  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 设置清除按钮
   *
   * @param clearDrawable 清除按钮资源 id
   */
  public void setClearDrawable(@DrawableRes int clearDrawable) {
    mDrawableClearResourceId = clearDrawable;
    showClearDrawable(!TextUtils.isEmpty(getText()));
  }

  //</editor-fold>

  //<editor-fold desc="生命周期">

  @Override
  public Parcelable onSaveInstanceState() {
    return new ClearParcelable(super.onSaveInstanceState(), isClearDrawableShown);
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (state instanceof ClearParcelable) {
      super.onRestoreInstanceState(((ClearParcelable) state).mBase);
      showClearDrawable(((ClearParcelable) state).mShowClearDrawable);
      return;
    }
    super.onRestoreInstanceState(state);
  }

  //</editor-fold>

  //<editor-fold desc="序列化">
  @SuppressWarnings("WeakerAccess")
  private static final class ClearParcelable implements Parcelable {
    private Parcelable mBase;
    private boolean mShowClearDrawable;

    private ClearParcelable(Parcelable base, boolean showClearDrawable) {
      mBase = base;
      mShowClearDrawable = showClearDrawable;
    }

    protected ClearParcelable(Parcel in) {
      mBase = in.readParcelable(CREATOR.getClass().getClassLoader());
      mShowClearDrawable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(mBase, flags);
      dest.writeByte((byte) (mShowClearDrawable ? 1 : 0));
    }

    @Override
    public int describeContents() {
      return 0;
    }

    public static final Creator<ClearParcelable> CREATOR = new Creator<ClearParcelable>() {
      @Override
      public ClearParcelable createFromParcel(Parcel in) {
        return new ClearParcelable(in);
      }

      @Override
      public ClearParcelable[] newArray(int size) {
        return new ClearParcelable[size];
      }
    };
  }
  //</editor-fold>
}
