package com.hzlz.aviation.feature.live.ui.homelive.homelive;

import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.EMPTY;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.LOADING;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NETWORK_NOT_AVAILABLE;
import static com.hzlz.aviation.kernel.base.placeholder.PlaceholderType.NONE;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.live.adapter.PlayingLiveAdapter;
import com.hzlz.aviation.feature.live.adapter.PreviewListAdapter;
import com.hzlz.aviation.feature.live.repository.LiveRepository;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;


public class HomeLiveContentViewModel extends BaseRefreshLoadMoreViewModel {

    // LiveRepository
    private final LiveRepository liveRepository;

    // 正在播放的直播列表适配器
    public PlayingLiveAdapter playingLiveAdapter;

    // 直播预告列表适配器
    public PreviewListAdapter previewListAdapter;

    // 正在播放的直播列表数据
    public List<MediaModel> playLiveList;

    // 通知View层更新正在播放的直播列表
    public CheckThreadLiveData<Boolean> loadPlayingData = new CheckThreadLiveData<>();

    // 更新“直播回顾”标题的显示与隐藏
    public CheckThreadLiveData<Boolean> updateLiveReviewTitle = new CheckThreadLiveData<>();

    public HomeLiveContentViewModel(@NonNull Application application) {
        super(application);
        previewListAdapter = new PreviewListAdapter();
        liveRepository = new LiveRepository();
        playLiveList = new ArrayList<>();
    }

    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return previewListAdapter;
    }

    public void onLoadMore(IGVideoRefreshLoadMoreView refreshLayout) {
        showLoadingLayout();
        getLiveReviewList(refreshLayout, false);
    }

    public void onRefresh(IGVideoRefreshLoadMoreView refreshLayout) {
        showLoadingLayout();
        liveRepository.getPlayingLiveList()
                .subscribe(new BaseGVideoResponseObserver<List<MediaModel>>() {

                    @Override
                    protected void onRequestData(List<MediaModel> netData) {
                        super.onRequestData(netData);
                        playLiveList = netData;
                        loadPlayingData.setValue(true);
                        getLiveReviewList(refreshLayout, true);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        super.onRequestError(throwable);
                        getLiveReviewList(refreshLayout, true);
                    }

                });
    }

    private void showLoadingLayout(){
        List<MediaModel> adapterData = previewListAdapter.getData();
        if ((adapterData == null || adapterData.isEmpty())
                && (playLiveList == null || playLiveList.isEmpty())) {
            updatePlaceholderLayoutType(LOADING);
        }
    }

    private void getLiveReviewList(IGVideoRefreshLoadMoreView refreshLayout, boolean isRefresh) {
        if (isRefresh) {
            liveRepository.getLiveReviewList(1, mLocalPage.getPageSize())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GVideoRefreshObserver<MediaModel>(refreshLayout) {
                        @Override
                        protected void onSuccess(@Nullable List<MediaModel> dataList) {
                            super.onSuccess(dataList);
                            updateHolder();
                        }

                        @Override
                        protected void onFailed(@NonNull Throwable throwable) {
                            super.onFailed(throwable);
                            updateHolder();
                        }
                    });
        } else {
            liveRepository.getLiveReviewList(mLocalPage.getPageNumber(), mLocalPage.getPageSize())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GVideoLoadMoreObserver<MediaModel>(refreshLayout) {
                        @Override
                        protected void onSuccess(@Nullable List<MediaModel> dataList) {
                            super.onSuccess(dataList);
                            updateHolder();
                        }

                        @Override
                        protected void onFailed(@NonNull Throwable throwable) {
                            super.onFailed(throwable);
                            updateHolder();
                        }
                    });
        }
    }

    public void follow(final AuthorModel author) {
        PluginManager.get(AccountPlugin.class).getFollowRepository().followAuthor(
                author.getId(),
                author.getType(),
                author.getName(),
                !author.isFollow()
        ).subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
            @Override
            protected void onRequestData(@NonNull Object result) {
                playingLiveAdapter.updateSingleOnlyData(author, (Boolean) result);
            }

            @Override
            protected void onRequestError(Throwable throwable) {
                showToast(throwable.getMessage());
                GVideoSensorDataManager.getInstance().followAccount(!author.isFollow(), author.getId(),
                        author.getName(), author.getType(), throwable.getMessage());
            }
        });
    }

    private void updateHolder() {
        List<MediaModel> adapterData = previewListAdapter.getData();
        if ((adapterData == null || adapterData.isEmpty())
                && (playLiveList == null || playLiveList.isEmpty())) {
            if (!NetworkUtils.isNetworkConnected()) {
                updatePlaceholderLayoutType(NETWORK_NOT_AVAILABLE);
            } else {
                updatePlaceholderLayoutType(EMPTY);
            }
        } else {
            updatePlaceholderLayoutType(NONE);
        }
        updateLiveReviewTitle.setValue(adapterData != null && !adapterData.isEmpty());
    }

}