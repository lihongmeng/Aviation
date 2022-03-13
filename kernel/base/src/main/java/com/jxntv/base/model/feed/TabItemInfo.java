package com.jxntv.base.model.feed;

import com.google.gson.annotations.SerializedName;

/**
 * tab数据信息
 */
public class TabItemInfo {

    /**
     * tab标签名
     */
    @SerializedName("name")
    public String tabName;

    /**
     * tab id
     */
    @SerializedName("id")
    public String tabId;

    /**
     * 资源id
     * 1000 webView外链界面
     */
    @SerializedName("channelResType")
    public String channelResType;

    /**
     * url
     */
    @SerializedName("url")
    public String url;

    /**
     * 频道是否选中
     */
    public boolean selected;

    /**
     * 构造函数
     */
    public TabItemInfo() {

    }

    /**
     * 构造函数
     */
    public TabItemInfo(String tabName) {
        this.tabName = tabName;
    }

}
