package com.jxntv.account.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.BR;
import com.jxntv.account.R;
import com.jxntv.account.model.Author;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;

/**
 *
 * @since 2020-02-10 21:59
 */
public final class FollowAdapter extends BaseDataBindingAdapter<Author> {
  //<editor-fold desc="属性">
  @Nullable
  private WidgetListener mWidgetListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public FollowAdapter() {
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
    return R.layout.adapter_folow;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    Author author = mDataList.get(position);
    holder.bindData(BR.author, author.getObservable());
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
    void onItemClick(@NonNull View view, @NonNull FollowAdapter adapter, int position);

    /**
     * 点击关注时回调
     *
     * @param view 被点击的 View
     * @param adapter 适配器
     * @param position 位置
     */
    void follow(@NonNull View view, @NonNull FollowAdapter adapter, int position);
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
