package com.gvideo;

import static com.jxntv.base.Constant.BUNDLE_KEY.START_VIDEO_TAB_ID;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.H5EntryPlugin;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

/**
 * 外链分发入口
 */
public class H5EntryPluginImpl implements H5EntryPlugin {
  /**
   * 应用冷启HomeFragment注册监听需要时间，消息延时100ms发送;
   * 如果需要，后续可以替换为startHomeActivity-startHomeFragment-解析分发；
   */
  private static final long DELAY_TIME = 100;
  @Override public  boolean dispatch(Context context, Intent intent) {
    Uri uri = intent.getData();
    //im oppo离线消息
    if(TextUtils.equals(intent.getAction(), OPPO_ACTION)) {
      PluginManager.get(ChatIMPlugin.class).dispatcherPushMessage(intent);
      return true;
    }
    if (uri == null || !TextUtils.equals(uri.getScheme(), SCHEME)) {
      return false;
    }
    if (TextUtils.equals(uri.getHost(), HOST_DETAIL)) {
      dispatchDetail(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_PGC)) {
      dispatchPGC(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_UGC)) {
      dispatchUGC(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_VIDEO)) {
      dispatchVideoTab(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_FM)) {
      dispatchFmTab(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_HOME)) {
      dispatchHomeTab(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_LIVE)) {
      dispatchLiveTab(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_WEB)) {
      dispatchWeb(context, uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_IM_PUSH)) {
      PluginManager.get(ChatIMPlugin.class).dispatcherPushMessage(intent);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_NOTIFICATION)){
      dispatchNotification(context,uri);
      return true;
    }
    if (TextUtils.equals(uri.getHost(), HOST_JX_NEWS)){
      dispatchJXNews(context,uri);
      return true;
    }
    return false;
  }

  @Override
  public String getScheme() {
    return SCHEME;
  }

  private boolean dispatchDetail(Context context, Uri uri) {
    String mediaId = uri.getLastPathSegment();
    int mediaType;
    String columnId = null;
    try {
      mediaType = Integer.parseInt(uri.getQueryParameter("mediaType"));
      columnId = uri.getQueryParameter("programId");
    } catch (Exception e) {
      mediaType = MediaType.LONG_VIDEO;
    }
    Bundle bundle = new Bundle();
    bundle.putString(VideoPlugin.EXTRA_FROM_PID, StatPid.WEB);
    VideoModel model = VideoModel.Builder.aVideoModel()
        .withId(mediaId)
        .withMediaType(mediaType)
        .withColumnId(columnId)
        .build();
    PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context,model,bundle);

    return true;
  }

  private boolean dispatchPGC(Context context, Uri uri) {
    String authorId = uri.getLastPathSegment();
    GVideoEventBus.get(HomePlugin.EVENT_PGC, String.class).postDelay(authorId, DELAY_TIME);
    return false;
  }

  private boolean dispatchUGC(Context context, Uri uri) {
    String authorId = uri.getLastPathSegment();
    GVideoEventBus.get(HomePlugin.EVENT_UGC, String.class).postDelay(authorId, DELAY_TIME);
    return false;
  }

  private boolean dispatchVideoTab(Context context, Uri uri) {
    String videoTabId = uri.getLastPathSegment();
    GVideoEventBus.get(HomePlugin.EVENT_VIDEO_TAB, String.class).postDelay(videoTabId, DELAY_TIME);
    return false;
  }

  private boolean dispatchFmTab(Context context, Uri uri) {
    String fmTabId = uri.getLastPathSegment();
    GVideoEventBus.get(HomePlugin.EVENT_FM_TAB, String.class).postDelay(fmTabId, DELAY_TIME);
    return false;
  }

  private boolean dispatchHomeTab(Context context, Uri uri) {
    String fmTabId = uri.getLastPathSegment();
    Bundle bundle = new Bundle();
    bundle.putString(START_VIDEO_TAB_ID, fmTabId);
    GVideoEventBus.get(HomePlugin.EVENT_HOME_TAB, Bundle.class).postDelay(bundle, DELAY_TIME);
    return false;
  }

  private boolean dispatchLiveTab(Context context, Uri uri) {
    String fmTabId = uri.getLastPathSegment();
    GVideoEventBus.get(HomePlugin.EVENT_LIVE_TAB, String.class).postDelay(fmTabId, DELAY_TIME);
    return false;
  }

  private boolean dispatchWeb(Context context, Uri uri) {
    String encodedUrl = uri.getQueryParameter("gv_url");
    PluginManager.get(WebViewPlugin.class).startWebViewActivity(context, Uri.decode(encodedUrl),
            "");
    return false;
  }

  private boolean dispatchNotification(Context context, Uri uri){
    int msgType = 0;
    String title = "消息";
    try {
      msgType = Integer.parseInt(uri.getQueryParameter("msgType"));
      title = uri.getQueryParameter("title");
    } catch (Exception e) {
      e.printStackTrace();
    }
    PluginManager.get(AccountPlugin.class).startNotificationDetailActivity(
            context,
            msgType,
            title,
            ResourcesUtils.getString(R.string.h5_page)
    );
    return false;
  }

  private void dispatchJXNews(Context context, Uri uri) {
    String mediaId = uri.getLastPathSegment();
    Bundle bundle = new Bundle();
    bundle.putString(VideoPlugin.EXTRA_FROM_PID, StatPid.WEB);
    VideoModel model = VideoModel.Builder.aVideoModel()
                                       .withId(mediaId)
                                       .build();
    PluginManager.get(VideoPlugin.class).startJXNewsActivity(context, model, bundle);
  }

}
