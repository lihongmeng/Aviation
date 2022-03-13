package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import com.jxntv.base.model.video.InteractDataObservable;

import java.util.List;

public class CircleDetail implements Parcelable {

    public boolean admin;

    public int applyMode;

    /**
     * 圈子封面
     */
    public CircleCommentImage cover;

    /**
     * 封面背景
     */
    public CircleCommentImage backgroundImage;

    /**
     * 红人信息
     */
    public CircleDetailFavoriteVO favoriteVO;

    /**
     * 圈子id
     */
    public long groupId;

    /**
     * 圈子简介
     */
    public String introduction;

    /**
     * 当前用户是否加入圈子
     */
    public boolean join;

    /**
     * 直播对象
     */
    public CircleDetailLiveVo liveVO;

    /**
     * 圈子名称
     */
    public String name;

    /**
     * 圈主id
     */
    public CircleFamous owner;

    /**
     * 圈主手机
     */
    public String ownerMobile;

    /**
     * 话题前三
     */
    public List<TopicDetail> topicList;

    /**
     * 默认排序规则
     * {@link com.jxntv.base.Constant.CircleSortType}
     */
    public int sortType;

    /**
     * 圈子下的动态数量
     */
    public int mediaCount;

    /**
     * 粉丝数量
     */
    public int fansCount;

    public List<String> labels;

    /**
     * 问答
     */
    public QaGroupListModel answerDetailListVO;

    /**
     * 边听边聊
     */
    public BroadCastDetail broadcastDetail;

    /**
     * 分享链接
     */
    public String shareUrl;

    public TopicDetail topicDetail;

    public CircleDetail() {

    }

    protected CircleDetail(Parcel in) {
        admin = in.readByte() != 0;
        applyMode = in.readInt();
        cover = in.readParcelable(CircleCommentImage.class.getClassLoader());
        backgroundImage = in.readParcelable(CircleCommentImage.class.getClassLoader());
        favoriteVO = in.readParcelable(CircleDetailFavoriteVO.class.getClassLoader());
        groupId = in.readLong();
        introduction = in.readString();
        join = in.readByte() != 0;
        liveVO = in.readParcelable(CircleDetailLiveVo.class.getClassLoader());
        name = in.readString();
        owner = in.readParcelable(CircleFamous.class.getClassLoader());
        ownerMobile = in.readString();
        topicList = in.createTypedArrayList(TopicDetail.CREATOR);
        sortType = in.readInt();
        mediaCount = in.readInt();
        fansCount = in.readInt();
        answerDetailListVO = in.readParcelable(QaGroupListModel.class.getClassLoader());
        broadcastDetail = in.readParcelable(BroadCastDetail.class.getClassLoader());
        shareUrl = in.readString();
        topicDetail = in.readParcelable(TopicDetail.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (admin ? 1 : 0));
        dest.writeInt(applyMode);
        dest.writeParcelable(cover, flags);
        dest.writeParcelable(backgroundImage, flags);
        dest.writeParcelable(favoriteVO, flags);
        dest.writeLong(groupId);
        dest.writeString(introduction);
        dest.writeByte((byte) (join ? 1 : 0));
        dest.writeParcelable(liveVO, flags);
        dest.writeString(name);
        dest.writeParcelable(owner, flags);
        dest.writeString(ownerMobile);
        dest.writeTypedList(topicList);
        dest.writeInt(sortType);
        dest.writeInt(mediaCount);
        dest.writeInt(fansCount);
        dest.writeParcelable(answerDetailListVO,flags);
        dest.writeParcelable(broadcastDetail,flags);
        dest.writeString(shareUrl);
        dest.writeParcelable(topicDetail,flags);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleDetail> CREATOR = new Creator<CircleDetail>() {
        @Override
        public CircleDetail createFromParcel(Parcel in) {
            return new CircleDetail(in);
        }

        @Override
        public CircleDetail[] newArray(int size) {
            return new CircleDetail[size];
        }
    };

    public void updateInteract(){
        InteractDataObservable.getInstance().setJoinCircle(String.valueOf(groupId),join);
    }

}
