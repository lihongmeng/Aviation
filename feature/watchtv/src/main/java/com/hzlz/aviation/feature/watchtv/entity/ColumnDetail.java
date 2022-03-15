package com.hzlz.aviation.feature.watchtv.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.hzlz.aviation.kernel.base.model.circle.GroupInfo;


public class ColumnDetail implements Parcelable {

    private String columnName;

    private String coverUrl;

    private String id;

    private String introduction;

    private ColumnGroupInfo group;

    protected ColumnDetail(Parcel in) {
        columnName = in.readString();
        coverUrl = in.readString();
        id = in.readString();
        introduction = in.readString();
        group = in.readParcelable(GroupInfo.class.getClassLoader());
    }

    public static final Creator<ColumnDetail> CREATOR = new Creator<ColumnDetail>() {
        @Override
        public ColumnDetail createFromParcel(Parcel in) {
            return new ColumnDetail(in);
        }

        @Override
        public ColumnDetail[] newArray(int size) {
            return new ColumnDetail[size];
        }
    };

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public ColumnGroupInfo getGroup() {
        return group;
    }

    public void setGroup(ColumnGroupInfo group) {
        this.group = group;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(columnName);
        parcel.writeString(coverUrl);
        parcel.writeString(id);
        parcel.writeString(introduction);
        parcel.writeParcelable(group, i);
    }
}