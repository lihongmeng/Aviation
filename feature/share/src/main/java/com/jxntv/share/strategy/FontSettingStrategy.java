package com.jxntv.share.strategy;

import android.content.Context;
import android.os.Bundle;

import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.share.BuildConfig;

public class FontSettingStrategy implements ShareStrategy {

  private Context mContext;

  public FontSettingStrategy(Context context) {
    mContext = context;
  }

  @Override
  public boolean canShare() {
    return true;
  }

  @Override
  public void share(ShareDataModel dataModel) {
    GVideoEventBus.get(SharePlugin.EVENT_SHOW_FONT_SETTING).post("");

  }
}
