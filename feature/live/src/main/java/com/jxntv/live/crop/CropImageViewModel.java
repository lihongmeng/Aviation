package com.jxntv.live.crop;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.cropper.CropImageView;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.live.Constants;

import java.io.File;

public final class CropImageViewModel extends BaseViewModel {

    private CheckThreadLiveData<Uri> mCropLiveData = new CheckThreadLiveData<>();

    public CropImageViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<Uri> getCropLiveData() {
        return mCropLiveData;
    }

    void crop() {
        File photo = new File(getApplication().getExternalCacheDir(), "live_thumb_crop.jpg");
        Uri uri = FileProvider.getUriForFile(
                getApplication(), FilePlugin.AUTHORITY, photo
        );
        mCropLiveData.setValue(uri);
    }

    void onCropComplete(@NonNull CropImageView view, @NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            GVideoEventBus.get(Constants.EVENT_CROP, Uri.class).post(result.getUri());
            Navigation.findNavController(view).popBackStack();
        } else {
            showToast("图片保存失败");
        }
    }
}
