package com.jxntv.android.video;

import android.text.TextUtils;

import com.jxntv.base.Constant;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;

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
