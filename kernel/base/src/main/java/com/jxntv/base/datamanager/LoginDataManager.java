package com.jxntv.base.datamanager;

import com.jxntv.aes.AesUtils;
import com.jxntv.base.Constant;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.ioc.PluginManager;

public class LoginDataManager {

    private String quickLoginKey;

    private volatile static LoginDataManager singleInstance = null;

    private LoginDataManager() {
    }

    public static LoginDataManager getInstance() {
        if (singleInstance == null) {
            synchronized (LoginDataManager.class) {
                if (singleInstance == null) {
                    singleInstance = new LoginDataManager();
                }
            }
        }
        return singleInstance;
    }

    public String getQuickLoginKey() {
        return quickLoginKey;
    }

    public void setQuickLoginKey(String quickLoginKey) {
        this.quickLoginKey = AesUtils.getInstance().decrypt(
                Constant.AES_SIGN.ONE_KEY_LOGIN,
                Constant.AES_SIGN_IV.ONE_KEY_LOGIN,
                quickLoginKey
        );
        PluginManager.get(AccountPlugin.class).initOneKeyLoginSDK();
    }

}
