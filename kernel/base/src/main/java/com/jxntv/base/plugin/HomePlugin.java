package com.jxntv.base.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.circle.CircleDetail;
import com.jxntv.ioc.Plugin;

import io.reactivex.rxjava3.core.Observable;

/**
 * Home模块接口
 */
public interface HomePlugin extends Plugin {

    /** 首页搜索预制文案 */
    String EVENT_HOME_SEARCH_WORD = "home_search_word";
    /** 侧边栏事件 */
    String EVENT_HOME_DRAWER = "home_drawer_event";
    /** 打开消息界面 */
    String EVENT_HOME_MESSAGE = "home_message_event";
    /** 检查安装包更新 */
    String EVENT_UPDATE_MODEL = "event_update_model";
    /** 跳转到video某tab */
    String EVENT_VIDEO_TAB = "event_video_tab";
    /** 跳转到fm某tab */
    String EVENT_FM_TAB = "event_fm_tab";
    /** 跳转到首页某tab */
    String EVENT_HOME_TAB = "event_home_tab";
    /** 跳转到直播某tab */
    String EVENT_LIVE_TAB = "event_live_tab";
    /** 跳转到pgc */
    String EVENT_PGC = "event_pgc";
    /** 跳转到UGC */
    String EVENT_UGC = "event_ugc";
    /** 跳转到圈子 */
    String EVENT_CIRCLE = "event_circle";
    /** 跳转到personal */
    String EVENT_PERSONAL = "event_personal";
    /** 跳转到某tab key */
    String KEY_TRANS_TAB = "key_trans_tab";
    /** 跳转到personal某tab */
    String TRANS_PERSONAL_TAB = "event_personal_tab";

    void startBlankActivity(Context context);

    /**
     * 跳转至首页activity
     *
     * @param context 上下文
     */
    void navigateToHomeActivity(@NonNull Context context);

    void navigateToHomePersonFragment(@NonNull Context context);

    boolean hasHomeActivity(Context context, Bundle bundle);

    /**
     * 重新启动app
     */
    void restartApp(@NonNull Context context);

    Observable<BannerModel> getBannerTopList(long locationId);

}
