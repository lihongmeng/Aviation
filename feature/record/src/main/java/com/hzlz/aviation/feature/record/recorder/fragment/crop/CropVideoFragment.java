package com.hzlz.aviation.feature.record.recorder.fragment.crop;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.record.databinding.FragmentCropVideoBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.crop.CropImageView;
import com.hzlz.aviation.feature.record.R;

/**
 * 裁剪视频
 */
public class CropVideoFragment extends BaseFragment<FragmentCropVideoBinding>
    implements CropImageView.OnCropImageCompleteListener{

  /** 当前fragment持有的view model */
  private CropVideoModel mCropViewModel;

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_crop_video;
  }

  @Override
  protected void initView() {
    showRightOperationTextView(true);
    setRightOperationTextViewText(R.string.all_ensure);
    mBinding.imageViewCrop.setOnCropImageCompleteListener(this);
  }

  @Override
  protected void bindViewModels() {
    mCropViewModel = bingViewModel(CropVideoModel.class);
    mBinding.setViewModel(mCropViewModel);

    mCropViewModel.getCropLiveData().observe(this,
        uri -> mBinding.imageViewCrop.saveCroppedImageAsync(uri));
  }

  @Override
  protected void loadData() {
    if (getArguments() != null) {
      Uri uri = CropVideoFragmentArgs.fromBundle(getArguments()).getCropImageUri();
      mBinding.imageViewCrop.setImageUriAsync(uri);
    }
  }

  @Override
  public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
    mCropViewModel.onCropComplete(view, result);
  }

  @Override
  public void onRightOperationPressed(@NonNull View view) {
    mCropViewModel.crop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mBinding.imageViewCrop.setOnCropImageCompleteListener(null);
  }

  @Override
  public String getPid() {
    return StatPid.CROP_VIDEO;
  }

}
