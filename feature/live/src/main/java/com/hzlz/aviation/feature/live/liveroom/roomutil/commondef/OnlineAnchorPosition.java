package com.hzlz.aviation.feature.live.liveroom.roomutil.commondef;

import java.util.ArrayList;

/**
 * {@link AnchorInfo}的简化版
 * 目的是为了减少直播间传输过程中的流量损耗
 */
public class OnlineAnchorPosition {

    // 上麦的位置
    public Integer index;

    // 用户id
    public String userid;

    // 是否静音
    public boolean mute;

    // 用户头像
    public String userAvatar;

    // 用户昵称
    public String userName;

    public OnlineAnchorPosition() {

    }

    public OnlineAnchorPosition(AnchorInfo anchorInfo) {
        if (anchorInfo == null) {
            return;
        }
        this.index = anchorInfo.index;
        this.userid = anchorInfo.userid;
        this.mute = anchorInfo.mute;
        this.userAvatar = anchorInfo.userAvatar;
        this.userName = anchorInfo.userName;
    }

    public static ArrayList<OnlineAnchorPosition> change(ArrayList<AnchorInfo> anchorInfoArrayList) {
        ArrayList<OnlineAnchorPosition> result = new ArrayList<>();
        if (anchorInfoArrayList == null) {
            return result;
        }
        for (AnchorInfo anchorInfo : anchorInfoArrayList) {
            if (anchorInfo == null||anchorInfo.isUseless()) {
                continue;
            }
            result.add(new OnlineAnchorPosition(anchorInfo));
        }
        return result;
    }

    public static ArrayList<OnlineAnchorPosition> update(ArrayList<AnchorInfo> anchorInfoArrayList) {
        ArrayList<OnlineAnchorPosition> result = new ArrayList<>();
        if (anchorInfoArrayList == null) {
            return result;
        }
        for (AnchorInfo anchorInfo : anchorInfoArrayList) {
            if (anchorInfo == null) {
                continue;
            }
            result.add(new OnlineAnchorPosition(anchorInfo));
        }
        return result;
    }

}
