package com.hzlz.aviation.kernel.base.model.banner;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/6/19
 * desc : banner信息
 **/
public class BannerModel {

    private int id;
    private List<ImagesBean> images;
    private boolean isDefault;
    private String name;
    private int type;
    //轮播时间
    private int interval;

    private int height;
    private int width;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public static class ImagesBean {
        private int bannerId;
        private String mediaId;
        private String programId;
        private LinkParamBean linkParam;
        private String linkTitle;
        private String linkUrl;
        //是否加入圈子
        private boolean isJoined;
        private int mediaType;
        private String ossId;
        private String url;
        private String jumpMediaType;
        private String jumpAddress;
        private String bannerName;
        // 图片页面对象
        private Image image;
        // 弹窗次数
        private Integer popTime;

        public String getProgramId() {
            return programId;
        }

        public void setProgramId(String programId) {
            this.programId = programId;
        }

        public String getType(){
            return "图片";
        }

        public int getBannerId() {
            return bannerId;
        }

        public void setBannerId(int bannerId) {
            this.bannerId = bannerId;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public LinkParamBean getLinkParam() {
            return linkParam;
        }

        public void setLinkParam(LinkParamBean linkParam) {
            this.linkParam = linkParam;
        }

        public String getLinkTitle() {
            return TextUtils.isEmpty(linkTitle) ? "" : linkTitle;
        }

        public void setLinkTitle(String linkTitle) {
            this.linkTitle = linkTitle;
        }

        public String getLinkUrl() {
            return TextUtils.isEmpty(linkUrl) ? "" : linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

        public int getMediaType() {
            return mediaType;
        }

        public void setMediaType(int mediaType) {
            this.mediaType = mediaType;
        }

        public String getOssId() {
            return ossId;
        }

        public void setOssId(String ossId) {
            this.ossId = ossId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getJumpMediaType() {
            return TextUtils.isEmpty(jumpMediaType) ? "" : jumpMediaType;
        }

        public void setJumpMediaType(String jumpMediaType) {
            this.jumpMediaType = jumpMediaType;
        }

        public String getJumpAddress() {
            return TextUtils.isEmpty(jumpAddress) ? "" : jumpAddress;
        }

        public void setJumpAddress(String jumpAddress) {
            this.jumpAddress = jumpAddress;
        }

        public boolean isJoined() {
            return isJoined;
        }

        public void setJoined(boolean joined) {
            isJoined = joined;
        }

        public String getBannerName() {
            return TextUtils.isEmpty(bannerName) ? "" : bannerName;
        }

        public void setBannerName(String bannerName) {
            this.bannerName = bannerName;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public int getPopTime() {
            return popTime == null ? 0 : popTime;
        }

        public void setPopTime(Integer popTime) {
            this.popTime = popTime;
        }

        public static class LinkParamBean {
        }
    }

    public static class Image{
        // 宽
        @SerializedName("weight")
        private String width;
        // 高
        private String height;
        // 图片地址
        private String url;

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public float getWidthFloat(){
            try {
                return Float.parseFloat(width);
            }catch(Exception exception){
                return 0;
            }
        }

        public float getHeightFloat(){
            try {
                return Float.parseFloat(height);
            }catch(Exception exception){
                return 0;
            }
        }

        public float getWidthHeightRatio() {
            float widthFloat = getWidthFloat();
            float heightFloat = getHeightFloat();
            if(widthFloat <=0|| heightFloat <=0){
                return 0;
            }
            return widthFloat / heightFloat;
        }

    }

    public static boolean isDataValid(BannerModel bannerModel) {
        return bannerModel != null
                && bannerModel.images != null
                && !bannerModel.images.isEmpty();
    }
}
