package com.jxntv.home.splash.db.entitiy;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 闪屏页广告数据库实体
 */
@Entity(tableName = "splash_ad_table")
public class SplashAdEntity {
    /** 推广位id */
    @NonNull
    @PrimaryKey
    @ColumnInfo()
    public String extendId;

    /** 广告资源url */
    @NonNull
    @ColumnInfo(name = "adSourceUrl")
    public String adSourceUrl;

    /** 广告跳转url */
    @ColumnInfo(name = "adUrl")
    public String adUrl;

    /** 广告图倒数秒数 */
    @ColumnInfo(name = "countDownSec")
    public int countDownSec;

    /** 广告图倒数样式 */
    @ColumnInfo(name = "countDownStyle")
    public int countDownStyle;

    /** 推广描述 */
    @ColumnInfo(name = "description")
    public String description;

    /** 挑战样式 */
    @ColumnInfo(name = "jumpStyle")
    public int jumpStyle;

    /** 推广标题 */
    @ColumnInfo(name = "title")
    public String title;

    /** 资源类型 0:底图，1：图片，2：视频 */
    @ColumnInfo(name = "sourceType")
    public int sourceType;

    /** 广告开始显示时间 */
    @ColumnInfo(name = "startTime")
    public Date startTime;

    /** 广告结束显示时间 */
    @ColumnInfo(name = "endTime")
    public Date endTime;

    /** 资源权重 */
    @ColumnInfo(name = "weight")
    public int weight;

    /** 资源Md5 */
    @ColumnInfo(name = "md5")
    public String md5;

    /** 广告显示持续时间 */
    @ColumnInfo(name = "durationSec")
    public int durationSec;

    /** 展示样式 1全屏；2半屏； */
    @SerializedName("showType")
    @ColumnInfo
    public int showStyle;
    /** 广告角标 1展示；0隐藏 */
    @ColumnInfo
    public int isAd;
    /** 资源id */
    @ColumnInfo
    public String mediaId;
    /** 资源类型 */
    @ColumnInfo
    public int mediaType;


    /**
     * 构造函数
     */
    public SplashAdEntity() {
    }

    public String getHashKey() {
        return extendId + adSourceUrl;
    }


    @NonNull
    public String getAdSourceUrl() {
        return adSourceUrl;
    }

    public void setAdSourceUrl(@NonNull String adSourceUrl) {
        this.adSourceUrl = adSourceUrl;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public int getCountDownSec() {
        return countDownSec;
    }

    public void setCountDownSec(int countDownSec) {
        this.countDownSec = countDownSec;
    }

    public int getCountDownStyle() {
        return countDownStyle;
    }

    public void setCountDownStyle(int countDownStyle) {
        this.countDownStyle = countDownStyle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getJumpStyle() {
        return jumpStyle;
    }

    public void setJumpStyle(int jumpStyle) {
        this.jumpStyle = jumpStyle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(int durationSec) {
        this.durationSec = durationSec;
    }


}


