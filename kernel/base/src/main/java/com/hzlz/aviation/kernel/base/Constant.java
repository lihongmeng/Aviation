package com.hzlz.aviation.kernel.base;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Constant {

    interface COUNTRY_CODE {
        String CHINA = "+86";
    }

    interface AES_SIGN {
        // 一键登录
        String ONE_KEY_LOGIN = "eteBiJ6uXd@mq3hJ";
    }

    interface AES_SIGN_IV {
        // 一键登录
        String ONE_KEY_LOGIN = "Iti87RTSwb02jAyG";
    }

    /**
     * 首页顶部主题样式类型
     */
    interface THEME_COLOR_SWITCH_TOP {

        // 默认
        int NORMAL = 0;

        // 两会
        int MEET = 1;

        // 春节
        int SPRING_FESTIVAL = 2;

    }

    // 启动发布页面的意图,在发布页面关闭的时候，区分场景处理
    interface START_PUBLISH_FROM {

        String HOME = "home";

        // 在社区详情页、话题详情页等二级及以上页面，发布完成后，保留在当前页面
        String CIRCLE_DETAIL = "circle_detail";
        String TOPIC_DETAIL = "circle_detail";
    }

    // 信息流自动刷新的时间间隔
    interface CONFIG {
        int AUTO_REFRESH_TIME = 1000 * 60 * 60;
    }

    interface PAGE_DEFAULT {

        // 信息流分页获取数据，首页页码
        int FIRST_PAGE = 1;

        // 默认一页拉取数据量
        int PAGE_SIZE = 10;
    }

    interface REGEX_PATTERN {

        // 匹配URL
        String URL = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

        // 匹配http、https协议的域名
        String DOMAIN = "(http|https)://(www.)?(\\w+(\\.)?)+";

    }

    interface EVENT_BUS_EVENT {
        // 播放视频结束事件
        String VIDEO_PLAY_END = "video_play_end";
        // 需要刷新首页关注整个页面
        String NEED_UPDATE_HOME_FOLLOW_WHOLE_PAGE = "need_update_home_follow_whole_page";

        // 将视频静音
        String MUTE_VIDEO = "mute_video";

        // 将视频回复声音
        String RESUME_VIDEO_VOLUME = "resume_video_volume";

        // 启动登录页面
        // 可以根据业务需求发起广播
        // 但是接受者必须放在HomeActivity中
        // 否则页面在onPause没有注销广播的话
        // 登录页面会启动多次
        String START_LOGIN = "start_login";

        // 登录成功
        String LOGIN = "login";

        // 登出成功
        String LOGOUT = "logout";

        // 唤起评论弹窗
        String SHOW_COMMENT_INPUT_PANEL = "show_comment_input_panel";

        // 隐藏评论弹窗
        String DISMISS_COMMENT_INPUT_PANEL = "dismiss_comment_input_panel";

        // 隐藏评论loading
        String HIDE_LOADING_INPUT_PANEL = "hide_loading_input_panel";

        // 竖向的视频详情页开启滑动
        String SUPPORT_SLIDE = "support_slide";

        // 将VideoModel数据广播出去，用于播放
        String SEND_VIDEO_DATA_TO_PLAY = "send_video_data_to_play";

        // 将GroupInfo信息广播出来
        String SEND_GROUP_INFO = "send_group_info";

        // 标识圈子与账号之间的关系发生变化
        String CIRCLE_JOIN_EXIT_CHANGE = "circle_join_exit_change";

        // 启动HomeActivity的扫码界面
        String START_SCAN_HOME = "start_scan_home";

        // 启动SearchActivity的扫码界面
        String START_SCAN_SEARCH = "start_scan_search";

        // 启动社区详情页
        String START_CIRCLE_DETAIL = "start_circle_detail";

        // wifi信号强度变化
        String WIFI_SIGNAL_STRENGTH_CHANGE = "wifi_signal_strength_change";

        // wifi断开
        String WIFI_DISCONNECTED = "wifi_disconnected";

        // 连接到网络
        String WIFI_CONNECTED = "wifi_connected";

        // 系统关闭wifi
        String SYSTEM_CLOSE_WIFI = "system_close_wifi";

        // 系统开启wifi
        String SYSTEM_START_WIFI = "system_start_wifi";

        // 选择投屏设备
        String SELECT_SCREEN_PROJECTION = "select_screen_projection";

        // 投屏失败
        String SCREEN_PROJECTION_FAILED = "screen_projection_failed";

        // 配置接口通知各个界面，更新主题背景
        String CHANGE_THEME_COLOR = "change_theme_color";

        // 发布成功后会广播，跳转到首页Home Tab，并展示关注页
        String SHOW_HOME_TAB_FOLLOW = "show_home_tab_follow";

        // 发布的Floating弹窗消失后会广播，根据携带的类型，更新指定页面
        String REFRESH_PAGE_BECAUSE_PUBLISH = "refresh_page_because_publish";

        // 已启动某一类型的登录界面，需要关闭其他登录界面
        // 因为一键登录和验证码登录，使用不同的Activity
        String ALREADY_START_LOGIN = "already_start_login";

        // 特定情况下，登录成功后，需要继续执行跳转操作
        // 所以在登录整体逻辑中需要传递数据，记录需要的跳转操作
        String HOME_TO_ME = "after_login_jump_type_key";

    }

    interface BUNDLE_KEY {
        String CHANNEL_ID = "channel_id";
        String START_ACTIVITY_DESTINATION_ID = "start_activity_destination_id";
        String DATE_YEAR = "date_year";
        String DATE_MONTH = "date_month";
        String DATE_DAY = "date_day";
        String COMMENT_ID = "comment_id";
        String COMMENT_TYPE = "comment_type";
        String ID = "id";
        String COLUMN_ID = "columnId";
        String PROGRAM_ID = "programId";
        String PLAY_TYPE = "play_type";
        String NEED_SEND_VIDEO_DATA_ONCE = "need_send_video_data_once";
        String INDEX = "index";
        String OUT_SHARE_TITLE = "outShareTitle";
        String OUT_SHARE_URL = "outShareUrl";
        String VIDEO_MODEL = "videoModel";
        String COMMUNITY_NAME = "communityName";
        String TENANT_ID = "tenant_id";
        String TENANT_NAME = "tenant_name";
        String SOURCE_FRAGMENT_PID = "source_fragment_pid";
        String START_VIDEO_TAB_ID = "start_video_tab_id";
        String SHOW_HOME_TAB_TAB_ID = "show_home_tab_tab_id";
        String CIRCLE = "circle";
        String TOPIC_DETAIL = "topic_detail";
        String GATHER_ID = "gather_id";
        String IS_QA = "is_qa";
        String AUTHOR_MODEL = "author_model";
        String NEED_HIDE_COMMUNITY_LAYOUT = "need_hide_community_layout";
        String IS_NEED_BROAD_HOME_TO_ME = "IS_NEED_BROAD_HOME_TO_ME";
        String SOURCE_PID = "source_pid";
    }

    interface SP_KEY {
        String HISTORY_COMMUNITY_ID = "history_community_id";
        String LAST_SHOW_TIME = "last_show_time";
        String ALREADY_SHOW_COUNT = "already_show_count";
        String MAX_SHOW_COUNT = "max_show_count";
        String BANNER_ID = "banner_id";
    }

    // 评论弹窗的字数限制
    int COMMENT_DIALOG_TEXT_LIMIT_COUNT = 2000;

    String SP_LOGIN_HAS_VERIFICATE = "sp_login_has_verificate";
    String EXTRA_AUTHOR_ID = "authorId";
    String EXTRA_AUTHOR_TYPE = "authorType";
    String EXTRA_SEARCH_TYPE = "searchType";
    String EXTRA_MODIFY_TYPE = "modifyType";
    String EXTRA_MODIFY_INFO = "modifyInfo";
    String EXTRA_GROUP_ID = "group_id";
    String EXTRA_CIRCLE_FRAGMENT_INDEX = "circle_fragment_index";
    String EXTRA_GATHER_ID = "gather_id";
    String EXTRA_PID = "pid";
    String EXTRA_IS_START_WITH_ACTIVITY = "isStartWithActivity";
    String EXTRA_LIVE_TYPE = "live_type";
    String EXTRA_IS_FOLLOW_NEED_TOP_SPACE = "extra_is_follow_need_top_space";
    String EXTRA_FEED_FRAGMENT_TAB_ORIENTATION = "FEED_FRAGMENT_TAB_ORIENTATION";
    String EXTRA_TAB_ID = "tab_id";
    String EXTRA_FROM_PID = "from_pid";
    String EXTRA_FROM_CHANNEL_ID = "from_channel_id";
    String EXTRA_COLUMN_ID = "column_id";
    String EXTRA_VIDEO_SHORT_MODELS = "video_short_models";
    String EXTRA_VIDEO_MODEL = "video_model";
    String VISITOR = "visitor";
    String AUTHOR_TYPE = "authorType";
    String CONNECT_VIDEO = "connectVideo";
    String MEDIA_ID = "mediaId";
    String IS_JUST_SELECT = "isJustSelect";
    String AVATAR_URL = "avatar_url";
    String SELECT_AVATAR = "select_avatar";
    String CROP = "crop";
    String EXTRA_FRAGMENT_ID = "fragmentId";
    String EXTRA_NAVIGATION = "navigation";
    String EXTRA_TYPE = "type";

    interface EVENT_MSG {
        String COMMENT_DELETE = "comment_delete";
        //增加评论
        String COMMENT_ADD = "comment_add";
        //增加回答
        String COMMENT_ADD_QA = "comment_add_qa";

        //发布动态
        String COMPOSITION_ADD = "composition_add";
        //发布提问
        String COMPOSITION_ADD_QUESTION = "composition_add_qa";

        String PAGE_ENTER = "page_enter";
        String PAGE_EXIT = "page_exit";
        String PAGE_EXIT_DATA = "page_exit_width_data";

        String BACK_TOP = "back_top";

        //视频播放需要暂停
        String VIDEO_NEED_PAUSE = "need_pause_video";
        //后台音频直播需要暂停
        String AUDIO_BG_NEED_PAUSE = "need_pause_audio";
        //通知关闭首页音频控件
        String COLLAPSE_AUDIO_FLOAT_VIEW = "collapse_audio_float_view";
        String HAS_BACK_HOME = "has_back_home";
        //通过tab返回首页
        String BACK_HOME_BY_TAB = "back_home_by_tab";
    }

    interface CircleTopicStatus {
        /**
         * 启用
         */
        int ENABLE = 1;
        /**
         * 禁用
         */
        int DISABLE = 2;
    }

    interface CircleSortType {
        /**
         * 默认最新排序
         */
        int DEFAULT_NEWEST_SORT = 0;
        /**
         * 默认推荐排序
         */
        int DEFAULT_RECOMMEND_SORT = 1;
    }

    interface FindCircleContentType {
        /**
         * 直播
         */
        int LIVE = 1;
        /**
         * 话题
         */
        int TOPIC = 2;
        /**
         * 动态
         */
        int MOMENT = 3;
    }

    interface CIRCLE_FAMOUS_TYPE {
        /**
         * 普通用户
         */
        int NORMAL = 1;
        /**
         * 管理员
         */
        int ADMIN = 2;
        /**
         * 圈主
         */
        int CIRCLE_OWNER = 3;
        /**
         * 超级管理员
         */
        int SUPER_MANAGER = 4;
    }

    interface MODIFY_IMAGE_CODE {
        /**
         * 相册
         */
        int REQUEST_CODE_FROM_GALLERY = 1;
        /**
         * 相机
         */
        int REQUEST_CODE_FROM_CAMERA = 2;
    }

    interface UPLOAD_VIDEO_TYPE {
        // 横视频
        int HORIZONTAL = 1;
        // 竖视频
        int VERTICAL = 2;
    }

    // 选择图片的方式
    interface REQUEST_CODE_FROM {
        // 相册
        int GALLERY = 1;
        // 相机
        int CAMERA = 2;
    }

    // 开启的直播类型
    interface LIVE_ROOM_TYPE {
        // 视频直播
        int VIDEO = 1;
        // 语音
        int SOUND = 2;
    }

    // 连麦开启还是关闭
    interface MICRO_CONNECT {
        // 关闭
        int OFF = 0;
        // 开启
        int ON = 1;
    }


    // 信息流的ChannelId
    interface CHANNEL_ID {
        String VIDEO = "-3";
    }

    interface TAB_MEDIA_TYPE {
        int MEDIA_TYPE_HOME = 0;
        int MEDIA_TYPE_VIDEO = 1;
        int MEDIA_TYPE_RADIO = 2;
        int MEDIA_TYPE_LIVE = 3;
    }

    @IntDef({
            TAB_MEDIA_TYPE.MEDIA_TYPE_HOME,
            TAB_MEDIA_TYPE.MEDIA_TYPE_VIDEO,
            TAB_MEDIA_TYPE.MEDIA_TYPE_RADIO,
            TAB_MEDIA_TYPE.MEDIA_TYPE_LIVE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabMediaType {
    }

    interface LIVE_TYPE {

        // 未开始
        int NOT_START = 0;

        // 预告
        int HERALD = 1;

        // 直播中
        int LIVING = 2;

        // 回放
        int REVIEW = 3;

        // 下架
        int UNDER_CARRIAGE = 4;
    }

    interface VIDEO_SUPER_FRAGMENT_TYPE {
        int COMMENT_RECOMMEND = 0;
        int SELECT_COMMENT = 1;
    }

    interface WATCH_TV_CHANNEL_TYPE {
        int TV = 0;
        int BUILD_SELF_LIVE = 1;
        int BUILD_SELF_OTHER = 2;
    }

    interface BANNER_LOCATION_ID {
        int RECOMMEND_HOME = -1;
        int NEWS_HOME = -2;
        int WATCH_TV = -3;
    }

    interface BANNER_SCENE {
        int FEED = 0;
        int OPERATION = 1;
    }

    interface LIVE_TO_WATCH_TV_PLAY_TYPE {
        int BUILD_SELF = 3;
    }

    interface MAP_STRING {
        String IS_FOLLOW = "is_follow";
        String IS_FOLLOW_ME = "is_follow_me";
    }

    interface ONE_KEY_LOGIN_UI_STYLE {
        int FULL_PORT = 0;
    }

    interface LOGIN_TYPE {
        int SMS_CODE_LOGIN = 0;
        int ONE_KEY_LOGIN = 1;
    }
}
