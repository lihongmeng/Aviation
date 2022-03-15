package com.hzlz.aviation.library.widget.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 弹窗 item 视图接口
 *
 * @param <T> 泛型，用于表示当前 item 选择的值
 *
 * @since 2020-02-05 20:21
 */
public interface IDialogItemView<T> {
  /**
   * 获取 item 视图, 用于弹窗中显示
   *
   * @param parent 父布局
   * @param context 上下文
   * @return 视图
   */
  @NonNull
  View getView(@Nullable ViewGroup parent, @NonNull Context context);

  /**
   * 获取当前的值
   *
   * @return 当前的值
   */
  @Nullable
  T getCurrentValue();

  /**
   * 当前值是否合法
   *
   * @return true : 合法；false : 不合法
   */
  boolean isCurrentValueValid();

  /**
   * 存储的值是否变化
   *
   * @return 是否变化
   */
  boolean isValueChanged();

  /**
   * 在同组有值发生变化
   *
   * @param changed 是否发生变化
   */
  void onValueChanged(boolean changed);

  /**
   * 分发值发生了变化
   */
  void dispatchValueChanged();

  /**
   * 设置父容器
   *
   * @param parent 父容器
   */
  void setParent(@NonNull IDialogItemViewGroup parent);

  /**
   * 确定
   */
  void sure();

  /**
   * 取消
   */
  void cancel();
}
