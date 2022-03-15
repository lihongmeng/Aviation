package com.hzlz.aviation.kernel.liteav.view;

import static android.os.Build.VERSION_CODES.KITKAT;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hzlz.aviation.kernel.liteav.R;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

/**
 * 音频动画视图
 */
public class SoundWaveView extends View {

  private static final float density =
      GVideoRuntime.getAppContext().getResources().getDisplayMetrics().density;

  private static final int WAVE_NOT_START = -1;
  /** 音波高度变化过程的缓存值 */
  private static final float DP2 = dip2px(2);
  /** 音波高度变化过程的缓存值 */
  private static final float DP3 = dip2px(3);
  private static final float DP4 = dip2px(4);

  private static final float DP6 = dip2px(6);
  /** 音波高度变化过程的缓存值 */
  private static final float DP7 = dip2px(7);
  /** 音波高度变化过程的缓存值 */
  private static final float DP9 = dip2px(9);
  private static final float DP10 = dip2px(10);
  /** 音波高度变化过程的缓存值 */
  private static final float DP11 = dip2px(11);
  /** 音波高度变化过程的缓存值 */
  private static final float DP15 = dip2px(15);
  /** 音波高度变化过程的缓存值 */
  private static final float DP17 = dip2px(17);
  /** 音波动画柱形数量 */
  private static int NUMBER = 6;
  /** 音波动画计算常量 */
  private static final float DIV_250_1000 = 250f / 1000f;
  private static final float DIV_500_1000 = 500f / 1000f;
  private static final float DIV_750_1000 = 750f / 1000f;
  private static final float DIV_1000_1000 = 1f;

  /** 各类动画时间常量 */
  private static final int ANIM_DURATION = 800;
  /** 音频颜色 */
  private static final int COLOR_WAVE =
      GVideoRuntime.getAppContext().getResources().getColor(R.color.feed_wave_color);

  /** 保存每个柱形的长度、透明度和颜色的数组 */
  private float[] mWaveHeights;
  private float[] mWaveXs;
  private float[] mWaveYs;

  /** 动画Animator */
  private ValueAnimator mWaveAnimator;
  /** 波形动画绘制画笔和路径 */
  private Paint mWavePaint;
  private Path mWavePath;

  /** 柱状绘制要是用的常量(类似) */
  private float medianLine;
  private float halfWaveWidth;

  /** 当前动画时间比例,为 WAVE_NOT_START 则不进行任何绘制 */
  private float mRate;

  /** 波形柱间隔距离 */
  private float mWaveInterval;
  /** 波形柱最小宽度 */
  private float mWaveWidth;
  /** view的长宽 */
  private float mWidth;
  private float mHeight;

  /** 音波高度变化过程的单元值 */
  private float mWaveUnitPixelValue = dip2px(1);

  public SoundWaveView(Context context) {
    this(context, null, 0);
  }

  public SoundWaveView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SoundWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  /**
   * 初始化参数
   */
  private void init(Context context) {
    mRate = WAVE_NOT_START;

    mWaveInterval = context.getResources().getDimension(R.dimen.feed_sound_wave_interval);
    mWaveWidth = context.getResources().getDimension(R.dimen.feed_sound_wave_size);
    medianLine = (float) (mWaveWidth * (Math.sqrt(3.0) / 2));
    halfWaveWidth = mWaveWidth / 2f;

    mWaveHeights = new float[NUMBER];
    mWaveXs = new float[NUMBER];
    mWaveYs = new float[NUMBER];

    // 初始化paint及path

    mWavePaint = new Paint();
    mWavePaint.setAntiAlias(true);
    mWavePaint.setStyle(Paint.Style.FILL);

    mWavePath = new Path();

    initWaveAnimator();
  }

