package com.jxntv.base.model.circle;

import android.os.Parcel;

import com.jxntv.base.model.video.RecommendModel;

import java.util.List;

public class CirclePendantModel extends RecommendModel {

    /**
     * 内容标签
     */
    public String contentLabel;

    /**
     * 详情页大图
     */
    public String detailBigPic;

    /**
     * 详情页小图
     */
    public String detailSmallPic;

    /**
     * 专题头图
     */
    public String headPic;

    /**
     * 图片链接
     */
    public List<String> imageUrls;

    /**
     * 展示时长
     */
    public long showTime;

    /**
     * 专题ID
     * 当specialId = mediaId时
     * 当前资源为专题列表，需要进入专题列表页
     * 否则资源详情页
     */
    public long specialId;

    protected CirclePendantModel(Parcel in) {
        super(in);
        contentLabel = in.readString();
        detailBigPic = in.readString();
        detailSmallPic = in.readString();
        headPic = in.readString();
        imageUrls = in.createStringArrayList();
        showTime = in.readLong();
        specialId = in.readLong();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(contentLabel);
        dest.writeString(detailBigPic);
        dest.writeString(detailSmallPic);
        dest.writeString(headPic);
        dest.writeStringList(imageUrls);
        dest.writeLong(showTime);
        dest.writeLong(specialId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CirclePendantModel> CREATOR = new Creator<CirclePendantModel>() {
        @Override
        public CirclePendantModel createFromParcel(Parcel in) {
            return new CirclePendantModel(in);
        }

        @Override
        public CirclePendantModel[] newArray(int size) {
            return new CirclePendantModel[size];
        }
    };

}
