package com.hzlz.aviation.kernel.base.model;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public class ImConfigModel {

    private int imAppId;
    private String imSign;
    private String licenseKey;
    private String licenseUrl;

    public int getImAppId() {
        return imAppId;
    }

    public void setImAppId(int imAppId) {
        this.imAppId = imAppId;
    }

    public String getImSign() {
        return imSign;
    }

    public void setImSign(String imSign) {
        this.imSign = imSign;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }


}
