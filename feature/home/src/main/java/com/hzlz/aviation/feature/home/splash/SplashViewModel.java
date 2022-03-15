package com.hzlz.aviation.feature.home.splash;

import android.app.Application;
import android.content.Context;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.MutableLiveData;

import com.hzlz.aviation.feature.home.launch.LaunchProtocolHelper;
import com.hzlz.aviation.feature.home.launch.LaunchSharedPrefs;
import com.hzlz.aviation.feature.home.splash.binding.SplashDataBinding;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.plugin.IAuthRepository;
import com.hzlz.aviation.kernel.base.plugin.InitializationPlugin;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 闪屏数据模型
 */
public class SplashViewModel extends BaseViewModel {

    /** 通知状态 -- 退出 */
    public static final int STATUS_EXIT = 1;
    /** 通知状态 -- 跳转home */
    public static final int STATUS_TO_HOME = 2;
    /** 通知状态 -- 跳转广告 */
    public static final int STATUS_TO_AD = 3;
    /** 通知状态 -- 从跳转广告返回 */
    public static final int STATUS_TO_AD_BACK = 4;

    @IntDef({STATUS_EXIT, STATUS_TO_HOME, STATUS_TO_AD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SplashStatus{
    }

    /** splash 事件状态 */
    public MutableLiveData<Integer> splashStatus = new MutableLiveData<>();

    /** 持有的splash data binding对象 */
    private SplashDataBinding mSplashDataBinding;

    /**
     * 构造函数
     */
    public SplashViewModel(@NonNull Application application) {
        super(application);
        mSplashDataBinding = new SplashDataBinding();
        mSplashDataBinding.checkSplash();
    }

    /**
     * 获取持有的splash data binding 对象
     *
     * @return 对应对象
     */
    public SplashDataBinding getSplashDataBinding() {
        return mSplashDataBinding;
    }

    /**
     * 获取launch 状态
     */
    public int getLaunchState() {
        if (!hasConfirmProtocol()) {
            return LaunchSharedPrefs.STATE_INSTALL;
        }
        int versionCode = LaunchSharedPrefs.getInstance().getInt(LaunchSharedPrefs.KEY_VERSION, 0);
        int pkgVersionCode = GVideoRuntime.getVersionCode();
        if (pkgVersionCode != versionCode) {
            return LaunchSharedPrefs.STATE_UPDATE;
        }
        return LaunchSharedPrefs.STATE_NORMAL;
    }

    /**
     * 是否已确认协议
     */
    private boolean hasConfirmProtocol() {
        return LaunchSharedPrefs.getInstance().getBoolean(LaunchSharedPrefs.KEY_HAS_CONFIRM, false);
    }

    /**
     * 首次启动
     *
     * @param context 上下文环境
     */
    public void onStateInstall(Context context) {
        LaunchProtocolHelper.onLaunchInstall(context, new LaunchProtocolHelper.OnProtocolResultListener() {
            @Override
            public void onConfirm() {
                authVisit();
                LaunchSharedPrefs.getInstance().putBoolean(LaunchSharedPrefs.KEY_HAS_CONFIRM, true);
                LaunchSharedPrefs.getInstance().putInt(LaunchSharedPrefs.KEY_VERSION, GVideoRuntime.getVersionCode());
                splashStatus.postValue(STATUS_TO_HOME);
            }

            @Override
            public void onCancel() {
                LaunchSharedPrefs.getInstance().putBoolean(LaunchSharedPrefs.KEY_HAS_CONFIRM, false);
                splashStatus.postValue(STATUS_EXIT);
            }
        });
    }

    /**
     * 开始倒计时
     *
     * @param model 广告数据模型
     */
    public void startCountDown(@NonNull SplashAdEntity model) {
        mSplashDataBinding.startCountDown(model);
        mSplashDataBinding.countDownStatus.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (mSplashDataBinding.countDownStatus.equals(sender)) {

                    Integer countDown = mSplashDataBinding.countDownStatus.get();
                    if (countDown != null) {

                        switch (countDown) {
                            case SplashDataBinding.COUNT_DOWN_STATUS_FINISH:
                            case SplashDataBinding.COUNT_DOWN_STATUS_INTERRUPT_CLICK:
                                splashStatus.postValue(STATUS_TO_HOME);
                                break;
                            case SplashDataBinding.COUNT_DOWN_STATUS_INTERRUPT_AD:
                                splashStatus.postValue(STATUS_TO_AD);
                                break;
                            case  SplashDataBinding.COUNT_DOWN_STATUS_INTERRUPT_CLICK_DETAILS:
                                splashStatus.postValue(STATUS_TO_AD_BACK);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 用户授权登陆协议
     */
    public void authVisit() {
        // 上传用户授权协议
        boolean authVisited = LaunchSharedPrefs.getInstance().getBoolean(LaunchSharedPrefs.KEY_AUTH_VISITED, false);
        if (authVisited) {
            return;
        }
        PluginManager.get(InitializationPlugin.class).getAuthRepository().authVisit(
            IAuthRepository.PROTOCOL_USER, IAuthRepository.PROTOCOL_PRIVACY).subscribe(
            new GVideoResponseObserver<Object>() {
                @Override protected void onSuccess(@NonNull Object o) {
                    LaunchSharedPrefs.getInstance().putBoolean(LaunchSharedPrefs.KEY_AUTH_VISITED, true);
                }
            });
    }

}
