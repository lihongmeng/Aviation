package com.tencent.qcloud.tuikit.tuichat.component.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.jxntv.android.liteav.GVideoView;
import com.jxntv.android.liteav.player.GVideoPlayerListener;
import com.tencent.qcloud.tuikit.tuichat.R;
import com.tencent.qcloud.tuikit.tuichat.TUIChatConstants;

public class VideoViewActivity extends Activity {

    private GVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat_video_view);
        videoView = findViewById(R.id.video_view);
        String imagePath = getIntent().getStringExtra(TUIChatConstants.CAMERA_IMAGE_PATH);
        Uri videoUri = getIntent().getParcelableExtra(TUIChatConstants.CAMERA_VIDEO_PATH);
        videoView.showCover(imagePath);
        videoView.startPlay(videoUri.getPath());
        videoView.setKSVideoPlayerListener(new GVideoPlayerListener() {

            @Override
            public void onBackPressed() {
                if (!videoView.handleBackPressed()){
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView!=null){
            videoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView!=null){
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView!=null){
            videoView.release();
        }
    }
}
