package com.jxntv.feed.model;

import com.jxntv.media.model.MediaModel;
import java.util.List;

/**
 * feed接口数据模型
 */
public class FeedResponse {

    /** 当前cursor */
    public String cursor;
    /** 数据list */
    public List<MediaModel> list;
    /** 是否有置顶数据 */
    public boolean hasStick = false;
    /** 置顶数据 */
    public MediaModel stickFeedModel;
}
