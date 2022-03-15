package com.hzlz.aviation.feature.share.strategy;

import android.content.Context;

import com.hzlz.aviation.feature.share.utils.QQShareHelper;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;

public class QQShareStrategy implements ShareStrategy {

  private static final boolean DEBUG = true;
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
