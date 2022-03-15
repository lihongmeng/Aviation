package com.hzlz.aviation.kernel.push.push.hms;

import android.text.TextUtils;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.hzlz.aviation.kernel.push.PushManager;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.async.GlobalExecutor;

/**
 * 华为推送工具类
 */
public class HmsPushHelper {

    private static HmsPushHelper hmsPushHelper;
    private PushManager.PushListener listener;

    public static HmsPushHelper getInstance() {
        if (hmsPushHelper == null) {
            hmsPushHelper = new HmsPushHelper();
        }
        return hmsPushHelper;
    }

    /**
     * 设置token
     * @param token 注册id
     */
    public void setHmsToken(String token) {
        if (listener!=null) listener.success(token);
    }


    public void init(PushManager.PushListener listener){
        this.listener = listener;
        GlobalExecutor.execute(() -> {
            try {
                String appId = AGConnectServicesConfig.fromContext(GVideoRuntime.getAppContext()).getString("client/app_id");
                String token = HmsInstanceId.getInstance(GVideoRuntime.getAppContext()).getToken(appId,"HCM");
                if (!TextUtils.isEmpty(token)){
                    setHmsToken(token);
                }
            } catch (ApiException e) {
                e.printStackTrace();
                listener.error("hms token 获取失败，error:"+e.getMessage());
            }
        }, "hms", GlobalExecutor.PRIORITY_USER);
        HmsMessaging.getInstance(GVideoRuntime.getAppContext()).turnOnPush().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LogUtils.d( "通知消息设置打开");
            } else {
                LogUtils.d( "通知消息设置关闭");
            }
        });
    }



    public void deleteToken() {

        GlobalExecutor.execute(() -> {
            try {
                String appId = AGConnectServicesConfig.fromContext(GVideoRuntime.getAppContext()).getString("client/app_id");
                HmsInstanceId.getInstance(GVideoRuntime.getAppContext()).deleteToken(appId, "HCM");
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }, "hms", GlobalExecutor.PRIORITY_USER);
    }

}
