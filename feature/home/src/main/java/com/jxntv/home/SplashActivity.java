package com.jxntv.home;

import static com.jxntv.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_HOME;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jxntv.android.liteav.model.GVideoPlayerRenderMode;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.model.feed.TabItemInfo;
import com.jxntv.base.plugin.AppSDKInitPlugin;
import com.jxntv.base.plugin.FeedPlugin;
import com.jxntv.base.plugin.InitializationPlugin;
import com.jxntv.base.sp.SharedPrefsWrapper;
import com.jxntv.base.utils.AnimUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.home.databinding.HomeSplashBinding;
import com.jxntv.home.launch.LaunchSharedPrefs;
import com.jxntv.home.splash.SplashViewModel;
import com.jxntv.home.splash.ad.SplashAdManager;
import com.jxntv.home.splash.db.entitiy.SplashAdEntity;
import com.jxntv.home.splash.repository.SplashRepository;
import com.jxntv.home.splash.utils.SplashConstants;
import com.jxntv.home.utils.HomeTabUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.GVideoStatManager;
import com.jxntv.stat.StatConstants;
import com.jxntv.stat.StatPid;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.utils.LogUtils;
import java.io.File;
import java.util.List;

/**
 * 空页面，用于解决切到后台，点击桌面快捷方式返回时，会杀死HomeActivity栈之上Activity的问题
 */
public class SplashActivity extends BaseActivity<HomeSplashBinding> {

