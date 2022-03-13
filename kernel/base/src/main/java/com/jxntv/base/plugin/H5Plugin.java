package com.jxntv.base.plugin;

import androidx.annotation.Nullable;
import com.jxntv.ioc.Plugin;

/**
 * H5 页面接口
 *
 *
 * @since 2020-03-07 20:36
 */
public interface H5Plugin extends Plugin {

  //<editor-fold desc="H5 界面 Url">

  /**
   * 获取用户协议 url
   *
   * @return 用户协议 url
   */
  @Nullable
  String getUserAgreementUrl();

  /**
   * 获取法律条款 Url
   *
   * @return 法律条款 Url
   */
  @Nullable
  String getLegalTermsUrl();

  /**
   * 获取隐私条款 Url
   *
   * @return 隐私条款 Url
   */
  @Nullable
  String getPrivacyPolicyUrl();

  /**
   * 获取注销协议 Url
   *
   * @return 注销协议 Url
   */
  @Nullable
  String getCancellationAgreementUrl();

  /**
   * 获取反馈 Url
   *
   * @return 反馈 Url
   */
  @Nullable
  String getFeedbackUrl();

  /**
   * 获取入驻 Url
   *
   * @return 入驻 Url
   */
  @Nullable
  String getSettleInUrl();

  /**
   * 获取举报 Url
   *
   * @return 举报 Url
   */
  @Nullable
  String getReportUrl();


  /**
   * 获取举报 Url
   *
   * @return 举报 Url
   */
  @Nullable
  String getAppQrCodeUrl();


  //</editor-fold>
}
