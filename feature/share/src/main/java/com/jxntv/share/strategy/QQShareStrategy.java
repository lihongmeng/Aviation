package com.jxntv.share.strategy;

import android.content.Context;

import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.share.BuildConfig;
import com.jxntv.share.utils.QQShareHelper;
import com.jxntv.share.utils.WeiboShareHelper;

public class QQShareStrategy implements ShareStrategy {

  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TAG = QQShareStrategy.class.getSimpleName();
  private int scene;

  public QQShareStrategy(Context context,int scene) {
    this.scene = scene;
    QQShareHelper.getHelper().init(context);
  }

  @Override
  public boolean canShare() {
    return true;
  }

  @Override
  public void share(ShareDataModel model) {
    QQShareHelper.getHelper().share(model,scene);
  }


}
