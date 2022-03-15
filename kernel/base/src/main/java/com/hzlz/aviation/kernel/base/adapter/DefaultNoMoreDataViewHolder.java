package com.hzlz.aviation.kernel.base.adapter;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

/**
 * 没有更多数据 ViewHolder
 *
 *
 * @since 2020-03-02 20:40
 */
public final class DefaultNoMoreDataViewHolder extends DataBindingViewHolder {

  //<editor-fold desc="构造函数">
  public DefaultNoMoreDataViewHolder(@NonNull ViewGroup parent) {
    super(createView(parent));
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  public static View createView(@NonNull ViewGroup parent) {
    return createView(parent, R.string.all_nor_more_data);
  }

  public static View createView(@NonNull ViewGroup parent, int stringRes) {
    ContextThemeWrapper context = new ContextThemeWrapper(
        parent.getContext(), R.style.NoMoreDataDefaultStyle
    );
    GVideoTextView textView = new GVideoTextView(context, null);
    textView.setText(stringRes);
    textView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    ));
    return textView;
  }
  //</editor-fold>
}
