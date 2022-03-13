package com.jxntv.share.strategy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.share.BuildConfig;
import com.jxntv.share.R;
import com.jxntv.share.utils.ShareConstants;

public class CopyShareStrategy implements ShareStrategy {
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TAG = CopyShareStrategy.class.getSimpleName();

  private Context mContext;

  public CopyShareStrategy(Context context) {
    mContext = context;
  }

  @Override
  public boolean canShare() {
    return false;
  }

  @Override
  public void share(ShareDataModel model) {
    ClipboardManager clipboardManager =
        (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    if (clipboardManager != null) {
      clipboardManager.setPrimaryClip(ClipData.newPlainText(ShareConstants.CLIPBOARD_LABEL, model.getUrl()));
      Toast.makeText(mContext, R.string.share_copy_link_success, Toast.LENGTH_SHORT).show();
    }
  }
}
