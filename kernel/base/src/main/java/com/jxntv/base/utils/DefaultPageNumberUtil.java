package com.jxntv.base.utils;

import android.text.TextUtils;

import com.jxntv.base.sharedprefs.KernelSharedPrefs;
import com.jxntv.utils.DateUtils;

/**
 * 某些信息流需要每次下拉，都展示不同的数据
 * 就需要在每次下拉刷新、获取下一页的时候将页数保存
 * 页数累加无上限
 */
public class DefaultPageNumberUtil {

    // 记录上次处理页码的时间
    // 如果超过一定时间，将页码重置
    private final String LAST_BROWSE_TIME = "last_browse_time";

    // 页码最小值
    public static final int MIN = 0;

    public void initSp(String pid) {
        KernelSharedPrefs kernelSharedPrefs = KernelSharedPrefs.getInstance();
        long lastTime = kernelSharedPrefs.getLong(LAST_BROWSE_TIME, 0);

        // 如果上次处理页码的时间不是今天，就重置
        if (DateUtils.isCurrentDay(lastTime)) {
            return;
        }
        kernelSharedPrefs.putLong(LAST_BROWSE_TIME, System.currentTimeMillis());
        kernelSharedPrefs.putInt(pid, MIN);
    }

    public int getDefaultPageNumber(String pid) {
        if (TextUtils.isEmpty(pid)) {
            return MIN;
        }
        return KernelSharedPrefs.getInstance().getInt(pid, MIN);
    }

    public void saveDefaultPageNumber(String pid, int page) {
        if (TextUtils.isEmpty(pid)) {
            return;
        }
        KernelSharedPrefs.getInstance().putInt(pid, page);

    }

}
