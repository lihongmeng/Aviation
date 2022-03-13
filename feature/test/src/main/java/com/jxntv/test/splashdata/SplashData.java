package com.jxntv.test.splashdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.jxntv.base.adapter.AbstractAdapterModel;
import com.jxntv.home.splash.ad.SplashItemHelper;
import com.jxntv.home.splash.db.entitiy.SplashAdEntity;
import java.io.File;

public class SplashData extends AbstractAdapterModel implements Parcelable {
  public SplashAdEntity mEntity;

  public SplashData(SplashAdEntity mEntity) {
    this.mEntity = mEntity;
  }

  protected SplashData(Parcel in) {
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<SplashData> CREATOR = new Creator<SplashData>() {
    @Override
    public SplashData createFromParcel(Parcel in) {
      return new SplashData(in);
    }

    @Override
    public SplashData[] newArray(int size) {
      return new SplashData[size];
    }
  };

  public String getText() {
    StringBuilder sb = new StringBuilder();
    sb.append("ExtendId:").append(mEntity.extendId).append("\n");
    sb.append("Title:").append(mEntity.title).append("\n");
    sb.append("Type:").append(mEntity.sourceType).append("\n");
    sb.append("adSourceUrl:").append(mEntity.adSourceUrl).append("\n");
    sb.append("adUrl:").append(mEntity.adUrl).append("\n");
    sb.append("StartTime:").append(mEntity.startTime).append("\n");
    sb.append("EndTime:").append(mEntity.endTime).append("\n");
    sb.append("showTime:").append(mEntity.durationSec).append("\n");
    sb.append("countDownTime:").append(mEntity.countDownSec).append("\n");
    sb.append("mediaId:").append(mEntity.mediaId).append("\n");
    sb.append("mediaType:").append(mEntity.mediaType).append("\n");
    sb.append("showStyle:").append(mEntity.showStyle).append("\n");
    sb.append("isAd:").append(mEntity.isAd).append("\n");
    sb.append("localFile:").append(getSource()).append("\n");

    //sb.append("EndTime:").append(mEntity.endTime).append("\n");
    //sb.append("EndTime:").append(mEntity.endTime).append("\n");
    //sb.append("EndTime:").append(mEntity.endTime).append("\n");

    return sb.toString();
  }

  public String getSource() {
    File file = SplashItemHelper.getSplashDataFile(mEntity.md5, mEntity.adSourceUrl);
    if (file != null && file.isFile() && file.exists()) {
      return file.getAbsolutePath();
    }
    return "";
  }
}
