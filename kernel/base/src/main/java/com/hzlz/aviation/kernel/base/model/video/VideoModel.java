package com.hzlz.aviation.kernel.base.model.video;

import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.HERALD;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.LIVING;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.REVIEW;
import static com.hzlz.aviation.kernel.base.model.anotation.MediaType.COLLECTION_DETAIL;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.model.QualityComment;
import com.hzlz.aviation.kernel.base.model.QuestionModel;
import com.hzlz.aviation.kernel.base.model.anotation.MediaPrivacyTag;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.model.anotation.PlayerType;
import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.base.model.circle.FindCircleContent;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfo;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.ugcauth.AuthUgcReply;
import com.hzlz.aviation.kernel.base.tag.TagHelper;
import com.hzlz.aviation.kernel.base.utils.CompatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 视频模型
 */
public class VideoModel implements Parcelable {

    private transient VideoObservable mVideoObservable;

    /**
     * 作者对象
     */
    @NonNull
    private AuthorModel author;
    /**
     * 资源封面
     */
    private String coverUrl;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 资源描述
     */
    private String description;
    /**
     * 资源id
     */
    private String id;
    /**
     * 栏目，剧集id
     */
    private String columnId;
    /**
     * 资源大类 1: 长视频(一定是横视频样式） 2：短视频（一定是竖视频样式） 3：长音频 4：短音频  5横直播  6竖直播  7IM横直播  8IM竖直播
     */
    private @MediaType
    int mediaType;
    /**
     * 资源链接合集
     */
    private List<String> mediaUrls;
    /**
     * 缩略图图片资源链接合集
     */
    private List<String> imageUrls;
    /**
     * 缩略图图片资源链接合集，返回图片大小信息
     */
    private List<ImageModel> imageList;
    /**
     * 原图图片资源链接合集
     */
    private List<String> oriUrls;
    /**
     * 语音翻译文字
     */
    private String soundContent;
    /**
     * 语音长度
     */
    private int length;
    /**
     * 挂件模块
     */
    private PendantModel pendant;
    /**
     * 分享链接
     */
    private String shareUrl;
    /**
     * 原始数据运营配置标识
     */
    private int sourceType;
    /**
     * 资源标题
     */
    private String title;
    /**
     * 资源内容
     */
    private String content;
    /**
     * 标题tag类型
     */
    private @TagHelper.GvideoTagType
    int tagType;
    /**
     * 视频支持评论
     */
    private boolean canComment;
    /**
     * 隐私标记
     */
    private @MediaPrivacyTag
    int isPublic;
    /**
     * pptv vodId
     */
    private String vodId;

    /**
     * 评论数
     */
    private int reviews;
    /**
     * 是否喜欢
     */
    private int isFavor;
    /**
     * 喜欢总数
     */
    private int favors;


    /**
     * 问答时回答对应的评论id
     */
    private String commentId;
    /**
     * 是否对问答的回答进行点赞
     */
    private boolean isPraise;
    /**
     * 问答时回答的点赞总数
     */
    private int praiseTotal;

    /**
     * 直播状态   0 未开始 1 预告 2 直播中 3 回放 4 下架
     * <p>
     * {@link Constant.LIVE_TYPE}
     */
    private int liveBroadcastStatus;
    /**
     * 直播标签
     */
    private String liveBroadcastStatusStr;
    /**
     * 新闻来源
     */
    private String source;
    /**
     * 阅读量
     */
    private int pv;
    /**
     * 新闻标签
     */
    private String contentLabel;
    /**
     * 专题列表资源id
     */
    private String specialId;
    /**
     * 专题头图
     */
    private String headPic;
    /**
     * 专题大背景图
     */
    private String detailBigPic;
    /**
     * 专题收缩背景图
     */
    private String detailSmallPic;
    /**
     * 专题标签
     */
    private List<SpecialTagModel> specialTagList;

    /**
     * 动态关联圈子信息
     */
    private GroupInfo groupInfo;
    /**
     * UGC认证的用户回复的相关信息
     */
    private AuthUgcReply authUgcReply;

    public QualityComment qualityComment;

    /**
     * 埋点业务对象
     */
    private StatFromModel statFromModel;

    public int playType;

    /**
     * 内容属性标签
     */
    private List<String> labels;

    /**
     * 直播开始的时间
     */
    public Long liveBroadcastStartTime;

    /**
     * 问答id ,
     */
    private int answerSquareId;
    /**
     * 问答类型  1 提问  2 回答
     */
    private int answerSquareType;
    /**
     * 问答，被提问者id (回答者id)
     */
    private String mentorJid;

    private QuestionModel questionVO;

