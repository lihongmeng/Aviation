package com.hzlz.aviation.kernel.push.push.hms;

import android.content.Intent;
import android.text.TextUtils;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.huawei.hms.push.SendException;
import com.hzlz.aviation.library.util.LogUtils;

import java.util.Arrays;

/**
 * 华为推送服务
 */
public class HmsPlatformsService extends HmsMessageService {

    private final static String CODELABS_ACTION = "com.huawei.codelabpush.action";

    private String TAG = "HMS: ";

    @Override
    public void onNewToken(String token) {
        LogUtils.d(TAG + "received refresh token:" + token);
        if (!TextUtils.isEmpty(token)) {
            HmsPushHelper.getInstance().setHmsToken(token);
        }
    }

    /**
     * This method is used to receive downstream data messages.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
     *
     * @param message RemoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        //华为透传消息处理
        LogUtils.d(TAG + "onMessageReceived is called");
        if (message == null) {
            LogUtils.d(TAG + "Received message entity is null!");
            return;
        }
        LogUtils.d(TAG + "getCollapseKey: " + message.getCollapseKey()
                + "\n getData: " + message.getData()
                + "\n getFrom: " + message.getFrom()
                + "\n getTo: " + message.getTo()
                + "\n getMessageId: " + message.getMessageId()
//                + "\n getOriginalUrgency: " + message.getOriginalUrgency()
//                + "\n getUrgency: " + message.getUrgency()
                + "\n getSendTime: " + message.getSentTime()
                + "\n getMessageType: " + message.getMessageType()
                + "\n getTtl: " + message.getTtl());

        RemoteMessage.Notification notification = message.getNotification();
        if (notification != null) {
            LogUtils.d(TAG + "\n getImageUrl: " + notification.getImageUrl()
                    + "\n getTitle: " + notification.getTitle()
                    + "\n getTitleLocalizationKey: " + notification.getTitleLocalizationKey()
                    + "\n getTitleLocalizationArgs: " + Arrays.toString(notification.getTitleLocalizationArgs())
                    + "\n getBody: " + notification.getBody()
                    + "\n getBodyLocalizationKey: " + notification.getBodyLocalizationKey()
                    + "\n getBodyLocalizationArgs: " + Arrays.toString(notification.getBodyLocalizationArgs())
                    + "\n getIcon: " + notification.getIcon()
                    + "\n getSound: " + notification.getSound()
                    + "\n getTag: " + notification.getTag()
                    + "\n getColor: " + notification.getColor()
                    + "\n getClickAction: " + notification.getClickAction()
                    + "\n getChannelId: " + notification.getChannelId()
                    + "\n getLink: " + notification.getLink()
                    + "\n getNotifyId: " + notification.getNotifyId());
        }

        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "onMessageReceived");
        intent.putExtra("msg", "onMessageReceived called, message id:" + message.getMessageId() + ", payload data:"
                + message.getData());

        sendBroadcast(intent);

        Boolean judgeWhetherIn10s = false;

        if (judgeWhetherIn10s) {
            startWorkManagerJob(message);
        } else {
            processWithin10s(message);
        }
    }

    private void startWorkManagerJob(RemoteMessage message) {

    }

    private void processWithin10s(RemoteMessage message) {
    }

    @Override
    public void onMessageSent(String msgId) {
        LogUtils.d(TAG + "onMessageSent called, Message id:" + msgId);
        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "onMessageSent");
        intent.putExtra("msg", "onMessageSent called, Message id:" + msgId);

        sendBroadcast(intent);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        LogUtils.d(TAG + "onSendError called, message id:" + msgId + ", ErrCode:"
                + ((SendException) exception).getErrorCode() + ", description:" + exception.getMessage());

        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "onSendError");
        intent.putExtra("msg", "onSendError called, message id:" + msgId + ", ErrCode:"
                + ((SendException) exception).getErrorCode() + ", description:" + exception.getMessage());

        sendBroadcast(intent);
    }
}
