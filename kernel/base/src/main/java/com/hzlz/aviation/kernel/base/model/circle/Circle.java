package com.hzlz.aviation.kernel.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.adapter.AbstractAdapterModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;

import java.util.List;

public class Circle extends AbstractAdapterModel implements Parcelable {

    /**
     * 内容列表
     */
    public List<FindCircleContent> contentList;

    /**
     * 圈子id
     */
    public long groupId;

    /**
     * 圈子封面
     */
    public CircleCommentImage imageVO;

    /**
     * 圈子简介
     */
    public String introduction;

    /**
     * 当前用户是否加入圈子
     */
    private boolean join;

    /**
     * 内容最后发布时间
     */
    public long lastPublish;

    /**
     * 圈子名称
     */
    public String name;

    public int applyMode;

    /**
     * 社区红人
     */
    public List<CircleFamous> userList;

    /**
     * 社区标签
     */
    public List<String> labels;

    /**
     * 问答广场
     */
    public List<QaGroupModel> answerList;

    /**
     * 所属MCN
     */
    public Long tenantId;

    /**
     * 所属MCN名称
     */
    public String tenantName;

    /**
     * 在获取到圈子信息的时候，将当前页面的PID保存起来
     * 主要用于神策统计
     */
    private String dataSourcePagePid;

    public Circle() {

    }

    public Circle(long groupId) {
        this.groupId = groupId;
    }

    public void updateInteract() {
        InteractDataObservable.getInstance().setJoinCircle(groupId, join);
    }

    public Circle(TopicDetail topicDetail) {
        setGroupId(topicDetail.groupId);
        setName(topicDetail.groupName);
        tenantId = topicDetail.tenantId;
        tenantName = topicDetail.tenantName;
        imageVO=new CircleCommentImage("",topicDetail.groupCoverUrl);
    }

    public Circle(GroupInfo groupInfo) {
        setGroupId(groupInfo.groupId);
        setName(groupInfo.getGroupName());
        setIntroduction(groupInfo.getGroupIntroduction());
        setLabels(groupInfo.getLabels());
        tenantName = groupInfo.tenantName;
        tenantId = groupInfo.tenantId;
        imageVO=new CircleCommentImage("",groupInfo.coverUrl);
    }

    protected Circle(Parcel in) {
        contentList = in.createTypedArrayList(FindCircleContent.CREATOR);
        groupId = in.readLong();
        imageVO = in.readParcelable(CircleCommentImage.class.getClassLoader());
        introduction = in.readString();
        join = in.readByte() != 0;
        lastPublish = in.readLong();
        name = in.readString();
        applyMode = in.readInt();
        userList = in.createTypedArrayList(CircleFamous.CREATOR);
        labels = in.createStringArrayList();
        answerList = in.createTypedArrayList(QaGroupModel.CREATOR);
        if (in.readByte() == 0) {
            tenantId = null;
        } else {
            tenantId = in.readLong();
        }
        tenantName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(contentList);
        dest.writeLong(groupId);
        dest.writeParcelable(imageVO, flags);
        dest.writeString(introduction);
        dest.writeByte((byte) (join ? 1 : 0));
        dest.writeLong(lastPublish);
        dest.writeString(name);
        dest.writeInt(applyMode);
        dest.writeTypedList(userList);
        dest.writeStringList(labels);
        dest.writeTypedList(answerList);
        if (tenantId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(tenantId);
        }
        dest.writeString(tenantName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Circle> CREATOR = new Creator<Circle>() {
        @Override
        public Circle createFromParcel(Parcel in) {
            return new Circle(in);
        }

        @Override
        public Circle[] newArray(int size) {
            return new Circle[size];
        }
    };

    public void update(@NonNull Circle circle) {
        contentList = circle.contentList;
        groupId = circle.groupId;
        imageVO = circle.imageVO;
        introduction = circle.introduction;
        join = circle.join;
        lastPublish = circle.lastPublish;
        name = circle.name;
        userList = circle.userList;
        labels = circle.labels;
        answerList = circle.answerList;
        tenantId = circle.tenantId;
        tenantName = circle.tenantName;
    }


    public String getIntroduction() {
        if (TextUtils.isEmpty(introduction)) {
            return "";
        }
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FindCircleContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<FindCircleContent> contentList) {
        this.contentList = contentList;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public CircleCommentImage getImageVO() {
        return imageVO;
    }

    public void setImageVO(CircleCommentImage imageVO) {
        this.imageVO = imageVO;
    }

    public boolean isJoin() {
        return join;
    }

    public void setJoin(boolean join) {
        InteractDataObservable.getInstance().setJoinCircle(groupId, join);
        this.join = join;
    }

    public long getLastPublish() {
        return lastPublish;
    }

    public void setLastPublish(long lastPublish) {
        this.lastPublish = lastPublish;
    }

    public int getApplyMode() {
        return applyMode;
    }

    public void setApplyMode(int applyMode) {
        this.applyMode = applyMode;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public void update(CircleDetail circleDetail) {
        if (circleDetail == null) {
            setApplyMode(-1);
            setGroupId(-1);
            setImageVO(null);
            setIntroduction(null);
            setJoin(false);
            setName(null);
            return;
        }
        setApplyMode(circleDetail.applyMode);
        setGroupId(circleDetail.groupId);
        setImageVO(circleDetail.cover);
        setIntroduction(circleDetail.introduction);
        setJoin(circleDetail.join);
        setName(circleDetail.name);
        setLabels(circleDetail.labels);
    }

    public String getDataSourcePagePid() {
        return TextUtils.isEmpty(dataSourcePagePid) ? "" : dataSourcePagePid;
    }

    public void setDataSourcePagePid(String dataSourcePagePid) {
        this.dataSourcePagePid = dataSourcePagePid;
    }
}
