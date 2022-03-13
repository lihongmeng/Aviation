package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import com.jxntv.base.model.video.AuthorModel;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/9/3
 * desc : 圈子详情问答广场对象
 **/
public class QaGroupListModel implements Parcelable {

    /**
     * 问答列表
     */
    private List<QaGroupModel> answerList;
    /**
     * 问答组件
     */
    private GatherModel gather;
    /**
     * 问答老师列表
     */
    private List<AuthorModel> mentorVoList;
    /**
     *  问答类型: 1-问答广场， 2-找老师
     */
    private int answerType;


    public List<AuthorModel> getMentorVoList() {
        return mentorVoList;
    }

    public void setMentorVoList(List<AuthorModel> mentorVoList) {
        this.mentorVoList = mentorVoList;
    }

    /**
     * 是否显示问答广场列表
     */
    public boolean isQAGroupType() {
        return answerType != 2;
    }

    public void setAnswerType(int answerType) {
        this.answerType = answerType;
    }

    public List<QaGroupModel> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<QaGroupModel> answerList) {
        this.answerList = answerList;
    }

    public GatherModel getGather() {
        return gather;
    }

    public void setGather(GatherModel gather) {
        this.gather = gather;
    }

    public static final Creator<QaGroupListModel> CREATOR = new Creator<QaGroupListModel>() {
        @Override
        public QaGroupListModel createFromParcel(Parcel in) {
            return new QaGroupListModel(in);
        }

        @Override
        public QaGroupListModel[] newArray(int size) {
            return new QaGroupListModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected QaGroupListModel(Parcel in){
        gather = in.readParcelable(GatherModel.class.getClassLoader());
        answerList = in.createTypedArrayList(QaGroupModel.CREATOR);
        mentorVoList = in.createTypedArrayList(AuthorModel.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeParcelable(gather,i);
        dest.writeTypedList(answerList);
        dest.writeTypedList(mentorVoList);
    }

    public boolean hasValidData(){
        return gather!=null || (mentorVoList!=null && mentorVoList.size()>0) || (answerList!=null && answerList.size()>0);
    }
}


