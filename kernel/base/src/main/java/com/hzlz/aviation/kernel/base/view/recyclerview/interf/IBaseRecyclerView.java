package com.hzlz.aviation.kernel.base.view.recyclerview.interf;

import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewModel;

/**
 * 持有recyclerView视图接口，根据当前数据状态更新视图状态
 *
 *
 * @since 2020.1.17
 */
public interface IBaseRecyclerView {

    /**
     * 开始加载
     *
     * @param loadType 加载的类型
     */
    default void loadStart(@BaseRecyclerViewModel.LoadType int loadType) {}

    /**
     * 加载完成
     *
     * @param needScrollTop 是否需要滑动到顶端
     */
    default void loadComplete(boolean needScrollTop) {}

    /**
     * 加载失败
     *
     * @param throwable
     */
    default void loadFailure(Throwable throwable) {}
}
