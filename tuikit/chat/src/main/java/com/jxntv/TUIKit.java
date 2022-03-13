package com.jxntv;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.LogUtils;
import com.jxntv.utils.OfflineMessageDispatcher;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.TUILogin;
import com.tencent.qcloud.tuikit.tuichat.bean.OfflineMessageBean;

import java.util.HashMap;
import java.util.Map;


public class TUIKit {
    public static final String TAG = "TUIKit";

    /**
     * TUIKit的初始化函数
     *
     * @param context  应用的上下文，一般为对应应用的ApplicationContext
     * @param sdkAppID 您在腾讯云注册应用时分配的sdkAppID
     * @param config   IMSDK 的相关配置项，一般使用默认即可，需特殊配置参考API文档
     * @param listener IMSDK 初始化监听器
     */
    public static void init(Context context, int sdkAppID, @Nullable V2TIMSDKConfig config, @Nullable V2TIMSDKListener listener) {
        TUILogin.init(context, sdkAppID, config, listener);
    }

    /**
     * 释放一些资源等，一般可以在退出登录时调用
     */
    public static void unInit() {
        TUILogin.unInit();
    }

    /**
     * 获取TUIKit保存的上下文Context，该Context会长期持有，所以应该为Application级别的上下文
     *
     * @return
     */
    public static Context getAppContext() {
        return TUILogin.getAppContext();
    }


    /**
     * 用户IM登录
     *
     * @param userid   用户名
     * @param usersig  从业务服务器获取的usersig
     * @param callback 登录是否成功的回调
     */
    public static void login(final String userid, final String usersig, final V2TIMCallback callback) {
        TUILogin.login(userid, usersig, new V2TIMCallback() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
                loginTUIKitLive(TUILogin.getSdkAppId(), userid, usersig);
            }

            @Override
            public void onError(int code, String desc) {
                if (callback != null) {
                    callback.onError(code, desc);
                }
            }
        });
    }

    public static void logout(final V2TIMCallback callback) {
        logoutTUIKitLive();
        TUILogin.logout(callback);
    }


    private static void loginTUIKitLive(int sdkAppId, String userId, String userSig) {
        Map<String, Object> param = new HashMap<>();
        param.put(TUIConstants.TUILive.SDK_APP_ID, sdkAppId);
        param.put(TUIConstants.TUILive.USER_ID, userId);
        param.put(TUIConstants.TUILive.USER_SIG, userSig);
        TUICore.callService(TUIConstants.TUILive.SERVICE_NAME, TUIConstants.TUILive.METHOD_LOGIN, param);
    }

    private static void logoutTUIKitLive() {
        TUICore.callService(TUIConstants.TUILive.SERVICE_NAME, TUIConstants.TUILive.METHOD_LOGOUT, null);
    }

    public static boolean isUserLogined() {
        return TUILogin.isUserLogined();
    }

    public static void sendLiveGroupMessage(String data) {
        Map<String, Object> map = new HashMap<>();
        map.put(TUIConstants.TUIChat.MESSAGE_CONTENT, data);
        TUICore.callService(TUIConstants.TUIChat.SERVICE_NAME, TUIConstants.TUIChat.METHOD_SEND_MESSAGE, map);
    }

    public static void startActivity(String activityName, Bundle param) {
        TUICore.startActivity(activityName, param);
    }

    /**
     * 进入私信、群聊界面
     *
     * @param chatId   id
     * @param chatName 昵称、群名称
     * @param chatType 聊天类型
     */
    public static void startChat(String chatId, String chatName, int chatType) {
        Bundle bundle = new Bundle();
        bundle.putString(TUIConstants.TUIChat.CHAT_ID, chatId);
        bundle.putString(TUIConstants.TUIChat.CHAT_NAME, chatName);
        bundle.putInt(TUIConstants.TUIChat.CHAT_TYPE, chatType);
        if (chatType == V2TIMConversation.V2TIM_C2C) {
            TUICore.startActivity(TUIConstants.TUIChat.C2C_CHAT_ACTIVITY_NAME, bundle);
        } else if (chatType == V2TIMConversation.V2TIM_GROUP) {
            TUICore.startActivity(TUIConstants.TUIChat.GROUP_CHAT_ACTIVITY_NAME, bundle);
        }
    }

    public static void startCall(String sender, String data) {
        Map<String, Object> param = new HashMap<>();
        param.put(TUIConstants.TUICalling.SENDER, sender);
        param.put(TUIConstants.TUICalling.PARAM_NAME_CALLMODEL, data);
        TUICore.callService(TUIConstants.TUICalling.SERVICE_NAME, TUIConstants.TUICalling.METHOD_START_CALL, param);
    }

    /**
     * 更新用户头像、昵称
     */
    public static void updateProfile() {

        if (PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            V2TIMUserFullInfo v2TIMUserFullInfo = new V2TIMUserFullInfo();
            v2TIMUserFullInfo.setNickname(PluginManager.get(AccountPlugin.class).getNickName());
            v2TIMUserFullInfo.setFaceUrl(PluginManager.get(AccountPlugin.class).getRealUserAvatar());
            V2TIMManager.getInstance().setSelfInfo(v2TIMUserFullInfo, new V2TIMCallback() {
                @Override
                public void onSuccess() {
                    LogUtils.d("更新用户信息成功");
                }

                @Override
                public void onError(int i, String s) {
                    LogUtils.e("更新用户信息失败：" + s + " , code =" + i);
                }
            });
        }

    }


    /**
     * 处理IM离线消息
     *
     * @param intent
     */
    public static void handleOfflinePush(Intent intent) {
        if (V2TIMManager.getInstance().getLoginStatus() == V2TIMManager.V2TIM_STATUS_LOGOUT) {
            return;
        }
        final OfflineMessageBean bean = OfflineMessageDispatcher.parseOfflineMessage(intent);
        dealMessageBean(bean);
    }

    public static void dealMessageBean(OfflineMessageBean bean) {
        if (bean != null) {
            LogUtils.e("im 消息内容" + new Gson().toJson(bean));
//            setIntent(null);
            NotificationManager manager = (NotificationManager) GVideoRuntime.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.cancelAll();
            }

            if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CHAT) {
                if (TextUtils.isEmpty(bean.sender)) {
                    return;
                }
                TUIKit.startChat(bean.sender, bean.nickname, bean.chatType);
            } else if (bean.action == OfflineMessageBean.REDIRECT_ACTION_CALL) {
//                IBaseLiveListener baseCallListener = TUIKitLiveListenerManager.getInstance().getBaseCallListener();
//                if (baseCallListener != null) {
//                    baseCallListener.handleOfflinePushCall(bean);
//                }
            }
        }
    }

    /**
     * 删除会话
     *
     * @param userId 用户id
     */
    public static void deleteConversation(String userId) {

        if (TUILogin.isUserLogined()) {
            V2TIMManager.getConversationManager().deleteConversation("c2c_"+userId, new V2TIMCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {
                    LogUtils.e("error:" + s + ", code:" + i);
                }
            });
        }
    }

}
