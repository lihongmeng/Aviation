package com.hzlz.aviation.feature.watchtv.ui;

import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NETWORK_NOT_AVAILABLE;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NONE;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.watchtv.WatchTvRepository;
import com.hzlz.aviation.feature.watchtv.adapter.HomeWatchTvTvListAdapter;
import com.hzlz.aviation.feature.watchtv.callback.SmartRefreshListener;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.video.WatchTvChannel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.feature.watchtv.R;

import java.util.ArrayList;
import java.util.List;

public class HomeWatchTvTvListViewModel extends BaseViewModel {

    // 因为上拉下拉操作控制在上一层，需要将上拉结果使用接口回馈
    public SmartRefreshListener smartRefreshListener;

    // WatchTvRepository
    private final WatchTvRepository watchTvRepository = new WatchTvRepository();

    // 渠道Id
    public long channelId;

    // 列表内容适配器
    public HomeWatchTvTvListAdapter homeWatchTvTvListAdapter;

    // 节目列表数据
    public List<WatchTvChannel> tvDataList = new ArrayList<>();

    // 当前页数
    private int pageNumber = 1;

    // 如果没有下一页数据，将此值置为true，禁用上拉操作
    private boolean noMoreData;

    public CheckThreadLiveData<Integer> holderLiveData =new CheckThreadLiveData<>();

    public HomeWatchTvTvListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshData() {
        if (isDataAllEmpty()) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        pageNumber = 1;
        watchTvRepository.getChannelColumnList(
                channelId,
                1,
                10
        ).subscribe(new BaseResponseObserver<ListWithPage<WatchTvChannel>>() {
            @Override
            protected void onRequestData(ListWithPage<WatchTvChannel> netData) {
                tvDataList = netData.getList();
                if (tvDataList == null) {
                    tvDataList = new ArrayList<>();
                }
                homeWatchTvTvListAdapter.updateDataSource(tvDataList);

                updatePlaceHolder();

                if (netData.getPage().hasNextPage()) {
                    pageNumber++;
                    noMoreData = false;
                } else {
                    noMoreData = true;
                }

            }

            @Override
            protected void onRequestError(Throwable throwable) {
                updatePlaceHolder();
            }
        });
    }

    public void loadMoreData() {
        if (isDataAllEmpty()) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        if (noMoreData) {
            updatePlaceHolder();
            if (smartRefreshListener != null) {
                smartRefreshListener.loadMoreSuccess();
            }
            showToast(R.string.all_nor_more_data);
            return;
        }

        watchTvRepository.getChannelColumnList(
                channelId,
                pageNumber,
                10
        ).subscribe(new BaseResponseObserver<ListWithPage<WatchTvChannel>>() {
            @Override
            protected void onRequestData(ListWithPage<WatchTvChannel> netData) {
                tvDataList = netData.getList();
                if (tvDataList == null) {
                    tvDataList = new ArrayList<>();
                }
                homeWatchTvTvListAdapter.addDataSource(tvDataList);

                updatePlaceHolder();

                if (netData.getPage().hasNextPage()) {
                    pageNumber++;
                    noMoreData = false;
                } else {
                    noMoreData = true;
                }

                if (smartRefreshListener != null) {
                    smartRefreshListener.loadMoreSuccess();
                }
            }

            @Override
            protected void onRequestError(Throwable throwable) {
                updatePlaceHolder();

                if (smartRefreshListener != null) {
                    smartRefreshListener.loadMoreFailed();
                }
            }
        });
    }

    private void updatePlaceHolder() {
        if (isDataAllEmpty()) {
            if (!NetworkUtils.isNetworkConnected()) {
                holderLiveData.setValue(NETWORK_NOT_AVAILABLE);
            } else {
                holderLiveData.setValue(EMPTY);
            }
        } else {
            holderLiveData.setValue(NONE);
        }
    }

    private boolean isDataAllEmpty() {
        return tvDataList == null || tvDataList.isEmpty();
    }

}