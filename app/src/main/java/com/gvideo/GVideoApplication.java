package com.gvideo;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.jxntv.ChatIMPluginImpl;
import com.jxntv.account.AccountPluginImpl;
import com.jxntv.account.CompositionPluginImpl;
import com.jxntv.account.FavoritePluginImpl;
import com.jxntv.account.FilePluginImpl;
import com.jxntv.account.H5PluginImpl;
import com.jxntv.account.InitializationPluginImpl;
import com.jxntv.account.VideoPlaySettingPluginImpl;
import com.jxntv.android.video.PhotoPreviewPluginImpl;
import com.jxntv.android.video.VideoPluginImpl;
import com.jxntv.base.BaseApplication;
import com.jxntv.base.environment.EnvironmentManager;
import com.jxntv.base.image.ImageLoaderImpl;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.AppSDKInitPlugin;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.CompositionPlugin;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.FavoritePlugin;
import com.jxntv.base.plugin.FeedPlugin;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.base.plugin.H5EntryPlugin;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.base.plugin.InitializationPlugin;
import com.jxntv.base.plugin.LivePlugin;
import com.jxntv.base.plugin.LoadMorePlugin;
import com.jxntv.base.plugin.PhotoPreviewPlugin;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.plugin.SearchPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.plugin.StatPlugin;
import com.jxntv.base.plugin.TestPlugin;
import com.jxntv.base.plugin.VideoPlaySettingPlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.plugin.WatchTvPlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.base.sharedprefs.KernelSharedPrefs;
import com.jxntv.base.view.DefaultRefreshFooterView;
import com.jxntv.base.view.DefaultRefreshHeadView;
import com.jxntv.circle.CirclePluginImpl;
import com.jxntv.feed.FeedPluginImpl;
import com.jxntv.home.HomePluginImpl;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.LivePluginImpl;
import com.jxntv.record.RecordPluginImpl;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.search.SearchPluginImpl;
import com.jxntv.share.SharePluginImpl;
import com.jxntv.stat.StatPluginImpl;
import com.jxntv.test.TestPluginImpl;
import com.jxntv.utils.DeviceId;
import com.jxntv.utils.LogUtils;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.watchtv.WatchTvPluginImpl;
import com.jxntv.webview.WebViewPluginImpl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import static com.jxntv.base.Constant.SP_LOGIN_HAS_VERIFICATE;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;


/**
 * App入口
 */
public final class GVideoApplication extends BaseApplication {

  //<editor-fold desc="生命周期">
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
    GVideoRuntime.onApplicationattachBaseContext(this, BuildConfig.FLAVOR);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected void onMainProcessCreate() {
    long t = System.currentTimeMillis();
    // DeviceId
    DeviceId.init(this).externalStorageDirectoryName("GVideo").init();
    EnvironmentManager.getInstance().setTestProductFlavors(TextUtils.equals("Test",BuildConfig.FLAVOR));
    EnvironmentManager.getInstance().setEnvironment(new GVideoEnvironment());
    // 初始化网络
    NetworkInitialization.initialize(this);
    // 图片
    ImageLoaderManager.setImageLoader(new ImageLoaderImpl());
    // 工具类
    ResourcesUtils.init(this);
    // 生命周期回调
    registerActivityLifecycleCallbacks(new GVideoLifecycleCallbacks());
    // Plugin注册
    initPlugin();
    // SmartRefreshLayout
    SmartRefreshLayout.setDefaultRefreshHeaderCreator(
        (context, refreshLayout) -> new DefaultRefreshHeadView(context)
    );
    SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new DefaultRefreshFooterView(context));

    if (TextUtils.equals("vip",BuildConfig.FLAVOR)) {
      KernelSharedPrefs.getInstance().putBoolean(SP_LOGIN_HAS_VERIFICATE, true);
    }
    LogUtils.e("初始化用时："+(System.currentTimeMillis() - t));

    // 取消订阅后，抛出的异常无法捕获，导致程序崩溃，使用此方法解决
    RxJavaPlugins.setErrorHandler(Throwable::printStackTrace);

  }

  private void initPlugin() {
    PluginManager.register(VideoPlugin.class, new VideoPluginImpl());
    PluginManager.register(AccountPlugin.class, new AccountPluginImpl());
    PluginManager.register(CirclePlugin.class, new CirclePluginImpl());
    PluginManager.register(HomePlugin.class, new HomePluginImpl());
    PluginManager.register(VideoPlaySettingPlugin.class, new VideoPlaySettingPluginImpl());
    PluginManager.register(WebViewPlugin.class, new WebViewPluginImpl());
    PluginManager.register(FeedPlugin.class, new FeedPluginImpl());
    PluginManager.register(SearchPlugin.class, new SearchPluginImpl());
    PluginManager.register(SharePlugin.class, new SharePluginImpl());
    PluginManager.register(TestPlugin.class, new TestPluginImpl());
    PluginManager.register(FilePlugin.class, new FilePluginImpl());
    PluginManager.register(H5Plugin.class, new H5PluginImpl());
    PluginManager.register(InitializationPlugin.class, new InitializationPluginImpl());
    PluginManager.register(LoadMorePlugin.class, new LoadMorePluginImpl());
    PluginManager.register(FavoritePlugin.class, new FavoritePluginImpl());
    PluginManager.register(CompositionPlugin.class, new CompositionPluginImpl());
    PluginManager.register(H5EntryPlugin.class, new H5EntryPluginImpl());
    PluginManager.register(RecordPlugin.class, new RecordPluginImpl());
    PluginManager.register(PhotoPreviewPlugin.class, new PhotoPreviewPluginImpl());
    PluginManager.register(LivePlugin.class,new LivePluginImpl());
    PluginManager.register(DetailPagePlugin.class,new DetailPagePluginImpl());
    PluginManager.register(ChatIMPlugin.class,new ChatIMPluginImpl());
    PluginManager.register(AppSDKInitPlugin.class,new AppSDKInitPluginImpl());
    PluginManager.register(WatchTvPlugin.class,new WatchTvPluginImpl());
    PluginManager.register(StatPlugin.class,new StatPluginImpl());
  }


  @Override
  public Resources getResources() {
    return super.getResources();
  }
  //</editor-fold>
}
