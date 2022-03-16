package com.hzlz.aviation.app;

import static com.hzlz.aviation.kernel.base.Constant.SP_LOGIN_HAS_VERIFICATE;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.hzlz.aviation.feature.account.AccountPluginImpl;
import com.hzlz.aviation.feature.account.CompositionPluginImpl;
import com.hzlz.aviation.feature.account.FavoritePluginImpl;
import com.hzlz.aviation.feature.account.FilePluginImpl;
import com.hzlz.aviation.feature.account.H5PluginImpl;
import com.hzlz.aviation.feature.account.InitializationPluginImpl;
import com.hzlz.aviation.feature.account.VideoPlaySettingPluginImpl;
import com.hzlz.aviation.feature.community.CirclePluginImpl;
import com.hzlz.aviation.feature.feed.FeedPluginImpl;
import com.hzlz.aviation.feature.home.HomePluginImpl;
import com.hzlz.aviation.feature.live.LivePluginImpl;
import com.hzlz.aviation.feature.record.RecordPluginImpl;
import com.hzlz.aviation.feature.search.SearchPluginImpl;
import com.hzlz.aviation.feature.share.SharePluginImpl;
import com.hzlz.aviation.feature.test.TestPluginImpl;
import com.hzlz.aviation.feature.video.PhotoPreviewPluginImpl;
import com.hzlz.aviation.feature.video.VideoPluginImpl;
import com.hzlz.aviation.feature.watchtv.WatchTvPluginImpl;
import com.hzlz.aviation.feature.webview.WebViewPluginImpl;
import com.hzlz.aviation.kernel.base.BaseApplication;
import com.hzlz.aviation.kernel.base.environment.EnvironmentManager;
import com.hzlz.aviation.kernel.base.image.ImageLoaderImpl;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.AppSDKInitPlugin;
import com.hzlz.aviation.kernel.base.plugin.ChatIMPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.CompositionPlugin;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.FavoritePlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.plugin.H5EntryPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.base.plugin.InitializationPlugin;
import com.hzlz.aviation.kernel.base.plugin.LivePlugin;
import com.hzlz.aviation.kernel.base.plugin.LoadMorePlugin;
import com.hzlz.aviation.kernel.base.plugin.PhotoPreviewPlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.plugin.SearchPlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.plugin.StatPlugin;
import com.hzlz.aviation.kernel.base.plugin.TestPlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlaySettingPlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.plugin.WatchTvPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.sharedprefs.KernelSharedPrefs;
import com.hzlz.aviation.kernel.base.view.DefaultRefreshFooterView;
import com.hzlz.aviation.kernel.base.view.DefaultRefreshHeadView;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.StatPluginImpl;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DeviceId;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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
    DeviceId.init(this).externalStorageDirectoryName("Aviation").init();

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
