package com.hzlz.aviation.kernel.stat.stat.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * 通用埋点实体
 */
@Entity(tableName = "statGroup")
public class StatGroupEntity {
  public static final long EMPTY_ID = 0;


  @PrimaryKey(autoGenerate = true)
  public long id;
  /** 实时埋点 */
  @ColumnInfo
  public boolean realtime;

  /** 今视频号 */
  @ColumnInfo
  public String uid;
  /** 时间戳 */
  @ColumnInfo
  public Date timestamp;
  /** 网络类型，例如WIFI，4G，5G，UNKNOWN */
  @ColumnInfo
  public String networkType;
  /** 运营商信息，例如中国电信 */
  @ColumnInfo
  public String carrier;

  /** 设备终端deviceId */
  @ColumnInfo
  public String cid;
  /** 应用包名 */
  @ColumnInfo
  public String appName;
  /** 应用的版本 */
  @ColumnInfo
  public String appVersion;
  /** 渠道号 */
  @ColumnInfo
  public String channel;

  /** 设备制造商，例如Apple */
  @ColumnInfo
  public String manufacturer;
  /** 设备型号，例如iphone6 */
  @ColumnInfo
  public String model;
  /** 操作系统，例如iOS */
  @ColumnInfo
  public String os;
  /** 操作系统版本，例如8.1.1 */
  @ColumnInfo
  public String osVersion;

  public static final class Builder {
    private int id;
    private boolean realtime;
    private String uid;
    private String cid;
    private Date timestamp;
    private String networkType;
    private String carrier;
    private String appName;
    private String appVersion;
    private String channel;
    private String manufacturer;
    private String model;
    private String os;
    private String osVersion;

    private Builder() {
    }

    public static Builder aStatGroupEntity() {
      return new Builder();
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withRealtime(boolean realtime) {
      this.realtime = realtime;
      return this;
    }

    public Builder withUid(String uid) {
      this.uid = uid;
      return this;
    }

    public Builder withCid(String cid) {
      this.cid = cid;
      return this;
    }

    public Builder withTimestamp(Date timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder withNetworkType(String networkType) {
      this.networkType = networkType;
      return this;
    }

    public Builder withCarrier(String carrier) {
      this.carrier = carrier;
      return this;
    }

    public Builder withAppName(String appName) {
      this.appName = appName;
      return this;
    }

    public Builder withAppVersion(String appVersion) {
      this.appVersion = appVersion;
      return this;
    }

    public Builder withChannel(String channel) {
      this.channel = channel;
      return this;
    }

    public Builder withManufacturer(String manufacturer) {
      this.manufacturer = manufacturer;
      return this;
    }

    public Builder withModel(String model) {
      this.model = model;
      return this;
    }

    public Builder withOs(String os) {
      this.os = os;
      return this;
    }

    public Builder withOsVersion(String osVersion) {
      this.osVersion = osVersion;
      return this;
    }

    public StatGroupEntity build() {
      StatGroupEntity statGroupEntity = new StatGroupEntity();
      statGroupEntity.realtime = this.realtime;
      statGroupEntity.channel = this.channel;
      statGroupEntity.model = this.model;
      statGroupEntity.osVersion = this.osVersion;
      statGroupEntity.id = this.id;
      statGroupEntity.timestamp = this.timestamp;
      statGroupEntity.os = this.os;
      statGroupEntity.networkType = this.networkType;
      statGroupEntity.carrier = this.carrier;
      statGroupEntity.appName = this.appName;
      statGroupEntity.manufacturer = this.manufacturer;
      statGroupEntity.cid = this.cid;
      statGroupEntity.appVersion = this.appVersion;
      statGroupEntity.uid = this.uid;
      return statGroupEntity;
    }
  }
}
