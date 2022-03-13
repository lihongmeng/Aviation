package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author huangwei
 * date : 2021/9/3
 * desc : 圈子详情问答广场对象
 **/
public class GatherModel implements Parcelable {

    /**
     * 名称
     */
    private String name;
    /**
     * 引导文案
     */
    private String guideText;

    private int id;

    public GatherModel() {
    }

    protected GatherModel(Parcel in) {
        name = in.readString();
        guideText = in.readString();
        id = in.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuideText() {
        return guideText;
    }

    public void setGuideText(String guideText) {
        this.guideText = guideText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static final Creator<GatherModel> CREATOR = new Creator<GatherModel>() {
        @Override
        public GatherModel createFromParcel(Parcel in) {
            return new GatherModel(in);
        }

        @Override
        public GatherModel[] newArray(int size) {
            return new GatherModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(name);
        dest.writeString(guideText);
        dest.writeInt(id);

    }
}
