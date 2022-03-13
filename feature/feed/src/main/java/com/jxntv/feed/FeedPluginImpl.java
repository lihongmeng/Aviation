package com.jxntv.feed;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.feed.TabItemInfo;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.FeedPlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.feed.model.FeedResponse;
import com.jxntv.feed.repository.FeedRepository;
import com.jxntv.media.MediaRuntime;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.player.MediaPlayManager;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.network.observer.BaseResponseObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * Feed模块接口实现类
 */
public class FeedPluginImpl implements FeedPlugin {

    public int unreadCount;

    @NonNull
    @Override
    public BaseFragment getFeedFragment(String packageName) {
        return new FeedFragment(packageName);
    }

    @NonNull
    @Override
    public BaseFragment getRadioFragment() {
        return new RadioFragment();
    }

    @NonNull
    @Override
    public BaseFragment getVideoFragment() {
        return new VideoFragment();
    }

    @Override
    public void onSplashEnd() {
        MediaPlayManager.getInstance().onSplashEnd();
    }

    @Override
    public void onHomeRelease() {
        MediaRuntime.release();
        FeedFragmentManager.release();
    }

    @Override
    public Observable loadMoreFeedData(boolean refresh, Bundle data) {
        if (data == null) {
            return null;
        }
        String fragmentId = data.getString(KEY_FRAGMENT_UUID);
        if (TextUtils.isEmpty(fragmentId)) {
            return null;
        }
        BaseFragment fragment = FeedFragmentManager.getInstance().getFragment(fragmentId);
        if (fragment instanceof MediaPageFragment) {
            MediaPageFragment pageFragment = (MediaPageFragment) fragment;
            return pageFragment.loadMoreShortData(refresh);
        } else {
            String tabId = data.getString(VideoPlugin.EXTRA_FROM_CHANNEL_ID);
            return loadMoreFeedData(refresh, tabId);
        }
    }

    @Override
    public Observable<ShortVideoListModel> loadMoreFeedData(boolean refresh, String tabId) {

//        if(TextUtils.isEmpty(tabId)){
//            return null;
//        }
        return Observable.create(e -> FeedRepository.getInstance().loadTabDetail(refresh, "")
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(new BaseResponseObserver<FeedResponse>() {
                    @Override
                    protected void onRequestData(FeedResponse response) {
                        List<MediaModel> feedModelList = response.list;
                        List<VideoModel> shortList = createVideoList(feedModelList);
                        e.onNext(ShortVideoListModel.Builder.aFeedModel().withList(shortList).withLoadMore(true).build());
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        e.onError(throwable);
                    }
                }));
    }

    /**
     * 获取banner数据
     *
     * @param locationId 内容id  -2 首页推荐   -1 新闻首页   圈子id
     */
    @Override
    public Observable<BannerModel> getBannerList(int locationId) {
        return FeedRepository.getInstance().getBannerList(locationId);
    }

    /**
     * 获取banner数据
     *
     * @param locationId 内容id  -2 首页推荐   -1 新闻首页   圈子id
     * @param scene      Banner位场景
     * @return Observable<BannerModel>
     */
    @Override
    public Observable<BannerModel> getBannerList(int locationId, int scene) {
        return FeedRepository.getInstance().getBannerList(locationId,scene);
    }

    /**
     * 创建对应的 short video List
     *
     * @param data 下拉刷新数据
     * @return 完成处理的 short video list
     */
    private List<VideoModel> createVideoList(@NonNull List<MediaModel> data) {
        List<VideoModel> videoModels = new ArrayList<>();
        VideoModel videoModel;
        for (MediaModel model : data) {
            if (model == null) {
                continue;
            }
            if (!model.isShortMedia()) {
                continue;
            }
            videoModel = model; //FeedUtils.buildVideoModel(model);
            if (videoModel == null) {
                continue;
            }
            videoModels.add(videoModel);
            model.correspondVideoModelAddress = videoModel.getMemoryAddress();
        }
        return videoModels;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    @Override
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public Observable<List<TabItemInfo>> getTabItemInfoList(int mediaType) {
        return FeedRepository.getInstance().getTabsInfo(mediaType);
    }

    @Override
    public Observable<ShortVideoListModel> loadMoreShortVideo(String mediaId, int pageNumber, int pageSize) {
        return Observable.create(e -> FeedRepository.getInstance().loadMoreShortVideo(mediaId,pageNumber,pageSize)
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(new BaseResponseObserver<FeedResponse>() {
                    @Override
                    protected void onRequestData(FeedResponse response) {
                        List<MediaModel> feedModelList = response.list;
                        List<VideoModel> shortList = createVideoList(feedModelList);
                        e.onNext(ShortVideoListModel.Builder.aFeedModel().withList(shortList).withLoadMore(true).build());
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        e.onError(throwable);
                    }
                }));
    }
}
