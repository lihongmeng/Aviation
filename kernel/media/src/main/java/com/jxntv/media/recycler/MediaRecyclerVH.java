package com.jxntv.media.recycler;

import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.media.template.IMediaTemplate;

/**
 * media recyclerview数据绑定模型
 */
public class MediaRecyclerVH extends BaseRecyclerViewHolder {

    /**
     * feed 持有的模板
     */
    private IMediaTemplate mFeedTemplate;

    public MediaRecyclerVH(IMediaTemplate view) {
        super(view.getDataBinding());
        mFeedTemplate = view;
    }

    /**
     * 获取feed 持有的模板视图
     *
     * @return feed模板对象
     */
    public IMediaTemplate getFeedTemplate() {
        return mFeedTemplate;
    }
}
