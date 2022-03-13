package com.jxntv.stat;

import android.text.TextUtils;

import com.jxntv.utils.ResourcesUtils;

/**
 * Pid定义
 */
public class StatPid {

    //-----------  这部分数据和展示内容有关不可修改   -----------
    // 新闻
    public static final String HOME_NEWS = "-2";
    // 推荐
    public static final String HOME_RECOMMEND = "-1";
    // 关注
    public static final String HOME_FOLLOW = "-4";
    // 视听
    public static final String VIDEO_AUDIO = "-3";
    // 直播
    public static final String HOME_LIVE = "-5";
    // 看电视
    public static final String WATCH_TV = "-6";
    //------------------------------------------------------------


    public static final String HOME = "home";
    public static final String LIVE = "live";
    public static final String HOME_FOLLOW_LIST = "home_follow_list";
    public static final String HOME_FOLLOW_CONTENT_LIST = "home_follow_content_list";

    public static final String VIDEO = "video";

    public static final String PID_CIRCLE = "circle";
    public static final String CIRCLE_FIND = "circle_find";
    public static final String CIRCLE_MINE = "circle_mine";
    public static final String CIRCLE_DETAIL = "circle_detail";
    public static final String PID_TOPIC_DETAIL = "topic_detail";

    public static final String MINE = "mine";
    public static final String MINE_COMMENT = "mine_comment";

    public static final String DETAIL = "detail";
    public static final String DETAIL_NEWS = "detail_news";
    public static final String DETAIL_COMPOSITION = "detail_composition";
    public static final String DETAIL_QA = "detail_qa";
    public static final String GROUP_QA = "qa_group";
    public static final String PUBLISH_COMPOSITION = "publish_composition";
    public static final String PUBLISH_QA = "publish_qa";
    public static final String QUESTION_ANSWER_LIST = "question_answer_list";
    public static final String RECOMMEND_COMMUNITY_SELECT = "recommend_community_select";

    public static final String WEB_LINK = "1000";

    public static final String PGC = "pgc";
    public static final String PGC_CONTENT = "pgc_content";
    public static final String MOMENT = "moment";
    public static final String SEARCH_TOPIC = "search_topic";
    public static final String SEARCH = "search";
    public static final String FAVORITE = "favorite";
    public static final String SPLASH = "splash";
    public static final String WEB = "web";

    public static final String FM = "fm";
    public static final String UGC = "ugc";
    public static final String UGC_COMPOSITION = "ugc_composition";
    public static final String UGC_FAVORITE = "ugc_favorite";
    public static final String UGC_COMMENT = "ugc_comment";
    public static final String UGC_QUESTION = "ugc_question";
    public static final String UGC_ANSWER = "ugc_answer";
    public static final String COMPOSITION = "composition";

    public static final String WATCH_TV_HOME = "watch_tv_home";
    public static final String WATCH_TV_HOME_LIST = "watch_tv_home_list";
    public static final String WATCH_TV_CHANNEL_DETAIL = "watch_tv_channel_detail";
    public static final String LOGIN = "login";
    public static final String ONE_KEY_LOGIN = "one_key_login";

    public static final String FANS = "fans";

    public static final String ABOUT_JSP = "about_jsp";
    public static final String ACCOUNT_SECURITY = "account_security";
    public static final String ACTIVITY_LIVE = "activity_live";
    public static final String INTERACT_LIVE_AUTHOR = "interact_live_author";
    public static final String INTERACT_LIVE_AUDIENCE = "interact_live_audience";
    public static final String AUTHENTICATION = "authentication";
    public static final String LIVE_PREPARE = "live_prepare";
    public static final String CANCEL_ACCOUNT = "cancel_account";
    public static final String SELECT_IMAGE_VIDEO = "select_image_video";
    public static final String CIRCLE_FAMOUS = "circle_famous";
    public static final String CIRCLE_SELECT = "circle_select";
    public static final String COUNTRY_CODE = "country_code";
    public static final String CROP_IMAGE = "crop_image";
    public static final String CROP_VIDEO = "crop_video";
    public static final String NICKNAME_SET = "nickname_set";
    public static final String WHOLE_PERIOD_DETAIL = "whole_period_detail";
    public static final String VERIFY_CODE_LOGIN = "verify_code_login";
    public static final String SETTING = "setting";

    public static final String H5_WEB = "h5_web";

