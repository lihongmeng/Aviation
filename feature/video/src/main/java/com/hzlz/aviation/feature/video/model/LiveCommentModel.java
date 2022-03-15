package com.hzlz.aviation.feature.video.model;

import android.text.TextUtils;

import com.hzlz.aviation.kernel.base.adapter.AbstractAdapterModel;

import java.util.List;

public class LiveCommentModel {

    private List<CommentLstBean> commentLst;
    private int status;
    private String lastAuditTime;
    private String liveUrl;

    public List<CommentLstBean> getCommentLst() {
        return commentLst;
    }

    public void setCommentLst(List<CommentLstBean> commentLst) {
        this.commentLst = commentLst;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getLastAuditTime() {
        return lastAuditTime;
    }

    public void setLastAuditTime(String lastAuditTime) {
        this.lastAuditTime = lastAuditTime;
    }

    public static class CommentLstBean extends AbstractAdapterModel {
        /**
         * name : 小乌龟2
         * message : 呵呵1
         * userId : 1
         * createdDate : 2021-02-23 10:33:57
         * images : [{"url":"http://gvideo-uat.oss-cn-hangzhou.aliyuncs.com/media-cover/028811869be74e46abfd6143c50b192a?Expires=1614133277&OSSAccessKeyId=LTAI4FzAR3FTWRscE4XRNzgC&Signature=%2Fe9aXSXXT%2FAau%2FG490q0NGmeKy0%3D","link":"www.baidu.com"}]
         * links : [{"text":"111111","link":"www.baidu.com"}]
         */

        private String name;
        private String message;
        private int userId;
        private String createdDate;
        private List<ImagesBean> images;
        private List<LinksBean> links;

        public String getName() {
            return name;
        }

        public String getShowName() {
            return TextUtils.isEmpty(name) ? "" : name + "：";
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public List<LinksBean> getLinks() {
            return links;
        }

        public void setLinks(List<LinksBean> links) {
            this.links = links;
        }

        public static class ImagesBean extends AbstractAdapterModel {
            /**
             * url : http://gvideo-uat.oss-cn-hangzhou.aliyuncs.com/media-cover/028811869be74e46abfd6143c50b192a?Expires=1614133277&OSSAccessKeyId=LTAI4FzAR3FTWRscE4XRNzgC&Signature=%2Fe9aXSXXT%2FAau%2FG490q0NGmeKy0%3D
             * link : www.baidu.com
             */

            private String url;
            private String link;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }
        }

        public static class LinksBean extends AbstractAdapterModel {
            /**
             * text : 111111
             * link : www.baidu.com
             */

            private String text;
            private String link;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }
        }
    }
}
