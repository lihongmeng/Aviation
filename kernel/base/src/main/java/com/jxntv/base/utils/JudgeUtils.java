package com.jxntv.base.utils;

import android.view.View;

public class JudgeUtils {

    public static boolean isViewAvailable(View view) {
        return view != null && view.getContext() != null;
    }

}
