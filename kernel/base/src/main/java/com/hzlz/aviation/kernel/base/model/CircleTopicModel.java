package com.hzlz.aviation.kernel.base.model;

import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;

public final class CircleTopicModel {

    public Circle circle;

    public TopicDetail topicDetail;

    public CircleTopicModel(Circle circle, TopicDetail topicDetail) {
        this.circle = circle;
        this.topicDetail = topicDetail;
    }

}
