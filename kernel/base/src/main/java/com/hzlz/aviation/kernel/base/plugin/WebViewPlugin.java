package com.hzlz.aviation.kernel.base.plugin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.library.ioc.Plugin;

/**
 * WebView 接口
 *
 *
 * @since 2020-02-20 10:58
 */
public interface WebViewPlugin extends Plugin {
  /**
   * 拦截js协议后，关闭当前页面
   */
  String EVENT_CLOSE_PAGE = "event_close_page";
  /**
   * 拦截js协议后，隐藏导航栏
   */
  String EVENT_HIDE_NAV = "event_hide_nav";
  /**
   * 拦截js协议后，设置标题
   */
  String EVENT_SET_TITLE = "event_set_title";

  /**
   * 添加 WebView 目的地
   *
   * @param fragment Fragment
   */
  void addWebViewDestination(@NonNull BaseFragment fragment);

  /**
   * 跳转 WebView 界面
   *
   * @param view 控件
   * @param arguments 参数
   */
  void startWebViewFragment(@NonNull View view, @NonNull Bundle arguments);

  /**
   * 跳转 WebView 界面
   *
   * @param fragment Fragment
   * @param arguments 参数
   */
  void startWebViewFragment(@NonNull Fragment fragment, @NonNull Bundle arguments);

  /**
   * 获取webview 界面
   * @param url 链接
   * @return
   */
  BaseFragment getWebViewFragment(@NonNull String url,@NonNull Bundle arguments);

  /**
   * 跳转 WebView 界面
   *
   * @param activity Activity
   * @param viewId View id 用于查找 NavHostFragment
   * @param arguments 参数
   */
  void startWebViewFragment(@NonNull Activity activity, @IdRes int viewId, @NonNull Bundle arguments);


  /**
   * 对于某些不在Fragment栈上的请求（如Dialog），启动Activity响应
   */
  void startWebViewActivity(@NonNull Context context, String url,String title);
}
