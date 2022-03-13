package com.jxntv.live;

import static com.jxntv.base.Constant.EVENT_BUS_EVENT.LOGIN;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.START_LOGIN;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.plugin.LivePlugin;
import com.jxntv.base.sp.SharedPrefsWrapper;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.dialog.MessageConfirmDialog;
import com.jxntv.live.liveroom.MLVBLiveRoomImpl;
import com.jxntv.live.liveroom.live.entity.TCUserMgr;
import com.jxntv.live.liveroom.live.net.TCHTTPMgr;
import com.jxntv.base.model.ImConfigModel;
import com.jxntv.live.model.OpenLiveResultModel;
import com.jxntv.live.repository.LiveRepository;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.DeviceId;
import com.jxntv.utils.LogUtils;
import com.jxntv.utils.ResourcesUtils;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.rtmp.TXLiveBase;

import org.json.JSONObject;

import java.util.Random;

import io.reactivex.rxjava3.core.Observable;

/**
 * @author huangwei
 * date : 2021/3/5
 * desc : im、云直播信息管理
 **/
public class LiveManager {

    private static LiveManager helper;
    private ImConfigModel imConfigModel;
    private boolean initSuccess = false;
    private LiveRepository liveRepository = new LiveRepository();

    public static LiveManager getInstance() {
        if (helper == null) {
            helper = new LiveManager();
        }

        return helper;
    }

    public void init() {
        initListener();
        getIMConfig();
    }

    /**
     * 检查sdk 是否初始化成功
     */
    public boolean checkOrInitSuccess() {
        if (!initSuccess) {
            if (imConfigModel == null) {
                getIMConfig();
            } else {
                loginTXService();
            }
        }
        return initSuccess;
    }

    /**
     * 初始化
     */
    private void getIMConfig() {
        logoutTxService();
        liveRepository.getIMConfig(getUserId()).subscribe(new BaseResponseObserver<ImConfigModel>() {
            @Override
            protected void onRequestData(ImConfigModel configModel) {
                imConfigModel = configModel;
                if (imConfigModel != null) {
                    initSDK(imConfigModel);
                }
            }

            @Override
            protected void onRequestError(Throwable throwable) {
            }
        });
    }

    /**
     * 登录监听
     */
    private void initListener() {

        GVideoEventBus.get(LOGIN).observeForever(o -> {
            //通知直播间对游客账号进行退群
            GVideoEventBus.get(Constants.TX_LOGIN_EVENT_BEFORE, String.class).post(null);
            new Handler().postDelayed(() -> getIMConfig(), 100);
        });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observeForever(o -> {
            getIMConfig();
        });

    }

    /**
     * 初始化SDK
     *
     * @param configModel 配置信息
     */
    private void initSDK(ImConfigModel configModel) {

        // Tencent IM 配置 Config，请按需配置
//        TUIKitConfigs configs = TUIKit.getConfigs();
//        configs.setSdkConfig(new TIMSdkConfig(configModel.getImAppId()));
//        configs.setCustomFaceConfig(new CustomFaceConfig());
//        configs.setGeneralConfig(new GeneralConfig());
//        TUIKit.init(GVideoRuntime.getAppContext(), configModel.getImAppId(), configs);
        // 必须：初始化 LiteAVSDK Licence。 用于直播推流鉴权。
        TXLiveBase.getInstance().setLicence(GVideoRuntime.getAppContext(), configModel.getLicenseUrl(), imConfigModel.getLicenseKey());
        // 必须：初始化 MLVB 组件
        MLVBLiveRoomImpl.sharedInstance(GVideoRuntime.getAppContext());
        // 必须：初始化全局的 用户信息管理类，记录个人信息。
        TCUserMgr.getInstance().initContext(GVideoRuntime.getAppContext());

        PluginManager.get(ChatIMPlugin.class).login(configModel);
        loginTXService();
    }

    /**
     * 登录
     */
    private void loginTXService() {

        //清理缓存
        TCUserMgr.getInstance().logout();
        //退出后，重新登录
        TCUserMgr.getInstance().login(getUserId(), "", new TCHTTPMgr.Callback() {
            @Override
            public void onSuccess(JSONObject data) {
                initSuccess = true;
                LogUtils.d(getUserId() + "   小直播登录成功");
                // 重新登录成功后通知直播间重新登录
                GVideoEventBus.get(Constants.TX_LOGIN_EVENT_AFTER, String.class).post(null);
            }

            @Override
            public void onFailure(int code, final String msg) {
                initSuccess = false;
                LogUtils.e("小直播初始化失败，code = " + code + " ,error = " + msg);
            }
        });
    }

