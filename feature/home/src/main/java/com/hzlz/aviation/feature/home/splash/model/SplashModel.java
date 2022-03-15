package com.hzlz.aviation.feature.home.splash.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 闪屏数据模型
 */
public class SplashModel {

    /** json key */
    private static final String KEY_AD_SOURCE_URL = "key_ad_source_url";
    private static final String KEY_AD_URL = "key_ad_url";
    private static final String KEY_COUNT_DOWN_SEC = "key_count_down_sec";
    private static final String KEY_COUNT_DOWN_STYLE = "key_count_down_style";
    private static final String KEY_DESCRIPTION = "key_description";
    private static final String KEY_JUMP_STYLE = "key_jump_style";
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_SOURCE_TYPE = "key_source_type";
    private static final String KEY_START_TIME = "key_start_time";
    private static final String KEY_END_TIME = "key_end_time";
    private static final String KEY_WEIGHT = "key_weight";
    private static final String KEY_MD5 = "key_md5";
    private static final String KEY_DURATION_SEC = "key_duration_sec";

    /** 广告资源url */
    public String adSourceUrl;
    /** 广告跳转url */
    public String adUrl;
    /** 广告图倒数秒数 */
    public int countDownSec;
    /** 广告图倒数样式 */
    public int countDownStyle;
    /** 推广秒数 */
    public String description;
    /** 挑战样式 */
    public int jumpStyle;
    /** 推广标题 */
    public String title;
    /** 资源类型 1：图片，2：视频 */
    public int sourceType;
    /** 广告开始显示时间 */
    public long startTime;
    /** 广告结束显示时间 */
    public long endTime;
    /** 资源权重 */
    public long weight;
    /** 资源Md5 */
    public String md5;
    /** 广告显示持续时间 */
    public int durationSec;

    /** 本地存储文件名 */
    public String fileName = "";

    /**
     * 数据解析器
     */
    public static class SplashDataParser {

        /**
         * 根据json生成splash model
         */
        public static SplashModel createFromJson(JSONObject jsonObj) {
            SplashModel model = new SplashModel();
            try {
                model.adSourceUrl = jsonObj.optString(KEY_AD_SOURCE_URL);
                model.adUrl = jsonObj.optString(KEY_AD_URL);
                model.countDownSec = jsonObj.optInt(KEY_COUNT_DOWN_SEC);
                model.countDownStyle = jsonObj.optInt(KEY_COUNT_DOWN_STYLE);
                model.description = jsonObj.optString(KEY_DESCRIPTION);
                model.jumpStyle = jsonObj.optInt(KEY_JUMP_STYLE);
                model.title = jsonObj.optString(KEY_TITLE);
                model.sourceType = jsonObj.optInt(KEY_SOURCE_TYPE);
                model.startTime = jsonObj.optLong(KEY_START_TIME);
                model.endTime = jsonObj.optLong(KEY_END_TIME);
                model.weight = jsonObj.optInt(KEY_WEIGHT);
                model.md5 = jsonObj.optString(KEY_MD5);
                model.durationSec = jsonObj.optInt(KEY_DURATION_SEC);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return model;
        }

        /**
         * 根据splash model生成json
         */
        public static JSONObject persistToJson(SplashModel model) {
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put(KEY_AD_SOURCE_URL, model.adSourceUrl);
                jsonObj.put(KEY_AD_URL, model.adUrl);
                jsonObj.put(KEY_COUNT_DOWN_SEC, model.countDownSec);
                jsonObj.put(KEY_COUNT_DOWN_STYLE, model.countDownStyle);
                jsonObj.put(KEY_DESCRIPTION, model.description);
                jsonObj.put(KEY_JUMP_STYLE, model.jumpStyle);
                jsonObj.put(KEY_TITLE, model.title);
                jsonObj.put(KEY_SOURCE_TYPE, model.sourceType);
                jsonObj.put(KEY_START_TIME, model.startTime);
                jsonObj.put(KEY_END_TIME, model.endTime);
                jsonObj.put(KEY_WEIGHT, model.weight);
                jsonObj.put(KEY_MD5, model.md5);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObj;
        }

    }

}