  /**
   * 初始化动画
   */
  private void initWaveAnimator() {

    mWaveAnimator = ValueAnimator.ofFloat(0, 1);
    mWaveAnimator.setInterpolator(new LinearInterpolator());
    mWaveAnimator.setDuration(ANIM_DURATION);
    mWaveAnimator.setRepeatMode(ValueAnimator.RESTART);
    mWaveAnimator.setRepeatCount(ValueAnimator.INFINITE);
    mWaveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        mRate = (float) valueAnimator.getAnimatedValue();
        postInvalidate();
      }
    });
  }

  /**
   * 开始动画
   */
  public void startWaveAnim() {
    if (mWaveAnimator == null) {
      initWaveAnimator();
    }
    if (mWaveAnimator != null && !mWaveAnimator.isRunning()) {
      mWaveAnimator.start();
    }
  }

  /**
   * 暂停动画
   *
   * @param needInitAnimator 是否需要初始化动画
   */
  public void pauseWaveAnim(boolean needInitAnimator) {
    if (mWaveAnimator != null) {
      if (Build.VERSION.SDK_INT >= KITKAT) {
        mWaveAnimator.pause();
      } else {
        mWaveAnimator.end();
      }
    } else if (needInitAnimator) {
      initWaveAnimator();
      // 触发绘制
      if (!mWaveAnimator.isRunning()) {
        mWaveAnimator.start();
      }
      if (Build.VERSION.SDK_INT >= KITKAT) {
        mWaveAnimator.pause();
      } else {
        mWaveAnimator.end();
      }
    }
    postInvalidate();
  }

  /**
   * 停止动画
   */
  public void stopWaveAnim() {
    mRate = WAVE_NOT_START;
    if (mWaveAnimator != null && mWaveAnimator.isRunning()) {
      mWaveAnimator.cancel();
    }
    postInvalidate();
  }

  /**
   * 释放动画资源
   */
  public void releaseWaveAnim() {
    if (mWaveAnimator != null) {
      mWaveAnimator.setRepeatCount(0);
      mWaveAnimator.removeAllUpdateListeners();
      mWaveAnimator.removeAllListeners();
      mWaveAnimator.cancel();
      mWaveAnimator = null;
    }
    mRate = WAVE_NOT_START;
  }

  /**
   * 获取wave总款度
   */
  private float getWaveTotalWidth() {
    return mWaveWidth * NUMBER + mWaveInterval * (NUMBER - 1) + mWaveWidth / 2;
  }

  /**
   * 获取wave总高度
   */
  private float getWaveTotalHeight() {
    return mWaveUnitPixelValue * 10 + mWaveWidth / 2;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);

    setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec)),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec)));
    if (mWidth == 0 || mHeight == 0) {
      mWidth = getMeasuredWidth();
      mHeight = getMeasuredHeight();
    }

    for (int i = 0; i < NUMBER; i++) {
      float x = (mWidth - getWaveTotalWidth()) / 2 + i * (mWaveInterval + mWaveWidth); // 提前算
      float y = mHeight - (mHeight / 2 - getWaveTotalHeight() / 2);
      mWaveXs[i] = x;
      mWaveYs[i] = y;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (mRate == WAVE_NOT_START) {
      super.onDraw(canvas);
      return;
    }

    super.onDraw(canvas);

    changeWavePropsByTime();

    drawWaves(canvas);
  }

  /**
   * 根据时间改变形状
   */
  private void changeWavePropsByTime() {
    float rate;
    if (mRate <= DIV_500_1000) {
      rate = calculateAnimRate(0, DIV_500_1000);

      mWaveHeights[0] = DP7 - DP4 * rate; // 缓存
      mWaveHeights[2] = DP2 + DP4 * rate;
      mWaveHeights[3] = DP15 - DP6 * rate;
      mWaveHeights[5] = DP9 - DP3 * rate;
    } else {
      rate = calculateAnimRate(DIV_500_1000, DIV_1000_1000);

      mWaveHeights[0] = DP3 + DP4 * rate;
      mWaveHeights[2] = DP6 - DP4 * rate;
      mWaveHeights[3] = DP9 + DP6 * rate;
      mWaveHeights[5] = DP6 + DP3 * rate;
    }

    if (mRate <= DIV_250_1000
        || (mRate > DIV_500_1000 && mRate <= DIV_750_1000)) {
      if (mRate <= DIV_250_1000) {
        rate = calculateAnimRate(0, DIV_250_1000);
      } else {
        rate = calculateAnimRate(DIV_500_1000, DIV_750_1000);
      }
      mWaveHeights[1] = DP3 + DP7 * rate;
      mWaveHeights[4] = DP6 + DP11 * rate;
    } else {
      if (mRate > DIV_750_1000) {
        rate = calculateAnimRate(DIV_750_1000, DIV_1000_1000);
      } else {
        rate = calculateAnimRate(DIV_250_1000, DIV_500_1000);
      }
      mWaveHeights[1] = DP10 - DP7 * rate;
      mWaveHeights[4] = DP17 - DP11 * rate;
    }
  }

  /**
   * 绘制波形
   *
   * @param canvas Canvas
   */
  private void drawWaves(Canvas canvas) {
    for (int i = 0; i < NUMBER; i++) {
      drawSingleWave(canvas, mWaveHeights[i], mWaveXs[i], mWaveYs[i]);
    }
  }

  /**
   * 绘制单个博波形
   */
  private void drawSingleWave(Canvas canvas, float length, float x, float y) {
    if (length >= mWaveWidth) {
      mWavePaint.setColor(COLOR_WAVE);

      float anchorX = x;
      float anchorY = y;
      mWavePath.reset();
      anchorY = anchorY - length + medianLine;
      mWavePath.moveTo(x, anchorY);
      anchorY = anchorY + length - medianLine;
      mWavePath.lineTo(anchorX, anchorY);
      anchorX += mWaveWidth;
      mWavePath.quadTo(anchorX - halfWaveWidth, anchorY + medianLine, anchorX, anchorY);
      anchorY = anchorY - length + medianLine;
      mWavePath.lineTo(anchorX, anchorY);
      anchorX -= mWaveWidth;
      mWavePath.quadTo(anchorX + halfWaveWidth, anchorY - medianLine, anchorX, anchorY);
      canvas.drawPath(mWavePath, mWavePaint);
    }
  }

  /**
   * 计算动画某段时间的比例
   *
   * @param start 起始参数
   * @param end 结束参数
   * @return 计算的时间比例
   */
  private float calculateAnimRate(float start, float end) {
    if (end == start) {
      return 0;
    }
    float rate = (mRate - start) / (end - start);
    if (rate < 0) {
      rate = 0;
    } else if (rate > 1) {
      rate = 1;
    }
    return rate;
  }

  /**
   * dip 转换为px
   */
  private static float dip2px(float dpValue) {
    return dpValue * density;
  }
}

