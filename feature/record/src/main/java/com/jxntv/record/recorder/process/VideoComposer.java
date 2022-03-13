package com.jxntv.record.recorder.process;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

/**
 * 视频合成辅助类
 */
public class VideoComposer {
    private final String TAG = "VideoComposer";
    private List<File> mVideoList;
    private String mOutFilename;

    private MediaMuxer mMuxer;
    private ByteBuffer mReadBuf;
    private int mOutAudioTrackIndex;
    private int mOutVideoTrackIndex;
    private MediaFormat mAudioFormat;
    private MediaFormat mVideoFormat;
    private int mRotation;

    /**
     * 构造函数
     */
    public VideoComposer(List<File> videoList, String outFilename) {
        this(videoList, outFilename, 0);
    }

    /**
     * 构造函数
     */
    public VideoComposer(List<File> videoList, String outFilename, int rotation) {
        mVideoList = videoList;
        this.mOutFilename = outFilename;
        mReadBuf = ByteBuffer.allocate(1048576);
        mRotation = rotation;
    }

    /**
     * 合成视频
     */
    public boolean joinVideo() {
        boolean getAudioFormat = false;
        boolean getVideoFormat = false;
        Iterator<File> videoIterator = mVideoList.iterator();

        String videoPath;
        while(videoIterator.hasNext()) {
            videoPath = videoIterator.next().getAbsolutePath();
            MediaExtractor extractor = new MediaExtractor();

            try {
                extractor.setDataSource(videoPath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            int trackIndex;
            if(!getVideoFormat) {
                trackIndex = VideoProcessUtils.selectTrack(extractor, false);
                if(trackIndex < 0) {
                    Log.e(TAG, "No video track found in " + videoPath);
                } else {
                    extractor.selectTrack(trackIndex);
                    mVideoFormat = extractor.getTrackFormat(trackIndex);
                    getVideoFormat = true;
                }
            }

            if(!getAudioFormat) {
                trackIndex = VideoProcessUtils.selectTrack(extractor, true);
                if(trackIndex < 0) {
                    Log.e(TAG, "No audio track found in " + videoPath);
                } else {
                    extractor.selectTrack(trackIndex);
                    mAudioFormat = extractor.getTrackFormat(trackIndex);
                    getAudioFormat = true;
                }
            }

            extractor.release();
            if(getVideoFormat && getAudioFormat) {
                break;
            }
        }

        try {
            mMuxer = new MediaMuxer(this.mOutFilename, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            if (mRotation != 0) {
                mMuxer.setOrientationHint(mRotation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(getVideoFormat) {
            mOutVideoTrackIndex = mMuxer.addTrack(mVideoFormat);
        }
        if(getAudioFormat) {
            mOutAudioTrackIndex = mMuxer.addTrack(mAudioFormat);
        }
        try {
            mMuxer.start();
        } catch (Exception e) {
            int as = 3;
        }


        long ptsOffset = 0L;
        Iterator<File> trackIndex = mVideoList.iterator();
        while(trackIndex.hasNext()) {
            videoPath = trackIndex.next().getAbsolutePath();
            boolean hasVideo = true;
            boolean hasAudio = true;
            MediaExtractor videoExtractor = new MediaExtractor();

            try {
                videoExtractor.setDataSource(videoPath);
            } catch (Exception var27) {
                var27.printStackTrace();
            }

            int inVideoTrackIndex = VideoProcessUtils.selectTrack(videoExtractor, false);
            if(inVideoTrackIndex < 0) {
                hasVideo = false;
            } else {
                videoExtractor.selectTrack(inVideoTrackIndex);
            }

            MediaExtractor audioExtractor = new MediaExtractor();

            try {
                audioExtractor.setDataSource(videoPath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int inAudioTrackIndex = VideoProcessUtils.selectTrack(audioExtractor, true);
            if(inAudioTrackIndex < 0) {
                hasAudio = false;
            } else {
                audioExtractor.selectTrack(inAudioTrackIndex);
            }

            boolean bMediaDone = false;
            long presentationTimeUs = 0L;
            long audioPts = 0L;
            long videoPts = 0L;

            while(!bMediaDone) {
                if(!hasVideo && !hasAudio) {
                    break;
                }

                int outTrackIndex;
                MediaExtractor extractor;
                int currenttrackIndex;
                if((!hasVideo || audioPts - videoPts <= 50000L) && hasAudio) {
                    currenttrackIndex = inAudioTrackIndex;
                    outTrackIndex = mOutAudioTrackIndex;
                    extractor = audioExtractor;
                } else {
                    currenttrackIndex = inVideoTrackIndex;
                    outTrackIndex = mOutVideoTrackIndex;
                    extractor = videoExtractor;
                }

                mReadBuf.rewind();
                int chunkSize = extractor.readSampleData(mReadBuf, 0);//读取帧数据
                if(chunkSize < 0) {
                    if(currenttrackIndex == inVideoTrackIndex) {
                        hasVideo = false;
                    } else if(currenttrackIndex == inAudioTrackIndex) {
                        hasAudio = false;
                    }
                } else {
                    if(extractor.getSampleTrackIndex() != currenttrackIndex) {
                        Log.e(TAG, "WEIRD: got sample from track "
                            + extractor.getSampleTrackIndex() + ", expected " + currenttrackIndex);
                    }

                    presentationTimeUs = extractor.getSampleTime();//读取帧的pts
                    if(currenttrackIndex == inVideoTrackIndex) {
                        videoPts = presentationTimeUs;
                    } else {
                        audioPts = presentationTimeUs;
                    }

                    BufferInfo info = new BufferInfo();
                    info.offset = 0;
                    info.size = chunkSize;
                    info.presentationTimeUs = ptsOffset + presentationTimeUs;//pts重新计算
                    if((extractor.getSampleFlags() & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
                        info.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                    }

                    mReadBuf.rewind();
                    mMuxer.writeSampleData(outTrackIndex, mReadBuf, info);//写入文件
                    extractor.advance();
                }
            }

            ptsOffset += videoPts > audioPts ? videoPts : audioPts;
            ptsOffset += 10000L;

            videoExtractor.release();
            audioExtractor.release();
        }

        if(mMuxer != null) {
            try {
                mMuxer.stop();
                mMuxer.release();
            } catch (Exception e) {
                Log.e(TAG, "Muxer close error. No data was written");
            }

            mMuxer = null;
        }

        Log.i(TAG, "video join finished");
        return true;
    }

}
