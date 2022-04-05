package com.hzlz.aviation.kernel.liteav.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.util.LogUtils;

import java.util.HashMap;

public class GVideoSoundViewWave extends View {

    // 记录所有波形条纹的高度系数，Paint落笔的位置固定
    // 只是递归本数组的长度进行绘制
    // 绘制长度 = waveMinHeight * 系数
    private final int[] waveHeightRatioArray = new int[]{
            1, 2, 3, 4, 2, 3, 5, 4, 5, 6,
            5, 3, 6, 5, 7, 3, 6, 4, 5, 3,
            2, 5, 2, 4, 3, 2, 3, 2, 1
    };

    // 波形宽度
    private float waveWidth = 5.0f;

    // 最小波形的高度
    private float waveMinHeight = 2.5f;

    // 波形之间的间距
    private float waveMargin = 14.0f;

    // 波形进度颜色
    private int progressWaveColor;

    // 波形默认颜色
    private int defaultWaveColor;

    // 绘制总时长,上级必传
    // 大于0才会启动绘制
    private long totalTime;

    // 当前是否正在播放
    private boolean isPlaying;

    // 绘制的时间间隔
    private long drawDuration = 100;

    // 当前绘制进度
    private int currentDrawProgress;

    // 当前需要显示多少个波形
    private int waveCount;

    // 开始播放的时间
    private long startPlayTime;

    // 当前已经播放的时间
    private long alreadyPlayTime;

    // RectF复用
    private final HashMap<Integer, RectF> rectFHashMap = new HashMap<>();

    private Context context;
    private Paint paint;

    public GVideoSoundViewWave(Context context) {
        this(context, null);
    }

    public GVideoSoundViewWave(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoSoundViewWave(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
    }

    private void initVars(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        progressWaveColor = ContextCompat.getColor(context, R.color.color_e4344e);
        defaultWaveColor = ContextCompat.getColor(context, R.color.color_ffffff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float halfViewHeight = getHeight() * 0.5f;

        initRectFHashMap();

        for (int index = 0; index < waveCount; index++) {
            RectF rectF = rectFHashMap.get(index);
            if (rectF == null) {
                continue;
            }
            rectF.left = index * (waveWidth + waveMargin);
            rectF.right = index * (waveWidth + waveMargin) + waveWidth;
            rectF.top = halfViewHeight - (waveMinHeight * getHeightRatio(index));
            rectF.bottom = halfViewHeight + (waveMinHeight * getHeightRatio(index));

            if (!isPlaying || isPositionPlayed(index)) {
                paint.setColor(progressWaveColor);
            } else {
                paint.setColor(defaultWaveColor);
            }

            canvas.drawRect(rectF, paint);
        }

        // 每次暂停，计算累计播放的时长
        if (isPlaying) {
            alreadyPlayTime += (System.currentTimeMillis() - startPlayTime);
        }
        startPlayTime = System.currentTimeMillis();

    }

    /**
     * 根据绘制波形的位置，获取数组中对应系数
     *
     * @param position 波形位置
     * @return 系数
     */
    private int getHeightRatio(int position) {
        int index = position + currentDrawProgress;
        if (index >= waveHeightRatioArray.length) {
            index = index % waveHeightRatioArray.length;
        }
        return waveHeightRatioArray[index];
    }

    /**
     * 设置播放的总时长
     *
     * @param totalTime 播放的总时长
     */
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    // 开始播放
    public void start() {
        if (totalTime <= 0) {
            return;
        }
        startPlayTime = System.currentTimeMillis();
        isPlaying = true;
        invalidate();
        postDelayed(playRunnable, drawDuration);
        LogUtils.d("Stop duration -->> " + (totalTime - alreadyPlayTime));
        postDelayed(stopRunnable, totalTime - alreadyPlayTime);
    }

    public void stop() {
        if (totalTime <= 0) {
            return;
        }
        alreadyPlayTime = 0;
        currentDrawProgress = 0;
        isPlaying = false;
        invalidate();
        removeCallbacks(playRunnable);
        removeCallbacks(stopRunnable);
    }

    public void pause() {
        if (totalTime <= 0) {
            return;
        }
        isPlaying = false;
        invalidate();
        removeCallbacks(playRunnable);
        removeCallbacks(stopRunnable);
    }

    private final Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            currentDrawProgress++;
            invalidate();
            postDelayed(playRunnable, drawDuration);
        }
    };

    private final Runnable stopRunnable = this::stop;

    /**
     * 结合语音播放的时间在总时长的占比
     * 对比波形View的index在总波形数量的位置
     * 得出，该波形View是否已经播放
     *
     * @param index 波形View的位置
     * @return 该波形View是否已经播放
     */
    private boolean isPositionPlayed(int index) {
        return ((float) (alreadyPlayTime) / totalTime) > ((float) index / waveCount);
    }

    private void initRectFHashMap() {
        if (!rectFHashMap.isEmpty()) {
            return;
        }
        int viewWidth = getWidth();

        // 根据当前View的宽度，计算需要绘制多少波形
        waveCount = (int) (viewWidth / (waveWidth + waveMargin));
        if (waveCount <= 0) {
            return;
        }
        for (int index = 0; index < waveCount; index++) {
            rectFHashMap.put(index, new RectF());
        }
    }

    public long remainPlayTime() {
        if (alreadyPlayTime <= 0) {
            return totalTime;
        } else {
            return totalTime - alreadyPlayTime;
        }
    }

}
