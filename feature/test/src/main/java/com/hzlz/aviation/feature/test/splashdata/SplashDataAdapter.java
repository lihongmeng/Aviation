package com.hzlz.aviation.feature.test.splashdata;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.feature.test.BR;
import com.hzlz.aviation.feature.test.R;

/**
 *
 * @since 2020-02-10 21:59
 */
public final class SplashDataAdapter extends BaseDataBindingAdapter<SplashData> {
  //<editor-fold desc="属性">
  @Nullable
  private WidgetListener mWidgetListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public SplashDataAdapter() {
    super();
  }
  //</editor-fold>

  //<editor-fold desc="API">
  public void setWidgetListener(@Nullable WidgetListener widgetListener) {
    mWidgetListener = widgetListener;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  protected int getItemLayoutId() {
    return R.layout.adapter_splashdata;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    SplashData author = mDataList.get(position);
    holder.bindData(BR.data, author);
    holder.bindData(BR.adapter, this);
    holder.bindData(BR.position, author.getModelPosition());
  }
  //</editor-fold>

  //<editor-fold desc="接口">

  /**
   * 控件接口
   */
  public interface WidgetListener {
    /**
     * 当 Item 点击时回调
     *
     * @param view 被点击的 View
     * @param adapter 适配器
     * @param position 位置
     */
    void onItemClick(@NonNull View view, @NonNull SplashDataAdapter adapter, int position);

    /**
     * 点击关注时回调
     *
     * @param view 被点击的 View
     * @param adapter 适配器
     * @param position 位置
     */
    void follow(@NonNull View view, @NonNull SplashDataAdapter adapter, int position);
  }
  //</editor-fold>

  //<editor-fold desc="数据绑定">
  public void onItemClick(@NonNull View view, int position) {
    if (mWidgetListener != null) {
      mWidgetListener.onItemClick(view, this, position);
    }
  }

  public void follow(@NonNull View view, int position) {
    if (mWidgetListener != null) {
      mWidgetListener.follow(view, this, position);
    }
  }
  //</editor-fold>
}
