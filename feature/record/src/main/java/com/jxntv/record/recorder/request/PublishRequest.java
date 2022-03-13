package com.jxntv.record.recorder.request;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.record.recorder.api.RecordApi;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

import static com.jxntv.base.Constant.BUNDLE_KEY.OUT_SHARE_TITLE;
import static com.jxntv.base.Constant.BUNDLE_KEY.OUT_SHARE_URL;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_CONTENT;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_FILE_NAME;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_FILE_TYPE;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_GROUP_ID;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_IMAGE_ID;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_IMAGE_LIST;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_INTRODUCTION;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_IS_PUBLIC;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_IS_QA;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_LENGTH;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_LINK_TITLE;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_LINK_VALUE;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_PLAY_STYLE;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_SOUND_CONTENT;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_TOPIC_ID;
import static com.jxntv.record.recorder.Constants.NET_BODY_KEY_VIDEO_ID;

public class PublishRequest extends BaseGVideoMapRequest<Integer> {

    public void setContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        mParameters.put(NET_BODY_KEY_CONTENT, content);
    }

    public void setFileType(Integer fileType) {
        if (fileType == null) {
            return;
        }
        mParameters.put(NET_BODY_KEY_FILE_TYPE, fileType);
    }

    public void setImageId(String imageId) {
        if (TextUtils.isEmpty(imageId)) {
            return;
        }
        mParameters.put(NET_BODY_KEY_IMAGE_ID, imageId);
    }

    public void setImageList(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }
        mParameters.put(NET_BODY_KEY_IMAGE_LIST, imageList);
    }

    public void setIntroduction(String introduction) {
        if (TextUtils.isEmpty(introduction)) {
            return;
        }
        mParameters.put(NET_BODY_KEY_INTRODUCTION, introduction);
    }

    public void setIsPublic(boolean isPublic) {
        mParameters.put(NET_BODY_KEY_IS_PUBLIC, isPublic);
    }

    public void setLength(String length) {
        if (TextUtils.isEmpty(length)) {
            return;
        }
        mParameters.put(NET_BODY_KEY_LENGTH, length);
    }

    public void setSoundContent(String soundContent) {
        if (TextUtils.isEmpty(soundContent)) {
            return;
        }
        mParameters.put(NET_BODY_KEY_SOUND_CONTENT, soundContent);
    }

    public void setVideoId(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        mParameters.put(NET_BODY_KEY_VIDEO_ID, videoId);
    }

    public void setTopicId(Long topicId) {
        if (topicId == null) {
            return;
        }
        mParameters.put(NET_BODY_KEY_TOPIC_ID, topicId);
    }

    public void setGroupId(Long groupId) {
        if (groupId == null) {
            return;
        }
        mParameters.put(NET_BODY_KEY_GROUP_ID, groupId);
    }

    public void setGatherId(Long gatherId) {
        if (gatherId == null || gatherId <= 0) {
            return;
        }
        mParameters.put("gatherId", gatherId);
    }

    public void setFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        mParameters.put(NET_BODY_KEY_FILE_NAME, fileName);
    }

    public void setOutShareTitle(String outShareTitle) {
        if (TextUtils.isEmpty(outShareTitle)) {
            return;
        }
        mParameters.put(OUT_SHARE_TITLE, outShareTitle);
    }

    public void setOutShareUrl(String outShareUrl) {
        if (TextUtils.isEmpty(outShareUrl)) {
            return;
        }
        mParameters.put(OUT_SHARE_URL, outShareUrl);
    }

    public void setPlayStyle(Integer playStyle) {
        if (playStyle == null) {
            return;
        }
        mParameters.put(NET_BODY_KEY_PLAY_STYLE, playStyle);
    }

    /**
     * 是否是问答
     *
     * @param mentorJid 指定回答者id
     */
    public void setIsQa(boolean isQa, String mentorJid){
        mParameters.put(NET_BODY_KEY_IS_QA, isQa);
        mParameters.put("mentorJid", mentorJid);
    }


    @Override
    protected int getMaxParameterCount() {
        return 12;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return RecordApi.Instance.get().publish(mParameters);
    }
}
