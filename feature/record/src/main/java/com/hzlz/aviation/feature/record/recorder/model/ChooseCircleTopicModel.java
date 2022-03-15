package com.hzlz.aviation.feature.record.recorder.model;

import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;

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
