package com.hzlz.aviation.feature.record.recorder.view.soundrecord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.SizeUtils;

import java.util.HashMap;

public class GVideoSoundRecordWaveView extends View {

    private Context context;
    private Paint paint;

    // 波形最大高度
    private float waveMaxHeight = 60.0f;

    // 波形最小高度
    private float waveMinHeight = 10f;

    // 波形宽度
    private float waveWidth = 10f;

    // 时间文本的宽度
    private float timeTextWidth = 60f;

    // 时间文本的高度
    private float timeTextHeight = 40f;

    // 开始录制的时间
    private long startRecordTime = 0;

    // 已经录制的时间
    private String record = "00:00";

    // 时间字体尺寸
    private float timeTextSize = SizeUtils.sp2px(15);

    // 有多少个波形
    private int waveCount = 12;

    // 当前音量，用来计算Wave高度
    private double volume = 0;

    // 波形的颜色
    private int waveColor;

    // RectF复用
    private HashMap<Integer, RectF> rectFHashMap = new HashMap<>();

    public GVideoSoundRecordWaveView(Context context) {
        this(context, null);
    }

    public GVideoSoundRecordWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoSoundRecordWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
    }

    private void initVars(Context context) {
        this.context = context;
        paint = new Paint();

        for (int index = 0; index < waveCount; index++) {
            rectFHashMap.put(index, new RectF());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;

        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(context, R.color.color_525566));
        paint.setTextSize(timeTextSize);
        canvas.drawText(
                record,
                halfWidth - timeTextWidth / 2 - timeTextWidth / 2,
                halfHeight + timeTextHeight / 2,
                paint
        );

        for (int index = 0; index < waveCount; index++) {
            RectF rectF = rectFHashMap.get(index);
            if (rectF == null) {
                continue;
            }

            int hafCount = waveCount / 2;

            if (index < hafCount) {
                rectF.left = halfWidth - timeTextWidth / 2 - (hafCount - index) * 15 - 50;
                rectF.right = halfWidth - timeTextWidth / 2 - (hafCount - index) * 15 + waveWidth - 50;
                rectF.top = (float) (halfHeight - (volume * Math.pow(index, 2) / (waveCount * 3)) + 5);
            } else {
                rectF.left = halfWidth + timeTextWidth / 2 + (index - hafCount) * 15 + 43;
                rectF.right = halfWidth + timeTextWidth / 2 + (index - hafCount) * 15 + waveWidth + 43;
                rectF.top = (float) (halfHeight - (volume * Math.pow(waveCount - index - 1, 2) / (waveCount * 3)) + 5);
            }
            rectF.bottom = halfHeight + waveWidth / 2 + 10;

            paint.setColor(waveColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(waveWidth);

            canvas.drawRect(rectF, paint);
        }
    }

    public void updateVolume(double volume) {
        this.volume = volume;
        invalidate();
    }

    public void startTimeCountDown() {
        post(timeRunnable);
    }

    public void removeCallbacks() {
        removeCallbacks(timeRunnable);
        record = "00:00";
        volume = 0;
    }

    public void setStartRecordTime(long startRecordTime) {
        this.startRecordTime = startRecordTime;
    }

    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {

            // 计算时间差，转换为字符串
            record = DateUtils.changeTimeLongToString(
                    DateUtils.getCurrentMillSecondTime() - startRecordTime,
                    "mm:ss"
            );
            LogUtils.d("record -->> " + record);

            // record = dealTimeStyle(record);
            invalidate();
            postDelayed(timeRunnable, 1000);
        }
    };

    /**
     * 如果时间字符串以“0”开头，去掉
     *
     * @param targetTime 目标时间
     * @return 处理后的时间
     */
    private String dealTimeStyle(String targetTime) {
        if (TextUtils.isEmpty(targetTime)) {
            return "";
        }
        if (!targetTime.startsWith("0")) {
            return targetTime;
        }
        return targetTime.substring(1);
    }

    public void destroy() {
        removeCallbacks(timeRunnable);
    }

    public void setTimeTextSize(float timeTextSize) {
        this.timeTextSize = timeTextSize;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
    }

}