    /**
     * splash页 view model
     */
    private SplashViewModel mSplashViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.home_splash;
    }

    @Override
    protected void initView() {
        ImmersiveUtils.enterImmersiveFullTransparent(SplashActivity.this);
        //预先请求首页tab
        PluginManager.get(FeedPlugin.class)
                .getTabItemInfoList(MEDIA_TYPE_HOME)
                .subscribe(new BaseResponseObserver<List<TabItemInfo>>() {
                    @Override
                    protected void onRequestData(List<TabItemInfo> tabItemInfos) {
                        LogUtils.e(new Gson().toJson(tabItemInfos));
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        LogUtils.e(throwable.getMessage());
                    }
                });

        HomeTabUtils.getInstance().initOpenHomeSwitch();
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void bindViewModels() {
        mSplashViewModel = bingViewModel(SplashViewModel.class);
        mBinding.setBinding(mSplashViewModel.getSplashDataBinding());
        mBinding.setViewModel(mSplashViewModel);

    }

    /**
     * 从广告详情页返回的标识
     */
    private boolean adPageBackResumeFlag = false;
    @Override
    protected void onResume() {
        super.onResume();
        if (adPageBackResumeFlag) {
            enterHome();
        }
    }

    private int request_time = 0;
    @Override
    protected void loadData() {

        //避免首次授权登陆失败，这里再检查一下
        mSplashViewModel.authVisit();

        mSplashViewModel.splashStatus.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case SplashViewModel.STATUS_EXIT:
                        handleExit();
                        break;
                    case SplashViewModel.STATUS_TO_HOME:
                        PluginManager.get(AppSDKInitPlugin.class).init();
                        OpenInstall.init(SplashActivity.this);
                        OpenInstall.getInstall(installAdapter);
                        enterHome();
                        break;
                    case SplashViewModel.STATUS_TO_AD_BACK:
                        adPageBackResumeFlag = true;
                        break;
                    default:
                        break;
                }
            }
        });

        int launchState = mSplashViewModel.getLaunchState();
        switch (launchState) {
            case LaunchSharedPrefs.STATE_INSTALL:
                PluginManager.get(InitializationPlugin.class)
                                .getInitializationPRepository()
                                .getInitializationConfigure()
                                .subscribe(new BaseResponseObserver<Object>() {
                                    @Override
                                    protected void onRequestData(Object o) {
                                        OnStateInstall();
                                    }

                                    @Override
                                    protected void onRequestError(Throwable throwable) {
                                        if(request_time == 3){
                                            ToastUtils.showShort("网络错误");
                                            return;
                                        }
                                        loadData();
                                        request_time++;
                                    }
                                });
                break;
            case LaunchSharedPrefs.STATE_UPDATE:
                OnStateUpdate();
                break;
            case LaunchSharedPrefs.STATE_NORMAL:
            default:
                OnStateNormal();
                break;
        }

    }

    /**
     * 首次安装
     */
    private void OnStateInstall() {
        // 首次安装不处理广告，首先显示确认页
        mSplashViewModel.onStateInstall(this);
    }

    /**
     * 更新后启动
     */
    private void OnStateUpdate() {
        tryShowAd();
    }

    /**
     * 普通打开
     */
    private void OnStateNormal() {
        tryShowAd();
    }

    /**
     * 尝试显示广告
     */
    private void tryShowAd() {

        PluginManager.get(AppSDKInitPlugin.class).init();
        OpenInstall.init(this);
        OpenInstall.getInstall(installAdapter);

        SplashAdManager.getInstance().loadSplash(new SplashAdManager.SplashItemReadyListener() {
            @Override
            public void onItemReady(SplashAdEntity model, File modelFile) {
                if (model == null || modelFile == null || !modelFile.exists()) {
                    enterHome();
                    return;
                }
                if (model.sourceType == SplashConstants.SPLASH_SOURCE_TYPE_IMG) {
                    handleAdImgShow(model, modelFile);
                    return;
                }
                if (model.sourceType == SplashConstants.SPLASH_SOURCE_TYPE_VIDEO) {
                    handleAdVideoShow(model, modelFile);
                    return;
                }
                enterHome();
            }

            @Override
            public void onNoneAvailableItem() {
                enterHome();
            }
        });

    }


    /**
     * 图片广告显示
     */
    private void handleAdImgShow(@NonNull SplashAdEntity model, @NonNull File modelFile) {
        if (isFinishing()){
            return;
        }
        mBinding.adImg.setVisibility(View.VISIBLE);
        Glide.with(mBinding.adImg).load(modelFile).centerCrop().into(mBinding.adImg);
        mSplashViewModel.startCountDown(model);
        AnimUtils.setShowAnimation(mBinding.adImg,300);

        statSplash(model);

    }

    /**
     * 视频广告显示
     */
    private void handleAdVideoShow(@NonNull SplashAdEntity model, @NonNull File modelFile) {
        AnimUtils.setShowAnimation(mBinding.adPlayerView, 500);
        mBinding.adPlayerView.setVisibility(View.VISIBLE);
        mBinding.adPlayerView.setMediaController(null);
        mBinding.adPlayerView.setCanResize(false);

        String path = modelFile.getAbsolutePath();
        mBinding.adPlayerView.startPlay(path);
        mBinding.adPlayerView.setLoop(true);
        mBinding.adPlayerView.setMute(true);
        mBinding.adPlayerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN);
        mSplashViewModel.startCountDown(model);

        statSplash(model);
    }

    private void statSplash(SplashAdEntity entity) {
        String extendId = entity.extendId;
        String extendName = entity.title;
        String isClick = (TextUtils.isEmpty(entity.adUrl) && TextUtils.isEmpty(entity.mediaId)) ? "0" : "1";
        JsonObject ds = new JsonObject();
        ds.addProperty(StatConstants.DS_KEY_EXTEND_ID, extendId);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_NAME, extendName);
        ds.addProperty(StatConstants.DS_KEY_IS_CLICK, isClick);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withType(StatConstants.TYPE_SHOW_E)
                .withEv(StatConstants.EV_SPLASH)
                .withDs(ds.toString())
                .withPid(StatPid.SPLASH)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    /**
     * 处理退出事件
     */
    private void handleExit() {
        finish();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    AppInstallAdapter installAdapter = new AppInstallAdapter() {
        @Override
        public void onInstall(AppData appData) {
            if (appData != null) uploadChanelMessage(appData.getChannel());
        }
    };

    private void uploadChanelMessage(String channelId) {
        if (TextUtils.isEmpty(channelId)){
            return;
        }
        GVideoSensorDataManager.getInstance().setChannel(channelId);
        SharedPrefsWrapper sharedPrefsWrapper = new SharedPrefsWrapper("openInstall");
        if (sharedPrefsWrapper.getBoolean("channel",false)){
            return;
        }
        SplashRepository repository = new SplashRepository();
        repository.uploadInstallChannel(channelId)
                .subscribe(new BaseResponseObserver<String>() {
                    @Override
                    protected void onRequestData(String o) {
                        LogUtils.d("channel 上传成功：" + o);
                        sharedPrefsWrapper.putBoolean("channel",true);
//                        AsyncUtils.runOnUIThread(() -> showToast("channelId:"+channelId+"上传成功"));
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        LogUtils.e("channel 上传失败：" + throwable.getMessage());
//                        AsyncUtils.runOnUIThread(() -> showToast("channelId:"+channelId+throwable.getMessage()));
                    }
                });
    }

    private void enterHome(){

        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

}

