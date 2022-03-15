package com.hzlz.aviation.feature.live.video.widget;

import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.live.R;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Module:   TCVideoView
 * <p>
 * Function: 视频播放View的封装类
 * <p>
 * 1. 封装了主播连麦、观众观看的View。
 * <p>
 * 2. loading显示、踢出按钮等
 */
public class TCVideoView {

    public TXCloudVideoView videoView;
    public View loadingBkg;
    public ImageView loadingImg,kickButton;
    public String userID;
    public View rootLayout;
    boolean isUsed;

    public TCVideoView(TXCloudVideoView view, ImageView button, View loadingBkg, ImageView loadingImg, View rootLayout,
                    final OnRoomViewListener l) {
        this.videoView = view;
        this.videoView.setVisibility(View.GONE);
        this.loadingBkg = loadingBkg;
        this.loadingImg = loadingImg;
        this.isUsed = false;
        this.kickButton = button;
        this.rootLayout = rootLayout;
        this.kickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = TCVideoView.this.userID;
                if (userID != null && l != null) {
                    l.onKickUser(userID);
                }
            }
        });
    }

    public void startLoading() {
        kickButton.setVisibility(View.INVISIBLE);
        loadingBkg.setVisibility(View.VISIBLE);
        loadingImg.setVisibility(View.VISIBLE);
        loadingImg.setImageResource(R.drawable.universal_refresh_anim);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getDrawable();
        ad.start();
    }

    public void stopLoading(boolean showKickoutBtn) {
        String userId = PluginManager.get(AccountPlugin.class).getUserId();
        showKickoutBtn = (!TextUtils.isEmpty(userID) && userID.equalsIgnoreCase(userId)) || showKickoutBtn;
        kickButton.setVisibility(showKickoutBtn ? View.VISIBLE : View.GONE);
        loadingBkg.setVisibility(View.GONE);
        loadingImg.setVisibility(View.GONE);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    public void stopLoading() {
        kickButton.setVisibility(View.GONE);
        loadingBkg.setVisibility(View.GONE);
        loadingImg.setVisibility(View.GONE);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    public void setUsed(boolean used) {
        videoView.setVisibility(used ? View.VISIBLE : View.GONE);
        rootLayout.setVisibility(used ? View.VISIBLE : View.GONE);
        if (!used) {
            stopLoading(false);
        }
        this.isUsed = used;
    }

    public interface OnRoomViewListener {
        void onKickUser(String userId);
    }
}

