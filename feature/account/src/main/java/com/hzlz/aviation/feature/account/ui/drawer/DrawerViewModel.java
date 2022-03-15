package com.hzlz.aviation.feature.account.ui.drawer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.HasUnreadMessageNotificationResponse;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.repository.MessageNotificationRepository;
import com.hzlz.aviation.feature.account.repository.MomentRepository;
import com.hzlz.aviation.feature.account.repository.UserRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.environment.EnvironmentManager;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.ChatIMPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.TestPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.utils.StorageUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.FileUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 个人中心抽屉界面 ViewModel
 *
 * @since 2020-01-17 15:35
 */
public final class DrawerViewModel extends BaseViewModel {
    //<editor-fold desc="属性">
    // 数据绑定
    @NonNull
    public ObservableBoolean hasUnreadMessageNotification = new ObservableBoolean();
    @NonNull
    public ObservableField<String> cacheSize = new ObservableField<>();
    @NonNull
    public ObservableInt switchEnvironmentVisibility = new ObservableInt();
    // LiveData
    @NonNull
    private CheckThreadLiveData<User> mUserLiveData = new CheckThreadLiveData<>();
    @NonNull
    private CheckThreadLiveData<Boolean> mDrawerLiveData = new CheckThreadLiveData<>();
    @NonNull
    private CheckThreadLiveData<Boolean> mShowCacheLoadingLiveData = new CheckThreadLiveData<>();
    // Repository
    @NonNull
    private UserRepository mUserRepository = new UserRepository();
    @NonNull
    private MessageNotificationRepository mMessageNotificationRepository =
            new MessageNotificationRepository();
    private MomentRepository mMomentRepository = new MomentRepository();
    //
    private Disposable mNotificationMessageDisposable;
    private Disposable mNewMomentDisposable;
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public DrawerViewModel(@NonNull Application application) {
        super(application);
        switchEnvironmentVisibility.set(true ||
                EnvironmentManager.getInstance().getIsTestProductFlavors() ? View.VISIBLE : View.GONE);
    }
    //</editor-fold>

    //<editor-fold desc="API">

    @NonNull
    LiveData<User> getUserLiveData() {
        return mUserLiveData;
    }

    @NonNull
    LiveData<Boolean> getCloseDrawerLiveData() {
        return mDrawerLiveData;
    }

    @NonNull
    LiveData<Boolean> getShowCacheLoadingLiveData() {
        return mShowCacheLoadingLiveData;
    }

    void loadData(Activity activity) {
        mUserRepository.getCurrentUser().subscribe(new GVideoResponseObserver<User>() {
            @Override
            protected void onSuccess(@NonNull User user) {
                mUserLiveData.setValue(user);
            }
        });
        checkUnreadMessageNotification();

        mUserRepository.getPlatformAccountData();

//        checkNewMoment();

        calculateCache();
    }

    void onLogin() {
        checkUnreadMessageNotification();
//        checkNewMoment();
    }

