package com.hzlz.aviation.feature.account.ui.sms;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.AviationButton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 短信验证码界面数据绑定
 *
 * @since 2020-01-14 11:55
 */
@SuppressWarnings("FieldCanBeLocal")
public final class SmsCodeDataBinding {

    public String mCountryCode = "";

    public String mPhoneNumber = "";

    // 短信验证码，或者邀请码
    public String smsInviteCode = "";

    // 短信验证码已发送至XXXXXXXXXXXX
    public ObservableField<String> msmCodeSent = new ObservableField<>();
    //
    // // 是否展示邀请码界面
    // public ObservableField<Boolean> showInviteCode = new ObservableField<>();

    // 是否启用发送验证码按钮点击
    public ObservableBoolean enableSendSmsCode = new ObservableBoolean();

    // 发送验证码按钮的文本显示
    public ObservableField<String> sendSmsCode = new ObservableField<>();

    // 是否启用确认按钮
    public ObservableField<Boolean> enableConfirm = new ObservableField<>();

    // 用于处理发送验证码倒计时
    private Timer mTimer;
    private int mTimerCountdown;
    private static final int TIMER_MAX_COUNT_DOWN = 60;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public SmsCodeDataBinding() {
        enableSendSmsCode(true);
    }

    protected void init(LoginCodeFragmentArgs args) {
        if (args == null) {
            return;
        }
        mCountryCode = args.getCountryCode();
        mPhoneNumber = args.getPhoneNumber();
    }

    protected void smsCodeSentSuccessfully() {
        msmCodeSent.set(ResourcesUtils.getString(R.string.all_sms_code_has_sent_to_phone, mCountryCode, mPhoneNumber));
        startTimer();
    }

    protected void onCleared() {
        cancelTimer();
        handler.removeCallbacksAndMessages(null);
    }

    private void enableSendSmsCode(boolean enable) {
        enableSendSmsCode.set(enable);
        if (enable) {
            sendSmsCode.set(ResourcesUtils.getString(R.string.all_obtain_sms_code));
        } else {
            sendSmsCode.set(ResourcesUtils.getString(R.string.all_resend_sms_code, mTimerCountdown));
        }
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void startTimer() {
        cancelTimer();
        mTimer = new Timer();
        mTimerCountdown = TIMER_MAX_COUNT_DOWN;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mTimerCountdown == 0) {
                    cancelTimer();
                    enableSendSmsCode(true);
                } else {
                    enableSendSmsCode(false);
                    mTimerCountdown--;
                }
            }
        }, 0, 1000L);
    }

    /**
     * 更新确认按钮的状态
     *
     * @param confirmButton 确认按钮
     */
    public void updateConfirmButtonChange(AviationButton confirmButton) {
        boolean enableConfirmValue = enableConfirm();
        enableConfirm.set(enableConfirmValue);
        confirmButton.setEnabled(enableConfirmValue);
    }

    /**
     * 判断当前按钮是否可以点击
     *
     * @return boolean
     */
    public boolean enableConfirm() {
        // 去除验证码的空格
        smsInviteCode = TextUtils.isEmpty(smsInviteCode) ? "" : smsInviteCode.trim();
        // 验证码6位
        return !TextUtils.isEmpty(smsInviteCode) && smsInviteCode.length() == 6;
    }

}
