package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.callback.AudioRecordListener;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.library.ioc.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 录制接口
 */
public interface RecordPlugin extends Plugin {


    /**
     * 录制 intent type
     */
    String INTENT_RECORD_TYPE = "record_type";

    /**
     * 是否视频选择
     */
    String INTENT_IS_VIDEO_TYPE = "record_is_video_type";

    /**
     * 选择图片的动机，是更换，还是添加
     * 暂时不做视频多选
     */
    String INTENT_SELECT_IMAGE_OPERATION_TYPE = "intent_select_image_operation_type";

    /**
     * 单张替换时，对应的位置
     */
    String INTENT_SINGLE_OPERATION_INDEX = "intent_single_operation_index";

    /**
     * 不能选择的列表
     */
    String INTENT_CAN_NOT_SELECT_LIST = "intent_can_not_select_list";

    /**
     * 录制、选择的视频文件，在阿里云的地址
     */
    String EVENT_BUS_VIDEO_ADDRESS = "event_bus_video_address";

    /**
     * 选择的图片文件，在阿里云的地址，列表
     */
    String EVENT_BUS_SELECT_IMAGE_ADDRESS_LIST = "event_bus_select_image_address_list";

    /**
     * 选择的圈子、话题
     */
    String EVENT_BUS_SELECT_CIRCLE_TOPIC = "event_bus_select_circle_topic";

    /**
     * 选择的圈子
     */
    String EVENT_BUS_SELECT_CIRCLE = "event_bus_select_circle";

    /**
     * 选择的话题
     */
    String EVENT_BUS_SELECT_TOPIC = "event_bus_select_topic";

    /**
     * 显示录制dialog
     *
     * @param context 上下文环境
     */
    void showRecordDialog(@NonNull Context context, Bundle bundle);

    /**
     * 更新选择视频最大时长
     *
     * @param maxTime 最大时长
     */
    void updateSelectVideoMaxTime(int maxTime);

    /**
     * 设置阿里配置
     *
     * @param aliKey   阿里语音key
     * @param aliToken 阿里语音token
     */
    void updateAudioConfig(String aliKey, String aliToken);

    /**
     * 启动发布页面，在bundle数据中区分是发布问答还是发布普通帖子
     * 参考{@link com.hzlz.aviation.feature.record.recorder.fragment.publish.PublishFragment#bindViewModels()}
     *
     * @param context 上下文Context
     * @param bundle  携带参数
     */
    @SuppressWarnings("JavadocReference")
    void startPublishFragmentUseActivity(Context context, Bundle bundle);

    /**
     * 打开问答发布界面
     *
     * @param circle      社区信息
     * @param authorModel 导师信息（选填）
     * @param formPid     来源页面pid
     */
    void startQAPublishActivity(Context context, @NonNull Circle circle, AuthorModel authorModel, @NonNull String formPid);

    interface CommentListener {
        void imageResult(String content, List<String> imageList);

        void soundResult(String content, String soundUrl, String soundFilePath, long soundLength, String soundTxt);
    }

    /**
     * 选择的图片文件，在阿里云的地址，列表
     */
    String DIALOG_TXT = "dialog_txt";
    String DIALOG_AUDIO = "dialog_audio";
    String DIALOG_IMAGE = "dialog_image";

    /**
     * 获取显示评论弹窗
     *
     * @param context     上下文Context
     * @param replyPrefix 显示  回复 xxx :
     * @param commentId   评论id,用于获取缓存数据
     * @param type        默认显示类型
     */
    void showCommentInputDialog(Context context, String replyPrefix, String commentId, String type, CommentListener listener);

    /**
     * 设置草稿文字
     */
    void setInputCacheText(String key,String text);

    /**
     * 获取草稿文字
     */
    String getInputCacheText(String key);


    /**
     * 启动视频录制界面
     *
     * @param context Context
     */
    void startVideoRecordFragment(Context context);

    /**
     * 启动图片选择界面
     *
     * @param context              Context
     * @param canNotSelectList     不能选择图片
     * @param operationType        操作类型
     * @param singleOperationIndex 单张操作的时候对应的位置,非单张操作传-1即可
     */
    void startImageSelectFragment(
            Context context,
            ArrayList<String> canNotSelectList,
            int operationType,
            int singleOperationIndex
    );

    void startRecord(AudioRecordListener audioRecordListener);

    void stopRecord();

    void destroy();

    void initWhiteListData();

}
