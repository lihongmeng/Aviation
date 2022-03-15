package com.hzlz.aviation.feature.account.ui.crop;

import static com.hzlz.aviation.kernel.base.Constant.CROP;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.crop.CropImageView;

import java.io.File;

public final class CropViewModel extends BaseViewModel {
  private CheckThreadLiveData<Uri> mCropLiveData = new CheckThreadLiveData<>();
  public CropViewModel(@NonNull Application application) {
    super(application);
  }

  LiveData<Uri> getCropLiveData() {
    return mCropLiveData;
  }
  void crop() {
    File photo = new File(getApplication().getExternalCacheDir(), "avatar_crop.jpg");
    Uri uri = FileProvider.getUriForFile(
        getApplication(), FilePlugin.AUTHORITY, photo
    );
    mCropLiveData.setValue(uri);
  }

  void onCropComplete(@NonNull CropImageView view, @NonNull CropImageView.CropResult result) {
    if (result.isSuccessful()) {
      GVideoEventBus.get(CROP, Uri.class).post(result.getUri());
      Navigation.findNavController(view).popBackStack();
    } else {
      showToast(R.string.fragment_profile_crop_error);
    }
  }
}
