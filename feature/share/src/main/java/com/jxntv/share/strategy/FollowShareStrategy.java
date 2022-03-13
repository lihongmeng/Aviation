package com.jxntv.share.strategy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.share.BuildConfig;
import com.jxntv.share.R;

public class FollowShareStrategy implements ShareStrategy {
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TAG = FollowShareStrategy.class.getSimpleName();

  private Context mContext;

  public FollowShareStrategy(Context context) {
    mContext = context;
  }

  @Override
  public boolean canShare() {
    if (TextUtils.isEmpty(PluginManager.get(AccountPlugin.class).getToken())) {
      return false;
    }
    return true;
  }

  @Override
  public void share(ShareDataModel dataModel) {
    if (!canShare()) {
      Toast.makeText(mContext, R.string.share_login_unavailable, Toast.LENGTH_SHORT).show();
      return;
    }
    if (!NetworkTipUtils.checkNetworkOrTip(mContext)) {
      return;
    }
    String authorId = dataModel.getAuthorId();
    boolean follow = !dataModel.isFollow();
    PluginManager.get(AccountPlugin.class).getFollowRepository()
        .followAuthor(authorId, dataModel.getAuthorType(), dataModel.getAuthorName(),follow)
        .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
          @Override protected void onRequestData(Boolean result) {
            if (result) {
              Toast.makeText(mContext, R.string.share_other_follow_success, Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(mContext, R.string.share_other_follow_cancel, Toast.LENGTH_SHORT).show();
            }
          }

          @Override protected void onRequestError(Throwable throwable) {
            if (DEBUG) {
              Log.e(TAG, "", throwable);
            }
            GVideoSensorDataManager.getInstance().followAccount(follow,dataModel.getAuthorId(),
                    dataModel.getAuthorName(),dataModel.getAuthorType(),throwable.getMessage());
            if (follow) {
              Toast.makeText(mContext, R.string.share_other_follow_failed, Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(mContext, R.string.share_other_follow_cancel_failed, Toast.LENGTH_SHORT).show();
            }
          }
        });
  }
}
