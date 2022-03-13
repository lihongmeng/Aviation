package com.jxntv.test.environment;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.environment.EnvironmentManager;
import com.jxntv.base.environment.IEnvironment;
import com.jxntv.utils.AppManager;

import static com.jxntv.base.environment.IEnvironment.Type.OTHER;
import static com.jxntv.base.environment.IEnvironment.Type.RELEASE;
import static com.jxntv.base.environment.IEnvironment.Type.TEST_190;
import static com.jxntv.base.environment.IEnvironment.Type.TEST_47;

/**
 *
 * @since 2020-03-04 10:37
 */
public final class EnvironmentViewModel extends BaseViewModel {
  //<editor-fold desc="属性">
  @NonNull
  public ObservableField<String> currentEnvironment = new ObservableField<>();
  @NonNull
  public ObservableField<String> otherUrlObservable = new ObservableField<>();
  @NonNull
  private CheckThreadLiveData<Boolean> mEnableSwitchLiveData = new CheckThreadLiveData<>(false);
  @IEnvironment.Type
  private int mOriginalType;
  @IEnvironment.Type
  private int mCurrentType;
  @NonNull
  private String mCurrentEnvironment;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public EnvironmentViewModel(@NonNull Application application) {
    super(application);
    mCurrentType = EnvironmentManager.getInstance().getCurrentType();
    mOriginalType = mCurrentType;
    switch (mCurrentType) {
      case RELEASE:
        mCurrentEnvironment = "当前环境 : 正式服务器";
        break;
      case OTHER:
        String url = EnvironmentManager.getInstance().getCurrentAPIUrl();
        mCurrentEnvironment = "当前环境 : OTHER:" + url;
        otherUrlObservable.set(url);
        break;
      case TEST_47:
        mCurrentEnvironment = "当前环境 : 47服务器";
        break;
      case TEST_190:
        mCurrentEnvironment = "当前环境 : 190服务器";
        break;
      default:
        mCurrentEnvironment = "当前环境 : TEST服务器";
        break;
    }
    currentEnvironment.set(mCurrentEnvironment);
  }
  //</editor-fold>

  //<editor-fold desc="API">
  void switchEnvironment(@NonNull View view) {
    String otherUrl = otherUrlObservable.get();
    EnvironmentManager.getInstance().updateType(mCurrentType, otherUrl);
    AppManager.getAppManager().finishAllActivity();
  }

  @NonNull
  LiveData<Boolean> getEnableSwitchLiveData() {
    return mEnableSwitchLiveData;
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void test190Clicked() {
    changeEnvironment(TEST_190, "190服务器");
  }

  public void test47Clicked() {
    changeEnvironment(TEST_47, "47服务器");
  }


  public void releaseClicked() {
    changeEnvironment(RELEASE, "正式服务器");
  }

  public void otherClicked() {
    changeEnvironment(OTHER, "OTHER");
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void changeEnvironment(@IEnvironment.Type int type, @NonNull String text) {
    mCurrentType = type;
    if (mCurrentType != mOriginalType) {
      mEnableSwitchLiveData.setValue(true);
      currentEnvironment.set(mCurrentEnvironment + " ==> " + text);
    } else {
      mEnableSwitchLiveData.setValue(false);
      currentEnvironment.set(mCurrentEnvironment);
    }
  }
  //</editor-fold>
}
