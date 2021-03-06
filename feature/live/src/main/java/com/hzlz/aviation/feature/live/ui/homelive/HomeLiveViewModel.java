package com.hzlz.aviation.feature.live.ui.homelive;

import static com.hzlz.aviation.kernel.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_LIVE;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class HomeLiveViewModel extends BaseViewModel {

    public List<TabItemInfo> tabItemInfoArrayList;

    public CheckThreadLiveData<Boolean> refreshSuccess = new CheckThreadLiveData<>();

    public int failReason = PlaceholderType.EMPTY;

    public HomeLiveViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData(boolean needCompare) {
        PluginManager.get(FeedPlugin.class)
                .getTabItemInfoList(MEDIA_TYPE_LIVE)
                .subscribe(new GVideoResponseObserver<List<TabItemInfo>>() {

                    @Override
                    protected void onSuccess(@NonNull List<TabItemInfo> netList) {

                        if (needCompare && !tabItemInfoArrayList.isEmpty()) {
                            int size = tabItemInfoArrayList.size();

                            for (int index = 0; index < size; index++) {
                                TabItemInfo current = tabItemInfoArrayList.get(index);
                                TabItemInfo net = tabItemInfoArrayList.get(index);

                            }

                        }

                        tabItemInfoArrayList = netList;
                        refreshSuccess.setValue(true);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);

                        if (needCompare) {
                            return;
                        }

                        if (throwable instanceof TimeoutException
                                || throwable instanceof SocketTimeoutException) {
                            failReason = PlaceholderType.NETWORK_NOT_AVAILABLE;
                        } else {
                            failReason = PlaceholderType.ERROR;
                        }

                        if (tabItemInfoArrayList == null) {
                            tabItemInfoArrayList = new ArrayList<>();
                        }
                        refreshSuccess.setValue(false);
                    }

                });

    }

}
