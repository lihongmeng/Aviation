package com.hzlz.aviation.kernel.base.plugin;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.model.ImConfigModel;
import com.hzlz.aviation.library.ioc.Plugin;

import java.util.List;

public interface ChatIMPlugin extends Plugin {

    void login(ImConfigModel configModel);

    void logout();

    Class<? extends Fragment> getConversationFragmentClass();

    void updateProfile();

    /**
     * 私聊
     *
     * @param userId   聊天对象的用户id
     * @param nickName 聊天对象的用户昵称
     */
    void startC2CChatActivity(String userId, String nickName);

    /**
     * 群聊
     *
     * @param groupId   群id
     * @param groupName 群名称
     */
    void startGroupChatActivity(String groupId, String groupName);

    /**
     * 获取未读消息数量
     */
    void getUnreadMessageCount(TotalUnreadMessageCountListener listener);

    /**
     * 是否有未读聊天消息
     */
    boolean getHasUnreadMessage();

    /**
     * 设置离线消息推送token
     */
    void setThirdPushToken(String token);

    /**
     * 解析离线消息
     */
    void dispatcherPushMessage(Intent intent);

    /**
     * 是否开启后台离线消息接收，开启后会在通知栏收到聊天消息
     * @param isBackground  true 开启 ， false  不开窍
     */
    void setOfflineMessageDoBackground(boolean isBackground);

    /**
     * 设置官方账号
     */
    void setPlatformAccountData(List<String> platformAccountData);

    /**
     * 是否是官方账号
     *
     * @param accountId 用户id
     */
    boolean isPlatformAccount(String accountId);


    /**
     * 是否有会话
     *
     */
    void setHasConversation(boolean hasConversation);

    /**
     * 是否有会话
     *
     */
    boolean getHasConversation();

    /**
     * 删除会话
     *
     * @param userId 用户id
     */
    void deleteConversation(String userId);

    interface TotalUnreadMessageCountListener {
        void onSuccess(long count);

        void onError();
    }

}
