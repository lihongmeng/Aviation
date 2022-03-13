package com.jxntv.account.ui.authentication;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentAuthenticationBinding;
import com.jxntv.base.BaseFragment;
import com.jxntv.dialog.GVideoBottomSheetItemDialog;
import com.jxntv.stat.StatPid;

/**
 * 认证界面
 *
 *
 * @since 2020-02-03 16:03
 */
@SuppressWarnings("FieldCanBeLocal")
public final class AuthenticationFragment extends BaseFragment<FragmentAuthenticationBinding>
    implements GVideoBottomSheetItemDialog.OnItemSelectedListener {
  //<editor-fold desc="属性">
  private AuthenticationViewModel mViewModel;
  @Nullable
  private GVideoBottomSheetItemDialog mCameraGalleryDialog;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_authentication;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.all_real_name_authentication);
    openSoftKeyBoardDelay(mBinding.editTextIdCardNumber);
  }

  @Override
  protected void bindViewModels() {
    mViewModel = bingViewModel(AuthenticationViewModel.class);
    mBinding.setViewModel(mViewModel);
    mBinding.setBinding(mViewModel.getBinding());
    mViewModel.getDialogLiveData().observe(this, new NotNullObserver<Boolean>() {
      @Override
      protected void onModelChanged(@NonNull Boolean show) {
        if (show) {
          showCameraGalleryDialog();
        }
      }
    });
    mViewModel.getTakePictureLiveData().observe(this, new NotNullObserver<Boolean>() {
      @Override
      protected void onModelChanged(@NonNull Boolean take) {
        if (take) {
          Context context = getContext();
          if (context != null) {
            mViewModel.doTakePicture(getContext());
          }
        }
      }
    });
  }

  @Override
  protected void loadData() {

  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 显示
   */
  private void showCameraGalleryDialog() {
    Context context = getContext();
    if (context == null) {
      return;
    }
    if (mCameraGalleryDialog == null) {
      mCameraGalleryDialog = new GVideoBottomSheetItemDialog.Builder(context)
          .addItem(R.string.fragment_authentication_camera)
          .addItem(R.string.fragment_authentication_gallery)
          .cancel(R.string.dialog_back)
          .itemSelectedListener(this)
          .build();
    }
    if (!mCameraGalleryDialog.isShowing()) {
      mCameraGalleryDialog.show();
    }
  }
  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onItemSelected(@NonNull GVideoBottomSheetItemDialog dialog, int position) {
    if (dialog.equals(mCameraGalleryDialog)) {
      switch (position) {
        case 0:
          mViewModel.takePicture(this);
          break;
        case 1:
          mViewModel.selectPictureFromGallery();
          break;
        default:
          break;
      }
    }
  }
  //</editor-fold>

  //<editor-fold desc="生命周期">
  @Override
  public void onDestroy() {
    if (mCameraGalleryDialog != null) {
      mCameraGalleryDialog.dismiss();
      mCameraGalleryDialog = null;
    }
    closeSoftKeyboard();
    super.onDestroy();
  }
  //</editor-fold>

  @Override
  public String getPid() {
    return StatPid.AUTHENTICATION;
  }

}
