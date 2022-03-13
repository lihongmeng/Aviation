package com.jxntv.live.video.widget;


import android.view.View;
import android.widget.ImageView;

import com.jxntv.live.databinding.ViewAnchorPlayerListBinding;
import com.jxntv.live.liveroom.MLVBLiveRoomImpl;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Module:   TCVideoViewMgr
 *
 * Function: 视频播放View的管理类
 *
 *  {@link TCVideoView}  的管理类
 */
public class TCVideoViewMgr {
    private List<TCVideoView> mVideoViews = new ArrayList<>();

    public TCVideoViewMgr(ViewAnchorPlayerListBinding binding, final TCVideoView.OnRoomViewListener l) {
        if (binding == null){
            return;
        }
        TXCloudVideoView[] videoViews = new TXCloudVideoView[MLVBLiveRoomImpl.MAX_JOIN_ANCHOR];
        videoViews[0] = binding.layout1.videoPlayer;
        videoViews[1] = binding.layout2.videoPlayer;
        videoViews[2] = binding.layout3.videoPlayer;
        videoViews[3] = binding.layout4.videoPlayer;

        ImageView[] kickoutBtns = new ImageView[MLVBLiveRoomImpl.MAX_JOIN_ANCHOR];
        kickoutBtns[0] = binding.layout1.btnKickOut;
        kickoutBtns[1] = binding.layout2.btnKickOut;
        kickoutBtns[2] = binding.layout3.btnKickOut;
        kickoutBtns[3] = binding.layout4.btnKickOut;

        View[] loadingBkgs = new View[MLVBLiveRoomImpl.MAX_JOIN_ANCHOR];
        loadingBkgs[0] = binding.layout1.loadingBackground;
        loadingBkgs[1] = binding.layout2.loadingBackground;
        loadingBkgs[2] = binding.layout3.loadingBackground;
        loadingBkgs[3] = binding.layout4.loadingBackground;

        ImageView[] loadingImgs = new ImageView[MLVBLiveRoomImpl.MAX_JOIN_ANCHOR];
        loadingImgs[0] = binding.layout1.loadingImageview;
        loadingImgs[1] = binding.layout2.loadingImageview;
        loadingImgs[2] = binding.layout3.loadingImageview;
        loadingImgs[3] = binding.layout4.loadingImageview;

        View[] rootView = new View[MLVBLiveRoomImpl.MAX_JOIN_ANCHOR];
        rootView[0] = binding.layout1.rootLayout;
        rootView[1] = binding.layout2.rootLayout;
        rootView[2] = binding.layout3.rootLayout;
        rootView[3] = binding.layout4.rootLayout;

        // 连麦拉流
        mVideoViews.add(new TCVideoView(videoViews[0], kickoutBtns[0], loadingBkgs[0], loadingImgs[0], rootView[0], l));
        mVideoViews.add(new TCVideoView(videoViews[1], kickoutBtns[1], loadingBkgs[1], loadingImgs[1], rootView[1], l));
        mVideoViews.add(new TCVideoView(videoViews[2], kickoutBtns[2], loadingBkgs[2], loadingImgs[2], rootView[2], l));
        mVideoViews.add(new TCVideoView(videoViews[3], kickoutBtns[3], loadingBkgs[3], loadingImgs[3], rootView[3], l));

    }
    public synchronized TCVideoView applyVideoView(String id) {
        if (id == null) {
            return null;
        }

        for (TCVideoView item : mVideoViews) {
            if (!item.isUsed) {
                item.setUsed(true);
                item.userID = id;
                return item;
            } else {
                if (item.userID != null && item.userID.equals(id)) {
                    item.setUsed(true);
                    return item;
                }
            }
        }
        return null;
    }

    public synchronized void recycleVideoView(String id){
        for (TCVideoView item : mVideoViews) {
            if (item.userID != null && item.userID.equals(id)){
                item.userID = null;
                item.setUsed(false);
            }
        }
    }

    public synchronized void recycleVideoView(){
        for (TCVideoView item : mVideoViews) {
            item.userID = null;
            item.setUsed(false);
        }
    }

    public synchronized void showLog(boolean show) {
        for (TCVideoView item : mVideoViews) {
            if (item.isUsed) {
                item.videoView.showLog(show);
            }
        }
    }

    public synchronized TCVideoView getFirstRoomView() {
        return mVideoViews.get(0);
    }
}
