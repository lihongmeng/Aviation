package com.jxntv.record.recorder.helper;

/**
 * 录制helper类接口
 */
public interface IVideoRecordHelper {
  /**
   * 开始录制
   *
   * @param listener  计时结果监听器
   */
  void startTimer(IRecordTimeOutListener listener, int totalTime, int intervalTime);
  /**
   * 停止计时
   */
  void stopTimer();
  /**
   * 取消计时
   */
  void cancelTimer();
  /**
   * 错误事件
   */
  void onError();
}
