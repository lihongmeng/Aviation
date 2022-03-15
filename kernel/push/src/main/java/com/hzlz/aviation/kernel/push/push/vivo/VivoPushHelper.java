package com.hzlz.aviation.kernel.push.push.vivo;

import android.text.TextUtils;

import com.hzlz.aviation.kernel.push.PushManager;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.LogUtils;
import com.vivo.push.PushClient;

/**
 * @author huangwei
 * time : 2021/1/22 9:34
 * desc :  vivo  推送
 **/
public class VivoPushHelper {

    private static VivoPushHelper helper;
    private PushManager.PushListener listener;

    public static VivoPushHelper getInstance(){
        if (helper == null){
            helper = new VivoPushHelper();
        }
        return helper;
    }

    public void init(PushManager.PushListener listener){
        this.listener = listener;
        PushClient.getInstance(GVideoRuntime.getAppContext()).initialize();
        openPush();

    }


    public void setRegisterId(String registerId) {
        if (listener!=null){
            listener.success(registerId);
        }
    }


    public void closePush(){
        PushClient.getInstance(GVideoRuntime.getAppContext()).turnOffPush(i -> {
                if (i==0){
                    LogUtils.d("vivo push close success  :");
                }else{
                    LogUtils.e("vivo push close error:"+ i);
                }
            });
    }


    /**
     * 打开push
     */
    public void openPush(){
        PushClient.getInstance(GVideoRuntime.getAppContext()).turnOnPush(i -> {
            if (i==0){
                LogUtils.d("vivo push open success");
                String regId = PushClient.getInstance(GVideoRuntime.getAppContext()).getRegId();
                if(!TextUtils.isEmpty(regId)){
                    setRegisterId(regId);
                }
            }else{
                LogUtils.e("vivo push open error : "+ i);
            }
        });
    }


}
