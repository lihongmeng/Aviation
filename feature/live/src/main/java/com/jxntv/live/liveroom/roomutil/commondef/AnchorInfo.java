package com.jxntv.live.liveroom.roomutil.commondef;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

import com.google.gson.annotations.SerializedName;
import com.jxntv.base.adapter.IAdapterModel;

import java.util.List;

public class AnchorInfo implements Parcelable, IAdapterModel {

    public static final int CONNECT_DEFAULT = 0;
    public static final int CONNECT_AGREE = 1;
    public static final int CONNECT_INVALID = 2;

    /**
     * 用户ID
     */
    @SerializedName(value = "userID", alternate = {"userid"})
    public String userid;

    /**
     * 用户昵称
     */
    public String userName;

    /**
     * 用户头像地址
     */
    public String userAvatar;

    /**
     * 低时延拉流地址（带防盗链key）
     */
    public String accelerateURL;

    /**
     * 此用户是不是自己的粉丝
     */
    public boolean isFans;

    /**
     * 此用户是否是认证用户
     */
    public boolean isUserAuthentication;

    /**
     * 申请连麦的理由
     */
    public String applyRequestReason;

    /**
     * 连麦当前的位置
     */
    public Integer index;

    /**
     * 音量信息
     */
    public boolean mute;

    /**
     * 连麦状态
     */
    public int connectState;

    public long time = System.currentTimeMillis();


    public AnchorInfo() {

    }

    public AnchorInfo(int index) {
        this.index = index;
    }

    public AnchorInfo(OnlineAnchorPosition onlineAnchorPosition) {
        if (onlineAnchorPosition == null) {
            return;
        }
        this.index = onlineAnchorPosition.index;
        this.userid = onlineAnchorPosition.userid;
        this.mute = onlineAnchorPosition.mute;
        this.userAvatar = onlineAnchorPosition.userAvatar;
        this.userName = onlineAnchorPosition.userName;
    }

    public AnchorInfo(
            String userid,
            String userName,
            String userAvatar,
            String accelerateURL,
            boolean isFans,
            boolean isUserAuthentication,
            String applyRequestReason,
            Integer index
    ) {
        this.userid = userid;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.accelerateURL = accelerateURL;
        this.isFans = isFans;
        this.isUserAuthentication = isUserAuthentication;
        this.applyRequestReason = applyRequestReason;
        this.index = index;
    }

    public AnchorInfo(String userID, String userName, String userAvatar, String accelerateURL) {
        this.userid = userID;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.accelerateURL = accelerateURL;
        this.isFans = false;
        this.isUserAuthentication =false;
        this.applyRequestReason = "";
        this.index = 0;
    }

    protected AnchorInfo(Parcel in) {
        userid = in.readString();
        userName = in.readString();
        userAvatar = in.readString();
        accelerateURL = in.readString();
        isFans = in.readByte() != 0;
        isUserAuthentication = in.readByte() != 0;
        applyRequestReason = in.readString();
        index = in.readInt();
        mute = in.readByte() != 0;
        connectState = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(userName);
        dest.writeString(userAvatar);
        dest.writeString(accelerateURL);
        dest.writeByte((byte) (isFans ? 1 : 0));
        dest.writeByte((byte) (isUserAuthentication ? 1 : 0));
        dest.writeString(applyRequestReason);
        dest.writeInt(index);
        dest.writeByte((byte) (mute ? 1 : 0));
        dest.writeInt(index);
        dest.writeInt((byte) (connectState));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AnchorInfo> CREATOR = new Creator<AnchorInfo>() {
        @Override
        public AnchorInfo createFromParcel(Parcel in) {
            return new AnchorInfo(in);
        }

        @Override
        public AnchorInfo[] newArray(int size) {
            return new AnchorInfo[size];
        }
    };

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(userid)) {
            return "".hashCode();
        }
        return userid.hashCode();
    }

    @Override
    public String toString() {
        return "AnchorInfo{" +
                "userid='" + userid + '\'' +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", accelerateURL='" + accelerateURL + '\'' +
                ", isFans=" + isFans +
                ", isUserAuthentication=" + isUserAuthentication +
                ", applyRequestReason='" + applyRequestReason + '\'' +
                ", index=" + index +
                ", mute=" + mute +
                ", hadBeInvited=" + connectState +
                '}';
    }

    @Override
    public void setModelPosition(int position) {
    }

    @NonNull
    @Override
    public ObservableInt getModelPosition() {
        return null;
    }

    /**
     * 初始化
     * 例如在连麦观众退出直播间
     * 那在数据源中观众位置的数据需要重置
     */
    public void init() {
        this.userid = "";
        this.userName = "";
        this.userAvatar = "";
        this.accelerateURL = "";
        this.isFans = false;
        this.isUserAuthentication = false;
        this.applyRequestReason = "";
        this.mute = false;
    }

    public static boolean isListDifferent(List<AnchorInfo> left, List<AnchorInfo> right) {
        if (left == null || right == null) {
            return true;
        }
        for (int index = 0; index < 6; index++) {
            AnchorInfo leftItem = left.get(index);
            AnchorInfo rightItem = right.get(index);
            if (!TextUtils.equals(leftItem.userid, rightItem.userid)
                    || !TextUtils.equals(leftItem.userName, rightItem.userName)
                    || !TextUtils.equals(leftItem.userAvatar, rightItem.userAvatar)
                    || leftItem.isUserAuthentication != rightItem.isUserAuthentication
                    || leftItem.isFans != rightItem.isFans
                    || leftItem.mute != rightItem.mute
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean isUseful(OnlineAnchorPosition onlineAnchorPosition) {
        if (onlineAnchorPosition == null) {
            return false;
        }
        return !TextUtils.equals(userid, onlineAnchorPosition.userid)
                || !TextUtils.equals(userName, onlineAnchorPosition.userName)
                || !TextUtils.equals(userAvatar, onlineAnchorPosition.userAvatar)
                || mute != onlineAnchorPosition.mute;
    }

    public boolean isUseless() {
        return TextUtils.isEmpty(userid);
    }

}
