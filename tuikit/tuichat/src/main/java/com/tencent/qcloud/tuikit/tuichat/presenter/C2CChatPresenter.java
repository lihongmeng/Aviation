package com.tencent.qcloud.tuikit.tuichat.presenter;

import android.text.TextUtils;

import com.jxntv.base.Constant;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.bean.MessageInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.MessageReceiptInfo;
import com.tencent.qcloud.tuikit.tuichat.interfaces.C2CChatEventListener;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatLog;

import java.util.List;
import java.util.Map;

public class C2CChatPresenter extends ChatPresenter {
    private static final String TAG = C2CChatPresenter.class.getSimpleName();

    private ChatInfo chatInfo;

    private C2CChatEventListener chatEventListener;
    //第一次加载新数据
    private boolean isFirstLoadMessage = true;

    public C2CChatPresenter() {
        super();
        TUIChatLog.i(TAG, "C2CChatPresenter Init");
        initListener();
    }

    public void setRelationShip(Map<String, Boolean> map){
        if (map!=null){
            boolean isFollow = false;
            boolean isFollowMe = false;
            if (map.get(Constant.MAP_STRING.IS_FOLLOW)!=null){
                isFollow = map.get(Constant.MAP_STRING.IS_FOLLOW);
            }
            if(map.get(Constant.MAP_STRING.IS_FOLLOW_ME)!=null){
                isFollowMe = map.get(Constant.MAP_STRING.IS_FOLLOW_ME);
            }
            if (isFollow){
                if (isFollowMe) {
                    relationShip = "互相关注";
                }else {
                    relationShip = "单方面关注";
                }
            }else if (isFollowMe){
                relationShip = "被关注";
            }
        }
    }

    public void initListener() {
        chatEventListener = new C2CChatEventListener() {
            @Override
            public void onReadReport(List<MessageReceiptInfo> receiptList) {
                C2CChatPresenter.this.onReadReport(receiptList);
            }

            @Override
            public void exitC2CChat(String chatId) {
                C2CChatPresenter.this.onExitChat(chatId);
            }

            @Override
            public void handleRevoke(String msgId) {
                C2CChatPresenter.this.handleRevoke(msgId);
            }

            @Override
            public void onRecvNewMessage(MessageInfo message) {
                if (chatInfo == null || !TextUtils.equals(message.getUserId(), chatInfo.getId())) {
                    TUIChatLog.i(TAG, "receive a new message , not belong to current chat.");
                } else {
                    C2CChatPresenter.this.onRecvNewMessage(message);
                }
            }

            @Override
            public void onFriendNameChanged(String userId, String newName) {
                if (chatInfo == null || !TextUtils.equals(userId, chatInfo.getId())) {
                    return;
                }
                C2CChatPresenter.this.onFriendInfoChanged();
            }
        };
        TUIChatService.getInstance().setChatEventListener(chatEventListener);
    }

    /**
     * 拉取消息
     * @param type 向前，向后或者前后同时拉取
     * @param lastMessageInfo 拉取消息的起始点
     */
    @Override
    public void loadMessage(int type, MessageInfo lastMessageInfo) {
        if (chatInfo == null || isLoading) {
            return;
        }
        isLoading = true;

        String chatId = chatInfo.getId();
        // 向前拉取更旧的消息
        if (type == TUIChatConstants.GET_MESSAGE_FORWARD) {
            provider.loadC2CMessage(chatId, MSG_PAGE_COUNT, lastMessageInfo, new IUIKitCallback<List<MessageInfo>>() {

                @Override
                public void onSuccess(List<MessageInfo> data) {
                    TUIChatLog.i(TAG, "load c2c message success " + data.size());
                    if (lastMessageInfo == null) {
                        isHaveMoreNewMessage = false;
                    }
                    onMessageLoadCompleted(data, type);
                    if (isFirstLoadMessage && data.size()>0){
                        isFirstLoadMessage = false;
                        MessageInfo messageInfo = null;
                        for (int i=data.size()-1;i<data.size();i--){
                            if (i < 0){
                                break;
                            }
                            if (!data.get(i).isSelf()){
                                messageInfo = data.get(i);
                                break;
                            }
                        }
                        if (messageInfo!=null) {
                            GVideoSensorDataManager.getInstance().openSystemMessage(messageInfo.getExtra().toString(),
                                    messageInfo.getId(), getMessageStatType(messageInfo));
                        }
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    TUIChatLog.e(TAG, "load c2c message failed " + errCode + "  " + errMsg);
                }
            });
        } else { // 向后拉更新的消息 或者 前后同时拉消息
            loadHistoryMessageList(chatId, false, type, MSG_PAGE_COUNT, lastMessageInfo);
        }
    }

    // 加载消息成功之后会调用此方法
    @Override
    protected void onMessageLoadCompleted(List<MessageInfo> data, int getType) {
        c2cReadReport(chatInfo.getId());
        processLoadedMessage(data, getType);
    }

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }

    @Override
    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public void onFriendNameChanged(String newName) {
        if (chatNotifyHandler != null) {
            chatNotifyHandler.onFriendNameChanged(newName);
        }
    }

    public void onFriendInfoChanged() {
        provider.getFriendName(chatInfo.getId(), new IUIKitCallback<String[]>() {
            @Override
            public void onSuccess(String[] data) {
                String displayName = chatInfo.getId();
                if (!TextUtils.isEmpty(data[0])) {
                    displayName = data[0];
                } else if (!TextUtils.isEmpty(data[1])) {
                    displayName = data[1];
                }
                onFriendNameChanged(displayName);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }
}
