package com.hzlz.aviation.kernel.base.utils;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.List;

public class ActivityUtils {

    //得到栈顶Activity的名字，注意此处要进行判断，Android在5.0以后Google把getRunningTasks的方法给屏蔽掉了，所以要分开处理
    private static String getTopActivityName(Context context) {
        String topActivityPackageName;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //此处要判断用户的安全权限有没有打开，如果打开了就进行获取栈顶Activity的名字的方法
        //当然，我们的要求是如果没打开就不获取了，要不然跳转会影响用户的体验
        if (isSecurityPermissionOpen(context)) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 1000 * 60 * 2;
            UsageStats recentStats = null;

            List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
            if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                return null;
            }

            for (UsageStats usageStats : queryUsageStats) {
                if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    recentStats = usageStats;
                }
            }
            topActivityPackageName = recentStats.getPackageName();
            return topActivityPackageName;
        } else {
            return null;
        }
    }

    //判断用户对应的安全权限有没有打开
    private static boolean isSecurityPermissionOpen(Context context) {
        long endTime = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getApplicationContext().getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, endTime);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }
}
