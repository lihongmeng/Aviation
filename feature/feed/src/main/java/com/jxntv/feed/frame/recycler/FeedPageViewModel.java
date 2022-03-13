package com.jxntv.feed.frame.recycler;

import static com.jxntv.async.GlobalExecutor.PRIORITY_USER;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jxntv.async.GlobalExecutor;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.feed.R;
import com.jxntv.feed.frame.tab.TabItemDataManager;
import com.jxntv.feed.model.FeedResponse;
import com.jxntv.feed.repository.FeedRepository;
import com.jxntv.feed.repository.FeedShortVideoRepository;
import com.jxntv.feed.utils.FeedConstants;
import com.jxntv.media.MediaPageSource;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageViewModel;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.Observable;

/**
 * feed  page页面数据模型
 */
public class FeedPageViewModel extends MediaPageViewModel {

    /**
     * 最长等待时间
     */
    private static final int LOAD_DATA_TIME_LIMIT = 10;

    /**
     * 持有的feed仓库类
     */
    private final FeedRepository feedRepository = FeedRepository.getInstance();

    /**
     * 标记adapter完成刷新
     */
    private boolean mIsAdapterDone = false;

    /**
     * 标记短视频完成刷新
     */
    private boolean mIsShortUpdateDone = false;

    /**
     * 持有的handler
     */
    private final Handler handler = new Handler(Looper.getMainLooper());

    public FeedPageViewModel(@NonNull Application application) {
        super(application);
    }

    public ShortVideoListModel createShortListModel(MediaModel mediaModel) {
        List<VideoModel> list = new ArrayList<>();
        list.add(mediaModel);
        return ShortVideoListModel.Builder.aFeedModel()
                .withList(list)
                /*.withCursor(String.valueOf(cursor))*/
                .withLoadMore(false).build();
    }

    /**
     * 加载更多短视频/音频数据,用于外部调用，无ui相关处理
     *
     * @param refresh 进入详情页首次加载数据时会清空掉历史cursor
     * @return 对应的observable
     */
    public Observable<ShortVideoListModel> loadMoreShortData(boolean refresh) {
        return Observable.create(
                e -> feedRepository.loadTabDetail(refresh, mTabId)
                        .timeout(LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                        .subscribe(new GVideoResponseObserver<FeedResponse>() {
                                       @Override
                                       protected void onSuccess(@NonNull FeedResponse feedResponse) {
                                           e.onNext(handleFeedResponse(feedResponse));
                                       }

                                       @Override
                                       public void onFailed(Throwable throwable) {
                                           e.onError(throwable);
                                       }
                                   }
                        )
        );
    }

    /**
     * 处理请求数据
     *
     * @param response 对应的feed页response
     * @return 生成的短视频列表数据模型
     */
    private ShortVideoListModel handleFeedResponse(FeedResponse response) {
        List<MediaModel> feedModelList = response.list;
        List<VideoModel> shortList = createVideoList(feedModelList);
        //FeedShortVideoRepository.getInstance().updateShortVideoList(mTabId, shortList,true);
        //mAdapter.loadMoreData(feedModelList);
        return ShortVideoListModel.Builder.aFeedModel().withList(shortList).withLoadMore(true).build();
    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {

        return new IRecyclerModel<MediaModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<MediaModel> loadListener) {
                //新闻刷新需要清除数据
                handleLoadData(loadListener, page == 1 && (mTabId.equals(TabItemDataManager.TAB_ID_NEWS)));
            }

            @Override
            public void initialData(RecyclerViewLoadListener<MediaModel> loadListener) {
                mAdapter.clearList();
                handleLoadData(loadListener, !mTabId.equals(TabItemDataManager.TAB_ID_VIDEO_AUDIO));
            }
        };
    }

