package com.jxntv.stat;

import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.CircleComment;
import com.jxntv.base.plugin.StatPlugin;
import com.jxntv.sensordata.GVideoSensorDataManager;

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
