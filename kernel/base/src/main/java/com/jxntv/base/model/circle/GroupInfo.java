package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.jxntv.base.model.video.InteractDataObservable;

import java.util.List;

public class GroupInfo implements Parcelable {

    /**
     * 是否是精华帖
     */
    public boolean essence;

    /**
     * 圈子id
     */
    public long groupId;

    /**
     * 圈子名称
     */
    private String groupName;

    /**
     * 动态状态（圈子内部状态）内容状态
     * {@link com.jxntv.base.Constant.CircleTopicStatus}
     */
    public int status;

    /**
     * 是否是置顶帖
     */
    public boolean top;

    /**
     * 话题id
     */
    public long topicId;

    /**
     * 话题名称
     */
    private String topicName;

    /**
     * 圈子简介
     */
    private String introduction;

    private List<String> labels;

    /**
     * 社区封面地址
     */
    public String coverUrl;

    /**
     * 是否已加入社区
     */
    private boolean join;

    /**
     * 所属MCN
     */
    public Long tenantId;

    /**
     * 所属MCN名称
     */
    public String tenantName;

    public boolean isJoin() {
        return join;
    }

    public void setJoin(boolean join) {
        this.join = join;
        InteractDataObservable.getInstance().setJoinCircle(groupId, join);
    }

    public void updateInteract(){
        InteractDataObservable.getInstance().setJoinCircle(groupId, join);
    }

    public GroupInfo(){

    }

    protected GroupInfo(Parcel in) {
        essence = in.readByte() != 0;
        groupId = in.readLong();
        groupName = in.readString();
        status = in.readInt();
        top = in.readByte() != 0;
        topicId = in.readLong();
        topicName = in.readString();
        introduction = in.readString();
        labels = in.createStringArrayList();
        coverUrl = in.readString();
        join = in.readByte() != 0;
        if (in.readByte() == 0) {
            tenantId = null;
        } else {
            tenantId = in.readLong();
        }
        tenantName = in.readString();
    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel in) {
            return new GroupInfo(in);
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };

    public String getGroupName() {
        if (TextUtils.isEmpty(groupName)) {
            return "";
        }
        return groupName;
    }

    public String getTopicName() {
        if (TextUtils.isEmpty(topicName)) {
            return "";
        }
        return topicName;
    }

    public String getGroupIntroduction() {
        if (TextUtils.isEmpty(introduction)) {
            return "";
        }
        return introduction;
    }

    public long getTopicId() {
        return topicId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (essence ? 1 : 0));
        dest.writeLong(groupId);
        dest.writeString(groupName);
        dest.writeInt(status);
        dest.writeByte((byte) (top ? 1 : 0));
        dest.writeLong(topicId);
        dest.writeString(topicName);
        dest.writeString(introduction);
        dest.writeStringList(labels);
        dest.writeString(coverUrl);
        dest.writeByte((byte) (join ? 1 : 0));
        if (tenantId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(tenantId);
        }
        dest.writeString(tenantName);
    }
}
