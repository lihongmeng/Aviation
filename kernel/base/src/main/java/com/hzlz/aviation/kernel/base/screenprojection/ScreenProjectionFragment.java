package com.hzlz.aviation.kernel.base.screenprojection;

import static android.animation.ValueAnimator.INFINITE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SCREEN_PROJECTION_FAILED;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SELECT_SCREEN_PROJECTION;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.WIFI_CONNECTED;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.WIFI_DISCONNECTED;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.animation.LinearInterpolator;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.FragmentScreenProjectionBinding;
import com.hzlz.aviation.kernel.base.plugin.StatPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.liuwei.android.upnpcast.NLDeviceRegistryListener;
import com.liuwei.android.upnpcast.NLUpnpCastManager;
import com.liuwei.android.upnpcast.controller.CastObject;
import com.liuwei.android.upnpcast.controller.ICastEventListener;
import com.liuwei.android.upnpcast.device.CastDevice;

import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;

import java.util.ArrayList;

public class ScreenProjectionFragment extends BaseFragment<FragmentScreenProjectionBinding> {

    private NLUpnpCastManager nlUpnpCastManager;
    private ScreenProjectionAdapter adapter;
    private WifiManager wifiManager;
    private boolean isUserSelect;
    private CastObject currentCastObject;
    private ObjectAnimator objectAnimator;

