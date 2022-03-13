package com.jxntv.account.ui.crop;

import static com.jxntv.base.Constant.CROP;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import com.jxntv.account.R;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.cropper.CropImageView;
import com.jxntv.event.GVideoEventBus;
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
