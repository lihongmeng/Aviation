package com.jxntv.push.vivo;
import android.content.Context;
import android.text.TextUtils;
import com.jxntv.utils.LogUtils;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoPushReceiver extends OpenClientPushMessageReceiver {

    @Override
    public void onReceiveRegId(Context context, String s) {
        LogUtils.d("vivo RegisterId:"+s);
        if (!TextUtils.isEmpty(s)) {
            VivoPushHelper.getInstance().setRegisterId(s);
        }
    }
}
