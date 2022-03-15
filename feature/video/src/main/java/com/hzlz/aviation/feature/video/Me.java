package com.hzlz.aviation.feature.video;

import android.text.TextUtils;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.ioc.PluginManager;

public class Me {
  public static AuthorModel getMeInfo() {
    String uid = PluginManager.get(AccountPlugin.class).getUserId();
    AuthorModel me = AuthorModel.Builder.anAuthorModel()
        .withAvatar(Constants.AVATAR_URL)
        .withName(PluginManager.get(AccountPlugin.class).getNickName())
        .withId(uid)
        .build();
    return me;
  }

  public static boolean isLogined() {
    String token = PluginManager.get(AccountPlugin.class).getToken();
    return !TextUtils.isEmpty(token);
  }

  public static boolean checkOrLogin() {
    if (Me.isLogined()) {
      return true;
    } else {
      GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
      return false;
    }
  }
}
