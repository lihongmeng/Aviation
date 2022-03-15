package com.hzlz.aviation.library.util;

import android.util.Log;

/**
 * log 日志工具
 */
public class LogUtils {

    private final static String MAIN_TAG = "JINSHIPIN";
    //Log输出所在类
    private static String className;
    //Log输出所在方法
    private static String methodName;
    //Log输出所行号
    private static int lineNumber;

    /**
     * 获取输出所在位置的信息className methodName lineNumber
     *
     * @param elements
     */
    private static void getDetail(StackTraceElement[] elements) {
        className = elements[1].getFileName().split("\\.")[0];
        methodName = elements[1].getMethodName();
        lineNumber = elements[1].getLineNumber();
    }

    /**
     * 创建Log输出的基本信息
     *
     * @param log
     * @return
     */
    private static String createLog(String log) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        buffer.append(className);
        buffer.append(".java ");
        buffer.append(methodName);
        buffer.append("()");
        buffer.append(" line:");
        buffer.append(lineNumber);
        buffer.append("] ");
        buffer.append(log);
        return buffer.toString();
    }

    public static void d(String tag, String content) {
        // if (true) {
            getDetail(new Throwable().getStackTrace());
            Log.d(MAIN_TAG, tag + ":" + createLog(content));
        // }
    }

    public static void i(String tag, String content) {
        // if (true) {
            getDetail(new Throwable().getStackTrace());
            Log.i(MAIN_TAG, tag + ":" + createLog(content));
        // }
    }

    public static void w(String tag, String content) {
        // if (true) {
            getDetail(new Throwable().getStackTrace());
            Log.w(MAIN_TAG, tag + ":" + createLog(content));
        // }
    }

    public static void e(String tag, String content) {
        // if (true) {
            getDetail(new Throwable().getStackTrace());
            Log.e(MAIN_TAG, tag + ":" + createLog(content));
        // }
    }


    public static void e(String content) {
        // if (true) {
            getDetail(new Throwable().getStackTrace());
            Log.e(MAIN_TAG, createLog(content));
        // }
    }

    public static void d(String content) {
        // if (true) {
            getDetail(new Throwable().getStackTrace());
            Log.d(MAIN_TAG, createLog(content));
        // }
    }

    /**
     * 打印异常信息
     *
     * @param exception Exception
     */
    public static void printException(Exception exception) {
        if (exception == null) {
            return;
        }
        String message = exception.getMessage();
        e(message);
    }


    /**
     * 打印神策埋点
     */
    public static void printSALog(String content){
        // if (true) {
            getDetail(new Throwable().getStackTrace());
            Log.d("SA_STAT", createLog(content));
        // }
    }

}

