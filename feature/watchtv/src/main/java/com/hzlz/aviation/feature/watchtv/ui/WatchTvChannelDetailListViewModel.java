package com.hzlz.aviation.feature.watchtv.ui;

import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NETWORK_NOT_AVAILABLE;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.watchtv.WatchTvRepository;
import com.hzlz.aviation.feature.watchtv.entity.ChannelTvManifest;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class WatchTvChannelDetailListViewModel extends BaseViewModel {

    // WatchTvRepository
    private final WatchTvRepository watchTvRepository = new WatchTvRepository();

    // 渠道Id
    public Long channelId;

    // 需要请求的时间，拆开来传是为了方便列表使用
    public int year;
    public int month;
    public int day;

    public int index;

    // 获取当前频道的节目清单数据
    public List<ChannelTvManifest> channelTvManifestList = new ArrayList<>();
    // 通知fragment更新下方的FragmentList
    public final CheckThreadLiveData<Boolean> updateFragmentList = new CheckThreadLiveData<>();

    private final CheckThreadLiveData<Integer> updatePlaceLiveData = new CheckThreadLiveData<>();

    public CheckThreadLiveData<Integer> getUpdatePlaceLiveData(){
        return updatePlaceLiveData;
    }

    public WatchTvChannelDetailListViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateData() {
        if (channelId == null) {
            return;
        }
        if (channelTvManifestList == null || channelTvManifestList.isEmpty()) {
            updatePlaceLiveData.setValue(PlaceholderType.LOADING);
        }

        String monthValue = month < 10 ? "0" + month : "" + month;
        String dayValue = day < 10 ? "0" + day : "" + day;

        watchTvRepository.getChannelTvManifest(
                channelId,
                year + "-" + monthValue + "-" + dayValue
        ).subscribe(new GVideoResponseObserver<List<ChannelTvManifest>>() {

            @Override
            protected void onSuccess(@NonNull List<ChannelTvManifest> channelTvManifests) {
                channelTvManifestList = channelTvManifests;
                updateFragmentList.setValue(true);
                if (channelTvManifestList == null || channelTvManifestList.isEmpty()) {
                    updatePlaceLiveData.setValue(PlaceholderType.EMPTY);
                } else {
                    updatePlaceLiveData.setValue(PlaceholderType.NONE);
                }
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                super.onFailed(throwable);
                channelTvManifestList.clear();
                updateFragmentList.setValue(true);
                if (channelTvManifestList == null || channelTvManifestList.isEmpty()) {
                    if (!NetworkUtils.isNetworkConnected()) {
                        updatePlaceLiveData.setValue(NETWORK_NOT_AVAILABLE);
                    } else {
                        updatePlaceLiveData.setValue(PlaceholderType.EMPTY);
                    }
                }
            }

        });
    }

}
