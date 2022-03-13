package com.jxntv.account.ui.ugc;

import static com.jxntv.base.Constant.COUNTRY_CODE.CHINA;
import static com.jxntv.base.plugin.AccountPlugin.VERIFICATION_STATUS_NO_VERIFY;
import static com.jxntv.base.plugin.AccountPlugin.VERIFICATION_STATUS_VERIFYING;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.account.R;
import com.jxntv.account.model.LoginResponse;
import com.jxntv.account.model.User;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.account.utils.HeaderUtils;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.Constant;
import com.jxntv.base.StaticParams;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.exception.GVideoAPIException;
import com.jxntv.sensordata.GVideoSensorDataManager;

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
