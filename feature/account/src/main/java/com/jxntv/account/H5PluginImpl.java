package com.jxntv.account;

import androidx.annotation.Nullable;
import com.jxntv.account.presistence.H5UrlManager;
import com.jxntv.base.plugin.H5Plugin;

/**
 * H5 接口实现
 *
 *
 * @since 2020-03-07 20:45
 */
public final class H5PluginImpl implements H5Plugin {
  //<editor-fold desc="方法实现">
  @Nullable
  @Override
  public String getUserAgreementUrl() {
    return H5UrlManager.getUserAgreementUrl();
  }

  @Nullable
  @Override
  public String getLegalTermsUrl() {
    return H5UrlManager.getLegalTermsUrl();
  }

  @Nullable
  @Override
  public String getPrivacyPolicyUrl() {
    return H5UrlManager.getPrivacyPolicyUrl();
  }

  @Nullable
  @Override
  public String getCancellationAgreementUrl() {
    return H5UrlManager.getCancellationAgreementUrl();
  }

  @Nullable
  @Override
  public String getFeedbackUrl() {
    return H5UrlManager.getFeedbackUrl();
  }

  @Nullable
  @Override
  public String getSettleInUrl() {
    return H5UrlManager.getSettleInUrl();
  }

  @Nullable
  @Override
  public String getReportUrl() {
    return H5UrlManager.getReportUrl();
  }

  @Nullable
  @Override
  public String getAppQrCodeUrl() {
    return H5UrlManager.getAppQrCodeUrl();
  }
  //</editor-fold>
}
