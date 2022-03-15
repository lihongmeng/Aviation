package com.hzlz.aviation.feature.home;

import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.START_SCAN_HOME;
import static com.hzlz.aviation.kernel.base.plugin.AccountPlugin.VERIFICATION_STATUS_VERIFYING;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hzlz.aviation.feature.home.databinding.ActivityHomeBinding;
import com.hzlz.aviation.feature.home.splash.ad.SplashAdManager;
import com.hzlz.aviation.feature.home.splash.repository.SplashRepository;
import com.hzlz.aviation.feature.home.update.UpdateDialog;
import com.hzlz.aviation.feature.home.update.UpdateForceDialog;
import com.hzlz.aviation.feature.home.update.UpdateViewModel;
import com.hzlz.aviation.feature.home.utils.HomeTabUtils;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.model.update.UpdateModel;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.AppSDKInitPlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5EntryPlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.base.plugin.LivePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.scan.ScanActivity;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;
import com.hzlz.aviation.kernel.base.utils.StorageUtils;
import com.hzlz.aviation.kernel.base.view.floatwindow.FloatingView;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.service.AudioLivePlayHelper;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.sensordata.utils.LocationUtils;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.DeviceId;
import com.hzlz.aviation.library.util.LogUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * 首页activity
 */
public class HomeActivity extends BaseActivity<ActivityHomeBinding> {

    private UpdateViewModel mUpdateViewModel;
    /**
     * 跳转delay时间
     */
    private static final long DELAY_TIME = 100;

