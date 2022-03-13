package com.jxntv.search.model;

import java.util.List;

/**
 * 搜索数据模型
 */
public class SearchModel {
    public String query;
    public String hintQuery;


    public List<SearchDetailModel> mAll;
    /** 搜索结果--社区 */
    public List<SearchDetailModel> mCommunities;
    /** 搜索结果--作者 */
    public List<SearchDetailModel> mAuthors;
    /** 搜索结果--节目 */
    public List<SearchDetailModel> mProgrammes;
    /** 搜索结果--新闻 */
    public List<SearchDetailModel> mNews;
    /** 搜索结果--动态 */
    public List<SearchDetailModel> mMoment;

    public boolean hasMoreAll,hasMoreCommunities,hasMoreAuthors,hasMoreProgrammes,hasMoreNews,hasMoreMoment;

    /**
     * 判断搜索结果是否为空
     *
     * @return boolean
     */
    public boolean isResultAllEmpty() {
        return (mAll == null || mAll.isEmpty())
                && (mCommunities == null || mCommunities.isEmpty())
                && (mAuthors == null || mAuthors.isEmpty())
                && (mProgrammes == null || mProgrammes.isEmpty())
                && (mNews == null || mNews.isEmpty())
                && (mMoment == null || mMoment.isEmpty());
    }

    public int getResultNumber(){
        return mAll.size();
    }

    /**
     * 根据type，获取对应的结果数据
     *
     * @param type  对应的tab type
     * @return 对应的结果数据
     */
    public List<SearchDetailModel> getResultByType(int type) {
        switch (type) {
            case SearchType.CATEGORY_ALL:
                return mAll;
            case SearchType.CATEGORY_AUTHORS:
                return mAuthors;
            case SearchType.CATEGORY_COMMUNITY:
                return mCommunities;
            case SearchType.CATEGORY_PROGRAM:
                return mProgrammes;
            case SearchType.CATEGORY_NEWS:
                return mNews;
            case SearchType.CATEGORY_MOMENT:
                return mMoment;
        }
        return null;

    }
}