    /**
     * h5地址
     */
    private String linkUrl;
    /**
     * h5标题
     */
    private String linkTitle;

    // 链接标题
    public String outShareTitle;

    // 链接地址
    public String outShareUrl;

    // 播放时间
    public String playTime;

    // 信息流列表对应的tab名称
    public String tabName;

    // 如果不为3，那本条数据不能分享
    public Integer mediaStatus;

    // 视频总时长
    public Integer totalPlayDuration;

    // 携带的一些额外参数
    public Bundle bundle = new Bundle();

    public VideoModel() {
    }

    public VideoModel(WatchTvChannel watchTvChannel,Integer totalPlayDuration) {
        if (watchTvChannel == null) {
            return;
        }
        id = watchTvChannel.id + "";
        title = watchTvChannel.name;
        this.totalPlayDuration = totalPlayDuration;
        mediaType = COLLECTION_DETAIL;
    }

    public VideoModel(BannerModel.ImagesBean bean) {
        if (bean == null) {
            return;
        }
        setId(bean.getMediaId());
        setColumnId(bean.getProgramId());
        setTitle(bean.getLinkTitle());
        setMediaType(bean.getMediaType());

        List<String> list = new ArrayList<>();
        list.add(bean.getLinkUrl());
        setMediaUrls(list);
    }

    public VideoModel(String id,String title) {
        this.id = id;
        this.title = title;
    }

    public VideoModel(FindCircleContent findCircleContent) {
        if (findCircleContent == null) {
            return;
        }
        this.id = findCircleContent.mediaId;
        this.mediaType = findCircleContent.mediaType;
        this.title = findCircleContent.title;
    }

    protected VideoModel(Parcel in) {
        author = in.readParcelable(AuthorModel.class.getClassLoader());
        coverUrl = in.readString();
        description = in.readString();
        id = in.readString();
        columnId = in.readString();
        mediaType = in.readInt();
        mediaUrls = in.createStringArrayList();
        imageUrls = in.createStringArrayList();
        imageList = in.createTypedArrayList(ImageModel.CREATOR);
        oriUrls = in.createStringArrayList();
        soundContent = in.readString();
        length = in.readInt();
        pendant = in.readParcelable(PendantModel.class.getClassLoader());
        shareUrl = in.readString();
        sourceType = in.readInt();
        title = in.readString();
        content = in.readString();
        tagType = in.readInt();
        canComment = in.readByte() != 0;
        isPublic = in.readInt();
        vodId = in.readString();
        reviews = in.readInt();
        isFavor = in.readInt();
        favors = in.readInt();
        commentId = in.readString();
        isPraise = in.readByte() != 0;
        praiseTotal = in.readInt();
        liveBroadcastStatus = in.readInt();
        liveBroadcastStatusStr = in.readString();
        source = in.readString();
        pv = in.readInt();
        contentLabel = in.readString();
        specialId = in.readString();
        headPic = in.readString();
        detailBigPic = in.readString();
        detailSmallPic = in.readString();
        groupInfo = in.readParcelable(GroupInfo.class.getClassLoader());
        authUgcReply = in.readParcelable(AuthUgcReply.class.getClassLoader());
        qualityComment = in.readParcelable(QualityComment.class.getClassLoader());
        statFromModel = in.readParcelable(StatFromModel.class.getClassLoader());
        playType = in.readInt();
        labels = in.createStringArrayList();
        if (in.readByte() == 0) {
            liveBroadcastStartTime = null;
        } else {
            liveBroadcastStartTime = in.readLong();
        }
        answerSquareId = in.readInt();
        answerSquareType = in.readInt();
        mentorJid = in.readString();
        questionVO = in.readParcelable(QuestionModel.class.getClassLoader());
        linkUrl = in.readString();
        linkTitle = in.readString();
        outShareTitle = in.readString();
        outShareUrl = in.readString();
        playTime = in.readString();
        tabName = in.readString();
        if (in.readByte() == 0) {
            mediaStatus = null;
        } else {
            mediaStatus = in.readInt();
        }
        if (in.readByte() == 0) {
            totalPlayDuration = null;
        } else {
            totalPlayDuration = in.readInt();
        }
        bundle = in.readBundle(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(author, flags);
        dest.writeString(coverUrl);
        dest.writeString(description);
        dest.writeString(id);
        dest.writeString(columnId);
        dest.writeInt(mediaType);
        dest.writeStringList(mediaUrls);
        dest.writeStringList(imageUrls);
        dest.writeTypedList(imageList);
        dest.writeStringList(oriUrls);
        dest.writeString(soundContent);
        dest.writeInt(length);
        dest.writeParcelable(pendant, flags);
        dest.writeString(shareUrl);
        dest.writeInt(sourceType);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(tagType);
        dest.writeByte((byte) (canComment ? 1 : 0));
        dest.writeInt(isPublic);
        dest.writeString(vodId);
        dest.writeInt(reviews);
        dest.writeInt(isFavor);
        dest.writeInt(favors);
        dest.writeString(commentId);
        dest.writeByte((byte) (isPraise ? 1 : 0));
        dest.writeInt(praiseTotal);
        dest.writeInt(liveBroadcastStatus);
        dest.writeString(liveBroadcastStatusStr);
        dest.writeString(source);
        dest.writeInt(pv);
        dest.writeString(contentLabel);
        dest.writeString(specialId);
        dest.writeString(headPic);
        dest.writeString(detailBigPic);
        dest.writeString(detailSmallPic);
        dest.writeParcelable(groupInfo, flags);
        dest.writeParcelable(authUgcReply, flags);
        dest.writeParcelable(qualityComment, flags);
        dest.writeParcelable(statFromModel, flags);
        dest.writeInt(playType);
        dest.writeStringList(labels);
        if (liveBroadcastStartTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(liveBroadcastStartTime);
        }
        dest.writeInt(answerSquareId);
        dest.writeInt(answerSquareType);
        dest.writeString(mentorJid);
        dest.writeParcelable(questionVO, flags);
        dest.writeString(linkUrl);
        dest.writeString(linkTitle);
        dest.writeString(outShareTitle);
        dest.writeString(outShareUrl);
        dest.writeString(playTime);
        dest.writeString(tabName);
        if (mediaStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mediaStatus);
        }
        if (totalPlayDuration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalPlayDuration);
        }
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    public StatFromModel getStatFromModel() {
        return statFromModel;
    }

