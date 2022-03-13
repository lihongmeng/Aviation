package com.jxntv.base;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jxntv.base.model.NetworkDialogModel;
import com.jxntv.base.model.PlaceholderModel;
import com.jxntv.base.model.StartActivityForResultModel;
import com.jxntv.base.model.ToastModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.StatPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.NetworkUtils;
import com.jxntv.network.exception.GVideoAPIException;
import com.jxntv.network.exception.GVideoCode;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.utils.ResourcesUtils;

import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * ViewModel 基类
 *
 *
 * @since 2020-01-06 21:33
 */
@SuppressWarnings("FieldCanBeLocal")
public abstract class BaseViewModel extends AndroidViewModel {

  private String pid;

  //<editor-fold desc="属性">
  // 吐司
  @NonNull
  private CheckThreadLiveData<ToastModel> mToastLiveData = new CheckThreadLiveData<>();
  // 占位布局
  @NonNull
  private CheckThreadLiveData<PlaceholderModel> mPlaceholderLiveData = new CheckThreadLiveData<>();
  // 网络弹窗
  @NonNull
  private CheckThreadLiveData<NetworkDialogModel> mNetworkDialogModelLiveData =
      new CheckThreadLiveData<>();
  // StartActivityForResult
  @NonNull
  private CheckThreadLiveData<StartActivityForResultModel>
      mStartActivityForResultModelLiveData = new CheckThreadLiveData<>();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public BaseViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="生命周期">

