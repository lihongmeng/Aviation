package com.tencent.qcloud.tuikit.tuichat.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.tencent.imsdk.BaseConstants;
import com.tencent.qcloud.tuicore.component.interfaces.IUIKitCallback;
import com.tencent.qcloud.tuicore.util.ThreadHelper;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;
import com.tencent.qcloud.tuikit.tuichat.TUIChatService;
import com.tencent.qcloud.tuikit.tuichat.bean.GroupApplyInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.GroupInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.MessageInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.MessageReceiptInfo;
import com.tencent.qcloud.tuikit.tuichat.bean.OfflineMessageBean;
import com.tencent.qcloud.tuikit.tuichat.bean.OfflineMessageContainerBean;
import com.tencent.qcloud.tuikit.tuichat.bean.OfflinePushInfo;
import com.tencent.qcloud.tuikit.tuichat.config.TUIChatConfigs;
import com.tencent.qcloud.tuikit.tuichat.interfaces.IBaseMessageSender;
import com.tencent.qcloud.tuikit.tuichat.bean.ChatInfo;
import com.tencent.qcloud.tuikit.tuichat.model.ChatProvider;
import com.tencent.qcloud.tuikit.tuichat.ui.interfaces.IMessageAdapter;
import com.tencent.qcloud.tuikit.tuichat.ui.view.message.MessageRecyclerView;
import com.tencent.qcloud.tuikit.tuichat.util.ChatMessageInfoUtil;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatLog;
import com.tencent.qcloud.tuikit.tuichat.util.TUIChatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ChatPresenter {
    private static final String TAG = ChatPresenter.class.getSimpleName();
    // 逐条转发 Group 消息发送消息的时间间隔
    private static final int FORWARD_GROUP_INTERVAL = 90; // 单位： 毫秒
    // 逐条转发 C2C 消息发送消息的时间间隔
    private static final int FORWARD_C2C_INTERVAL = 50; // 单位： 毫秒
    // 消息已读上报时间间隔
    private static final int READ_REPORT_INTERVAL = 1000; // 单位： 毫秒

    protected static final int MSG_PAGE_COUNT = 20;

    //关注关系，用于埋点
    protected String relationShip = "";

    protected final ChatProvider provider;
    protected List<MessageInfo> loadedMessageInfoList = new ArrayList<>();

    protected IMessageAdapter messageListAdapter;

    protected ChatNotifyHandler chatNotifyHandler;

    private long lastReadReportTime = 0L;
    private boolean canReadReport = true;
    private final MessageReadReportHandler readReportHandler = new MessageReadReportHandler();

    // 当前聊天界面是否显示，用来判断接收到消息是否设置已读
    private boolean isChatFragmentShow = false;

    // 用来定位消息搜索时消息的位置
    private MessageInfo locateMessage;

//    // 对其他模块暴露的消息发送接口
    private IBaseMessageSender baseMessageSender;

    // 标识是否有 更新的 消息没有更新下来
    protected boolean isHaveMoreNewMessage = false;

    protected boolean isLoading = false;

    public ChatPresenter() {
        TUIChatLog.i(TAG, "ChatPresenter Init");

        provider = new ChatProvider();

        baseMessageSender = new IBaseMessageSender() {

            @Override
            public void sendMessage(MessageInfo messageInfo, String receiver, boolean isGroup) {
                ChatPresenter.this.sendMessage(messageInfo, receiver, isGroup);
            }
        };
        TUIChatService.getInstance().setMessageSender(baseMessageSender);
    }

    public abstract void loadMessage(int type, MessageInfo locateMessage);

    public void clearMessageAndReLoad() {
        if (!isHaveMoreNewMessage) {
            messageListAdapter.onScrollToEnd();
            return;
        }
        loadedMessageInfoList.clear();
        messageListAdapter.onViewNeedRefresh(MessageRecyclerView.DATA_CHANGE_TYPE_REFRESH, 0);
        loadMessage(TUIChatConstants.GET_MESSAGE_FORWARD, null);
    }

    public abstract ChatInfo getChatInfo();

    protected void c2cReadReport(String userId) {
        provider.c2cReadReport(userId);
    };

    protected void groupReadReport(String groupId) {
        provider.groupReadReport(groupId);
    };

    public void setMessageListAdapter(IMessageAdapter messageListAdapter) {
        this.messageListAdapter = messageListAdapter;
    }

    protected void loadHistoryMessageList(String chatId, boolean isGroup, int getType, int loadCount,
                                          MessageInfo locateMessageInfo) {
        // 如果是前后同时拉取消息，需要拉取两次，第一次向后拉取，第二次向前拉取
        // 例如现在有消息 1,2,3,4,5,6,7  locateMessageInfo 是 4
        // 如果 getType 为 GET_MESSAGE_FORWARD， 就会拉取到消息 1,2,3
        // 如果 getType 为 GET_MESSAGE_BACKWARD， 就会拉取到消息 5,6,7
        // 如果 getType 为 GET_MESSAGE_TWO_WAY， 就会拉取到消息 1,2,3,5,6,7 ， 4 要手动加上
        int firstGetType;
        int secondGetType = TUIChatConstants.GET_MESSAGE_FORWARD;
        if (getType == TUIChatConstants.GET_MESSAGE_TWO_WAY) {
            firstGetType = TUIChatConstants.GET_MESSAGE_BACKWARD;
        } else {
            firstGetType = getType;
        }
        provider.loadHistoryMessageList(chatId, isGroup, loadCount, locateMessageInfo, firstGetType, new IUIKitCallback<List<MessageInfo>>() {
            @Override
            public void onSuccess(List<MessageInfo> firstData) {
                if (firstGetType == TUIChatConstants.GET_MESSAGE_BACKWARD) {
                    if (firstData.size() >= loadCount) {
                        isHaveMoreNewMessage = true;
                    } else {
                        isHaveMoreNewMessage = false;
                    }
                }

                // 如果是前后同时拉取就再拉一次消息，两次都拉取完成后再返回
                if (getType == TUIChatConstants.GET_MESSAGE_TWO_WAY) {
                    // 拉取历史消息的时候不会把 lastMsg 返回，需要手动添加上
                    firstData.add(0, locateMessageInfo);
                    ChatPresenter.this.locateMessage = locateMessageInfo;
                    provider.loadHistoryMessageList(chatId, isGroup, loadCount, locateMessageInfo, secondGetType, new IUIKitCallback<List<MessageInfo>>() {
                        @Override
                        public void onSuccess(List<MessageInfo> secondData) {
                            Collections.reverse(firstData);
                            secondData.addAll(0, firstData);
                            onMessageLoadCompleted(secondData, TUIChatConstants.GET_MESSAGE_TWO_WAY);
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {

                        }
                    });
                } else { // 拉取一次直接返回
                    onMessageLoadCompleted(firstData, firstGetType);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    protected abstract void onMessageLoadCompleted(List<MessageInfo> data, int getType);

    protected void processLoadedMessage(List<MessageInfo> data, int type) {
        boolean isForward = type == TUIChatConstants.GET_MESSAGE_FORWARD;
        boolean isTwoWay = type == TUIChatConstants.GET_MESSAGE_TWO_WAY;
        if (isForward || isTwoWay) {
            Collections.reverse(data);
        }
        List<MessageInfo> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            MessageInfo info = data.get(i);
            if (checkExist(info)) {
                continue;
            }
            list.add(info);
        }
        if (isForward || isTwoWay) {
            loadedMessageInfoList.addAll(0, list);
            if (isForward) {
                updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_ADD_FRONT, list.size());
            } else {
                updateAdapter(MessageRecyclerView.DATA_CHANGE_SCROLL_TO_POSITION, locateMessage);
            }
        } else {
            loadedMessageInfoList.addAll(list);
            updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_ADD_BACK, list.size());
        }

        for (MessageInfo messageInfo : data) {
            if (messageInfo.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
                sendMessage(messageInfo, true, null);
            }
        }
        isLoading = false;
    }

    protected void addMessageInfo(MessageInfo messageInfo) {
        if (messageInfo == null) {
            return;
        }
        if (checkExist(messageInfo)) {
            return;
        }
        loadedMessageInfoList.add(messageInfo);
        updateAdapter(MessageRecyclerView.DATA_CHANGE_NEW_MESSAGE, 1);

    }

    protected void onRecvNewMessage(MessageInfo msg) {
        TUIChatLog.i(TAG, "onRecvNewMessage msgID:" + msg.getId());
        int elemType = msg.getMsgType();
        if (elemType == MessageInfo.MSG_TYPE_CUSTOM) {
            if (ChatMessageInfoUtil.isTyping(msg.getCustomElemData())) {
                notifyTyping();
                return;
            } else if (ChatMessageInfoUtil.isOnlineIgnored(msg)) {
                // 这类消息都是音视频通话邀请的在线消息，忽略
                TUIChatLog.i(TAG, "ignore online invitee message");
                return;
            }
        }

        if (!isHaveMoreNewMessage) {
            addMessage(msg);
        }
    }

    protected void addMessage(MessageInfo messageInfo) {
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "addMessage unSafetyCall");
            return;
        }
        if (messageInfo != null) {
            boolean isGroupMessage = false;
            String groupID = null;
            String userID = null;
            if (!TextUtils.isEmpty(messageInfo.getGroupId())) {
                isGroupMessage = true;
                groupID = messageInfo.getGroupId();
            } else if (!TextUtils.isEmpty(messageInfo.getUserId())) {
                userID = messageInfo.getUserId();
            } else {
                return;
            }
            if (isChatFragmentShow()) {
                messageInfo.setRead(true);
            }

            addMessageInfo(messageInfo);

            if (isChatFragmentShow()) {
                if (isGroupMessage) {
                    limitReadReport(groupID, true);
                } else {
                    limitReadReport(userID, false);
                }
            }
        }
    }

    public boolean isChatFragmentShow() {
        return isChatFragmentShow;
    }

    public void setChatFragmentShow(boolean isChatFragmentShow) {
        this.isChatFragmentShow = isChatFragmentShow;
    }

    private void notifyTyping() {
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "notifyTyping unSafetyCall");
            return;
        }
    }

    public void sendMessage(final MessageInfo messageInfo, String receiver, boolean isGroup) {
        if (!TextUtils.isEmpty(receiver)) {
            if (!TextUtils.equals(getChatInfo().getId(), receiver) || isGroup != TUIChatUtils.isGroupChat(getChatInfo().getType())) {
                return;
            }
        }
        sendMessage(messageInfo, false, null);
    }

    public void sendMessage(final MessageInfo message, boolean retry, final IUIKitCallback callBack) {
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "sendMessage unSafetyCall");
            return;
        }
        if (message == null || message.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
            return;
        }
        message.setSelf(true);
        message.setRead(true);
        assembleGroupMessage(message);

        String msgId = provider.sendMessage(message, getChatInfo(), new IUIKitCallback<MessageInfo>() {

            @Override
            public void onSuccess(MessageInfo data) {
                TUIChatLog.v(TAG, "sendMessage onSuccess:" + data.getTimMessage().getMsgID());
                if (!safetyCall()) {
                    TUIChatLog.w(TAG, "sendMessage unSafetyCall");
                    return;
                }
                message.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                if (message.getMsgType() == MessageInfo.MSG_TYPE_FILE) {
                    message.setDownloadStatus(MessageInfo.MSG_STATUS_DOWNLOADED);
                }
                if (callBack != null) {
                    callBack.onSuccess(data);
                }
                updateMessageInfo(message);
                if (!message.isGroup()){
                    GVideoSensorDataManager.getInstance().sendC2CMessage(getChatInfo().getId(),
                            getChatInfo().getChatName(),getMessageStatType(message),relationShip,null);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIChatLog.v(TAG, "sendMessage fail:" + errCode + "=" + errMsg);
                if (!safetyCall()) {
                    TUIChatLog.w(TAG, "sendMessage unSafetyCall");
                    return;
                }
                if (callBack != null) {
//                    callBack.onError(TAG, errCode, "");
                }
                message.setStatus(MessageInfo.MSG_STATUS_SEND_FAIL);
                updateMessageInfo(message);
                if (!message.isGroup()){
                    GVideoSensorDataManager.getInstance().sendC2CMessage(getChatInfo().getId(),
                            getChatInfo().getChatName(),getMessageStatType(message),relationShip,errMsg);
                }
            }
        });
        //消息先展示，通过状态来确认发送是否成功
        TUIChatLog.i(TAG, "sendMessage msgID:" + msgId);
        message.setId(msgId);
        if (message.getMsgType() < MessageInfo.MSG_TYPE_TIPS || message.getMsgType() > MessageInfo.MSG_STATUS_REVOKE) {
            message.setStatus(MessageInfo.MSG_STATUS_SENDING);
            if (retry) {
                resendMessageInfo(message);
            } else {
                addMessageInfo(message);
            }
        }
    }

    public String getMessageStatType(MessageInfo message){
        String msgType = "其他";
        if (message.getMsgType() == MessageInfo.MSG_TYPE_IMAGE){
            msgType = "图片";
        }else if (message.getMsgType() == MessageInfo.MSG_TYPE_TEXT){
            msgType = "文字";
        }else if (message.getMsgType() == MessageInfo.MSG_TYPE_VIDEO){
            msgType = "视频";
        }else if (message.getMsgType() == MessageInfo.MSG_TYPE_AUDIO){
            msgType = "语音";
        }
        return msgType;
    }

    private void updateMessageInfo(MessageInfo messageInfo) {
        for (int i = 0; i < loadedMessageInfoList.size(); i++) {
            if (loadedMessageInfoList.get(i).getId().equals(messageInfo.getId())) {
                loadedMessageInfoList.remove(i);
                loadedMessageInfoList.add(i, messageInfo);
                updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_UPDATE, i);
            }
        }
    }

    private void resendMessageInfo(MessageInfo messageInfo) {
        boolean found = false;
        for (int i = 0; i < loadedMessageInfoList.size(); i++) {
            if (loadedMessageInfoList.get(i).getId().equals(messageInfo.getId())) {
                loadedMessageInfoList.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            return;
        }
        addMessageInfo(messageInfo);
    }

    public void deleteMessage(final int position, MessageInfo messageInfo) {
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "deleteMessage unSafetyCall");
            return;
        }
        if (position >= loadedMessageInfoList.size()){
            TUIChatLog.w(TAG, "deleteMessage invalid position");
            return;
        }
        List<MessageInfo> msgs = new ArrayList<>();
        msgs.add(loadedMessageInfoList.get(position));

        provider.deleteMessages(msgs, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                loadedMessageInfoList.remove(position);
                updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_DELETE, position);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });

    }


    public void handleRevoke(String msgId) {
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "handleInvoke unSafetyCall");
            return;
        }
        TUIChatLog.i(TAG, "handleInvoke msgID = " + msgId);
        for (int i = 0; i < loadedMessageInfoList.size(); i++) {
            MessageInfo messageInfo = loadedMessageInfoList.get(i);
            // 一条包含多条元素的消息，撤回时，会把所有元素都撤回，所以下面的判断即使满足条件也不能return
            if (messageInfo.getId().equals(msgId)) {
                messageInfo.setMsgType(MessageInfo.MSG_STATUS_REVOKE);
                messageInfo.setStatus(MessageInfo.MSG_STATUS_REVOKE);
                updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_UPDATE, i);
            }
        }
    }

    public void onReadReport(List<MessageReceiptInfo> receiptList) {
        TUIChatLog.i(TAG, "onReadReport:" + receiptList.size());
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "onReadReport unSafetyCall");
            return;
        }
        if (receiptList.size() == 0) {
            return;
        }
        MessageReceiptInfo max = receiptList.get(0);
        for (MessageReceiptInfo msg : receiptList) {
            if (!TextUtils.equals(msg.getUserID(), getChatInfo().getId())) {
                continue;
            }
            if (max.getTimestamp() < msg.getTimestamp()) {
                max = msg;
            }
        }
        for (int i = 0; i < loadedMessageInfoList.size(); i++) {
            MessageInfo messageInfo = loadedMessageInfoList.get(i);
            if (messageInfo.getMsgTime() > max.getTimestamp()) {
                messageInfo.setPeerRead(false);
            } else if (messageInfo.isPeerRead()) {
                // do nothing
            } else {
                messageInfo.setPeerRead(true);
                updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_UPDATE, i);
            }
        }
    }

    protected boolean safetyCall() {
        if (getChatInfo() == null) {
            return false;
        }
        return true;
    }

    public void markMessageAsRead(ChatInfo chatInfo) {
        if (chatInfo == null) {
            TUIChatLog.i(TAG, "markMessageAsRead() chatInfo is null");
            return;
        }

        boolean isGroup;
        if (chatInfo.getType() == ChatInfo.TYPE_C2C) {
            isGroup = false;
        } else {
            isGroup = true;
        }
        String chatId = chatInfo.getId();
        if (isGroup) {
            groupReadReport(chatId);
        } else {
            c2cReadReport(chatId);
        }
    }

    /**
     * 收到消息上报已读加频率限制
     * @param chatId 如果是 C2C 消息， chatId 是 userId, 如果是 Group 消息 chatId 是 groupId
     * @param isGroup 是否为 Group 消息
     */
    private void limitReadReport(final String chatId, boolean isGroup) {
        final long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastReadReportTime;
        if (timeDifference >= READ_REPORT_INTERVAL) {
            readReport(chatId, isGroup);
            lastReadReportTime = currentTime;
        } else {
            if (!canReadReport) {
                TUIChatLog.d(TAG, "limitReadReport : Reporting , please wait.");
                return;
            }
            long delay = READ_REPORT_INTERVAL - timeDifference;
            TUIChatLog.d(TAG, "limitReadReport : Please retry after " + delay + " ms.");
            canReadReport = false;
            readReportHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    readReport(chatId, isGroup);
                    lastReadReportTime = System.currentTimeMillis();
                    canReadReport = true;
                }
            }, delay);
        }
    }

    private void readReport(String userId, boolean isGroup) {
        if (!isGroup) {
            TUIChatLog.i(TAG, "C2C message ReadReport userId is " + userId);
            c2cReadReport(userId);
        } else {
            TUIChatLog.i(TAG, "Group message ReadReport groupId is " + userId);
            groupReadReport(userId);
        }
    }

    protected void updateAdapter(int type, int value) {
        if (messageListAdapter != null) {
            messageListAdapter.onDataSourceChanged(loadedMessageInfoList);
            messageListAdapter.onViewNeedRefresh(type, value);
        }
    }

    protected void updateAdapter(int type, MessageInfo locateMessage) {
        if (messageListAdapter != null) {
            messageListAdapter.onDataSourceChanged(loadedMessageInfoList);
            messageListAdapter.onViewNeedRefresh(type, locateMessage);
        }
    }

    protected boolean checkExist(MessageInfo msg) {
        if (msg != null) {
            String msgId = msg.getId();
            for (int i = loadedMessageInfoList.size() - 1; i >= 0; i--) {
                if (loadedMessageInfoList.get(i).getId().equals(msgId)
                        && loadedMessageInfoList.get(i).getUniqueId() == msg.getUniqueId()
                        && TextUtils.equals(loadedMessageInfoList.get(i).getExtra().toString(), msg.getExtra().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void deleteMessageInfos(final List<MessageInfo> messageInfos) {
        if (!safetyCall() || messageInfos == null || messageInfos.isEmpty()) {
            TUIChatLog.w(TAG, "deleteMessages unSafetyCall");
            return;
        }

        provider.deleteMessages(messageInfos, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                for (int i = loadedMessageInfoList.size() -1; i >= 0 ; i--) {
                    for (int j = messageInfos.size() -1; j >= 0; j--) {
                        if (loadedMessageInfoList.get(i).getId().equals(messageInfos.get(j).getId())) {
                            loadedMessageInfoList.remove(i);
                            updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_DELETE, i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    public void deleteMessages(final List<Integer> positions) {
        if (!safetyCall() || positions == null || positions.isEmpty()) {
            TUIChatLog.w(TAG, "deleteMessages unSafetyCall");
            return;
        }
        List<MessageInfo> msgs = new ArrayList<>();
        for(int i = 0; i < positions.size(); i++) {
            msgs.add(loadedMessageInfoList.get(positions.get(i)));
        }

        provider.deleteMessages(msgs, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                for(int i = positions.size() -1 ; i >= 0; i--) {
                    loadedMessageInfoList.remove(positions.get(i));
                    updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_DELETE, i);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    public boolean checkFailedMessages(final List<Integer> positions){
        if (!safetyCall() || positions == null || positions.isEmpty()) {
            TUIChatLog.w(TAG, "checkFailedMessages unSafetyCall");
            return false;
        }

        boolean failed = false;
        for(int i = 0; i < positions.size(); i++) {
            MessageInfo messageInfo = loadedMessageInfoList.get(positions.get(i));
            if (provider.checkFailedMessageInfo(messageInfo)) {
                failed = true;
                break;
            }
        }

        return failed;
    }

    public boolean checkFailedMessageInfos(final List<MessageInfo> messageInfos){
        if (!safetyCall() || messageInfos == null || messageInfos.isEmpty()) {
            TUIChatLog.w(TAG, "checkFailedMessagesById unSafetyCall");
            return false;
        }

        boolean failed = false;
        for (int i = 0; i < messageInfos.size(); i++) {
            if (provider.checkFailedMessageInfo(messageInfos.get(i))) {
                failed = true;
                break;
            }
        }

        return failed;
    }

    public List<MessageInfo> getSelectPositionMessage(final List<Integer> positions){
        if (!safetyCall() || positions == null || positions.isEmpty()) {
            TUIChatLog.w(TAG, "getSelectPositionMessage unSafetyCall");
            return null;
        }

        List<MessageInfo> msgs = new ArrayList<>();
        for(int i = 0; i < positions.size(); i++) {
            if (positions.get(i) < loadedMessageInfoList.size()) {
                msgs.add(loadedMessageInfoList.get(positions.get(i)));
            } else {
                TUIChatLog.d(TAG, "mCurrentProvider not include SelectPosition ");
            }
        }
        return msgs;
    }

    public List<MessageInfo> getSelectPositionMessageById(final List<String> msgIds){
        if (!safetyCall() || msgIds == null || msgIds.isEmpty()) {
            TUIChatLog.w(TAG, "getSelectPositionMessageById unSafetyCall");
            return null;
        }

        List<MessageInfo> messageInfos = loadedMessageInfoList;
        if (messageInfos == null || messageInfos.size() <= 0) {
            return null;
        }

        final List<MessageInfo> msgSelectedMsgInfos = new ArrayList<>();
        for (int i = 0; i < msgIds.size(); i++) {
            for (int j = 0; j < messageInfos.size(); j++) {
                if (msgIds.get(i).equals(messageInfos.get(j).getId())) {
                    msgSelectedMsgInfos.add(messageInfos.get(j));
                    break;
                }
            }
        }

        return msgSelectedMsgInfos;
    }

    public void revokeMessage(final int position, final MessageInfo messageInfo) {
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "revokeMessage unSafetyCall");
            return;
        }
        provider.revokeMessage(messageInfo, new IUIKitCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                if (!safetyCall()) {
                    TUIChatLog.w(TAG, "revokeMessage unSafetyCall");
                    return;
                }
                updateMessageRevoked(messageInfo.getId());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                if (errCode == ChatProvider.ERR_REVOKE_TIME_LIMIT_EXCEED ||
                        errCode == BaseConstants.ERR_SVR_MSG_REVOKE_TIME_LIMIT) {
                    ToastUtil.toastLongMessage(TUIChatService.getAppContext().getString(R.string.send_two_mins));
                } else {
                    ToastUtil.toastLongMessage(TUIChatService.getAppContext().getString(R.string.revoke_fail) + errCode + "=" + errMsg);
                }
            }
        });
    }

    public boolean updateMessageRevoked(String msgId) {
        for (int i = 0; i < loadedMessageInfoList.size(); i++) {
            MessageInfo messageInfo = loadedMessageInfoList.get(i);
            // 一条包含多条元素的消息，撤回时，会把所有元素都撤回，所以下面的判断即使满足条件也不能return
            if (messageInfo.getId().equals(msgId)) {
                messageInfo.setMsgType(MessageInfo.MSG_STATUS_REVOKE);
                messageInfo.setStatus(MessageInfo.MSG_STATUS_REVOKE);
                updateAdapter(MessageRecyclerView.DATA_CHANGE_TYPE_UPDATE, i);
            }
        }
        return false;
    }

    public void forwardMessage(List<MessageInfo> msgInfos, boolean isGroup, String id, String offlineTitle, int forwardMode, boolean selfConversation, boolean retry, final IUIKitCallback callBack) {
        if (!safetyCall()) {
            TUIChatLog.w(TAG, "sendMessage unSafetyCall");
            return;
        }

        if (forwardMode == TUIChatConstants.FORWARD_MODE_ONE_BY_ONE) {
            forwardMessageOneByOne(msgInfos, isGroup, id, offlineTitle, selfConversation, retry, callBack);
        } else if (forwardMode == TUIChatConstants.FORWARD_MODE_MERGE) {
            forwardMessageMerge(msgInfos, isGroup, id, offlineTitle, selfConversation, retry, callBack);
        } else {
            TUIChatLog.d(TAG, "invalid forwardMode");
        }
    }

    public void forwardMessageOneByOne(final List<MessageInfo> msgInfos, final boolean isGroup, final String id, final String offlineTitle, final boolean selfConversation, final boolean retry, final IUIKitCallback callBack) {
        if (msgInfos == null || msgInfos.isEmpty()){
            return;
        }

        Runnable forwardMessageRunnable = new Runnable() {
            @Override
            public void run() {
                int timeInterval = isGroup ? FORWARD_GROUP_INTERVAL : FORWARD_C2C_INTERVAL;
                for(int j = 0; j < msgInfos.size(); j++){
                    MessageInfo info = msgInfos.get(j);
                    MessageInfo msgInfo = ChatMessageInfoUtil.buildForwardMessage(info.getTimMessage());

                    if (selfConversation) {
                        sendMessage(msgInfo, false, callBack);
                        try {
                            Thread.sleep(timeInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    if (msgInfo == null || msgInfo.getStatus() == MessageInfo.MSG_STATUS_SENDING) {
                        continue;
                    }
                    msgInfo.setSelf(true);
                    msgInfo.setRead(true);
                    assembleGroupMessage(msgInfo);

                    OfflineMessageContainerBean containerBean = new OfflineMessageContainerBean();
                    OfflineMessageBean entity = new OfflineMessageBean();
                    entity.content = msgInfo.getExtra().toString();
                    entity.sender = msgInfo.getFromUser();
                    entity.nickname = TUIChatConfigs.getConfigs().getGeneralConfig().getUserNickname();
                    entity.faceUrl = TUIChatConfigs.getConfigs().getGeneralConfig().getUserFaceUrl();
                    containerBean.entity = entity;

                    if (isGroup) {
                        entity.chatType = ChatInfo.TYPE_GROUP;
                        entity.sender = id;
                    }

                    OfflinePushInfo offlinePushInfo = new OfflinePushInfo();
                    offlinePushInfo.setExtension(new Gson().toJson(containerBean).getBytes());
                    offlinePushInfo.setDescription(offlineTitle);
                    // OPPO必须设置ChannelID才可以收到推送消息，这个channelID需要和控制台一致
                    offlinePushInfo.setAndroidOPPOChannelID("tuikit");

                    forwardMessageInternal(msgInfo, isGroup, id, offlinePushInfo, retry, callBack);
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread forwardThread = new Thread(forwardMessageRunnable);
        forwardThread.setName("ForwardMessageThread");
        ThreadHelper.INST.execute(forwardThread);
    }

    protected void assembleGroupMessage(MessageInfo message) {
    }

    public void forwardMessageMerge(List<MessageInfo> msgInfos, boolean isGroup, String id, String offlineTitle, boolean selfConversation, boolean retry, final IUIKitCallback callBack) {
        if (msgInfos == null || msgInfos.isEmpty()){
            return;
        }

        Context context = TUIChatService.getAppContext();
        if (context == null) {
            TUIChatLog.d(TAG, "context == null");
            return;
        }
        //abstractList
        List<String> abstractList = new ArrayList<>();
        for(int j = 0; j < msgInfos.size() && j < 3; j++){
            MessageInfo messageInfo = msgInfos.get(j);
            int type = messageInfo.getMessageInfoElemType();
            String userid = ChatMessageInfoUtil.getDisplayName(messageInfo.getTimMessage());
            if (type == MessageInfo.MSG_TYPE_CUSTOM) {
                abstractList.add(userid + ":" + messageInfo.getExtra());
            } else if (type == MessageInfo.MSG_TYPE_TIPS) {
            } else if (type == MessageInfo.MSG_TYPE_TEXT) {
                abstractList.add(userid + ":" + messageInfo.getExtra());
            } else if (type == MessageInfo.MSG_TYPE_CUSTOM_FACE) {
                abstractList.add(userid + ":" + context.getString(R.string.custom_emoji));
            } else if (type == MessageInfo.MSG_TYPE_AUDIO) {
                abstractList.add(userid + ":" + context.getString(R.string.audio_extra));
            } else if (type == MessageInfo.MSG_TYPE_IMAGE) {
                abstractList.add(userid + ":" + context.getString(R.string.picture_extra));
            } else if (type == MessageInfo.MSG_TYPE_VIDEO) {
                abstractList.add(userid + ":" + context.getString(R.string.video_extra));
            } else if (type == MessageInfo.MSG_TYPE_FILE) {
                abstractList.add(userid + ":" + context.getString(R.string.file_extra));
            } else if (type == MessageInfo.MSG_TYPE_MERGE) {
                // 合并转发消息
                abstractList.add(userid + ":" + context.getString(R.string.forward_extra));
            }
        }

        //createMergerMessage
        MessageInfo msgInfo = ChatMessageInfoUtil.buildMergeMessage(msgInfos, offlineTitle, abstractList,
                TUIChatService.getAppContext().getString(R.string.forward_compatible_text));

        if (selfConversation) {
            sendMessage(msgInfo, false, callBack);
            return;
        }

        msgInfo.setSelf(true);
        msgInfo.setRead(true);
        assembleGroupMessage(msgInfo);

        OfflineMessageContainerBean containerBean = new OfflineMessageContainerBean();
        OfflineMessageBean entity = new OfflineMessageBean();
        entity.content = msgInfo.getExtra().toString();
        entity.sender = msgInfo.getFromUser();
        entity.nickname = TUIChatConfigs.getConfigs().getGeneralConfig().getUserNickname();
        entity.faceUrl = TUIChatConfigs.getConfigs().getGeneralConfig().getUserFaceUrl();
        containerBean.entity = entity;

        if (isGroup) {
            entity.chatType = ChatInfo.TYPE_GROUP;
            entity.sender = id;
        }

        OfflinePushInfo offlinePushInfo = new OfflinePushInfo();
        offlinePushInfo.setExtension(new Gson().toJson(containerBean).getBytes());
        offlinePushInfo.setDescription(offlineTitle);
        // OPPO必须设置ChannelID才可以收到推送消息，这个channelID需要和控制台一致
        offlinePushInfo.setAndroidOPPOChannelID("tuikit");

        forwardMessageInternal(msgInfo, isGroup, id, offlinePushInfo, retry, callBack);
    }

    public void forwardMessageInternal(final MessageInfo message, boolean isGroup, String id, OfflinePushInfo offlinePushInfo, boolean retry, final IUIKitCallback callBack) {
        if (message == null) {
            TUIChatLog.e(TAG, "forwardMessageInternal null message!");
            return;
        }

        String msgId = provider.sendMessage(message, isGroup, id, offlinePushInfo, new IUIKitCallback<MessageInfo>() {

            @Override
            public void onSuccess(MessageInfo data) {
                if (!safetyCall()) {
                    TUIChatLog.w(TAG, "sendMessage unSafetyCall");
                    return;
                }

                if (callBack != null) {
                    callBack.onSuccess(data);
                }

                message.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                message.setMsgTime(data.getMsgTime());
                updateMessageInfo(message);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIChatLog.v(TAG, "sendMessage fail:" + errCode + "=" + errMsg);
                if (!safetyCall()) {
                    TUIChatLog.w(TAG, "sendMessage unSafetyCall");
                    return;
                }
                if (callBack != null) {
                    callBack.onError(TAG, errCode, errMsg);
                }
                message.setStatus(MessageInfo.MSG_STATUS_SEND_FAIL);
                updateMessageInfo(message);
            }
        });
        //消息先展示，通过状态来确认发送是否成功
        TUIChatLog.i(TAG, "sendMessage msgID:" + msgId);
        message.setId(msgId);
        if (message.getMsgType() < MessageInfo.MSG_TYPE_TIPS) {
            message.setStatus(MessageInfo.MSG_STATUS_SENDING);
            if (retry) {
                resendMessageInfo(message);
            } else {
                addMessage(message);
            }
        }
    }

    public void loadApplyList(IUIKitCallback<List<GroupApplyInfo>> callBack) {
        provider.loadApplyInfo(new IUIKitCallback<List<GroupApplyInfo>>() {
            @Override
            public void onSuccess(List<GroupApplyInfo> data) {
                if (!(getChatInfo() instanceof GroupInfo)) {
                    return;
                }
                String groupId = getChatInfo().getId();
                List<GroupApplyInfo> applyInfos = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    GroupApplyInfo applyInfo = data.get(i);
                    if (groupId.equals(applyInfo.getGroupApplication().getGroupID())
                            && !applyInfo.isStatusHandled()) {
                        applyInfos.add(applyInfo);
                    }
                }
                TUIChatUtils.callbackOnSuccess(callBack, applyInfos);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                TUIChatUtils.callbackOnError(callBack, module, errCode, errMsg);
            }
        });

    }

    public void setDraft(String draft) {
        ChatInfo chatInfo = getChatInfo();
        if (chatInfo == null) {
            return;
        }
        String conversationId = TUIChatUtils.getConversationIdByUserId(chatInfo.getId(), TUIChatUtils.isGroupChat(chatInfo.getType()));
        provider.setDraft(conversationId, draft);
    }

    public void getConversationLastMessage(String conversationId, IUIKitCallback<MessageInfo> callback) {
        provider.getConversationLastMessage(conversationId, callback);
    }

    public void setChatNotifyHandler(ChatNotifyHandler chatNotifyHandler) {
        this.chatNotifyHandler = chatNotifyHandler;
    }

    public void onExitChat(String chatId) {
        if (chatNotifyHandler != null) {
            chatNotifyHandler.onExitChat(chatId);
        }
    }

    static class MessageReadReportHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public interface TypingListener {
        void onTyping();
    }


    public interface ChatNotifyHandler {

        default void onGroupForceExit() {}

        default void onGroupNameChanged(String newName) {}

        default void onFriendNameChanged(String newName) {}

        default void onApplied(int size) {}

        void onExitChat(String chatId);
    }
}
