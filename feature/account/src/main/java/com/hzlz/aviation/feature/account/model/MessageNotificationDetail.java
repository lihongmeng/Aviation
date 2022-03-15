package com.hzlz.aviation.feature.account.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType;
import com.hzlz.aviation.kernel.base.adapter.AbstractAdapterModel;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.library.util.ResourcesUtils;

import java.util.Date;

/**
 * 消息通知详情
 *
 *
 * @since 2020-03-12 09:56
 */
public final class MessageNotificationDetail extends AbstractAdapterModel {
  //<editor-fold desc="属性">
  /**  可以是作者 id, 也可能是是用户 id  */
  @Nullable
  @SerializedName("sourceId")
  private String mSourceId;
  /** 消息详情 id */
  @Nullable
  @SerializedName("id")
  private String mId;
  /** 消息通知类型 */
  @MessageNotificationType
  @SerializedName("type")
  private int mType;
  /** 头像 Url */
  @Nullable
  @SerializedName("avatarUrl")
  private String mAvatarUrl;
  /** 消息通知详情标题 */
  @Nullable
  @SerializedName("title")
  private String mTitle;
  @Nullable
  @SerializedName("titleDescription")
  private String mTitleDescription;
  /** 消息通知详情内容 */
  @Nullable
  @SerializedName("content")
  private String mContent;
  /** 链接 Url */
  @Nullable
  @SerializedName("linkUrl")
  private String mLinkUrl;
  /** 消息图片 Url */
  @Nullable
  @SerializedName("imageUrl")
  private String mImageUrl;
  /** 按钮文本 */
  @Nullable
  @SerializedName("buttonText")
  private String mButtonText;
  /** 消息通知详情创建时间 */
  @Nullable
  @SerializedName("createdAt")
  private Date mCreatedAt;
  /** 是否已读 */
  @SerializedName("hasRead")
  private boolean mHasRead;

  /** 列表消息类型 */
  @SerializedName("msgType")
  private int msgType;
  /** 关注 */
  @SerializedName("follow")
  private Follow mFollow;

  @SerializedName("comment")
  private Comment mComment;

  private Auth auth;

  private int answerSquareId;

  // 自定义属性
  @Nullable
  private transient MessageNotificationDetailObservable mMessageNotificationDetailObservable;
  //</editor-fold>

  //<editor-fold desc="API">

  public void update(@NonNull MessageNotificationDetail messageNotificationDetail) {
    setSourceId(messageNotificationDetail.mSourceId);
    setId(messageNotificationDetail.mId);
    setType(messageNotificationDetail.mType);
    setAvatarUrl(messageNotificationDetail.mAvatarUrl);
    setTitle(messageNotificationDetail.mTitle);
    setTitleDescription(messageNotificationDetail.mTitleDescription);
    setContent(messageNotificationDetail.mContent);
    setLinkUrl(messageNotificationDetail.mLinkUrl);
    setImageUrl(messageNotificationDetail.mImageUrl);
    setButtonText(messageNotificationDetail.mButtonText);
    setCreatedAt(messageNotificationDetail.mCreatedAt);
    setHasRead(messageNotificationDetail.mHasRead);
    if (messageNotificationDetail.mFollow!=null) {
      setFollow(messageNotificationDetail.mFollow.isFollow,messageNotificationDetail.mFollow.beFollow);
    }
    if (messageNotificationDetail.auth!=null){
        if (mMessageNotificationDetailObservable!=null){
            mMessageNotificationDetailObservable.setIsAuth(messageNotificationDetail.auth.isAuth);
        }
    }
    if (messageNotificationDetail.mComment!=null) {
      setMediaPic();
      setMediaTitle();
    }
  }

  @NonNull
  public MessageNotificationDetailObservable getMessageNotificationDetailObservable() {
    if (mMessageNotificationDetailObservable == null) {
      mMessageNotificationDetailObservable = new MessageNotificationDetailObservable();
      //
      update(this);
    }
    return mMessageNotificationDetailObservable;
  }

  //</editor-fold>

  //<editor-fold desc="Setter">

  public void setSourceId(@Nullable String sourceId) {
    mSourceId = sourceId;
  }

  public void setId(@Nullable String id) {
    mId = id;
  }

  public void setType(@MessageNotificationType int type) {
    mType = type;
  }