  @Override
  protected void onCleared() {
    // 通过反射取消所有请求，避免内存泄露
    Field[] fields = getClass().getDeclaredFields();
    for (Field field : fields) {
      try {
        boolean isAccessible = field.isAccessible();
        if (!isAccessible) {
          field.setAccessible(true);
        }
        Object dataRepository = field.get(this);
        if (dataRepository instanceof BaseDataRepository) {
          ((BaseDataRepository) dataRepository).cancelAllRequest();
        }
        if (!isAccessible) {
          field.setAccessible(false);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    super.onCleared();
  }
  //</editor-fold>

  //<editor-fold desc="LiveData">

  /**
   * 获取吐司 LiveData 实例
   *
   * @return 吐司 LiveData 实例
   */
  @NonNull
  LiveData<ToastModel> getToastLiveData() {
    return mToastLiveData;
  }

  /**
   * 获取占位布局 LiveData 实例
   *
   * @return 占位布局 LiveData 实例
   */
  @NonNull
  LiveData<PlaceholderModel> getPlaceholderLiveData() {
    return mPlaceholderLiveData;
  }

  protected int getPlaceholderType() {
    return mPlaceholderLiveData.getValue() != null ? mPlaceholderLiveData.getValue().getType() : PlaceholderType.NONE;
  }

  /**
   * 获取网络弹窗 LiveData 实例
   *
   * @return 网络弹窗 LiveData 实例
   */
  @NonNull
  LiveData<NetworkDialogModel> getNetworkDialogLiveData() {
    return mNetworkDialogModelLiveData;
  }

  /**
   * 获取 StartActivityForResult LiveData 实例
   *
   * @return StartActivityForResult LiveData 实例
   */
  @NonNull
  CheckThreadLiveData<StartActivityForResultModel> getStartActivityForResultModelLiveData() {
    return mStartActivityForResultModelLiveData;
  }
  //</editor-fold>

  //<editor-fold desc="吐司">

  /**
   * 显示吐司
   *
   * @param stringResourceId 字符串资源 id
   * @param arguments 字符串资对应的源参数
   */
  protected void showToast(@StringRes int stringResourceId, @Nullable Object... arguments) {
    mToastLiveData.setValue(new ToastModel(stringResourceId, arguments));
  }

  /**
   * 显示吐司
   *
   * @param text 吐司文本
   */
  protected void showToast(@Nullable String text) {
    if (text != null) {
      mToastLiveData.setValue(new ToastModel(text));
    }
  }
  //</editor-fold>

  //<editor-fold desc="占位布局">

  /**
   * 更新占位布局类型
   *
   * @param type 占位布局类型 {@link PlaceholderType}
   */
  protected void updatePlaceholderLayoutType(@PlaceholderType int type) {
    mPlaceholderLiveData.setValue(new PlaceholderModel(type));
  }

  /**
   * 占位重新加载
   *
   * @param view 被点击的视图
   */
  public void placeholderReload(@NonNull View view) {

  }

  /**
   * 占位登录
   *
   * @param view 被点击的视图
   */
  void placeholderLogin(@NonNull View view) {
    AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
    if (plugin != null) {
      plugin.startLoginActivity(view.getContext());
      PluginManager.get(StatPlugin.class).enterRegister(
              getPid(),
              ResourcesUtils.getString(R.string.follow)
      );
    }
  }
  //</editor-fold>

  //<editor-fold desc="网络弹窗">

  /**
   * 显示网络弹窗
   *
   * @param stringResourceId 字符串资源 id
   */
  @SuppressWarnings("SameParameterValue")
  protected void showNetworkDialog(@StringRes int stringResourceId) {
    mNetworkDialogModelLiveData.setValue(new NetworkDialogModel(stringResourceId));
  }

  /**
   * 显示网络弹窗
   *
   * @param text 字符串
   */
  @SuppressWarnings("SameParameterValue")
  protected void showNetworkDialog(@Nullable String text) {
    mNetworkDialogModelLiveData.setValue(new NetworkDialogModel(text));
  }

  /**
   * 隐藏网络弹窗
   */
  protected void hideNetworkDialog() {
    mNetworkDialogModelLiveData.setValue(new NetworkDialogModel());
  }
  //</editor-fold>

  //<editor-fold desc="StartActivityForResult">

  /**
   * 跳转 Activity 并带有结果
   *
   * @param intent 意图
   * @param requestCode 请求码
   */
  protected void startActivityForResult(@NonNull Intent intent, int requestCode) {
    mStartActivityForResultModelLiveData.setValue(
        new StartActivityForResultModel(intent, requestCode)
    );
  }

  /**
   * Activity 结果
   *
   * @param requestCode 请求码
   * @param resultCode 结果码
   * @param data 数据
   */
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
  }
  //</editor-fold>

  //<editor-fold desc="Observer">

  /**
   * 响应观察者，提供占位布局、网络弹窗、吐司处理
   *
   * @param <T> 响应模型泛型
   */
  @SuppressWarnings("SpellCheckingInspection")
  protected abstract class GVideoResponseObserver<T> extends BaseGVideoResponseObserver<T> {

    //<editor-fold desc="吐司相关方法">

    /**
     * 是否显示吐司，默认不显示
     *
     * @return false : 不显示
     */
    protected boolean isShowToast() {
      return false;
    }
    //</editor-fold>

    //<editor-fold desc="占位布局">

    /**
     * 是否显示占位布局，默认不显示
     *
     * @return true : 显示
     */
    protected boolean isShowPlaceholderLayout() {
      return false;
    }
    //</editor-fold>

    //<editor-fold desc="网络弹窗">

    /**
     * 是否显示网络弹窗，默认不显示
     *
     * @return true : 显示
     */
    protected boolean isShowNetworkDialog() {
      return false;
    }

    /**
     * 获取网络弹窗提示字符串资源 id
     *
     * @return 网络弹窗提示字符串资源 id
     */
    protected int getNetworkDialogTipTextResId() {
      return 0;
    }

    /**
     * 获取网络弹窗提示字符串资源
     *
     * @return 网络弹窗提示字符串
     */
    @Nullable
    protected String getNetworkDialogTipText() {
      return null;
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">

    @Override
    protected void onRequestStart() {
      super.onRequestStart();
      // 占位布局
      if (isShowPlaceholderLayout()) {
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
      }
      // 网络弹窗
      if (isShowNetworkDialog()) {
        String text = getNetworkDialogTipText();
        if (text != null) {
          showNetworkDialog(text);
        } else {
          showNetworkDialog(getNetworkDialogTipTextResId());
        }
      }
    }

    @Override
    protected final void onRequestData(T t) {
      // 占位布局
      if (isShowPlaceholderLayout()) {
        updatePlaceholderLayoutType(PlaceholderType.NONE);
      }
      // 网络弹窗
      if (isShowNetworkDialog()) {
        hideNetworkDialog();
      }
      // 回调成功
      onSuccess(t);
    }

    @Override
    protected final void onRequestError(Throwable throwable) {
      boolean handled = false;
      // 吐司
      if (isShowToast()) {
          String message = throwable.getMessage();
          if (!TextUtils.isEmpty(message)) {
            showToast(message);
          }
      }
      // 占位布局
      if (isShowPlaceholderLayout()) {
        if (NetworkUtils.isNetworkConnected()) {
          if (throwable instanceof TimeoutException ||
              throwable instanceof SocketTimeoutException) {
            showToast(R.string.all_network_not_available);
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            handled = true;
          } else {
            updatePlaceholderLayoutType(PlaceholderType.ERROR);
          }
        } else {
          updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
        }
      }
      // 网络弹窗
      if (isShowNetworkDialog()) {
        hideNetworkDialog();
      }
      if (!handled) {
        // 回调失败
        onFailed(throwable);
      }
    }
    //</editor-fold>

    //<editor-fold desc="抽象方法">

    /**
     * 请求响应成功回调
     *
     * @param t 响应模型
     */
    protected abstract void onSuccess(@NonNull T t);
    //</editor-fold>

    //<editor-fold desc="内部方法">

    /**
     * 请求失败响应
     *
     * @param throwable 异常
     */
    protected void onFailed(@NonNull Throwable throwable) {
      if (NetworkUtils.isNetworkConnected() || throwable instanceof GVideoAPIException) {
        onAPIError(throwable);
      } else {
        onNetworkNotAvailableError(throwable);
      }
    }

    /**
     * API 错误
     *
     * @param throwable 异常
     */
    protected void onAPIError(@NonNull Throwable throwable) {

    }

    /**
     * 网络不可用错误
     *
     * @param throwable 异常
     */
    protected void onNetworkNotAvailableError(@NonNull Throwable throwable) {

    }
    //</editor-fold>
  }

  public static class BaseGVideoResponseObserver<T> extends BaseResponseObserver<T> {
    @Override protected void onRequestData(T t) {
    }

    @Override protected void onRequestError(Throwable throwable) {
    }

    @Override public void onError(Throwable throwable) {
      super.onError(throwable);
      if (throwable instanceof GVideoAPIException) {
        if (((GVideoAPIException) throwable).getCode() == GVideoCode.CODE_ACCOUNT_FREEZE) {
          GVideoEventBus.get(AccountPlugin.EVENT_ACCOUNT_FREEZE).post(null);
        }

      }
    }
  }

  public void setPid(String pid){
    this.pid = pid;
  }

  public String getPid(){
    return pid;
  }

  //</editor-fold>
}