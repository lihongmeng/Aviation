package com.jxntv.account.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.R;

/**
 * Pgc 视频列表
 *
 *
 * @since 2020-02-17 21:18
 */
public final class PgcAdapter extends AbstractMediaAdapter {
  //<editor-fold desc="属性">
  @Nullable
  private Listener mListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public PgcAdapter() {
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
    return R.layout.adapter_pgc;
  }

  //</editor-fold>

  //<editor-fold desc="接口">
  public interface Listener {
    void onItemClick(@NonNull PgcAdapter adapter, @NonNull View view, int position);

    void onComment(@NonNull PgcAdapter adapter, @NonNull View view, int position);

    void onShare(@NonNull PgcAdapter adapter, @NonNull View view, int position);

    void onFavorite(@NonNull PgcAdapter adapter, @NonNull View view, int position);
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void onItemClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onItemClick(this, view, position);
    }
  }

  public void onComment(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onComment(this, view, position);
    }
  }

  public void onShare(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onShare(this, view, position);
    }
  }

  public void favorite(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onFavorite(this, view, position);
    }
  }
  //</editor-fold>
}
