package com.jxntv.record.recorder.fragment.choose;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jxntv.async.GlobalExecutor;
import com.jxntv.async.GlobalExecutorScheduler;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.network.schedule.GVideoSchedulers;
import com.jxntv.record.R;
import com.jxntv.record.recorder.data.ImageVideoEntity;
import com.jxntv.record.recorder.dialog.ProcessVideoDialog;
import com.jxntv.record.recorder.dialog.RecordConfirmDialog;
import com.jxntv.record.recorder.helper.VideoChooseHelper;
import com.jxntv.record.recorder.helper.VideoRecordHelper;
import com.jxntv.record.recorder.model.ChooseImageListModel;
import com.jxntv.record.recorder.model.ChooseVideoModel;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.FileUtils;
import com.jxntv.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jxntv.async.GlobalExecutor.PRIORITY_USER;
import static com.jxntv.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_CAMERA;
import static com.jxntv.base.plugin.RecordPlugin.EVENT_BUS_SELECT_IMAGE_ADDRESS_LIST;
import static com.jxntv.base.plugin.RecordPlugin.EVENT_BUS_VIDEO_ADDRESS;
import static com.jxntv.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_REPLACE;
import static com.jxntv.record.recorder.Constants.SELECT_IMAGE_TYPE.SINGLE_PLACE;

/**
 * 选择视频数据模型
 */
public class ChooseImageVideoViewModel extends BaseViewModel implements VideoChooseHelper.ItemSelectListener {

    /**
     * 当前选择的数量
     */
    MutableLiveData<Integer> selectItemNum = new MutableLiveData<>(0);
    /**
     * 当前选择的数量
     */
    MutableLiveData<Float> selectItemTime = new MutableLiveData<>(0f);
    /**
     * 完成按钮是否可点击
     */
    boolean isVideoType = true;
    @NonNull
    public ObservableBoolean enableNext = new ObservableBoolean(false);
    /**
     * 视频缩放辅助类
     */
    private VideoRecordHelper mVideoRecordHelper;

    /**
     * 是否是以替换的方式启动
     */
    private int operationType = BATCH_REPLACE;

    /**
     * 如果是单张替换，需要对应的位置
     */
    public int singleOperationIndex;
    /**
     * 拍照地址
     */
    private String takePhotoPath;

