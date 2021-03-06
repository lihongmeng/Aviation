package com.hzlz.aviation.feature.record.recorder.fragment.choose;

import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.BATCH_REPLACE;
import static com.hzlz.aviation.feature.record.recorder.Constants.SELECT_IMAGE_TYPE.SINGLE_PLACE;
import static com.hzlz.aviation.kernel.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_CAMERA;
import static com.hzlz.aviation.kernel.base.plugin.RecordPlugin.EVENT_BUS_SELECT_IMAGE_ADDRESS_LIST;
import static com.hzlz.aviation.kernel.base.plugin.RecordPlugin.EVENT_BUS_VIDEO_ADDRESS;
import static com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_USER;

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

import com.hzlz.aviation.feature.record.recorder.data.ImageVideoEntity;
import com.hzlz.aviation.feature.record.recorder.dialog.ProcessVideoDialog;
import com.hzlz.aviation.feature.record.recorder.dialog.RecordConfirmDialog;
import com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper;
import com.hzlz.aviation.feature.record.recorder.helper.VideoRecordHelper;
import com.hzlz.aviation.feature.record.recorder.model.ChooseImageListModel;
import com.hzlz.aviation.feature.record.recorder.model.ChooseVideoModel;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.AppManager;
import com.hzlz.aviation.library.util.FileUtils;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.async.GlobalExecutor;
import com.hzlz.aviation.feature.record.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ????????????????????????
 */
public class ChooseImageVideoViewModel extends BaseViewModel implements VideoChooseHelper.ItemSelectListener {

    /**
     * ?????????????????????
     */
    MutableLiveData<Integer> selectItemNum = new MutableLiveData<>(0);
    /**
     * ?????????????????????
     */
    MutableLiveData<Float> selectItemTime = new MutableLiveData<>(0f);
    /**
     * ???????????????????????????
     */
    boolean isVideoType = true;
    @NonNull
    public ObservableBoolean enableNext = new ObservableBoolean(false);
    /**
     * ?????????????????????
     */
    private VideoRecordHelper mVideoRecordHelper;

    /**
     * ?????????????????????????????????
     */
    private int operationType = BATCH_REPLACE;

    /**
     * ?????????????????????????????????????????????
     */
    public int singleOperationIndex;
    /**
     * ????????????
     */
    private String takePhotoPath;

    /**
     * ????????????
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
     * ????????????
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
     * ??????????????????
     *
     * @param list ????????????????????????
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
     * ??????????????????
     *
     * @param list ????????????????????????
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
     * ????????????????????????
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
                    //????????????
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
            ToastUtils.showShort("????????????????????????");
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
                    //????????????????????????????????????
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
                        //??????????????????
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
