package com.jxntv.account.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;

import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.model.anotation.MediaPrivacyTag;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.model.video.VideoObservable;

import java.util.Date;

/**
 * 媒体模型
 *
 * @since 2020-02-13 20:31
 */
public final class Media extends VideoModel implements IAdapterModel {
    //<editor-fold desc="属性">
    private Author mAuthor;
    @Nullable
    private transient MediaObservable mMediaObservable;
    @Nullable
    private final transient ObservableInt mModelPosition = new ObservableInt();
    //</editor-fold>

    //<editor-fold desc="API">
    @Override
    protected VideoObservable createObservable(String mediaId) {
        mMediaObservable = new MediaObservable(mediaId);
        return mMediaObservable;
    }

    @Override
    public void updateFrom(VideoModel media) {
        super.updateFrom(media);
        setPrivacyTag(media.getIsPublic() == MediaPrivacyTag.PRIVATE);
    }
    //</editor-fold>

    //<editor-fold desc="Setter">

    public void setAuthor(@Nullable Author author) {
        super.setAuthor(author);
        if (author != null) {
            if (mAuthor != null) {
                mAuthor.updateFrom(author);
            } else {
                mAuthor = Author.fromAuthorModel(author);
            }
            author.updateFrom(author);
        }
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        super.setCreateDate(createdAt);
        if (mMediaObservable != null) {
            mMediaObservable.setCreatedAt(createdAt);
        }
    }

    public void setPrivacyTag(boolean privacyTag) {
        if (mMediaObservable != null) {
            mMediaObservable.setPrivacyTag(privacyTag);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getter">

    @Nullable
    public Author getAuthor() {
        if (mAuthor == null) {
            mAuthor = Author.fromAuthorModel(super.getAuthor());
        }
        return mAuthor;
    }

    //<editor-fold desc="方法实现">
    @Override
    public void setModelPosition(int position) {
        mModelPosition.set(position);
    }

    @Override
    @NonNull
    public ObservableInt getModelPosition() {
        return mModelPosition;
    }
    //</editor-fold>
}
