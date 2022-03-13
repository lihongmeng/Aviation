package com.jxntv.base.observable;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

public class TopMessageObservable implements BaseTopObservable {

    public ObservableField<Drawable> messageIconDrawable = new ObservableField<>();
    public ObservableField<Drawable> settingIconDrawable = new ObservableField<>();
    public ObservableInt unReadBgDrawable = new ObservableInt();
    public ObservableInt unReadTextColorDrawable = new ObservableInt();

    public TopMessageObservable(){

    }

    @Override
    public void updateToNormal(Context context) {

    }

    @Override
    public void updateToRed(Context context) {

    }

}