    private long stayTime;
    private long onResumeTime;
    private String errorMessage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_screen_projection;
    }

    @Override
    protected void initView() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        setToolbarTitle(getString(R.string.screen_projection));

        objectAnimator = ObjectAnimator.ofFloat(mBinding.loading, "rotation", 0f, 360f);
        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(INFINITE);
        objectAnimator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            mBinding.loading.setRotation(value);
        });
        objectAnimator.start();

        adapter = new ScreenProjectionAdapter(getActivity());
        mBinding.wifiList.setAdapter(adapter);
        mBinding.wifiList.setOnItemClickListener((parent, view, position, id) -> {
            isUserSelect = true;
            adapter.updateSelectedDeviceToLoading(position);
            CastDevice castDevice = adapter.getCurrentCastDevice();

            if (castDevice.getDevice().findService(NLUpnpCastManager.SERVICE_AV_TRANSPORT) != null) {
                adapter.updateSelectedDeviceToLoading(position);
                NLUpnpCastManager.getInstance().connect(adapter.getCurrentCastDevice());
            } else {
                adapter.updateSelectedDeviceToLoading(-1);
                showToast("此设备无法进行投屏");
            }
        });

        nlUpnpCastManager = NLUpnpCastManager.getInstance();
        nlUpnpCastManager.addCastEventListener(iCastEventListener);
        nlUpnpCastManager.addRegistryDeviceListener(onRegistryDeviceListener);
        nlUpnpCastManager.search(NLUpnpCastManager.DEVICE_TYPE_DMR, 300);

        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        updateBottomWifiName();

    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        stayTime += (System.currentTimeMillis() - onResumeTime);

    }

    @Override
    public void onDestroyView() {
        mBinding.loading.clearAnimation();
        nlUpnpCastManager.removeCastEventListener(iCastEventListener);
        nlUpnpCastManager.removeRegistryListener(onRegistryDeviceListener);
        super.onDestroyView();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindViewModels() {
        GVideoEventBus.get(WIFI_DISCONNECTED).observe(
                this,
                o -> {
                    showLogDebug("ScreenProjection", "网络连接断开...");
                    if (mBinding.loading == null) {
                        return;
                    }
                    if (adapter != null) {
                        adapter.updateDataSource(new ArrayList<>());
                        adapter.notifyDataSetChanged();
                    }
                    updateTopWifiInfo();
                    updateBottomWifiName();
                }
        );

        GVideoEventBus.get(WIFI_CONNECTED, String.class).observe(
                this,
                wifiName -> {
                    showLogDebug("ScreenProjection", "网络连接恢复...");
                    if (mBinding.loading == null) {
                        return;
                    }
                    if (adapter != null) {
                        adapter.updateDataSource(new ArrayList<>());
                        adapter.notifyDataSetChanged();
                    }
                    updateTopWifiInfo();
                    updateBottomWifiName();
                    nlUpnpCastManager.clear();
                    nlUpnpCastManager.search(NLUpnpCastManager.DEVICE_TYPE_DMR, 300);
                }
        );
    }

    @SuppressLint("SetTextI18n")
    private void updateBottomWifiName() {
        boolean isWifiNetworkConnected = NetworkUtils.isWifiNetworkConnected();
        if (!isWifiNetworkConnected) {
            mBinding.wifiName.setVisibility(GONE);
            mBinding.wifiName.setText("");
            return;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        //获取当前wifi名称
        String wifiName = wifiInfo.getSSID();
        if (TextUtils.isEmpty(wifiName)) {
            mBinding.wifiName.setVisibility(GONE);
            mBinding.wifiName.setText("");
            return;
        }
        mBinding.wifiName.setVisibility(VISIBLE);
        mBinding.wifiName.setText("当前网络: " + wifiName.replaceAll("\"", ""));
    }

    private void updateTopWifiInfo() {
        if (!NetworkUtils.isNetworkConnected() || !NetworkUtils.isWifiNetworkConnected()) {
            showLogDebug("ScreenProjection", "当前没有Wifi网络...");
            showNeedConnectWifi();
            return;
        }
        if (adapter == null || adapter.isDataSourceEmpty()) {
            showLogDebug("ScreenProjection", "展示Loading...");
            showLoading();
            return;
        }
        showLogDebug("ScreenProjection", "展示Wifi列表...");
        showDeviceList();
    }

    private void showNeedConnectWifi() {
        nlUpnpCastManager.clear();
        DLNAHelper.getInstance().stop();
        mBinding.wifiList.setVisibility(GONE);
        mBinding.loading.setVisibility(GONE);
        mBinding.loadingText.setVisibility(VISIBLE);
        mBinding.loadingText.setText(getString(R.string.please_connect_wifi_then_retry));
    }

    @Override
    protected void loadData() {

    }

    public void showLoading() {
        mBinding.loadingText.setVisibility(VISIBLE);
        mBinding.loadingText.setText(getString(R.string.is_searching_device));
        mBinding.loading.setVisibility(VISIBLE);
        mBinding.wifiList.setVisibility(GONE);
    }

    public void showDeviceList() {
        mBinding.loadingText.setVisibility(GONE);
        mBinding.loading.setVisibility(GONE);
        mBinding.wifiList.setVisibility(VISIBLE);
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    private final NLDeviceRegistryListener.OnRegistryDeviceListener onRegistryDeviceListener
            = new NLDeviceRegistryListener.OnRegistryDeviceListener() {

        @Override
        public void onDeviceAdded(CastDevice castDevice) {
            showLogDebug("ScreenProjection", "发现设备连接...");
            if (adapter == null || mBinding.loading == null) {
                return;
            }
            adapter.addDevice(castDevice);
            updateTopWifiInfo();
        }

        @Override
        public void onDeviceRemoved(CastDevice castDevice) {
            showLogDebug("ScreenProjection", "发现设备断开连接...");
            if (adapter == null || mBinding.loading == null) {
                return;
            }
            adapter.removeDevice(castDevice);
            updateTopWifiInfo();
        }
    };

    private final ICastEventListener iCastEventListener = new ICastEventListener() {

        @Override
        public void onConnecting(CastDevice castDevice) {
            showLogDebug("ScreenProjection", "正在链接电视服务...");
        }

        @Override
        public void onConnected(CastDevice castDevice, TransportInfo transportInfo, MediaInfo mediaInfo, int i) {
            showLogDebug("ScreenProjection", "链接电视服务成功...");
            DLNAHelper.getInstance().setCurrentDevice(castDevice);
            if (isUserSelect) {
                isUserSelect = false;
                boolean deviceFlag = castDevice != null && castDevice.getDevice() != null;
                NLUpnpCastManager.getInstance().cast(
                        CastObject.newInstance(
                                DLNAHelper.getInstance().getNeedPlayUrl(),
                                deviceFlag ? castDevice.getId() : "",
                                deviceFlag ? castDevice.getName() : ""
                        ).setDuration(60 * 2)
                );
            }
        }

        @Override
        public void onDisconnect() {
            showLogDebug("ScreenProjection", "电视服务链接断开...");
            DLNAHelper.getInstance().setCurrentDevice(null);
        }

        @Override
        public void onCast(CastObject castObject) {
            showLogDebug("ScreenProjection", "投屏成功...");
            currentCastObject = castObject;
            NLUpnpCastManager.getInstance().start();
            GVideoEventBus.get(SELECT_SCREEN_PROJECTION, CastObject.class).post(currentCastObject);
            errorMessage = "";
        }

        @Override
        public void onStart() {
            showLogDebug("ScreenProjection", "播放成功...");
            finishActivity();
        }

        @Override
        public void onPause() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onSeekTo(long l) {
        }

        @Override
        public void onError(String s) {
            errorMessage = s;
            GVideoEventBus.get(SCREEN_PROJECTION_FAILED, CastObject.class).post(null);
        }

        @Override
        public void onVolume(int i) {
        }

        @Override
        public void onBrightness(int i) {
        }

        @Override
        public void onUpdatePositionInfo(PositionInfo positionInfo) {
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter == null) {
            return;
        }
        PluginManager.get(StatPlugin.class).exitDeviceSelectPage(
                stayTime,
                adapter.getDeviceNameList(),
                currentCastObject == null ? "" : currentCastObject.name,
                currentCastObject == null,
                errorMessage = ""
        );
    }
}
