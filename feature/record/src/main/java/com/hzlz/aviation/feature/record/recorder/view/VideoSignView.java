package com.hzlz.aviation.feature.record.recorder.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.feature.record.R;

public class VideoSignView extends View {

  /** view的长宽 */
  private float mWidth;
  private float mHeight;
  /** 标记最大size */
  private float mSignMaxSize = GVideoRuntime.getAppContext().getResources().getDimension(
      R.dimen.fragment_record_sign_width);
  /** 标记最大宽度 */
  private float mSignMaxWidth = mSignMaxSize - GVideoRuntime.getAppContext().getResources().getDimension(
      R.dimen.fragment_record_sign_paint_size) / 2;
  /** 画笔颜色：红色 */
  private int colorRed = GVideoRuntime.getAppContext().getResources().getColor(R.color.color_fc284d);
  /** 动画Animator */
  private ValueAnimator mChangeAnimator;
  /** 当前绘制进度 */
  private float mRate;
  /** 标记画笔 */
  private Paint mSignPaint = new Paint();

  /**
   * 构造函数
   */
  public VideoSignView(Context context) {
    this(context, null, 0);
  }

  /**
   * 构造函数
   */
  public VideoSignView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  /**
   * 构造函数
   */
  public VideoSignView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  /**
   * 初始化
   */
  private void init() {
    mSignPaint.setAntiAlias(true);
    mSignPaint.setStyle(Paint.Style.FILL);
    mSignPaint.setStrokeWidth(GVideoRuntime.getAppContext().getResources().getDimension(
        R.dimen.fragment_record_sign_paint_size));
    mSignPaint.setStrokeCap(Paint.Cap.ROUND);
    mSignPaint.setColor(colorRed);
  }

  /**
   * 更改状态动画
   *
   * @param isRecordToPause 是否从录制切换到暂停
   */
  public void startChangeStateAnim(boolean isRecordToPause) {
    if (mChangeAnimator != null) {
      mChangeAnimator.cancel();
    }
    if (isRecordToPause) {
      mChangeAnimator = ValueAnimator.ofFloat(mRate, 2);
    } else {
      mChangeAnimator = ValueAnimator.ofFloat(mRate, 0);
    }
    mChangeAnimator.setInterpolator(new LinearInterpolator());
    mChangeAnimator.setDuration(200);
    mChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        mRate = (float) valueAnimator.getAnimatedValue();
        postInvalidate();
      }
    });
    mChangeAnimator.start();
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
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mRate <= 1) {
      drawPauseSign(canvas);
    } else {
      drawPlaySign(canvas);
    }
  }

  /**
   * 绘制暂停标记
   *
   * @param canvas  绘制canvas
   */
  private void drawPauseSign(Canvas canvas) {
    float size = mSignMaxWidth * (1 - mRate);
    canvas.drawLine(mWidth / 2 - size, mHeight / 2 - size,
        mWidth / 2 - size, mHeight / 2 + size, mSignPaint);
    canvas.drawLine(mWidth / 2 + size, mHeight / 2 - size,
        mWidth / 2 + size, mHeight / 2 + size, mSignPaint);
  }

  /**
   * 绘制录制标记
   *
   * @param canvas  绘制canvas
   */
  private void drawPlaySign(Canvas canvas) {
    canvas.drawCircle(mWidth / 2, mHeight / 2,
        mSignMaxSize * (mRate - 1), mSignPaint);
  }
}
