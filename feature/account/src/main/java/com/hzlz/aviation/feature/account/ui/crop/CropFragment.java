package com.hzlz.aviation.feature.account.ui.crop;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentCropBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.library.crop.CropImageView;

public final class CropFragment extends BaseFragment<FragmentCropBinding>
    implements CropImageView.OnCropImageCompleteListener {
  private CropViewModel mCropViewModel;

  @Override
  protected boolean showToolbar() {
    return true;
  }

  @Override
  protected boolean enableImmersive() {
    return true;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mBinding.imageViewCrop.setOnCropImageCompleteListener(null);
  }

  @Override public void onRightOperationPressed(@NonNull View view) {
    mCropViewModel.crop();
  }

  @Override protected int getLayoutId() {
    return R.layout.fragment_crop;
  }

  @Override protected void initView() {
    showRightOperationTextView(true);
    setRightOperationTextViewText(R.string.all_ensure);
    mBinding.imageViewCrop.setOnCropImageCompleteListener(this);
  }

  @Override protected void bindViewModels() {
    mCropViewModel = bingViewModel(CropViewModel.class);
    mBinding.setViewModel(mCropViewModel);

    mCropViewModel.getCropLiveData().observe(this,
        uri -> mBinding.imageViewCrop.saveCroppedImageAsync(uri));
  }

  @Override protected void loadData() {
    if (getArguments() != null) {
      Uri uri = CropFragmentArgs.fromBundle(getArguments()).getImageUri();
      mBinding.imageViewCrop.setImageUriAsync(uri);
    }
  }

  @Override public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
    mCropViewModel.onCropComplete(view, result);

  }

}
