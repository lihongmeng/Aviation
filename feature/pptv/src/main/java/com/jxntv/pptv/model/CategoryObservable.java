package com.jxntv.pptv.model;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

public final class CategoryObservable{
  public ObservableField<String> name = new ObservableField<>();

  public ObservableBoolean check = new ObservableBoolean();
}
