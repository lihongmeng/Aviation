package com.hzlz.aviation.feature.record.recorder.dialog;

import static android.view.View.GONE;
import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_ADD;
import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_REPLACE;
import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.SINGLE_PLACE;
import static com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper.DEFAULT_MAX_SELECT_IMAGE_NUM;
import static com.hzlz.aviation.kernel.base.Constant.COMMENT_DIALOG_TEXT_LIMIT_COUNT;
import static com.hzlz.aviation.kernel.base.plugin.IFileRepository.FILE_BUSINESS_TYPE_IMAGE;
import static com.hzlz.aviation.kernel.base.plugin.RecordPlugin.DIALOG_AUDIO;
import static com.hzlz.aviation.kernel.base.plugin.RecordPlugin.DIALOG_IMAGE;

import android.Manifest;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import com.alibaba.sdk.android.vod.upload.VODUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODUploadClient;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
import com.alibaba.sdk.android.vod.upload.model.VodInfo;
import com.hzlz.aviation.feature.record.recorder.model.ChooseImageListModel;
import com.hzlz.aviation.feature.record.recorder.model.VodVideoCreateModel;
import com.hzlz.aviation.feature.record.recorder.repository.VideoRecordRepository;
import com.hzlz.aviation.feature.record.recorder.view.soundrecord.GVideoSoundRecordView;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.utils.StringUtils;
import com.hzlz.aviation.kernel.base.view.PublishImageRecyclerView;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.SoftInputUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetDialog;
import com.hzlz.aviation.feature.record.R;

import java.util.ArrayList;

/**
 * @author huangwei
 * date : 2021/4/22
 * desc : 图文、语音评论弹窗
 */
public class CommentInputDialog extends GVideoBottomSheetDialog {

    private final DialogInputCommentBinding mBinding;

    private String textContent = "";
    private String soundFilePathString;
    private String soundTextString;
    private long soundTimeLong;
    private ArrayList<String> imageList = new ArrayList<>();
    private final RecordPlugin.CommentListener commentListener;
    private boolean isSoundRecording = false;
    private ProcessVideoDialog loadingDialog;
    private String type;

    private final Context context;
    private String cacheKey, inputText;

    public CommentInputDialog(Context context, String replyPrefix, String key,String type, RecordPlugin.CommentListener listener) {
        super(context);
        this.context = context;
        this.type = type;
        this.cacheKey = key;

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_input_comment,
                null, false);
        mExpectWindowWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        setContentView(mBinding.getRoot(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        this.commentListener = listener;
        if (!TextUtils.isEmpty(replyPrefix)) {
            mBinding.edittext.setHint(replyPrefix);
        } else {
            mBinding.edittext.setHint(R.string.publish_comment);
        }

        String cacheText = PluginManager.get(RecordPlugin.class).getInputCacheText(key);
        if (!TextUtils.isEmpty(cacheText)){
            mBinding.edittext.setText(cacheText);
            inputText = cacheText;
        }

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.DISMISS_COMMENT_INPUT_PANEL).observeForever(dismissObserver);
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.HIDE_LOADING_INPUT_PANEL).observeForever(hideLoadingObserver);

