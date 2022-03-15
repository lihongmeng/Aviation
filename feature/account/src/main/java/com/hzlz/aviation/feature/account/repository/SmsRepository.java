package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.request.CheckNeedInviteCodeRequest;
import com.hzlz.aviation.feature.account.request.SendSmsCodeRequest;
import com.hzlz.aviation.feature.account.request.VerificationInviteCodeRequest;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;

/**
 * 验证码仓库类
 *
 * @since 2020-01-13 16:02
 */
public final class SmsRepository extends BaseDataRepository {
    //<editor-fold desc="API">

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     * @param countryCode 国家代码
     * @param type        短信验证码业务类型
     */
    @NonNull
    public Observable<Object> sendSmsCode(
            @NonNull String phoneNumber,
            @NonNull String countryCode,
            int type) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                SendSmsCodeRequest request = new SendSmsCodeRequest();
                request.setPhoneNumber(phoneNumber);
                request.setCountryCode(countryCode);
                request.setType(type);
                return request;
            }
        }.asObservable();
    }

    @NonNull
    public Observable<Boolean> checkNeedInviteCode(@NonNull String mobile) {
        return new OneTimeNetworkData<Boolean>(mEngine) {
            @Override
            protected BaseRequest<Boolean> createRequest() {
                CheckNeedInviteCodeRequest request = new CheckNeedInviteCodeRequest();
                request.setMobile(mobile);
                return request;
            }
        }.asObservable();
    }


    @NonNull
    public Observable<Object> verificationInviteCode(String mobile, String inviteCode) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                VerificationInviteCodeRequest request = new VerificationInviteCodeRequest();
                request.setMobile(mobile);
                request.setInviteCode(inviteCode);
                return request;
            }
        }.asObservable();
    }

    //</editor-fold>
}
