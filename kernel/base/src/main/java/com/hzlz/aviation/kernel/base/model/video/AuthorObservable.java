package com.hzlz.aviation.kernel.base.model.video;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.kernel.base.utils.StringUtils;

/**
 * 作者模型Observable
 */
public class AuthorObservable {
    @NonNull
    private final ObservableBoolean isFollow;
    @NonNull
    private final ObservableField<String> name = new ObservableField<>();
    @NonNull
    private final ObservableField<String> avatar = new ObservableField<>();
    @NonNull
    private final ObservableField<String> intro = new ObservableField<>();
    @NonNull
    private final ObservableField<String> cover = new ObservableField<>();
    @NonNull
    private final ObservableBoolean followMe = new ObservableBoolean();
    @NonNull
    private final ObservableBoolean self = new ObservableBoolean();
    @NonNull
    private final ObservableBoolean isAuthentication = new ObservableBoolean();
    @NonNull
    private final ObservableField<String> authenticationIntro = new ObservableField<>();

    public AuthorObservable(String authorId) {
        isFollow = InteractDataObservable.getInstance().getFollowObservable(authorId);
    }

    public ObservableBoolean getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow.set(isFollow);
    }

    public ObservableField<String> getName() {
        return name;
    }

    public void setName(String name) {
        name = StringUtils.filterWhiteSpace(name);
        this.name.set(name);
    }

    public ObservableField<String> getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar.set(avatar);
    }

    public ObservableField<String> getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        intro = StringUtils.filterWhiteSpace(intro);
        this.intro.set(intro);
    }

    public ObservableField<String> getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover.set(cover);
    }

    public ObservableBoolean getFollowMe() {
        return followMe;
    }

    public void setFollowMe(boolean followMe) {
        this.followMe.set(followMe);
    }

    public ObservableBoolean getSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self.set(self);
    }

    public void setIsAuthentication(boolean isAuthentication) {
        this.isAuthentication.set(isAuthentication);
    }

    @NonNull
    public ObservableBoolean getIsAuthentication() {
        return isAuthentication;
    }

    public void setAuthenticationIntro(String authenticationIntro) {
        String result = "";
        String introString = getIntro().get();
        if (!isAuthentication.get()) {
            result = introString;
        }
        if (!TextUtils.isEmpty(authenticationIntro)) {
            result = authenticationIntro;
        } else if (!TextUtils.isEmpty(introString)) {
            result = introString;
        }
        result = StringUtils.showMaxLengthString(result, 13);
        this.authenticationIntro.set(result);
    }

    @NonNull
    public ObservableField<String> getAuthenticationIntro() {
        return authenticationIntro;
    }

}
