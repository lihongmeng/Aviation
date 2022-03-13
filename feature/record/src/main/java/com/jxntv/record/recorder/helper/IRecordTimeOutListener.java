package com.jxntv.record.recorder.helper;

/**
 * 录制计时结束监听
 */
public interface IRecordTimeOutListener {

  /**
   * 计时结束
   */
  void timeout();
  /**
   * 取消事件
   */
  void cancel();
  /**
   * 间隔更新事件
   */
  void onTimeInterval();
}
