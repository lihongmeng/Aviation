package com.hzlz.aviation.kernel.base.toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Toolbar 抽象实现类
 *
 *
 * @since 2020-02-04 10:28
 */
public abstract class AbstractToolbar implements IToolbar, View.OnClickListener {
  //<editor-fold desc="属性">
  @Nullable
  private View mToolbarView;
  @Nullable
  private TextView mTextViewTitle;
  @Nullable
  private ImageView mImageViewLefBack;
  @Nullable
  private ImageView mImageViewRightOperation;
  @Nullable
  private TextView mTextViewLeftBack;
  @Nullable
  private TextView mTextViewRightOperation;
  @Nullable
  private ToolbarListener mListener;
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 获取 Toolbar View
   *
   * @param parent 父控件
   * @return Toolbar View
   */
  @NonNull
  protected abstract View getToolbarView(@NonNull ViewGroup parent);

  /**
   * 获取标题 TextView
   *
   * @return 标题 TextView
   */
  @Nullable
  protected abstract TextView getTitleTextView();

  /**
   * 获取左边返回 ImageView
   *
   * @return 左边返回 ImageView
   */
  @Nullable
  protected abstract ImageView getLeftBackImageView();


  /**
   * 获取右边功能 ImageView
   *
   * @return 右边功能 ImageView
   */
  @Nullable
  protected abstract ImageView getRightImageView();

  /**
   * 获取左边返回 TextView
   *
   * @return 左边返回 TextView
   */
  @Nullable
  protected abstract TextView getLeftBackTextView();

  /**
   * 获取右边操作 TextView
   *
   * @return 右边操作 TextView
   */
  @Nullable
  protected abstract TextView getRightOperationTextView();
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public final View getView(@NonNull ViewGroup parent) {
    if (mToolbarView == null) {
      mToolbarView = getToolbarView(parent);
      mTextViewTitle = getTitleTextView();
      mImageViewLefBack = getLeftBackImageView();
      mTextViewLeftBack = getLeftBackTextView();
      mTextViewRightOperation = getRightOperationTextView();
      mImageViewRightOperation = getRightImageView();
      // 设置监听
      if (mImageViewLefBack != null) {
        mImageViewLefBack.setOnClickListener(this);
      }
      if (mTextViewLeftBack != null) {
        mTextViewLeftBack.setOnClickListener(this);
      }
      if (mTextViewRightOperation != null) {
        mTextViewRightOperation.setOnClickListener(this);
      }
      if (mImageViewRightOperation!=null){
        mImageViewRightOperation.setOnClickListener(this);
      }
    }
    return mToolbarView;
  }

  @Override
  public void setToolbarBackgroundColor(@ColorInt int color) {
    if (mToolbarView != null) {
      mToolbarView.setBackgroundColor(color);
    }
  }

  @Override
  public void setToolbarTitle(int resId, Object... args) {
    if (mToolbarView != null) {
      setToolbarTitle(mToolbarView.getContext().getString(resId, args));
    }
  }

  @Override
  public void setToolbarTitle(@NonNull String text) {
    if (mTextViewTitle != null) {
      mTextViewTitle.setText(text);
    }
  }

  @Override
  public void setLeftBackText(int resId, Object... args) {
    if (mToolbarView != null) {
      setLeftBackText(mToolbarView.getContext().getString(resId, args));
    }
  }

  @Override
  public void setLeftBackText(@NonNull String text) {
    if (mTextViewLeftBack != null) {
      mTextViewLeftBack.setText(text);
    }
  }

  @Override
  public void showRightOperationTextView(boolean show) {
    if (mTextViewRightOperation != null) {
      mTextViewRightOperation.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }

  @Override
  public void enableRightOperationTextView(boolean enable) {
    if (mTextViewRightOperation != null) {
      mTextViewRightOperation.setEnabled(enable);
    }
  }

  @Override
  public void setRightOperationImage(int resId) {
    if (mToolbarView != null) {
      mImageViewRightOperation.setImageResource(resId);
    }
  }

  @Override
  public void showRightOperationImageView(boolean show) {
    if (mTextViewRightOperation != null) {
      mImageViewRightOperation.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }

  @Override
  public void enableRightOperationImageView(boolean enable) {
    if (mTextViewRightOperation != null) {
      mImageViewRightOperation.setEnabled(enable);
    }
  }

  @Override
  public void setRightOperationTextViewText(int resId, Object... args) {
    if (mToolbarView != null) {
      setRightOperationTextViewText(mToolbarView.getContext().getString(resId, args));
    }
  }

  @Override
  public void setRightOperationTextViewText(@NonNull String text) {
    if (mTextViewRightOperation != null) {
      mTextViewRightOperation.setText(text);
    }
  }

  @Override
  public void setToolbarListener(@NonNull ToolbarListener listener) {
    mListener = listener;
  }
  //</editor-fold>

  //<editor-fold desc="事件监听">

  @Override
  public void onClick(@NonNull View v) {
    if (mListener == null) {
      return;
    }
    if (v.equals(mImageViewLefBack) || v.equals(mTextViewLeftBack)) {
      mListener.onLeftBackPressed(v);
    } else if (v.equals(mTextViewRightOperation) || v.equals(mImageViewRightOperation)) {
      mListener.onRightOperationPressed(v);
    }
  }
  //</editor-fold>
}
