package com.hzlz.aviation.feature.live.liveroom.roomutil.im;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.library.util.LogUtils;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMGroupListener;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.imsdk.v2.V2TIMSDKListener;
import com.tencent.imsdk.v2.V2TIMSimpleMsgListener;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.imsdk.v2.V2TIMUserInfo;
import com.tencent.imsdk.v2.V2TIMValueCallback;

import java.util.FormatFlagsConversionMismatchException;
import java.util.List;


/**
 * Created by jac on 2017/11/4.
 * Copyright © 2013-2017 Tencent Cloud. All Rights Reserved.
 * IM消息
 */

public class IMMessageMgr {

    private Context mContext;
    private Handler mHandler;

    private static boolean mConnectSuccess = false;
    private boolean mLoginSuccess = false;

    private String mSelfUserID;
    private String mSelfUserSig;
    private String mGroupID;

    private V2TIMSDKConfig mTIMSdkConfig;
    private IMMessageLoginCallback mIMLoginListener;
    private IMMessageCallback mMessageListener;

    /**
     * 函数级公共Callback定义
     */
    public interface Callback {
        void onError(int code, String errInfo);

        void onSuccess(Object... args);
    }

    /**
     * 模块回调Listener定义
     */
    public interface IMMessageListener {
        /**
         * IM连接成功
         */
        void onConnected();

        /**
         * IM断开连接
         */
        void onDisconnected();

        /**
         * IM群组里推流者成员变化通知
         */
        void onPusherChanged();

        /**
         * 收到群文本消息
         */
        void onGroupTextMessage(String groupID, String userid, String userName, String headPic, String message);

        /**
         * 收到自定义的群消息
         */
        void onGroupCustomMessage(String groupID, String userid, String message);

        /**
         * 收到自定义的C2C消息
         */
        void onC2CCustomMessage(String sendID, String cmd, String message);

        /**
         * IM群组销毁回调
         */
        void onGroupDestroyed(final String groupID);

        /**
         * 日志回调
         */
        void onDebugLog(String log);

        /**
         * 用户进群通知
         *
         * @param groupID 群标识
         * @param memberInfoList   进群用户信息
         */
        void onGroupMemberEnter(String groupID, List<V2TIMGroupMemberInfo> memberInfoList);

        /**
         * 用户退群通知
         *
         * @param groupID 群标识
         * @param memberInfo   退群用户信息
         */
        void onGroupMemberExit(String groupID, V2TIMGroupMemberInfo memberInfo);

        /**
         * 用户被强制下线通知
         */
        void onForceOffline();
    }

    public IMMessageMgr(final Context context) {
        this.mContext = context.getApplicationContext();
        this.mHandler = new Handler(this.mContext.getMainLooper());
        this.mMessageListener = new IMMessageCallback(null);
    }

    /**
     * 设置回调
     *
     * @param listener
     */
    public void setIMMessageListener(IMMessageListener listener) {
        this.mMessageListener.setListener(listener);
    }

