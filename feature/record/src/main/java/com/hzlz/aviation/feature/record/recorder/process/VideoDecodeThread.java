package com.hzlz.aviation.feature.record.recorder.process;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;

import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.record.recorder.process.surface.InputSurface;
import com.hzlz.aviation.feature.record.recorder.process.surface.OutputSurface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by huangwei on 2018/4/8 0008.
 */

public class VideoDecodeThread extends Thread {
    private MediaExtractor mExtractor;
    private MediaCodec mDecoder;
    private AtomicBoolean mDecodeDone;
    private Exception mException;
    private int mVideoIndex;
    private VideoEncodeThread mVideoEncodeThread;
    private InputSurface mInputSurface;
    private OutputSurface mOutputSurface;
    private Integer mDstFrameRate;
    private Integer mSrcFrameRate;
    private int mRotate;
    private float mWidthRate;
    private float mHeightRate;

    public VideoDecodeThread(VideoEncodeThread videoEncodeThread, MediaExtractor extractor,
                             @Nullable Integer srcFrameRate, @Nullable Integer dstFrameRate,
                             int videoIndex, AtomicBoolean decodeDone, int rotate,
                             float widthRate, float heightRate
    ) {
        super("VideoProcessDecodeThread");
        mExtractor = extractor;
        mVideoIndex = videoIndex;
        mDecodeDone = decodeDone;
        mVideoEncodeThread = videoEncodeThread;
        mDstFrameRate = dstFrameRate;
        mSrcFrameRate = srcFrameRate;
        mRotate = rotate;
        mWidthRate = widthRate;
        mHeightRate = heightRate;
    }

    @Override
    public void run() {
        super.run();
        try {
            doDecode();
        } catch (Exception e) {
            mException = e;
        } finally {
            if (mInputSurface != null) {
                mInputSurface.release();
            }
            if (mOutputSurface != null) {
                mOutputSurface.release();
            }
            try {
                if (mDecoder != null) {
                    mDecoder.stop();
                    mDecoder.release();
                }
            } catch (Exception e) {
                mException = mException == null ? e : mException;
            }
        }
    }

    private void doDecode() throws IOException {
        CountDownLatch eglContextLatch = mVideoEncodeThread.getEglContextLatch();
        try {
            boolean await = eglContextLatch.await(20, TimeUnit.SECONDS);
            if (!await) {
                mException = new TimeoutException("wait eglContext timeout!");
                return;
            }
        } catch (InterruptedException e) {
            mException = e;
            return;
        }
        Surface encodeSurface = mVideoEncodeThread.getSurface();
        mInputSurface = new InputSurface(encodeSurface);
        mInputSurface.makeCurrent();

        MediaFormat inputFormat = mExtractor.getTrackFormat(mVideoIndex);

        //初始化解码器
        mDecoder = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME));
        mOutputSurface = new OutputSurface(mRotate, mWidthRate, mHeightRate);
        mDecoder.configure(inputFormat, mOutputSurface.getSurface(), null, 0);
        mDecoder.start();
        //丢帧判断
        int frameIndex = 0;

        //开始解码
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        boolean decoderDone = false;
        boolean inputDone = false;
        long videoStartTimeUs = -1;
        int decodeTryAgainCount = 0;


        ByteBuffer inputBuf = null;
        while (!decoderDone) {
            //还有帧数据，输入解码器
            if (!inputDone) {
                boolean eof = false;
                int index = mExtractor.getSampleTrackIndex();
                if (index == mVideoIndex) {
                    int inputBufIndex = mDecoder.dequeueInputBuffer(VideoProcessUtils.TIMEOUT_USEC);
                    if (inputBufIndex >= 0) {
                        inputBuf = mDecoder.getInputBuffer(inputBufIndex);
                        if (inputBuf == null) {
                            continue;
                        }
                        int chunkSize = mExtractor.readSampleData(inputBuf, 0);
                        if (chunkSize < 0) {
                            mDecoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            decoderDone = true;
                        } else {
                            long sampleTime = mExtractor.getSampleTime();
                            mDecoder.queueInputBuffer(inputBufIndex, 0, chunkSize, sampleTime, 0);
                            mExtractor.advance();
                        }
                    }
                } else if (index == -1) {
                    eof = true;
                }

                if (eof) {
                    //解码输入结束
                    int inputBufIndex = mDecoder.dequeueInputBuffer(VideoProcessUtils.TIMEOUT_USEC);
                    if (inputBufIndex >= 0) {
                        mDecoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    }
                }
            }
            boolean decoderOutputAvailable = !decoderDone;
            if (decoderDone) {
            }
            while (decoderOutputAvailable) {
                int outputBufferIndex = mDecoder.dequeueOutputBuffer(info, VideoProcessUtils.TIMEOUT_USEC);
                if (inputDone && outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    decodeTryAgainCount++;
                    if (decodeTryAgainCount > 10) {
                        //小米2上出现BUFFER_FLAG_END_OF_STREAM之后一直tryAgain的问题
                        decoderDone = true;
                        break;
                    }
                } else {
                    decodeTryAgainCount = 0;
                }
                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    break;
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = mDecoder.getOutputFormat();
                } else if (outputBufferIndex < 0) {
                    //ignore
                } else {
                    boolean doRender = true;
                    //解码数据可用
                    if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        decoderDone = true;
                        mDecoder.releaseOutputBuffer(outputBufferIndex, false);
                        break;
                    }
                    frameIndex++;
                    mDecoder.releaseOutputBuffer(outputBufferIndex, doRender);
                    if (doRender) {
                        boolean errorWait = false;
                        try {
                            mOutputSurface.awaitNewImage();
                        } catch (Exception e) {
                            errorWait = true;
                        }
                        if (!errorWait) {
                            if (videoStartTimeUs == -1) {
                                videoStartTimeUs = info.presentationTimeUs;
                            }
                            mOutputSurface.drawImage(false);
                            long presentationTimeNs = (info.presentationTimeUs - videoStartTimeUs) * 1000;
                            mInputSurface.setPresentationTime(presentationTimeNs);
                            mInputSurface.swapBuffers();
                            break;
                        }
                    }
                }
            }
        }
        mDecodeDone.set(true);
    }

    public Exception getException() {
        return mException;
    }
}