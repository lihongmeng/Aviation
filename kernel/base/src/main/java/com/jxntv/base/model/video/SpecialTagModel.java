package com.jxntv.base.model.video;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

import com.jxntv.base.adapter.IAdapterModel;

/**
 * @author huangwei
 * date : 2021/5/25
 * desc :
 **/
public class SpecialTagModel implements IAdapterModel {

    private String id;
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

    private ObservableInt position = new ObservableInt();

    @Override
    public void setModelPosition(int position) {
        this.position.set(position);
    }

    @NonNull
    @Override
    public ObservableInt getModelPosition() {
        return position;
    }
}