  public void setAvatarUrl(@Nullable String avatarUrl) {
    mAvatarUrl = avatarUrl;
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setAvatarUrl(avatarUrl);
    }
  }
  public void setAvatarIcon(@Nullable int resId) {
    mAvatarUrl = "";
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setAvatarUrl(resId);
    }
  }

  public void setTitle(@Nullable String title) {
    mTitle = title;
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setTitle(title);
    }
  }

  public void setTitleDescription(@Nullable String titleDescription){
    mTitleDescription = titleDescription;
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setTitleDescription(titleDescription);
    }
  }

  public void setContent(@Nullable String content) {
    mContent = content;
    if (mMessageNotificationDetailObservable != null) {
      if (mComment!=null && !TextUtils.isEmpty(mComment.content)){
        mMessageNotificationDetailObservable.setContent(mComment.content);
        mMessageNotificationDetailObservable.setTimeRight(mContent);
      }else {
        mMessageNotificationDetailObservable.setTimeRight("");
        mMessageNotificationDetailObservable.setContent(mContent);
      }
    }
  }

  public void setLinkUrl(@Nullable String linkUrl) {
    mLinkUrl = linkUrl;
  }

  public void setImageUrl(@Nullable String imageUrl) {
    mImageUrl = imageUrl;
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setImageUrl(imageUrl);
    }
  }

  public void setButtonText(@Nullable String buttonText) {
    mButtonText = buttonText;
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setButtonText(buttonText);
    }
  }

  public void setCreatedAt(@Nullable Date createdAt) {
    mCreatedAt = createdAt;
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setCreatedAt(createdAt);
    }
  }

  public void setHasRead(boolean hasRead) {
    mHasRead = hasRead;
  }

  public void setFollow(boolean follow, boolean isFollowMe){
    if (mFollow!=null){
      mFollow.setFollow(follow);
    }
    if (mMessageNotificationDetailObservable != null) {
      mMessageNotificationDetailObservable.setIsFollow(mSourceId,follow,isFollowMe);
    }
  }

  //</editor-fold>

  //<editor-fold desc="Getter">

  @Nullable
  public String getSourceId() {
    return mSourceId;
  }

  @Nullable
  public String getId() {
    return mId;
  }

  @MessageNotificationType
  public int getType() {
    return mType;
  }

  @Nullable
  public String getAvatarUrl() {
    return mAvatarUrl;
  }

  @Nullable
  public String getTitle() {
    return mTitle;
  }

  @Nullable
  public String getContent() {
    return mContent;
  }

  @Nullable
  public String getLinkUrl() {
    return mLinkUrl;
  }

  @Nullable
  public String getImageUrl() {
    return mImageUrl;
  }

  @Nullable
  public String getButtonText() {
    return mButtonText;
  }

  @Nullable
  public Date getCreatedAt() {
    return mCreatedAt;
  }

  public boolean hasRead() {
    return mHasRead;
  }

  public int getMsgType() {
    return msgType;
  }

  public void setMsgType(int msgType) {
    this.msgType = msgType;
  }

  public int getAnswerSquareId() {
    return answerSquareId;
  }

  public void setAnswerSquareId(int answerSquareId) {
    this.answerSquareId = answerSquareId;
  }

  public Comment getComment() {
    return mComment;
  }

  @Nullable
  public String getTitleDescription() {
    return mTitleDescription;
  }

  public class Comment{
    private String mediaId;
    private int mediaType;
    //资源缩略图
    private String mediaPic;
    //资源标题
    private String mediaTitle;
    //评论/回复ID
    private long commentId;
    //评论/回复文本内容
    private String content;
    //评论类型：0-评论、1-回复
    private int type;
    //平台类型：0-今视频、1-赣云
    private String platform;
    //栏目ID
    private String categoryId;

    public String getMediaId() {
      return mediaId;
    }

    public int getMediaType() {
      return mediaType;
    }

    public String getMediaPic() {
      return mediaPic;
    }

    public String getMediaTitle() {
      return mediaTitle;
    }

    public long getCommentId() {
      return commentId;
    }

    public String getContent() {
      return content;
    }

    public int getType() {
      return type;
    }

    public String getPlatform() {
      return platform;
    }

    public String getCategoryId() {
      return categoryId;
    }
  }

  public class Follow{
    //Ta是否关注我
    private boolean isFollow;
    //我是否关注Ta
    private boolean beFollow;

    public boolean isBeFollow() {
      return beFollow;
    }

    public void setBeFollow(boolean beFollow) {
      this.beFollow = beFollow;
    }

    public boolean isFollow() {
      return isFollow;
    }

    public void setFollow(boolean follow) {
      isFollow = follow;
    }
  }

  /**
   * 认证信息
   */
  public class Auth{
    private boolean isAuth;
    private String authIntro;

  }

  public void setMediaPic(){
    if (mMessageNotificationDetailObservable!=null) {
      if (!TextUtils.isEmpty(mComment.mediaPic)){
        mMessageNotificationDetailObservable.setMediaPic(mComment.mediaPic);
      }else if (mComment.mediaType == MediaType.AUDIO_TXT){
        mMessageNotificationDetailObservable.setMediaPic(R.drawable.ic_notification_sound);
      }else {
        mMessageNotificationDetailObservable.setMediaPic(R.drawable.default_imageload_bg);
      }
    }
  }

  public void setMediaTitle(){
    if(mMessageNotificationDetailObservable==null){
      return;
    }
    if (TextUtils.isEmpty(mComment.mediaPic)
            && mComment.mediaType != MediaType.AUDIO_TXT){
      if(TextUtils.isEmpty(mComment.mediaTitle)){
        mMessageNotificationDetailObservable.setMediaTitle(ResourcesUtils.getString(R.string.link_with_bracket));
      }else{
        mMessageNotificationDetailObservable.setMediaTitle(mComment.mediaTitle);
      }
    }
    mMessageNotificationDetailObservable.setIsVideo(MediaType.SHORT_VIDEO == mComment.mediaType
            || MediaType.LONG_VIDEO == mComment.mediaType);
  }


  //</editor-fold>
}
