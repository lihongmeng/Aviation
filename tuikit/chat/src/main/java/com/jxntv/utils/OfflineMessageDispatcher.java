package com.jxntv.utils;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.jxntv.PushManager;
import com.jxntv.TUIKit;
import com.jxntv.runtime.GVideoRuntime;
import com.tencent.qcloud.tuikit.tuichat.bean.OfflineMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.OfflineMessageContainerBean;
import java.util.Set;

/**
 * 离线消息分发
 */
public class OfflineMessageDispatcher {

    private static final String TAG = OfflineMessageDispatcher.class.getSimpleName();

    /**
     * 离线消息解析
     */
    public static OfflineMessageBean parseOfflineMessage(Intent intent) {
        if (intent == null) {
            LogUtils.e("intent is null");
            return null;
        }
        Bundle bundle = intent.getExtras();
        String ext;
        if (bundle == null) {
            ext = PushManager.getInstance().getVivoMessage();
            if (!TextUtils.isEmpty(ext)) {
                return getOfflineMessageBeanFromContainer(ext);
            }
        } else {
            ext = bundle.getString("ext");
            if (TextUtils.isEmpty(ext)) {
                if (OSUtil.isMiui()) {
                    ext = PushManager.getInstance().getXiaomiMessageByBundle(bundle);
                    return getOfflineMessageBeanFromContainer(ext);
                } else if (OSUtil.isOppo()) {
                    ext = getOPPOMessage(bundle);
                    return getOfflineMessageBean(ext);
                }
            } else {
                return getOfflineMessageBeanFromContainer(ext);
            }
        }
        return null;
    }


    private static String getOPPOMessage(Bundle bundle) {
        Set<String> set = bundle.keySet();
        if (set != null) {
            for (String key : set) {
                Object value = bundle.get(key);
                if (TextUtils.equals("entity", key)) {
                    return value.toString();
                }
            }
        }
        return null;
    }

    public static OfflineMessageBean getOfflineMessageBeanFromContainer(String ext) {
        if (TextUtils.isEmpty(ext)) {
            return null;
        }
        OfflineMessageContainerBean bean = null;
        try {
            bean = new Gson().fromJson(ext, OfflineMessageContainerBean.class);
        } catch (Exception e) {
           LogUtils.w(TAG, "getOfflineMessageBeanFromContainer: " + e.getMessage());
        }
        if (bean == null) {
            return null;
        }
        return offlineMessageBeanValidCheck(bean.entity);
    }

    private static OfflineMessageBean getOfflineMessageBean(String ext) {
        if (TextUtils.isEmpty(ext)) {
            return null;
        }
        OfflineMessageBean bean = new Gson().fromJson(ext, OfflineMessageBean.class);
        return offlineMessageBeanValidCheck(bean);
    }

    private static OfflineMessageBean offlineMessageBeanValidCheck(OfflineMessageBean bean) {
        if (bean == null) {
            return null;
        } else if (bean.version != 1
                || (bean.action != OfflineMessageBean.REDIRECT_ACTION_CHAT
                    && bean.action != OfflineMessageBean.REDIRECT_ACTION_CALL) ) {
            PackageManager packageManager = GVideoRuntime.getAppContext().getPackageManager();
            String label = String.valueOf(packageManager.getApplicationLabel(GVideoRuntime.getAppContext().getApplicationInfo()));
            LogUtils.e(TAG, "unknown version: " + bean.version + " or action: " + bean.action);
            return null;
        }
        return bean;
    }

    public static boolean redirect(final OfflineMessageBean bean) {
        if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CHAT) {
            if (TextUtils.isEmpty(bean.sender)) {
                return true;
            }
            TUIKit.startChat(bean.sender, bean.nickname, bean.chatType);
            return true;
        } else if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CALL) {
//            IBaseLiveListener baseCallListener = TUIKitLiveListenerManager.getInstance().getBaseCallListener();
//            if (baseCallListener != null) {
//                baseCallListener.redirectCall(bean);
//            }
        }
        return true;
    }
}
