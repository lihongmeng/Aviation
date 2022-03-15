package com.hzlz.aviation.feature.account.ui.authentication;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.repository.FileRepository;
import com.hzlz.aviation.feature.account.repository.UserRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;

/**
 * 身份认证界面 ViewModel
 *
 *
 * @since 2020-02-03 17:31
 */
public final class AuthenticationViewModel extends BaseViewModel {
  //<editor-fold desc="常量">
  // 照片类型
  private static final int PICTURE_TYPE_ID_CARD_FRONT = 1;
  private static final int PICTURE_TYPE_ID_CARD_BACK = 2;
  private static final int PICTURE_TYPE_ID_CARD_SELF = 3;
  // 请求码
  private static final int REQUEST_CODE_ID_CARD_FRONT_FROM_GALLERY = 1;
  private static final int REQUEST_CODE_ID_CARD_BACK_FROM_GALLERY = 2;
  private static final int REQUEST_CODE_ID_CARD_SELF_FROM_GALLERY = 3;
  private static final int REQUEST_CODE_ID_CARD_FRONT_FROM_CAMERA = 4;
  private static final int REQUEST_CODE_ID_CARD_BACK_FROM_CAMERA = 5;
  private static final int REQUEST_CODE_ID_CARD_SELF_FROM_CAMERA = 6;
  // 权限
  private static final int PERMISSION_CAMERA = 1;
  //
  private static final int FILE_BUSINESS_TYPE_ID_CARD = 2;
  //</editor-fold>

  //<editor-fold desc="属性">
  @NonNull
  private AuthenticationDataBinding mBinding = new AuthenticationDataBinding();
  @NonNull
  private CheckThreadLiveData<Boolean> mShowDialogLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Boolean> mTakePictureLiveData = new CheckThreadLiveData<>();
  private int mPictureType = 0;
  @Nullable
  private Uri mIdCardFrontUri;
  @Nullable
  private Uri mIdCardBackUri;
  @Nullable
  private Uri mIdCardSelfUri;
  //
  @NonNull
  private FileRepository mFileRepository = new FileRepository();
  @NonNull
  private UserRepository mUserRepository = new UserRepository();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public AuthenticationViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull
  AuthenticationDataBinding getBinding() {
    return mBinding;
  }

  @NonNull
  CheckThreadLiveData<Boolean> getDialogLiveData() {
    return mShowDialogLiveData;
  }

  @NonNull
  CheckThreadLiveData<Boolean> getTakePictureLiveData() {
    return mTakePictureLiveData;
  }

  /**
   * 拍照
   *
   * @param fragment Fragment
   */
  void takePicture(@NonNull Fragment fragment) {
    Context context = fragment.getContext();
    if (context == null) {
      return;
    }
    new RxPermissions(fragment).request(Manifest.permission.CAMERA).subscribe(aBoolean -> {
      if (aBoolean){
        doTakePicture(context);
      }else {
        showToast("没有相机权限");
      }
    });
//    PermissionManager
//        .with(context)
//        .permissions(Manifest.permission.CAMERA)
//        .request(new PermissionCallback() {
//          @Override
//          public void onPermissionGranted(@NonNull Context context) {
//            doTakePicture(context);
//          }
//
//          @Override
//          public void onPermissionDenied(
//              @NonNull Context context,
//              @Nullable String[] grantedPermissions,
//              @NonNull String[] deniedPermission) {
//            showToast("没有相机权限");
//          }
//        });
  }

  public void onCanTakePicture() {
    mTakePictureLiveData.setValue(true);
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
    switch (mPictureType) {
      case PICTURE_TYPE_ID_CARD_FRONT:
        requestCode = REQUEST_CODE_ID_CARD_FRONT_FROM_CAMERA;
        if (mIdCardFrontUri == null) {
          File photo = new File(context.getExternalCacheDir(), "id_card_front.jpg");
          mIdCardFrontUri = FileProvider.getUriForFile(
              context, FilePlugin.AUTHORITY, photo
          );
        }
        imageUri = mIdCardFrontUri;
        break;
      case PICTURE_TYPE_ID_CARD_BACK:
        requestCode = REQUEST_CODE_ID_CARD_BACK_FROM_CAMERA;
        if (mIdCardBackUri == null) {
          File photo = new File(context.getExternalCacheDir(), "id_card_back.jpg");
          mIdCardBackUri = FileProvider.getUriForFile(
              context, FilePlugin.AUTHORITY, photo
          );
        }
        imageUri = mIdCardBackUri;
        break;
      case PICTURE_TYPE_ID_CARD_SELF:
        requestCode = REQUEST_CODE_ID_CARD_SELF_FROM_CAMERA;
        if (mIdCardSelfUri == null) {
          File photo = new File(context.getExternalCacheDir(), "id_card_self.jpg");
          mIdCardSelfUri = FileProvider.getUriForFile(
              context, FilePlugin.AUTHORITY, photo
          );
        }
        imageUri = mIdCardSelfUri;
        break;
      default:
        break;
    }
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
    switch (mPictureType) {
      case PICTURE_TYPE_ID_CARD_FRONT:
        startActivityForResult(intent, REQUEST_CODE_ID_CARD_FRONT_FROM_GALLERY);
        break;
      case PICTURE_TYPE_ID_CARD_BACK:
        startActivityForResult(intent, REQUEST_CODE_ID_CARD_BACK_FROM_GALLERY);
        break;
      case PICTURE_TYPE_ID_CARD_SELF:
        startActivityForResult(intent, REQUEST_CODE_ID_CARD_SELF_FROM_GALLERY);
        break;
      default:
        break;
    }
  }
  //</editor-fold>

