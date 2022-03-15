package com.hzlz.aviation.kernel.base.utils;

import android.app.Activity;
import android.content.Context;

/**
 * @author huangwei
 * date : 2021/12/24
 * desc : 工具类
 **/
public class ContextUtils {


    /**
     * 是否被销毁
     */
    public static boolean isFinishing(Context context) {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return true;
        }
        return false;
    }

}
