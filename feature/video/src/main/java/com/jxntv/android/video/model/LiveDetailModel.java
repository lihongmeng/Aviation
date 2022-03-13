package com.jxntv.android.video.model;

import com.jxntv.android.video.model.anotation.AtyLiveStatus;
import com.jxntv.base.adapter.AbstractAdapterModel;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.anotation.MediaType;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc : 直播详情
 **/
public class LiveDetailModel extends AbstractAdapterModel {

    /**
     * 竖屏全屏直播模式
     */
    public final static int VERTICAL_FULL_PLAY_STYLE = 2;
    /**
     * 普通模式
     */
    public final static int NORMAL_PLAY_STYLE = 1;


    /**
     * broadcastDTO : {"id":8,"title":"123测试直播","thumbUrl":null,"createdDate":"2020-12-07 11:49:06","broadcastStatus":"直播中","description":"123测试直播简介","liveUrl":"www.baidu.com123","status":2,"mediaId":null,"playStyle":0,"defaultDesc":"欢迎来到直播间！本直播间提倡绿色健康直播，直播内容和评论严谨出现违法违规、低俗涩情、吸烟酗酒等内容，若有违反，将是情节严重程度对作者账号进行禁播或封停账号处理。"}
     * cerDTO : {"avatar":"f8ea51581c844f34b8f7289e20d2a7a0","name":"云游江西","id":2,"follower":0}
     */

    private BroadcastDTOBean broadcastDTO;
    private CerDTOBean cerDTO;

    public BroadcastDTOBean getBroadcastDTO() {
        return broadcastDTO;
    }

    public void setBroadcastDTO(BroadcastDTOBean broadcastDTO) {
        this.broadcastDTO = broadcastDTO;
    }

    public CerDTOBean getCerDTO() {
        return cerDTO;
    }

    public void setCerDTO(CerDTOBean cerDTO) {
        this.cerDTO = cerDTO;
    }

    public static class BroadcastDTOBean {
        /**
         * id : 8
         * title : 123测试直播
         * thumbUrl : 封面
         * createdDate : 2020-12-07 11:49:06
         * broadcastStatus : 直播中
         * description : 123测试直播简介
         * liveUrl : www.baidu.com123
         * status : 2
         * mediaId : null
         * playStyle : 0
         * defaultDesc : 欢迎来到直播间！本直播间提倡绿色健康直播，直播内容和评论严谨出现违法违规、低俗涩情、吸烟酗酒等内容，若有违反，将是情节严重程度对作者账号进行禁播或封停账号处理。
         */

        private int id;
        private String title;
        private String thumbUrl;
        private String createdDate;
        private String broadcastStatus;
        private String description;
        private String liveUrl;
        /**
         * 直播状态  1 预加  2 直播中  3 回放   4 下架
         */
        private @AtyLiveStatus int status;
        private String mediaId;
        //1 横屏直播  2 竖屏直播
        private int playStyle;
        private String defaultDesc;
        private String shareUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public boolean isVerticalPlayStyle(){
            return playStyle == VERTICAL_FULL_PLAY_STYLE;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

    }

    public static class CerDTOBean {
        /**
         * avatar : f8ea51581c844f34b8f7289e20d2a7a0
         * name : 云游江西
         * id : 2
         * follower : 0
         */

        private String avatar;
        private String name;
        private @AuthorType int authorType;
        private String id;
        private int follower;

        // mcn机构Id数组
        public List<Integer> tenantIdList;
        // mcn机构名称数组
        public List<String> tenantNameList;

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

        public int getAuthorType() {
            return authorType;
        }

        public void setAuthorType(int authorType) {
            this.authorType = authorType;
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
    }
}
