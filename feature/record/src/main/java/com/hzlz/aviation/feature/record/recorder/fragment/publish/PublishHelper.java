package com.hzlz.aviation.feature.record.recorder.fragment.publish;


import static com.hzlz.aviation.feature.record.recorder.Constants.PublishFileType.PIC;
import static com.hzlz.aviation.feature.record.recorder.Constants.PublishFileType.SOUND;
import static com.hzlz.aviation.feature.record.recorder.Constants.PublishFileType.VIDEO;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SHOW_HOME_TAB_FOLLOW;
import static com.hzlz.aviation.kernel.base.Constant.SP_KEY.HISTORY_COMMUNITY_ID;
import static com.hzlz.aviation.kernel.base.Constant.UPLOAD_VIDEO_TYPE.HORIZONTAL;
import static com.hzlz.aviation.kernel.base.Constant.UPLOAD_VIDEO_TYPE.VERTICAL;
import static com.hzlz.aviation.kernel.base.plugin.IFileRepository.FILE_BUSINESS_TYPE_IMAGE;

import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;

import com.alibaba.sdk.android.vod.upload.VODUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODUploadClient;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
import com.alibaba.sdk.android.vod.upload.model.VodInfo;
import com.google.gson.Gson;
import com.hzlz.aviation.feature.record.recorder.data.SoundEntity;
import com.hzlz.aviation.feature.record.recorder.dialog.PublishFailedCancelDialog;
import com.hzlz.aviation.feature.record.recorder.helper.AudioUploadHelper;
import com.hzlz.aviation.feature.record.recorder.helper.RecordSharedPrefs;
import com.hzlz.aviation.feature.record.recorder.model.VodVideoCreateModel;
import com.hzlz.aviation.feature.record.recorder.repository.PublishRepository;
import com.hzlz.aviation.feature.record.recorder.repository.VideoRecordRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.entity.PublishSataData;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.IFileRepository;
import com.hzlz.aviation.kernel.base.utils.CalculateUtils;
import com.hzlz.aviation.kernel.base.view.floatwindow.FloatingView;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AppManager;
import com.hzlz.aviation.library.util.RegexUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author huangwei
 * date : 2021/8/13
 * desc : 动态发布
 **/
public class PublishHelper {

    // 视频上传后缀
    private static final String VOD_VIDEO_CREATE_FILE_SUFFIX = ".mp4";

    private static PublishHelper mInstance;
    // 发布相关接口
    private final PublishRepository publishRepository = new PublishRepository();
    // VideoRecordRepository
    private final VideoRecordRepository mRecordRepository = new VideoRecordRepository();
    // 文件执行相关操作
    private final IFileRepository iFileRepository = PluginManager.get(AccountPlugin.class).getFileRepository();

    private String content;
    private String introduce;
    private String topicName;
    private String userInstitution;

    private Circle circle;
    private Long topicId;
    private Long gatherId;
    private Long userInstitutionId;

    private int type;
    private boolean isQaType;
    private String answerAuthorId;

    // 链接标题数据
    public String outShareTitle;

    // 链接地址
    public String outShareUrl;

    /**
     * 是否正在发布内容
     */
    private boolean isPublishing;

    /**
     * 启动发布页面的意图,在发布页面关闭的时候，区分场景处理
     * <p>
     * {@see Constant#START_PUBLISH_FROM#HOME}
     * 在首页启动发布页面，发布完成后，自动跳转到特定的首页子页面
     * <p>
     * {@see Constant#START_PUBLISH_FROM#DETAIL}
     * 在社区详情页、话题详情页等二级及以上页面，发布完成后，保留在当前页面
     */
    private String startPublishFrom;

    public static PublishHelper get() {
        if (mInstance == null) {
            synchronized (FloatingView.class) {
                if (mInstance == null) {
                    mInstance = new PublishHelper();
                }
            }
        }
        return mInstance;
    }

    public boolean isPublishing() {
        return isPublishing;
    }

