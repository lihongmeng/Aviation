package com.hzlz.aviation.kernel.base.model.update;

import android.text.TextUtils;

import com.hzlz.aviation.kernel.base.utils.StorageUtils;

/**
 * 更新Model
 */
public class UpdateModel {

    /** 下载地址 */
    public String downloadUrl;
    /** 最新程序版本 */
    public String version;
    /** 更新内容 */
    public String releaseNotes;
    /** 更新日期 */
    public String date;
    /** 是否强制更新 */
    public boolean forceUpdate; //强制更新，所有接口都不可用，弹窗提示；
    /** 是否从官网获取 */
    public boolean officialSource;
    /** 包名 */
    public String bundleId;

    public boolean isValid() {
        if (TextUtils.isEmpty(downloadUrl)) {
            return false;
        }
        if (TextUtils.isEmpty(bundleId)) {
            return false;
        }

        return true;
    }

    public String getAPKPath(){
        return StorageUtils.getCacheDirectory()+ "/ "+ bundleId +"_" + version +".apk";
    }
}
