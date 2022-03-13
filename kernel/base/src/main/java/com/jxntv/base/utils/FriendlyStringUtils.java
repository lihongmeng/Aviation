package com.jxntv.base.utils;

import com.jxntv.base.R;
import com.jxntv.runtime.GVideoRuntime;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author huangwei
 * date : 2021/5/25
 * desc : 时间转String
 **/
public class FriendlyStringUtils {

    /**
     * 阅读数量转换
     * @param pv  阅读数
     */
    public static String friendlyPv(int pv) {
        if (pv < 10000) {
            return "阅读 " + pv;
        }
        DecimalFormat df = new DecimalFormat("#.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String pvStr = df.format(((double) pv) / 10000);
        if (pvStr.endsWith(".0")) {
            pvStr = pvStr.replace(".0", "");
        }
        return GVideoRuntime.getAppContext().getString(R.string.news_review_count, pvStr);
    }


    /**
     * 数量转换
     * @param count 数量
     * @return 1.2w
     */
    public static String friendlyW(int count) {
        if (count < 10000) {
            return count+"";
        }
        DecimalFormat df = new DecimalFormat("#.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String pvStr = df.format(((double) count) / 10000);
        if (pvStr.endsWith(".0")) {
            pvStr = pvStr.replace(".0", "");
        }
        return GVideoRuntime.getAppContext().getString(R.string.count_w, pvStr);
    }

}
