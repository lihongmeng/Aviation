package com.hzlz.aviation.app;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5EntryPlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.webview.JavaScriptCommunicationHandler;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;

/**
 * JS交互协议
 */
public class WebJavaScriptHandler implements JavaScriptCommunicationHandler {
  /**
   * 今视频统一外链scheme
   */
  String SCHEME = BuildConfig.app_scheme;

  String HOST = "h5-native";

  String COMMANDS = "/commands/";
  String DATAS = "/datas/";

  String PATH_CLOSE_PAGE = COMMANDS + "closePage";
  String PATH_HIDE_NAV =  COMMANDS + "hideNavigationBar";
  String PATH_SET_TITLE = COMMANDS + "setNavTitle";
  String PATH_LOGIN =  COMMANDS + "login";
  String PATH_SHARE = COMMANDS + "share";
  String PATH_APP_VERSION = DATAS + "appVersion";
  String PATH_USER_TOKEN = DATAS + "userToken";
  String PATH_OPEN_APP_PAGE = COMMANDS + "openAppPage";
  String PARAM_TITLE = "title";
  String PARAM_URL = "url";
  String PARAM_IMAGE = "image";
  String PARAM_DESCRIPTION = "description";

  @Override public HandleResult handle(@NonNull WebView webView, @NonNull String uriString) {
    Uri uri = Uri.parse(uriString);
    if (!TextUtils.equals(SCHEME, uri.getScheme())) {
      return null;
    }
    if (!TextUtils.equals(HOST, uri.getHost())) {
      return null;
    }

    if (TextUtils.equals(PATH_CLOSE_PAGE, uri.getPath())) {
      dispatchClosePage(webView, uri);
      return new HandleResult(true, "");
    } else if (TextUtils.equals(PATH_HIDE_NAV, uri.getPath())) {
      dispatchHideNav(webView, uri);
      return new HandleResult(true, "");
    } else if (TextUtils.equals(PATH_SET_TITLE, uri.getPath())) {
      dispatchSetTitle(webView, uri);
      return new HandleResult(true, "");
    } else if (TextUtils.equals(PATH_APP_VERSION, uri.getPath())) {
      return new HandleResult(true, GVideoRuntime.getVersionName());
    }else if (TextUtils.equals(PATH_USER_TOKEN,uri.getPath())){
      return new HandleResult(true, PluginManager.get(AccountPlugin.class).getToken());
    }else if (TextUtils.equals(PATH_LOGIN,uri.getPath())){
      dispatchLogin(webView);
      return new HandleResult(true, "");
    }else if (TextUtils.equals(PATH_OPEN_APP_PAGE,uri.getPath())){
      dispatchDetail(webView,uri);
      return new HandleResult(true, "");
    }else if (TextUtils.equals(PATH_SHARE, uri.getPath())){
      dispatchShare(webView,uri);
      return new HandleResult(true,"");
    }
    return null;
  }

  private void dispatchClosePage(WebView webView, Uri uri) {
    GVideoEventBus.get(WebViewPlugin.EVENT_CLOSE_PAGE).post(0);
  }

  private void dispatchHideNav(WebView webView, Uri uri) {
    GVideoEventBus.get(WebViewPlugin.EVENT_HIDE_NAV).post(0);
  }

  private void dispatchSetTitle(WebView webView, Uri uri) {
    String title = uri.getQueryParameter(PARAM_TITLE);
    GVideoEventBus.get(WebViewPlugin.EVENT_SET_TITLE, String.class).post(title);
  }

  private void dispatchShare(WebView webView, Uri uri){
    String title = uri.getQueryParameter(PARAM_TITLE);
    String url = uri.getQueryParameter(PARAM_URL);
    String image = uri.getQueryParameter(PARAM_IMAGE);
    String description = uri.getQueryParameter(PARAM_DESCRIPTION);
    ShareDataModel shareDataModel = new ShareDataModel.Builder()
            .setUrl(url)
            .setTitle(title)
            .setImage(image)
            .setDescription(description)
            .setShowReport(false)
            .setShowFavorite(false)
            .setShowFollow(false)
            .build();
    PluginManager.get(SharePlugin.class).showShareDialog(webView.getContext(),false,shareDataModel,null);
  }

  private void dispatchDetail(WebView webView, Uri uri) {
    String url = uri.getQueryParameter(PARAM_URL);
    Intent intent = new Intent();
    intent.setData(Uri.parse(url));
    PluginManager.get(H5EntryPlugin.class).dispatch(webView.getContext(),intent);
  }

  private void dispatchLogin(WebView webView) {
    if (webView == null) {
      return;
    }
    PluginManager.get(AccountPlugin.class).startLoginActivity(webView.getContext());
    String title = webView.getTitle();
    String h5PageString = ResourcesUtils.getString(R.string.h5_page);
    title = TextUtils.isEmpty(title) ? h5PageString : title + "-" + h5PageString;
    GVideoSensorDataManager.getInstance().enterRegister(title, title);
  }

}
