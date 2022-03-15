package com.hzlz.aviation.feature.record.recorder.process;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.text.TextUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoProcessUtils {

  public static int DEFAULT_FRAME_RATE = 20;
  public final static int TIMEOUT_USEC = 2500;
  public final static String OUTPUT_MIME_TYPE = "video/avc";

  public static int selectTrack(MediaExtractor extractor, boolean audio) {
    int numTracks = extractor.getTrackCount();
    for (int i = 0; i < numTracks; i++) {
      MediaFormat format = extractor.getTrackFormat(i);
      String mime = format.getString(MediaFormat.KEY_MIME);
      if (TextUtils.isEmpty(mime)) {
        continue;
      }
      if (audio) {
        if (mime.startsWith("audio/")) {
          return i;
        }
      } else {
        if (mime.startsWith("video/")) {
          return i;
        }
      }
    }
    return -5;
  }

  public static int getMaxSupportBitrate(MediaCodec codec, String mime) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
      return -1;
    }
    try {
      MediaCodecInfo codecInfo = codec.getCodecInfo();
      MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mime);
      Integer maxBitrate = null;
      maxBitrate = capabilities.getVideoCapabilities().getBitrateRange().getUpper();
      return maxBitrate;
    } catch (Exception e) {
      return -1;
    }
  }

  public static int getFrameRate(String videoPath) {
    MediaExtractor extractor = new MediaExtractor();
    try {
      extractor.setDataSource(videoPath);
      int trackIndex = selectTrack(extractor, false);
      MediaFormat format = extractor.getTrackFormat(trackIndex);
      return format.containsKey(MediaFormat.KEY_FRAME_RATE) ? format.getInteger(MediaFormat.KEY_FRAME_RATE) : -1;
    } catch (IOException e) {
      return -1;
    } finally {
      extractor.release();
    }
  }

  public static float getAveFrameRate(String videoPath) throws IOException {
    MediaExtractor extractor = new MediaExtractor();
    extractor.setDataSource(videoPath);
    int trackIndex = selectTrack(extractor, false);
    extractor.selectTrack(trackIndex);
    long lastSampleTimeUs = 0;
    int frameCount = 0;
    while (true) {
      long sampleTime = extractor.getSampleTime();
      if (sampleTime < 0) {
        break;
      } else {
        lastSampleTimeUs = sampleTime;
      }
      frameCount++;
      extractor.advance();
    }
    extractor.release();
    return frameCount / (lastSampleTimeUs / 1000f / 1000f);
  }

  public static long writeAudioTrack(MediaExtractor extractor, int audioTrack,
                                     MediaMuxer mediaMuxer, int muxerAudioTrackIndex) throws IOException {
    MediaFormat audioFormat = extractor.getTrackFormat(audioTrack);
    long durationUs = audioFormat.getLong(MediaFormat.KEY_DURATION);
    int maxBufferSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
    ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

    long lastFrametimeUs = 0;
    while (true) {
      long sampleTimeUs = extractor.getSampleTime();
      if (sampleTimeUs == -1) {
        break;
      }
      if (sampleTimeUs < 0) {
        extractor.advance();
        continue;
      }
      if (sampleTimeUs > durationUs) {
        break;
      }
      info.presentationTimeUs = sampleTimeUs;
      info.flags = extractor.getSampleFlags();
      info.size = extractor.readSampleData(buffer, 0);
      if (info.size < 0) {
        break;
      }
      mediaMuxer.writeSampleData(muxerAudioTrackIndex, buffer, info);
      lastFrametimeUs = info.presentationTimeUs;
      extractor.advance();
    }
    return lastFrametimeUs;
  }
}
