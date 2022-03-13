package com.jxntv.base;

import com.jxntv.base.model.PublishLinkWhiteListItem;

import java.util.ArrayList;

/**
 * 保存一些程序运行期间全局的变量
 * 但是不要依赖此类，需要注意线程安全问题
 */
public class StaticParams {

    /**
     * 此类用于帮助首页圈子tab页的页码计算
     */
    public static int circleFragmentInitIndex = 0;

    /**
     * 用于区分launch过程中启动的登录界面
     * 还是从“我的”页面启动的
     * 如果isForeLogin = true，点击返回时要直接退出App
     */
    public static boolean isForeLogin = false;

    /**
     * 当前包含信息流的页面的tabId
     * 本应该在页面不可见的时候将此值置空
     * 因为Fragment的生命周期较为复杂
     * 例如弹窗引起的onPause，不需要置空
     * 故此值暂时不做置空处理
     * 如果跳转到没有tabId的页面
     * 那么此值保存的就是上一个页面的TabId
     */
    public static String currentTabId = "";

    /**
     * 发布链接的时候规定的白名单列表
     * 对这个值做更改一定要放在UI线程
     */
    public static ArrayList<PublishLinkWhiteListItem> whiteListItemArrayList = new ArrayList<>();

    /**
     * 当前出于前台的Fragment的StatPid
     * 在{@link BaseFragment#onResume()}方法中更新本值
     * 大数据统计时需要此值
     * 需要注意的是，如果是子线程需要此值，在获取的时候，UI线程跳转到其他界面
     * 取值就会不准
     *
     * 用之前先考虑一下
     */
    public static String currentStatPid = "";

    // 点击全屏按钮,需要做特殊处理
    // 例如手机是竖屏状态，这个时候点击全屏按钮，需要暂时锁定屏幕为横屏
    // 轻微幅度转动手机，即使有回调告知手机是竖屏状态，也不做处理
    // 只有将手机转到横屏再转回竖屏，将此值重置，此时才改变为竖屏状态
    // 因为旋转屏幕，Activity会重建，不能保存在Activity实例中，所以保存在此处
    // 需要注意此值的重置时机
    public static boolean needTemplateLockFullScreen;

    // 除了全屏按钮，返回按钮也有类似的逻辑，
    // 因为在同一个回调中处理，所以需要分开记录
    public static boolean needTemplateLockNotFullScreen;

}
