package com.hzlz.aviation.feature.home;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.home.databinding.ActivityHomeBinding;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;
import com.hzlz.aviation.kernel.base.view.floatwindow.FloatingView;
import com.hzlz.aviation.kernel.liteav.service.AudioLivePlayHelper;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.sensordata.utils.LocationUtils;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DeviceId;
import com.tbruyelle.rxpermissions3.RxPermissions;


/**
 * 首页activity
 */
public class HomeActivity extends BaseActivity<ActivityHomeBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        NavController navController = Navigation.findNavController(this, R.id.fragment_home_module_activity);
        NavInflater inflater = navController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.home_module_nav_graph);
        graph.setStartDestination(R.id.homeFragment);
        navController.setGraph(graph, null);
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void bindViewModels() {
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onDestroy() {
        PluginManager.get(FeedPlugin.class).onHomeRelease();
        PluginManager.get(AccountPlugin.class).onHomeRelease();
        GVideoSensorDataManager.getInstance().onHomeRelease();
        AudioLivePlayHelper.getInstance().close();
        FloatingView.get().remove();
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
                    public void onPermissionDenied(
                            @NonNull Context context,
                            @Nullable String[] grantedPermissions,
                            @NonNull String[] deniedPermission
                    ) {
                        if (grantedPermissions == null) {
                            return;
                        }
                        for (String grantedPermission : grantedPermissions) {
                            if (grantedPermission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                LocationUtils.getInstance().location(HomeActivity.this);
                                break;
                            }
                        }
                    }
                },
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        );

    }

}
