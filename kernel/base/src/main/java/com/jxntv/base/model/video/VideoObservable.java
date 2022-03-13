package com.jxntv.base.model.video;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.jxntv.base.R;
import com.jxntv.base.utils.FriendlyStringUtils;
import com.jxntv.base.utils.StringUtils;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.DateUtils;

import java.util.Date;

/**
 * 视频模型Observable
 */
public class VideoObservable {
    private String mDefaultCommentText;
    private String mDefaultCommentLastText;
    @NonNull
    private final ObservableBoolean isFavor;
    @NonNull
    private final ObservableInt favorColor = new ObservableInt();
    @NonNull
    private final ObservableInt favorCount;
    @NonNull
    private final ObservableField<String> favorText = new ObservableField<>();
    @NonNull
    private final ObservableInt commentCount;
    private final ObservableField<String> commentText = new ObservableField<>();
    @NonNull
    private final ObservableField<String> cover = new ObservableField<>();
    @NonNull
    private final ObservableBoolean canShare = new ObservableBoolean();
    @NonNull
    private final ObservableField<String> createDate = new ObservableField<>();
    @NonNull
    public final ObservableField<String> authUgcReplyAvatar = new ObservableField<>();
    @NonNull
    public final ObservableField<String> authUgcReplyContent = new ObservableField<>();
    @NonNull
    public final ObservableBoolean showAuthUgc = new ObservableBoolean();

    public ObservableField<String> groupName = new ObservableField<>();
    public ObservableField<String> groupIntroduction = new ObservableField<>();

    @NonNull
    private final ObservableField<String> title = new ObservableField<>();
    @NonNull
    private final ObservableField<String> content = new ObservableField<>();
    // 有些场景下，需要优先使用Content，再使用Title
    @NonNull
    public final ObservableField<String> contentThanTitle = new ObservableField<>();

    private boolean isQaComment = false;

    //如果信息流是一条评论
    @NonNull
    private ObservableBoolean isPraise;
    @NonNull
    private ObservableInt praiseCount = new ObservableInt();
    private final ObservableField<String> praiseText = new ObservableField<>();

    public VideoObservable(String mediaId) {
        isFavor = InteractDataObservable.getInstance().getFavorObservable(mediaId);
        commentCount = InteractDataObservable.getInstance().getCommentObservable(mediaId);
        favorCount = InteractDataObservable.getInstance().getFavorCountObservable(mediaId);
        commentCount.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (commentCount == sender) {
                    int count = commentCount.get();
                    String text = getCommentCountText(count, mDefaultCommentText, mDefaultCommentLastText);
                    commentText.set(text);
                }
            }
        });
        favorCount.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (favorCount == sender) {
                    int c = favorCount.get();
                    favorText.set(getFavoriteCountText(c, "喜欢"));
                }
            }
        });
        isFavor.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isFavor == sender) {
                    Context context = GVideoRuntime.getAppContext();
                    favorColor.set(isFavor.get() ? ContextCompat.getColor(context, R.color.color_e4344e) :
                            ContextCompat.getColor(context, R.color.color_333333));
                }
            }
        });

        int count = commentCount.get();
        String text = getCommentCountText(count, mDefaultCommentText, mDefaultCommentLastText);
        commentText.set(text);
        favorText.set(getFavoriteCountText(favorCount.get(), "喜欢"));
        Context context = GVideoRuntime.getAppContext();
        favorColor.set(isFavor.get() ? ContextCompat.getColor(context, R.color.color_e4344e) :
                ContextCompat.getColor(context, R.color.color_333333));
    }

    public void initCommentID(String commentId) {
        isQaComment = true;
        praiseText.set(getFavoriteCountText(praiseCount.get(), "喜欢"));
        isPraise = InteractDataObservable.getInstance().isPraiseCommentObservable(commentId);
        praiseCount = InteractDataObservable.getInstance().getPraiseCountObservable(commentId);

        isPraise.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isPraise == sender) {
                    Context context = GVideoRuntime.getAppContext();
                    favorColor.set(isPraise.get() ? ContextCompat.getColor(context, R.color.color_e4344e) :
                            ContextCompat.getColor(context, R.color.color_333333));
                }
            }
        });

        praiseCount.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (praiseCount == sender) {
                    int c = praiseCount.get();
                    praiseText.set(getFavoriteCountText(c, "喜欢"));
                }
            }
        });
    }

    public void setDefaultCommentText(String defaultCommentText) {
        setDefaultCommentText(defaultCommentText, "");
    }

    public void setDefaultCommentText(String defaultCommentText, String defaultCommentLastText) {
        mDefaultCommentText = defaultCommentText;
        mDefaultCommentLastText = defaultCommentLastText;
        int count = commentCount.get();
        String text = getCommentCountText(count, mDefaultCommentText, mDefaultCommentLastText);
        commentText.set(text);
    }

    public ObservableBoolean getIsFavor() {
        return isFavor;
    }

    public void setIsFavor(boolean isFavor) {
        this.isFavor.set(isFavor);
    }

    public ObservableInt getFavorCount() {
        return favorCount;
    }

    public void setFavorCount(int favorCount) {
        this.favorCount.set(favorCount);
    }

    public ObservableBoolean isFeedFavor() {
        if (isQaComment) {
            return isPraise;
        } else {
            return isFavor;
        }
    }

    public ObservableField<String> getFeedFavorText() {
        if (isQaComment) {
            return praiseText;
        } else {
            return favorText;
        }
    }

    public void setIsPraise(boolean isPraise) {
        if (this.isPraise != null) {
            this.isPraise.set(isPraise);
        }
    }

    public void setPraiseCount(int praiseCount) {
        if (this.praiseCount != null) {
            this.praiseCount.set(praiseCount);
            praiseText.set(getFavoriteCountText(praiseCount, "喜欢"));
        }
    }

    public ObservableBoolean getIsPraise() {
        return isPraise;
    }

    public ObservableField<String> getFavorText() {
        return favorText;
    }

    public ObservableInt getFavorColor() {
        return favorColor;
    }

    public ObservableField<String> getCommentText() {
        return commentText;
    }

    ObservableInt getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount.set(commentCount);
    }

    public ObservableField<String> getTitle() {
       return title;
    }

    public void setTitle(String title) {
        title = StringUtils.filterWhiteSpace(title);
        this.title.set(title);
    }

    public ObservableField<String> getContent() {
        return content;
    }

    public void setContent(String content) {
        content = StringUtils.filterWhiteSpace(content);
        this.content.set(content);
    }


    public ObservableField<String> getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover.set(cover);
    }

    public ObservableBoolean canShare() {
        return canShare;
    }

    public void setCanShare(boolean canShare) {
        this.canShare.set(canShare);
    }

    @NonNull
    public ObservableField<String> getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date date) {
        if (date == null) {
            this.createDate.set("");
            return;
        }
        String str = DateUtils.getChineseYMD(date);
        this.createDate.set(str);
    }

    /**
     * 格式化评论数
     */
    public static final String getCommentCountText(int count, String defaultText, String lastText) {
        if (count <= 0) {
            if (defaultText != null) {
                return defaultText;
            }
            return GVideoRuntime.getAppContext().getResources().getString(R.string.comment);
        }
        return FriendlyStringUtils.friendlyW(count) + (TextUtils.isEmpty(lastText) ? "" : lastText);
    }


    public static final String getFavoriteCountText(int count, String defaultText) {
        return getCommentCountText(count, defaultText, "");
    }

}
