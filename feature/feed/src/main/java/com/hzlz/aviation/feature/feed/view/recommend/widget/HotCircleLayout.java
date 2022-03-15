package com.hzlz.aviation.feature.feed.view.recommend.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.feature.feed.R;
import com.hzlz.aviation.feature.feed.adapter.HotCircleAdapter;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.view.CircleTopicLiveLayout;
import com.hzlz.aviation.kernel.base.view.recyclerview.RecyclerViewVideoOnScrollListener;
import com.hzlz.aviation.library.widget.widget.GVideoRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class HotCircleLayout extends GVideoRelativeLayout {

    private boolean isLoadingData;

    private Context context;
    private final List<Circle> dataSource = new ArrayList<>();
    private HotCircleAdapter hotCircleAdapter;
    private RecyclerView contentLayout;
    private Listener listener;

    public int lastLoadIndex = 0;

    public HotCircleLayout(Context context) {
        this(context, null);
    }

    public HotCircleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotCircleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVar(context);
        initViews();
    }

    private void initVar(Context context) {
        this.context = context;
    }

    private void initViews() {
        LayoutInflater.from(context).inflate(R.layout.layout_hot_circle, this);
        contentLayout = findViewById(R.id.content_layout);

        hotCircleAdapter = new HotCircleAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        contentLayout.setLayoutManager(linearLayoutManager);
        contentLayout.setAdapter(hotCircleAdapter);

        contentLayout.addOnScrollListener(new RecyclerViewVideoOnScrollListener(contentLayout,
                new RecyclerViewVideoOnScrollListener.onScrolledPositionListener() {
                    @Override
                    public void onScrolled(int fistVisible, int lastVisible) {

                        int dataSourceSize = dataSource.size();
                        if (dataSourceSize <= 3 || lastLoadIndex == lastVisible) {
                            return;
                        }

                        if (!isLoadingData && lastVisible == dataSourceSize - 3 && listener != null) {
                            isLoadingData = true;
                            lastLoadIndex = lastVisible;
                            listener.isLoadMore();
                        }
                    }

                    @Override
                    public void onScrollStateChanged(int fistVisible, int lastVisible) {
                        mFistVisible = fistVisible;
                        mLastVisible = lastVisible;
                    }

                    @Override
                    public void onItemEnter(int position) {
                        postExposure(position, position, true);
                    }

                    @Override
                    public void onItemExit(int position) {
                        postExposure(position, position, false);
                    }
                }));

    }

    public void updateLoadingStatus() {
        isLoadingData = false;
    }

    public void updateDataSource(List<Circle> dataSource) {
        if (dataSource == null || dataSource.isEmpty()) {
            return;
        }
        setVisibility(VISIBLE);
        this.dataSource.addAll(dataSource);
        hotCircleAdapter.updateSource(this.dataSource);
    }

    public void replaceDataSource(List<Circle> dataSource) {
        lastLoadIndex = 0;
        this.dataSource.clear();
        if (dataSource == null || dataSource.isEmpty()) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        this.dataSource.addAll(dataSource);
        hotCircleAdapter.updateSource(this.dataSource);
    }

    public boolean isDataSourceEmpty(){
        return dataSource==null||dataSource.isEmpty();
    }

    public void setListener(
            HotCircleAdapter.Listener hotCircleAdapterListener,
            CircleTopicLiveLayout.Listener circleTopicLiveLayoutListener
    ) {
        hotCircleAdapter.setHotCircleAdapterListener(hotCircleAdapterListener);
        hotCircleAdapter.setCircleTopicLiveLayoutListener(circleTopicLiveLayoutListener);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void isLoadMore();
    }

    //------- 埋点相关  --------
    private String pid;
    private int mFistVisible = -1, mLastVisible = -1;

    /**
     * 内容曝光处处理
     *
     * @param start  起始位置
     * @param end    结束位置
     * @param isShow 是否显示
     */
    private void postExposure(int start, int end, boolean isShow) {
        if (start == -1 && end == -1 && hotCircleAdapter != null) {
            start = 0;
            end = hotCircleAdapter.getShowLastPosition();
        }
        if (start < 0 || end < 0 || end < start) {
            return;
        }
        for (int i = start; i <= end; i++) {
            if (isShow) {
                hotCircleAdapter.enterPosition(i, pid);
            } else {
                hotCircleAdapter.exitPosition(i, pid);
            }
        }
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void onViewResume() {
        postExposure(mFistVisible, mLastVisible, true);
    }

    public void onViewPause() {
        postExposure(mFistVisible, mLastVisible, false);
    }

}
