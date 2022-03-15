package com.hzlz.aviation.kernel.base.plugin;

import com.hzlz.aviation.library.ioc.Plugin;

/**
 * @author huangwei
 * date : 2021/10/28
 * desc :
 **/
public interface AppSDKInitPlugin extends Plugin {

    /**
     * 隐私协议同意后初始化sdk
     */
    void init();

    boolean hasInit();

}
