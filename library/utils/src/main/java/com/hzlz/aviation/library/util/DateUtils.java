package com.hzlz.aviation.library.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 *
 * @since 2020-03-07 10:04
 */
public final class DateUtils {

    //<editor-fold desc="构造函数">

    public static final String CHINESE_YMD = "yyyy年MM月dd日";

    public static final String YYYY_MM_DD_T_HH_MM_SS_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String HH_MM = "HH:mm";

    private final static SimpleDateFormat defaultDateFormat = new SimpleDateFormat(YYYY_MM_DD);
    private final static SimpleDateFormat defaultDateFormatMMdd = new SimpleDateFormat("MM-dd");
    private final static SimpleDateFormat defaultDateFormatHHmm = new SimpleDateFormat("HH:mm");
    private final static SimpleDateFormat defaultDateFormatYear = new SimpleDateFormat("yyyy");
    private final static SimpleDateFormat defaultDateFormatMMddHHmm = new SimpleDateFormat("MM-dd HH:mm");
    private final static SimpleDateFormat defaultDateFormatYYMMddHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final static SimpleDateFormat defaultDateFormatAll = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
    private final static SimpleDateFormat isoDateFormat = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_SSSZ);

    //</editor-fold>

    //<editor-fold desc="时间转字符串">
    public static final String TIME_FORMAT_DEFAULT = "yyyy年MM月dd日:HH:mm:ss";
    public static final String TIME_FORMAT_MM_SS = "mm:ss";

    private DateUtils() {
        throw new IllegalStateException("no instance !!!");
    }

    public static String getChineseYMD(/*@NotNull*/ Date date) {
        return getDateSting(date, CHINESE_YMD);
    }

    @SuppressWarnings("WeakerAccess")
    public static String getDateSting(/*@NotNull*/ Date date,/*@NotNull*/ String format) {
        if (date == null || format == null) {
            throw new NullPointerException("date or format is null");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getDateSting(String date, String format) {
        if (TextUtils.isEmpty(date)
                || TextUtils.isEmpty(format)) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Long getDateLong(String dataString, String format) {
        Long result = null;
        if (TextUtils.isEmpty(dataString)
                || TextUtils.isEmpty(format)) {
            return result;
        }
        try {
            Date date = new SimpleDateFormat(format, Locale.getDefault()).parse(dataString);
            if (date == null) {
                return result;
            }
            result = date.getTime();
        } catch (Exception exception) {
            result = null;
        }
        return result;
    }

    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }

    /*
     * 获取当前时间，单位：毫秒
     */
    public static long getCurrentMillSecondTime() {
        return System.currentTimeMillis();
    }

    /**
     * 按照指定的格式，将时间从long转换为String
     *
     * @param targetTime 目标时间，单位毫秒
     * @param format     时间转换格式
     * @return 转换后的时间
     */
    public static String changeTimeLongToString(long targetTime, String format) {
        if (TextUtils.isEmpty(format)) {
            format = TIME_FORMAT_DEFAULT;
        }
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(targetTime));
    }

    /**
     * 将long类型的时间转换为“  mm'ss"  ”的格式
     * 因为其中有双引号，如果使用SimpleDateFormat会报错
     * “...IllegalArgumentException: Unterminated quote...”
     * 故使用Calendar处理
     *
     * @param targetTime 时间
     * @return mm'ss"
     */
    public static String changeTimeLongToMinSecondDot(long targetTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(targetTime));
        return calendar.get(Calendar.MINUTE) + "'" + calendar.get(Calendar.SECOND) + "\"";
    }

    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar.get(Calendar.MONTH);
    }

    public static int getWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isCurrentDay(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(new Date(time));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return currentYear == year
                && currentMonth == month
                && currentDay == day;
    }

    /**
     * 当前时间和参数时间对比，是否超过指定小时
     *
     * @param time   需要和当前时间比较的时间
     * @param number 是否超过指定小时
     * @return boolean
     */
    public static boolean isThanHour(long time, int number) {
        if (number <= 0) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        calendar.setTime(new Date(time));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (currentYear != year || currentMonth != month || currentDay != day) {
            return true;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return currentHour - hour >= number;
    }

    /**
     * 1 天毫秒数
     */
    private static final long ONE_DAY_MILLISECONDS = 86400000;// 1000 * 3600 * 24


    /**
     * 时间日期转换
     *
     * @return 根据日期返回  xx分钟前, xx小时前, 12-01, 2020-12-01
     */
    public static String friendlyTime(Date time) {
        if (null == time) {
            return "";
        }
        Calendar cal = Calendar.getInstance();

        String currentYear = defaultDateFormatYear.format(cal.getTime());
        String paramYear = defaultDateFormatYear.format(time);

        int days = (int) ((cal.getTimeInMillis() - time.getTime()) / ONE_DAY_MILLISECONDS);

        if (!currentYear.equals(paramYear)) {
            //不同年份
            return defaultDateFormat.format(time);
        } else if (days == 0) {
            //同一天
            long millis = System.currentTimeMillis() - time.getTime();
            int min = (int) (millis / 1000 / 60);
            min = Math.max(1,min);
            int hour = (int) (millis/ 1000 / 60 / 60);
            if (hour > 0){
                return hour + "小时前";
            }else {
                return min + "分钟前";
            }
        } else {
            //同一年
            return defaultDateFormatMMdd.format(time);
        }
    }


    /**
     * 时间日期转换
     *
     * @return 根据日期返回 12:00， 昨天 12：00 ， 12-01 12：00 ， 2021-12-01
     */
    public static String friendlyTime2(Date time) {
        if (null == time) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        String currentYear = defaultDateFormatYear.format(cal.getTime());
        String paramYear = defaultDateFormatYear.format(time);

        if (!currentYear.equals(paramYear)) {
            //不同年份
            return defaultDateFormat.format(time);
        } else if (getMonth(System.currentTimeMillis()) == getMonth(time.getTime())){
            int days = getDay(System.currentTimeMillis()) - getDay(time.getTime());
            if (days == 0) {
                //同一天
                return defaultDateFormatHHmm.format(time);
            } else if (days == 1) {
                //昨天
                return "昨天 " + defaultDateFormatHHmm.format(time);
            }else {
                return defaultDateFormatMMddHHmm.format(time);
            }
        }else {
            return defaultDateFormatMMddHHmm.format(time);
        }
    }


    /**
     * 日期转换
     *
     * @return 2020-12-01
     */
    public static String getDefaultTime(Date time) {
        if (null == time) {
            return "";
        }
        return defaultDateFormat.format(time);
    }


    public static String getDefaultAllTime(Date time) {
        if (null == time) {
            return "";
        }
        return defaultDateFormatAll.format(time);
    }

    public static String getDefaultDateFormatYYMMddHHmm(Date time) {
        if (null == time) {
            return "";
        }
        return defaultDateFormatYYMMddHHmm.format(time);
    }

    //</editor-fold>

}
