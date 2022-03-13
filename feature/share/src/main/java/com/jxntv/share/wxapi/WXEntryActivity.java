package com.jxntv.share.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jxntv.base.plugin.H5EntryPlugin;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.share.utils.ShareConstants;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.net.URLDecoder;

/**
 * 微信entry activity
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

  private IWXAPI mWxApi;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (checkIntent()) {
      return;
    }

    mWxApi = WXAPIFactory.createWXAPI(this, ShareConstants.WECHAT_APP_ID, true);
    mWxApi.handleIntent(getIntent(), this);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    mWxApi.handleIntent(intent, this);
  }

  @Override
  public void onReq(BaseReq baseReq) {
    if (baseReq instanceof ShowMessageFromWX.Req) {
      WXMediaMessage mediaMessage = ((ShowMessageFromWX.Req) baseReq).message;
      if (mediaMessage != null && !TextUtils.isEmpty(mediaMessage.messageExt)) {
        try {
          String messageExtDecode = URLDecoder.decode(mediaMessage.messageExt, "utf-8");
          Intent intent = new Intent();
          intent.setData(Uri.parse(messageExtDecode));
          intent.setClassName(this.getPackageName(),"com.jxntv.home.HomeActivity");
          startActivity(intent);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    finish();
  }

  @Override
  public void onResp(BaseResp baseResp) {
    Log.d("---WXEntryActivity---","微信回调消息："+baseResp.toString());
    switch (baseResp.errCode){
      case BaseResp.ErrCode.ERR_OK:
        ToastUtils.showShort("分享成功");
        break;
      case BaseResp.ErrCode.ERR_USER_CANCEL:
        ToastUtils.showShort("分享取消");
        break;
    }
    finish();
  }

  private boolean checkIntent() {
    boolean isCrash = checkIntentExtra(getIntent());
    if (isCrash) {
      try {
        finish();
      } catch (Exception e) {
        return true;
      }
      return true;
    }
    return false;
  }

  private boolean checkIntentExtra(Intent intent) {
    if (intent != null) {
      Bundle extras = intent.getExtras();
      if (extras != null) {
        try {
          extras.isEmpty();
        } catch (Exception e) {
          return true;
        }
      }
    }
    return false;
  }
}
