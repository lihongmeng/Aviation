package com.hzlz.aviation.kernel.base.receiver;

import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SYSTEM_CLOSE_WIFI;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SYSTEM_START_WIFI;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.WIFI_CONNECTED;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.WIFI_DISCONNECTED;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.WIFI_SIGNAL_STRENGTH_CHANGE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.hzlz.aviation.kernel.event.GVideoEventBus;

public class NetWorkStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "wifiReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
            GVideoEventBus.get(WIFI_SIGNAL_STRENGTH_CHANGE).post(null);
            Log.i(TAG, "wifi信号强度变化");
        }
        //wifi连接上与否
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                NetworkInfo.State state = info.getState();
                if (state.equals(NetworkInfo.State.DISCONNECTED)) {
                    GVideoEventBus.get(WIFI_DISCONNECTED).post(null);
                    Log.i(TAG, "wifi断开");
                } else if (state.equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager =
                            (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    String wifiName = wifiInfo.getSSID();
                    GVideoEventBus.get(WIFI_CONNECTED,String.class).post(wifiName);
                    Log.i(TAG, "连接到网络 " + wifiName);
                }
            }

        }
        //wifi打开与否
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                GVideoEventBus.get(SYSTEM_CLOSE_WIFI).post(null);
                Log.i(TAG, "系统关闭wifi");
            } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                GVideoEventBus.get(SYSTEM_START_WIFI).post(null);
                Log.i(TAG, "系统开启wifi");
            }
        }
    }
}
