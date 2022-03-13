package com.jxntv.search.model;

import androidx.databinding.ObservableBoolean;
import com.google.gson.annotations.SerializedName;
import com.jxntv.base.model.video.AuthorModel;

/**
 * 搜索作者数据模型类
 */
public class SearchAuthorModel extends AuthorModel implements ISearchModel {
    /** 作者姓名 */
    @SerializedName("title")
    private String name;
    /** 已关注 */
    public ObservableBoolean isFollowed = new ObservableBoolean();

    /** 模型位置 */
    private int position;
    /** 搜素词 */
    private String searchWord;

    public void setFollow(boolean follow) {
        super.setFollow(follow);
        isFollowed.set(follow);
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
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public int getSearchType() {
        return 0;
    }

    @Override
    public void setShowTag(boolean isShowTag) {

    }

    @Override
    public boolean getShowTag() {
        return false;
    }
}
