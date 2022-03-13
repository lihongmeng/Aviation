package com.jxntv.live.model;

/**
 * @author huangwei
 * date : 2021/3/10
 * desc :
 **/
public class LiveDetailModel {

    private CerDTOBean cerDTO;
    private DetailVOBean detailVO;

    public CerDTOBean getCerDTO() {
        return cerDTO;
    }

    public void setCerDTO(CerDTOBean cerDTO) {
        this.cerDTO = cerDTO;
    }

    public DetailVOBean getDetailVO() {
        return detailVO;
    }

    public void setDetailVO(DetailVOBean detailVO) {
        this.detailVO = detailVO;
    }

    public static class CerDTOBean {
        /**
         * avatar : http://gvideo-uat.oss-cn-hangzhou.aliyuncs.com/pgc-head/7218eeeb4a77468b9a2a79be6bce3a33?Expires=1615369490&OSSAccessKeyId=LTAI4FzAR3FTWRscE4XRNzgC&Signature=2V9QBIZ9yEMBNpM3rts9NvJ91WQ%3D
         * name : 爱现场
         * id : 1
         * follower : 0
         * authorType : 0
         */

        private String avatar;
        private String name;
        private String id;
        private int follower;
        private int authorType;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getFollower() {
            return follower;
        }

        public void setFollower(int follower) {
            this.follower = follower;
        }

        public int getAuthorType() {
            return authorType;
        }

        public void setAuthorType(int authorType) {
            this.authorType = authorType;
        }
    }

    public static class DetailVOBean {
        /**
         * id : 29
         * title : 今视频测试1号的直播间
         * thumbUrl : null
         * createdDate : 2021-03-09 10:59:48
         * broadcastStatus : 下架
         * description : null
         * liveUrl : http://jinshipin.live.jxtvcn.com.cn/live/380830362896
         * status : 3
         * mediaId : 69816
         * playStyle : 2
         * defaultDesc : 欢迎来到直播间！本直播间提倡绿色健康直播，直播内容和评论严谨出现违法违规、低俗涩情、吸烟酗酒等内容，若有违反，将是情节严重程度对作者账号进行禁播或封停账号处理。
         * shareUrl : null
         * type: 直播类型{@link com.jxntv.base.Constant.LIVE_ROOM_TYPE}
         */
        private String id;
        //主播ID
        private String jid;
        private String title;
        private String thumbUrl;
        private String createdDate;
        private String broadcastStatus;
        private String description;
        private String liveUrl;
        private int status;
        private String mediaId;
        private int playStyle;
        private String defaultDesc;
        private String shareUrl;
        public int type;
        public int connectVideo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getJid() {
            return jid;
        }

        public void setJid(String jid) {
            this.jid = jid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }

        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getBroadcastStatus() {
            return broadcastStatus;
        }

        public void setBroadcastStatus(String broadcastStatus) {
            this.broadcastStatus = broadcastStatus;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLiveUrl() {
            return liveUrl;
        }

        public void setLiveUrl(String liveUrl) {
            this.liveUrl = liveUrl;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public int getPlayStyle() {
            return playStyle;
        }

        public void setPlayStyle(int playStyle) {
            this.playStyle = playStyle;
        }

        public String getDefaultDesc() {
            return defaultDesc;
        }

        public void setDefaultDesc(String defaultDesc) {
            this.defaultDesc = defaultDesc;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }
    }
}
