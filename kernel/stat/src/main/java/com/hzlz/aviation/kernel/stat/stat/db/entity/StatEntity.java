package com.hzlz.aviation.kernel.stat.stat.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatType;

import java.util.Date;

/**
 * 基础埋点实体
 */
@Entity(tableName = "stat")
public class StatEntity {
  @PrimaryKey(autoGenerate = true)
  public long id;
  /** 缓存埋点批量上传分包标识 */
  @ColumnInfo
  public long groupId;
  /** 实时埋点 */
  @ColumnInfo
  public boolean realtime;

  /** 埋点类型 */
  @ColumnInfo
  public @StatType String type;
  /** 页面id */
  @ColumnInfo
  public String pid;
  /** 生命周期id */
  @ColumnInfo
  public String sessionId;
  /** 时间戳 */
  @ColumnInfo
  public Date timestamp;
  /** 事件id */
  @ColumnInfo
  public String ev;
  /** 事件参数 */
  @ColumnInfo
  public String ds;

  public static final class Builder {
    private long id;
    private long groupId;
    private boolean realtime;
    private String type;
    private String pid;
    private String sessionId;
    private Date timestamp;
    private String ev;
    private String ds;

    private Builder() {
    }

    public static Builder aStatEntity() {
      return new Builder();
    }

    public Builder withId(long id) {
      this.id = id;
      return this;
    }

    public Builder withGroupId(long groupId) {
      this.groupId = groupId;
      return this;
    }

    public Builder withRealtime(boolean realtime) {
      this.realtime = realtime;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public Builder withPid(String pid) {
      this.pid = pid;
      return this;
    }

    public Builder withSessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
    }

    public Builder withTimestamp(Date timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withEv(String ev) {
      this.ev = ev;
      return this;
    }

    public Builder withDs(String ds) {
      this.ds = ds;
      return this;
    }

    public StatEntity build() {
      StatEntity statEntity = new StatEntity();
      statEntity.ev = this.ev;
      statEntity.ds = this.ds;
      statEntity.pid = this.pid;

      statEntity.id = this.id;
      statEntity.groupId = this.groupId;
      statEntity.type = this.type != null ? this.type :
          StatConstants.TYPE_CLICK_C;
      statEntity.sessionId = this.sessionId != null ? this.sessionId :
          GVideoStatManager.getInstance().getSessionId();
      statEntity.timestamp = this.timestamp != null ? this.timestamp :
          new Date();
      statEntity.realtime = this.realtime;

      return statEntity;
    }
  }
}
