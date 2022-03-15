package com.hzlz.aviation.feature.account.model;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.media.model.MediaModel;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/6/17
 * desc : 个人主页我的评论
 **/
public class UgcMyCommentModel extends CommentModel implements IAdapterModel {

    private MediaModel media;

    protected UgcMyCommentModel(Parcel in) {
        super(in);
    }

    public List<String> getImageList() {
        return imageList;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public int getLength() {
        return length;
    }

    public String getSoundContent() {
        return soundContent;
    }
    public String getContent() {
        return content;
    }

    public MediaModel getMedia() {
        return media;
    }

    public void setMedia(MediaModel media) {
        this.media = media;
    }

    @Override
    public void setModelPosition(int position) {

    }

    @NonNull
    @Override
    public ObservableInt getModelPosition() {
        return null;
    }
}
