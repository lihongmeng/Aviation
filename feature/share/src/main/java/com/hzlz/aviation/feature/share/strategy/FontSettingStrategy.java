package com.hzlz.aviation.feature.share.strategy;

import android.content.Context;

import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;

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
