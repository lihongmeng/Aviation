package com.hzlz.aviation.library.util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class VibratorUtil {

    private volatile static VibratorUtil singleInstance = null;

    // 当前设备是否有振动器
    private boolean hasVibrator;

    // 振动器对象
    private Vibrator vibrator;

    private VibratorUtil(Activity activity) {
        if (activity == null) {
            return;
        }
        vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        hasVibrator = vibrator.hasVibrator();
    }

    public static VibratorUtil getInstance(Activity activity) {
        if (singleInstance == null) {
            synchronized (VibratorUtil.class) {
                if (singleInstance == null) {
                    singleInstance = new VibratorUtil(activity);
                }
            }
        }
        return singleInstance;
    }

    //取消震动
    public void cancel() {
        if (vibrator == null || !hasVibrator) {
            return;
        }
        vibrator.cancel();


    }

    //震动milliseconds毫秒
    public void vibrate(long milliseconds) {
        if (vibrator == null || !hasVibrator) {
            return;
        }
        vibrator.vibrate(milliseconds);
    }

    //以pattern[]方式震动
    public void vibrate(long[] pattern, int repeat) {
        if (vibrator == null || !hasVibrator) {
            return;
        }
        vibrator.vibrate(pattern, repeat);
    }

}
