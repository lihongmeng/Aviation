package com.hzlz.aviation.kernel.base.model.share;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;

/**
 * 分享模块入口数据
 */
public class ShareDataModel implements Parcelable {
    private String mTitle;
    private String mDescription;
    private String mShareUrl;
    private String mImage;

    private String mMediaId;
    private String mAuthorId;
    private String mAuthorName;
    private @AuthorType int mAuthorType;
    private boolean mIsFavorite;
    private boolean mIsFollow;

    private String mReportUrl;

    private boolean mShowShare = true;
    private boolean mShowDelete = false;
    private boolean mShowCreateBill = false;
    private boolean mShowFollow = true;
    private boolean mShowFontSetting = false;
    private boolean mShowReport = true;
    private boolean mShowFavorite = true;
    private boolean mContentIsComment;

    public String extraData;

    @Nullable
    private VideoModel mVideoModel;

    private ShareDataModel() {
    }

    protected ShareDataModel(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
        mShareUrl = in.readString();
        mImage = in.readString();
        mMediaId = in.readString();
        mAuthorId = in.readString();
        mAuthorName = in.readString();
        mAuthorType = in.readInt();
        mIsFavorite = in.readByte() != 0;
        mIsFollow = in.readByte() != 0;
        mReportUrl = in.readString();
        mShowShare = in.readByte() != 0;
        mShowDelete = in.readByte() != 0;
        mShowCreateBill = in.readByte() != 0;
        mShowFollow = in.readByte() != 0;
        mShowFontSetting = in.readByte() != 0;
        extraData = in.readString();
        mVideoModel = in.readParcelable(VideoModel.class.getClassLoader());
        mContentIsComment = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mShareUrl);
        dest.writeString(mImage);
        dest.writeString(mMediaId);
        dest.writeString(mAuthorId);
        dest.writeString(mAuthorName);
        dest.writeInt(mAuthorType);
        dest.writeByte((byte) (mIsFavorite ? 1 : 0));
        dest.writeByte((byte) (mIsFollow ? 1 : 0));
        dest.writeString(mReportUrl);
        dest.writeByte((byte) (mShowShare ? 1 : 0));
        dest.writeByte((byte) (mShowDelete ? 1 : 0));
        dest.writeByte((byte) (mShowCreateBill ? 1 : 0));
        dest.writeByte((byte) (mShowFollow ? 1 : 0));
        dest.writeByte((byte) (mShowFontSetting ? 1 : 0));
        dest.writeString(extraData);
        dest.writeParcelable(mVideoModel, flags);
        dest.writeByte((byte) (mContentIsComment ? 1 : 0));
    }

    public String getPid(){
        return mVideoModel == null ? "" : mVideoModel.getPid();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShareDataModel> CREATOR = new Creator<ShareDataModel>() {
        @Override
        public ShareDataModel createFromParcel(Parcel in) {
            return new ShareDataModel(in);
        }

        @Override
        public ShareDataModel[] newArray(int size) {
            return new ShareDataModel[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getUrl() {
        return mShareUrl;
    }

    public String getImage() {
        return mImage;
    }

    public String getMediaId() {
        return mMediaId;
    }

    public String getAuthorId() {
        return mAuthorId;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public @AuthorType
    int getAuthorType() {
        return mAuthorType;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public boolean isFollow() {
        return mIsFollow;
    }

    public String getReportUrl() {
        return mReportUrl;
    }

    public boolean isShowShare() {
        return mShowShare;
    }

    public boolean isShowDelete() {
        return mShowDelete;
    }

    public boolean isShowCreateBill() {
        return mShowCreateBill;
    }

    public boolean isShowFollow() {
        return mShowFollow;
    }

    public boolean isShowFontSetting() {
        return mShowFontSetting;
    }

    public boolean isShowReport() {
        return mShowReport;
    }

    public boolean isShowFavorite() {
        return mShowFavorite;
    }

    public boolean isContentIsComment() {
        return mContentIsComment;
    }

    public void setContentIsComment(boolean mContentIsComment) {
        this.mContentIsComment = mContentIsComment;
    }

    @Nullable
    public VideoModel getVideoModel() {
        return mVideoModel;
    }

    public static class Builder {
        @Nullable
        private VideoModel mVideoModel;

        private String mTitle;
        private String mDescription;
        private String mUrl;
        private String mImage;

        private String mMediaId;
        private String mAuthorId;
        private String mAuthorName;
        private @AuthorType
        int mAuthorType;
        private boolean mIsFavorite;
        private boolean mIsFollow;

        private String mReportUrl;

        private boolean mShowShare = true;
        private boolean mShowDelete = false;
        private boolean mShowCreateBill = false;
        private boolean mShowFollow = true;
        private boolean mShowReport = true;
        private boolean mShowFavorite = true;
        private boolean mShowFontSetting = false;
        private boolean mContentIsComment = false;
        private String extraData;

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setDescription(String description) {
            mDescription = description;
            return this;
        }

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        public Builder setImage(String image) {
            mImage = image;
            return this;
        }

        public Builder setMediaId(String mediaId) {
            this.mMediaId = mediaId;
            return this;
        }

        public Builder setAuthorId(String authorId) {
            this.mAuthorId = authorId;
            return this;
        }

        public Builder setAuthorName(String authorName) {
            this.mAuthorName = authorName;
            return this;
        }

        public Builder setAuthorType(@AuthorType int authorType) {
            this.mAuthorType = authorType;
            return this;
        }

        public Builder setFavorite(boolean isFavorite) {
            this.mIsFavorite = isFavorite;
            return this;
        }

        public Builder setFollow(boolean isFollow) {
            this.mIsFollow = isFollow;
            return this;
        }

        public Builder setReportUrl(String reportUrl) {
            this.mReportUrl = reportUrl;
            return this;
        }

        public Builder setShowShare(boolean mShowShare) {
            this.mShowShare = mShowShare;
            return this;
        }

        public Builder setShowDelete(boolean mShowDelete) {
            this.mShowDelete = mShowDelete;
            return this;
        }

        public Builder setShowFavorite(boolean mShowFavorite) {
            this.mShowFavorite = mShowFavorite;
            return this;
        }

        public Builder setShowReport(boolean mShowReport) {
            this.mShowReport = mShowReport;
            return this;
        }

        public Builder setShowCreateBill(boolean mShowCreateBill) {
            this.mShowCreateBill = mShowCreateBill;
            return this;
        }

        public Builder setExtraData(String extraData) {
            this.extraData = extraData;
            return this;
        }

        public Builder setShowFollow(boolean mShowFollow) {
            this.mShowFollow = mShowFollow;
            return this;
        }

        public Builder setShowFontSetting(boolean mShowFontSetting) {
            this.mShowFontSetting = mShowFontSetting;
            return this;
        }

        public Builder setVideoModel(VideoModel videoModel) {
            this.mVideoModel = videoModel;
            return this;
        }

        public Builder setContentIsComment(boolean isComment) {
            this.mContentIsComment = isComment;
            return this;
        }

        public ShareDataModel build() {
            ShareDataModel shareModel = new ShareDataModel();
            if (mVideoModel != null) {

                shareModel.mDescription = mVideoModel.getDescription();
                if (TextUtils.isEmpty(mVideoModel.getCoverUrl())) {
                    if (mVideoModel.getImageUrls() != null && mVideoModel.getImageUrls().size() > 0)
                        shareModel.mImage = mVideoModel.getImageUrls().get(0);
                } else {
                    shareModel.mImage = mVideoModel.getCoverUrl();
                }

                shareModel.mTitle = mVideoModel.getTitle();
                if (TextUtils.isEmpty(shareModel.mTitle)) {
                    String text = "";
                    String name = "";
                    if (mVideoModel.getMediaType() == MediaType.AUDIO_TXT) {
                        text = "语音";
                    } else if (mVideoModel.getMediaType() == MediaType.IMAGE_TXT) {
                        text = "图片";
                    } else if (mVideoModel.isMedia()) {
                        text = "视频";
                    } else {
                        text = "精彩内容";
                    }
                    if (mVideoModel.getAuthor() != null) {
                        name = mVideoModel.getAuthor().getName();
                    }
                    shareModel.mTitle = "来看看" + name + "分享的" + text;
                }
                shareModel.mShareUrl = mVideoModel.getShareUrl();
                shareModel.mMediaId = mContentIsComment ? mVideoModel.getCommentId() : mVideoModel.getId();
                if (mVideoModel.getAuthor() != null) {
                    shareModel.mAuthorId = mVideoModel.getAuthor().getId();
                    shareModel.mAuthorName = mVideoModel.getAuthor().getName();
                    shareModel.mAuthorType = mVideoModel.getAuthor().getType();
                    shareModel.mShowFollow = ! mVideoModel.getAuthor().isSelf();
                } else {
                    shareModel.mShowFollow = false;
                }
                shareModel.mShowShare = mVideoModel.canShare() && mShowShare;
            } else {
                shareModel.mDescription = mDescription;
                shareModel.mImage = mImage;
                shareModel.mTitle = mTitle;
                shareModel.mShareUrl = mUrl;
                shareModel.mMediaId = mMediaId;
                shareModel.mAuthorId = mAuthorId;
                shareModel.mAuthorName = mAuthorName;
                shareModel.mAuthorType = mAuthorType;
                shareModel.mShowShare = mShowShare;
                shareModel.mShowFollow = mShowFollow;
            }

            if (TextUtils.isEmpty(shareModel.getDescription())) {
                shareModel.mDescription = GVideoRuntime.getAppContext().getString(R.string.share_default_description);
            }

            if(PluginManager.get(AccountPlugin.class).hasLoggedIn()){
                String userId = PluginManager.get(AccountPlugin.class).getUserId();
                userId = shareModel.mShareUrl.contains("?") ? "&userid="+userId : "?userid=" +userId;
                shareModel.mShareUrl = shareModel.mShareUrl + userId;
            }

            shareModel.mReportUrl = mReportUrl;
            shareModel.mShowDelete = mShowDelete;
            shareModel.mShowCreateBill = mShowCreateBill;
            shareModel.mIsFavorite = mIsFavorite;
            shareModel.mIsFollow = mIsFollow;
            shareModel.mShowFontSetting = mShowFontSetting;
            shareModel.extraData = extraData;
            shareModel.mVideoModel = mVideoModel;
            shareModel.mShowReport = mShowReport;
            shareModel.mShowFavorite = mShowFavorite;
            shareModel.mContentIsComment = mContentIsComment;

            return shareModel;
        }
    }
}
