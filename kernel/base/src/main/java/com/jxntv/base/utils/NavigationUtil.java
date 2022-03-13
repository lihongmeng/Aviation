package com.jxntv.base.utils;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.navigation.Navigation;

import com.jxntv.utils.LogUtils;

public class NavigationUtil {

    private volatile static NavigationUtil singleInstance = null;

    private NavigationUtil() {
    }

    public static NavigationUtil getInstance() {
        if (singleInstance == null) {
            synchronized (NavigationUtil.class) {
                if (singleInstance == null) {
                    singleInstance = new NavigationUtil();
                }
            }
        }
        return singleInstance;
    }

    /**
     * 本方法用于处理子线程执行过程中，UI已被销毁
     * 子线程结束后，使用View而造成的Crash问题
     *
     * @param view      View
     * @param id        navigation对应的id
     * @param inclusive No idea~
     */
    public void popBackStack(View view, @IdRes int id, boolean inclusive) {
        try {
            Navigation.findNavController(view).popBackStack(id, inclusive);
        } catch (Exception exception) {
            exception.printStackTrace();
            LogUtils.d("popBackStack 异常");
        }
    }

    /**
     * 导航方法
     *
     * @param view   View
     * @param id     指定到导航的Id
     * @param bundle 携带的数据
     */
    public void navigate(View view, @IdRes int id, Bundle bundle) {
        try {
            Navigation.findNavController(view).navigate(id, bundle);
        } catch (Exception exception) {
            exception.printStackTrace();
            LogUtils.d("navigate 异常");
        }
    }

    public Boolean navigateUp(View view) {
        Boolean result = null;
        try {
            result = Navigation.findNavController(view).navigateUp();
        } catch (Exception exception) {
            exception.printStackTrace();
            LogUtils.d("navigateUp 异常");
        }
        return result;
    }

}
