package com.hzlz.aviation.feature.record.recorder.fragment.upload;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hzlz.aviation.feature.record.databinding.FragmentUploadVideoBinding;
import com.hzlz.aviation.feature.record.recorder.data.ImageVideoEntity;
import com.hzlz.aviation.feature.record.recorder.fragment.crop.CropVideoFragmentArgs;
import com.hzlz.aviation.feature.record.recorder.helper.VideoChooseHelper;
import com.hzlz.aviation.feature.record.recorder.helper.VideoRecordHelper;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.record.R;

import java.io.File;

/**
 * 上传界面
 */
public class  UploadFragment extends BaseFragment<FragmentUploadVideoBinding> {

  /** 当前fragment持有的view model */
  private UploadViewModel mUploadViewModel;

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_upload_video;
  }

  @Override
  protected boolean enableImmersive() {
    return true;
  }

  @Override
  protected void initView() {
    mBinding.etEdit.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (mUploadViewModel == null) {
          return;
        }
        if (s == null) {
          mUploadViewModel.updateIntroduction(null);
          mUploadViewModel.updateEditTextNum(0);
          return;
        }
        String text = s.toString().trim();
        mUploadViewModel.updateIntroduction(text);
        if (TextUtils.isEmpty(text)) {
          mUploadViewModel.updateEditTextNum(0);
          return;
        }
        int length = text.length();
        mUploadViewModel.updateEditTextNum(length);
        mBinding.tvEditNum.setTextColor(ResourcesUtils.getColor(
            length >= 30 ? R.color.t_color06 : R.color.t_color03));
      }
    });
  }

  @Override
  protected void bindViewModels() {
    mUploadViewModel = bingViewModel(UploadViewModel.class);
    mBinding.setViewModel(mUploadViewModel);
    mUploadViewModel.getCropLiveData().observe(this, new NotNullObserver<Uri>() {
      @Override protected void onModelChanged(@NonNull Uri uri) {
        CropVideoFragmentArgs args = new CropVideoFragmentArgs.Builder(uri).build();
        Navigation.findNavController(mBinding.getRoot()).navigate(R.id.cropVideoFragment, args.toBundle());
      }
    });
    String filePath = getArguments() != null ? getArguments().getString(
        VideoRecordHelper.FILE_UPLOAD_PATH) : null;
    if (!TextUtils.isEmpty(filePath)) {
      mUploadViewModel.init(new File(filePath));
    }
    mUploadViewModel.isPublic.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
      @Override
      public void onPropertyChanged(Observable sender, int propertyId) {
        if (mUploadViewModel.isPublic.equals(sender)) {
          boolean isPublic = mUploadViewModel.isPublic.get();
          mBinding.uploadVideoPublic.setTypeface(Typeface.defaultFromStyle(
              isPublic ? Typeface.BOLD : Typeface.NORMAL));
          mBinding.uploadVideoPrivate.setTypeface(Typeface.defaultFromStyle(
              isPublic ? Typeface.NORMAL : Typeface.BOLD));
        }
      }
    });
    mUploadViewModel.publishResult.observe(this, new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean result) {
        if (result != null && result) {
          Activity activity = getActivity();
          if (activity == null || activity.isFinishing()) {
            return;
          }
          PluginManager.get(HomePlugin.class).navigateToHomePersonFragment(activity);
          GVideoEventBus.get(AccountPlugin.EVENT_REFRESH_DATA).post(0);
        }
      }
    });
  }

  @Override
  protected void loadData() {
    File uploadFile = mUploadViewModel.getUploadFile();
    if (uploadFile == null || !uploadFile.exists()) {
      Navigation.findNavController(mBinding.getRoot()).popBackStack();
      return;
    }
    PluginManager.get(AccountPlugin.class).addDestinations(this);
    PluginManager.get(CirclePlugin.class).addDestinations(this);
    GVideoEventBus.get("cropVideoImage", Uri.class).observe(this, new Observer<Uri>() {
      @Override public void onChanged(Uri uri) {
        Glide.with(mBinding.playerView.getContext())
            .load(uri)
            .into(new CustomTarget<Drawable>() {
              @Override
              public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mUploadViewModel.onImageSourceReady(resource);
                mBinding.playerView.setBackground(resource);
              }

              @Override
              public void onLoadCleared(@Nullable Drawable placeholder) {

              }
            });

      }
    });
    ImageVideoEntity entity = new ImageVideoEntity();
    entity.path = uploadFile.getAbsolutePath();
    VideoChooseHelper.getInstance().setPreviewVideoEntity(entity);
    Glide.with(mBinding.playerView.getContext())
        .load(Uri.fromFile(uploadFile))
        .placeholder(GVideoRuntime.getAppContext().getResources().getDrawable(R.drawable.ic_choose_default))
        .into(new CustomTarget<Drawable>() {
          @Override
          public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            mUploadViewModel.onImageSourceReady(resource);
            mBinding.playerView.setBackground(resource);
          }

          @Override
          public void onLoadCleared(@Nullable Drawable placeholder) {

          }
        });
  }

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
