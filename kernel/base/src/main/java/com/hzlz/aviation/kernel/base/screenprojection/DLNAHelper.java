package com.hzlz.aviation.kernel.base.screenprojection;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.liuwei.android.upnpcast.NLUpnpCastManager;
import com.liuwei.android.upnpcast.device.CastDevice;

/**
 * 投屏
 */
public class DLNAHelper {

    private static DLNAHelper helper;

    // 当前正在播放的
    private String currentPlayUrl;

    /**
     * 需要播放的投屏链接
     */
    private String needPlayUrl;

    private CastDevice currentDevice;

    public static DLNAHelper getInstance() {
        if (helper == null) {
            helper = new DLNAHelper();
        }
        return helper;
    }

    public void startScreenProjection(Context context, String playUrl) {
        Intent intent = new Intent(context, ScreenProjectionActivity.class);
        this.needPlayUrl = playUrl;
        context.startActivity(intent);
    }

    // 判断是否是当前正在播放的url
    public boolean isSameWithCurrentPlayUrl(String playUrl) {
        return TextUtils.equals(this.currentPlayUrl, playUrl);
    }

    public boolean isSameWithCurrentPlayUrlWithT(String playUrl) {
        return TextUtils.equals(
                this.currentPlayUrl.substring(0, this.currentPlayUrl.lastIndexOf("t")),
                playUrl.substring(0, this.currentPlayUrl.lastIndexOf("t"))
        );
    }

    public boolean isCurrentPlayEmpty() {
        return TextUtils.isEmpty(currentPlayUrl);
    }

    // 当前连接的设备的名称
    public String getSelectedDeviceName() {
        CastDevice castDevice = getCurrentDevice();
        if (castDevice == null) {
            return "";
        }
        return castDevice.getName();
    }

    /**
     * 停止播放
     */
    public void stop() {
        NLUpnpCastManager.getInstance().stop();
        NLUpnpCastManager.getInstance().disconnect();
    }

    public String getCurrentPlayUrl() {
        return currentPlayUrl;
    }

    public void setCurrentPlayUrl(String currentPlayUrl) {
        this.currentPlayUrl = currentPlayUrl;
    }

    public String getNeedPlayUrl() {
        currentPlayUrl = needPlayUrl;
        return needPlayUrl;
    }

    public void setNeedPlayUrl(String needPlayUrl) {
        this.needPlayUrl = needPlayUrl;
    }

    public CastDevice getCurrentDevice() {
        return currentDevice;
    }

    public void setCurrentDevice(CastDevice currentDevice) {
        this.currentDevice = currentDevice;
    }
}
