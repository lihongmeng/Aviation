package com.hzlz.aviation.feature.account.ui.nickname;

import static com.hzlz.aviation.kernel.base.Constant.AVATAR_URL;
import static com.hzlz.aviation.kernel.base.Constant.IS_JUST_SELECT;
import static com.hzlz.aviation.kernel.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_CAMERA;
import static com.hzlz.aviation.kernel.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_GALLERY;
import static com.hzlz.aviation.kernel.base.Constant.REQUEST_CODE_FROM.CAMERA;
import static com.hzlz.aviation.kernel.base.Constant.REQUEST_CODE_FROM.GALLERY;
import static com.hzlz.aviation.kernel.base.plugin.AccountPlugin.DEFAULT_AVATAR_LIST;

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

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.AvatarInfo;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.repository.FileRepository;
import com.hzlz.aviation.feature.account.repository.UserRepository;
import com.hzlz.aviation.feature.account.utils.HeaderUtils;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.utils.StringUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetItemDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class NickNameSetViewModel extends BaseViewModel {

    // ?????????????????????
    public String currentNickName;

    // ??????????????????????????????
    private ArrayList<AvatarInfo> avatarInfoList = new ArrayList<>();

    // ???????????????????????????
    private GVideoBottomSheetItemDialog mAvatarEntryDialog;

    // UserRepository
    private final UserRepository userRepository = new UserRepository();

    // ??????????????????????????????
    public ObservableBoolean confirmEnable = new ObservableBoolean(false);

    // ?????????????????????
    public final CheckThreadLiveData<String> avatar = new CheckThreadLiveData<>();

    // ??????View??????????????????
    public CheckThreadLiveData<Boolean> submitSuccess = new CheckThreadLiveData<>();

    // ????????????????????????????????????????????????????????????
    public Uri mAvatarUri;

    // 1?????????????????????????????????
    // 2????????????????????????????????????????????????
    // ????????????????????????
    public String picId;

    // ??????View???????????????????????????
    public CheckThreadLiveData<Boolean> cropImage = new CheckThreadLiveData<>();

    // ?????????????????????index
    public int randomIndex = -1;

    // ??????????????????????????????
    public boolean isSelectDefaultAvatar;

    public NickNameSetViewModel(@NonNull Application application) {
        super(application);
        initCurrentHeaderUrl();
    }

    /**
     * ????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????
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
            showToast("????????????????????????");
            return;
        }

        if (StringUtils.isNumeric(currentNickName)) {
            showToast("????????????????????????");
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
                showToast("??????????????????");
            }
        }, Manifest.permission.CAMERA);
    }

    /**
     * ??????
     *
     * @param context ?????????
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
            // ??????
            case REQUEST_CODE_FROM_CAMERA:
                cropImage.setValue(null);
                break;
            // ??????
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
