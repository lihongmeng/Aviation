package com.jxntv.base.entity;

import com.jxntv.base.model.circle.Circle;

public class PublishSataData {
    public String startPublishFrom;
    public Circle circle;

    public PublishSataData(String startPublishFrom,Circle circle){
        this.startPublishFrom=startPublishFrom;
        this.circle=circle;
    }
}
