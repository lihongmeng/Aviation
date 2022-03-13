package com.jxntv.account.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import com.jxntv.account.R;
import com.jxntv.account.model.RegionModel;
import com.jxntv.account.model.User;
import com.jxntv.account.model.annotation.AvatarEntry;
import com.jxntv.account.model.annotation.PrivacyRange;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.repository.FileRepository;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.account.utils.HeaderUtils;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.anotation.Gender;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.event.GVideoEventBus;

import java.io.File;
import java.util.Date;
import java.util.List;

import static com.jxntv.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_CAMERA;
import static com.jxntv.base.Constant.MODIFY_IMAGE_CODE.REQUEST_CODE_FROM_GALLERY;

/**
 * 个人中心 ViewModel
 *
 *
 * @since 2020-01-13 18:46
 */
public final class ProfileViewModel extends BaseViewModel {
  //<editor-fold desc="属性">
  @Nullable
  private Uri mAvatarUri;
  @NonNull
  private CheckThreadLiveData<Uri> mCropLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Object> mAvatarDialogLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Object> mGenderDialogLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Object> mUpdateGenderDialogLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Object> mBirthdayDialogLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Object> mUpdateBirthdayDialogLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<User> mUserLiveData = new CheckThreadLiveData<>();

  @NonNull
  private CheckThreadLiveData<Boolean> mUploadAvatarLiveData = new CheckThreadLiveData<>();
  @NonNull
  private FileRepository mFileRepository = new FileRepository();
  @NonNull
  private UserRepository mUserRepository = new UserRepository();

  @NonNull
  public CheckThreadLiveData<List<RegionModel>> mRegionLiveData = new CheckThreadLiveData<>();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public ProfileViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull
  public LiveData<Uri> getCropLiveData() {
    return mCropLiveData;
  }

  @NonNull
  public LiveData<Object> getAvatarLiveData() {
    return mAvatarDialogLiveData;
  }

  @NonNull
  LiveData<Object> getGenderLiveData() {
    return mGenderDialogLiveData;
  }

  @NonNull
  LiveData<Object> getUpdateGenderDialogLiveData() {
    return mUpdateGenderDialogLiveData;
  }

  @NonNull
  LiveData<Object> getBirthdayLiveData() {
    return mBirthdayDialogLiveData;
  }

  @NonNull
  LiveData<Object> getUpdateBirthdayDialogLiveData() {
    return mUpdateBirthdayDialogLiveData;
  }

  @NonNull
  public LiveData<User> getUserLiveData() {
    return mUserLiveData;
  }

  public void loadData() {
    mUserLiveData.setValue(UserManager.getCurrentUser());
    mUserRepository.getCurrentUser().subscribe(new GVideoResponseObserver<User>() {
      @Override
      protected void onSuccess(@NonNull User user) {
        mUserLiveData.setValue(user);
      }
    });
  }

 public void onAvatarEntrySelected(@NonNull View view, @AvatarEntry int avatarEntry) {
    if (avatarEntry == AvatarEntry.CAMERA) {
      takePicture(view.getContext());
    } else if (avatarEntry == AvatarEntry.GALLERY) {
      selectPictureFromGallery();
    } else {
      Navigation.findNavController(view).navigate(R.id.action_profile_to_modify_avatar);
    }
  }

  void onGenderSelected(@Gender int gender, @PrivacyRange int privacyRange) {
    mUserRepository.modifyUserGender(gender, privacyRange)
        .subscribe(new GVideoResponseObserver<User>() {
          @Override
          protected void onSuccess(@NonNull User user) {
            mUpdateGenderDialogLiveData.setValue(
                new Object[] { user.getGender(), user.getGenderPrivacyRange() }
            );
          }
        });
  }

  void onBirthdaySelected(@NonNull Date date, @PrivacyRange int privacyRange) {
    mUserRepository.modifyUserBirthday(date, privacyRange)
        .subscribe(new GVideoResponseObserver<User>() {
          @Override
          protected void onSuccess(@NonNull User user) {
            mUpdateBirthdayDialogLiveData.setValue(new Object[] {
                user.getBirthday(), user.getBirthdayPrivacyRange()
            });
          }
        });
  }

    void onRegionSelected(int provinceId,String province,int cityId, String city) {
        mUserRepository.modifyRegion(provinceId, province, cityId, city)
                .subscribe(new GVideoResponseObserver<User>() {
                    @Override
                    protected void onSuccess(@NonNull User user) {
//                        mUpdateBirthdayDialogLiveData.setValue(new Object[] {
//                                user.getBirthday(), user.getBirthdayPrivacyRange()
//                        });
                    }
                });
    }

