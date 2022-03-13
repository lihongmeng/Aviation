package com.jxntv.base.model.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;

import com.google.gson.annotations.SerializedName;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.circle.CircleFamous;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.CompatUtils;
import com.jxntv.ioc.PluginManager;

import java.util.List;

/**
 * 作者模型
 */
public class AuthorModel implements Parcelable {

    private transient AuthorObservable mAuthorObservable;

    /**
     * 作者id
     */
    private String id;

    /**
     * 作者名
     */
    private String name;

    /**
     * 作者头像
     */
    private String avatar;

    /**
     * 作者简介
     */
    private String intro;

    /**
     * 是否已关注
     */
    private boolean isFollow;

    /**
     * 是否关注我
     */
    private boolean isFollowMe;

    /**
     * 是否认证用户
     */
    private boolean isAuthentication;

    /**
     * 认证相关信息
     */
    private String authenticationIntro;

    /**
     * PGC页面背景图
     */
    @Deprecated
    @SerializedName("background")
    private String mCoverUrl;

    /**
     * 用户类型
     */
    @AuthorType
    private int type = AuthorType.PGC;

    /**
     * 情感问答老师标签
     */
    private String areasOfExpertise;

    /**
     * 分享链接
     */
    private String shareUrl;

    // 发布者账号所属频道频率ID
    public List<String> tenantIdList;

    // 发布者账号所属频道频率
    public List<String> tenantNameList;

    public AuthorModel() {
    }

    public AuthorModel(String userId) {
        this.id = userId;
        this.type = AuthorType.UGC;
    }

    public AuthorModel(CircleFamous circleFamous) {
        if (circleFamous == null) {
            return;
        }
        this.id = circleFamous.jid + "";
        this.type = AuthorType.UGC;
    }

    protected AuthorModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        avatar = in.readString();
        intro = in.readString();
        isFollow = in.readByte() != 0;
        isFollowMe = in.readByte() != 0;
        isAuthentication = in.readByte() != 0;
        authenticationIntro = in.readString();
        mCoverUrl = in.readString();
        type = in.readInt();
        shareUrl = in.readString();
        tenantIdList = in.createStringArrayList();
        tenantNameList = in.createStringArrayList();
        areasOfExpertise = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(intro);
        dest.writeByte((byte) (isFollow ? 1 : 0));
        dest.writeByte((byte) (isFollowMe ? 1 : 0));
        dest.writeByte((byte) (isAuthentication ? 1 : 0));
        dest.writeString(authenticationIntro);
        dest.writeString(mCoverUrl);
        dest.writeInt(type);
        dest.writeString(shareUrl);
        dest.writeStringList(tenantIdList);
        dest.writeStringList(tenantNameList);
        dest.writeString(areasOfExpertise);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthorModel> CREATOR = new Creator<AuthorModel>() {
        @Override
        public AuthorModel createFromParcel(Parcel in) {
            return new AuthorModel(in);
        }

        @Override
        public AuthorModel[] newArray(int size) {
            return new AuthorModel[size];
        }
    };

    protected AuthorObservable createObservable(String authorId) {
        return new AuthorObservable(authorId);
    }

