package com.jxntv.record.recorder.model;

import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.TopicDetail;

public final class ChooseCircleTopicModel {

    public TopicDetail topicDetail;

    public Circle circle;

    public ChooseCircleTopicModel(
            TopicDetail topicDetail,
            Circle circle
    ) {
        this.topicDetail = topicDetail;
        this.circle = circle;
    }

}
