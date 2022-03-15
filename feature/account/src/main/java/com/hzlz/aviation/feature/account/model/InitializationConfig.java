package com.hzlz.aviation.feature.account.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.hzlz.aviation.kernel.base.model.update.UpdateModel;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @since 2020-03-07 21:02
 */
public final class InitializationConfig {
  //<editor-fold desc="属性">
  @Nullable
  @SerializedName("userPolicy")
  private String mUserAgreementUrl;
  @Nullable
  @SerializedName("lawPolicy")
  private String mLegalTermsUrl;
  @Nullable
  @SerializedName("privacyPolicy")
  private String mPrivacyPolicyUrl;
  @Nullable
  @SerializedName("cancelPolicy")
  private String mCancellationAgreementUrl;
  @Nullable
  @SerializedName("feedback")
  private String mFeedbackUrl;
  @Nullable
  @SerializedName("settle")
  private String mSettleInUrl;
  @Nullable
  @SerializedName("report")
  private String mReportUrl;

  @SerializedName("versionUpdate")
  private UpdateModel mUpdateModel;

  @SerializedName("selectVideoMaxTime")
  private int mSelectVideoTime;
  //</editor-fold>
  @SerializedName("qrcodeUrl")
  private String mQrcodeUrl;

  private InitConfig initConfig;

  /**
   * 分享开关
   */
  private HashMap<String, Boolean> shareSwitch;

  private Boolean themeColorSwitch;

  private Integer themeColorType;
  /**
   * 搜索预制文案
   */
  private List<PlaceholderConfig> placeholderList;

  private String quickLoginKey;

  //<editor-fold desc="Getter">

  public boolean getWeiXin() {
    return getShareValue("share_weixin");
  }

  public boolean getQQ() {
    return getShareValue("share_qq");
  }

  public boolean getWeiBo() {
    return getShareValue("share_weibo");
  }

  private boolean getShareValue(String key) {
    if (shareSwitch != null && shareSwitch.get(key) != null) {
      return shareSwitch.get(key);
    }
    return false;
  }

  public String getQuickLoginKey() {
    return quickLoginKey;
  }

  public InitConfig getInitConfig() {
    return initConfig;
  }

  public void setInitConfig(InitConfig initConfig) {
    this.initConfig = initConfig;
  }

  @Nullable
  public String getUserAgreementUrl() {
    return mUserAgreementUrl;
  }

  @Nullable
  public String getLegalTermsUrl() {
    return mLegalTermsUrl;
  }

  @Nullable
  public String getPrivacyPolicyUrl() {
    return mPrivacyPolicyUrl;
  }

  @Nullable
  public String getCancellationAgreementUrl() {
    return mCancellationAgreementUrl;
  }

  @Nullable
  public String getFeedbackUrl() {
    return mFeedbackUrl;
  }

  @Nullable
  public String getSettleInUrl() {
    return mSettleInUrl;
  }

  @Nullable
  public String getReportUrl() {
    return mReportUrl;
  }

  public UpdateModel getUpdateModel() {
    return mUpdateModel;
  }

  public int getSelectVideoMaxTime() {
    return mSelectVideoTime;
  }

  public String getQrcodeUrl() {
    return mQrcodeUrl;
  }

  public void setQrcodeUrl(String mQrcodeUrl) {
    this.mQrcodeUrl = mQrcodeUrl;
  }

  public class InitConfig{

    private String aliToken;
    private String nlpAppKey;

    public String getAliToken() {
      return aliToken;
    }

    public void setAliToken(String aliToken) {
      this.aliToken = aliToken;
    }

    public String getNlpAppKey() {
      return nlpAppKey;
    }

    public void setNlpAppKey(String nlpAppKey) {
      this.nlpAppKey = nlpAppKey;
    }
  }

  public Boolean getThemeColorSwitch() {
    return themeColorSwitch;
  }

  public void setThemeColorSwitch(Boolean themeColorSwitch) {
    this.themeColorSwitch = themeColorSwitch;
  }

  public Integer getThemeColorType() {
    return themeColorType;
  }

  public void setThemeColorType(Integer themeColorType) {
    this.themeColorType = themeColorType;
  }


  public List<PlaceholderConfig> getPlaceholderList() {
    return placeholderList;
  }

  public void setPlaceholderList(List<PlaceholderConfig> placeholderList) {
    this.placeholderList = placeholderList;
  }

  public static class PlaceholderConfig{
    private String placeholder;
    private String startDate;
    private String endDate;

    public String getPlaceholder() {
      return placeholder;
    }

    public void setPlaceholder(String placeholder) {
      this.placeholder = placeholder;
    }

    public String getStartDate() {
      return startDate;
    }

    public void setStartDate(String startDate) {
      this.startDate = startDate;
    }

    public String getEndDate() {
      return endDate;
    }

    public void setEndDate(String endDate) {
      this.endDate = endDate;
    }
  }

  //</editor-fold>
}
