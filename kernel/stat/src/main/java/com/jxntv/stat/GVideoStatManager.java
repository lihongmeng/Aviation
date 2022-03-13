package com.jxntv.stat;

import android.text.TextUtils;
import androidx.lifecycle.Observer;
import com.google.gson.JsonObject;
import com.jxntv.base.Constant;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.stat.net.StatRepository;
import java.util.UUID;

/**
 * 统计埋点入口
 */
public class GVideoStatManager {
  private GVideoStatManager() {
  }

  private static final class INNER {
    private static final GVideoStatManager MANAGER = new GVideoStatManager();
  }

  public static GVideoStatManager getInstance() {
    return INNER.MANAGER;
  }
  private String mSessionId;
  private String mUid;
  private String mChannelId;
  private StatRepository repository = new StatRepository();

  /** 初始化埋点服务 */
  public void init() {
    checkUid();
    GVideoEventBus.get(GVideoRuntime.APP_STATUS_CHANGED, Boolean.class)
        .observeForever(new Observer<Boolean>() {
          @Override public void onChanged(Boolean foreground) {
            if (foreground) {
              String sessionId = getSessionId();
              StatEntity entity = StatEntity.Builder.aStatEntity()
                  .withPid("")
                  .withDs("")
                  .withEv(StatConstants.EV_APP_START)
                  .withType(StatConstants.TYPE_APP_A)
                  .build();
              stat(entity);
              upload();
            } else {
              StatEntity entity = StatEntity.Builder.aStatEntity()
                  .withPid("")
                  .withDs("")
                  .withEv(StatConstants.EV_APP_EXIT)
                  .withType(StatConstants.TYPE_APP_A)
                  .build();
              stat(entity);
              upload();
            }
          }
        });
    // 登录
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observeForever(new Observer<Object>() {
      @Override public void onChanged(Object o) {
        checkUid();
      }
    });
    // 登出
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observeForever(new Observer<Object>() {
      @Override public void onChanged(Object o) {
        checkUid();
      }
    });
  }

  public void setChannelId(String channelId) {
    mChannelId = channelId;
  }

  public String getSessionId() {
    if (TextUtils.isEmpty(mSessionId)) {
      mSessionId = UUID.randomUUID().toString();
    }
    return mSessionId;
  }

  String getUid() {
    return mUid;
  }
  String getChannelId() {
    return mChannelId;
  }
  private JsonObject createFrom(String pid, String channelId) {
    JsonObject from = new JsonObject();
    from.addProperty(StatConstants.DS_KEY_PID, pid);
    from.addProperty(StatConstants.DS_KEY_CHANNEL_ID, channelId);
    return from;
  }
  private JsonObject createDsContent(String contentId, String channelId, JsonObject from) {
    JsonObject ds = new JsonObject();
    ds.addProperty(StatConstants.DS_KEY_CONTENT_ID, contentId);
    ds.addProperty(StatConstants.DS_KEY_CHANNEL_ID, channelId);
    ds.add(StatConstants.DS_KEY_FROM, from);
    return ds;
  }
  private JsonObject createDsContent(String contentId, String channelId, String fromPid, String fromChannelId) {
    return createDsContent(contentId, channelId, createFrom(fromPid, fromChannelId));
  }
  public JsonObject createDsContent(StatFromModel stat) {
    String contentId = stat != null ? stat.contentId : "";
    String channelId = stat != null ? stat.channelId : "";
    String fromPid = stat != null ? stat.fromPid : "";
    String fromChannelId = stat != null ? stat.fromChannelId : "";

    return createDsContent(contentId, channelId, fromPid, fromChannelId);
  }

  /**
   * 实时业务埋点
   * @param statEntity
   */
  public void statRealtime(StatEntity statEntity) {
    repository.statRealtime(statEntity);
  }

  /**
   * 业务埋点
   *
   * @param statEntity 具体业务点
   */
  public void stat(StatEntity statEntity) {
    //StatEntity statEntity = StatEntity.Builder.aStatEntity()
    //    .withType(StatConstants.TYPE_CLICK_C)
    //    .withTimestamp(new Date())
    //    .withEv("UPLOAD")
    //    .withDs("type=1")
    //    .withPid("Home")
    //    .withSessionId(UUID.randomUUID().toString())
    //    .build();
    repository.stat(statEntity);
  }

  /**
   * 上传埋点；在a点切换时调用
   */
  private void upload() {
    repository.upload();
  }

  private void checkUid() {
    try {
      String uid = PluginManager.get(AccountPlugin.class).getUserId();
      mUid = uid;
    } catch (Exception e) {
    }
  }
}