    public static String getPageName(String pid) {
        if (TextUtils.isEmpty(pid)) {
            return "";
        }
        if (isChinese(pid)) {

            // 这两步是用来去除空格
            pid = pid.trim();
            pid = pid.replaceAll("\u00A0", "");

            return pid;
        }

        switch (pid) {
            case HOME:
                return "首页";
            case HOME_NEWS:
                return "首页-新闻";
            case HOME_RECOMMEND:
                return "首页-推荐";
            case HOME_FOLLOW_LIST:
            case HOME_FOLLOW_CONTENT_LIST:
                return "首页-关注";

            case LIVE:
                return "直播";
            case HOME_LIVE:
                return "直播-直播";
            case WATCH_TV_HOME:
                return "直播-看电视";

            case VIDEO:
                return "视听";
            case VIDEO_AUDIO:
                return "首页-视频";
            case WEB_LINK:
                return "外链";

            case PID_CIRCLE:
                return "社区";
            case CIRCLE_FIND:
                return "社区-发现社区";
            case CIRCLE_MINE:
                return "社区-我的社区";
            case CIRCLE_DETAIL:
                return "社区详情页";
            case PID_TOPIC_DETAIL:
                return "话题详情页";
            case GROUP_QA:
                return "问答广场";
            case DETAIL_QA:
                return "问答详情";
            case QUESTION_ANSWER_LIST:
                return "选择提问对象页";
            case RECOMMEND_COMMUNITY_SELECT:
                return "推荐社区选择";

            case MINE:
                return "我的";
            case UGC:
                return "Ta人主页";
            case UGC_COMPOSITION:
                return "Ta人主页-动态";
            case MINE_COMMENT:
                return "Ta人主页-评论";
            case UGC_FAVORITE:
                return "Ta人主页-喜欢";
            case UGC_QUESTION:
                return "Ta人主页-提问";
            case UGC_ANSWER:
                return "Ta人主页-回答";

            case DETAIL:
                return "视频详情页";
            case DETAIL_NEWS:
                return "新闻详情";
            case DETAIL_COMPOSITION:
                return "动态详情页";
            case PUBLISH_COMPOSITION:
                return "内容编辑";
            case PUBLISH_QA:
                return "问答发布";
            case LOGIN:
                return "注册/登录";

            case ABOUT_JSP:
                return ResourcesUtils.getString(R.string.about_jsp);
            case ACCOUNT_SECURITY:
                return ResourcesUtils.getString(R.string.account_and_security);
            case ACTIVITY_LIVE:
                return ResourcesUtils.getString(R.string.activity_live);
            case INTERACT_LIVE_AUTHOR:
                return ResourcesUtils.getString(R.string.interact_live_author);
            case INTERACT_LIVE_AUDIENCE:
                return ResourcesUtils.getString(R.string.interact_live_audience);
            case AUTHENTICATION:
                return ResourcesUtils.getString(R.string.authentication);
            case LIVE_PREPARE:
                return ResourcesUtils.getString(R.string.live_prepare);
            case CANCEL_ACCOUNT:
                return ResourcesUtils.getString(R.string.cancel_account);
            case SELECT_IMAGE_VIDEO:
                return ResourcesUtils.getString(R.string.select_image_video);
            case CIRCLE_FAMOUS:
                return ResourcesUtils.getString(R.string.circle_famous);
            case CIRCLE_SELECT:
                return ResourcesUtils.getString(R.string.circle_select);
            case COUNTRY_CODE:
                return ResourcesUtils.getString(R.string.country_code);
            case CROP_IMAGE:
                return ResourcesUtils.getString(R.string.crop_image);
            case CROP_VIDEO:
                return ResourcesUtils.getString(R.string.crop_video);
            case NICKNAME_SET:
                return ResourcesUtils.getString(R.string.nickname_set);
            case WHOLE_PERIOD_DETAIL:
                return ResourcesUtils.getString(R.string.whole_period_detail);
            case VERIFY_CODE_LOGIN:
                return ResourcesUtils.getString(R.string.verify_code_login);
            case ONE_KEY_LOGIN:
                return ResourcesUtils.getString(R.string.one_key_login);
            case SETTING:
                return ResourcesUtils.getString(R.string.setting);
            default:
                return isChinese(pid) ? pid : "";
        }
    }


    /**
     * 判断字符是否有中文
     *
     * @param str
     */
    private static boolean isChinese(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        for (char c : str.toCharArray()) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                return true;
            }
        }
        return false;
    }

}
