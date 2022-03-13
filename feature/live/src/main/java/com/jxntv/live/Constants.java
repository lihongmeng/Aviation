package com.jxntv.live;

public class Constants {

    public static final String INTENT_LIVE_TYPE = "intent_live_type";
    public static final String INTENT_LIVE_TITLE = "intent_live_title";
    public static final String INTENT_LIVE_ID = "intent_live_id";
    public static final String INTENT_LIVE_SHARE = "intent_live_share_url";
    public static final String INTENT_IMAGE_URL = "intent_image_url";

    public static final String INTENT_ID = "intent_id";
    public static final String EXTRA_VIDEO_MODEL = "extra_video_model";

    /**
     * 图片裁切
     */
    public static final String EVENT_CROP = "event_crop";
    /**
     * 用户登录，在IM账号登录前
     */
    public static final String TX_LOGIN_EVENT_BEFORE = "EVENT_TX_LOGIN_BEFORE";
    /**
     * 用户登录，在IM账号登录后
     */
    public static final String TX_LOGIN_EVENT_AFTER = "EVENT_TX_LOGIN_AFTER";



    /**
     * 3. 小直播后台服务器地址
     *
     * 3.1 您可以不填写后台服务器地址：
     *     小直播 App 单靠客户端源码运行，方便快速跑通体验小直播。
     *     不过在这种模式下运行的“小直播”，没有注册登录、回放列表等功能，仅有基本的直播推拉流、聊天室、连麦等功能。
     *     另外在这种模式下，腾讯云安全签名 UserSig 是使用本地 GenerateTestUserSig 模块计算的，存在 SECRETKEY 被破解的导致腾讯云流量被盗用的风险。
     *
     * 3.2 您可以填写后台服务器地址：
     *     服务器需要您参考文档 https://cloud.tencent.com/document/product/454/15187 自行搭建。
     *     服务器提供注册登录、回放列表、计算 UserSig 等服务。
     *     这种情况下 SDKAPPID 和 SECRETKEY 可以设置为任意值。
     *
     * 注意：
     *     后台服务器地址（APP_SVR_URL）和 （SDKAPPID，SECRETKEY）一定要填一项。
     *     要么填写后台服务器地址（@link #APP_SVR_URL），要么填写 SDKAPPID 和 SECRETKEY。
     *
     * 详情请参考：
     */
    public static final String APP_SVR_URL = "";

}