    public AuthorObservable getObservable() {
        if (mAuthorObservable == null) {
            mAuthorObservable = createObservable(id);
            mAuthorObservable.getIsFollow().addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            if (sender == mAuthorObservable.getIsFollow()) {
                                setFollow(mAuthorObservable.getIsFollow().get());
                            }
                        }
                    });
            updateFrom(this);
        }
        return mAuthorObservable;
    }

    public void updateInteract() {
        if (TextUtils.isEmpty(getAreasOfExpertise())) {
            if (InteractDataObservable.getInstance().containsFollow(id)) {
                InteractDataObservable.getInstance().removeFollow(id);
            }
        }
        getObservable();
    }

    public void updateFrom(AuthorModel author) {
        setId(author.getId());
        setName(author.getName());
        setAvatar(author.getAvatar());
        setCoverUrl(author.getCoverUrl());
        setIntro(author.getIntro());
        setFollow(author.isFollow());
        setFollowMe(author.isFollowMe());
        setAuthentication(author.isAuthentication());
        setAuthenticationIntro(author.getAuthenticationIntro());
        setShareUrl(author.getShareUrl());
        setAreasOfExpertise(author.areasOfExpertise);
        this.tenantIdList = author.tenantIdList;
        this.tenantNameList = author.tenantNameList;
    }

    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
        if (mAuthorObservable != null) {
            mAuthorObservable.setSelf(isSelf());
        }
    }

    public boolean isSelf() {
        String uid = PluginManager.get(AccountPlugin.class).getUserId();
        boolean self = TextUtils.equals(id, uid);
        return self;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (mAuthorObservable != null) {
            mAuthorObservable.setName(name);
        }
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        if (mAuthorObservable != null) {
            mAuthorObservable.setAvatar(avatar);
        }
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
        if (mAuthorObservable != null) {
            mAuthorObservable.setIntro(intro);
        }
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
        if (mAuthorObservable != null) {
            mAuthorObservable.setIsFollow(follow);
        }
    }

    public boolean isFollowMe() {
        return isFollowMe;
    }

    public void setFollowMe(boolean followMe) {
        isFollowMe = followMe;
        if (mAuthorObservable != null) {
            mAuthorObservable.setFollowMe(followMe);
        }
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setCoverUrl(String mCoverUrl) {
        this.mCoverUrl = mCoverUrl;
        if (mAuthorObservable != null) {
            mAuthorObservable.setCover(mCoverUrl);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAuthentication() {
        return isAuthentication;
    }

    public void setAuthentication(boolean authentication) {
        isAuthentication = authentication;
        if (mAuthorObservable == null) {
            return;
        }
        mAuthorObservable.setIsAuthentication(authentication);
    }

    public String getAuthenticationIntro() {
        if (TextUtils.isEmpty(authenticationIntro)) {
            return "";
        }
        return authenticationIntro;
    }

    public void setAuthenticationIntro(String authenticationIntro) {
        if (TextUtils.isEmpty(authenticationIntro)) {
            authenticationIntro = "";
        }
        this.authenticationIntro = authenticationIntro;
        if (mAuthorObservable == null) {
            return;
        }
        mAuthorObservable.setAuthenticationIntro(authenticationIntro);
    }

    public String getAreasOfExpertise() {
        return areasOfExpertise;
    }

    public void setAreasOfExpertise(String areasOfExpertise) {
        this.areasOfExpertise = areasOfExpertise;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(name) && !TextUtils.isEmpty(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthorModel)) return false;
        AuthorModel that = (AuthorModel) o;
        return isFollow == that.isFollow &&
                isFollowMe == that.isFollowMe &&
                type == that.type &&
                CompatUtils.equals(id, that.id) &&
                CompatUtils.equals(name, that.name) &&
                CompatUtils.equals(avatar, that.avatar) &&
                CompatUtils.equals(intro, that.intro) &&
                CompatUtils.equals(shareUrl, that.shareUrl) &&
                CompatUtils.equals(mCoverUrl, that.mCoverUrl) &&
                CompatUtils.equals(authenticationIntro, that.authenticationIntro) &&
                CompatUtils.equals(isAuthentication, that.isAuthentication) &&
                CompatUtils.equals(areasOfExpertise, that.areasOfExpertise);
    }

    @Override
    public int hashCode() {
        return CompatUtils.hash(id, name, avatar, intro, isFollow, isFollowMe, mCoverUrl, type, shareUrl,
                        authenticationIntro, isAuthentication, areasOfExpertise);
    }


    public static final class Builder {

        private String id;
        private String name;
        private String avatar;
        private String intro;
        private boolean isFollow;
        private boolean isFollowMe;
        private String mCoverUrl;
        private int type;
        private boolean isAuthentication;
        private String authenticationIntro;
        private String shareUrl;
        public List<String> tenantIdList;
        public List<String> tenantNameList;
        private String areasOfExpertise;

        private Builder() {
        }

        public static Builder anAuthorModel() {
            return new Builder();
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder withIntro(String intro) {
            this.intro = intro;
            return this;
        }

        public Builder withIsFollow(boolean isFollow) {
            this.isFollow = isFollow;
            return this;
        }

        public Builder withIsFollowMe(boolean isFollowMe) {
            this.isFollowMe = isFollowMe;
            return this;
        }

        public Builder withCoverUrl(String mCoverUrl) {
            this.mCoverUrl = mCoverUrl;
            return this;
        }

        public Builder withType(int type) {
            this.type = type;
            return this;
        }

        public Builder whitIsAuthentication(boolean isAuthentication) {
            this.isAuthentication = isAuthentication;
            return this;
        }

        public Builder withAuthenticationIntro(String authenticationIntro) {
            this.authenticationIntro = authenticationIntro;
            return this;
        }

        public Builder withShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
            return this;
        }

        public Builder withTenantIdList(List<String> tenantIdList) {
            this.tenantIdList = tenantIdList;
            return this;
        }

        public Builder withTenantNameList(List<String> tenantNameList) {
            this.tenantNameList = tenantNameList;
            return this;
        }

        public Builder fromAuthor(@NonNull AuthorModel author) {
            this.id = author.id;
            this.name = author.name;
            this.avatar = author.avatar;
            this.intro = author.intro;
            this.isFollow = author.isFollow;
            this.isFollowMe = author.isFollowMe;
            this.mCoverUrl = author.mCoverUrl;
            this.type = author.type;
            this.isAuthentication = author.isAuthentication;
            this.authenticationIntro = author.authenticationIntro;
            this.shareUrl = author.shareUrl;
            this.tenantIdList = author.tenantIdList;
            this.tenantNameList = author.tenantNameList;
            this.areasOfExpertise = author.areasOfExpertise;
            return this;
        }

        public AuthorModel build() {
            AuthorModel authorModel = new AuthorModel();
            authorModel.setId(id);
            authorModel.setName(name);
            authorModel.setAvatar(avatar);
            authorModel.setIntro(intro);
            authorModel.setFollow(isFollow);
            authorModel.setFollowMe(isFollowMe);
            authorModel.setCoverUrl(mCoverUrl);
            authorModel.setType(type);
            authorModel.setAuthentication(isAuthentication);
            authorModel.setAuthenticationIntro(authenticationIntro);
            authorModel.setShareUrl(shareUrl);
            authorModel.tenantIdList = tenantIdList;
            authorModel.tenantNameList = tenantNameList;
            authorModel.setAreasOfExpertise(areasOfExpertise);
            return authorModel;
        }
    }

}
