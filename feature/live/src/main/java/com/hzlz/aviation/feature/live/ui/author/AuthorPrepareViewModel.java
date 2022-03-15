package com.hzlz.aviation.feature.live.ui.author;

import static com.hzlz.aviation.kernel.base.Constant.REQUEST_CODE_FROM.CAMERA;
import static com.hzlz.aviation.kernel.base.Constant.REQUEST_CODE_FROM.GALLERY;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.feature.live.LiveManager;
import com.hzlz.aviation.feature.live.model.OpenLiveResultModel;
import com.hzlz.aviation.feature.live.model.PlatformMessageModel;
import com.hzlz.aviation.feature.live.repository.LiveRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.plugin.IFileRepository;
import com.hzlz.aviation.kernel.base.plugin.LivePlugin;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.live.R;

import java.io.File;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc : 主播直播
 **/
public class AuthorPrepareViewModel extends BaseViewModel {

    // 平台Id
    private int platformId;

    // 封面图Uri
    private Uri liveThumbUri;

    // 直播类型
    public int liveType;

    @NonNull
    private final LiveRepository liveRepository = new LiveRepository();
    @NonNull
    public final CheckThreadLiveData<List<PlatformMessageModel>> platformList = new CheckThreadLiveData<>();
    @NonNull
    public final CheckThreadLiveData<Uri> cropUri = new CheckThreadLiveData<>();
    @NonNull
    public final CheckThreadLiveData<Object> pictureSelectMethodDialog = new CheckThreadLiveData<>();
    @NonNull
    public final CheckThreadLiveData<Object> selectLiveTypeDialog = new CheckThreadLiveData<>();
    @NonNull
    public final ObservableField<String> liveRoomIntroduction = new ObservableField<>();
    @NonNull
    public final ObservableField<String> liveRoomName = new ObservableField<>();
    @NonNull
    public final ObservableField<String> liveTypeName = new ObservableField<>(ResourcesUtils.getString(R.string.video_live));
    @NonNull
    public final ObservableField<Uri> cropThumbUri = new ObservableField<>();

    public AuthorPrepareViewModel(@NonNull Application application) {
        super(application);
        liveTypeName.set(ResourcesUtils.getString(R.string.video_live));
        liveType = Constant.LIVE_ROOM_TYPE.VIDEO;
    }

    public void init() {
        getPlatformListData();
    }

    /**
     * 获取账号所属入驻号
     */
    private void getPlatformListData() {
        liveRepository.getPlatformList().subscribe(new GVideoResponseObserver<List<PlatformMessageModel>>() {
            @Override
            protected void onSuccess(@NonNull List<PlatformMessageModel> platformMessages) {
                platformList.setValue(platformMessages);
            }
        });
    }

    public void onLiveThumbClicked() {
        pictureSelectMethodDialog.setValue("");
        LogUtils.e(liveRoomName.get() + " |  " + liveRoomIntroduction.get());
    }

    public void onSelectLiveTypeClicked() {
        selectLiveTypeDialog.setValue("");
    }

    /**
     * 发起直播
     *
     * @param clickView 点击的View
     */
    public void onStartLiveClicked(View clickView) {
        if (!LiveManager.getInstance().checkOrInitSuccess()) {
            showToast("发起直播失败，请重试");
            return;
        }
        String liveRoomNameString = liveRoomName.get();
        if (liveRoomNameString == null || TextUtils.isEmpty(liveRoomNameString)) {
            showToast("请填写直播间名称");
            return;
        }
        if (TextUtils.isEmpty(liveRoomNameString.trim())) {
            showToast("昵称不能全为空格");
            return;
        }
        Uri uri = cropThumbUri.get();
        String uriContent = (uri == null) ? "" : uri.toString();
        if (uri == null || TextUtils.isEmpty(uriContent)) {
            showToast("您还未选择直播封面图片");
            return;
        }
        PluginManager.get(FilePlugin.class).getFileRepository().doUploadImageFile(
                IFileRepository.FILE_BUSINESS_TYPE_MEDIA_COVER, uri, null).subscribe(new BaseViewModel.GVideoResponseObserver<String>() {
            @Override
            protected void onSuccess(@NonNull String thumbId) {
                startLive(thumbId, clickView);
            }

            @Override
            protected boolean isShowNetworkDialog() {
                return true;
            }

            @Override
            protected int getNetworkDialogTipTextResId() {
                return R.string.upload_live_thumb;
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                showToast(R.string.upload_error_retry);
            }
        });

    }


    /**
     * 发起直播
     *
     * @param thumbId 阿里云图片资源id
     */
    private void startLive(String thumbId, View view) {
        liveRepository.getStartLive(platformId, liveRoomName.get(), thumbId,
                liveRoomIntroduction.get(), liveType).subscribe(new BaseViewModel.GVideoResponseObserver<OpenLiveResultModel>() {
            @Override
            protected void onSuccess(@NonNull OpenLiveResultModel resultModel) {
                Context context = view.getContext();
                if (context == null) {
                    return;
                }
                PluginManager.get(LivePlugin.class).startAuthorLiveActivity(context,
                        resultModel.getMediaId(), liveRoomName.get(), resultModel.getShareUrl(), null);
                ((Activity) context).finish();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                showToast(throwable.getMessage());
            }
        });

    }

    public AdapterView.OnItemSelectedListener getItemSelectedClickListener() {

        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<PlatformMessageModel> platformMessageModelList = platformList.getValue();
                if (platformMessageModelList == null) {
                    return;
                }
                PlatformMessageModel data = platformMessageModelList.get(i);
                if (data == null) {
                    return;
                }
                platformId = data.getId();
                liveRoomName.set(data.getName() + "的直播间");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }


    /**
     * 拍照
     *
     * @param context 上下文
     */
    public void doTakePicture(@NonNull Context context) {
        PermissionManager
                .requestPermissions(context,new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = null;
                        int requestCode = -1;
                        requestCode = CAMERA;
                        File photo = new File(context.getExternalCacheDir(), "live_thumb.jpg");
                        imageUri = FileProvider.getUriForFile(context, FilePlugin.AUTHORITY, photo);
                        if (imageUri != null) {
                            liveThumbUri = imageUri;
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, liveThumbUri);
                            startActivityForResult(intent, requestCode);
                        }
                    }

                    @Override
                    public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions,
                                                   @NonNull String[] deniedPermission) {
                        showToast("没有相机权限");
                    }
                },Manifest.permission.CAMERA);
    }


    /**
     * 从相册选择图片
     */
    public void selectPictureFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY);
    }

    // 裁剪
    private void cropImage(Uri uri) {
        cropUri.setValue(uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // 相机
            case CAMERA:
                if (liveThumbUri != null) {
                    cropImage(liveThumbUri);
                }
                break;
            // 相册
            case GALLERY:
                if (data != null && data.getData() != null) {
                    cropImage(data.getData());
                }
                break;
            default:
                break;
        }
    }
}
