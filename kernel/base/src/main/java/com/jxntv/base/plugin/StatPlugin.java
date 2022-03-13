package com.jxntv.base.plugin;

import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.ioc.Plugin;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/10/28
 * desc :
 **/
public interface StatPlugin extends Plugin {

    void bannerClick(
            BannerModel.ImagesBean imagesBean,
            int index,
            String dataSourcePid,
            String bannerType
    );

    void enterRegister(
            String sourceFragmentPid,
            String element
    );

    void exitDeviceSelectPage(
            long stayTime,
            List<String> deviceNameList,
            String selectDevice,
            boolean isSuccess,
            String failReason
    );

    void dialogShow(Circle circle);

    void dialogClick(Circle circle);

    void dialogClose(Circle circle);

}
