package com.hzlz.aviation.kernel.base.screenprojection;

import static android.view.animation.Animation.INFINITE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.liuwei.android.upnpcast.device.CastDevice;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.ModelDetails;

import java.util.ArrayList;
import java.util.List;

public class ScreenProjectionAdapter extends BaseAdapter {

    private final List<CastDevice> dataSource = new ArrayList<>();
    private int currentSelectedIndex = -1;
    private Context context;
    private boolean isLoading;
    private RotateAnimation rotateAnimation;

    public ScreenProjectionAdapter(Context context) {
        this.context = context;
        rotateAnimation = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(INFINITE);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CastDevice castDevice = dataSource.get(position);
        if (castDevice == null) {
            return null;
        }
        convertView = LayoutInflater.from(context).inflate(R.layout.item_screen_projection, null);
        ((AviationTextView) convertView.findViewById(R.id.name)).setText(castDevice.getName());

        ImageView loading = convertView.findViewById(R.id.loading);
        if (isLoading && position == currentSelectedIndex) {
            loading.setVisibility(View.VISIBLE);
            loading.setAnimation(rotateAnimation);
            rotateAnimation.start();
        } else {
            loading.setVisibility(View.GONE);
            loading.clearAnimation();
        }

        convertView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.DIMEN_48DP))
        );

        return convertView;
    }

    public void updateDataSource(List<CastDevice> list) {
        dataSource.clear();
        dataSource.addAll(list);

        if (dataSource.isEmpty()) {
            currentSelectedIndex = -1;
        }
        notifyDataSetChanged();
    }

    public void updateSelectedDeviceToLoading(int index) {
        if (index < 0 || index >= getCount()) {
            currentSelectedIndex = 0;
            isLoading = false;
        } else {
            currentSelectedIndex = index;
            isLoading = true;
        }
        notifyDataSetChanged();
    }

    public void updateSelectedDeviceToComplete() {
        isLoading = false;
        notifyDataSetChanged();
    }

    public CastDevice getCurrentCastDevice() {
        if (currentSelectedIndex < 0) {
            return null;
        }
        return dataSource.get(currentSelectedIndex);
    }

    public void addDevice(CastDevice castDevice) {
        if (castDevice == null) {
            return;
        }
        if (dataSource.isEmpty()) {
            dataSource.add(castDevice);
            notifyDataSetChanged();
            return;
        }
        for (CastDevice temp : dataSource) {
            if (temp == null || TextUtils.equals(temp.getId(), castDevice.getId())) {
                continue;
            }
            dataSource.add(castDevice);
            notifyDataSetChanged();
            break;
        }
    }

    public String getModelUrl(CastDevice castDevice) {
        if (castDevice == null) {
            return "";
        }
        Device device = castDevice.getDevice();
        if (device == null) {
            return "";
        }
        DeviceDetails deviceDetails = device.getDetails();
        if (deviceDetails == null) {
            return "";
        }
        ModelDetails modelDetails = deviceDetails.getModelDetails();
        if (modelDetails == null) {
            return "";
        }
        String modelUrl = modelDetails.getModelURI().toString();
        return TextUtils.isEmpty("") ? "" : modelUrl;
    }

    public void removeDevice(CastDevice castDevice) {
        if (castDevice == null) {
            return;
        }
        for (CastDevice temp : dataSource) {
            if (temp == null || !TextUtils.equals(temp.getId(), castDevice.getId())) {
                continue;
            }
            dataSource.remove(temp);
            notifyDataSetChanged();
            break;
        }
    }

    public boolean isDataSourceEmpty() {
        return getCount() <= 0;
    }

    public List<String> getDeviceNameList() {
        List<String> result = new ArrayList<>();
        for (CastDevice castDevice : dataSource) {
            if (castDevice == null) {
                continue;
            }
            String deviceName = castDevice.getName();
            if (TextUtils.isEmpty(deviceName)) {
                continue;
            }
            result.add(deviceName);
        }
        return result;
    }

}