    void onLogout() {
        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION).post(0);
        hasUnreadMessageNotification.set(false);
        if (mNotificationMessageDisposable != null) {
            mNotificationMessageDisposable.dispose();
        }
        if (mNewMomentDisposable != null) {
            mNewMomentDisposable.dispose();
        }
    }

    void onAccountFreeze() {
        if (UserManager.hasLoggedIn()) {
            UserManager.deleteCurrentUser();
            UserManager.saveToken(null);
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).post(null);
        }
    }

    void updateHadMessageNotification(boolean hasMessageNotification) {
        hasUnreadMessageNotification.set(hasMessageNotification);
    }
    //</editor-fold>

    private boolean isGetMessageNotification = false;

    //<editor-fold desc="内部方法">
    private void checkUnreadMessageNotification() {
        if (!UserManager.hasLoggedIn()) {
            return;
        }

        mNotificationMessageDisposable = Observable.interval(0, 15, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    checkUnreadMessageCount();
                });
    }

    public void checkUnreadMessageCount() {
        if (isGetMessageNotification) {
            return;
        }
        isGetMessageNotification = true;
        mMessageNotificationRepository.hasUnreadMessageNotification()
                .subscribe(new GVideoResponseObserver<HasUnreadMessageNotificationResponse>() {
                    @Override
                    protected void onSuccess(@NonNull HasUnreadMessageNotificationResponse response) {
                        isGetMessageNotification = false;


                        PluginManager.get(ChatIMPlugin.class).getUnreadMessageCount(new ChatIMPlugin.TotalUnreadMessageCountListener() {
                            @Override
                            public void onSuccess(long count) {
                                int total = (int) (response.getUnreadCount() + count);
                                hasUnreadMessageNotification.set(total > 0);
                                GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION).post(total);
                            }

                            @Override
                            public void onError() {
                                hasUnreadMessageNotification.set(response.hasUnread());
                                GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION).post(response.getUnreadCount());
                            }
                        });


                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        isGetMessageNotification = false;
                    }
                });
    }

    private void checkNewMoment() {
        if (!UserManager.hasLoggedIn()) {
            return;
        }
        Observable
                .interval(0, 5, TimeUnit.MINUTES)
                .flatMap((Function<Long, ObservableSource<HasUnreadMessageNotificationResponse>>)
                        l -> mMomentRepository.hasNewMoment()
                )
                .subscribe(new GVideoResponseObserver<HasUnreadMessageNotificationResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mNewMomentDisposable = d;
                    }

                    @Override
                    protected void onSuccess(@NonNull HasUnreadMessageNotificationResponse response) {
                        GVideoEventBus.get(AccountPlugin.EVENT_NEW_MOMENT).post(response.hasUnread());
                    }
                });
    }

    /**
     * 导航到个人中心界面
     *
     * @param view 被点击的视图
     */
    public void navigateToProfile(@NonNull View view) {
        User user = mUserLiveData.getValue();
        if (user != null) {
            checkLoginStateCloseDrawerAndRun(view,
                    () -> Navigation.findNavController(view).navigate(R.id.profile_nav_graph),
                    StaticParams.currentStatPid
            );
        }
    }

    /**
     * 导航到消息与活动界面
     *
     * @param view 被点击的视图
     */
    public void navigateToMessageAndNotification(@NonNull View view) {
        checkLoginStateCloseDrawerAndRun(view,
                () -> {
                    Navigation.findNavController(view).navigate(R.id.message_notification_nav_graph);
                },
                StaticParams.currentStatPid
        );
    }

    /**
     * 导航到账号与安全界面
     *
     * @param view 被点击的视图
     */
    public void navigateToAccountAndSecurity(@NonNull View view) {
        checkLoginStateCloseDrawerAndRun(
                view,
                () -> Navigation.findNavController(view).navigate(R.id.account_security_nav_graph),
                StaticParams.currentStatPid
        );
    }

    /**
     * 导航到通用设置界面
     *
     * @param view 被点击的视图
     */
    public void navigateToGeneralSetting(@NonNull View view) {
        closeDrawerAndRun(
                view, () -> Navigation.findNavController(view).navigate(R.id.setting_nav_graph)
        );
    }

    /**
     * 导航到反馈与帮助界面
     *
     * @param view 被点击的视图
     */
    public void navigateToFeedbackAndHelp(@NonNull View view) {
        H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
        if (h5Plugin != null && h5Plugin.getFeedbackUrl() != null) {
            final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
            if (webViewPlugin != null) {
                closeDrawerAndRun(view, () -> {
                    Bundle arguments = new Bundle();
                    arguments.putString("url", h5Plugin.getFeedbackUrl());
                    String title = view.getContext().getString(R.string.fragment_profile_drawer_feedback_and_help);
                    arguments.putString("title", title);
                    webViewPlugin.startWebViewFragment(view, arguments);
                });
            }
        } else {
            showToast(R.string.all_account_check_network);
        }
    }

    public void showAppQrCode(@NonNull View view) {
        H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
        if (h5Plugin != null && h5Plugin.getAppQrCodeUrl() != null) {
            final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
            if (webViewPlugin != null) {
                Bundle arguments = new Bundle();
                arguments.putString("url", h5Plugin.getAppQrCodeUrl());
                arguments.putString("title", ResourcesUtils.getString(R.string.download_qr_code));
                webViewPlugin.startWebViewFragment(view, arguments);
            }
        } else {
            showToast(R.string.all_account_check_network);
        }
    }

    /**
     * 导航到关于界面
     *
     * @param view 被点击的视图
     */
    public void navigateToAbout(@NonNull View view) {
        closeDrawerAndRun(view,
                () -> Navigation.findNavController(view).navigate(R.id.about_nav_graph)
        );
    }

    /**
     * 评分
     *
     * @param view 被点击的视图
     */
    public void mark(@NonNull View view) {
        Context context = view.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 导航到入驻界面
     *
     * @param view 被点击的视图
     */
    public void navigateToSettleIn(@NonNull View view) {
        H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
        if (h5Plugin != null && h5Plugin.getSettleInUrl() != null) {
            WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
            if (webViewPlugin != null) {
                closeDrawerAndRun(view, () -> {
                    Bundle arguments = new Bundle();
                    //arguments.putString("title", ResourcesUtils.getString(R.string.fragment_profile_drawer_settle_in));
                    arguments.putString("url", h5Plugin.getSettleInUrl());
                    webViewPlugin.startWebViewFragment(view, arguments);
                });
            }
        } else {
            showToast(R.string.all_account_check_network);
        }
    }

    /**
     * 清除缓存
     *
     * @param view 被点击的视图
     */
    public void clearCache(@NonNull View view) {
        Boolean show = mShowCacheLoadingLiveData.getValue();
        if (show != null && show) {
            return;
        }
        mShowCacheLoadingLiveData.setValue(true);

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                File cacheDir = GVideoRuntime.getAppContext().getCacheDir();
                FileUtils.delete(StorageUtils.getCacheDirectory());
                boolean result = FileUtils.delete(cacheDir);
                e.onNext(result);
            }
        }).subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoResponseObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@NonNull Boolean result) {
                        mShowCacheLoadingLiveData.setValue(false);
                        calculateCache();
                    }

                    @Override
                    public void onFailed(Throwable e) {
                        mShowCacheLoadingLiveData.setValue(false);
                    }
                });
    }

    private void calculateCache() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                File cacheDir = GVideoRuntime.getAppContext().getCacheDir();
                String cacheLength = FileUtils.getSize(cacheDir);
                e.onNext(cacheLength);
            }
        }).subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoResponseObserver<String>() {
                    @Override
                    protected void onSuccess(@NonNull String cacheLength) {
                        cacheSize.set(cacheLength);
                    }
                });
    }

    /**
     * 切换环境
     *
     * @param view 被点击的视图
     */
    public void switchEnvironment(@NonNull View view) {
        closeDrawerAndRun(
                view, () -> PluginManager.get(TestPlugin.class).startFragment(view)
        );
    }

    /**
     * 关闭抽屉并执行
     *
     * @param view     控件
     * @param runnable Runnable
     */
    private void closeDrawerAndRun(@NonNull View view, @NonNull Runnable runnable) {
        mDrawerLiveData.setValue(true);
        view.postDelayed(runnable, ResourcesUtils.getInt(R.integer.drawer_close_duration));
    }

    /**
     * 检测登录状态关闭抽屉并执行
     *
     * @param view     控件
     * @param runnable Runnable
     */
    private void checkLoginStateCloseDrawerAndRun(
            @NonNull View view,
            @NonNull Runnable runnable,
            final String source
    ) {
        mDrawerLiveData.setValue(true);
        int duration = ResourcesUtils.getInt(R.integer.drawer_close_duration);
        if (UserManager.hasLoggedIn()) {
            view.postDelayed(runnable, duration);
        } else {
            view.postDelayed(() -> {
                AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
                if (plugin != null) {
                    plugin.startLoginActivity(view.getContext());
                    GVideoSensorDataManager.getInstance().enterRegister(
                            StatPid.getPageName(source),
                            ResourcesUtils.getString(R.string.setting)
                    );
                }
            }, duration);
        }
    }

    //</editor-fold>
}
