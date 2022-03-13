package com.jxntv.live.crop;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseFragment;
import com.jxntv.cropper.CropImageView;
import com.jxntv.live.Constants;
import com.jxntv.live.R;
import com.jxntv.live.databinding.FragmentCropImageBinding;
import com.jxntv.stat.StatPid;

public final class CropImageFragment extends BaseFragment<FragmentCropImageBinding> implements CropImageView.OnCropImageCompleteListener {

    private CropImageViewModel mCropViewModel;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding.imageViewCrop.setOnCropImageCompleteListener(null);
    }

    @Override
    public void onRightOperationPressed(@NonNull View view) {
        mCropViewModel.crop();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_crop_image;
    }

    @Override
    protected void initView() {
        showRightOperationTextView(true);
        setRightOperationTextViewText(R.string.all_ensure);
        mBinding.imageViewCrop.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void bindViewModels() {
        mCropViewModel = bingViewModel(CropImageViewModel.class);
        mBinding.setViewModel(mCropViewModel);

        mCropViewModel.getCropLiveData().observe(this,
                uri -> mBinding.imageViewCrop.saveCroppedImageAsync(uri));
    }

    @Override
    protected void loadData() {
        if (getArguments() != null) {
            Uri uri = getArguments().getParcelable(Constants.INTENT_IMAGE_URL);
            mBinding.imageViewCrop.setImageUriAsync(uri);
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        mCropViewModel.onCropComplete(view, result);

    }

    @Override
    public String getPid() {
        return StatPid.CROP_IMAGE;
    }

}
