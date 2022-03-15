package com.hzlz.aviation.app;

import static com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_USER;

import com.hzlz.aviation.feature.account.AccountJavaScriptCommunicationHandler;
import com.hzlz.aviation.kernel.base.environment.EnvironmentManager;
import com.hzlz.aviation.kernel.base.plugin.AppSDKInitPlugin;
import com.hzlz.aviation.kernel.base.plugin.LivePlugin;
import com.hzlz.aviation.kernel.base.webview.JavaScriptCommunicationDispatcher;
import com.hzlz.aviation.kernel.push.PushManager;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.async.GlobalExecutor;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author huangwei
 * date : 2021/10/28
 * desc :
 **/
public class AppSDKInitPluginImpl implements AppSDKInitPlugin {

    boolean hasInit = false;

    @Override
    public void init() {
        if (!hasInit) {
            hasInit = true;
            initInThread();
        }
    }

    @Override
    public boolean hasInit() {
        return hasInit;
    }

    private void initInThread() {
        GlobalExecutor.execute(() -> {

            PluginManager.get(LivePlugin.class).initTencentIM();
            //暂时注释推送初始化
            PushManager.getInstance().init();
            // 统计埋点，其中需要通过plugin获取uid，初始化位置调整
            GVideoStatManager.getInstance().init();
            GVideoStatManager.getInstance().setChannelId(BuildConfig.FLAVOR);
            // 神策埋点
            GVideoSensorDataManager.getInstance().init();
            GVideoSensorDataManager.getInstance().login();
            // Bugly 崩溃回传
            CrashReport.initCrashReport(GVideoRuntime.getAppContext(), true ||
                       EnvironmentManager.getInstance().getIsTestProductFlavors() ? "e736eb592f" : "c627168a3f", true);
            // WebView 初始化
            initWebView();

            FileDownloader.setupOnApplicationOnCreate(GVideoRuntime.getApplication())
                    .connectionCreator(new FileDownloadUrlConnection
                            .Creator(new FileDownloadUrlConnection.Configuration()
                            .connectTimeout(15_000)
                            .readTimeout(15_000)
                    ))
                    .commit();


        }, "initInThread", PRIORITY_USER);
    }


    private void initWebView() {
        JavaScriptCommunicationDispatcher.getInstance().addHandler(
                new WebJavaScriptHandler()
        );
        JavaScriptCommunicationDispatcher.getInstance().addHandler(
                AccountJavaScriptCommunicationHandler.getInstance()
        );
    }
}
