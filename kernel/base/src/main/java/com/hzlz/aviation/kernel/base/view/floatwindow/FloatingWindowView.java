package com.hzlz.aviation.kernel.base.view.floatwindow;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.utils.AnimUtils;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.widget.widget.GVideoConstraintLayout;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 悬浮loading布局
 */
public class FloatingWindowView extends FloatingMagnetView {

    private final GVideoTextView content;
    private final GVideoLinearLayout llContent;
    private final GVideoConstraintLayout rootLayout;
    private final GVideoImageView icon;
    private final ContentLoadingProgressBar progressBar;

    //布局停留显示时间
    private final int VIEW_STOP_SHOW_TIME = 1000;
    //动画时长
    private final int DURATION_TIME = 300;
    //检查线程时长
    private final int CHECK_THREAD_TIME = DURATION_TIME + 100;

    /**
     * 是否正在执行加载动画
     */
    private boolean isStartLoading;
    private boolean isNeedFinish;

    private String loadingText, finishText;

    public FloatingWindowView(@NonNull Context context) {
        this(context, R.layout.view_float_window);
    }

    public FloatingWindowView(@NonNull Context context, @LayoutRes int resource) {
        super(context, null);
        View view = inflate(context, resource, this);
        rootLayout = view.findViewById(R.id.root_layout);
        content = view.findViewById(R.id.content);
        llContent = view.findViewById(R.id.ll_content);
        icon = view.findViewById(R.id.icon);
        progressBar = view.findViewById(R.id.progress);
        rootLayout.setVisibility(GONE);
    }

    public void setProgress(int progress) {
        progressBar.setVisibility(VISIBLE);
        progressBar.setProgress(progress);
    }

    /**
     * loading 显示
     */
    public void setStartLoading() {

        isStartLoading = true;
        isNeedFinish = false;
        AsyncUtils.runOnUIThread(() -> {
            rootLayout.setVisibility(VISIBLE);
            content.setText(loadingText);
            icon.setImageResource(R.drawable.ic_float_window_loading);
            AnimUtils.setRotateView(icon, VIEW_STOP_SHOW_TIME);

            AnimUtils.setShowAnimationUp(rootLayout, llContent, DURATION_TIME, 1f, () -> {

                AtomicBoolean downCheck = new AtomicBoolean(false);
                AsyncUtils.runOnUIThread(() -> {

                    AnimUtils.setHideAnimationDown(rootLayout, llContent, DURATION_TIME, 1f, 2 / 52f, () -> {
                        downCheck.set(true);
                        startHideAnimationDown();
                    });

                    //存在动态addView会造成动画失效，开启线程确保功能流程正常
                    AsyncUtils.runOnUIThread(() -> { if (!downCheck.get()) startHideAnimationDown(); },CHECK_THREAD_TIME);

                }, VIEW_STOP_SHOW_TIME);
            });
        }, 100);
    }

    /**
     * 隐藏起始view
     */
    private void startHideAnimationDown(){
        isStartLoading = false;
        if (isNeedFinish) {
            FloatingView.get().setProgress(100);
            //延迟处理，否则动画不会执行
            AsyncUtils.runOnUIThread(() -> {
                FloatingView.get().finishLoading();
                isNeedFinish = false;
            }, 100);
        }
    }

    /**
     * loading 控件隐藏
     */
    public void setFinishLoading() {
        if (isStartLoading) {
            isNeedFinish = true;
            return;
        }
        content.setText(finishText);
        icon.clearAnimation();
        icon.setImageResource(R.drawable.ic_float_window_finish);
        progressBar.setVisibility(GONE);

        llContent.setVisibility(VISIBLE);
        AtomicBoolean upCheck = new AtomicBoolean(false);

        AnimUtils.setShowAnimationUp(rootLayout, llContent, DURATION_TIME, 1f, () -> {
            upCheck.set(true);
            finishHideView();
        });

        AsyncUtils.runOnUIThread(() -> { if (!upCheck.get()) finishHideView(); },CHECK_THREAD_TIME);

    }

    /**
     * 隐藏结束控件
     */
    private void finishHideView(){

        AsyncUtils.runOnUIThread(() -> {
            AtomicBoolean downCheck = new AtomicBoolean(false);

            AnimUtils.setHideAnimationDown(rootLayout, llContent, DURATION_TIME, 1f, 0f, () -> {
                downCheck.set(true);
                FloatingView.get().remove();
            });

            AsyncUtils.runOnUIThread(() -> { if (!downCheck.get())  FloatingView.get().remove(); },CHECK_THREAD_TIME);

        }, VIEW_STOP_SHOW_TIME);
    }

    /**
     * 设置提示问题
     *
     * @param loadingText lading提示文字
     * @param finishText  完成提示文字
     */
    public void setTipText(String loadingText, String finishText) {
        this.loadingText = loadingText;
        this.finishText = finishText;
    }


}
