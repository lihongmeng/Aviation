package com.jxntv.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.widget.GVideoLinearLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 弹窗 item 视图集合抽象实现
 *
 *
 * @since 2020-02-05 21:01
 */
public abstract class AbstractDialogItemViewGroup extends AbstractDialogItemView
    implements IDialogItemViewGroup {
  //<editor-fold desc="属性">
  @NonNull
  protected List<IDialogItemView> mChildList = new ArrayList<>();
  @Nullable
  private DialogOperationCallback mCallback;
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @NonNull
  @Override
  public IDialogItemView getChild(int index) {
    return mChildList.get(index);
  }

  @Override
  public final void addItemView(@NonNull IDialogItemView itemView) {
    mChildList.add(itemView);
    itemView.setParent(this);
  }

  @NonNull
  @Override
  public View getView(@Nullable ViewGroup parent, @NonNull Context context) {
    return getView(parent, context, 0);
  }

  @Nullable
  @Override
  public Object getCurrentValue() {
    return null;
  }

  @Override
  public void dispatchValueChanged() {
    // 循环判断是否有 Item 值发生变换
    boolean changedAndValid = false;
    for (IDialogItemView itemView : mChildList) {
      changedAndValid = itemView.isValueChanged() && itemView.isCurrentValueValid();
      if (changedAndValid) {
        break;
      }
    }
    // 通知 Item 值有发生变化
    for (IDialogItemView itemView : mChildList) {
      itemView.onValueChanged(changedAndValid);
    }
  }

  @Override
  public void setDialogCallback(@NonNull DialogOperationCallback callback) {
    mCallback = callback;
  }

  @Override
  public void sure() {
    if (mCallback != null) {
      int size = mChildList.size();
      Object[] values = new Object[size];
      for (int i = 0; i < size; i++) {
        values[i] = mChildList.get(i).getCurrentValue();
      }
      mCallback.onSure(values);
    }
  }

  @Override
  public void cancel() {
    if (mCallback != null) {
      mCallback.onCancel();
    }
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  protected View getView(@Nullable ViewGroup parent, @NonNull Context context, int marginBottom) {
    GVideoLinearLayout linearLayout = new GVideoLinearLayout(context);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    // 添加子视图
    int size = mChildList.size();
    for (int i = 0; i < size; i++) {
      View child = mChildList.get(i).getView(linearLayout, context);
      linearLayout.addView(child);
    }
    // 设置 MarginBottom
    View lastView = linearLayout.getChildAt(size - 1);
    if (lastView != null) {
      ViewGroup.MarginLayoutParams params =
          (ViewGroup.MarginLayoutParams) lastView.getLayoutParams();
      params.bottomMargin += marginBottom;
      lastView.setLayoutParams(params);
    }
    return linearLayout;
  }
  //</editor-fold>
}
