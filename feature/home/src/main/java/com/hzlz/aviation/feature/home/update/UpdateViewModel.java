package com.hzlz.aviation.feature.home.update;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.hzlz.aviation.feature.home.R;
import com.hzlz.aviation.feature.home.repository.UpdateRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.model.update.UpdateModel;
import com.hzlz.aviation.kernel.base.plugin.InitializationPlugin;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;

public class UpdateViewModel extends BaseViewModel {
  private static final boolean DEBUG = true;
  private static final String TAG = UpdateViewModel.class.getSimpleName();

  public MutableLiveData<UpdateModel> mCheckUpdateLiveData = new MutableLiveData<>();
  private UpdateRepository mUpdateRepository = new UpdateRepository();

  public UpdateViewModel(@NonNull Application application) {
    super(application);
  }

  public void checkUpdate() {
    PluginManager.get(InitializationPlugin.class)
        .getInitializationPRepository()
        .getInitializationConfigure()
        .subscribe(new GVideoResponseObserver<Object>() {
          @Override protected void onSuccess(@NonNull Object o) {

          }
        });
  }

  public void checkUpdateModel(UpdateModel updateModel) {
    mUpdateRepository.getUpdateMsg(updateModel).subscribe(new GVideoResponseObserver<UpdateModel>() {
      @Override protected void onSuccess(@NonNull UpdateModel updateModel) {
        mCheckUpdateLiveData.postValue(updateModel);
      }

      @Override public void onFailed(Throwable e) {
        mCheckUpdateLiveData.postValue(null);
      }
    });
  }

  public void download(String url, String path) {
    DownloadService.start(GVideoRuntime.getAppContext(), url, path);
    Toast.makeText(GVideoRuntime.getAppContext(), R.string.update_background,Toast.LENGTH_SHORT).show();
  }

  public void dismiss(UpdateModel model) {
    mUpdateRepository.saveDismissTime(model);
  }
}
