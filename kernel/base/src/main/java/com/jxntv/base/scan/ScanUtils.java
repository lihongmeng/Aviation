package com.jxntv.base.scan;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

public class ScanUtils {

    private volatile static ScanUtils singleInstance = null;

    private ScanUtils(){
    }

    public static ScanUtils getInstance(){
        if (singleInstance == null) {
            synchronized (ScanUtils.class) {
                if (singleInstance == null) {
                    singleInstance = new ScanUtils();
                }
            }
        }
        return singleInstance;
    }

    public void startScanActivity(Activity activity,Class<?> captureActivity){
        if(activity==null){
            return;
        }
        new IntentIntegrator(activity)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                //.setPrompt("请对准二维码")// 设置提示语
                .setCameraId(0)// 选择摄像头,可使用前置或者后置
                .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                .setCaptureActivity(captureActivity)//自定义扫码界面
                .initiateScan();// 初始化扫码
    }

}
