package com.hzlz.aviation.feature.share.strategy;

import android.content.Context;

import com.hzlz.aviation.feature.share.utils.WeiboShareHelper;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;

public class WeiBoShareStrategy implements ShareStrategy {
  private static final boolean DEBUG = true;
  private static final String TAG = WeiBoShareStrategy.class.getSimpleName();

  public WeiBoShareStrategy(Context context) {
    WeiboShareHelper.getHelper().init(context);
  }

  @Override
  public boolean canShare() {
    return true;
  }

  @Override
  public void share(ShareDataModel model) {
    WeiboShareHelper.getHelper().share(model);
  }


}
