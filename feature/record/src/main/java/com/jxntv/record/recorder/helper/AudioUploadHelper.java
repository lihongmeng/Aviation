package com.jxntv.record.recorder.helper;

import com.alibaba.sdk.android.vod.upload.VODUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODUploadClient;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
import com.alibaba.sdk.android.vod.upload.model.VodInfo;
import com.jxntv.base.BaseViewModel;
import com.jxntv.record.recorder.model.VodVideoCreateModel;
import com.jxntv.record.recorder.repository.VideoRecordRepository;
import com.jxntv.runtime.GVideoRuntime;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AudioUploadHelper {

    // 视频上传后缀
    private static final String VOD_AUDIO_CREATE_FILE_SUFFIX = ".wav";

    // 超时总时长限制
    private static final long UPLOAD_TIME_LIMIT = 2 * 60 * 1000;

    // 超时总时长错误
    private static final String ERROR_TIME_LIMIT = "error_time_limit";

    // 录制仓库接口
    private VideoRecordRepository mRecordRepository;

    private UploadListener uploadListener;

    public AudioUploadHelper(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
        mRecordRepository = new VideoRecordRepository();

    }

    public void uploadAudio(String audioFilePath, String soundText) {
        uploadAudioReal(new File(audioFilePath), soundText);
    }

    public void uploadAudioReal(final File audioFile, final String soundText) {
        final long startTime = System.currentTimeMillis();
        if (uploadListener != null) {
            uploadListener.onStart();
        }
        mRecordRepository.createVodVideoModel(System.currentTimeMillis() + VOD_AUDIO_CREATE_FILE_SUFFIX)
                .timeout(UPLOAD_TIME_LIMIT, TimeUnit.MILLISECONDS)
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<VodVideoCreateModel>() {
                    @Override
                    protected void onRequestData(VodVideoCreateModel model) {
                        // 获取id后再进行上传
                        handleVODUpdate(model, audioFile, startTime, model.getVideoId(), soundText);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        if (uploadListener != null) {
                            if (throwable instanceof TimeoutException ||
                                    throwable instanceof SocketTimeoutException) {
                                uploadListener.onError(ERROR_TIME_LIMIT, ERROR_TIME_LIMIT);
                                return;
                            }
                            uploadListener.onError("create_audio_error", throwable.getMessage());
                        }
                    }
                });
    }

    private void handleVODUpdate(
            VodVideoCreateModel vodVideoCreateModel,
            File video,
            long startTime,
            final String videoId,
            final String soundText
    ) {
        long updateStartTime = System.currentTimeMillis();
        long limitTime = UPLOAD_TIME_LIMIT - (updateStartTime - startTime);
        VODUploadClient uploadClient = new VODUploadClientImpl(GVideoRuntime.getAppContext());
        uploadClient.init(new VODUploadCallback() {
            @Override
            public void onUploadSucceed(UploadFileInfo info) {
                super.onUploadSucceed(info);
                if (uploadListener != null) {
                    uploadListener.onSuccess(videoId, soundText);
                }
            }

            @Override
            public void onUploadFailed(UploadFileInfo info, String code, String message) {
                super.onUploadFailed(info, code, message);
                if (uploadListener != null) {
                    uploadListener.onError(code, message);
                }
            }

            @Override
            public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
                super.onUploadProgress(info, uploadedSize, totalSize);
                if (uploadListener != null) {
                    // 超时
                    if (System.currentTimeMillis() - updateStartTime > limitTime) {
                        if (uploadClient.listFiles() != null) {
                            int index = uploadClient.listFiles().size() - 1;
                            uploadClient.cancelFile(index);
                        }
                        uploadListener.onError(ERROR_TIME_LIMIT, ERROR_TIME_LIMIT);
                        return;
                    }
                    if (totalSize == 0) {
                        return;
                    }
                    int rate = (int) ((float) uploadedSize / (float) totalSize * 100);
                    if (rate > 99) {
                        rate = 99;
                    } else if (rate < 0) {
                        rate = 0;
                    }
                    uploadListener.onProgress(rate);
                }
            }

            @Override
            public void onUploadTokenExpired() {
                super.onUploadTokenExpired();
            }

            @Override
            public void onUploadRetry(String code, String message) {
                super.onUploadRetry(code, message);
            }

            @Override
            public void onUploadRetryResume() {
                super.onUploadRetryResume();
            }

            @Override
            public void onUploadStarted(UploadFileInfo uploadFileInfo) {
                super.onUploadStarted(uploadFileInfo);
                uploadClient.setUploadAuthAndAddress(uploadFileInfo,
                        vodVideoCreateModel.getUploadAuth(),
                        vodVideoCreateModel.getUploadAddress());
            }
        });
        uploadClient.addFile(video.getAbsolutePath(), new VodInfo());
        uploadClient.start();
    }

    /**
     * 上传接口
     */
    public interface UploadListener {

        /**
         * 上传进度回调
         *
         * @param rate 进度比例
         */
        void onProgress(int rate);

        /**
         * 上传成功回调
         */
        void onSuccess(String soundId, String soundContent);

        /**
         * 上传失败回调
         *
         * @param code    失败code
         * @param message 失败message
         */
        void onError(String code, String message);

        /**
         * 上传开始回调
         */
        void onStart();

    }

}
