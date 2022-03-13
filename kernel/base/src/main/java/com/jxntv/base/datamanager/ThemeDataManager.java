package com.jxntv.base.datamanager;

import static com.jxntv.base.Constant.THEME_COLOR_SWITCH_TOP.NORMAL;

public class ThemeDataManager {

    private Boolean themeColorSwitch;
    private Integer themeColorSwitchInteger;

    private volatile static ThemeDataManager singleInstance = null;

    private ThemeDataManager() {
    }

    public static ThemeDataManager getInstance() {
        if (singleInstance == null) {
            synchronized (ThemeDataManager.class) {
                if (singleInstance == null) {
                    singleInstance = new ThemeDataManager();
                }
            }
        }
        return singleInstance;
    }

    public Boolean getThemeColorSwitch() {
        return themeColorSwitch;
    }

    public void setThemeColorSwitch(Boolean themeColorSwitch) {
        this.themeColorSwitch = themeColorSwitch;
    }

    public int getThemeColorSwitchInteger() {
        return themeColorSwitchInteger == null ? NORMAL : themeColorSwitchInteger;
    }

    public void setThemeColorSwitchInteger(Integer themeColorSwitchInteger) {
        this.themeColorSwitchInteger = themeColorSwitchInteger;
    }
}
