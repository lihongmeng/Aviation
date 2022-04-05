package com.hzlz.aviation.feature.record.recorder.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.feature.record.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VideoRecordProgressView extends View {

  /** view的长宽 */
  private float mWidth;
  private float mHeight;
  /** 圆大小定位 */
  private RectF mRecordRectF;
  /** 进度条宽度 */
  private float mProgressWidth = GVideoRuntime.getAppContext().getResources().getDimension(
      R.dimen.fragment_record_progress_radius);
  /** 画笔颜色：白/红 */
  private int colorWhite = GVideoRuntime.getAppContext().getResources().getColor(R.color.color_ffffff);
  private int colorRed = GVideoRuntime.getAppContext().getResources().getColor(R.color.color_fc284d);
  /** 动画Animator：录制 */
  private ValueAnimator mRecordAnimator;
  /** 动画Animator：删除确认 */
  private ValueAnimator mDeleteConfirmAnimator;
  /** 当前动画时间比例,为 WAVE_NOT_START 则不进行任何绘制 */
  private float mRate;
  /** 删除确认动画透明度 */
  private int mDeleteConfirmAlpha = 0;
  /** 进度条画笔 */
  private Paint mProgressBarPaint = new Paint();
  /** 删除画笔 */
  private Paint mProgressPausePaint = new Paint();
  /** 进度条删除确认画笔 */
  private Paint mProgressPauseConfirmPaint = new Paint();
  /** 进度条path */
  private Path mProgressBarPath = new Path();
  /** 最后一次path记录 */
  private PathRecord mLastPathRecord;
  /** 确认删除path记录 */
  private PathRecord mConfirmDeletePathRecord;
  /** path记录集合 */
  private List<PathRecord> mPathPairList = new CopyOnWriteArrayList<>();
  /** 最后绘制角度 */
  private float mLastAngle = -90;

  /** 当前绘制状态 */
  private int mState = STATE_INIT;
  /** 绘制状态：细分 */
  private static final int STATE_INIT = 0;
  private static final int STATE_RECORD = 1;
  private static final int STATE_PAUSE = 2;
  private static final int STATE_DELETE_CONFIRM_ING = 3;
  private static final int STATE_DELETE_CONFIRM = 4;

  /**
   * 构造函数
   */
  public VideoRecordProgressView(Context context) {
    this(context, null, 0);
  }

  /**
   * 构造函数
   */
  public VideoRecordProgressView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  /**
   * 构造函数
   */
  public VideoRecordProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  /**
   * 初始化
   */
  private void init() {
    mProgressBarPaint.setAntiAlias(true);
    mProgressBarPaint.setStyle(Paint.Style.STROKE);
    mProgressBarPaint.setStrokeWidth(GVideoRuntime.getAppContext().getResources().getDimension(
        R.dimen.fragment_record_progress_paint_size));
    mProgressBarPaint.setColor(colorRed);

    mProgressPauseConfirmPaint.setAntiAlias(true);
    mProgressPauseConfirmPaint.setStyle(Paint.Style.STROKE);
    mProgressPauseConfirmPaint.setStrokeWidth(GVideoRuntime.getAppContext().getResources().getDimension(
        R.dimen.fragment_record_progress_paint_size));
    mProgressPauseConfirmPaint.setColor(colorRed);

    mProgressPausePaint.setAntiAlias(true);
    mProgressPausePaint.setStyle(Paint.Style.STROKE);
    mProgressPausePaint.setStrokeWidth(GVideoRuntime.getAppContext().getResources().getDimension(
        R.dimen.fragment_record_progress_paint_size));
    mProgressPausePaint.setColor(colorWhite);
  }

  /**
   * 重置
   */
  public void reset() {
    mState = STATE_INIT;
    mLastAngle = -90;
    mLastPathRecord = null;
    mConfirmDeletePathRecord = null;
    mPathPairList = new ArrayList<>();
    postInvalidate();
  }

  /**
   * 开始录制
   */
  public void startRecord(int totalTime) {
    mState = STATE_RECORD;
    if (mDeleteConfirmAnimator != null) {
      mDeleteConfirmAnimator.end();
      mDeleteConfirmAnimator = null;
    }
    mProgressBarPaint.setColor(colorRed);
    if (mLastPathRecord != null) {
      mPathPairList.add(mLastPathRecord);
    }
    mProgressBarPath = new Path();
    mLastPathRecord = new PathRecord(mProgressBarPath, mProgressBarPaint, mLastAngle);
    mRecordAnimator = ValueAnimator.ofFloat(mRate, 360);
    mRecordAnimator.setInterpolator(new LinearInterpolator());
    mRecordAnimator.setDuration(totalTime);
    mRecordAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        mRate = (float) valueAnimator.getAnimatedValue();
        postInvalidate();
      }
    });
    mRecordAnimator.start();
  }

  /**
   * 暂停录制
   */
  public void pauseRecord() {
    if (mRecordAnimator != null) {
      mRecordAnimator.cancel();
    }
    if (mDeleteConfirmAnimator != null) {
      mDeleteConfirmAnimator.end();
      mDeleteConfirmAnimator = null;
    }
    if (mLastPathRecord != null) {
      mPathPairList.add(mLastPathRecord);
    }
    mProgressBarPath = new Path();
    mLastPathRecord = new PathRecord(mProgressBarPath, mProgressPausePaint, mLastAngle);
    post(new Runnable() {
      @Override
      public void run() {
        mState = STATE_PAUSE;
        postInvalidate();
      }
    });
  }

  /**
   * 显示确认删除动画
   */
  public void showDeleteConfirmAnim() {
    if (mState != STATE_PAUSE || mRecordAnimator.isRunning()) {
      return;
    }

    if (mLastPathRecord != null) {
      mPathPairList.add(mLastPathRecord);
      mLastPathRecord = null;
    }
    if (mPathPairList.size() <= 1) {
      return;
    }

    PathRecord record = mPathPairList.get(mPathPairList.size() - 2);
    if (record == null || record.path == null || record.paint != mProgressBarPaint) {
      return;
    }
    mConfirmDeletePathRecord = record;
    mPathPairList.remove(mConfirmDeletePathRecord);

    mState = STATE_DELETE_CONFIRM_ING;
    mDeleteConfirmAnimator = ValueAnimator.ofInt(0, 255);
    mDeleteConfirmAnimator.setInterpolator(new LinearInterpolator());
    mDeleteConfirmAnimator.setDuration(600);
    mDeleteConfirmAnimator.setRepeatMode(ValueAnimator.REVERSE);
    mDeleteConfirmAnimator.setRepeatCount(ValueAnimator.INFINITE);
    mDeleteConfirmAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
        mDeleteConfirmAlpha = (int) valueAnimator.getAnimatedValue();
        postInvalidate();
      }
    });
    mDeleteConfirmAnimator.start();
  }

  /**
   * 处理确认删除事件
   */
  public void handleDeleteConfirm(boolean realDelete) {
    if (mState != STATE_DELETE_CONFIRM_ING) {
      return;
    }
    if (!realDelete) {
      mState = STATE_PAUSE;
      if (mConfirmDeletePathRecord != null && mPathPairList.size() >= 1) {
        mPathPairList.add(mPathPairList.size() - 1, mConfirmDeletePathRecord);
      }
      return;
    }

    if (mPathPairList.size() <= 0) {
      return;
    }
    if (mDeleteConfirmAnimator != null) {
      mDeleteConfirmAnimator.end();
    }
    post(new Runnable() {
      @Override
      public void run() {
        if (mConfirmDeletePathRecord != null) {
          mLastAngle = mConfirmDeletePathRecord.startRate;
          mRate = mConfirmDeletePathRecord.startRate + 90;
          mConfirmDeletePathRecord = null;
        }
        if (mPathPairList != null && mPathPairList.size() > 0) {
          mPathPairList.remove(mPathPairList.size() - 1);
          if (mPathPairList.size() == 0) {
            mState = STATE_INIT;
          } else {
            mState = STATE_DELETE_CONFIRM;
          }
          invalidate();
        }
      }
    });
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
    switch (mState) {
      case STATE_INIT:
        drawInit(canvas);
        break;
      case STATE_RECORD:
        drawRecord(canvas, false);
        break;
      case STATE_DELETE_CONFIRM_ING:
        drawPauseConfirmIng(canvas);
        break;
      case STATE_DELETE_CONFIRM:
        drawPauseConfirm(canvas);
        break;
      case STATE_PAUSE:
        drawRecord(canvas, true);
        break;
      default:
        break;
    }
  }

  /**
   * 绘制初始化状态
   *
   * @param canvas  绘制canvas
   */
  private void drawInit(Canvas canvas) {
    mProgressBarPaint.setColor(colorWhite);
    canvas.drawCircle(mWidth / 2, mHeight / 2, mProgressWidth, mProgressBarPaint);
  }

  /**
   * 绘制录制状态
   *
   * @param canvas  绘制canvas
   * @param isPause 是否暂停
   */
  private void drawRecord(Canvas canvas, boolean isPause) {
    if (mRecordRectF == null) {
      mRecordRectF = new RectF(mWidth / 2 - mProgressWidth, mHeight / 2 - mProgressWidth,
          mWidth / 2 + mProgressWidth, mHeight / 2 + mProgressWidth);
    }
    Paint paint;
    if (isPause) {
      paint = mProgressPausePaint;
      mProgressBarPath.addArc(mRecordRectF, mLastAngle, 5f);
    } else {
      paint = mProgressBarPaint;
      float currentAngle = mRate - 90;
      mProgressBarPath.addArc(mRecordRectF, mLastAngle, currentAngle - mLastAngle);
      //    canvas.drawArc(mRecordRectF, mLastAngle, currentAngle - mLastAngle, false, paint);
      mLastAngle = currentAngle;
    }
    canvas.drawPath(mProgressBarPath, paint);
    drawCommonPaths(canvas);
  }

  /**
   * 绘制确认删除中状态
   *
   * @param canvas  绘制canvas
   */
  private void drawPauseConfirmIng(Canvas canvas) {
    if (mRecordRectF == null) {
      mRecordRectF = new RectF(mWidth / 2 - mProgressWidth, mHeight / 2 - mProgressWidth,
          mWidth / 2 + mProgressWidth, mHeight / 2 + mProgressWidth);
    }
    mProgressPauseConfirmPaint.setAlpha(mDeleteConfirmAlpha);
    if (mConfirmDeletePathRecord != null) {
      canvas.drawPath(mConfirmDeletePathRecord.path, mProgressPauseConfirmPaint);
    }
    drawCommonPaths(canvas);
  }

  /**
   * 绘制确认删除状态
   *
   * @param canvas  绘制canvas
   */
  private void drawPauseConfirm(Canvas canvas) {
    if (mRecordRectF == null) {
      mRecordRectF = new RectF(mWidth / 2 - mProgressWidth, mHeight / 2 - mProgressWidth,
          mWidth / 2 + mProgressWidth, mHeight / 2 + mProgressWidth);
    }
    drawCommonPaths(canvas);
    mState = STATE_PAUSE;
  }

  /**
   * 绘制公共path路径
   *
   * @param canvas  绘制canvas
   */
  private void drawCommonPaths(Canvas canvas) {
    PathRecord record;
    for (int i = mPathPairList.size() - 1; i >= 0; i--) {
      record = mPathPairList.get(i);
      if (record != null) {
        canvas.drawPath(record.path, record.paint);
      }
    }
  }

  /**
   * path记录
   */
  private static class PathRecord {
    /** 开始绘制角度 */
    float startRate = -90f;
    /** 绘制路径 */
    Path path;
    /** 绘制画笔 */
    Paint paint;

    /**
     * 构造函数
     */
    PathRecord(Path path, Paint paint, float startRate) {
      this.startRate = startRate;
      this.paint = paint;
      this.path = path;
    }
  }
}
