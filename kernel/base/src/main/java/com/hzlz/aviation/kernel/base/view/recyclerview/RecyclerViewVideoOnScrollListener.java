package com.hzlz.aviation.kernel.base.view.recyclerview;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewVideoOnScrollListener extends RecyclerView.OnScrollListener {

    private RecyclerView recyclerView;

    public RecyclerViewVideoOnScrollListener(RecyclerView recyclerView,onScrolledPositionListener listener) {
        super();
        this.recyclerView = recyclerView;
        this.listener = listener;
    }


    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        super.onScrolled(view, dx, dy);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        firstVisiblePos = linearLayoutManager.findFirstVisibleItemPosition();
        lastVisiblePos = linearLayoutManager.findLastVisibleItemPosition();

        //处理埋点曝光
        dealScrollEvent(firstVisiblePos,lastVisiblePos);
        if (listener!=null){
            listener.onScrolled(firstVisiblePos,lastVisiblePos);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (scrollState== 0 && listener!= null) {
            //自动播放列表中的视频
            listener.onScrollStateChanged(firstVisiblePos,lastVisiblePos);
        }
    }

    private int firstVisiblePos = -1,lastVisiblePos = -1;
    private onScrolledPositionListener listener;
    public void setonScrolledPositionListener(onScrolledPositionListener listener){
        this.listener = listener;
    }
    public interface onScrolledPositionListener{

        /**
         * 滑动中
         */
        void onScrolled(int fistVisible,int lastVisible);

        /**
         * 停止滑动
         * @param fistVisible 第一个显示view位置
         * @param lastVisible 最后一个显示view位置
         */
        void onScrollStateChanged(int fistVisible,int lastVisible);

        /**
         * item 进入屏幕
         * @param position  view 位置
         */
        void onItemEnter(int position);

        /**
         * item 滑出屏幕
         * @param position view 位置
         */
        void onItemExit(int position);

    }

    private int lastStart = -1;
    private int lastEnd = -1;
    private void dealScrollEvent(int firstVisible, int lastVisible) {
        int visibleItemCount = lastVisible - firstVisible;

        if (visibleItemCount > 0) {
            if (lastStart == -1) {
                lastStart = firstVisible;
                lastEnd = lastVisible;
                for (int i = lastStart; i < lastEnd + 1; i++) {
                    if (listener!=null){
                        listener.onItemEnter(i);
                    }
                }
            } else {
                if (firstVisible != lastStart) {
                    if (firstVisible > lastStart) {
                        //向上滑动
                        for (int i = lastStart; i < firstVisible; i++) {
                            if (listener!=null){
                                listener.onItemExit(i);
                            }
                        }
                    } else {
                        //向下滑动
                        for (int i = firstVisible; i < lastStart; i++) {
                            if (listener!=null){
                                listener.onItemEnter(i);
                            }
                        }
                    }
                    lastStart = firstVisible;
                }
                if (lastVisible != lastEnd) {
                    if (lastVisible > lastEnd) {
                        //向上滑动
                        for (int i = lastEnd; i < lastVisible; i++) {
                            if (listener!=null){
                                listener.onItemEnter(i + 1);
                            }

                        }
                    } else {
                        //向下滑动
                        for (int i = lastVisible; i < lastEnd; i++) {
                            if (listener!=null){
                                listener.onItemExit(i + 1);
                            }
                        }
                    }
                    lastEnd = lastVisible;
                }
            }
        }
    }

}
