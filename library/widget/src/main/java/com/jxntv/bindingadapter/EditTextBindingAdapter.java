package com.jxntv.bindingadapter;

import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

/**
 * EditText 自定义 Binding Adapter
 *
 */
public final class EditTextBindingAdapter {
  @BindingAdapter(value = {
      "onEditorAction"
  }, requireAll = false)
  public static void setOnEditorActionListener(
      @NonNull EditText editText,
      @NonNull View.OnClickListener listener) {
    editText.setOnEditorActionListener((v, actionId, event) -> {
      if (actionId == v.getImeOptions()) {
        listener.onClick(v);
        return true;
      }
      return false;
    });
  }

  @BindingAdapter(value = {
      "onFocusChange"
  }, requireAll = false)
  public static void setOnFocusChangeListener(
      @NonNull EditText editText,
      @NonNull View.OnFocusChangeListener listener) {
    editText.setOnFocusChangeListener(listener);
  }
}
