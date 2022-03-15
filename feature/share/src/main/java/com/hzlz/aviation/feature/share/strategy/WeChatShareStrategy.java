package com.hzlz.aviation.feature.share.strategy;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hzlz.aviation.feature.share.utils.ShareConstants;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.feature.share.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

public class WeChatShareStrategy implements ShareStrategy {
  private static final boolean DEBUG = true;
  private static final String TAG = WeChatShareStrategy.class.getSimpleName();

  private IWXAPI mWxapi;

  private Context mContext;

  private int mScene;

  public WeChatShareStrategy(Context context, int scene) {
    mContext = context;
    if (mWxapi == null) {
      mWxapi = WXAPIFactory.createWXAPI(context, ShareConstants.WECHAT_APP_ID, true);
    }
    mWxapi.registerApp(ShareConstants.WECHAT_APP_ID);
    mScene = scene;
  }

  @Override
  public boolean canShare() {
    boolean installed = mWxapi.isWXAppInstalled();
    if (!installed) return false;
    if (mScene == WXSceneTimeline) {
      if (mWxapi.getWXAppSupportAPI() < Build.TIMELINE_SUPPORTED_SDK_INT) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void share(ShareDataModel model) {
    if (!canShare()) {
      Toast.makeText(mContext, R.string.share_wx_unavailable, Toast.LENGTH_SHORT).show();
      return;
    }

    //使用Application，如果关联Activity,onLoadFailed可能会被多次调用
    Glide.with(GVideoRuntime.getAppContext()).asBitmap().load(TextUtils.isEmpty(model.getImage())?
            R.drawable.ic_launcher:model.getImage())
            .into(new CustomTarget<Bitmap>() {
      @Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
        try {
          readShare(model, null);
        } catch (Exception e) {
          Log.e(TAG, "", e);
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
          Log.e(TAG, "", e);
        }
      }
    });
  }

  private void readShare(ShareDataModel model, Bitmap resource) {
    WXWebpageObject webpage = new WXWebpageObject();
    webpage.webpageUrl = model.getUrl();
    WXMediaMessage msg = new WXMediaMessage(webpage);
    //设置缩略图
    if (resource != null) {
      Bitmap thumbBmp = Bitmap.createScaledBitmap(resource, 200, 200, true);
      msg.thumbData = bitmap2Bytes(thumbBmp, 32);
    }
    msg.title = model.getTitle();
    msg.description = model.getDescription();

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());
    req.message = msg;
    req.scene = mScene;

    mWxapi.sendReq(req);
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
}