    public void setStatFromModel(StatFromModel statFromModel) {
        this.statFromModel = statFromModel;
    }

    public String getPid() {
        return statFromModel == null ? "" : statFromModel.pid;
    }

    public void setPid(String pid) {
        if (statFromModel == null){
            statFromModel = new StatFromModel();
        }
        statFromModel.pid = pid;
    }

    public Integer getMediaStatus() {
        return mediaStatus;
    }

    public void setMediaStatus(Integer mediaStatus) {
        this.mediaStatus = mediaStatus;
    }

    protected VideoObservable createObservable(String mediaId) {
        VideoObservable observable = new VideoObservable(mediaId);
        if (answerSquareId > 1) {
            observable.setDefaultCommentText("立即回答", "个回答");
            return observable;
        }
        return observable;
    }

    public VideoObservable getObservable() {
        if (mVideoObservable == null) {
            String id = getFavorObservableId();
            mVideoObservable = createObservable(id);
            if (isQaAnswerType()) {
                mVideoObservable.initCommentID(commentId);
            }
            mVideoObservable.getIsFavor().addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            if (mVideoObservable.getIsFavor() == sender) {
                                setIsFavor(mVideoObservable.getIsFavor().get() ? 1 : 0);
                            }
                        }
                    });
            mVideoObservable.getCommentCount().addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            if (mVideoObservable.getCommentCount() == sender) {
                                setReviews(mVideoObservable.getCommentCount().get());
                            }
                        }
                    });
            mVideoObservable.getFavorCount().addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            if (mVideoObservable.getFavorCount() == sender) {
                                setFavors(mVideoObservable.getFavorCount().get());
                            }
                        }
                    });
            updateFrom(this);
        }
        return mVideoObservable;
    }

    public void updateInteract() {
        if (author != null) {
            author.updateInteract();
        }

        String id = getFavorObservableId();
        if (InteractDataObservable.getInstance().containsFavorite(id)) {
            InteractDataObservable.getInstance().removeFavorite(id);
        }
        if (InteractDataObservable.getInstance().containsComment(id)) {
            InteractDataObservable.getInstance().removeComment(id);
        }
        getObservable();
    }

    public void updateFrom(VideoModel media) {
        if (media == null) {
            return;
        }
        setAuthor(media.getAuthor());
        setCoverUrl(media.getCoverUrl());
        setCreateDate(media.getCreateDate());
        setDescription(media.getDescription());
        setId(media.getId());
        setIsFavor(media.getIsFavor());
        setColumnId(media.getColumnId());
        setMediaType(media.getMediaType());
        setMediaUrls(media.getMediaUrls());
        setImageUrls(media.getImageUrls());
        setImageList(media.getImageList());
        setOriUrls(media.getOriUrls());
        setSoundContent(media.getSoundContent());
        setPendant(media.getPendant());
        setReviews(media.getReviews());
        setShareUrl(media.getShareUrl());
        setSourceType(media.getSourceType());
        setTagType(media.getTagType());
        setCanComment(media.isCanComment());
        setIsPublic(media.getIsPublic());
        setVodId(media.getVodId());
        setLiveBroadcastStatus(media.getLiveBroadcastStatus());
        setLiveBroadcastStatusStr(media.getLiveBroadcastStatusStr());
        setFavors(media.getFavors());
        setLength(media.getLength());
        setSource(media.getSource());
        setPv(media.getPv());
        setContentLabel(media.getContentLabel());
        setSpecialId(media.getSpecialId());
        setHeadPic(media.getHeadPic());
        setDetailBigPic(media.getDetailBigPic());
        setDetailSmallPic(media.getDetailSmallPic());
        setGroupInfo(media.getGroupInfo());
        setAuthUgcReply(media.getAuthUgcReply());
        setQualityComment(media.qualityComment);
        setLabels(media.labels);
        liveBroadcastStartTime = media.liveBroadcastStartTime;
        setAnswerSquareId(media.getAnswerSquareId());
        setAnswerSquareType(media.getAnswerSquareType());
        setMentorJid(media.getMentorJid());
        setQuestionVO(media.getQuestionVO());
        if (isQaAnswerType()) {
            setPraise(media.isPraise());
            setPraiseTotal(media.getPraiseTotal());
        }
        setCommentId(media.getCommentId());
        outShareTitle = media.outShareTitle;
        outShareUrl = media.outShareUrl;

        String content = media.getContent();
        String title = media.getTitle();
        setContent(content);
        setTitle(title);
        initContentAndTitle(content, title);
        setMediaStatus(media.getMediaStatus());
    }

    public String getMentorJid() {
        return mentorJid;
    }

    public void setMentorJid(String mentorJid) {
        this.mentorJid = mentorJid;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public AuthorModel getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull AuthorModel author) {
        this.author = author;

        if (mVideoObservable != null) {
            mVideoObservable.setCanShare(canShare());
        }
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
        if (mVideoObservable != null) {
            mVideoObservable.setCover(coverUrl);
        }
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;

        if (mVideoObservable != null) {
            mVideoObservable.setCreateDate(createDate);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public PendantModel getPendant() {
        return pendant;
    }

    public void setPendant(PendantModel pendant) {
        this.pendant = pendant;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (mVideoObservable != null) {
            mVideoObservable.setTitle(title);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if (mVideoObservable != null) {
            mVideoObservable.setContent(content);
        }
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public void initContentAndTitle(String content, String title) {
        if (mVideoObservable == null) {
            return;
        }
        boolean isContentEmpty = TextUtils.isEmpty(content);
        if (isContentEmpty && TextUtils.isEmpty(title)) {
            mVideoObservable.contentThanTitle.set("");
            return;
        }
        if (isContentEmpty) {
            mVideoObservable.contentThanTitle.set(title);
            return;
        }
        mVideoObservable.contentThanTitle.set(content);
    }

    public String getContentThanTitle() {
        if (answerSquareId > 0){
            return content;
        }
        if (!TextUtils.isEmpty(content)){
            return content;
        }
        return title;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public String getGroupName() {
        return (groupInfo == null) ? "" : groupInfo.getGroupName();
    }

    public String getGroupIntroduction() {
        return (groupInfo == null) ? "" : groupInfo.getGroupIntroduction();
    }

    public String getTopicName() {
        if (groupInfo == null) {
            return "";
        }
        return groupInfo.getTopicName();
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
        if (mVideoObservable == null) {
            return;
        }
        mVideoObservable.groupName.set(getGroupName());
        mVideoObservable.groupIntroduction.set(getGroupIntroduction());
    }

    public void setAuthUgcReply(AuthUgcReply authUgcReply) {
        this.authUgcReply = authUgcReply;
        if (mVideoObservable == null) {
            return;
        }
        if (authUgcReply == null || TextUtils.isEmpty(authUgcReply.content)) {
            mVideoObservable.authUgcReplyAvatar.set("");
            mVideoObservable.authUgcReplyContent.set("");
            mVideoObservable.showAuthUgc.set(false);
            return;
        }
        mVideoObservable.authUgcReplyAvatar.set(authUgcReply.avatar);
        mVideoObservable.authUgcReplyContent.set(authUgcReply.content);
        mVideoObservable.showAuthUgc.set(true);
    }

    public AuthUgcReply getAuthUgcReply() {
        return authUgcReply;
    }

    public void setQualityComment(QualityComment qualityComment) {
        this.qualityComment = qualityComment;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;

        if (mVideoObservable != null) {
            mVideoObservable.setCanShare(canShare());
        }
    }

    public String getVodId() {
        return vodId;
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    public int getReviews() {
        return reviews;
    }

    public int getFavors() {
        return favors;
    }

    public void setFavors(int favors) {
        this.favors = favors;
        if (mVideoObservable != null) {
            mVideoObservable.setFavorCount(favors);
        }
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
        if (mVideoObservable != null) {
            mVideoObservable.setCommentCount(reviews);
        }
    }

    public int getIsFavor() {
        return isFavor;
    }

    public void setIsFavor(int isFavor) {
        this.isFavor = isFavor;
        if (mVideoObservable != null) {
            mVideoObservable.setIsFavor(isFavor > 0);
        }
    }


    public int getLiveBroadcastStatus() {
        return liveBroadcastStatus;
    }

    public void setLiveBroadcastStatus(int liveBroadcastStatus) {
        this.liveBroadcastStatus = liveBroadcastStatus;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getOriUrls() {
        return oriUrls;
    }

    public void setOriUrls(List<String> oriUrls) {
        this.oriUrls = oriUrls;
    }

    public String getSoundContent() {
        return soundContent;
    }

    public void setSoundContent(String voiceContent) {
        this.soundContent = voiceContent;
    }

    /**
     * tag 是否 直播中
     */
    public boolean getIsLivingTag() {
        return liveBroadcastStatus == LIVING;
    }

    public boolean getIsHeraldTag() {
        return liveBroadcastStatus == HERALD;
    }

    public boolean getIsReviewTag() {
        return liveBroadcastStatus == REVIEW;
    }

    public String getLiveBroadcastStatusStr() {
        return liveBroadcastStatusStr;
    }

    public void setLiveBroadcastStatusStr(String liveBroadcastStatusStr) {
        this.liveBroadcastStatusStr = liveBroadcastStatusStr;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public String getContentLabel() {
        return contentLabel;
    }

    public void setContentLabel(String contentLabel) {
        this.contentLabel = contentLabel;
    }


    public String getSpecialId() {
        return specialId;
    }

    public void setSpecialId(String specialId) {
        this.specialId = specialId;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getDetailBigPic() {
        return detailBigPic;
    }

    public void setDetailBigPic(String detailBigPic) {
        this.detailBigPic = detailBigPic;
    }

    public String getDetailSmallPic() {
        return detailSmallPic;
    }

    public void setDetailSmallPic(String detailSmallPic) {
        this.detailSmallPic = detailSmallPic;
    }

    public List<SpecialTagModel> getSpecialTagList() {
        return specialTagList;
    }

    public void setSpecialTagList(List<SpecialTagModel> specialTagList) {
        this.specialTagList = specialTagList;
    }

    public int getAnswerSquareId() {
        return answerSquareId;
    }

    public void setAnswerSquareId(int answerSquareId) {
        this.answerSquareId = answerSquareId;
    }

    public int getAnswerSquareType() {
        return answerSquareType;
    }

    public void setAnswerSquareType(int answerSquareType) {
        this.answerSquareType = answerSquareType;
    }

    public QuestionModel getQuestionVO() {
        return questionVO;
    }

    public void setQuestionVO(QuestionModel questionVO) {
        this.questionVO = questionVO;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
        if (mVideoObservable != null) {
            mVideoObservable.setIsPraise(isPraise);
        }
    }

    public int getPraiseTotal() {
        return praiseTotal;
    }

    public void setPraiseTotal(int praiseTotal) {
        this.praiseTotal = praiseTotal;
        if (mVideoObservable != null) {
            mVideoObservable.setPraiseCount(praiseTotal);
        }
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public int getPlayType() {
        return playType;
    }

    public void setPlayType(int playType) {
        this.playType = playType;
    }

    public List<ImageModel> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageModel> imageList) {
        this.imageList = imageList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoModel that = (VideoModel) o;
        return isFavor == that.isFavor &&
                mediaType == that.mediaType &&
                reviews == that.reviews &&
//        favors == that.favors &&
                sourceType == that.sourceType &&
                tagType == that.tagType &&
                canComment == that.canComment &&
                isPublic == that.isPublic &&
                (CompatUtils.equals(author, that.author)) &&
                (CompatUtils.equals(coverUrl, that.coverUrl)) &&
                (CompatUtils.equals(createDate, that.createDate)) &&
                (CompatUtils.equals(description, that.description)) &&
                (CompatUtils.equals(id, that.id)) &&
                (CompatUtils.equals(columnId, that.columnId)) &&
                (CompatUtils.equals(mediaUrls, that.mediaUrls)) &&
                (CompatUtils.equals(imageUrls, that.imageUrls)) &&
                (CompatUtils.equals(imageList, that.imageList)) &&
                (CompatUtils.equals(oriUrls, that.oriUrls)) &&
                (CompatUtils.equals(soundContent, that.soundContent)) &&
                (CompatUtils.equals(pendant, that.pendant)) &&
                (CompatUtils.equals(shareUrl, that.shareUrl)) &&
                (CompatUtils.equals(title, that.title)) &&
                (CompatUtils.equals(vodId, that.vodId)) &&
                length == that.length &&
                (CompatUtils.equals(content, that.content)) &&
                (CompatUtils.equals(source, that.source)) &&
                pv == that.pv &&
                (CompatUtils.equals(contentLabel, that.contentLabel) &&
                        CompatUtils.equals(groupInfo, that.groupInfo)) &&
                (CompatUtils.equals(labels, that.labels)) &&
                (CompatUtils.equals(answerSquareId, that.answerSquareId)) &&
                (CompatUtils.equals(answerSquareType, that.answerSquareType)) &&
                (CompatUtils.equals(mentorJid, that.mentorJid)) &&
                (CompatUtils.equals(questionVO, that.questionVO));
    }

    @Override
    public int hashCode() {

        return CompatUtils.hash(author, coverUrl, createDate, description, id, columnId, isFavor, mediaType,
                mediaUrls, pendant, reviews, shareUrl, sourceType, title, tagType, canComment, isPublic,
                vodId, liveBroadcastStatus, liveBroadcastStatusStr, imageUrls, imageList, oriUrls, soundContent, favors, length, content,
                source, pv, contentLabel, specialId, headPic, detailBigPic, detailSmallPic, labels,
                answerSquareId, answerSquareType, mentorJid, questionVO);
    }

    public int getLiveTypeBackGround() {
        switch (liveBroadcastStatus) {
            case LIVING:
                return R.drawable.shape_gradient_f36486_e4344e_180_corners_2dp;
            case REVIEW:
                return R.drawable.shape_solid_45a4e5_corners_2dp;
            default:
                return R.drawable.shape_solid_eba647_corners_2dp;
        }
    }

    public int getMemoryAddress() {
        return super.hashCode();
    }

    //是否可以在列表中自动播放
    public boolean isCanAutoPlay() {
        return isValid() && (isMedia() || isImageAudioTxt());
    }

    public boolean canShare() {
        return true;
        // return isPublic != MediaPrivacyTag.PRIVATE && isPublic != MediaPrivacyTag.AUTHENTICATING;
    }

    public boolean isShortMedia() {
        return mediaType == MediaType.SHORT_VIDEO ||
                mediaType == MediaType.SHORT_AUDIO;
    }

    public boolean isMedia() {
        return mediaType == MediaType.SHORT_VIDEO || mediaType == MediaType.LONG_VIDEO ||
                mediaType == MediaType.SHORT_AUDIO || mediaType == MediaType.LONG_AUDIO;
    }

    public boolean isAudio() {
        return mediaType == MediaType.SHORT_AUDIO ||
                mediaType == MediaType.LONG_AUDIO;
    }

    public boolean isLiveMedia() {
        return mediaType == MediaType.VERTICAL_LIVE || mediaType == MediaType.HORIZONTAL_LIVE
                || mediaType == MediaType.IM_HORIZONTAL_LIVE || mediaType == MediaType.IM_VERTICAL_LIVE;
    }

    public boolean isAtyLiveMedia() {
        return mediaType == MediaType.VERTICAL_LIVE || mediaType == MediaType.HORIZONTAL_LIVE;
    }

    public boolean isVerticalMedia() {
        return mediaType == MediaType.VERTICAL_LIVE || mediaType == MediaType.IM_VERTICAL_LIVE
                || mediaType == MediaType.SHORT_VIDEO;
    }

    public boolean isNormalMedia() {
        return mediaType == MediaType.HORIZONTAL_LIVE || mediaType == MediaType.IM_HORIZONTAL_LIVE
                || mediaType == MediaType.LONG_VIDEO;
    }

    public boolean isImageAudioTxt() {
        return mediaType == MediaType.IMAGE_TXT || mediaType == MediaType.AUDIO_TXT;
    }

    public boolean isImageText() {
        return mediaType == MediaType.IMAGE_TXT;
    }

    public boolean isAudioTxt() {
        return mediaType == MediaType.AUDIO_TXT && mediaUrls != null && !mediaUrls.isEmpty();
    }

    public boolean haveImage() {
        return mediaType == MediaType.IMAGE_TXT && imageUrls != null && !imageUrls.isEmpty();
    }

    public boolean isNews() {
        return mediaType == MediaType.NEWS_IMAGE || mediaType == MediaType.NEWS_RIGHT_IMAGE ||
                mediaType == MediaType.NEWS_LINK;
    }

    public boolean haveAudio() {
        if (mediaType != MediaType.AUDIO_TXT) {
            return false;
        }
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            return false;
        }
        return !TextUtils.isEmpty(mediaUrls.get(0));
    }

    public String getUrl() {
        if (mediaUrls != null && !mediaUrls.isEmpty()) {
            return mediaUrls.get(0);
        }
        return "";
    }

    public @PlayerType
    int getPlayerType() {
        return PlayerType.GVIDEO;
    }

    //数据是否有效
    public boolean isValid() {

        //特殊新闻块无需检测有效性
        if (!MediaType.MediaTypeCheck.isNeedCheckData(mediaType)) {
            return true;
        }
        // 1. 本体字段
        if (TextUtils.isEmpty(id) || (TextUtils.isEmpty(shareUrl) && !isNews())) {
            return false;
        }
        if (!checkMediaUrlsValid(mediaUrls) && (isLiveMedia() || isShortMedia())) {
            return false;
        }
        if (!isMediaTypeValid(mediaType)) {
            return false;
        }
        // 2. 作者字段,新闻可以没有作者
//        if ((author == null || !author.isValid()) && !isNews()) {
//            return false;
//        }

        //动态资源
        if (isImageAudioTxt() && (!checkMediaUrlsValid(imageUrls) && !checkMediaUrlsValid(mediaUrls)
                && TextUtils.isEmpty(content) && TextUtils.isEmpty(title))) {
            return false;
        }

        return true;
    }

    /**
     * 检查链接合法性
     *
     * @return 数据合法性
     */
    private boolean checkMediaUrlsValid(List<String> urls) {
        if (urls == null) {
            return false;
        }
        for (String url : urls) {
            if (TextUtils.isEmpty(url)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 当前media type是否合法
     *
     * @param mediaType 待校验的mediaType数据
     * @return 合法性
     */
    private boolean isMediaTypeValid(int mediaType) {
        return MediaType.MediaTypeCheck.isMediaTypeValid(mediaType);
    }

    public static final class Builder {
        private AuthorModel author;
        private String coverUrl;
        private Date createData;
        private String description;
        private String id;
        private String columnId;
        private @MediaType
        int mediaType;
        private List<String> mediaUrls;
        private List<String> imageUrls;
        private List<String> oriUrls;
        private String soundContent;
        private PendantModel pendant;
        private String shareUrl;
        private int sourceType;
        private String title;
        private @TagHelper.GvideoTagType
        int tagType;
        private boolean canComment = true;
        private @MediaPrivacyTag
        int isPublic;
        private String vodId;
        private int isFavor;
        private int favors;
        private int reviews;
        private int liveBroadcastStatus;
        private String liveBroadcastStatusStr;
        private int length;
        private String content;
        private String source;
        private int pv;
        private String contentLabel;
        private String specialId;
        private String headPic;
        private String detailBigPic;
        private String detailSmallPic;
        private GroupInfo groupInfo;
        private AuthUgcReply authUgcReply;
        private QualityComment qualityComment;
        private List<String> labels;
        private Integer mediaStatus;

        private Builder() {
        }

        public static Builder aVideoModel() {
            return new Builder();
        }

        public Builder fromVideoModel(VideoModel videoModel) {
            this.author = videoModel.author;
            this.coverUrl = videoModel.coverUrl;
            this.createData = videoModel.createDate;
            this.description = videoModel.description;
            this.id = videoModel.id;
            this.columnId = videoModel.columnId;
            this.isFavor = videoModel.isFavor;
            this.mediaType = videoModel.mediaType;
            this.mediaUrls = videoModel.mediaUrls;
            this.imageUrls = videoModel.imageUrls;
            this.oriUrls = videoModel.oriUrls;
            this.soundContent = videoModel.soundContent;
            this.pendant = videoModel.pendant;
            this.reviews = videoModel.reviews;
            this.shareUrl = videoModel.shareUrl;
            this.sourceType = videoModel.sourceType;
            this.title = videoModel.title;
            this.tagType = videoModel.tagType;
            this.canComment = videoModel.canComment;
            this.isPublic = videoModel.isPublic;
            this.vodId = videoModel.vodId;
            this.liveBroadcastStatus = videoModel.liveBroadcastStatus;
            this.liveBroadcastStatusStr = videoModel.liveBroadcastStatusStr;
            this.favors = videoModel.favors;
            this.length = videoModel.length;
            this.content = videoModel.content;
            this.groupInfo = videoModel.groupInfo;
            this.authUgcReply = videoModel.authUgcReply;
            this.qualityComment = videoModel.qualityComment;
            this.labels = videoModel.labels;
            this.mediaStatus = videoModel.mediaStatus;
            return this;
        }

        public Builder withAuthor(AuthorModel author) {
            this.author = author;
            return this;
        }

        public Builder withCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
            return this;
        }

        public Builder withCreateData(Date createData) {
            this.createData = createData;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withMediaType(@MediaType int mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder withMediaUrls(List<String> mediaUrls) {
            this.mediaUrls = mediaUrls;
            return this;
        }

        public Builder withPendant(PendantModel pendant) {
            this.pendant = pendant;
            return this;
        }

        public Builder withReviews(int reviews) {
            this.reviews = reviews;
            return this;
        }

        public Builder withShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
            return this;
        }

        public Builder withSourceType(int sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withTagType(@TagHelper.GvideoTagType int tagType) {
            this.tagType = tagType;
            return this;
        }

        public Builder withIsFavor(int isFavor) {
            this.isFavor = isFavor;
            return this;
        }

        public Builder withColumnId(String columnId) {
            this.columnId = columnId;
            return this;
        }

        public Builder withCanComment(boolean canComment) {
            this.canComment = canComment;
            return this;
        }

        public Builder withIsPublic(@MediaPrivacyTag int isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder withVodId(String vodId) {
            this.vodId = vodId;
            return this;
        }

        public Builder withImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            return this;
        }


        public Builder withOriUrls(List<String> oriUrls) {
            this.oriUrls = oriUrls;
            return this;
        }

        public Builder withSoundContent(String soundContent) {
            this.soundContent = soundContent;
            return this;
        }

        public Builder withFavors(int favors) {
            this.favors = favors;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withSpecialId(String specialId) {
            this.specialId = specialId;
            return this;
        }

        public Builder withGroupInfo(GroupInfo groupInfo) {
            this.groupInfo = groupInfo;
            return this;
        }

        public Builder withQualityComment(QualityComment qualityComment) {
            this.qualityComment = qualityComment;
            return this;
        }

        public Builder withMediaStatus(Integer mediaStatus) {
            this.mediaStatus = mediaStatus;
            return this;
        }

        public VideoModel build() {
            VideoModel v = new VideoModel();
            v.setAuthor(author);
            v.setCoverUrl(coverUrl);
            v.setCreateDate(createData);
            v.setDescription(description);
            v.setId(id);
            v.setIsFavor(isFavor);
            v.setColumnId(columnId);
            v.setMediaType(mediaType);
            v.setMediaUrls(mediaUrls);
            v.setImageUrls(imageUrls);
            v.setOriUrls(oriUrls);
            v.setSoundContent(soundContent);
            v.setPendant(pendant);
            v.setReviews(reviews);
            v.setShareUrl(shareUrl);
            v.setSourceType(sourceType);
            v.setTitle(title);
            v.setTagType(tagType);
            v.setCanComment(canComment);
            v.setIsPublic(isPublic);
            v.setVodId(vodId);
            v.setLiveBroadcastStatus(liveBroadcastStatus);
            v.setLiveBroadcastStatusStr(liveBroadcastStatusStr);
            v.setFavors(favors);
            v.setLength(length);
            v.setContent(content);
            v.setSpecialId(specialId);
            v.setGroupInfo(groupInfo);
            v.setAuthUgcReply(authUgcReply);
            v.setQualityComment(qualityComment);
            v.setMediaStatus(mediaStatus);
            return v;
        }
    }

    public String getFavorObservableId() {
        if (isQaAnswerType()) {
            return "comment_" + commentId;
        } else {
            return id;
        }
    }

    public boolean isQaAnswerType() {
        return answerSquareId > 0 && answerSquareType == 2;
    }

    public String getQaTitle() {
        if (answerSquareId > 0 && answerSquareType != 2){
            return title;
        }
        return "";
    }

    public boolean isNeedShowQaTitle(){
        return answerSquareId > 0 && answerSquareType != 2;
    }

    public boolean isNotNormalType() {
        return answerSquareId > 0 && (answerSquareType == 1 || answerSquareType == 2);
    }

    public void saveValueToBundle(String key,Boolean value){
        if(bundle==null){
            bundle=new Bundle();
        }
        bundle.putBoolean(key,value);
    }

    public boolean getValueToBundle(String key) {
        if (bundle == null) {
            bundle = new Bundle();
            return false;
        }
        return bundle.getBoolean(key, false);
    }

}