    /**
     * 初始化
     *
     * @param userID   用户ID
     * @param userSig  签名
     * @param appID    appID
     * @param callback
     */
    public void initialize(final String userID, final String userSig, final int appID, final Callback callback) {
        if (userID == null || userSig == null) {
            mMessageListener.onDebugLog("参数错误，请检查 UserID， userSig 是否为空！");
            if (callback != null) {
                callback.onError(-1, "参数错误");
            }
            return;
        }

        this.mSelfUserID = userID;
        this.mSelfUserSig = userSig;

        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {

                V2TIMManager.getInstance().addSimpleMsgListener(msgListener);
                V2TIMManager.getInstance().addGroupListener(groupListener);
                V2TIMManager.getInstance().addIMSDKListener(mIMConnListener);

                if (V2TIMManager.getInstance().initSDK(mContext, appID, mTIMSdkConfig)) {
                    login(new Callback() {
                        @Override
                        public void onError(int code, String errInfo) {
                            printDebugLog("login failed: %s  (errorCode：%d)", errInfo, code);
                            mLoginSuccess = false;
                            callback.onError(code, "IM登录失败");
                        }

                        @Override
                        public void onSuccess(Object... args) {
                            printDebugLog("login success");
                            mLoginSuccess = true;
                            mConnectSuccess = true;
                            callback.onSuccess();
                        }
                    });
                } else {
                    printDebugLog("init failed");
                    callback.onError(-1, "IM初始化失败");
                }
            }
        });
    }

    public void runOnHandlerThread(Runnable runnable) {
        Handler handler = mHandler;
        if (handler != null) {
            handler.post(runnable);
        } else {
            LogUtils.d("runOnHandlerThread -> Handler == null");
        }
    }

    /**
     * 反初始化
     */
    public void unInitialize() {

        V2TIMManager.getInstance().removeSimpleMsgListener(msgListener);
        V2TIMManager.getInstance().addGroupListener(groupListener);
        V2TIMManager.getInstance().removeIMSDKListener(mIMConnListener);

        mContext = null;
        mHandler = null;

        if (mTIMSdkConfig != null) {
            mTIMSdkConfig = null;
        }

        if (mIMLoginListener != null) {
            mIMLoginListener.clean();
            mIMLoginListener = null;
        }
        if (mMessageListener != null) {
            mMessageListener.setListener(null);
        }

        logout(null);
    }

    /**
     * 加入IM群组
     *
     * @param groupId  群ID
     * @param callback
     */
    public void jionGroup(final String groupId, final Callback callback) {
        if (!mLoginSuccess) {
            mMessageListener.onDebugLog("[jionGroup] IM 没有初始化");
            if (callback != null) {
                callback.onError(-1, "IM 没有初始化");
            }
            return;
        }

        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {
                V2TIMManager.getInstance().joinGroup(groupId, "who care?", new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        printDebugLog("加入群 {%s} 成功", groupId);
                        mGroupID = groupId;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("加入群 {%s} 失败:%s  (errorCode：%d)", groupId, s, i );
                        switch (i){
                            case 10015:
                                s = "直播间不存在";
                                break;
                            case 10010:
                                s = "直播已结束";
                                break;
                            default:
                                s = "加入直播间失败";
                        }
                        callback.onError(i, s);
                    }
                });
            }
        });
    }

    /**
     * 退出IM群组
     *
     * @param groupId  群ID
     * @param callback
     */
    public void quitGroup(final String groupId, final Callback callback) {
        if (!mLoginSuccess) {
            mMessageListener.onDebugLog("[quitGroup] IM 没有初始化");
            if (callback != null) {
                callback.onError(-1, "IM 没有初始化");
            }
            return;
        }

        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {

                V2TIMManager.getInstance().quitGroup(groupId, new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        printDebugLog("退出群 {%s} 成功", groupId);
                        mGroupID = groupId;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        if (i == 10010) {
                            printDebugLog("群 {%s} 已经解散了", groupId);
                            onSuccess();
                        } else {
                            printDebugLog("退出群 {%s} 失败： %s (errorCode：%d)", groupId, s, i);
                            callback.onError(i, s);
                        }
                    }
                });
            }
        });
    }


    /**
     * 创建群组
     * @param groupId    群ID
     * @param groupType  群类型
     * @param groupName  群名称
     */
    public void createGroup(final String groupId, final String groupType, final String groupName, final Callback callback) {
        if (!mLoginSuccess) {
            mMessageListener.onDebugLog("IM 没有初始化");
            if (callback != null) {
                callback.onError(-1, "IM 没有初始化");
            }
            return;
        }
        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {
                V2TIMManager.getInstance().createGroup(groupType, groupId, groupName, new V2TIMValueCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        printDebugLog("创建群 {%s} 成功", groupId);
                        mGroupID = groupId;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("创建群 {%s} 失败：%s (errorCode：%d)", groupId, s, i);
                        if (i == 10036) {
                            String createRoomErrorMsg = "您当前使用的云通讯账号未开通音视频聊天室功能" +
                                    "，创建聊天室数量超过限额，请前往腾讯云官网开通【IM音视频聊天室】，地址：https://buy.cloud.tencent.com/avc";
                            printDebugLog(createRoomErrorMsg);
                        }
                        if (i == 10025) {
                            mGroupID = groupId;
                        }
                        callback.onError(i, s);
                    }
                });
            }
        });
    }

    /**
     * 销毁IM群组
     *
     * @param groupId  群ID
     */
    public void destroyGroup(final String groupId, final Callback callback) {
        if (!mLoginSuccess) {
            mMessageListener.onDebugLog("IM 没有初始化");
            if (callback != null) {
                callback.onError(-1, "IM 没有初始化");
            }
            return;
        }

        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {
                V2TIMManager.getInstance().dismissGroup(groupId, new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        printDebugLog("解散群 {%s} 成功", groupId);
                        mGroupID = groupId;
                        callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("解散群 {%s} 失败：%s (errorCode：%d)", groupId, s, i);
                        callback.onError(i, s);
                    }
                });
            }
        });
    }

    /**
     * 发送IM群文本消息
     *
     * @param userName 发送者用户名
     * @param headPic  发送者头像
     * @param text     文本内容
     */
    public void sendGroupTextMessage(final @NonNull String userName, final @NonNull String headPic, final @NonNull String text, final Callback callback) {
        if (!mLoginSuccess) {
            mMessageListener.onDebugLog("[sendGroupTextMessage] IM 没有初始化");
            if (callback != null)
                callback.onError(-1, "IM 没有初始化");
            return;
        }

        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {
                String strCmdMsg ="";
                try {
                    CommonJson<UserInfo> txtHeadMsg = new CommonJson<UserInfo>();
                    txtHeadMsg.cmd = "CustomTextMsg";
                    txtHeadMsg.data = new UserInfo();
                    txtHeadMsg.data.nickName = userName;
                    txtHeadMsg.data.headPic = headPic;
                    txtHeadMsg.data.text = text;
                    strCmdMsg = new Gson().toJson(txtHeadMsg, new TypeToken<CommonJson<UserInfo>>() {}.getType());

                } catch (Exception e) {
                    printDebugLog("[sendGroupTextMessage] 发送群{%s}消息失败，组包异常", mGroupID);
                    if (callback != null) {
                        callback.onError(-1, "发送群消息失败");
                    }
                    return;
                }

                V2TIMManager.getInstance().sendGroupCustomMessage(strCmdMsg.getBytes(),
                        mGroupID, V2TIMMessage.V2TIM_PRIORITY_NORMAL, new V2TIMValueCallback<V2TIMMessage>() {
                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        printDebugLog("[sendGroupTextMessage] 发送群消息成功");
                        if (callback != null)
                            callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("[sendGroupTextMessage] 发送群{%s}消息失败: %s (errorCode：%d)", mGroupID, s, i);
                        if (callback != null)
                            callback.onError(i, s);
                    }
                });
            }
        });
    }

    /**
     * 发送自定义群消息
     *
     * @param content  自定义消息的内容
     * @param callback
     */
    public void sendGroupCustomMessage(final @NonNull String content, final Callback callback) {
        if (!mLoginSuccess) {
            mMessageListener.onDebugLog("[sendGroupCustomMessage] IM 没有初始化");
            if (callback != null)
                callback.onError(-1, "IM 没有初始化");
            return;
        }

        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {

                V2TIMManager.getInstance().sendGroupCustomMessage(content.getBytes(),mGroupID,V2TIMMessage.V2TIM_PRIORITY_NORMAL,
                        new V2TIMValueCallback<V2TIMMessage>() {
                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        if (callback != null)
                            callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("[sendGroupCustomMessage] 发送自定义群{%s}消息失败: %s (errorCode：%d)", mGroupID, s, i);
                        if (callback != null)
                            callback.onError(i, s);
                    }
                });
            }
        });
    }

    /**
     * 发送CC（端到端）自定义消息
     *
     * @param toUserID 接收者userID
     * @param content  自定义消息的内容
     * @param callback
     */
    public void sendC2CCustomMessage(final @NonNull String toUserID, final @NonNull String content, final Callback callback) {
        if (!mLoginSuccess) {
            mMessageListener.onDebugLog("[sendCustomMessage] IM 没有初始化");
            if (callback != null)
                callback.onError(-1, "IM 没有初始化");
            return;
        }

        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {
                V2TIMCustomElem customElem = new V2TIMCustomElem();
                try {
                    customElem.setData(content.getBytes());
                } catch (Exception e) {
                    printDebugLog("[sendCustomMessage] 发送CC{%s}消息失败，组包异常", toUserID);
                    if (callback != null) {
                        callback.onError(-1, "发送CC消息失败");
                    }
                    return;
                }

                V2TIMManager.getInstance().sendC2CCustomMessage(customElem.getData(),
                        toUserID, new V2TIMValueCallback<V2TIMMessage>() {
                    @Override
                    public void onSuccess(V2TIMMessage v2TIMMessage) {
                        printDebugLog("[sendCustomMessage] 发送CC消息成功");

                        if (callback != null)
                            callback.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s) {
                        printDebugLog("[sendCustomMessage] 发送CC{%s}消息失败: %s(%d)", toUserID, s, i);

                        if (callback != null)
                            callback.onError(i, s);
                    }
                });
            }
        });
    }

    public void getGroupMembers(final String groupId, final int maxSize, V2TIMValueCallback<V2TIMGroupMemberInfoResult> callback) {
        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {

                V2TIMManager.getGroupManager().getGroupMemberList(groupId, V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_COMMON,
                        0, callback);

            }
        });
    }

    public void setSelfProfile(final String nickname, final String faceURL) {
        if (nickname == null && faceURL == null) {
            return;
        }
        this.runOnHandlerThread(new Runnable() {
            @Override
            public void run() {
                V2TIMUserFullInfo v2TIMUserFullInfo = new V2TIMUserFullInfo();
                if (nickname != null) {
                    v2TIMUserFullInfo.setNickname(nickname);
                }
                if (faceURL != null) {
                    v2TIMUserFullInfo.setFaceUrl(faceURL);
                }

                V2TIMManager.getInstance().setSelfInfo(v2TIMUserFullInfo, new V2TIMCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtils.d("modifySelfProfile success");
                    }

                    @Override
                    public void onError(int code, String desc) {
                        LogUtils.e("modifySelfProfile failed: " + code + " desc" + desc);
                    }
                });
            }
        });
    }

    V2TIMSimpleMsgListener msgListener = new V2TIMSimpleMsgListener() {
        @Override
        public void onRecvC2CTextMessage(String msgID, V2TIMUserInfo sender, String text) {
            super.onRecvC2CTextMessage(msgID, sender, text);
        }

        @Override
        public void onRecvC2CCustomMessage(String msgID, V2TIMUserInfo sender, byte[] customData) {
            super.onRecvC2CCustomMessage(msgID, sender, customData);
            CommonJson<Object> commonJson = getCommonJson(customData);
            if (commonJson.cmd!=null){
                if (commonJson.cmd.equalsIgnoreCase("linkmic") || commonJson.cmd.equalsIgnoreCase("pk")) {
                    mMessageListener.onC2CCustomMessage(sender.getUserID(), commonJson.cmd, new Gson().toJson(commonJson.data));
                }else if (commonJson.cmd.equalsIgnoreCase("notifyPusherChange")){
                    mMessageListener.onPusherChanged();
                }
            }
        }

        @Override
        public void onRecvGroupTextMessage(String msgID, String groupID, V2TIMGroupMemberInfo sender, String text) {
            super.onRecvGroupTextMessage(msgID, groupID, sender, text);
            mMessageListener.onGroupTextMessage(groupID, sender.getUserID(), sender.getNickName(), sender.getFaceUrl(), text);
        }

        @Override
        public void onRecvGroupCustomMessage(String msgID, String groupID, V2TIMGroupMemberInfo sender, byte[] customData) {
            super.onRecvGroupCustomMessage(msgID, groupID, sender, customData);
            CommonJson<Object> commonJson = getCommonJson(customData);
            if (commonJson.cmd!=null){
                if (commonJson.cmd.equals("CustomTextMsg")){
                    UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), new TypeToken<UserInfo>() {}.getType());
                    if (userInfo != null){
                        mMessageListener.onGroupTextMessage(groupID, sender.getUserID(), sender.getNickName(),
                                sender.getFaceUrl(), userInfo.text);
                    }
                }else if (commonJson.cmd.equalsIgnoreCase("notifyPusherChange")) {
                    mMessageListener.onPusherChanged();
                }else {
                    mMessageListener.onGroupCustomMessage(mGroupID, sender.getUserID(), new Gson().toJson(commonJson.data));
                }
            }
        }
    };

    private CommonJson<Object> getCommonJson(byte[] customData){
        String data = new String(customData);
        CommonJson<Object> commonJson = new Gson().fromJson(data, new TypeToken<CommonJson<Object>>() {
        }.getType());
        return commonJson;
    }


    V2TIMGroupListener groupListener = new V2TIMGroupListener() {
        @Override
        public void onMemberEnter(String groupID, List<V2TIMGroupMemberInfo> memberList) {
            super.onMemberEnter(groupID, memberList);
            mMessageListener.onGroupMemberEnter(groupID,memberList);
        }

        @Override
        public void onMemberLeave(String groupID, V2TIMGroupMemberInfo member) {
            super.onMemberLeave(groupID, member);
            mMessageListener.onGroupMemberExit(groupID,member);
        }

        @Override
        public void onGroupDismissed(String groupID, V2TIMGroupMemberInfo opUser) {
            super.onGroupDismissed(groupID, opUser);
            mMessageListener.onGroupDestroyed(groupID);
        }

        @Override
        public void onQuitFromGroup(String groupID) {
            super.onQuitFromGroup(groupID);
        }

        @Override
        public void onReceiveRESTCustomData(String groupID, byte[] customData) {
            super.onReceiveRESTCustomData(groupID, customData);
        }
    };

