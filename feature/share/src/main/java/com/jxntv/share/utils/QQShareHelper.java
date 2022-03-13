package com.jxntv.share.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.utils.StorageUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.share.R;
import com.jxntv.utils.FileUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.DefaultUiListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * @author huangwei
 * date : 2021/10/12
 * desc : 微博分享工具类
 **/
public class QQShareHelper {

    private static QQShareHelper helper;
    private Tencent mTencent;
    private Context mContext;

    public static QQShareHelper getHelper() {
        if (helper==null){
            helper = new QQShareHelper();
        }
        return helper;
    }

    public void init(Context context){
        this.mContext = context;
        mTencent = Tencent.createInstance(ShareConstants.QQ_APP_ID,context);
        if (mTencent == null){
            return;
        }
    }

    @SuppressLint("RestrictedApi")
    public void share(ShareDataModel model, int scene){
        if (model==null || mTencent==null){
            return;
        }

        final Bundle params = new Bundle();
        if (scene==0) {
            params.putString(QQShare.SHARE_TO_QQ_TITLE,  model.getTitle());
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mContext.getString(R.string.app_name));
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, model.getUrl());
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, model.getDescription());
            if(TextUtils.isEmpty(model.getImage())) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, getDefaultIcon());
            }else {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, model.getImage());
            }
            mTencent.shareToQQ((Activity) mContext, params, qqShareListener);
        }else {
            ArrayList<String> arrayList = new ArrayList<>();
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE,  model.getTitle());
            params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, mContext.getString(R.string.app_name));
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, model.getUrl());
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, model.getDescription());
            if(TextUtils.isEmpty(model.getImage())) {
                arrayList.add(getDefaultIcon());
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,arrayList);
            }else {
                arrayList.add(model.getImage());
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,arrayList);
            }
            ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    mTencent.shareToQzone((Activity) mContext, params, qqShareListener);
                }
            });
        }

    }

    public IUiListener qqShareListener = new DefaultUiListener() {
        @Override
        public void onCancel() {
            ToastUtils.showShort(R.string.share_cancel);
        }

        @Override
        public void onComplete(Object response) {
            ToastUtils.showShort(R.string.share_success);
        }

        @Override
        public void onError(com.tencent.tauth.UiError e) {
            ToastUtils.showShort(e.errorMessage);
        }

        @Override
        public void onWarning(int code) {

        }
    };

    private String getDefaultIcon() {
        String path = "";
        try {
            PackageInfo info = GVideoRuntime.getAppContext().getPackageManager().getPackageInfo(GVideoRuntime.getAppContext().getPackageName(), 0);
            path = StorageUtils.getCacheDirectory().getAbsolutePath() + File.separator + info.versionName +"_logo.png";
            if (FileUtils.isFile(new File(path))) {
                return path;
            }
            BitmapDrawable drawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.ic_launcher);
            Bitmap bitmap = drawable.getBitmap();
            OutputStream os = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

}
