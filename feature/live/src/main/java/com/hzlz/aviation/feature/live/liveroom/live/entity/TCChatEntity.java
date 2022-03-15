package com.hzlz.aviation.feature.live.liveroom.live.entity;


import android.text.TextUtils;

import com.hzlz.aviation.kernel.base.adapter.AbstractAdapterModel;

import java.util.Objects;

/**
 * Module:   TCChatEntity
 * <p>
 * Function: 消息载体类。
 */
public class TCChatEntity extends AbstractAdapterModel {

    public String grpSendName;    // 发送者的名字

    private String content;        // 消息内容

    private int type;            // 消息类型

    public String getSenderName() {
        return grpSendName != null ? !grpSendName.contains("：") && !TextUtils.isEmpty(content) ? grpSendName + "：" : grpSendName : "";
    }

    public void setSenderName(String grpSendName) {
        this.grpSendName = grpSendName;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String context) {
        this.content = context;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCChatEntity)) return false;

        TCChatEntity that = (TCChatEntity) o;

        if (getType() != that.getType()) return false;
        if (!Objects.equals(grpSendName, that.grpSendName))
            return false;
        return getContent() != null ? getContent().equals(that.getContent()) : that.getContent() == null;

    }

    @Override
    public int hashCode() {
        int result = grpSendName != null ? grpSendName.hashCode() : 0;
        result = 31 * result + (getContent() != null ? getContent().hashCode() : 0);
        result = 31 * result + getType();
        return result;
    }
}
