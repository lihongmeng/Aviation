package com.hzlz.aviation.kernel.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.adapter.AbstractAdapterModel;

public class TopicDetail extends AbstractAdapterModel implements Parcelable {

    /**
     * 话题编码
     */
    public String code;

    /**
     * 话题内容
     */
    public String content;

    /**
     * 创建时间
     */
    public long createDate;

    /**
     * 圈子id
     */
    public long groupId;

    public long id;

    /**
     * 标签名称
     */
    public String labelName;

    /**
     * 状态
     * true  启用
     * false  禁用
     */
    public boolean status;

    /**
     * 权重
     */
    public int weight;

    /**
     * 圈子名称
     */
    public String groupName;

    /**
     * 参与认证用户名称
     */
    public String authName;

    /**
     * 分享链接
     */
    public String shareUrl;

    /**
     * 所属MCN
     */
    public Long tenantId;

    /**
     * 所属MCN名称
     */
    public String tenantName;

    /**
     * 是否加入了对应的圈子
     */
    public Boolean join;

    /**
     * 社区封面图
     */
    public String groupCoverUrl;

    public TopicDetail() {

    }

    public TopicDetail(long topicId) {
        id = topicId;
    }

    public TopicDetail(GroupInfo groupInfo) {
        if (groupInfo == null) {
            return;
        }
        groupId = groupInfo.groupId;
        groupName = groupInfo.getGroupName();
        id = groupInfo.topicId;
        status = (groupInfo.status == Constant.CircleTopicStatus.ENABLE);
        join = groupInfo.isJoin();
        groupCoverUrl = groupInfo.coverUrl;
    }

    protected TopicDetail(Parcel in) {
        code = in.readString();
        content = in.readString();
        createDate = in.readLong();
        groupId = in.readLong();
        id = in.readLong();
        labelName = in.readString();
        status = in.readByte() != 0;
        weight = in.readInt();
        groupName = in.readString();
        authName = in.readString();
        shareUrl = in.readString();
        if (in.readByte() == 0) {
            tenantId = null;
        } else {
            tenantId = in.readLong();
        }
        tenantName = in.readString();
        byte tmpJoin = in.readByte();
        join = tmpJoin == 0 ? null : tmpJoin == 1;
        groupCoverUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(content);
        dest.writeLong(createDate);
        dest.writeLong(groupId);
        dest.writeLong(id);
        dest.writeString(labelName);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeInt(weight);
        dest.writeString(groupName);
        dest.writeString(authName);
        dest.writeString(shareUrl);
        if (tenantId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(tenantId);
        }
        dest.writeString(tenantName);
        dest.writeByte((byte) (join == null ? 0 : join ? 1 : 2));
        dest.writeString(groupCoverUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TopicDetail> CREATOR = new Creator<TopicDetail>() {
        @Override
        public TopicDetail createFromParcel(Parcel in) {
            return new TopicDetail(in);
        }

        @Override
        public TopicDetail[] newArray(int size) {
            return new TopicDetail[size];
        }
    };

}
