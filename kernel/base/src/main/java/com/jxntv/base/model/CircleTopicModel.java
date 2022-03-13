package com.jxntv.base.model;

import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.TopicDetail;

public final class CircleTopicModel {

    public Circle circle;

    public TopicDetail topicDetail;

    public CircleTopicModel(Circle circle, TopicDetail topicDetail) {
        this.circle = circle;
        this.topicDetail = topicDetail;
    }

}
