package com.jxntv.live.callback;

import android.view.View;

import com.jxntv.live.liveroom.roomutil.commondef.AnchorInfo;

public interface OnMicroConnectRequestListListener {
    void onCloseClick();
    void onAcceptClick(AnchorInfo anchorInfo, int position);
}
