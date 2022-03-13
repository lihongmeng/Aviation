package com.jxntv.watchtv.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jxntv.base.model.circle.CircleModule;
import com.jxntv.base.model.circle.GroupInfo;
import com.jxntv.base.model.video.AuthorModel;

import java.util.List;

public class ColumnGroupInfo extends GroupInfo{

    //导师信息
    private List<AuthorModel> mentors;

    private CircleModule gather;

    protected ColumnGroupInfo(Parcel in) {
        super(in);
    }

    public List<AuthorModel> getMentors() {
        return mentors;
    }

    public void setMentors(List<AuthorModel> mentors) {
        this.mentors = mentors;
    }

    public CircleModule getGather() {
        return gather;
    }

    public void setGather(CircleModule gather) {
        this.gather = gather;
    }

}