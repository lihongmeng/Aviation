package com.hzlz.aviation.feature.community.adapter.qa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.kernel.base.model.circle.QaGroupModel;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author huangwei
 * date : 2021/8/31
 * desc : recycler 自动滚动帮助类
 **/
public class AutoScrollHelper {

    /**
     * 轮播线程
     */
    private Disposable autoTask;
    private LinearSmoothScroller smoothScroller;
    private int currentFirstPosition = 0;
    private LinearLayoutManager layoutManager;
    private CircleDetailQAAdapter adapter;
    private RecyclerView recyclerView;

    /**
     * 初始化
     */
    @SuppressLint("ClickableViewAccessibility")
    public void init(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        this.recyclerView = recyclerView;
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CircleDetailQAAdapter(context);
        recyclerView.setAdapter(adapter);

        smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                return super.calculateSpeedPerPixel(displayMetrics);
                return 2f / (displayMetrics.density != 0 ? displayMetrics.density : 1f);
            }
        };

        //屏蔽手动滑动
        recyclerView.setOnTouchListener((view, motionEvent) -> true);
    }

    /**
     * 刷新数据
     */
    public void refreshData(List<QaGroupModel> data) {
        int dp42 = GVideoRuntime.getAppContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_41DP);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();
        if (data!=null) {
            if (data.size() >= 3) {
                layoutParams.height = dp42 * 3;
            } else {
                layoutParams.height = dp42;
            }
        }
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutParams(layoutParams);
        adapter.clearList();
        adapter.addMoreData(data);
        currentFirstPosition = 0;
        startAuto();
    }

    public void startAuto() {

        if (autoTask != null) {
            autoTask.dispose();
        }
        if (adapter.getItemCount() <= 2) {
            return;
        }
        autoTask = Observable.interval(1000, 3000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    currentFirstPosition++;
                    smoothScroller.setTargetPosition(currentFirstPosition);
                    layoutManager.startSmoothScroll(smoothScroller);
                });

    }

    public void stop() {
        if (autoTask != null) {
            autoTask.dispose();
            autoTask = null;
        }
    }
}
