package com.jxntv.record.recorder.process;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoScaleHelper {

  private static final int OUT_WIDTH = 720;
  private static final int OUT_HEIGHT = 1280;

  public void scaleVideo(String videoPath, String outputVideoPath) throws Exception {
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(videoPath);
    int rotationValue = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
    int oriBitrate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
    int oriWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
    int oriHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
    retriever.release();

    float widthRate = 1f;
    float heightRate = 1f;

    if (rotationValue == 90 || rotationValue == 270) {
      int temp = oriWidth;
      oriWidth = oriHeight;
      oriHeight = temp;
    }

    if (oriWidth > 0 && oriHeight > 0) {
      long tempX = oriWidth * OUT_HEIGHT;
      long tempY = oriHeight * OUT_WIDTH;
      if (tempX > tempY) {
        heightRate = 1f * tempY / tempX;
      } else {
        widthRate = 1f * tempX / tempY;
      }
    }

    MediaExtractor extractor = new MediaExtractor();
    extractor.setDataSource(videoPath);
    int videoIndex = VideoProcessUtils.selectTrack(extractor, false);
    int audioIndex = VideoProcessUtils.selectTrack(extractor, true);

    MediaMuxer mediaMuxer = new MediaMuxer(outputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    int muxerAudioTrackIndex = 0;

    if (audioIndex >= 0) {
      MediaFormat audioTrackFormat = extractor.getTrackFormat(audioIndex);
      muxerAudioTrackIndex = mediaMuxer.addTrack(audioTrackFormat);
    }
    extractor.selectTrack(videoIndex);
    extractor.seekTo(0, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

    AtomicBoolean decodeDone = new AtomicBoolean(false);
    CountDownLatch muxerStartLatch = new CountDownLatch(1);
    VideoEncodeThread encodeThread = new VideoEncodeThread(extractor, mediaMuxer, oriBitrate,
        OUT_WIDTH, OUT_HEIGHT, 0, VideoProcessUtils.DEFAULT_FRAME_RATE, videoIndex,
        decodeDone, muxerStartLatch);
    int srcFrameRate = VideoProcessUtils.getFrameRate(videoPath);
    if (srcFrameRate <= 0) {
      srcFrameRate = (int) Math.ceil(VideoProcessUtils.getAveFrameRate(videoPath));
    }
    VideoDecodeThread decodeThread = new VideoDecodeThread(encodeThread, extractor, srcFrameRate,
        VideoProcessUtils.DEFAULT_FRAME_RATE , videoIndex, decodeDone, rotationValue, widthRate, heightRate);

    AudioProcessThread audioProcessThread = new AudioProcessThread(videoPath, mediaMuxer,
        muxerAudioTrackIndex, muxerStartLatch);
    decodeThread.start();
    encodeThread.start();
    audioProcessThread.start();
    try {
      decodeThread.join();
      encodeThread.join();
      audioProcessThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    try {
      mediaMuxer.release();
      extractor.release();
    } catch (Exception e2) {
    }
  }
}