    /**
     * 处理加载数据
     *
     * @param loadListener 加载监听，用于回调加载结果
     * @param isInitial    是否是初始化
     */
    private void handleLoadData(RecyclerViewLoadListener<MediaModel> loadListener, boolean isInitial) {

        // 约定首页加载时处理置顶数据，加载更多时忽略置顶数据；
        boolean handleStick = loadType != LOAD_DATA_TYPE_LOAD_MORE;
        feedRepository.loadTabData(mTabId, handleStick, isInitial)
                .timeout(LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new GVideoResponseObserver<FeedResponse>() {
                    @Override
                    protected void onRequestStart() {
                        mIsAdapterDone = false;
                        mIsShortUpdateDone = false;
                        if (mMediaModelList.isEmpty()) {
                            updatePlaceholderLayoutType(PlaceholderType.LOADING);
                        }
                    }

                    @Override
                    protected void onSuccess(@NonNull FeedResponse response) {
                        if (isInitial) {
                            mAdapter.clearList();
                        }
                        if (response.list == null) {
                            onFailed(new Throwable("response list empty"));
                            return;
                        }
                        if (response.hasStick && response.stickFeedModel != null) {
                            loadSuccessWithStick(response.list, response.stickFeedModel, isInitial);
                            loadComplete();
                            return;
                        }

                        int listSize = response.list.size();
                        if (listSize == 0) {
                            if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                                showToast(R.string.all_nor_more_data);
                            }
                            loadComplete();
                            return;
                        }
                        MediaModel mediaModel = response.list.get(0);
                        if (isInitial && TextUtils.equals(mTabId, TabItemDataManager.TAB_ID_VIDEO_AUDIO)) {
                            if (mediaModel.feedRecommendModelList != null
                                    && mediaModel.feedRecommendModelList.size() > 0) {
                                mediaModel.showMediaPageSource = MediaPageSource.PageSource.AUDIOVISUAL;
                            }
                        }
                        updateMediaDataOneByOne(response.list);
                        loadSuccess(response.list);
                        loadComplete();
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (throwable instanceof TimeoutException ||
                                throwable instanceof SocketTimeoutException) {
                            showToast(com.jxntv.media.R.string.all_network_not_available);
                            loadComplete();
                            return;
                        }
                        showToast(com.jxntv.media.R.string.all_nor_more_data);
                        loadComplete();
                    }
                });
    }

    private void updateMediaDataOneByOne(List<MediaModel> mediaModelList) {
        if (mediaModelList == null || mediaModelList.isEmpty()) {
            return;
        }
        for (MediaModel media : mediaModelList) {
            if (media == null || !media.isValid()) {
                continue;
            }
            media.setPid(getPid());
            media.tabName = tabName;
        }
    }

    @Override
    public void loadSuccess(List<MediaModel> list) {
        loadSuccessWithStick(list, null, false);
    }

    private void loadSuccessWithStick(List<MediaModel> list, MediaModel model, boolean isInitial) {
        GlobalExecutor.execute(() -> {
            /*
             * boolean isAdd = false;
             * if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
             *      isAdd = true;
             * }
             * FeedShortVideoRepository.getInstance().updateShortVideoList(mTabId, createVideoList(list), isAdd);
             */
            mIsShortUpdateDone = true;
            checkIsComplete();
        }, "feed_update", PRIORITY_USER);

        // 将指定的一条数据置顶
        if (model != null) {
            list.add(0, model);
        }

        MediaModel firstMediaModel = list.get(0);
        if (isInitial && firstMediaModel != null
                && TextUtils.equals(mTabId, TabItemDataManager.TAB_ID_VIDEO_AUDIO)
                && list.size() > 0
                && firstMediaModel.feedRecommendModelList != null
                && firstMediaModel.feedRecommendModelList.size() > 0) {
            //视听，强制修改第一个数据的展示类型
            firstMediaModel.showMediaPageSource = MediaPageSource.PageSource.AUDIOVISUAL;
        }

        updateMediaDataOneByOne(list);
        super.loadSuccess(list);
        mIsAdapterDone = true;
        checkIsComplete();
    }

    /**
     * 检查是否可以完成
     */
    private void checkIsComplete() {
        if (mIsAdapterDone && mIsShortUpdateDone) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    loadComplete();
                    mIsShortUpdateDone = false;
                    mIsAdapterDone = false;
                }
            });

        }
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

    private void updateShortList(String mediaId, @FeedConstants.UpdateType int type, int deltaCount) {
        //同时更新短视频缓存数据，以保证再次进入短视频详情时数据正确
        List<VideoModel> shortList = FeedShortVideoRepository.getInstance().getShortVideoList(mTabId);
        if (shortList != null) {
            Map<Integer, VideoModel> map = new HashMap<>();
            int shortSize = shortList.size();
            for (int i = 0; i < shortSize; i++) {
                VideoModel model = shortList.get(i);
                if (model != null) {
                    if (type == FeedConstants.UPDATE_TYPE_COMMENT || type == FeedConstants.UPDATE_TYPE_FAVORITE) {
                        if (TextUtils.equals(model.getId(), mediaId)) {
                            map.put(i, model);
                        }
                    } else if (type == FeedConstants.UPDATE_TYPE_FOLLOW) {
                        if (TextUtils.equals(model.getAuthor().getId(), mediaId)) {
                            map.put(i, model);
                        }
                    }
                }
            }
            for (Map.Entry<Integer, VideoModel> entry : map.entrySet()) {
                int index = entry.getKey();
                VideoModel oldModel = entry.getValue();
                VideoModel newModel;
                if (type == FeedConstants.UPDATE_TYPE_COMMENT) {
                    int reviews = oldModel.getReviews();
                    reviews += deltaCount;
                    newModel = VideoModel.Builder.aVideoModel().fromVideoModel(oldModel)
                            .withReviews(reviews).build();
                } else if (type == FeedConstants.UPDATE_TYPE_FOLLOW) {
                    newModel = VideoModel.Builder.aVideoModel().fromVideoModel(oldModel)
                            .withIsFavor(deltaCount).build();
                } else if (type == FeedConstants.UPDATE_TYPE_FAVORITE) {
                    AuthorModel author = AuthorModel.Builder.anAuthorModel()
                            .fromAuthor(oldModel.getAuthor())
                            .withIsFollow(deltaCount > 0)
                            .build();
                    newModel = VideoModel.Builder.aVideoModel().fromVideoModel(oldModel)
                            .withAuthor(author).build();
                } else {
                    break;
                }

                shortList.remove(index);
                shortList.add(index, newModel);
            }
            map.clear();
            FeedShortVideoRepository.getInstance().updateShortVideoList(mTabId, shortList, false);
        }
    }

}
