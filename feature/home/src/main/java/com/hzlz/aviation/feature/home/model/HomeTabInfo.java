package com.hzlz.aviation.feature.home.model;

/**
 * @author huangwei
 * date : 2021/12/29
 * desc : 主页菜单
 **/
public class HomeTabInfo {

    private boolean tabSwitch;
    // 0关闭，1春节
    private Integer tabType;

    public boolean isTabSwitch() {
        return tabSwitch;
    }

    public void setTabSwitch(boolean tabSwitch) {
        this.tabSwitch = tabSwitch;
    }

    public Integer getTabType() {
        return tabType;
    }

    public void setTabType(Integer tabType) {
        this.tabType = tabType;
    }
}