//    @Override
//    public boolean onNewMessages(List<TIMMessage> list) {
//        for (TIMMessage message : list) {
//
//            for (int i = 0; i < message.getElementCount(); i++) {
//                TIMElem element = message.getElement(i);
//
//                 printDebugLog("onNewMessage type = %s", element.getType());
//
//                switch (element.getType()) {
//
//                    case GroupSystem: {
//                        TIMGroupSystemElemType systemElemType = ((TIMGroupSystemElem) element).getSubtype();
//
//                        switch (systemElemType) {
//
//                            case TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE: {
//                                printDebugLog("onNewMessage subType = %s", systemElemType);
//                                if (mMessageListener != null)
//                                    mMessageListener.onGroupDestroyed(((TIMGroupSystemElem) element).getGroupId());
//                                break;
//                            }
//
//                            case TIM_GROUP_SYSTEM_CUSTOM_INFO: {
//
//                                byte[] userData = ((TIMGroupSystemElem) element).getUserData();
//                                if (userData == null || userData.length == 0) {
//                                    printDebugLog("userData == null");
//                                    break;
//                                }
//
//                                String data = new String(userData);
//                                printDebugLog("onNewMessage subType = %s content = %s", systemElemType, data);
//                                try {
//                                    CommonJson<Object> commonJson = new Gson().fromJson(data, new TypeToken<CommonJson<Object>>() {
//                                    }.getType());
//                                    if (commonJson.cmd.equals("notifyPusherChange")) {
//                                        mMessageListener.onPusherChanged();
//                                    }
//                                } catch (JsonSyntaxException e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                            }
//                        }
//
//                        break;
//                    }//case GroupSystem
//
//                    case Custom: {
//                        byte[] userData = ((TIMCustomElem) element).getData();
//                        if (userData == null || userData.length == 0) {
//                            printDebugLog("userData == null");
//                            break;
//                        }
//
//                        String data = new String(userData);
//                        printDebugLog("onNewMessage subType = Custom content = %s", data);
//                        try {
//                            CommonJson<Object> commonJson = new Gson().fromJson(data, new TypeToken<CommonJson<Object>>() {
//                            }.getType());
//                            if (commonJson.cmd != null) {
//                                if (commonJson.cmd.equalsIgnoreCase("CustomTextMsg")) {
//                                    ++i;
//                                    UserInfo userInfo = new Gson().fromJson(new Gson().toJson(commonJson.data), UserInfo.class);
//                                    if (userInfo != null && i < message.getElementCount()) {
//                                        TIMElem nextElement = message.getElement(i);
//                                        TIMTextElem textElem = (TIMTextElem) nextElement;
//                                        String text = textElem.getText();
//                                        if (text != null) {
//                                            mMessageListener.onGroupTextMessage(mGroupID, message.getSender(), userInfo.nickName, userInfo.headPic, text);
//                                        }
//                                    }
//                                } else if (commonJson.cmd.equalsIgnoreCase("linkmic") || commonJson.cmd.equalsIgnoreCase("pk")) {
//                                    mMessageListener.onC2CCustomMessage(message.getSender(), commonJson.cmd, new Gson().toJson(commonJson.data));
//                                } else if (commonJson.cmd.equalsIgnoreCase("CustomCmdMsg")) {
//                                    mMessageListener.onGroupCustomMessage(mGroupID, message.getSender(), new Gson().toJson(commonJson.data));
//                                } else if (commonJson.cmd.equalsIgnoreCase("notifyPusherChange")) {
//                                    mMessageListener.onPusherChanged();
//                                }
//                            }
//                        } catch (JsonSyntaxException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    }
//
//                    case GroupTips: {
//                        TIMGroupTipsElem tipsElem = (TIMGroupTipsElem) element;
//                        if (tipsElem.getTipsType() == TIMGroupTipsType.Join) {
//                            Map<String, TIMUserProfile> changedUserInfos = tipsElem.getChangedUserInfo();
//                            if (changedUserInfos != null && changedUserInfos.size() > 0) {
//                                ArrayList<TIMUserProfile> users = new ArrayList<>();
//                                for (Map.Entry<String, TIMUserProfile> entry : changedUserInfos.entrySet()) {
//                                    users.add(entry.getValue());
//                                }
//                                mMessageListener.onGroupMemberEnter(tipsElem.getGroupId(), users);
//                            }
//                        } else if (tipsElem.getTipsType() == TIMGroupTipsType.Quit) {
//                            ArrayList<TIMUserProfile> users = new ArrayList<>();
//                            users.add(tipsElem.getOpUserInfo());
//                            mMessageListener.onGroupMemberExit(tipsElem.getGroupId(), users);
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//        return false;
//    }

    private void login(final Callback cb) {
        if (mSelfUserID == null || mSelfUserSig == null) {
            if (cb != null) {
                cb.onError(-1, "没有 UserId");
            }
            return;
        }


        //如果登录的是相同账号不进行登录操作
        if (TextUtils.equals(V2TIMManager.getInstance().getLoginUser(),this.mSelfUserID)){
            LogUtils.d("login: userId = " + this.mSelfUserID);
            cb.onSuccess();
            return;
        }

        LogUtils.d("start login: userId = " + this.mSelfUserID);

        final long loginStartTS = System.currentTimeMillis();

        mIMLoginListener = new IMMessageLoginCallback(loginStartTS, cb);

        V2TIMManager.getInstance().login(this.mSelfUserID, this.mSelfUserSig, mIMLoginListener);

    }

    private void logout(final Callback callback) {
        if (!mLoginSuccess) {
            return;
        }
        V2TIMManager.getInstance().logout(null);
//        TIMManager.getInstance().logout(null);
    }

    private void printDebugLog(String format, Object... args) {
        String log;
        try {
            log = String.format(format, args);
            LogUtils.d(log);
            if (mMessageListener != null) {
                mMessageListener.onDebugLog(log);
            }
        } catch (FormatFlagsConversionMismatchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 辅助类 IM Connect Listener
     */
    private final V2TIMSDKListener mIMConnListener = new V2TIMSDKListener() {

        @Override
        public void onConnectSuccess() {
            mMessageListener.onConnected();
            mConnectSuccess = true;
        }

        @Override
        public void onConnectFailed(int code, String error) {
            printDebugLog("disconnect: %s(%d)", error, code);
            if (mLoginSuccess) {
                if (mMessageListener != null) {
                    mMessageListener.onDisconnected();
                }
            }
            mConnectSuccess = false;
        }

        @Override
        public void onKickedOffline() {
            super.onKickedOffline();
            if (mMessageListener != null) {
                mMessageListener.onForceOffline();
            }
        }
    };

    /**
     * 辅助类 IM Login Listener
     */
    private class IMMessageLoginCallback implements V2TIMCallback {
        private long loginStartTS;
        private Callback callback;

        public IMMessageLoginCallback(long ts, Callback cb) {
            loginStartTS = ts;
            callback = cb;
        }

        public void clean() {
            loginStartTS = 0;
            callback = null;
        }

        @Override
        public void onError(int i, String s) {
            if (callback != null) {
                callback.onError(i, s);
            }
        }

        @Override
        public void onSuccess() {
            printDebugLog("login success, time cost %.2f secs", (System.currentTimeMillis() - loginStartTS) / 1000.0);
            if (callback != null) {
                callback.onSuccess();
            }
        }
    }


    /**
     * 辅助类 IM Message Listener
     */
    private class IMMessageCallback implements IMMessageListener {
        private IMMessageListener listener;

        public IMMessageCallback(IMMessageListener listener) {
            this.listener = listener;
        }

        public void setListener(IMMessageListener listener) {
            this.listener = listener;
        }

        @Override
        public void onConnected() {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onConnected();
                }
            });
        }

        @Override
        public void onDisconnected() {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onDisconnected();
                }
            });
        }

        @Override
        public void onPusherChanged() {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onPusherChanged();
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupID) {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupDestroyed(groupID);
                }
            });
        }

        @Override
        public void onDebugLog(final String line) {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onDebugLog("[IM] " + line);
                }
            });
        }

        @Override
        public void onGroupMemberEnter(String groupID, List<V2TIMGroupMemberInfo> memberInfoList) {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupMemberEnter(groupID, memberInfoList);
                }
            });
        }

        @Override
        public void onGroupMemberExit(final String groupID, V2TIMGroupMemberInfo memberInfo) {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupMemberExit(groupID, memberInfo);
                }
            });
        }

        @Override
        public void onForceOffline() {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onForceOffline();
                    }
                }
            });
        }

        @Override
        public void onGroupTextMessage(final String roomID, final String senderID, final String userName, final String headPic, final String message) {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupTextMessage(roomID, senderID, userName, headPic, message);
                }
            });
        }

        @Override
        public void onGroupCustomMessage(final String groupID, final String senderID, final String message) {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onGroupCustomMessage(groupID, senderID, message);
                }
            });
        }

        @Override
        public void onC2CCustomMessage(final String senderID, final String cmd, final String message) {
            runOnHandlerThread(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.onC2CCustomMessage(senderID, cmd, message);
                }
            });
        }
    }

    private static class CommonJson<T> {
        String cmd;
        T data;
    }

    private static final class UserInfo {
        String nickName;
        String headPic;
        String text;
    }
}
