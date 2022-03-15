package com.hzlz.aviation.feature.community.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.BaseViewModel;

public class ModifyCircleTextInfoViewModel extends BaseViewModel {

    /**
     * 需要修改的内容类型
     */
    public int modifyType;

    /**
     * EditText长度限制
     */
    private int exitTextContentMaxLength;

    /**
     * 页面初始化时，EditText的内容
     * 用于判断用户是否有更改，如果内容没有变化，就不让提交
     */
    public String initContent = "";

    /**
     * 当前EditText内容
     */
    public String currentEditTextContent = "";

    public ModifyCircleTextInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public void setExitTextContentMaxLength(int value) {
        exitTextContentMaxLength = Math.max(value, 0);
    }

    public int getExitTextContentMaxLength() {
        return exitTextContentMaxLength;
    }


    /**
     * 修改类型
     */
    public interface ModifyType {
        /**
         * 圈子名称
         */
        int CIRCLE_NAME = 1;
        /**
         * 简介
         */
        int CIRCLE_INTRODUCTION = 2;
    }

}
