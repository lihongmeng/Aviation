package com.jxntv.base.model.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * @author huangwei
 * date : 2021/12/15
 * desc : 图片
 **/
public class ImageModel implements Parcelable {

	public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
		@Override
		public ImageModel createFromParcel(Parcel in) {
			return new ImageModel(in);
		}

		@Override
		public ImageModel[] newArray(int size) {
			return new ImageModel[size];
		}
	};
	private String ossid;
	//地址
	private String url;
	private String fileSize;
	//高度
	private String height;
	//宽度
	private String weight;

	public ImageModel() {
	}

	protected ImageModel(Parcel in) {
		ossid = in.readString();
		url = in.readString();
		fileSize = in.readString();
		height = in.readString();
		weight = in.readString();
	}

	public String getOssid() {
		return ossid;
	}

	public void setOssid(String ossid) {
		this.ossid = ossid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public int getHeight() {
		try {
			return TextUtils.isEmpty(height) ? 0 : (int) Float.parseFloat(height);
		}catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	public void setHeight(int height) {
		this.height = height + "";
	}

	public int getWeight() {
		try {
			return TextUtils.isEmpty(weight) ? 0 : (int) Float.parseFloat(weight);
		}catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	public void setWeight(int weight) {
		this.weight = weight + "";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(ossid);
		parcel.writeString(url);
		parcel.writeString(fileSize);
		parcel.writeString(height);
		parcel.writeString(weight);
	}
}
