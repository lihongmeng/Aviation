package com.hzlz.aviation.kernel.base.view.recyclerview.interf;

/**
 * recyclerView 模型接口
 *
 *
 * @since 2020.1.17
 */
public interface IRecyclerModel<MODEL> {

    /**
     * 加载数据
     *
     * @param page          当前加载页数
     * @param loadListener  加载结果监听
     */
    void loadData(int page, RecyclerViewLoadListener<MODEL> loadListener);

    /**
     * 移除指定位置数据
     *
     * @param position      指定位置
     * @param loadListener  加载结果监听
     */
    default void remove(int position, RecyclerViewLoadListener<MODEL> loadListener) {}

    /**
     * 将数据插入指定位置
     *
     * @param position      指定位置
     * @param model         待插入数据
     * @param loadListener  加载结果监听
     */
    default void insert(int position, MODEL model, RecyclerViewLoadListener<MODEL> loadListener) {}

    /**
     * 更新指定位置数据
     *
     * @param position      指定位置
     * @param model         待更新数据
     * @param loadListener  加载结果监听
     */
    default void update(int position, MODEL model, RecyclerViewLoadListener<MODEL> loadListener) {}

    /**
     * 初始化数据
     *
     * @param loadListener  加载结果监听
     */
    default void initialData(RecyclerViewLoadListener<MODEL> loadListener) {}
}
