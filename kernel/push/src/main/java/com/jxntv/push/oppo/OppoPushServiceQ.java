package com.jxntv.push.oppo;

import android.content.Context;
import com.heytap.msp.push.mode.DataMessage;
import com.heytap.msp.push.service.DataMessageCallbackService;
import com.jxntv.utils.LogUtils;

/**
 * 如果应用需要解析和处理Push消息（如透传消息），则继承PushService来处理，并在Manifest文件中申明Service
 * 如果不需要处理Push消息，则不需要继承PushService，直接在Manifest文件申明PushService即可
 *
 * Andrid Q 版本
 *
 */
public class OppoPushServiceQ extends DataMessageCallbackService {

    /**
     * 消息处理，应用可以打开页面或者执行命令,如果应用不需要处理透传消息，则不需要重写此方法
     *
     * @param context
     * @param dataMessage
     */
    @Override
    public void processMessage(Context context, DataMessage dataMessage) {
        super.processMessage(context, dataMessage);
        LogUtils.d(dataMessage.toString());
    }
}




