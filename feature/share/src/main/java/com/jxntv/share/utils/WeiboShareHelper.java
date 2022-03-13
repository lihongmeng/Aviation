package com.jxntv.share.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.share.BuildConfig;
import com.jxntv.share.R;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * @author huangwei
 * date : 2021/10/12
 * desc : 微博分享工具类
 **/
public class WeiboShareHelper {

    private static WeiboShareHelper helper;
    private IWBAPI mWBAPI;

    public static WeiboShareHelper getHelper() {
        if (helper==null){
            helper = new WeiboShareHelper();
        }
        return helper;
    }

    public void init(Context context){
        mWBAPI = WBAPIFactory.createWBAPI(context);
        AuthInfo authInfo = new AuthInfo(context, ShareConstants.WEIBO_APP_ID,
                ShareConstants.COMMENT_REDIRECT_URL, ShareConstants.SCOPE);
        mWBAPI.registerApp(context,authInfo);
        mWBAPI.setLoggerEnable(BuildConfig.DEBUG);
    }

    public void share(ShareDataModel model){
        if (model==null || mWBAPI==null){
            return;
        }
        Glide.with(GVideoRuntime.getAppContext()).asBitmap().load(TextUtils.isEmpty(model.getImage())?
                R.drawable.ic_launcher:model.getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        try {
                            readShare(model, null);
                        } catch (Exception e) {
                        }
                    }

                    @Override public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        try {
                            readShare(model, resource);
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void readShare(ShareDataModel model, Bitmap resource) {

        WeiboMultiMessage message = new WeiboMultiMessage();
        WebpageObject webpageObject = new WebpageObject();
        webpageObject.identify = UUID.randomUUID().toString();
        webpageObject.title = model.getTitle();
        webpageObject.description = model.getDescription();
        //设置缩略图
        if (resource != null) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(resource, 200, 200, true);
            webpageObject.thumbData = bitmap2Bytes(thumbBmp,32);
        }
        webpageObject.actionUrl = model.getUrl();
        webpageObject.defaultText = "分享网页";
        message.mediaObject = webpageObject;
        mWBAPI.shareMessage(message,false);

    }


    public byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset(); // 清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);
            options -= 10;
        }
        return output.toByteArray();
    }

    public void doResult(Intent intent){
        if (mWBAPI!=null){
            mWBAPI.doResultIntent(intent,callback);
        }
    }

    private final WbShareCallback callback = new WbShareCallback() {
        @Override
        public void onComplete() {
            ToastUtils.showShort(R.string.share_success);
            mWBAPI = null;
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtils.showShort(uiError.errorMessage);
            mWBAPI = null;
        }

        @Override
        public void onCancel() {
            ToastUtils.showShort(R.string.share_cancel);
            mWBAPI = null;
        }
    };
}
