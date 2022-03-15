package com.hzlz.aviation.feature.search.model;

import android.text.TextUtils;

import androidx.databinding.ObservableBoolean;

import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.search.R;

import java.util.List;

/**
 * 搜索结果数据模型
 */
public class SearchDetailModel extends MediaModel implements ISearchModel {

    public @SearchType int category;
    /**
     * 用户头像
     */
    public String authorAvatar;
    /**
     * 用户昵称
     */
    public String authorName;
    /**
     * 用户类型
     */
    public @AuthorType int authorType;
    /**
     * 用户是否认证
     */
    public boolean isAuthentication;
    /**
     * 用户认证简介
     */
    public String authenticationIntro;
    /**
     * 社区简介
     */
    public String intro;
    /**
     * 是否关注
     */
    public boolean isFollow;

    /**
     * 模板位置
     */
    public int position;

    /**
     * 所属MCN
     */
    public Long tenantId;

    /**
     * 所属MCN名称
     */
    public String tenantName;

    public List<ProgramModel> recentPrograms;

    public SearchDetailModel(VideoModel media) {
        super(media);
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String getSearchWord() {
        return searchWord;
    }

    @Override
    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    @Override
    public void setHintSearchWord(String hintSearchWord) {
        this.hintSearchWord = hintSearchWord;
    }

    @Override
    public int getSearchType() {
        return category;
    }

    @Override
    public void setShowTag(boolean isShowTag) {
        this.isShowTag = isShowTag;
    }

    @Override
    public boolean getShowTag() {
        return isShowTag;
    }

    public boolean isValid() {
        return true;
    }

    public String getAuthorIntro() {
        return TextUtils.isEmpty(authenticationIntro) ? intro : authenticationIntro;
    }

    public void updateInteract() {
        if (category == SearchType.CATEGORY_COMMUNITY) {
            InteractDataObservable.getInstance().setJoinCircle(getId(), isFollow);
        }
    }

    public ObservableBoolean getJoinCircleObservable() {
        return InteractDataObservable.getInstance().getJoinCircleObservable(getId());
    }

    public String getSearchTypeString() {
        String result = "";
        switch (category) {
            case SearchType.CATEGORY_AUTHORS:
                result = ResourcesUtils.getString(R.string.user);
                break;
            case SearchType.CATEGORY_COMMUNITY:
                result = ResourcesUtils.getString(R.string.community);
                break;
            case SearchType.CATEGORY_ALL:
                result = ResourcesUtils.getString(R.string.all);
                break;
            case SearchType.CATEGORY_PROGRAM:
                result = ResourcesUtils.getString(R.string.program);
                break;
            case SearchType.CATEGORY_NEWS:
                result = ResourcesUtils.getString(R.string.news);
                break;
            case SearchType.CATEGORY_MOMENT:
                result = ResourcesUtils.getString(R.string.tab_moment);
                break;
        }
        return result;
    }

    public class ProgramModel{
        public String id;
        public String columnId;
        public String programName;
        public String playDate;
    }
}