  //<editor-fold desc="数据绑定">

  /**
   * 选择身份证正面照片
   *
   * @param view 被点击的控件
   */
  public void selectIdCardFrontPicture(@NonNull View view) {
    mPictureType = PICTURE_TYPE_ID_CARD_FRONT;
    mShowDialogLiveData.setValue(true);
  }

  /**
   * 清除身份证正面照片
   */
  public void clearIdCardFrontPicture() {
    mBinding.clearIdCardFront();
  }

  /**
   * 选择身份证背面照片
   *
   * @param view 被点击的控件
   */
  public void selectIdCardBackPicture(@NonNull View view) {
    mPictureType = PICTURE_TYPE_ID_CARD_BACK;
    mShowDialogLiveData.setValue(true);
  }

  /**
   * 清除身份证背面照片
   */
  public void clearIdCardBackPicture() {
    mBinding.clearIdCardBack();
  }

  /**
   * 选择手持身份证照片
   *
   * @param view 被点击的控件
   */
  public void selectIdCardSelfPicture(@NonNull View view) {
    mPictureType = PICTURE_TYPE_ID_CARD_SELF;
    mShowDialogLiveData.setValue(true);
  }

  /**
   * 清除手持身份证照片
   */
  public void clearIdCardSelfPicture() {
    mBinding.clearIdCardSelf();
  }

  /**
   * 提交身份认证
   *
   * @param view 被点击的视图
   */
  public void submitAuthentication(@NonNull View view) {
    final String idCardNumber = mBinding.idCardNumber.get();
    if (idCardNumber == null
        || mIdCardFrontUri == null
        || mIdCardBackUri == null
        || mIdCardSelfUri == null) {
      return;
    }
    Observable.zip(
        mFileRepository.doUploadImageFile(FILE_BUSINESS_TYPE_ID_CARD, mIdCardFrontUri,null),
        mFileRepository.doUploadImageFile(FILE_BUSINESS_TYPE_ID_CARD, mIdCardBackUri,null),
        mFileRepository.doUploadImageFile(FILE_BUSINESS_TYPE_ID_CARD, mIdCardSelfUri,null),
        (idCardFrontFileId, idCardBackFileId, idCardSelfId) -> new String[] {
            idCardFrontFileId, idCardBackFileId, idCardSelfId
        })
        .subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND)
        .flatMap(fileIds -> mUserRepository.authenticateIdCard(
            idCardNumber, fileIds[0], fileIds[1], fileIds[2]
        ))
        .observeOn(GVideoSchedulers.MAIN)
        .subscribe(new GVideoResponseObserver<User>() {
          @Override
          protected boolean isShowNetworkDialog() {
            return true;
          }

          @Override
          protected int getNetworkDialogTipTextResId() {
            return R.string.uploading_authentication_info;
          }

          @Override
          protected void onSuccess(@NonNull User user) {
            Navigation.findNavController(view).popBackStack();
          }
        });
  }
  //</editor-fold>

  //<editor-fold desc="ActivityResult">

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    switch (requestCode) {
      // 相机
      case REQUEST_CODE_ID_CARD_FRONT_FROM_CAMERA:
        if (mIdCardFrontUri != null) {
          mBinding.setIdCardFrontUri(mIdCardFrontUri);
        }
        break;
      case REQUEST_CODE_ID_CARD_BACK_FROM_CAMERA:
        if (mIdCardBackUri != null) {
          mBinding.setIdCardBackUri(mIdCardBackUri);
        }
        break;
      case REQUEST_CODE_ID_CARD_SELF_FROM_CAMERA:
        if (mIdCardSelfUri != null) {
          mBinding.setIdCardSelfUri(mIdCardSelfUri);
        }
        break;
      // 相册
      case REQUEST_CODE_ID_CARD_FRONT_FROM_GALLERY:
        if (data != null && data.getData() != null) {
          mIdCardFrontUri = data.getData();
          mBinding.setIdCardFrontUri(mIdCardFrontUri);
        }
        break;
      case REQUEST_CODE_ID_CARD_BACK_FROM_GALLERY:
        if (data != null && data.getData() != null) {
          mIdCardBackUri = data.getData();
          mBinding.setIdCardBackUri(mIdCardBackUri);
        }
        break;
      case REQUEST_CODE_ID_CARD_SELF_FROM_GALLERY:
        if (data != null && data.getData() != null) {
          mIdCardSelfUri = data.getData();
          mBinding.setIdCardSelfUri(mIdCardSelfUri);
        }
        break;
      default:
        break;
    }
  }

  //</editor-fold>
}