    /**
     * 设置动态相关数据
     *
     * @param content          文字内容
     * @param introduce        简介/标题
     * @param circle           社区相关信息
     * @param topicId          话题id
     * @param tenantId         频道频率Id
     * @param tenantName       频道频率名称
     * @param topicName        话题名称
     * @param gatherId         关联tabId
     * @param isQaType         是否是问答
     * @param answerAuthorId   问答者id
     * @param outShareTitle    外链标题
     * @param outShareUrl      外链地址
     * @param startPublishFrom 启动发布页面的来源类型
     */
    public void setData(
            String content,
            String introduce,
            Circle circle,
            Long topicId,
            Long tenantId,
            String tenantName,
            String topicName,
            Long gatherId,
            boolean isQaType,
            String answerAuthorId,
            String outShareTitle,
            String outShareUrl,
            String startPublishFrom
    ) {
        this.content = content;
        this.introduce = introduce;
        this.circle = circle;
        this.topicId = topicId;
        this.topicName = topicName;
        this.gatherId = gatherId;
        this.isQaType = isQaType;
        this.answerAuthorId = answerAuthorId;
        this.outShareTitle = outShareTitle;
        this.outShareUrl = outShareUrl;
        this.userInstitutionId = tenantId;
        this.userInstitution = tenantName;
        this.startPublishFrom = startPublishFrom;
    }

    /**
     * 发布语音
     *
     * @param soundEntity 语音
     */
    public void publishSound(SoundEntity soundEntity) {
        isPublishing = true;
        type = SOUND;
        new AudioUploadHelper(new AudioUploadHelper.UploadListener() {
            @Override
            public void onProgress(int rate) {
                FloatingView.get().setProgress(rate);
            }

            @Override
            public void onSuccess(String videoId, String soundContent) {
                publish(soundContent, (int) (soundEntity.operationTime / 1000),
                        videoId, null, null, null);
            }

            @Override
            public void onError(String code, String message) {
                showFailedDialog(view -> publishSound(soundEntity));
            }

            @Override
            public void onStart() {
            }


        }).uploadAudio(soundEntity.soundFilePath, soundEntity.soundText);

        showLoading();
    }

    /**
     * 发布图文
     *
     * @param imageList 图片
     */
    public void publishPic(List<String> imageList, String linkTitle, String linkValue) {
        isPublishing = true;
        type = PIC;
        if (imageList != null && imageList.size() > 0) {
            iFileRepository.batchUpLoadFile(FILE_BUSINESS_TYPE_IMAGE,
                    new ArrayList<>(), imageList, "", result ->
                            publish(null, 0, null, null, null, result));
        } else {
            publish(null, 0, null, null, null, null);
        }

        showLoading();

    }

    /**
     * 发布视频
     *
     * @param coverImageFile 封面图
     * @param videoPath      视频地址
     */
    public void publishVideo(File coverImageFile, String videoPath) {
        isPublishing = true;
        type = VIDEO;
        iFileRepository.doUploadImageFile(
                IFileRepository.FILE_BUSINESS_TYPE_MEDIA_COVER,
                coverImageFile,
                GVideoSchedulers.IO_PRIORITY_USER).timeout(30, TimeUnit.SECONDS)
                .subscribe(new BaseResponseObserver<String>() {
                    @Override
                    protected void onRequestData(String coverImageId) {
                        // 在服务端创建一个文件
                        final File videoFile = new File(videoPath);
                        mRecordRepository.createVodVideoModel(System.currentTimeMillis() + VOD_VIDEO_CREATE_FILE_SUFFIX)
                                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<VodVideoCreateModel>() {
                                    @Override
                                    protected void onRequestData(VodVideoCreateModel model) {
                                        // 执行文件上传操作
                                        handleVODUpdate(model, videoFile, model.getVideoId(), coverImageId);
                                    }

                                    @Override
                                    protected void onRequestError(Throwable throwable) {
                                        showFailedDialog(view -> publishVideo(coverImageFile, videoPath));
                                    }
                                });

                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {

                    }
                });

        showLoading();

    }


