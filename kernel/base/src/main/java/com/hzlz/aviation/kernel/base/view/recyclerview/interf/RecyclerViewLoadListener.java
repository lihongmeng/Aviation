package com.hzlz.aviation.kernel.base.view.recyclerview.interf;

import java.util.List;

/**
 * recyclerView加载结果数据
 *
 *
 * @since 2020.1.17
 */
public interface RecyclerViewLoadListener<T> {

    /**
     * 加载数据成功
     *
     * @param list  待添加数据
     */
    void loadSuccess(List<T> list);

    /**
     * 加载失败
     *
     * @param message   加载失败Message
     */
    void loadFailure(Throwable throwable);

    /**
     * 开始加载
     */
    void loadStart();

    /**
     * 加载结束
     */
    void loadComplete();
}
