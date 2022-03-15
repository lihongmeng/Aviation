package com.hzlz.aviation.feature.watchtv.ui;

import static com.hzlz.aviation.kernel.base.Constant.WATCH_TV_CHANNEL_TYPE.BUILD_SELF_OTHER;
import static com.hzlz.aviation.kernel.base.Constant.WATCH_TV_CHANNEL_TYPE.TV;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.ERROR;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NETWORK_NOT_AVAILABLE;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NONE;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.watchtv.WatchTvRepository;
import com.hzlz.aviation.feature.watchtv.adapter.HomeWatchTvHotTvAdapter;
import com.hzlz.aviation.feature.watchtv.adapter.HomeWatchTvTvChannelAdapter;
import com.hzlz.aviation.feature.watchtv.adapter.HomeWatchTvTvChannelListTabAdapter;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.adapter.BaseFragmentVpAdapter;
import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.base.model.video.WatchTvChannel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class HomeWatchTvViewModel extends BaseViewModel {

    // WatchTvRepository
    private final WatchTvRepository watchTvRepository = new WatchTvRepository();

    // 电视频道横向列表的适配器
    public HomeWatchTvTvChannelAdapter tvChannelAdapter;

    // 热播节目横向列表的适配器
    public HomeWatchTvHotTvAdapter tvHotTvAdapter;

    // 电视频道横向列表的适配器(下方纯文字的tab)
    public HomeWatchTvTvChannelListTabAdapter tvChannelListTabAdapter;

    // 标签下ViewPager的adapter
    public BaseFragmentVpAdapter channelTvAdapter;

    // 频道列表数据（频道横向列表）
    public List<WatchTvChannel> watchTvChannelList = new ArrayList<>();

    // 频道栏目数据（下方的全部频道文字tab列表）
    public List<WatchTvChannel> watchTvChannelTabList = new ArrayList<>();

    // 热播节目数据
    public List<WatchTvChannel> hotTvList = new ArrayList<>();

    // 通知观测者需要更新Banner
    public final CheckThreadLiveData<BannerModel> bannerList = new CheckThreadLiveData<>();

    // 通知观测者需要更新横向频道列表
    public final CheckThreadLiveData<Boolean> updateChannelList = new CheckThreadLiveData<>();
    public final CheckThreadLiveData<Boolean> updateChannelTabList = new CheckThreadLiveData<>();

    // 通知观测者需要更新热播节目列表
    public final CheckThreadLiveData<Boolean> updateHotTvList = new CheckThreadLiveData<>();

    // 通知观察者，控制ViewPager子元素加载下一页的数据
    // 因为界面结构不宜嵌套过多的SmartRefreshLayout导致滑动冲突
    // 所以只考虑一个上拉和下拉操作，都由本fragment进行分发
    // 下拉操作用于刷新页面整体数据，Viewpager中的FragmentList直接重建
    // 上拉操作用于ViewPager中的当前的Fragment loadMore，加载结果由回调返回
    public final CheckThreadLiveData<Boolean> currentLoadMode = new CheckThreadLiveData<>();

    public HomeWatchTvViewModel(@NonNull Application application) {
        super(application);
    }

    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        if (isDataAllEmpty()) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        initBannerData(view);
    }

    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        if (isDataAllEmpty()) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        currentLoadMode.setValue(true);
    }

    private void updatePlaceHolder() {
        updatePlaceholderLayoutType(NONE);

        if (isDataAllEmpty()) {
            if (!NetworkUtils.isNetworkConnected()) {
                updatePlaceholderLayoutType(NETWORK_NOT_AVAILABLE);
            } else {
                updatePlaceholderLayoutType(ERROR);
            }
        }
    }

    private boolean isDataAllEmpty() {
        return (watchTvChannelList == null || watchTvChannelList.isEmpty())
                && (watchTvChannelTabList == null || watchTvChannelTabList.isEmpty())
                && (hotTvList == null || hotTvList.isEmpty())
                && !BannerModel.isDataValid(bannerList.getValue());
    }

    private void initBannerData(IGVideoRefreshLoadMoreView view) {
        PluginManager.get(FeedPlugin.class).getBannerList(
                Constant.BANNER_LOCATION_ID.WATCH_TV,
                Constant.BANNER_SCENE.OPERATION
        ).subscribe(new GVideoResponseObserver<BannerModel>() {
            @Override
            protected void onSuccess(@NonNull BannerModel model) {
                bannerList.setValue(model);
                initChannelData(view);
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                super.onFailed(throwable);
                LogUtils.e(throwable.getMessage());
                bannerList.setValue(null);
                updatePlaceHolder();
                view.finishGVideoRefresh();
            }
        });
    }

    private void initChannelData(IGVideoRefreshLoadMoreView view) {
        watchTvRepository.getChannelList(
                TV + "",
                1,
                100
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseResponseObserver<ListWithPage<WatchTvChannel>>() {
                               @Override
                               protected void onRequestData(ListWithPage<WatchTvChannel> netData) {
                                   watchTvChannelList = netData.getList();
                                   if (watchTvChannelList == null) {
                                       watchTvChannelList = new ArrayList<>();
                                   }
                                   updateChannelList.setValue(true);

                                   watchTvRepository.getChannelList(
                                           TV + "," + BUILD_SELF_OTHER,
                                           1,
                                           100
                                   ).observeOn(AndroidSchedulers.mainThread())
                                           .subscribe(new BaseResponseObserver<ListWithPage<WatchTvChannel>>() {
                                                          @Override
                                                          protected void onRequestData(ListWithPage<WatchTvChannel> netData) {
                                                              watchTvChannelTabList = netData.getList();
                                                              if (watchTvChannelTabList == null) {
                                                                  watchTvChannelTabList = new ArrayList<>();
                                                              }
                                                              updateChannelTabList.setValue(true);
                                                              initHotTvData(view);
                                                          }

                                                          @Override
                                                          protected void onRequestError(Throwable throwable) {
                                                              watchTvChannelTabList.clear();
                                                              updateChannelTabList.setValue(true);
                                                              updatePlaceHolder();
                                                              view.finishGVideoRefresh();
                                                          }
                                                      }
                                           );

                               }

                               @Override
                               protected void onRequestError(Throwable throwable) {
                                   watchTvChannelList.clear();
                                   updateChannelList.setValue(true);
                                   updatePlaceHolder();
                                   view.finishGVideoRefresh();
                               }
                           }
                );
    }

    private void initHotTvData(IGVideoRefreshLoadMoreView view) {
        watchTvRepository.getHotTvList(
                1,
                100
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseResponseObserver<ListWithPage<WatchTvChannel>>() {
                               @Override
                               protected void onRequestData(ListWithPage<WatchTvChannel> netData) {
                                   view.finishGVideoRefresh();
                                   hotTvList = netData.getList();
                                   if (hotTvList == null) {
                                       hotTvList = new ArrayList<>();
                                   }
                                   updateHotTvList.setValue(true);
                                   updatePlaceHolder();
                               }

                               @Override
                               protected void onRequestError(Throwable throwable) {
                                   view.finishGVideoRefresh();
                                   hotTvList.clear();
                                   updateHotTvList.setValue(true);
                                   updatePlaceHolder();
                               }
                           }
                );
    }

}