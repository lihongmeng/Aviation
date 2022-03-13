package com.jxntv.base.view.floatwindow;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 *
 * 悬浮控制器，主要处理横竖屏切换等
 */
public class FloatingMagnetView extends FrameLayout {

    protected MoveAnimator mMoveAnimator;
    protected int mScreenWidth;
    private int mScreenHeight;
    private float mPortraitY;


    public FloatingMagnetView(Context context) {
        this(context, null);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMoveAnimator = new MoveAnimator();
    }

    protected void updateSize() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup != null) {
            mScreenWidth = viewGroup.getWidth();
            mScreenHeight = viewGroup.getHeight();
        }
    }

    public void moveToEdge(boolean isLandscape) {
        float moveDistance = 0;
        float y = getY();
        if (!isLandscape && mPortraitY != 0) {
            y = mPortraitY;
            clearPortraitY();
        }
        mMoveAnimator.start(moveDistance, Math.min(Math.max(0, y), mScreenHeight - getHeight()));
    }

    private void clearPortraitY() {
        mPortraitY = 0;
    }

    protected class MoveAnimator implements Runnable {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (getRootView() == null || getRootView().getParent() == null) {
                return;
            }
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
            float deltaX = (destinationX - getX()) * progress;
            float deltaY = (destinationY - getY()) * progress;
            move(deltaX, deltaY);
            if (progress < 1) {
                handler.post(this);
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }

    private void move(float deltaX, float deltaY) {
        setX(getX() + deltaX);
        setY(getY() + deltaY);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getParent() != null) {
            final boolean isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
            markPortraitY(isLandscape);
            ((ViewGroup) getParent()).post(new Runnable() {
                @Override
                public void run() {
                    updateSize();
                    moveToEdge(isLandscape);
                }
            });
        }
    }

    private void markPortraitY(boolean isLandscape) {
        if (isLandscape) {
            mPortraitY = getY();
        }
    }
}
