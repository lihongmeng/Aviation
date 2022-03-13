package com.jxntv.share.strategy;

import android.content.Context;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.share.BuildConfig;
import com.jxntv.share.utils.WeiboShareHelper;

public class WeiBoShareStrategy implements ShareStrategy {
  private static final boolean DEBUG = BuildConfig.DEBUG;
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
