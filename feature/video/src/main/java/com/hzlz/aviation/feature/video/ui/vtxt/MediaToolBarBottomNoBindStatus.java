package com.hzlz.aviation.feature.video.ui.vtxt;


import com.hzlz.aviation.kernel.base.model.video.VideoModel;

public class MediaToolBarBottomNoBindStatus {

    /**
     * 点赞状态
     */
    public boolean isFlavor;

    /**
     * 话题名称
     */
    public String topicName;

    /**
     * 评论数量
     */
    public int commentCount;

    public MediaToolBarBottomNoBindStatus(VideoModel videoModel) {
        if (videoModel == null) {
            isFlavor = false;
            topicName = "";
            commentCount = 0;
            return;
        }
        isFlavor = videoModel.getIsFavor() > 0;
        topicName=videoModel.getTopicName();
        commentCount=videoModel.getReviews();
    }

}