    /**
     * 上传视频到阿里云
     */
    private void handleVODUpdate(VodVideoCreateModel vodVideoCreateModel, File video,
                                 final String videoId, final String coverImageId) {

        VODUploadClient uploadClient = new VODUploadClientImpl(GVideoRuntime.getAppContext());
        uploadClient.init(new VODUploadCallback() {
            @Override
            public void onUploadSucceed(UploadFileInfo info) {
                super.onUploadSucceed(info);

                // 判断视频时横向还是竖向
                Pair<Integer, Integer> pair = CalculateUtils.getVideoFileWidthHeight(video);
                int type = VERTICAL;
                if (pair.first != null && pair.second != null && pair.first > pair.second) {
                    type = HORIZONTAL;
                }
                publish(null, 0, videoId, coverImageId, type, null);
            }

            @Override
            public void onUploadFailed(UploadFileInfo info, String code, String message) {
                super.onUploadFailed(info, code, message);
                showFailedDialog(view -> handleVODUpdate(vodVideoCreateModel, video, videoId, coverImageId));
            }

            @Override
            public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
                super.onUploadProgress(info, uploadedSize, totalSize);
                float progress = (float) uploadedSize / totalSize;
                progress = progress * 100;
                FloatingView.get().setProgress((int) progress);
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


    private void publish(
            String soundContent, long soundTime, String videoId, String coverImageId,
            Integer platStyle, List<String> imageList) {

        boolean isCircleEmpty = circle == null;
        final Long groupId = isCircleEmpty ? null : circle.getGroupId();
        final String groupName = isCircleEmpty ? null : circle.getName();
        publishRepository.createVodVideoModel(
                content,
                type,
                coverImageId,
                imageList,
                introduce,
                true,
                soundTime + "",
                soundContent,
                videoId,
                "",
                groupId,
                topicId,
                platStyle,
                gatherId,
                isQaType,
                isQaType ? TextUtils.isEmpty(answerAuthorId) ? "0" : answerAuthorId : answerAuthorId,
                outShareTitle,
                outShareUrl
        ).subscribe(new BaseViewModel.BaseGVideoResponseObserver<Integer>() {
            @Override
            protected void onRequestData(@NonNull Integer result) {
                isPublishing = false;
                FloatingView.get().finishLoading();
                GVideoEventBus.get(REFRESH_PAGE_BECAUSE_PUBLISH, String.class).post(startPublishFrom);

                if (isQaType) {
                    GVideoEventBus.get(Constant.EVENT_MSG.COMPOSITION_ADD_QUESTION).post(null);
                } else {
                    GVideoEventBus.get(Constant.EVENT_MSG.COMPOSITION_ADD).post(null);
                }
                GVideoSensorDataManager.getInstance().publish(
                        groupId,
                        groupName,
                        userInstitutionId,
                        userInstitution,
                        topicId,
                        topicName,
                        result,
                        content,
                        RegexUtil.match(outShareUrl, Constant.REGEX_PATTERN.DOMAIN),
                        getPublishType(type),
                        isQaType,
                        String.valueOf(result),
                        null
                );

                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                String userId = accountPlugin.getUserId();
                if (TextUtils.isEmpty(userId)) {
                    return;
                }
                if (!isQaType && circle != null) {
                    RecordSharedPrefs.getInstance().putString(HISTORY_COMMUNITY_ID + "_" + userId, new Gson().toJson(circle));
                }
            }

            @Override
            protected void onRequestError(@NonNull Throwable throwable) {
                isPublishing = false;
                GVideoSensorDataManager.getInstance().publish(
                        groupId,
                        groupName,
                        userInstitutionId,
                        userInstitution,
                        topicId,
                        topicName,
                        -1,
                        content,
                        RegexUtil.match(outShareUrl, Constant.REGEX_PATTERN.DOMAIN),
                        getPublishType(type),
                        isQaType,
                        throwable.getMessage(),
                        throwable.getMessage()
                );
                showFailedDialog(view -> publish(soundContent, soundTime, videoId, coverImageId, platStyle, imageList));
            }
        });
    }


    /**
     * 显示失败弹窗
     *
     * @param clickListener 重试
     */
    private void showFailedDialog(View.OnClickListener clickListener) {
        PublishFailedCancelDialog publishFailedDialog = new PublishFailedCancelDialog(AppManager.getAppManager().currentActivity());
        publishFailedDialog.init(clickListener);
        publishFailedDialog.show();
    }

    public int getPublishType(int type) {
        switch (type) {
            case SOUND:
                return MediaType.AUDIO_TXT;
            case VIDEO:
                return MediaType.LONG_VIDEO;
            default:
                return MediaType.IMAGE_TXT;
        }
    }

    private void showLoading() {
        AppManager.getAppManager().finishActivity();
        if (!TextUtils.isEmpty(startPublishFrom)) {
            GVideoEventBus.get(SHOW_HOME_TAB_FOLLOW).post(new PublishSataData(startPublishFrom, circle));
        }
        FloatingView floatingView = FloatingView.get();
        floatingView.add();
        String loadText = isQaType ? "问答发布中，请稍候" : "动态发布中，请稍候";
        String finishText = isQaType ? "问答发布完成" : "动态发布完成";
        floatingView.setTipText(loadText, finishText);

    }

}
