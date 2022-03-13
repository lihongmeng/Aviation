package com.jxntv.account.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.R;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.Media;
import com.jxntv.base.adapter.DataBindingViewHolder;

/**
 * 动态适配器
 *
 *
 * @since 2020-02-11 15:40
 */
public final class MomentAdapter extends AbstractMediaAdapter {
  //<editor-fold desc="属性">
  @Nullable
  private Listener mListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public MomentAdapter() {
    super();
  }
  //</editor-fold>

  //<editor-fold desc="API">

  public void setListener(@Nullable Listener listener) {
    mListener = listener;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override

  protected int getItemLayoutId() {
    return R.layout.adapter_moment;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    super.bindData(holder, position);
    Media media = mDataList.get(position);
    Author author = media.getAuthor();
    if (author != null) {
      holder.bindData(com.jxntv.account.BR.author, author.getObservable());
    }
  }
  //</editor-fold>

  //<editor-fold desc="接口">
  public interface Listener {
    void onItemClick(@NonNull View view, @NonNull MomentAdapter adapter, int position);
    void onCommentClick(@NonNull View view, @NonNull MomentAdapter adapter, int position);

    void share(@NonNull View view, @NonNull MomentAdapter adapter, int position);

    void more(@NonNull View view, @NonNull MomentAdapter adapter, int position);

    void onUserClick(@NonNull View view, @NonNull MomentAdapter adapter, int position);
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void onItemClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onItemClick(view, this, position);
    }
  }
  public void onCommentClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onCommentClick(view, this, position);
    }
  }

  public void share(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.share(view, this, position);
    }
  }

  public void more(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.more(view, this, position);
    }
  }

  public void onUserClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onUserClick(view, this, position);
    }
  }
  //</editor-fold>
}
