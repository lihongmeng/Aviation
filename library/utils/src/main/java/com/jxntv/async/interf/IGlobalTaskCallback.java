package com.jxntv.async.interf;

/**
 * 线程任务处理接口
 */
public interface IGlobalTaskCallback {

    /**
     * 开始执行任务回调接口
     */
    void onStartHandleTask();

    /**
     * 结束执行任务回调接口
     */
    void onEndHandleTsk();
}