    /**
     * 构造函数
     */
    public ChooseImageVideoViewModel(@NonNull Application application) {
        super(application);
        VideoChooseHelper.getInstance().setItemSelectListener(this);
        mVideoRecordHelper = new VideoRecordHelper();
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideoType = isVideo;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public void setSingleOperationIndex(int singleOperationIndex) {
        this.singleOperationIndex = singleOperationIndex;
    }

    /**
     * 加载视频
     */
    LiveData<List<ImageVideoEntity>> loadMediaVideo() {
        MutableLiveData<List<ImageVideoEntity>> listLiveData = new MutableLiveData<>();
        GlobalExecutor.execute(() -> {
            List<ImageVideoEntity> entityList = new ArrayList<>();
            if (isVideoType) {
                loadLocalVideos(entityList);
            } else {
                loadLocalImages(entityList);
            }
            listLiveData.postValue(entityList);
        }, "record_check_video", PRIORITY_USER);
        return listLiveData;
    }

    /**
     * 加载本地视频
     *
     * @param list 本地视频缓存容器
     */
    private void loadLocalVideos(@NonNull List<ImageVideoEntity> list) {
        Cursor cursor = null;
        ContentResolver resolver = GVideoRuntime.getAppContext().getContentResolver();
        if (resolver == null) {
            return;
        }
        try {
            cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns.DATA,
                            MediaStore.Video.Media._ID,
                            "duration",
                            MediaStore.Video.Media.DATE_MODIFIED},
                    MediaStore.Video.Media.MIME_TYPE + "=?", new String[]{"video/mp4"},
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
            ImageVideoEntity entity;
            long videoId;
            do {
                entity = new ImageVideoEntity();
                entity.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                videoId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                entity.id = videoId;
                entity.duration = cursor.getLong(cursor.getColumnIndex("duration"));
                entity.modifiedTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));
                entity.isVideo = true;
                list.add(entity);
            } while (cursor.moveToNext());
        } catch (Exception e) {
            LogUtils.printException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 加载本地图片
     *
     * @param list 本地图片缓存容器
     */
    private void loadLocalImages(@NonNull List<ImageVideoEntity> list) {
        Cursor cursor = null;
        ContentResolver resolver = GVideoRuntime.getAppContext().getContentResolver();
        if (resolver == null) {
            return;
        }
        try {
            cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " desc"
            );
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }
            ImageVideoEntity entity;
            long videoId;
            do {
                entity = new ImageVideoEntity();
                entity.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                videoId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                entity.id = videoId;
                entity.modifiedTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED));
                entity.isVideo = false;
                list.add(entity);
            } while (cursor.moveToNext());

            ImageVideoEntity imageVideoEntity = new ImageVideoEntity();
            imageVideoEntity.path = R.drawable.ic_take_photo+"";
            imageVideoEntity.isTakePhoto = true;
            imageVideoEntity.isVideo = false;
            list.add(0, imageVideoEntity);
        } catch (Exception e) {
            LogUtils.printException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @Override
    public void onItemNumChanged(int num, long time) {
        if (num >= 0) {
            selectItemNum.setValue(num);
            selectItemTime.setValue(time * 1.0f / 1000f);
            enableNext.set(num != 0);
        }
    }

    /**
     * 结束按钮点击事件
     *
     * @param activity Activity
     */
    public void onItemFinishClick(Activity activity) {
        if (activity == null) {
            return;
        }
        if (isVideoType) {

            if (!VideoChooseHelper.getInstance().checkSelectAvailable()) {
                RecordConfirmDialog confirmDialog = new RecordConfirmDialog(activity);
                confirmDialog.show();
                return;
            }

            ProcessVideoDialog dialog = new ProcessVideoDialog(activity);
            dialog.show();

            File resultFile = VideoChooseHelper.getInstance().getResultFile();
            if (resultFile != null && resultFile.exists()) {
                GVideoEventBus.get(EVENT_BUS_VIDEO_ADDRESS, ChooseVideoModel.class)
                        .post(new ChooseVideoModel(resultFile.getAbsolutePath()));
                activity.finish();
                return;
            }
            List<ImageVideoEntity> mImageVideoEntities = VideoChooseHelper.getInstance().getSelectEntity();
            if (mImageVideoEntities != null && mImageVideoEntities.size() == 1) {
                GVideoEventBus.get(EVENT_BUS_VIDEO_ADDRESS, ChooseVideoModel.class)
                        .post(new ChooseVideoModel(mImageVideoEntities.get(0).path));
                activity.finish();
                return;
            }

            mVideoRecordHelper.tryProcessUploadVideo()
                    .observeOn(GVideoSchedulers.MAIN)
                    .subscribe(new BaseGVideoResponseObserver<File>() {
                        @Override
                        protected void onRequestData(File resultFile) {
                            VideoChooseHelper.getInstance().updateResultFile(resultFile);
                            MediaScannerConnection.scanFile(
                                    GVideoRuntime.getAppContext(),
                                    new String[]{resultFile.getPath()},
                                    null,
                                    null
                            );
                            GVideoEventBus.get(EVENT_BUS_VIDEO_ADDRESS, ChooseVideoModel.class)
                                    .post(new ChooseVideoModel(resultFile.getAbsolutePath()));
                            activity.finish();
                        }
                    });
        } else {
            ArrayList<String> image = VideoChooseHelper.getInstance().getSelectAddressList();
            if (!TextUtils.isEmpty(takePhotoPath) && new File(takePhotoPath).exists()){
                if (operationType == SINGLE_PLACE){
                    //单张替换
                    image.add(0,takePhotoPath);
                }else {
                    image.add(takePhotoPath);
                }
            }
            GVideoEventBus.get(EVENT_BUS_SELECT_IMAGE_ADDRESS_LIST, ChooseImageListModel.class)
                    .post(new ChooseImageListModel(
                                    image,
                                    operationType,
                                    singleOperationIndex
                            )
                    );
            activity.finish();
        }
    }

    public void takePhoto(){
        if (!VideoChooseHelper.getInstance().checkCanSelectImage()){
            ToastUtils.showShort("超过最大数量限制");
            return;
        }
        PermissionManager
                .requestPermissions(AppManager.getAppManager().currentActivity(), new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photo = new File(context.getExternalCacheDir(), "image_"+System.currentTimeMillis()+".jpg");
                        Uri imageUri = FileProvider.getUriForFile(context, FilePlugin.AUTHORITY, photo);
                        if (imageUri != null) {
                            takePhotoPath = photo.getAbsolutePath();
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);
                        }
                    }

                    @Override
                    public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                        ToastUtils.showShort(R.string.refuse_permission_camera);
                    }
                }, Manifest.permission.CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_FROM_CAMERA:
                if (!TextUtils.isEmpty(takePhotoPath)) {
                    //将拍照图片复制包本地相册
                    File photo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (photo==null){
                        photo = new File("/storage/emulated/0/Pictures");
                        if (!photo.exists()) {
                            photo = null;
                        }
                    }
                    if (photo!=null) {
                        String pathUrl = photo.getAbsolutePath() + "/JinShiPin/" + +System.currentTimeMillis() + ".jpg";
                        GlobalExecutor.execute(() -> {
                            FileUtils.copyFile(new File(takePhotoPath), new File(pathUrl));
                        }, "record_copy_image", PRIORITY_USER);
                        //通知刷新相册
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(new File(pathUrl));
                        intent.setData(uri);
                    }
                    onItemFinishClick(AppManager.getAppManager().currentActivity());
                }
                break;
            default:
                break;
        }
    }

}
