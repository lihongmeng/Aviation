package com.jxntv.media.template;

import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.ViewDataBinding;
import com.jxntv.media.model.MediaModel;

/**
 * media模板视图统一接口
 */
public interface IMediaTemplate extends View.OnClickListener {

  /**
   * media模板更新数据
   *
   * @param mediaModel media数据模型
   * @param isDarkMode 是否为暗黑模式
   * @param fragmentId 对应的fragment id
   * @param position  列表中的位置
   */
  void update(MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position);

  /**
   * 获取当前Media模型数据
   *
   * @return 当前Media模型数据
   */
  MediaModel getMediaModel();

  /**
   * 获取data binding
   *
   * @return 对应的data binding
   */
  ViewDataBinding getDataBinding();

  /**
   * 设置view group
   *
   * @param group 待设置的view group
   */
  void setViewGroup(ViewGroup group);

  void onClick(View v);

  /**
   * 该类模板提供给外部的回调监听类，模板触发了相应操作后，会回调相应接口
   *
   * @param listener 事件监听回调函数
   */
  void setOnChildViewClickListener(IMediaTemplate.OnChildViewClickListener listener);

  /**
   * 模板切换导致的页面可见回调
   */
  void onFragmentSwitchVisible();

  /**
   * 子模板点击事件监听器
   */
  interface OnChildViewClickListener {
    /**
     * 模板的某个按钮的点击事件
     *
     * @param view 被用户点击的视图控件
     */
    void onClick(View view);
  }
}
