package com.jxntv.pptv.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;
import com.google.gson.annotations.SerializedName;
import com.jxntv.base.adapter.IAdapterModel;

public final class Category implements IAdapterModel, Parcelable {
  @NonNull
  private transient ObservableInt mModelPosition = new ObservableInt();
  @Nullable
  private transient CategoryObservable mCategoryObservable;
  @SerializedName("key")
  private String id;
  private String name;
  private String categoryKey;
  private boolean check;

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

    if (mCategoryObservable != null) {
      mCategoryObservable.name.set(name);
    }
  }

  public String getCategoryKey() {
    return categoryKey;
  }

  public void setCategoryKey(String categoryKey) {
    this.categoryKey = categoryKey;
  }

  public boolean isCheck() {
    return check;
  }

  public void setCheck(boolean check) {
    this.check = check;

    if (mCategoryObservable != null) {
      mCategoryObservable.check.set(check);
    }
  }

  public void update(@NonNull Category category) {
    setName(category.name);
    setCheck(category.check);
  }

  @NonNull
  public CategoryObservable getCategoryObservable() {
    if (mCategoryObservable == null) {
      mCategoryObservable = new CategoryObservable();
      update(this);
    }
    return mCategoryObservable;
  }

  @Override public void setModelPosition(int position) {
    mModelPosition.set(position);
  }

  @NonNull @Override public ObservableInt getModelPosition() {
    return mModelPosition;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.categoryKey);
    dest.writeByte(this.check ? (byte) 1 : (byte) 0);
  }

  public Category() {
  }

  protected Category(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.categoryKey = in.readString();
    this.check = in.readByte() != 0;
  }

  public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
    @Override public Category createFromParcel(Parcel source) {
      return new Category(source);
    }

    @Override public Category[] newArray(int size) {
      return new Category[size];
    }
  };
}
