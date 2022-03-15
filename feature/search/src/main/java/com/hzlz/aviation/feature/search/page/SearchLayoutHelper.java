package com.hzlz.aviation.feature.search.page;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.IntDef;

import com.hzlz.aviation.feature.search.model.SearchDetailModel;
import com.hzlz.aviation.feature.search.model.SearchType;
import com.hzlz.aviation.feature.search.template.detail.SearchAuthorTemplate;
import com.hzlz.aviation.feature.search.template.detail.SearchCommunityTemplate;
import com.hzlz.aviation.feature.search.template.detail.SearchMomentTemplate;
import com.hzlz.aviation.feature.search.template.detail.SearchOneImgSoundTemplate;
import com.hzlz.aviation.feature.search.template.detail.SearchOneImgTemplate;
import com.hzlz.aviation.feature.search.template.detail.SearchProgramTemplate;
import com.hzlz.aviation.feature.search.template.detail.SearchShortProductionTemplate;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.NewsImageTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.NewsRightImageTemplate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 搜索layout 辅助类
 */
public class SearchLayoutHelper {

    /** 搜索布局类型：错误 */
    public static final int SEARCH_LAYOUT_TYPE_ERROR = -1;
    /** 搜索布局类型：单图 */
    public static final int SEARCH_LAYOUT_TYPE_ONE_IMG = 0;
    /**
     * 搜索布局类型：sound单图
     */
    public static final int SEARCH_LAYOUT_TYPE_ONE_IMG_SOUND = 1;
    /**
     * 搜索布局类型：短作品
     */
    public static final int SEARCH_LAYOUT_TYPE_PRODUCTION = 2;
    /**
     * 搜索布局类型：用户
     */
    public static final int SEARCH_LAYOUT_TYPE_AUTHOR = 3;
    /**
     * 搜索布局类型：社区
     */
    public static final int SEARCH_LAYOUT_TYPE_COMMUNITY = 4;
    /**
     * 搜索布局类型：节目
     */
    public static final int SEARCH_LAYOUT_TYPE_PROGRAM = 5;
    /**
     * 搜索布局类型： 新闻
     */
    public static final int SEARCH_LAYOUT_TYPE_NEWS_RIGHT_IMAGE = 6;
    /**
     * 搜索布局类型： 新闻
     */
    public static final int SEARCH_LAYOUT_TYPE_NEWS_IMAGE = 7;
    /**
     * 搜索布局类型： 动态
     */
    public static final int SEARCH_LAYOUT_TYPE_MOMENT = 8;

    /**
     * search数据模型统一基类
     *
     * @param context    上下文环境
     * @param layoutType 搜索type
     * @param parent     父布局
     */
    public static MediaBaseTemplate createInstance(Context context, @SearchLayoutType int layoutType,
                                                   ViewGroup parent) {
        switch (layoutType) {
            case SEARCH_LAYOUT_TYPE_ONE_IMG:
                return new SearchOneImgTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_ONE_IMG_SOUND:
                return new SearchOneImgSoundTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_PRODUCTION:
                return new SearchShortProductionTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_AUTHOR:
                return new SearchAuthorTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_COMMUNITY:
                return new SearchCommunityTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_PROGRAM:
                return new SearchProgramTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_NEWS_IMAGE:
                return new NewsImageTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_NEWS_RIGHT_IMAGE:
                return new NewsRightImageTemplate(context, parent);
            case SEARCH_LAYOUT_TYPE_MOMENT:
                return new SearchMomentTemplate(context, parent);
            default:
                throw new RuntimeException("searchLayout = " + layoutType + "search layout invalid");
        }
    }

    /**
     * 获取page页面类型
     *
     * @param tabType       tab类型
     * @param model         数据模型
     */
    public static @SearchLayoutType
    int getPageViewType(@SearchType int tabType, SearchDetailModel model) {

        switch (tabType) {
            case SearchType.CATEGORY_AUTHORS:
                return SEARCH_LAYOUT_TYPE_AUTHOR;
            case SearchType.CATEGORY_COMMUNITY:
                return SEARCH_LAYOUT_TYPE_COMMUNITY;
            case SearchType.CATEGORY_PROGRAM:
                return SEARCH_LAYOUT_TYPE_PROGRAM;
            case SearchType.CATEGORY_NEWS:
                switch (model.getMediaType()) {
                    case MediaType.NEWS_IMAGE:
                        return SEARCH_LAYOUT_TYPE_NEWS_IMAGE;
                    case MediaType.NEWS_RIGHT_IMAGE:
                        return SEARCH_LAYOUT_TYPE_NEWS_RIGHT_IMAGE;
                }
            case SearchType.CATEGORY_MOMENT:
                return SEARCH_LAYOUT_TYPE_MOMENT;
            default:
                return SEARCH_LAYOUT_TYPE_ERROR;
        }
    }

    @IntDef({SEARCH_LAYOUT_TYPE_ERROR, SEARCH_LAYOUT_TYPE_ONE_IMG, SEARCH_LAYOUT_TYPE_ONE_IMG_SOUND,
            SEARCH_LAYOUT_TYPE_PRODUCTION, SEARCH_LAYOUT_TYPE_AUTHOR, SEARCH_LAYOUT_TYPE_COMMUNITY,
            SEARCH_LAYOUT_TYPE_PROGRAM, SEARCH_LAYOUT_TYPE_NEWS_RIGHT_IMAGE, SEARCH_LAYOUT_TYPE_NEWS_IMAGE,
            SEARCH_LAYOUT_TYPE_MOMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SearchLayoutType {
    }
}
