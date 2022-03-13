package com.jxntv.account.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.jxntv.account.presistence.UserManager;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.AuthorObservable;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者模型
 *
 * @since 2020-03-03 20:18
 */
public final class Author extends AuthorModel implements IAdapterModel {
    //<editor-fold desc="属性">
    //
    @NonNull
    private transient ObservableInt mModelPosition = new ObservableInt();
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected AuthorObservable createObservable(String authorId) {
        return new AuthorObservable(authorId) {
            @NonNull
            @Override
            public ObservableField<String> getName() {
                if (getSelf().get()) {
                    return UserManager.getCurrentUser().getUserAuthorObservable().getName();
                }
                return super.getName();
            }

            @NonNull
            @Override
            public ObservableField<String> getAvatar() {
                if (getSelf().get()) {
                    return UserManager.getCurrentUser().getUserAuthorObservable().getAvatarUrl();
                }
                return super.getAvatar();
            }

            @NonNull
            @Override
            public ObservableField<String> getIntro() {
                if (getSelf().get()) {
                    return UserManager.getCurrentUser().getUserAuthorObservable().getIntroduction();
                }
                return super.getIntro();
            }

        };
    }

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

    public static Author fromAuthorModel(@NonNull AuthorModel authorModel) {
        Author author = new Author();
        author.setId(authorModel.getId());
        author.setName(authorModel.getName());
        author.setAvatar(authorModel.getAvatar());
        author.setCoverUrl(authorModel.getCoverUrl());
        author.setIntro(authorModel.getIntro());
        author.setFollow(authorModel.isFollow());
        author.setFollowMe(authorModel.isFollowMe());
        author.setType(authorModel.getType());
        author.setAuthentication(authorModel.isAuthentication());
        author.setAuthenticationIntro(authorModel.getAuthenticationIntro());
        return author;
    }

    public static List<Author> filterValidData(List<Author> dataList) {
        if (dataList == null) {
            return null;
        }
        List<Author> result = new ArrayList<>();
        for (Author author : dataList) {
            if (author == null
                    || TextUtils.isEmpty(author.getName())
                    || TextUtils.isEmpty(author.getAvatar())) {
                continue;
            }
            result.add(author);
        }
        return result;
    }

}
