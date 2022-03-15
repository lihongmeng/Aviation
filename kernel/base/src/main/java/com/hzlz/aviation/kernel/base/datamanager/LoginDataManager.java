package com.hzlz.aviation.kernel.base.datamanager;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.aes.AesUtils;

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
