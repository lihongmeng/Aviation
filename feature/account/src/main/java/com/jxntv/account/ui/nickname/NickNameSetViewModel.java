package com.jxntv.account.ui.nickname;

import static com.jxntv.base.Constant.AVATAR_URL;
import static com.jxntv.base.Constant.IS_JUST_SELECT;
import static com.jxntv.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_CAMERA;
import static com.jxntv.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_GALLERY;
import static com.jxntv.base.Constant.REQUEST_CODE_FROM.CAMERA;
import static com.jxntv.base.Constant.REQUEST_CODE_FROM.GALLERY;
import static com.jxntv.base.plugin.AccountPlugin.DEFAULT_AVATAR_LIST;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.ObservableBoolean;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.model.AvatarInfo;
import com.jxntv.account.model.User;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.repository.FileRepository;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.account.utils.HeaderUtils;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.base.utils.StringUtils;
import com.jxntv.dialog.GVideoBottomSheetItemDialog;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.ResourcesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class NickNameSetViewModel extends BaseViewModel {

    // 当前设置的昵称
    public String currentNickName;

    // 获取到的默认头像列表
    private ArrayList<AvatarInfo> avatarInfoList = new ArrayList<>();

    // 图片选择方式的弹窗
    private GVideoBottomSheetItemDialog mAvatarEntryDialog;

    // UserRepository
    private final UserRepository userRepository = new UserRepository();

    // 是否可以点击确认按钮
    public ObservableBoolean confirmEnable = new ObservableBoolean(false);

    // 随机设置的头像
    public final CheckThreadLiveData<String> avatar = new CheckThreadLiveData<>();

    // 通知View层，提交成功
    public CheckThreadLiveData<Boolean> submitSuccess = new CheckThreadLiveData<>();

    // 保存相机、相册选择，以及裁剪后的图片地址
    public Uri mAvatarUri;

    // 1、选择默认头像直接返回
    // 2、用户选择相册、拍照后，执行上传
    // 以上两种方式得到
    public String picId;

    // 通知View层启动图片裁剪功能
    public CheckThreadLiveData<Boolean> cropImage = new CheckThreadLiveData<>();

    // 选择头像的随机index
    public int randomIndex = -1;

    // 用户是否选择默认头像
    public boolean isSelectDefaultAvatar;

    public NickNameSetViewModel(@NonNull Application application) {
        super(application);
        initCurrentHeaderUrl();
    }

    /**
     * 如果当前账户有头像，直接加载头像
     * 如果没有头像，先加载默认头像列表，并随机选一张作为头像
     * 同时做一个标记，如果
     */
    private void initCurrentHeaderUrl() {
        String userAvatar = PluginManager.get(AccountPlugin.class).getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            isSelectDefaultAvatar = false;
            avatar.setValue(userAvatar);
            return;
        }
        userRepository.getAvatarList()
                .subscribe(new GVideoResponseObserver<ArrayList<AvatarInfo>>() {
                    @Override
                    protected void onSuccess(@NonNull ArrayList<AvatarInfo> netData) {
                        isSelectDefaultAvatar = true;
                        avatarInfoList = netData;
                        randomIndex = (int) (Math.random() * (avatarInfoList.size() - 1));
                        AvatarInfo avatarInfo = avatarInfoList.get(randomIndex);
                        picId = avatarInfo.getId();
                        avatar.setValue(avatarInfo.getUrl());
                    }

                    @Override
                    protected void onAPIError(@NonNull Throwable throwable) {
                        super.onAPIError(throwable);
                        avatarInfoList.clear();
                    }

                    @Override
                    protected void onNetworkNotAvailableError(@NonNull Throwable throwable) {
                        super.onNetworkNotAvailableError(throwable);
                        avatarInfoList.clear();
                    }
                });
    }

    public void onConfirmClick(View view) {
        Context context = view.getContext();
        if (context == null) {
            return;
        }

        if (TextUtils.isEmpty(currentNickName) || TextUtils.isEmpty(currentNickName.trim())) {
            showToast("昵称不能全为空格");
            return;
        }

        if (StringUtils.isNumeric(currentNickName)) {
            showToast("昵称不能为纯数字");
            return;
        }

        String userAvatar = PluginManager.get(AccountPlugin.class).getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            dealNickNameModify();
            return;
        }

        UserRepository userRepository = new UserRepository();

        if (isSelectDefaultAvatar) {
            userRepository.modifyUserAvatar(picId).subscribe(new GVideoResponseObserver<User>() {

                @Override
                protected boolean isShowNetworkDialog() {
                    return true;
                }

                @Override
                protected int getNetworkDialogTipTextResId() {
                    return R.string.uploading_avatar;
                }

                @Override
                protected void onSuccess(@NonNull User user) {
                    user.update(user);
                    UserManager.getCurrentUser().setAvatarUrl(user.getAvatarUrl());
                    HeaderUtils.getInstance().preHeaderImage(context);
                    GVideoEventBus.get(AccountPlugin.EVENT_AVATAR_UPDATE).post(null);
                    dealNickNameModify();
                }

                @Override
                protected void onFailed(@NonNull Throwable throwable) {
                    showToast(R.string.all_network_not_available_action_tip);
                }
            });
            return;
        }

        new FileRepository().doUploadImageFile(FileRepository.FILE_BUSINESS_TYPE_USER_HEADER_PIC, mAvatarUri, null)
                .flatMap((Function<String, ObservableSource<User>>) pictureId -> {
                    picId = pictureId;
                    return userRepository.modifyUserAvatar(pictureId);
                }).subscribe(new GVideoResponseObserver<User>() {
            @Override
            protected boolean isShowNetworkDialog() {
                return true;
            }

            @Override
            protected int getNetworkDialogTipTextResId() {
                return R.string.uploading_avatar;
            }

            @Override
            protected void onSuccess(@NonNull User user) {
                user.update(user);
                UserManager.getCurrentUser().setAvatarUrl(user.getAvatarUrl());
                HeaderUtils.getInstance().preHeaderImage(context);
                GVideoEventBus.get(AccountPlugin.EVENT_AVATAR_UPDATE).post(null);
                dealNickNameModify();
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                showToast(R.string.all_network_not_available_action_tip);
            }
        });
    }

    private void dealNickNameModify() {
        new UserRepository().modifyUserNickname(StringUtils.filterWhiteSpace(currentNickName.trim()))
                .subscribe(new GVideoResponseObserver<User>() {
                    @Override
                    protected void onSuccess(@NonNull User user) {
                        submitSuccess.setValue(null);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        if (throwable instanceof TimeoutException) {
                            showToast(ResourcesUtils.getString(R.string.update_failed));
                            return;
                        }
                        showToast(throwable.getMessage());
                    }
                });
    }

    public void randomSetDefaultAvatar() {
        if (avatarInfoList == null || avatarInfoList.isEmpty()) {
            return;
        }
        int result = (int) (avatarInfoList.size() * Math.random());
        AvatarInfo avatarInfo = avatarInfoList.get(result);
        isSelectDefaultAvatar = true;
        picId = avatarInfo.getId();
        avatar.setValue(avatarInfo.getUrl());
        mAvatarUri = null;
    }

    public void showAvatarEntryDialog(View view) {
        if (mAvatarEntryDialog == null) {
            mAvatarEntryDialog = new GVideoBottomSheetItemDialog.Builder(view.getContext())
                    .addItem(R.string.fragment_modify_avatar_entry_local)
                    .addItem(R.string.fragment_modify_avatar_entry_gallery)
                    .addItem(R.string.fragment_modify_avatar_entry_camera)
                    .cancel(R.string.dialog_back)
                    .itemSelectedListener((dialog, position) -> {
                        switch (position) {
                            case 0:
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList(DEFAULT_AVATAR_LIST, avatarInfoList);
                                bundle.putString(AVATAR_URL, picId);
                                bundle.putBoolean(IS_JUST_SELECT, true);
                                Navigation.findNavController(view).navigate(R.id.action_profile_to_modify_avatar, bundle);
                                break;
                            case 1:
                                selectPictureFromGallery();
                                break;
                            case 2:
                                takePicture(view.getContext());
                                break;
                        }
                    })
                    .build();
        }
        if (!mAvatarEntryDialog.isShowing()) {
            mAvatarEntryDialog.show();
        }
    }

    public void takePicture(@NonNull Context context) {
        PermissionManager.requestPermissions(context, new PermissionCallback() {
            @Override
            public void onPermissionGranted(@NonNull Context context) {
                takePictureReal(context);
            }

            @Override
            public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                showToast("没有相机权限");
            }
        }, Manifest.permission.CAMERA);
    }

    /**
     * 拍照
     *
     * @param context 上下文
     */
    private void takePictureReal(@NonNull Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        int requestCode;
        requestCode = CAMERA;
        File photo = new File(context.getExternalCacheDir(), "modify_avatar_nick_name_avatar.jpg");
        imageUri = FileProvider.getUriForFile(context, FilePlugin.AUTHORITY, photo);
        if (imageUri != null) {
            mAvatarUri = imageUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mAvatarUri);
            startActivityForResult(intent, requestCode);
        }
    }

    private void selectPictureFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
        }

        switch (requestCode) {
            // 相机
            case REQUEST_CODE_FROM_CAMERA:
                cropImage.setValue(null);
                break;
            // 相册
            case REQUEST_CODE_FROM_GALLERY:
                if (uri != null) {
                    mAvatarUri = data.getData();
                    cropImage.setValue(null);
                }
                break;
            default:
                break;
        }
    }
}
