package com.jxntv.android.liteav.model;

import com.jxntv.android.liteav.LiteavConstants;

/**
 * 播放器方向枚举
 */
public enum GVideoPlayerRenderRotation {
  RENDER_ROTATION_PORTRAIT(LiteavConstants.RENDER_ROTATION_PORTRAIT),
  RENDER_ROTATION_LANDSCAPE(LiteavConstants.RENDER_ROTATION_LANDSCAPE),
  RENDER_ROTATION_90(LiteavConstants.RENDER_ROTATION_90);

  public int mRenderRotation;

  GVideoPlayerRenderRotation(int rotation) {
    mRenderRotation = rotation;
  }
}
