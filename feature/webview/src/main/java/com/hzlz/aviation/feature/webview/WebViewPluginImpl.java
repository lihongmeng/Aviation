package com.hzlz.aviation.feature.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;

/**
 * WebView 接口实现
 *
 *
 * @since 2020-02-20 11:02
 */
public final class WebViewPluginImpl implements WebViewPlugin {
  @Override
  public void addWebViewDestination(@NonNull BaseFragment fragment) {
    fragment.addDestination(R.navigation.web_view_nav_graph);
  }

  @Override
  public void startWebViewFragment(@NonNull View view, @NonNull Bundle arguments) {
    Navigation.findNavController(view).navigate(R.id.web_view_nav_graph, arguments);
  }

  @Override
  public void startWebViewFragment(@NonNull Fragment fragment, @NonNull Bundle arguments) {
    NavHostFragment.findNavController(fragment).navigate(R.id.web_view_nav_graph, arguments);
  }

  @Override
  public BaseFragment getWebViewFragment(@NonNull String url,@NonNull Bundle arguments) {
    arguments.putString("url", url);
    WebViewFragment webViewFragment = new WebViewFragment();
    webViewFragment.setArguments(arguments);
    return webViewFragment;
  }

  @Override
  public void startWebViewFragment(@NonNull Activity activity,@IdRes int viewId, @NonNull Bundle arguments) {
    Navigation.findNavController(activity,viewId).navigate(R.id.web_view_nav_graph, arguments);
  }

  @Override
  public void startWebViewActivity(@NonNull Context context, String url, String title) {

    // 避免是null类型，系统似乎会报错
    if (TextUtils.isEmpty(url)) {
      return;
    }

    Bundle b = new Bundle();
    b.putString("url", url);
    b.putString("title", TextUtils.isEmpty(title) ? WebViewActivity.class.getName() : title);
    Intent intent = new Intent(context, WebViewActivity.class);
    intent.putExtras(b);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