    /**
     * 退出tx登录
     */
    private void logoutTxService() {
        if (V2TIMManager.getInstance().getLoginStatus() == V2TIMManager.V2TIM_STATUS_LOGINED){
            if (!TextUtils.equals(getUserId(),V2TIMManager.getInstance().getLoginUser())){
                PluginManager.get(ChatIMPlugin.class).logout();
            }
        }
    }

    public boolean checkOrLogin() {
        String token = PluginManager.get(AccountPlugin.class).getToken();
        boolean isLogin = !TextUtils.isEmpty(token);
        if (!isLogin) {
            GVideoEventBus.get(START_LOGIN, String.class).post(null);
        }
        return isLogin;
    }


    private String getRandom() {
        Random random = new Random();
        //生产10 - 99的随机数
        int random1 = (random.nextInt(91) + 9);
        int random2 = (random.nextInt(91) + 9);
        int random3 = (random.nextInt(91) + 9);
        int random4 = (random.nextInt(91) + 9);
        return "" + random1 + random2 + random3 + random4;
    }

    //------------------ API相关  ------------------------

    /**
     * 是否有直播权限
     *
     * @return
     */
    public Observable<String> hadLivePermission() {

        return Observable.create(e -> {

            liveRepository.getHasLivePermission().subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
                @Override
                protected void onRequestData(Object s) {
                    e.onNext("");
                }

                @Override
                protected void onRequestError(Throwable throwable) {
                    e.onNext(throwable.getMessage());
                }
            });

        });
    }

    /**
     * 检查是否有直播
     */
    public void checkHasLive(Context context) {

        if (TextUtils.isEmpty(PluginManager.get(AccountPlugin.class).getToken())) {
            return;
        }
        liveRepository.checkHasLive().subscribe(new BaseViewModel.BaseGVideoResponseObserver<OpenLiveResultModel>() {
            @Override
            protected void onRequestData(OpenLiveResultModel model) {
                if (model.getOnline() == 1) {
                    MessageConfirmDialog dialog = new MessageConfirmDialog(context);
                    dialog.setMessage("重新链接", "您意外退出直播：" + model.getTitle() + ",是否重新连接?");
                    dialog.setDoubleButton("取消", view -> {
                        upLoadLiveMessage(0, 0);

                    }, "重新连接", view -> {
                        PluginManager.get(LivePlugin.class).startAuthorLiveActivity(
                                context,
                                model.getMediaId(),
                                model.getTitle(),
                                model.getShareUrl(),
                                null
                        );

                    });
                    dialog.show();
                }
            }

            @Override
            protected void onRequestError(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
            }

        });
    }

    /**
     * 上报直播数据
     *
     * @param viewSum 观看人次
     * @param digg    点赞数
     */
    public void upLoadLiveMessage(int viewSum, int digg) {

        liveRepository.uploadLiveEndMessage(viewSum, digg)
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {

                    @Override
                    protected void onRequestData(@NonNull Object o) {
                        LogUtils.e("直播结束数据上报：" + new Gson().toJson(o));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        LogUtils.e("直播结束数据上报 Error ：" + throwable.getMessage());
                    }
                });

    }

    //-------------------  获取用户信息    -----------------

    public String getUserId() {
        String userId = PluginManager.get(AccountPlugin.class).getUserId();
        if (TextUtils.isEmpty(userId)) {
            userId = DeviceId.get().replace(":", "");
        }
        return userId;
    }

    public int getAppId() {
        if (imConfigModel != null) {
            return imConfigModel.getImAppId();
        }
        return 0;
    }

    public String getUserSig() {
        if (imConfigModel != null) {
            return imConfigModel.getImSign();
        }
        return "";
    }

    public String getUserAvatar() {
        return PluginManager.get(AccountPlugin.class).getRealUserAvatar();
    }

    public String getNickName() {
        String nickName = PluginManager.get(AccountPlugin.class).getNickName();
        //生成游客id,name
        if (TextUtils.isEmpty(nickName)) {
            SharedPrefsWrapper sp = new SharedPrefsWrapper(this.getClass().getName());
            nickName = sp.getString("userName", "");
            if (TextUtils.isEmpty(nickName)) {
                nickName = "游客：" + getRandom();
            }
        }
        return nickName;
    }

}
