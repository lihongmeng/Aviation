package com.jxntv.record.recorder.fragment.upload;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import com.jxntv.async.GlobalExecutor;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.dialog.DefaultStatusDialog;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.StringUtils;
import com.jxntv.dialog.GVideoDialog;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.NetworkUtils;
import com.jxntv.record.R;
import com.jxntv.record.recorder.dialog.IdentificationConfirmDialog;
import com.jxntv.record.recorder.dialog.PublishConfirmCancelDialog;
import com.jxntv.record.recorder.dialog.PublishProgressVideoDialog;
import com.jxntv.record.recorder.fragment.preview.PreviewVideoFragment;
import com.jxntv.record.recorder.helper.UploadHelper;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.FileUtils;
import com.jxntv.utils.ResourcesUtils;
import java.io.File;

/**
 * 上传界面数据模型
 */
public class UploadViewModel extends BaseViewModel {

  /** 上传界面数据模型 */
  private static final int REQUEST_CODE_FROM_GALLERY = 1;
  /** 失败事件弹窗时长 */
  private static final int RESULT_FAIL_DURATION = 2000;
  /** 成功事件弹窗时长 */
  private static final int RESULT_SUCCESS_DURATION = 1000;
  /** 本地保存图片后缀 */
  private static final String UPLOAD_IMAGE_SUFFIX = ".jepg";
  /** 默认编辑text后缀 */
  private static final String DEFAULT_EDIT_TEXT = "/30";
  /** 上传文件 */
  private File mUploadFile = null;
  /** 上传图片文件 */
  private File mUploadImageFile = null;
  /** 本地简介 */
  private String mIntroduction = null;

  /** 修剪图片结果live data */
  @NonNull
  private CheckThreadLiveData<Uri> mCropLiveData = new CheckThreadLiveData<>();
  /** 编辑字数 */
  @NonNull
  public ObservableField<String> editTextNum = new ObservableField<>();
  /** 是否公开 */
  public ObservableBoolean isPublic;
  /** 发布结果 */
  MutableLiveData<Boolean> publishResult = new MutableLiveData<>();
  /** 是否可发布 */
  @NonNull
  public ObservableBoolean enableNext = new ObservableBoolean(false);

  /**
   * 构造函数
   */
  public UploadViewModel(@NonNull Application application) {
    super(application);
    editTextNum.set("0" + DEFAULT_EDIT_TEXT);
    boolean hasIdentityVerified = PluginManager.get(AccountPlugin.class).getAuditStatus()
        == AccountPlugin.VERIFICATION_STATUS_VERIFIED;
    isPublic = new ObservableBoolean(hasIdentityVerified);
  }

  /**
   * 初始化
   *
   * @param uploadFile 待上传文件
   */
  public void init(File uploadFile) {
    mUploadFile = uploadFile;
  }

  /**
   * 获取当前待上传文件
   *
   * @return 当前待上传文件
   */
  File getUploadFile() {
    return mUploadFile;
  }

  /**
   * 更新编辑text 数量
   *
   * @param num 更新本地编辑文字数量
   */
  void updateEditTextNum(int num) {
    editTextNum.set(num + DEFAULT_EDIT_TEXT);
    enableNext.set(num > 0);
  }

  /**
   * 更新简介
   *
   * @param introduction 更新简介
   */
  void updateIntroduction(String introduction) {
    introduction = StringUtils.filterWhiteSpace(introduction);
    mIntroduction = introduction;
  }

  /**
   * 图片加载完成
   *
   * @param drawable 待处理的drawable
   */
  void onImageSourceReady(Drawable drawable) {
    GlobalExecutor.execute(new Runnable() {
      @Override
      public void run() {
        if (mUploadFile == null) {
          return;
        }
        String uploadFile = mUploadFile.getAbsolutePath();
        if (TextUtils.isEmpty(uploadFile)) {
          return;
        }
        String imageFile = FileUtils.getFileNameWithOutSuffix(uploadFile) + UPLOAD_IMAGE_SUFFIX;
        File resultFile = FileUtils.saveDrawableFile(imageFile, drawable);
        if (resultFile != null) {
          mUploadImageFile = resultFile;
        }
      }
    }, "save record img", GlobalExecutor.PRIORITY_USER);
  }

  /**
   * 获取剪切live data
   *
   * @return 剪切live data
   */
  @NonNull
  LiveData<Uri> getCropLiveData() {
    return mCropLiveData;
  }

  /**
   * 预览点击事件
   *
   * @param v 被点击的视图
   */
  public void onPreviewVideoClick(View v) {
    Bundle bundle = new Bundle();
    bundle.putString(PreviewVideoFragment.INTENT_PREVIEW_TYPE , PreviewVideoFragment.INTENT_PREVIEW_TYPE_NORMAL);
    Navigation.findNavController(v).navigate(R.id.previewVideoFragment, bundle);
  }

