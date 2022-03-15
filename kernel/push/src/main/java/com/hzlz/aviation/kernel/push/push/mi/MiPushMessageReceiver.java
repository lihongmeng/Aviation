package com.hzlz.aviation.kernel.push.push.mi;

import android.content.Context;
import android.text.TextUtils;

import com.hzlz.aviation.library.util.LogUtils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

/**
 * 小米推送
 *
 * 1、onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 2、onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 3、onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 4、onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 6、onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 7、以上这些方法运行在非 UI 线程中。
 */
public class MiPushMessageReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        //onReceivePassThroughMessage 透传消息
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        //通知栏消息被点击
        LogUtils.d("onNotificationMessageClicked:"+message.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        //通知栏消息到达

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {

        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                LogUtils.d("miPush "+"注册成功  ");
            } else {
                LogUtils.d("miPush "+"注册失败");
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                LogUtils.d("miPush "+"设置别名成功："+mAlias);
            } else {
                LogUtils.d("miPush "+"设置别名失败："+message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                LogUtils.d("miPush "+"注销设置别名成功："+ mAlias);
            } else {
                LogUtils.d("miPush "+"注销设置别名失败："+ message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                LogUtils.d("miPush "+"set_account_success："+ mAlias);
            } else {
                LogUtils.d("miPush "+"set_account_fail："+ message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                LogUtils.d("miPush "+"unset_account_success："+ mAccount);
            } else {
                LogUtils.d("miPush "+"unset_account_fail："+ message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                LogUtils.d("miPush "+"subscribe_topic_success："+ mTopic);
            } else {
                LogUtils.d("miPush "+"subscribe_topic_success："+ message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                LogUtils.d("miPush "+"unsubscribe_topic_success："+ mTopic);
            } else {
                LogUtils.d("miPush "+"unsubscribe_topic_fail："+ message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                LogUtils.d("miPush "+"set_accept_time_success："+  mStartTime+" -- "+mEndTime);
            } else {
                LogUtils.d("miPush "+"set_accept_time_fail："+ message.getReason());
            }
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        //推送注册结果
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                LogUtils.d("miPush "+"注册成功");
                MiPushHelper.getInstance().setRegisterId(mRegId);
            } else {
                LogUtils.d("miPush "+"注册失败");
            }
        }else {

        }

    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        LogUtils.d("miPush onRequirePermissions is called. need permission" + arrayToString(permissions));

    }

    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }


}
