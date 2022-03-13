package com.jxntv.record.recorder.fragment.crop;

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
import com.jxntv.record.R;
import java.io.File;

/**
 * 剪切视频封面数据模型
 */
public class CropVideoModel extends BaseViewModel {

  /**
   *  裁剪图片live data
   */
  private CheckThreadLiveData<Uri> mCropLiveData = new CheckThreadLiveData<>();

  /**
   * 构造函数
   */
  public CropVideoModel(@NonNull Application application) {
    super(application);
  }

  /**
   *  获取裁剪uri
   */
  LiveData<Uri> getCropLiveData() {
    return mCropLiveData;
  }

  /**
   *  裁剪图片
   */
  void crop() {
    File photo = new File(getApplication().getExternalCacheDir(), System.currentTimeMillis() + "_video_file_crop.jpg");
    Uri uri = FileProvider.getUriForFile(
        getApplication(), FilePlugin.AUTHORITY, photo
    );
    mCropLiveData.setValue(uri);
  }

  /**
   * 图片裁剪完成事件
   *
   * @param view    裁剪视图
   * @param result  裁剪结果
   */
  void onCropComplete(@NonNull CropImageView view, @NonNull CropImageView.CropResult result) {
    if (result.isSuccessful()) {
      GVideoEventBus.get("cropVideoImage", Uri.class).post(result.getUri());
      Navigation.findNavController(view).popBackStack();
    } else {
      showToast(R.string.fragment_profile_crop_error);
    }
  }
}
