package com.hzlz.aviation.feature.live.callback;

import com.hzlz.aviation.feature.live.liveroom.roomutil.commondef.AnchorInfo;

public interface OnMicroConnectRequestListListener {
    void onCloseClick();
    void onAcceptClick(AnchorInfo anchorInfo, int position);
}
