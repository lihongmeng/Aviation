package com.hzlz.aviation.kernel.stat.stat;

import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.plugin.StatPlugin;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;

import java.util.List;

public class StatPluginImpl implements StatPlugin {

    @Override
    public void bannerClick(
            BannerModel.ImagesBean imagesBean,
            int index,
            String dataSourcePid,
            String bannerType
    ) {
        GVideoSensorDataManager.getInstance().bannerClick(
                imagesBean,
                index,
                dataSourcePid,
                bannerType
        );
    }

    @Override
    public void enterRegister(String sourceFragmentPid, String element) {
        GVideoSensorDataManager.getInstance().enterRegister(
                sourceFragmentPid,
                element
        );
    }

    @Override
    public void exitDeviceSelectPage(
            long stayTime,
            List<String> deviceNameList,
            String selectDevice,
            boolean isSuccess,
            String failReason
    ) {
        GVideoSensorDataManager.getInstance().exitDeviceSelectPage(
                stayTime,
                deviceNameList,
                selectDevice,
                isSuccess,
                failReason
        );
    }

    @Override
    public void dialogShow(Circle circle) {
        GVideoSensorDataManager.getInstance().dialogShow(circle);
    }

    @Override
    public void dialogClick(Circle circle) {
        GVideoSensorDataManager.getInstance().dialogClick(circle);
    }

    @Override
    public void dialogClose(Circle circle) {
        GVideoSensorDataManager.getInstance().dialogClose(circle);
    }

}