    /**
     * 没有任何广告时，底图展示2s后跳转到首页
     */
    private Runnable mNavigateHomeRunnable = () -> handleNavigateToHome();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!PluginManager.get(AppSDKInitPlugin.class).hasInit()) {
            OpenInstall.init(HomeActivity.this);
            //因为点击推送消息不会打开启动页，再次检查初始化
            PluginManager.get(AppSDKInitPlugin.class).init();
            HomeTabUtils.getInstance().initOpenHomeSwitch();
        }

        AsyncUtils.runOnUIThread(() -> {
            PluginManager.get(LivePlugin.class).checkHasLive(this);
            PluginManager.get(H5EntryPlugin.class).dispatch(this, getIntent());
            new SplashRepository().uploadAnonymousId();
            OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
        }, 2000);

        PluginManager.get(AccountPlugin.class).preHeaderImage(this);

        requestPermission();
        LogUtils.e("360dp = "+GVideoRuntime.getAppContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_360DP));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (TextUtils.equals(intent.getStringExtra(HomePlugin.KEY_TRANS_TAB),
                HomePlugin.TRANS_PERSONAL_TAB)) {
            GVideoEventBus.get(HomePlugin.EVENT_PERSONAL, String.class).postDelay("", DELAY_TIME);
            return;
        }
        PluginManager.get(H5EntryPlugin.class).dispatch(this, intent);
        OpenInstall.getWakeUp(intent, wakeUpAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN).observe(this, o -> {
            PluginManager.get(AccountPlugin.class).startLoginActivity(this);
        });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(this, o -> OpenInstall.reportRegister());

        GVideoEventBus.get(START_SCAN_HOME).observe(
                this,
                o -> {
                    new IntentIntegrator(this)
                            .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                            //.setPrompt("请对准二维码")// 设置提示语
                            .setCameraId(0)// 选择摄像头,可使用前置或者后置
                            .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                            .setCaptureActivity(ScanActivity.class)//自定义扫码界面
                            .initiateScan();// 初始化扫码
                }
        );
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void bindViewModels() {
        mUpdateViewModel = ViewModelProviders.of(this).get(UpdateViewModel.class);
    }

    @Override
    protected void loadData() {
        handleNavigateToHome();
    }


    /**
     * 处理跳转至home事件
     */
    private void handleNavigateToHome() {
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
//        if (!accountPlugin.hasLoggedIn() && !KernelSharedPrefs.getInstance().getBoolean(SP_LOGIN_HAS_VERIFICATE, false)) {
//            StaticParams.isForeLogin = true;
//            accountPlugin.startLoginActivity(HomeActivity.this);
//            finish();
//            return;
//        }

        String mId = accountPlugin.getUserId();
        String mNickName = accountPlugin.getNickName();
        int nickAuditStatus = accountPlugin.getNickNameAuditStatus();
        if (mId != null
                && !TextUtils.isEmpty(mId)
                && mId.equals(mNickName)
                && nickAuditStatus != VERIFICATION_STATUS_VERIFYING) {
            StaticParams.isForeLogin = true;
            PluginManager.get(AccountPlugin.class).startNickNameSetActivity(this);
            finish();
            return;
        }

        SplashAdManager.getInstance().tryCacheSplashDataItem();
        PluginManager.get(FeedPlugin.class).onSplashEnd();

        //避免首次授权登陆失败，这里再检查一下
//        mSplashViewModel.authVisit();
        //检查升级
        checkUpdate();
    }


    private void checkUpdate() {
        GVideoEventBus.get(HomePlugin.EVENT_UPDATE_MODEL, UpdateModel.class).observe(this,
                new Observer<UpdateModel>() {
                    @Override
                    public void onChanged(UpdateModel updateModel) {
                        mUpdateViewModel.checkUpdateModel(updateModel);
                    }
                });
        mUpdateViewModel.mCheckUpdateLiveData.observe(this, new Observer<UpdateModel>() {
            @Override
            public void onChanged(UpdateModel updateModel) {
                if (updateModel != null && updateModel.isValid()) {
                    if (updateModel.forceUpdate) {
                        UpdateForceDialog dialog = new UpdateForceDialog(HomeActivity.this, updateModel);
                        dialog.show();
                    } else {
                        boolean hasLocalPath = new File(updateModel.getAPKPath()).exists();
                        String message = hasLocalPath ? "已下载更新包，是否去安装?" : updateModel.releaseNotes;
                        UpdateDialog dialog = new UpdateDialog(HomeActivity.this);
                        if (hasLocalPath) {
                            dialog.setConfirmText(R.string.install_bt_confirm);
                        }
                        dialog.setContent(message);
                        dialog.setConfirmListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (hasLocalPath) {
                                    StorageUtils.install(updateModel.getAPKPath(), HomeActivity.this);
                                } else {
                                    mUpdateViewModel.download(updateModel.downloadUrl, updateModel.getAPKPath());
                                }
                                dialog.dismiss();
                            }
                        });
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mUpdateViewModel.dismiss(updateModel);
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });
        mUpdateViewModel.checkUpdate();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mNavigateHomeRunnable);
        PluginManager.get(FeedPlugin.class).onHomeRelease();
        PluginManager.get(AccountPlugin.class).onHomeRelease();
        GVideoSensorDataManager.getInstance().onHomeRelease();
        AudioLivePlayHelper.getInstance().close();
        FloatingView.get().remove();
        wakeUpAdapter = null;
        super.onDestroy();
    }

    private void requestPermission() {

        SharedPrefsWrapper prefsWrapper = new SharedPrefsWrapper("permissionDenied");
        boolean hasRequest = prefsWrapper.getBoolean(Manifest.permission.ACCESS_FINE_LOCATION, false);
        //存储权限，定位权限只请求一次
        RxPermissions rxPermissions = new RxPermissions(this);
        if (hasRequest) {
            if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                LocationUtils.getInstance().location(HomeActivity.this);
            }
            return;
        }
        prefsWrapper.putBoolean(Manifest.permission.ACCESS_FINE_LOCATION, true);
        PermissionManager.requestPermissions(this, new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        DeviceId.clear();
                        DeviceId.get();
                        LocationUtils.getInstance().location(HomeActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions,
                                                   @NonNull String[] deniedPermission) {
                        if (grantedPermissions == null) {
                            return;
                        }
                        for (int i = 0; i < grantedPermissions.length; i++) {
                            if (grantedPermissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                LocationUtils.getInstance().location(HomeActivity.this);
                                break;
                            }
                        }
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult == null) {
            return;
        }
        String result = intentResult.getContents();
        if (TextUtils.isEmpty(result)) {
            return;
        }
        Log.d("TAG", "扫码结果：" + result);
        PluginManager.get(WebViewPlugin.class).startWebViewActivity(this, result, "");
    }

    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            if (!TextUtils.isEmpty(appData.getData())) {
                try {
                    JSONObject object = new JSONObject(appData.getData());
                    String data = object.getString("jspopen");
                    if (!TextUtils.isEmpty(data)) {
                        Intent intent = new Intent();
                        try {
                            intent.setData(Uri.parse(URLDecoder.decode(data, "utf-8")));
                            PluginManager.get(H5EntryPlugin.class).dispatch(HomeActivity.this, intent);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            LogUtils.e(appData.getData() + " , " + appData.getChannel());
        }
    };

}