        initViews();

    }

    @Override
    public void show() {
        updateBtnState();
        GVideoEventBus.get(RecordPlugin.EVENT_BUS_SELECT_IMAGE_ADDRESS_LIST, ChooseImageListModel.class)
                .observeForever(imageObserver);
        switch (type) {
            case DIALOG_AUDIO:
                mBinding.soundRecordIcon.performClick();
                break;
            case DIALOG_IMAGE:
                mBinding.picIcon.performClick();
                break;
            default:
                mBinding.edittext.requestFocus();
                SoftInputUtils.showSoftInput(mBinding.edittext, context, 200L);
        }

        super.show();
    }

    private void initViews() {
        mBinding.commentLengthCount.setText(getContext().getResources().getString(R.string.comment_length_count, "0"));
        mBinding.send.setOnClickListener(view -> {
            SoftInputUtils.hideSoftInput(mBinding.edittext.getWindowToken(), getContext());
            textContent = StringUtils.filterWhiteSpace(textContent);
            if (!TextUtils.isEmpty(soundFilePathString) || imageList.size() > 0) {

                if (!TextUtils.isEmpty(soundFilePathString)) {

                    upLoadSound();
                } else {
                    if (loadingDialog!=null){
                        loadingDialog.dismiss();
                    }
                    loadingDialog = new ProcessVideoDialog(getContext());
                    loadingDialog.show();

                    PluginManager.get(FilePlugin.class).getFileRepository().batchUpLoadFile(
                            FILE_BUSINESS_TYPE_IMAGE,
                            new ArrayList<>(),
                            imageList,
                            "",
                            result -> {
                                commentListener.imageResult(textContent, result);
                            });
                }
            } else {
                commentListener.imageResult(textContent, null);
            }
        });

        mBinding.edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = mBinding.edittext.getText();
                inputText = mBinding.edittext.getText().toString();
                int len = (editable == null) ? 0 : editable.length();

                if (len > COMMENT_DIALOG_TEXT_LIMIT_COUNT) {
                    Toast.makeText(getContext(), R.string.comment_input_max_toast, Toast.LENGTH_SHORT).show();

                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    // 截取新字符串
                    String newStr = str.substring(0, COMMENT_DIALOG_TEXT_LIMIT_COUNT);
                    mBinding.edittext.setText(newStr);
                    editable = mBinding.edittext.getText();
                    // 新字符串的长度
                    int newLen = editable.length();
                    // 旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    // 设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                }

                if (mBinding.edittext.getText().length() > COMMENT_DIALOG_TEXT_LIMIT_COUNT) {
                    Toast.makeText(getContext(), R.string.comment_input_max_toast, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isSoundRecording) {
                    updateBtnState();
                }
            }
        });

        mBinding.soundView.isShowDelete(true);
        mBinding.soundView.setDeleteOnClickListener(v -> {
            clearSound();
            updateBtnState();
            mBinding.soundRecord.setVisibility(View.VISIBLE);
        });

        mBinding.soundRecord.setCompleteListener(new GVideoSoundRecordView.OperationListener() {

            @Override
            public void startRequestPermission() {

            }

            @Override
            public void start() {
                isSoundRecording = true;
                mBinding.soundRecordIcon.setEnabled(false);
                mBinding.picIcon.setEnabled(false);
            }

            @Override
            public void stop() {
                isSoundRecording = false;
            }

            @Override
            public void errorOnBackgroundThread(String message, boolean isFirstCallBack) {
                isSoundRecording = false;
                //noinspection ConstantConditions
                if (mBinding == null
                        || context == null
                        || mBinding.soundRecord == null
                        || !isFirstCallBack) {
                    return;
                }
                mBinding.soundRecord.post(
                        () -> Toast.makeText(
                                context,
                                context.getString(R.string.record_failed),
                                Toast.LENGTH_SHORT
                        ).show());
            }

            @Override
            public void result(String soundFilePath, String soundText, long operationTime) {
                isSoundRecording = false;
                soundFilePathString = soundFilePath;
                soundTextString = soundText;
                soundTimeLong = operationTime;
                if (!TextUtils.isEmpty(soundFilePath)) {
                    imageList.clear();

                    mBinding.soundView.setSoundUrl(soundFilePath);
                    mBinding.soundView.setSoundText(soundText);
                    mBinding.soundView.setTotalTime(operationTime);

                }
                updateBtnState();
            }

            @Override
            public void onLoadingStart(String loadingMessage) {
                Context context = getContext();
                if (context == null) {
                    return;
                }
                if (loadingDialog == null) {
                    loadingDialog = new ProcessVideoDialog(context);
                }
                loadingDialog.setTipText(loadingMessage);
                loadingDialog.show();
            }

            @Override
            public void onLoadingEnd() {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }

        });

        mBinding.imageList.setOperationListener(new PublishImageRecyclerView.OperationListener() {
            @Override
            public void delete(int index) {
                if (index < imageList.size()) {
                    imageList.remove(index);
                    if (imageList.size() == 0) {
                        mBinding.imageList.setVisibility(GONE);
                    } else {
                        mBinding.imageList.setImagesData(imageList);
                        mBinding.imageList.setVisibility(View.VISIBLE);
                    }
                    clearSound();
                    updateBtnState();
                }
            }

            @Override
            public void replace(int index) {
                clearSound();
                PluginManager.get(RecordPlugin.class).startImageSelectFragment(
                        getContext(),
                        imageList,
                        SINGLE_PLACE,
                        index
                );
            }

            @Override
            public void add() {
                clearSound();
                PluginManager.get(RecordPlugin.class).startImageSelectFragment(
                        getContext(),
                        imageList,
                        BATCH_ADD,
                        -1
                );
            }
        });

        mBinding.soundRecordIcon.setOnClickListener(v -> {
            GVideoEventBus.get(Constant.EVENT_MSG.AUDIO_BG_NEED_PAUSE).post(null);
            GVideoEventBus.get(Constant.EVENT_MSG.VIDEO_NEED_PAUSE).post(null);
            SoftInputUtils.hideSoftInput(mBinding.edittext.getWindowToken(), getContext());
            PermissionManager
                    .requestPermissions(mBinding.soundRecordIcon.getContext(),new PermissionCallback() {
                        @Override
                        public void onPermissionGranted(@NonNull Context context) {
                            type = DIALOG_AUDIO;
                            mBinding.soundRecord.setVisibility(View.VISIBLE);
                            mBinding.imageList.setVisibility(GONE);
                        }

                        @Override
                        public void onPermissionDenied(
                                @NonNull Context context,
                                @Nullable String[] grantedPermissions,
                                @NonNull String[] deniedPermission) {
                            Toast.makeText(context, "没有录音权限", Toast.LENGTH_SHORT).show();
                        }
                    },Manifest.permission.RECORD_AUDIO);

        });

        mBinding.picIcon.setOnClickListener(v -> {
            SoftInputUtils.hideSoftInput(
                    mBinding.edittext.getWindowToken(),
                    getContext()
            );
            mBinding.soundRecord.setVisibility(View.GONE);
            type = DIALOG_IMAGE;
            PluginManager.get(RecordPlugin.class).startImageSelectFragment(
                    getContext(),
                    imageList,
                    BATCH_ADD,
                    -1
            );
        });

    }

    private final Observer<ChooseImageListModel> imageObserver = chooseImageListModel -> {

        switch (chooseImageListModel.operationType) {
            case BATCH_REPLACE:
                imageList = chooseImageListModel.imagePathList;
                break;
            case SINGLE_PLACE:
                if (chooseImageListModel.singleOperationIndex < 0) {
                    return;
                }
                imageList.set(chooseImageListModel.singleOperationIndex,
                        chooseImageListModel.imagePathList.get(0));
                break;
            case BATCH_ADD:
                if (chooseImageListModel.imagePathList == null) {
                    return;
                }
                imageList.addAll(chooseImageListModel.imagePathList);
                break;
        }

        clearSound();
        updateBtnState();
    };

    private void clearSound() {
        this.soundFilePathString = "";
        this.soundTextString = "";

    }

    public void updateBtnState() {
        Editable editable = mBinding.edittext.getText();
        textContent = (editable == null) ? "" : editable.toString();
        mBinding.commentLengthCount.setText(context.getString(R.string.comment_length_count, textContent.length() + ""));

        int imageLength = imageList.size();

        //发送按钮
        if (!TextUtils.isEmpty(soundFilePathString)
                || imageLength > 0
                || textContent.length() > 0
        ) {
            mBinding.send.setEnabled(true);
            mBinding.send.setSelected(true);
        } else {
            mBinding.send.setEnabled(false);
            mBinding.send.setSelected(false);
        }

        boolean isImageDataEmpty = imageLength == 0;
        boolean isSoundDataEmpty = TextUtils.isEmpty(soundFilePathString);

        if (isImageDataEmpty && isSoundDataEmpty) {
            mBinding.soundRecordIcon.setEnabled(true);
            mBinding.picIcon.setEnabled(true);
        } else {
            if (isImageDataEmpty) {
                mBinding.soundRecordIcon.setEnabled(false);
                mBinding.picIcon.setEnabled(false);
            }

            if (isSoundDataEmpty) {
                mBinding.soundRecordIcon.setEnabled(false);
                mBinding.picIcon.setEnabled(imageLength != DEFAULT_MAX_SELECT_IMAGE_NUM);
            }
        }

        //录音显示
        if (!TextUtils.isEmpty(soundFilePathString)) {
            mBinding.soundRecord.setVisibility(GONE);
            mBinding.soundView.setVisibility(View.VISIBLE);
        } else {
            mBinding.soundView.stop();
            mBinding.soundView.setVisibility(GONE);
            mBinding.soundRecord.setVisibility(GONE);
        }

        //图片显示
        if (imageList.size() != 0) {
            mBinding.imageList.setImagesData(imageList);
            mBinding.imageList.setVisibility(View.VISIBLE);
        } else {
            mBinding.imageList.setVisibility(GONE);
        }

    }

    private void upLoadSound() {

        new VideoRecordRepository().createVodVideoModel(System.currentTimeMillis() + ".wav")
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<VodVideoCreateModel>() {
                    @Override
                    protected void onRequestData(VodVideoCreateModel model) {
                        handleVODUpdate(model, soundFilePathString);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        showError(throwable.getMessage());
                        LogUtils.e("create_video_error", throwable.getMessage());
                    }
                });

    }


    /**
     * 处理上传vod视频
     *
     * @param vodVideoCreateModel 对应Vod数据模型
     * @param soundFilePath       待上传的文件
     */
    private void handleVODUpdate(VodVideoCreateModel vodVideoCreateModel, String soundFilePath) {
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        loadingDialog = new ProcessVideoDialog(getContext());
        loadingDialog.show();
        VODUploadClient uploadClient = new VODUploadClientImpl(GVideoRuntime.getAppContext());
        uploadClient.init(new VODUploadCallback() {
            @Override
            public void onUploadSucceed(UploadFileInfo info) {
                super.onUploadSucceed(info);
                commentListener.soundResult(textContent, vodVideoCreateModel.getVideoId(), soundFilePath, soundTimeLong / 1000, soundTextString);
            }

            @Override
            public void onUploadFailed(UploadFileInfo info, String code, String message) {
                super.onUploadFailed(info, code, message);
                showError(code + "," + message);
            }

            @Override
            public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
                super.onUploadProgress(info, uploadedSize, totalSize);
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
        uploadClient.addFile(soundFilePath, new VodInfo());
        uploadClient.start();
    }

    private void showError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void dismiss() {
        PluginManager.get(RecordPlugin.class).setInputCacheText(cacheKey,inputText);
        SoftInputUtils.hideSoftInput(mBinding.edittext.getWindowToken(), getContext());
        mBinding.soundRecord.destroy();
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME).post(null);
        GVideoEventBus.get(RecordPlugin.EVENT_BUS_SELECT_IMAGE_ADDRESS_LIST, ChooseImageListModel.class).removeObserver(imageObserver);
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.DISMISS_COMMENT_INPUT_PANEL).removeObserver(dismissObserver);
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.HIDE_LOADING_INPUT_PANEL).removeObserver(hideLoadingObserver);
        super.dismiss();
    }

    private final Observer dismissObserver = o ->{
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
        inputText = "";
        CommentInputDialog.this.dismiss();
    };

    private final Observer hideLoadingObserver = o ->{
        if (loadingDialog!=null){
            loadingDialog.dismiss();
        }
    };

}
