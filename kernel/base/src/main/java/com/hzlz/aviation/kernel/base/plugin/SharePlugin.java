package com.hzlz.aviation.kernel.base.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.library.ioc.Plugin;

/**
 * 分享 plugin
 */
public interface SharePlugin extends Plugin {

    /**
     * 收藏状态变化时，通过EventBus通信时使用;
     * 收藏类别、收藏详情、我的收藏数字等监听到变化后，触发刷新；
     */
    String EVENT_FAVORITE_CHANGE = "event_favorite_change";

    /**
     * 关注状态变化时，通过EventBus通信时使用；
     * 关注列表、动态、我的关注数字等监听到变化后，触发刷新；
     */
    String EVENT_FOLLOW_CHANGE = "event_follow_change";

    /**
     * 分享浮层删除个人作品时，通过EventBus通信时使用
     */
    String EVENT_COMPOSITION_DELETE = "event_composition_delete_change";

    /**
     * 分享修改字体大小，通过EventBus通信时使用
     */
    String EVENT_SHOW_FONT_SETTING = "event_show_font_setting";

    /**
     * 显示分享弹窗
     *
     * @param context    上下文
     * @param isDarkMode 是否为暗黑模式
     */
    void showShareDialog(Context context, boolean isDarkMode, ShareDataModel model, StatFromModel statFromModel);


    /**
     * 显示分享弹窗
     *
     * @param context    上下文
     * @param isDarkMode 是否为暗黑模式
     * @param isHideMore 是否隐藏other按钮
     */
    void showShareDialog(Context context, boolean isDarkMode, boolean isHideMore, ShareDataModel model, StatFromModel statFromModel);

    void showCreateBillDialog(Activity activity);

    /**
     * 分享结果处理
     */
    void doShareResultIntent(int requestCode, int resultCode, Intent data);


    /**
     * 分享开关配置
     *
     * @param isCanShareWeiXin 是否展示微信分享
     * @param isCanShareQQ     是否展示qq分享
     * @param isCanShareWeibo  是否微博分享
     */
    void setShareConfig(boolean isCanShareWeiXin, boolean isCanShareQQ, boolean isCanShareWeibo);


    /**
     * 分享是否打开
     */
    boolean isCanShare();

    /**
     * 获取分享布局view
     */
    View getShareView(Context context, ShareDataModel model, StatFromModel stat);

    void startWXShare(Context context, ShareDataModel dataModel, StatFromModel stat);

    void startWXCircleShare(Context context, ShareDataModel dataModel, StatFromModel stat);

}
