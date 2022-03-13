package com.jxntv.utils;

import android.text.TextUtils;

import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.ioc.PluginManager;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMOfflinePushConfig;

/**
 * 用来保存厂商注册离线推送token的管理类示例，当登陆im后，通过 setOfflinePushToken 上报证书 ID 及设备 token 给im后台
 */

public class ThirdPushTokenMgr {

    /****** 华为离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static final long HW_PUSH_BUZID = 20515;
    /****** 华为离线推送参数end ******/

    /****** 小米离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static final long XM_PUSH_BUZID = 20516;
    /****** 小米离线推送参数end ******/

    /****** vivo离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static final long VIVO_PUSH_BUZID = 20514;
    /****** vivo离线推送参数end ******/

    /****** oppo离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static final long OPPO_PUSH_BUZID = 20513;
    /****** oppo离线推送参数end ******/

    private static final String TAG = ThirdPushTokenMgr.class.getSimpleName();
    private String mThirdPushToken;

    public static ThirdPushTokenMgr getInstance() {
        return ThirdPushTokenHolder.instance;
    }

    public String getThirdPushToken() {
        return mThirdPushToken;
    }

    public void setThirdPushToken(String mThirdPushToken) {
        this.mThirdPushToken = mThirdPushToken;
    }

    public void setPushTokenToTIM() {
        String token = ThirdPushTokenMgr.getInstance().getThirdPushToken();
        if (TextUtils.isEmpty(token)) {
            LogUtils.i(TAG, "setPushTokenToTIM third token is empty");
            return;
        }
        V2TIMOfflinePushConfig v2TIMOfflinePushConfig = null;
        if (OSUtil.isMiui()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(XM_PUSH_BUZID, token);
        } else if (OSUtil.isEmui()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(HW_PUSH_BUZID, token);
        } else if (OSUtil.isOppo()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(OPPO_PUSH_BUZID, token);
        } else if (OSUtil.isVivo()) {
            v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(VIVO_PUSH_BUZID, token);
        } else {
            return;
        }

        V2TIMManager.getOfflinePushManager().setOfflinePushConfig(v2TIMOfflinePushConfig, new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                LogUtils.e(TAG, "setOfflinePushToken err code = " + code + ", error:"+desc);

            }

            @Override
            public void onSuccess() {
                LogUtils.d(TAG, "setOfflinePushToken success");
                PluginManager.get(ChatIMPlugin.class).setOfflineMessageDoBackground(true);
            }
        });
    }

    private static class ThirdPushTokenHolder {
        private static final ThirdPushTokenMgr instance = new ThirdPushTokenMgr();
    }
}
