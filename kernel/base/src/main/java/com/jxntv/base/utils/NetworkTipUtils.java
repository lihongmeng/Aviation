package com.jxntv.base.utils;

import android.content.Context;
import android.widget.Toast;
import com.jxntv.base.R;
import com.jxntv.network.NetworkUtils;

/**
 * 网络状态提示工具类
 */
public class NetworkTipUtils {
  /**
   * 请求发起前，检查网络连接，不通畅时给出提示
   * @param context
   * @return
   */
  public static boolean checkNetworkOrTip(Context context) {
    if (NetworkUtils.isNetworkConnected()) {
      return true;
    } else {
      Toast.makeText(context, R.string.all_network_not_available_action_tip, Toast.LENGTH_SHORT).show();
      return false;
    }
  }
}
