package com.jxntv.android.liteav.model;

import com.jxntv.android.liteav.LiteavConstants;

/**
 * 播放器视频布局枚举
 */
public enum GVideoPlayerRenderMode {
  //将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
  RENDER_MODE_ADJUST_RESOLUTION(LiteavConstants.RENDER_MODE_ADJUST_RESOLUTION),
  //将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
  RENDER_MODE_FILL_SCREEN(LiteavConstants.RENDER_MODE_FULL_FILL_SCREEN);
  public int mRenderMode;

  GVideoPlayerRenderMode(int mode) {
    mRenderMode = mode;
  }
}
