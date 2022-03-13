package com.jxntv.record.recorder.process;

import android.media.MediaExtractor;
import android.media.MediaMuxer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by huangwei on 2018/4/8 0008.
 */

public class AudioProcessThread extends Thread{

    private String mVideoPath;
    private Exception mException;
    private MediaMuxer mMuxer;
    private int mMuxerAudioTrackIndex;
    private MediaExtractor mExtractor;
    private CountDownLatch mMuxerStartLatch;

    public AudioProcessThread(String videoPath, MediaMuxer muxer,
                              int muxerAudioTrackIndex,
                              CountDownLatch muxerStartLatch

    ) {
        super("VideoProcessDecodeThread");
        mVideoPath = videoPath;
        mMuxer = muxer;
        mMuxerAudioTrackIndex = muxerAudioTrackIndex;
        mExtractor = new MediaExtractor();
        mMuxerStartLatch = muxerStartLatch;
    }

    @Override
    public void run() {
        super.run();
        try {
            doProcessAudio();
        } catch (Exception e) {
            mException = e;
        } finally {
            mExtractor.release();
        }
    }

    private void doProcessAudio() throws Exception {
        mExtractor.setDataSource(mVideoPath);
        int audioTrackIndex = VideoProcessUtils.selectTrack(mExtractor, true);
        if (audioTrackIndex >= 0) {
            //处理音频
            mExtractor.selectTrack(audioTrackIndex);
            boolean await = mMuxerStartLatch.await(3, TimeUnit.SECONDS);
            if (!await) {
                throw new TimeoutException("wait muxerStartLatch timeout!");
            }
            VideoProcessUtils.writeAudioTrack(mExtractor, audioTrackIndex, mMuxer, mMuxerAudioTrackIndex);
        }

    }

    public Exception getException() {
        return mException;
    }

}