  /**
   * 更换封面点击处理事件
   *
   * @param v 被点击的视图
   */
  public void onChangeCoverClick(View v) {
    PermissionManager
        .requestPermissions(v.getContext(), new PermissionCallback() {
          @Override
          public void onPermissionGranted(@NonNull Context context) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_FROM_GALLERY);
          }

          @Override
          public void onPermissionDenied(
              @NonNull Context context,
              @Nullable String[] grantedPermissions,
              @NonNull String[] deniedPermission) {
            showToast("没有存储相关权限");
          }
        },Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
  }

  /**
   * 裁剪图片
   *
   * @param uri 裁剪图片的uri
   */
  private void cropImage(Uri uri) {
    mCropLiveData.setValue(uri);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    switch (requestCode) {
      // 相册
      case REQUEST_CODE_FROM_GALLERY:
        if (data != null && data.getData() != null) {
          cropImage(data.getData());
        }
        break;
      default:
        break;
    }
  }

  /**
   * 改变权限设置
   *
   * @param isPublic  是否公开
   */
  public void changeLimit(boolean isPublic, View view) {
    if (!isPublic) {
      this.isPublic.set(false);
      return;
    }
    int auditStatus = PluginManager.get(AccountPlugin.class).getAuditStatus();
    switch (auditStatus) {
      case  AccountPlugin.VERIFICATION_STATUS_VERIFIED:
        // 已有权限，直接改变即可
        this.isPublic.set(true);
        break;
      case AccountPlugin.VERIFICATION_STATUS_NO_VERIFY:
        IdentificationConfirmDialog dialog = new IdentificationConfirmDialog(view.getContext());
        dialog.init(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
          }
        }, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
            // 未申请权限，跳转到认证页面
            PluginManager.get(AccountPlugin.class).startAccountSecurityFragment(view);
          }
        });
        dialog.show();
        break;
      case AccountPlugin.VERIFICATION_STATUS_REJECT:
      case AccountPlugin.VERIFICATION_STATUS_VERIFYING:
        // 权限提示
        Toast toast = Toast.makeText(GVideoRuntime.getAppContext(),
            GVideoRuntime.getAppContext().getResources().getString(R.string.identify_ing_text),
            Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
      default:
        break;
    }
  }

  /**
   * 处理发布点击事件
   *
   * @param view 被点击的视图
   */
  public void onPublish(View view) {
    // 1. 判断网络
    if (!NetworkUtils.isNetworkConnected()) {
      showResultDialog(view, null,
          false, RESULT_FAIL_DURATION, false);
      return;
    }
    // 2. 判断是否为wifi
    if (NetworkUtils.isWifiNetworkConnected()) {
      // 是wifi则直接进行发布
      handlePublish(view);
      return;
    }
    // 3. 不适wifi需要弹窗提醒
    PublishConfirmCancelDialog confirmDialog = new PublishConfirmCancelDialog(view.getContext());
    confirmDialog.init(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        confirmDialog.dismiss();
      }
    }, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        confirmDialog.dismiss();
        // 确认发布
        handlePublish(view);
      }
    });
    confirmDialog.show();
  }

  /**
   * 处理发布事件
   *
   * @param view 被点击的视图
   */
  private void handlePublish(View view) {
    // 1. 显示进度弹窗
    PublishProgressVideoDialog dialog = new PublishProgressVideoDialog(view.getContext());
    dialog.show();
    // 2. 生成辅助类，处理发布流程
    UploadHelper helper = new UploadHelper(new UploadHelper.UploadListener() {
      @Override
      public void onProgress(int rate) {
        view.post(() -> dialog.updateProgressText(rate));
      }

      @Override
      public void onSuccess() {
        view.post(() -> showResultDialog(view, dialog, true, RESULT_SUCCESS_DURATION, false));
      }

      @Override
      public void onError(String code, String message) {
        view.post(() -> showResultDialog(view, dialog, false, RESULT_FAIL_DURATION,
            TextUtils.equals(code, UploadHelper.ERROR_TIME_LIMIT)));
      }

      @Override
      public void onStart() {
      }
    }, mIntroduction, isPublic.get());
    helper.uploadFile(mUploadImageFile, mUploadFile);
  }

  /**
   * 显示处理结果弹窗
   *
   * @param view      被点击的视图
   * @param dialog    当前显示的dialog
   * @param isSuccess 是否成功
   * @param duration  显示时长
   * @param isTimeOut 是否超时
   */
  private void showResultDialog(View view, GVideoDialog dialog,
                                boolean isSuccess, long duration, boolean isTimeOut) {
    // 关掉当前的dialog
    if (dialog != null) {
      dialog.dismiss();
    }
    int errorResource = NetworkUtils.isNetworkConnected() && !isTimeOut ? R.string.publish_fail : R.string.publish_network_fail;
    int type = isSuccess ? DefaultStatusDialog.TYPE_SUCCESS : DefaultStatusDialog.TYPE_FAIL;
    String text = ResourcesUtils.getString(isSuccess ? R.string.publish_success : errorResource);
    DefaultStatusDialog statusDialog = new DefaultStatusDialog(view.getContext(),
        R.style.CenterDialogStyle_NoDarkBackground, type, text);
    statusDialog.show();
    view.postDelayed(new Runnable() {
      @Override
      public void run() {
        statusDialog.dismiss();
        if (isSuccess) {
          publishResult.postValue(true);
        }
      }
    }, duration);
  }

  /**
   * 发布取消点击事件
   *
   * @param  v  点击的view
   */
  public void onPublishBackCancel(View v) {
    Navigation.findNavController(v).popBackStack();
  }
}
