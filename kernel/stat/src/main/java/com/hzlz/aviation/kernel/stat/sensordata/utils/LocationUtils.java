package com.hzlz.aviation.kernel.stat.sensordata.utils;

import android.content.Context;

import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

/**
 * 定位工具类
 *
 */
public class LocationUtils implements TencentLocationListener {

    private static LocationUtils locationUtils;
    private TencentLocationRequest locationRequest;
    private TencentLocationManager locationManager;
    private TencentLocation location;
    private LocationChangedListener listener;
    private String areas;

    public TencentLocation getLocation() {
        return location;
    }

    public String getAreas() {
        return areas;
    }

    private LocationUtils() {
        locationRequest = TencentLocationRequest.create();
        locationRequest.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
    }

    public static LocationUtils getInstance() {
        if (locationUtils == null) {
            locationUtils = new LocationUtils();
        }
        return locationUtils;
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error,
                                  String reason) {
        if (error == TencentLocation.ERROR_OK) {
            locationUtils.location = location;
            locationManager.removeUpdates(locationUtils);
        }
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(location.getProvince())
                    .append(",")
                    .append(location.getCity())
                    .append(",")
                    .append(location.getDistrict());
            areas = sb.toString();
            GVideoSensorDataManager.getInstance().setLocation(location.getLatitude(),location.getLongitude());
        }
        if (listener != null) {
            listener.locationChanged(location);
        }
    }

    public interface LocationChangedListener {
        void locationChanged(TencentLocation location);
    }

    public void setOnLocationChangedListener(LocationChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
    }

    public void location(Context context) {
        try {
            locationManager = TencentLocationManager.getInstance(context);
            locationManager.requestLocationUpdates(locationRequest, locationUtils);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationUtils);
        }
    }
}
