package com.gvideo;

import static com.jxntv.async.GlobalExecutor.PRIORITY_USER;

import com.jxntv.PushManager;
import com.jxntv.account.AccountJavaScriptCommunicationHandler;
import com.jxntv.async.GlobalExecutor;
import com.jxntv.base.environment.EnvironmentManager;
import com.jxntv.base.plugin.AppSDKInitPlugin;
import com.jxntv.base.plugin.LivePlugin;
import com.jxntv.base.webview.JavaScriptCommunicationDispatcher;
import com.jxntv.ioc.PluginManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.GVideoStatManager;
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
            CrashReport.initCrashReport(GVideoRuntime.getAppContext(), BuildConfig.DEBUG ||
                       EnvironmentManager.getInstance().getIsTestProductFlavors() ? "e736eb592f" : "c627168a3f", BuildConfig.DEBUG);
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