  /**
   * 拍照
   *
   * @param context
   */
 public void takePicture(@NonNull Context context) {
    PermissionManager
        .requestPermissions(context, new PermissionCallback() {
          @Override
          public void onPermissionGranted(@NonNull Context context) {
            doTakePicture(context);
          }

          @Override
          public void onPermissionDenied(
              @NonNull Context context,
              @Nullable String[] grantedPermissions,
              @NonNull String[] deniedPermission) {
            showToast("没有相机权限");
          }
        }, Manifest.permission.CAMERA);
  }

  /**
   * 拍照
   *
   * @param context 上下文
   */
  void doTakePicture(@NonNull Context context) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Uri imageUri = null;
    int requestCode = -1;
    requestCode = REQUEST_CODE_FROM_CAMERA;
    File photo = new File(context.getExternalCacheDir(), "avatar.jpg");
    mAvatarUri = FileProvider.getUriForFile(
        context, FilePlugin.AUTHORITY, photo
    );
    imageUri = mAvatarUri;
    if (imageUri != null) {
      intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
      startActivityForResult(intent, requestCode);
    }
  }

  /**
   * 从相册选择图片
   */
  void selectPictureFromGallery() {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_PICK);
    intent.setType("image/*");
    startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
  }

  /**
   * 裁剪
   * @param uri
   */
  private void cropImage(Uri uri) {
    mCropLiveData.setValue(uri);
  }

  /**
   * 上传头像
   * @param uri
   */
  public void uploadAvatar(Context context,Uri uri) {
    mFileRepository.doUploadImageFile(FileRepository.FILE_BUSINESS_TYPE_USER_HEADER_PIC, uri,null)
        .flatMap(fileIds -> mUserRepository.modifyUserAvatar(fileIds))
        .subscribe(new GVideoResponseObserver<User>() {
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
          }

          @Override protected void onFailed(@NonNull Throwable throwable) {
            showToast(R.string.all_network_not_available_action_tip);
          }
        });

  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">

  /**
   * 修改头像
   *
   * @param view 被点击的 View
   */
  public void modifyAvatar(@NonNull View view) {
    User user = mUserLiveData.getValue();
    if (user != null) {
      mAvatarDialogLiveData.setValue(null);
    }
  }

  /**
   * 修改昵称界面
   *
   * @param view 被点击的视图
   */
  public void modifyNickName(@NonNull View view) {
    User user = mUserLiveData.getValue();
    if (user == null) {
      return;
    }
    Navigation.findNavController(view).navigate(
        ProfileFragmentDirections.actionProfileToModifyNickname(user.getNickname())
    );
  }

  /**
   * 修改性别
   */
  public void modifyGender() {
    User user = mUserLiveData.getValue();
    if (user != null) {
      mGenderDialogLiveData.setValue(new Object[] {
          user.getGender(), user.getGenderPrivacyRange()
      });
    }
  }

  /**
   * 修改生日
   */
  public void modifyBirthday() {
    User user = mUserLiveData.getValue();
    if (user != null) {
      mBirthdayDialogLiveData.setValue(new Object[] {
          user.getBirthday(), user.getBirthdayPrivacyRange()
      });
    }
  }

  /**
   * 修改地区
   */
  public void modifyRegion() {
      User user = mUserLiveData.getValue();
      if (user != null) {
          if (mRegionLiveData.getValue()!=null){
              mRegionLiveData.setValue(mRegionLiveData.getValue());
          }else {
              getRegionData();
          }
      }
  }

  /**
   * 修改简介界面
   *
   * @param view 被点击的视图
   */
  public void modifyDescription(@NonNull View view) {
    User user = mUserLiveData.getValue();
    if (user == null) {
      return;
    }
    Navigation.findNavController(view).navigate(
        ProfileFragmentDirections.actionProfileToModifyDescription(user.getDescription())
    );
  }
  //</editor-fold>

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    switch (requestCode) {
      // 相机
      case REQUEST_CODE_FROM_CAMERA:
        if (mAvatarUri != null) {
          cropImage(mAvatarUri);
        }
        break;
      // 相册
      case REQUEST_CODE_FROM_GALLERY:
        if (data != null && data.getData() != null) {
          mAvatarUri = data.getData();
          cropImage(mAvatarUri);
        }
        break;
      default:
        break;
    }
  }

  public void getRegionData(){
      mUserRepository.getRegionData().subscribe(new BaseGVideoResponseObserver<List<RegionModel>>(){
          @Override
          protected void onRequestData(List<RegionModel> regionModels) {
              if (regionModels!=null){
                  mRegionLiveData.setValue(regionModels);
              }
          }
      });
  }

}
