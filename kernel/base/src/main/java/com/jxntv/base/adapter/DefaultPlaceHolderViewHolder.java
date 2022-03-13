package com.jxntv.base.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.R;
import com.jxntv.base.databinding.PlaceholderDefaultBinding;
import com.jxntv.base.placeholder.DefaultPlaceholderLayout;
import com.jxntv.base.placeholder.PlaceholderListener;
import com.jxntv.base.placeholder.PlaceholderType;

/**
 * 默认占位视图 ViewHolder
 *
 *
 * @since 2020-02-28 22:16
 */
public final class DefaultPlaceHolderViewHolder
    extends DataBindingViewHolder<PlaceholderDefaultBinding> {
  //<editor-fold desc="属性">
  @NonNull
  private DefaultPlaceholderLayout mPlaceholderLayout;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public DefaultPlaceHolderViewHolder(@NonNull ViewGroup parent) {
    super(parent, R.layout.placeholder_default);
    mPlaceholderLayout = new DefaultPlaceholderLayout(binding);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 设置占位视图接口
   *
   * @param listener 占位视图接口
   */
  public void setPlaceholderListener(@Nullable PlaceholderListener listener) {
    mPlaceholderLayout.setPlaceholderListener(listener);
  }

  /**
   * 更新占位视图类型
   *
   * @param type 占位视图类型
   */
  public void updatePlaceholderType(@PlaceholderType int type) {
    mPlaceholderLayout.updateType(type, false, 0, false);
  }
  //</editor-fold>
}
