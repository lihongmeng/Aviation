package com.hzlz.aviation.kernel.base.entity;

import com.hzlz.aviation.kernel.base.model.circle.Circle;

public class PublishSataData {
    public String startPublishFrom;
    public Circle circle;

    public PublishSataData(String startPublishFrom,Circle circle){
        this.startPublishFrom=startPublishFrom;
        this.circle=circle;
    }
}
