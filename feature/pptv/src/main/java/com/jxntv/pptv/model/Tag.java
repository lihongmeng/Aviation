package com.jxntv.pptv.model;

import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import com.google.gson.annotations.SerializedName;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.pptv.R;
import com.jxntv.runtime.GVideoRuntime;

public class Tag implements IAdapterModel {
  private static final @ColorRes int[] COLOR_LIST = {
      R.color.adapter_tag_title_text_color01,
      R.color.adapter_tag_title_text_color02,
      R.color.adapter_tag_title_text_color03,
      R.color.adapter_tag_title_text_color04,
      R.color.adapter_tag_title_text_color05,
      R.color.adapter_tag_title_text_color06,
  };
  private static final @DrawableRes int[] BG_LIST = {
      R.drawable.tag_bg_01,
      R.drawable.tag_bg_02,
      R.drawable.tag_bg_03,
      R.drawable.tag_bg_04,
      R.drawable.tag_bg_05,
      R.drawable.tag_bg_06,
  };
  @NonNull
  private transient ObservableInt mModelPosition = new ObservableInt();

  @SerializedName("tagKey")
  private String id;
  @SerializedName("tagName")
  private String name;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getColor() {
    int pos = getModelPosition().get();
    int color = COLOR_LIST[pos % 6];
    color = GVideoRuntime.getAppContext().getResources().getColor(color);
    return color;
  }

  public Drawable getBg() {
    int pos = getModelPosition().get();
    int bgResId = BG_LIST[pos % 6];
    Drawable bg = GVideoRuntime.getAppContext().getDrawable(bgResId);
    return bg;
  }

  @Override public void setModelPosition(int position) {
    mModelPosition.set(position);
  }

  @NonNull @Override public ObservableInt getModelPosition() {
    return mModelPosition;
  }
}
