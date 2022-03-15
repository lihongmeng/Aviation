package com.hzlz.aviation.feature.account.ui.ugc;

import static com.hzlz.aviation.kernel.base.Constant.COUNTRY_CODE.CHINA;
import static com.hzlz.aviation.kernel.base.plugin.AccountPlugin.VERIFICATION_STATUS_NO_VERIFY;
import static com.hzlz.aviation.kernel.base.plugin.AccountPlugin.VERIFICATION_STATUS_VERIFYING;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.LoginResponse;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.repository.UserRepository;
import com.hzlz.aviation.feature.account.utils.HeaderUtils;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.exception.GVideoAPIException;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class OneKeyLoginViewModel extends BaseViewModel {

    public final CheckThreadLiveData<Object> finish = new CheckThreadLiveData<>();

    public long resumeTime;
    public long stayTime;

    public OneKeyLoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void startLogin(View view, String token, Bundle bundle) {
        new UserRepository().quickLogin(CHINA, token).subscribe(new GVideoResponseObserver<LoginResponse>() {

            @Override
            protected void onSuccess(@NonNull LoginResponse response) {
                // 通知登录
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).post(null);
                GVideoSensorDataManager.getInstance().login();
                if (view == null) {
                    return;
                }
                Context context = view.getContext();
                if (context == null) {
                    return;
                }

                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                int auditStatusValue;

                User user = response.getUser();
                User.NewValue auditStatus = user.getNewNickname();
                if (auditStatus == null) {
                    auditStatusValue = VERIFICATION_STATUS_NO_VERIFY;
                } else {
                    auditStatusValue = auditStatus.getAuditStatus();
                }

                // 如果id和nickname相同，并且不是审核中的状态，就需要设置昵称
                String mId = user.getId();
                String mNickName = user.getNickname();
                if (mId != null
                        && !TextUtils.isEmpty(mId)
                        && mId.equals(mNickName)
                        && auditStatusValue != VERIFICATION_STATUS_VERIFYING) {
                    accountPlugin.startNickNameSetActivity(context);
                    finish.setValue(null);
                } else {
                    if (StaticParams.isForeLogin) {
                        StaticParams.isForeLogin = false;
                        PluginManager.get(HomePlugin.class).restartApp(context);
                    } else {
                        // 准备一张头像，用于分享
                        HeaderUtils.getInstance().preHeaderImage(context);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof TimeoutException ||
                        throwable instanceof SocketTimeoutException ||
                        throwable instanceof UnknownHostException) {
                    showToast(R.string.all_network_not_available_action_tip);
                    finish.setValue(null);
                    return;
                }
                if (throwable instanceof GVideoAPIException) {
                    String message = throwable.getMessage();
                    if (!TextUtils.isEmpty(message)) {
                        showToast(message);
                    }
                    finish.setValue(null);
                }
            }
        });
    }
}
