package com.tencent.qcloud.tuikit.tuigroup.util;


import com.jxntv.utils.LogUtils;

public class TUIGroupLog {

    private static final String PRE = "TUIGroup-";

    private static String mixTag(String tag) {
        return PRE + tag;
    }

    /**
     * 打印INFO级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void v(String strTag, String strInfo) {
        LogUtils.d(mixTag(strTag), strInfo);
    }

    /**
     * 打印DEBUG级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void d(String strTag, String strInfo) {
        LogUtils.d(mixTag(strTag), strInfo);
    }

    /**
     * 打印INFO级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void i(String strTag, String strInfo) {
        LogUtils.i(mixTag(strTag), strInfo);
    }

    /**
     * 打印WARN级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void w(String strTag, String strInfo) {
        LogUtils.d(mixTag(strTag), strInfo);
    }

    /**
     * 打印WARN级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void w(String strTag, String strInfo, Throwable e) {
        LogUtils.d(mixTag(strTag), strInfo + e.getMessage());
    }

    /**
     * 打印ERROR级别日志
     *
     * @param strTag  TAG
     * @param strInfo 消息
     */
    public static void e(String strTag, String strInfo) {
        LogUtils.e(mixTag(strTag), strInfo);
    }

}
