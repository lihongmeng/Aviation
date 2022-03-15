package com.hzlz.aviation.kernel.network;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.hzlz.aviation.kernel.network.response.APIResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Retrofit;

/**
 * 配置管理类
 *
 *
 * @since 2020-01-03 17:10
 */
public final class NetworkManager {
  //<editor-fold desc="属性">
  private static NetworkManager sInstance;
  private static boolean sHasInitialized;
  private Context mApplicationContext;
  private Retrofit mRetrofit;
  private Gson mGson;
  private NetworkLogger mLogger;
  private List<Class<? extends APIResponse>> mResponseTypeList;
  private HashSet<BaseUrlChangedCallback> mBaseUrlChangedCallbacks;
  //</editor-fold>

  //<editor-fold desc="构造函数">okhttp

  /**
   * 构造函数
   *
   * @param initialization 初始化 {@link Initialization} 实例
   */
  private NetworkManager(Initialization initialization) {
    sHasInitialized = true;
    mApplicationContext = initialization.mApplicationContext;
    mRetrofit = initialization.mRetrofit;
    mGson = initialization.mGson;
    mLogger = initialization.mLogger;
    mResponseTypeList = initialization.mResponseTypeList;
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 获取 Retrofit {@link Retrofit} 实例
   *
   * @return Retrofit 实例
   */
  /*@NotNull*/
  public Retrofit getRetrofit() {
    return mRetrofit;
  }

  /**
   * 获取 Gson {@link Gson} 实例
   *
   * @return Gson 实例
   */
  /*@NotNull*/
  @SuppressWarnings("SpellCheckingInspection")
  public Gson getGson() {
    return mGson;
  }

  /**
   * 获取网络日志 {@link NetworkLogger} 接口
   *
   * @return 网络日志接口
   */
  public NetworkLogger getLogger() {
    return mLogger;
  }

  /**
   * 获取响应类型 {@link APIResponse} 列表
   *
   * @return 响应类型列表
   */
  public List<Class<? extends APIResponse>> getResponseTypeList() {
    return mResponseTypeList;
  }

  /**
   * 获取 BaseUrl
   *
   * @return BaseUrl
   */
  public String getBaseUrl() {
    return mRetrofit.baseUrl().toString();
  }

  /**
   * 更新 BaseUrl
   *
   * @param baseUrl BaseUr响应类型 {@link APIResponse} 列表l
   */
  public void updateBaseUrl(String baseUrl) {
    mRetrofit = mRetrofit.newBuilder().baseUrl(baseUrl).build();
    // 通知 BaseUrl 发生变化
    if (mBaseUrlChangedCallbacks != null) {
      for (BaseUrlChangedCallback callback : mBaseUrlChangedCallbacks) {
        callback.onBaseUrlChanged();
      }
    }
  }

  /**
   * 获取 Application Context
   *
   * @return Application Context
   */
  public Context getApplicationContext() {
    return mApplicationContext;
  }

  /**
   * 添加 BaseUrl 改变回调
   *
   * @param callback 回调
   */
  public void addBaseUrlChangedCallback(BaseUrlChangedCallback callback) {
    if (callback != null) {
      if (mBaseUrlChangedCallbacks == null) {
        mBaseUrlChangedCallbacks = new HashSet<>();
      }
      mBaseUrlChangedCallbacks.add(callback);
    }
  }

  /**
   * 移除 BaseUrl 改变回调
   *
   * @param callback 回调
   */
  public void removeBaseUrlChangedCallback(BaseUrlChangedCallback callback) {
    if (mBaseUrlChangedCallbacks != null && callback != null) {
      mBaseUrlChangedCallbacks.remove(callback);
    }
  }
  //</editor-fold>

  //<editor-fold desc="静态 API 方法">

  /**
   * 使用 Application Context 初始化 NetworkManager
   *
   * @param context Application Context
   */
  public static Initialization with(Context context) {
    return new Initialization(context);
  }

  /**
   * 获取 NetworkManager {@link NetworkManager} 实例
   *
   * @return NetworkManager 实例
   */
  public static NetworkManager getInstance() {
    if (sInstance == null) {
      throw new NullPointerException("please initialize NetworkManager first");
    }
    return sInstance;
  }
  //</editor-fold>

  //<editor-fold desc="初始化">

  /**
   * 初始化类，用于初始化 NetworkManager {@link NetworkManager} 实例
   */
  public static final class Initialization {
    //<editor-fold desc="属性">
    private Context mApplicationContext;
    private Retrofit mRetrofit;
    @SuppressWarnings("SpellCheckingInspection")
    private Gson mGson;
    private NetworkLogger mLogger;
    private List<Class<? extends APIResponse>> mResponseTypeList;
    //</editor-fold>

    /**
     * 构造函数
     *
     * @param context Application Context
     */
    private Initialization(Context context) {
      if (!(context instanceof Application)) {
        throw new IllegalArgumentException("context must be Application");
      }
      mApplicationContext = context;
    }

    /**
     * 设置 Retrofit {@link Retrofit} 实例
     *
     * @param retrofit Retrofit 实例
     */
    public Initialization retrofit(Retrofit retrofit) {
      if (retrofit == null) {
        throw new NullPointerException("retrofit == null");
      }
      mRetrofit = retrofit;
      return this;
    }

    /**
     * 设置 Gson {@link Gson} 实例
     *
     * @param gson Gson 实例
     */
    @SuppressWarnings("SpellCheckingInspection")
    public Initialization gson(Gson gson) {
      if (gson == null) {
        throw new NullPointerException("gson == null");
      }
      mGson = gson;
      return this;
    }

    /**
     * 设置网络日志接口
     *
     * @param logger 网络日志接口 {@link NetworkLogger}
     */
    public Initialization logger(NetworkLogger logger) {
      mLogger = logger;
      return this;
    }

    /**
     * 添加响应类型
     *
     * @param type 响应类型
     */
    public Initialization addResponseType(Class<? extends APIResponse> type) {
      if (mResponseTypeList == null) {
        mResponseTypeList = new ArrayList<>();
      }
      mResponseTypeList.add(type);
      return this;
    }

    /**
     * 初始化
     */
    @SuppressWarnings("SpellCheckingInspection")
    public void init() {
      if (sHasInitialized) {
        throw new RuntimeException("NetworkManager can only be initialized on time");
      }
      if (mRetrofit == null) {
        throw new NullPointerException("Retrofit is null");
      }
      if (mGson == null) {
        throw new NullPointerException("Gson is null");
      }
      sInstance = new NetworkManager(this);
    }
  }
  //</editor-fold>
}
