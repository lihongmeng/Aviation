package com.jxntv.account.model.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @since 2020-03-05 10:23
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        SmsCodeType.LOGIN,
        SmsCodeType.REBIND_PHONE,
        SmsCodeType.SWITCH_ACCOUNT,
        SmsCodeType.NICK_NAME_SET
})
public @interface SmsCodeType {
    int LOGIN = 1;
    int REBIND_PHONE = 2;
    int SWITCH_ACCOUNT = 3;
    int NICK_NAME_SET = 4;
}
