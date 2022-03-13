package com.jxntv.account.model.annotation;

import androidx.annotation.IntDef;
import com.jxntv.base.plugin.AccountPlugin;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 审核状态
 *
 *
 * @since 2020-02-18 10:47
 */
@IntDef({
    Verification.REJECT,
    Verification.VERIFYING,
    Verification.VERIFIED,
})
@Retention(RetentionPolicy.SOURCE)
public @interface Verification {
  int REJECT = AccountPlugin.VERIFICATION_STATUS_REJECT;
  int VERIFYING = AccountPlugin.VERIFICATION_STATUS_VERIFYING;
  int VERIFIED = AccountPlugin.VERIFICATION_STATUS_VERIFIED;
}
