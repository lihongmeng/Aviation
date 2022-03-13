package com.jxntv.account.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.jxntv.base.model.video.VideoObservable;
import com.jxntv.utils.DateUtils;

import java.util.Date;

import static com.jxntv.utils.DateUtils.CHINESE_YMD;

/**
 * 可观察的媒体模型
 *
 * @since 2020-02-13 20:20
 */
public final class MediaObservable extends VideoObservable {
    //<editor-fold desc="属性">

    @NonNull
    public ObservableField<String> createdAt = new ObservableField<>();

    @NonNull
    public ObservableBoolean isPrivacyTag = new ObservableBoolean();

    public MediaObservable(String mediaId) {
        super(mediaId);
    }

    //</editor-fold>

    //<editor-fold desc="API">

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt.set(DateUtils.getChineseYMD(createdAt));
    }

    public void setPrivacyTag(boolean privacyTag) {
        isPrivacyTag.set(privacyTag);
    }

    //</editor-fold>
}
