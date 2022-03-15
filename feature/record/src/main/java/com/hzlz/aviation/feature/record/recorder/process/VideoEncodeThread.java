package com.hzlz.aviation.feature.record.recorder.process;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by huangwei on 2018/4/8 0008.
 */

public class VideoEncodeThread extends Thread {

    private final static int TIMEOUT_USEC = 2500;
    private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private AtomicBoolean mDecodeDone;
    private CountDownLatch mMuxerStartLatch;
    private Exception mException;
    private int mBitrate;
    private int mResultWidth;
    private int mResultHeight;
    private int mIFrameInterval;
    private int mFrameRate;
    private MediaExtractor mExtractor;
    private int mVideoIndex;
    private volatile CountDownLatch mEglContextLatch;
    private volatile Surface mSurface;

    public VideoEncodeThread(MediaExtractor extractor, MediaMuxer muxer,
                             int bitrate, int resultWidth, int resultHeight, int iFrameInterval,
                             int frameRate, int videoIndex,
                             AtomicBoolean decodeDone, CountDownLatch muxerStartLatch) {
        super("VideoProcessEncodeThread");
        mMuxer = muxer;
        mDecodeDone = decodeDone;
        mMuxerStartLatch = muxerStartLatch;
        mExtractor = extractor;
        mBitrate = bitrate;
        mResultHeight = resultHeight;
        mResultWidth = resultWidth;
        mIFrameInterval = iFrameInterval;
        mVideoIndex = videoIndex;
        mFrameRate = frameRate;
        mEglContextLatch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        super.run();
        try {
            doEncode();
        } catch (Exception e) {
            mException = e;
        } finally {
            try {
                if (mEncoder != null) {
                    mEncoder.stop();
                    mEncoder.release();
                }
            } catch (Exception e) {
                mException = mException == null ? e : mException;
            }
        }
    }

    private void doEncode() throws IOException {
        MediaFormat inputFormat = mExtractor.getTrackFormat(mVideoIndex);
        //初始化编码器
        int frameRate;
        if (mFrameRate > 0) {
            frameRate = mFrameRate;
        } else {
            frameRate = inputFormat.containsKey(MediaFormat.KEY_FRAME_RATE) ?
                inputFormat.getInteger(MediaFormat.KEY_FRAME_RATE) :VideoProcessUtils. DEFAULT_FRAME_RATE;
        }
        String mimeType = VideoProcessUtils.OUTPUT_MIME_TYPE;
        MediaFormat outputFormat = MediaFormat.createVideoFormat(mimeType, mResultWidth, mResultHeight);
        outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        outputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, mIFrameInterval);

        mEncoder = MediaCodec.createEncoderByType(mimeType);

        int maxBitrate = VideoProcessUtils.getMaxSupportBitrate(mEncoder,mimeType);
        if (maxBitrate > 0 && mBitrate > maxBitrate) {
            mBitrate = (int) (maxBitrate * 0.8f);//直接设置最大值小米2报错
        }
        outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, mBitrate);
        mEncoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
//        mInputSurface = new InputSurface(encodeSurface);
//        mInputSurface.makeCurrent();
        mEncoder.start();
        mEglContextLatch.countDown();

        boolean signalEncodeEnd = false;
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        int encodeTryAgainCount = 0;
        int videoTrackIndex = -5;
        boolean detectTimeError = false;
        final int VIDEO_FRAME_TIME_US = (int) (1000 * 1000f / frameRate);
        long lastVideoFrameTimeUs = -1;
        //开始编码
        //输出
        while (true) {
            if (mDecodeDone.get() && !signalEncodeEnd) {
                signalEncodeEnd = true;
                mEncoder.signalEndOfInputStream();
            }
            int outputBufferIndex = mEncoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
            if (signalEncodeEnd && outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                encodeTryAgainCount++;
                if (encodeTryAgainCount > 10) {
                    break;
                }
            } else {
                encodeTryAgainCount = 0;
            }
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                continue;
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mEncoder.getOutputFormat();
                if (videoTrackIndex == -5) {
                    videoTrackIndex = mMuxer.addTrack(newFormat);
                    mMuxer.start();
                    mMuxerStartLatch.countDown();
                }
            } else if (outputBufferIndex < 0) {
                //ignore
            } else {
                //编码数据可用
                ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
                if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM && info.presentationTimeUs < 0) {
                    info.presentationTimeUs = 0;
                }
                //写入视频
                if (!detectTimeError && lastVideoFrameTimeUs != -1 && info.presentationTimeUs < lastVideoFrameTimeUs + VIDEO_FRAME_TIME_US / 2) {
                    //某些视频帧时间会出错
                    detectTimeError = true;
                }
                if (detectTimeError) {
                    info.presentationTimeUs = lastVideoFrameTimeUs + VIDEO_FRAME_TIME_US;
                    detectTimeError = false;
                }
                if (info.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                    lastVideoFrameTimeUs = info.presentationTimeUs;
                }
                mMuxer.writeSampleData(videoTrackIndex, outputBuffer, info);
                mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    break;
                }
            }
        }
    }


    public Surface getSurface() {
        return mSurface;
    }

    public CountDownLatch getEglContextLatch() {
        return mEglContextLatch;
    }

    public Exception getException() {
        return mException;
    }

}
