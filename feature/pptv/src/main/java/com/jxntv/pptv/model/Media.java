package com.jxntv.pptv.model;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.model.video.VideoModel;

public final class Media extends VideoModel implements IAdapterModel {
  private transient ObservableInt mModelPosition = new ObservableInt();

  @Override
  public void setModelPosition(int position) {
    mModelPosition.set(position);
  }

  @Override
  @NonNull
  public ObservableInt getModelPosition() {
    return mModelPosition;
  }
}
