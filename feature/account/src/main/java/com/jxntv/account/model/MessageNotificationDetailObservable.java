package com.jxntv.account.model;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.jxntv.account.R;
import com.jxntv.base.model.video.InteractDataObservable;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.DateUtils;

import java.util.Date;

/**
 * @since 2020-03-12 10:38
 */
public final class MessageNotificationDetailObservable {
    //<editor-fold desc="属性">
    /**
     * 头像 Url
     */
    @NonNull
    public ObservableField<Object> avatarUrl = new ObservableField<>();
    /**
     * 消息通知详情标题
     */
    @NonNull
    public ObservableField<String> title = new ObservableField<>();
    /**
     * 消息通知详情标题描述
     */
    public ObservableField<String> titleDescription = new ObservableField<>();
    /**
     * 消息通知详情内容
     */
    @NonNull
    public ObservableField<String> content = new ObservableField<>();
    /**
     * 消息图片 Url
     */
    @NonNull
    public ObservableField<String> imageUrl = new ObservableField<>();
    /**
     * 按钮文本
     */
    @NonNull
    public ObservableField<String> buttonText = new ObservableField<>();
    /**
     * 消息通知详情创建时间
     */
    @NonNull
    public ObservableField<String> createdAt = new ObservableField<>();
    /**
     * 是否关注
     */
    @NonNull
    public ObservableBoolean isFollow = new ObservableBoolean();
    /**
     * 内容图片
     */
    @NonNull
    public ObservableField<Object> mediaPic = new ObservableField<>();
    /**
     * 内容标题
     */
    @NonNull
    public ObservableField<String> mediaTitle = new ObservableField<>();
    /**
     * 时间右侧提示问题
     */
    @NonNull
    public ObservableField<String> timeRight = new ObservableField<>();
    /**
     * 是否是视频内容
     */
    @NonNull
    public ObservableBoolean isVideo = new ObservableBoolean();
    /**
     * 是否认证
     */
    public ObservableBoolean isAuth = new ObservableBoolean();

    private boolean isFollowMe = false;
    //</editor-fold>

    //<editor-fold desc="API">
    void setAvatarUrl(@Nullable Object avatarUrl) {
        this.avatarUrl.set(avatarUrl);
    }

    void setTitle(@Nullable String title) {
        this.title.set(title);
    }

    void setTitleDescription(@Nullable String titleDescription){
        this.titleDescription.set(titleDescription);
    }

    void setContent(@Nullable String content) {
        this.content.set(content);
    }

    void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl.set(imageUrl);
    }

    void setButtonText(@Nullable String buttonText) {
        this.buttonText.set(buttonText);
    }

    void setCreatedAt(@Nullable Date createdAt) {
        if (createdAt == null) {
            this.createdAt.set(null);
        } else {
            if (!TextUtils.isEmpty(timeRight.get())) {
                this.createdAt.set(DateUtils.friendlyTime2(createdAt) + "  " + timeRight.get());
            }else {
                this.createdAt.set(DateUtils.friendlyTime2(createdAt));
            }
        }
    }

    void setIsFollow(String mediaId, boolean isFollow, boolean isFollowMe) {
        this.isFollowMe = isFollowMe;
        InteractDataObservable.getInstance().setFollow(mediaId, isFollow);
        this.isFollow = InteractDataObservable.getInstance().getFollowObservable(mediaId);
    }

    void setMediaPic(Object mediaPic) {
        this.mediaPic.set(mediaPic);
    }

    void setMediaTitle(String mediaTitle) {
        this.mediaTitle.set(mediaTitle);
    }

    void setTimeRight(String txt) {
        this.timeRight.set(txt);
    }

    void setIsVideo(boolean isVideo) {
        this.isVideo.set(isVideo);
    }

    void setIsAuth(boolean isAuth){
        this.isAuth.set(isAuth);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public Drawable getFollowedDrawableRes(boolean isFollowed) {
        int res;
        if (isFollowMe) {
            if (isFollowed) {
                res = R.drawable.ic_notification_detail_follow_each;
            } else {
                res = R.drawable.ic_notification_detail_follow;
            }
        }else {
            if (isFollowed){
                res = R.drawable.ic_followed;
            }else {
                res = R.drawable.icon_follow;
            }
        }
        return GVideoRuntime.getAppContext().getResources().getDrawable(res);
    }

    //</editor-fold>
}
