package com.jxntv.stat.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jxntv.network.NetworkManager;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.stat.db.entity.StatGroupEntity;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 埋点请求
 */
public final class StatRequest extends BaseGVideoMapRequest<StatResponse> {
  private final boolean mRealtime;
  public StatRequest(List<StatEntity> stats, StatGroupEntity group) {
    mRealtime = group.realtime;

    JsonArray array = format(stats, group);
    mParameters.put("stat", array);
  }
  private JsonArray format(List<StatEntity> stats, StatGroupEntity group) {
    JsonArray array = new JsonArray();
    if (group != null && stats != null && !stats.isEmpty()) {
      for (StatEntity stat : stats) {
        JsonObject json = new JsonObject();
        json.addProperty("type", stat.type);
        json.addProperty("pid", stat.pid);
        json.addProperty("sessionId", stat.sessionId);
        json.addProperty("timestamp", stat.timestamp.getTime());
        json.addProperty("ev", stat.ev);
        json.add("ds", NetworkManager.getInstance().getGson()
            .fromJson(stat.ds, JsonObject.class));

        json.addProperty("uid", group.uid);
        json.addProperty("networkType", group.networkType);
        json.addProperty("carrier", group.carrier);
        json.addProperty("cid", group.cid);
        json.addProperty("appName", group.appName);
        json.addProperty("appVersion", group.appVersion);
        json.addProperty("channel", group.channel);
        json.addProperty("manufacturer", group.manufacturer);
        json.addProperty("model", group.model);
        json.addProperty("os", group.os);
        json.addProperty("osVersion", group.osVersion);

        array.add(json);
      }
    }
    return array;
  }

  @Override protected int getMaxParameterCount() {
    return 2;
  }

  @Override protected Observable<Response<JsonElement>> getResponseObservable() {
    if (mRealtime) {
      return StatApi.Instance.get().uploadStatRealtime("gzip", mParameters);
    } else {
      return StatApi.Instance.get().uploadStat("gzip", mParameters);
    }
  }
}