package com.hzlz.aviation.kernel.base.model.circle;

import android.os.Parcel;

import com.hzlz.aviation.kernel.base.model.video.RecommendModel;

import java.util.List;

public class CircleRecommendModel extends RecommendModel {

    public String detailBigPic;

    public String detailSmallPic;

    public String headPic;

    public List<String> imageUrls;

    protected CircleRecommendModel(Parcel in) {
        super(in);
        detailBigPic = in.readString();
        detailSmallPic = in.readString();
        headPic = in.readString();
        imageUrls = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(detailBigPic);
        dest.writeString(detailSmallPic);
        dest.writeString(headPic);
        dest.writeStringList(imageUrls);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleRecommendModel> CREATOR = new Creator<CircleRecommendModel>() {
        @Override
        public CircleRecommendModel createFromParcel(Parcel in) {
            return new CircleRecommendModel(in);
        }

        @Override
        public CircleRecommendModel[] newArray(int size) {
            return new CircleRecommendModel[size];
        }
    };

}
