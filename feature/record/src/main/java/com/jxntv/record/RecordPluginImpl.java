package com.jxntv.record;

import static com.jxntv.base.Constant.BUNDLE_KEY.AUTHOR_MODEL;
import static com.jxntv.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.jxntv.base.Constant.BUNDLE_KEY.IS_QA;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.base.Constant;
import com.jxntv.base.StaticParams;
import com.jxntv.base.callback.AudioRecordListener;
import com.jxntv.base.model.PublishLinkWhiteListItem;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.record.recorder.dialog.CommentInputDialog;
import com.jxntv.record.recorder.dialog.RecordDialog;
import com.jxntv.record.recorder.helper.AudioRecordManager;
import com.jxntv.record.recorder.helper.VideoRecordHelper;
import com.jxntv.record.recorder.repository.PublishRepository;
import com.jxntv.sensordata.GVideoSensorDataManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 录制接口实现类
 */
public class RecordPluginImpl implements RecordPlugin {

    private final HashMap<String, String> inputCacheMap = new HashMap<>();

    @Override
    public void startRecord(AudioRecordListener audioRecordListener) {
        AudioRecordManager audioRecordManager = AudioRecordManager.getInstance();
        audioRecordManager.setListener(audioRecordListener);
        audioRecordManager.startRecord();
    }

    @Override
    public void stopRecord() {
        AudioRecordManager.getInstance().stopRecord();
    }

    @Override
    public void destroy() {
        AudioRecordManager.getInstance().destroy();
    }

    @Override
    public void initWhiteListData() {
        new PublishRepository().getPublishLinkWhiteList()
                .subscribe(new BaseResponseObserver<ArrayList<PublishLinkWhiteListItem>>() {
                    @Override
                    protected void onRequestData(ArrayList<PublishLinkWhiteListItem> publishLinkWhiteListItems) {
                        StaticParams.whiteListItemArrayList = publishLinkWhiteListItems;
                        if (StaticParams.whiteListItemArrayList == null) {
                            StaticParams.whiteListItemArrayList = new ArrayList<>();
                        }
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {

                    }
                });

    }

    @Override
    public void showRecordDialog(@NonNull Context context, Bundle bundle) {
        new RecordDialog(context, bundle).show();
    }

    @Override
    public void updateSelectVideoMaxTime(int maxTime) {
        VideoRecordHelper.updateSelectVideoMaxTime(maxTime);
    }

    @Override
    public void updateAudioConfig(String aliKey, String aliToken) {
        AudioRecordManager.getInstance().setAliConfig(aliKey, aliToken);
    }

    /**
     * 启动发布页面，在bundle数据中区分是发布问答还是发布普通帖子
     * 参考{@link com.jxntv.record.recorder.fragment.publish.PublishFragment#bindViewModels()}
     *
     * @param context 上下文Context
     * @param bundle 携带参数
     */
    @SuppressWarnings("JavadocReference")
    @Override
    public void startPublishFragmentUseActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, RecordActivity.class);
        if(bundle==null){
            bundle=new Bundle();
        }
        bundle.putInt(RecordPluginImpl.INTENT_RECORD_TYPE, R.id.publishFragment);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void startQAPublishActivity(Context context, @NonNull Circle circle, AuthorModel authorModel, @NonNull String formPid) {
        Bundle bundle=new Bundle();
        bundle.putParcelable(CIRCLE, circle);
        bundle.putBoolean(IS_QA, true);
        bundle.putString(Constant.EXTRA_FROM_PID, formPid);
        bundle.putParcelable(AUTHOR_MODEL, authorModel);
        startPublishFragmentUseActivity(context, bundle);
    }


    @Override
    public void showCommentInputDialog(Context context, String replyPrefix, String key, String type, CommentListener listener) {
//        if (commentInputDialog == null || commentInputDialog.isTypeDif(type)) {
//            commentInputDialog = new CommentInputDialog(context, replyPrefix, type, listener);
//        }
        new CommentInputDialog(context, replyPrefix, key, type, listener).show();
    }

    @Override
    public void setInputCacheText(String key, String text) {
        if (!TextUtils.isEmpty(key)) {
            inputCacheMap.put(key, text);
        }
    }

    @Override
    public String getInputCacheText(String key) {
        if (!TextUtils.isEmpty(key)) {
            return inputCacheMap.get(key);
        }
        return "";
    }

    @Override
    public void startVideoRecordFragment(Context context) {
        PermissionManager
                .requestPermissions(context, new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        Intent intent = new Intent(context, RecordActivity.class);
                        intent.putExtra(RecordPluginImpl.INTENT_RECORD_TYPE, R.id.recordFragment);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                        Toast.makeText(context, R.string.refuse_permission_write_read, Toast.LENGTH_SHORT).show();
                    }
                },
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                );
    }

    @Override
    public void startImageSelectFragment(Context context, ArrayList<String> canNotSelectList,
                                         int operationType, int singleOperationIndex) {
        PermissionManager
                .requestPermissions(context, new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        Intent intent = new Intent(context, RecordActivity.class);
                        intent.putExtra(RecordPluginImpl.INTENT_RECORD_TYPE, R.id.chooseImageVideoFragment);
                        intent.putExtra(INTENT_IS_VIDEO_TYPE, false);
                        intent.putExtra(INTENT_SELECT_IMAGE_OPERATION_TYPE, operationType);
                        intent.putExtra(INTENT_SINGLE_OPERATION_INDEX, singleOperationIndex);
                        intent.putStringArrayListExtra(INTENT_CAN_NOT_SELECT_LIST, canNotSelectList);
                        context.startActivity(intent);

                    }

                    @Override
                    public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                        Toast.makeText(context, R.string.refuse_permission_write_read, Toast.LENGTH_SHORT).show();
                    }
                },Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

}
