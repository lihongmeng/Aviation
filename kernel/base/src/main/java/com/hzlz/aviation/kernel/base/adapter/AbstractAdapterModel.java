package com.hzlz.aviation.kernel.base.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

/**
 * Adapter 模型基类
 *
 * @since 2020-02-28 18:34
 */
public abstract class AbstractAdapterModel implements IAdapterModel {
    //<editor-fold desc="属性">

    private transient ObservableInt mModelPosition = new ObservableInt();
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    public void setModelPosition(int position) {
        if (mModelPosition == null) {
            mModelPosition = new ObservableInt();
        }
        mModelPosition.set(position);
    }

    @Override
    @NonNull
    public ObservableInt getModelPosition() {
        return mModelPosition;
    }
    //</editor-fold>
}
