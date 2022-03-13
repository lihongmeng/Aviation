package com.jxntv.search.model;

/**
 * 搜索数据模型接口
 */
public interface ISearchModel {

    /**
     * 设置数据位置
     *
     * @param position  数据位置
     */
    public void setPosition(int position);

    /**
     * 获取搜索词
     *
     * @return 获取数据搜索词
     */
    public String getSearchWord();

    /**
     * 设置搜索词
     *
     * @param searchWord 待设置的搜索词
     */
    public void setSearchWord(String searchWord);

    /**
     * 设置预制文案搜索词
     *
     * @param hintSearchWord 待设置的预制文案搜索词
     */
    public void setHintSearchWord(String hintSearchWord);

    /**
     * 获取标题
     *
     * @return 获取标题
     */
    public String getTitle();

    @SearchType int getSearchType();

    void setShowTag(boolean isShowTag);

    boolean getShowTag();
}
