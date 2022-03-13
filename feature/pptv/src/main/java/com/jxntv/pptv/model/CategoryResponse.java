package com.jxntv.pptv.model;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import com.jxntv.base.adapter.IAdapterModel;
import java.util.List;

public final class CategoryResponse implements IAdapterModel {
  @NonNull
  private transient ObservableInt mModelPosition = new ObservableInt();
  private String categoryKey;
  private List<Category> categories;

  public String getCategoryKey() {
    return categoryKey;
  }

  public void setCategoryKey(String categoryKey) {
    this.categoryKey = categoryKey;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  @Override public void setModelPosition(int position) {
    mModelPosition.set(position);
  }

  @NonNull @Override public ObservableInt getModelPosition() {
    return mModelPosition;
  }
}
