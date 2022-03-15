package com.hzlz.aviation.feature.account.presistence;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.hzlz.aviation.feature.account.model.InitializationConfig;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.FileUtils;

import java.io.InputStream;

/**
 * H5 Url 管理类
 *
 *
 * @since 2020-03-07 20:47
 */
public final class H5UrlManager {
  //<editor-fold desc="常量">
  private static final String SP_FILE_NAME_H5_URL = "h5_url";
  private static final String SP_KEY_USER_AGREEMENT = "user_agreement";
  private static final String SP_KEY_LEGAL_TERMS = "legal_terms";
  private static final String SP_KEY_PRIVACY_POLICY = "privacy_policy";
  private static final String SP_KEY_CANCELLATION_AGREEMENT = "cancellation_agreement";
  private static final String SP_KEY_FEEDBACK = "feedback";
  private static final String SP_KEY_SETTLE_IN = "settle_in";
  private static final String SP_KEY_REPORT = "report";
  private static final String SP_KEY_APP_QR_CODE_URL = "qrcodeUrl";
  //</editor-fold>

  //<editor-fold desc="属性">
  @NonNull
  private static SharedPrefsWrapper sH5UrlSp = new SharedPrefsWrapper(SP_FILE_NAME_H5_URL);
  @Nullable
  private static volatile String sUserAgreementUrl;
  @Nullable
  private static volatile String sLegalTermsUrl;
  @Nullable
  private static volatile String sPrivacyPolicyUrl;
  @Nullable
  private static volatile String sCancellationAgreementUrl;
  @Nullable
  private static volatile String sFeedbackUrl;
  @Nullable
  private static volatile String sSettleInUrl;
  @Nullable
  private static volatile String sReportUrl;
  @Nullable
  private static volatile String sQrcodeUrl;

  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private H5UrlManager() {
    throw new IllegalStateException("no instance !!!");
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 获取用户协议 Url
   *
   * @return 用户协议 Url
   */
  @Nullable
  public static String getUserAgreementUrl() {
    if (sUserAgreementUrl == null) {
      sUserAgreementUrl = getString(SP_KEY_USER_AGREEMENT);
    }
    return sUserAgreementUrl;
  }

  /**
   * 获取法律条款 Url
   *
   * @return 法律条款 Url
   */
  @Nullable
  public static String getLegalTermsUrl() {
    if (sLegalTermsUrl == null) {
      sLegalTermsUrl = getString(SP_KEY_LEGAL_TERMS);
    }
    return sLegalTermsUrl;
  }

  /**
   * 获取隐私政策条款 Url
   *
   * @return 隐私政策条款 Url
   */
  @Nullable
  public static String getPrivacyPolicyUrl() {
    if (sPrivacyPolicyUrl == null) {
      sPrivacyPolicyUrl = getString(SP_KEY_PRIVACY_POLICY);
    }
    return sPrivacyPolicyUrl;
  }

  /**
   * 获取注销协议 Url
   *
   * @return 注销协议 Url
   */
  @Nullable
  public static String getCancellationAgreementUrl() {
    if (sCancellationAgreementUrl == null) {
      sCancellationAgreementUrl = getString(SP_KEY_CANCELLATION_AGREEMENT);
    }
    return sCancellationAgreementUrl;
  }

  /**
   * 获取反馈 Url
   *
   * @return 反馈 Url
   */
  @Nullable
  public static String getFeedbackUrl() {
    if (sFeedbackUrl == null) {
      sFeedbackUrl = getString(SP_KEY_FEEDBACK);
    }
    return sFeedbackUrl;
  }

  /**
   * 获取入驻 Url
   *
   * @return 入驻 Url
   */
  @Nullable
  public static String getSettleInUrl() {
    if (sSettleInUrl == null) {
      sSettleInUrl = getString(SP_KEY_SETTLE_IN);
    }
    return sSettleInUrl;
  }

  /**
   * 获取举报 Url
   *
   * @return 举报 Url
   */
  @Nullable
  public static String getReportUrl() {
    if (sReportUrl == null) {
      sReportUrl = getString(SP_KEY_REPORT);
    }
    return sReportUrl;
  }

  /**
   * 获取app分享地址
   *
   * @return app分享地址
   */
  @Nullable
  public static String getAppQrCodeUrl() {
    if (sQrcodeUrl == null) {
      sQrcodeUrl = getString(SP_KEY_APP_QR_CODE_URL);
    }
    return sQrcodeUrl;
  }

  /**
   * 更新 H5 Url
   *
   * @param config 初始化配置
   */
  public static void updateH5Urls(@NonNull InitializationConfig config) {
    sUserAgreementUrl = config.getUserAgreementUrl();
    sLegalTermsUrl = config.getLegalTermsUrl();
    sPrivacyPolicyUrl = config.getPrivacyPolicyUrl();
    sCancellationAgreementUrl = config.getCancellationAgreementUrl();
    sFeedbackUrl = config.getFeedbackUrl();
    sSettleInUrl = config.getSettleInUrl();
    sReportUrl = config.getReportUrl();
    sQrcodeUrl = config.getQrcodeUrl();
    // 保存 Sp
    sH5UrlSp.putString(SP_KEY_USER_AGREEMENT, sUserAgreementUrl);
    sH5UrlSp.putString(SP_KEY_LEGAL_TERMS, sLegalTermsUrl);
    sH5UrlSp.putString(SP_KEY_PRIVACY_POLICY, sPrivacyPolicyUrl);
    sH5UrlSp.putString(SP_KEY_CANCELLATION_AGREEMENT, sCancellationAgreementUrl);
    sH5UrlSp.putString(SP_KEY_FEEDBACK, sFeedbackUrl);
    sH5UrlSp.putString(SP_KEY_SETTLE_IN, sSettleInUrl);
    sH5UrlSp.putString(SP_KEY_REPORT, sReportUrl);
    sH5UrlSp.putString(SP_KEY_APP_QR_CODE_URL, sQrcodeUrl);
  }

  /** 尝试从sp中加载，没有则尝试从assets中初始化sp再加载 */
  private static String getString(String key) {
    String text = sH5UrlSp.getString(key, null);
    if (TextUtils.isEmpty(text)) {
      initLocal();
      text = sH5UrlSp.getString(key, null);
    }
    return text;
  }

  /** 没有从云端更新数据之前，加载本地预置数据 */
  private static void initLocal() {
    Context c = GVideoRuntime.getAppContext();
    String content;
    try {
      InputStream is = c.getAssets().open("entry.json");
      content = FileUtils.readInputStream(is);
    } catch (Exception e) {
      content = "";
    }
    if (!TextUtils.isEmpty(content)) {
      InitializationConfig config = new Gson().fromJson(content, InitializationConfig.class);
      updateH5Urls(config);
    }
  }
  //</editor-fold>
}
