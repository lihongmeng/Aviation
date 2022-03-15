package com.hzlz.aviation.feature.search.page;

import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;

/**
 * search recycler view数据绑定模型
 */
public class SearchRecyclerVH extends BaseRecyclerViewHolder {

    /** search 持有的模板 */
    private MediaBaseTemplate mSearchTemplate;

    /**
     * 构造函数
     */
    public SearchRecyclerVH(MediaBaseTemplate template) {
        super(template.getDataBinding());
        mSearchTemplate = template;
    }

    /**
     * 获取search 持有的模板视图
     *
     * @return search模板对象
     */
    public MediaBaseTemplate getSearchTemplate() {
        return mSearchTemplate;
    }
}
