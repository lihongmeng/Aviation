package com.hzlz.aviation.feature.live.model;


/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public class OpenLiveResultModel {

    //开播返回数据
    private String mediaId;
    private String shareUrl;

    //检查直播返回部分
    private int online;
    private String thumb;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
