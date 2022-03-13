package com.jxntv;

import android.content.Intent;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.jxntv.base.model.ImConfigModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.plugin.StatPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.AsyncUtils;
import com.jxntv.utils.LogUtils;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.utils.ThirdPushTokenMgr;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tuikit.tuiconversation.ui.page.TUIConversationFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/10/19
 * desc :
 **/
public class ChatIMPluginImpl implements ChatIMPlugin {

    private boolean hasUnreadMessage = false;
    private boolean hasConversation = false;
    private String pushToken;
    //官方私信账号id,用于标记官方账号
    private List<String> platformAccountData = new ArrayList<>();
    private long unreadMessageCount;


    @Override
    public void login(ImConfigModel configModel) {
        TUIKit.init(GVideoRuntime.getAppContext(),configModel.getImAppId(),null,null);
        String userId = PluginManager.get(AccountPlugin.class).getUserId();
        if (TextUtils.isEmpty(userId)){
            return;
        }
        TUIKit.login(userId, configModel.getImSign(), new V2TIMCallback() {
            @Override
            public void onSuccess() {
                LogUtils.d("登录成功："+ userId);
//                AsyncUtils.runOnUIThread(() ->{
//                    if (!TextUtils.isEmpty(pushToken)){
//                        ThirdPushTokenMgr.getInstance().setThirdPushToken(pushToken);
//                        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
//                    }
//                    updateProfile();
//                },200);
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.e("登录失败："+ i+ "  |  "+ s);
            }
        });
    }

    @Override
    public void logout() {
        TUIKit.logout(new V2TIMCallback() {
            @Override
            public void onSuccess() {
                LogUtils.d(" IM 退出登录成功");
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.e("IM退出登录失败：code:" + i + "," + s);
            }
        });
    }

    @Override
    public Class<? extends Fragment> getConversationFragmentClass() {
        return TUIConversationFragment.class;
    }

    @Override
    public void updateProfile() {
        TUIKit.updateProfile();
    }

    @Override
    public void startC2CChatActivity(String userId, String nickName) {
        TUIKit.startChat(userId,nickName,V2TIMConversation.V2TIM_C2C);
    }

    @Override
    public void startGroupChatActivity(String groupId, String groupName) {
        TUIKit.startChat(groupId,groupName,V2TIMConversation.V2TIM_GROUP);
    }

    @Override
    public void getUnreadMessageCount(TotalUnreadMessageCountListener listener) {
        V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                if (listener!=null) {
                    if (aLong != null) {
                        hasUnreadMessage = aLong > 0;
                        listener.onSuccess(aLong);
                    } else {
                        hasUnreadMessage = false;
                        listener.onError();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                if (listener!=null) {
                    listener.onError();
                }
                LogUtils.e("获取未读聊天消息失败："+s + "  , code:"+i);
            }
        });
    }

    @Override
    public boolean getHasUnreadMessage() {
        return hasUnreadMessage;
    }

    @Override
    public void setThirdPushToken(String token) {
        pushToken = token;
//        if (V2TIMManager.getInstance().getLoginStatus() == V2TIMManager.V2TIM_STATUS_LOGINED){
//            ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
//            ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
//        }
    }

    @Override
    public void dispatcherPushMessage(Intent intent) {

        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()){
            accountPlugin.startLoginActivity(AppManager.getAppManager().currentActivity());
            PluginManager.get(StatPlugin.class).enterRegister(
                    "处理IM消息",
                    ResourcesUtils.getString(R.string.like)
            );
            return;
        }
        TUIKit.handleOfflinePush(intent);
    }

    @Override
    public void setOfflineMessageDoBackground(boolean isBackground) {
        //暂时关闭离线消息接收
//        isBackground = false;
//        if (isBackground){
//            V2TIMManager.getOfflinePushManager().doBackground(0, new V2TIMCallback() {
//                @Override
//                public void onSuccess() {
//                    LogUtils.d("开启IM离线消息接收");
//                }
//
//                @Override
//                public void onError(int i, String s) {
//                    LogUtils.e("开启IM离线消息接收失败："+s+"  code:"+i);
//                }
//            });
//        }else {
//            V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
//                @Override
//                public void onSuccess() {
//                    LogUtils.d("关闭IM离线消息接收");
//                }
//
//                @Override
//                public void onError(int i, String s) {
//                    LogUtils.e("关闭IM离线消息接收失败："+s+"  code:"+i);
//                }
//            });
//        }
    }

    @Override
    public void setPlatformAccountData(List<String> platformAccountData) {
        this.platformAccountData.addAll(platformAccountData);
    }

    @Override
    public boolean isPlatformAccount(String accountId) {
        return platformAccountData.contains(accountId);
    }

    @Override
    public void setHasConversation(boolean conversation) {
        hasConversation = conversation;
    }

    @Override
    public boolean getHasConversation() {
        return hasConversation;
    }

    @Override
    public void deleteConversation(String userId) {
        TUIKit.deleteConversation(userId);
    }

}
