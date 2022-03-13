package com.jxntv.feed.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jxntv.base.Constant;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.base.sharedprefs.KernelSharedPrefs;
import com.jxntv.feed.frame.tab.TabItemDataManager;
import com.jxntv.base.model.feed.TabItemInfo;
import com.jxntv.feed.model.FeedResponse;
import com.jxntv.feed.request.FeedTabDataRequest;
import com.jxntv.feed.request.FeedTabDetailRequest;
import com.jxntv.feed.request.FeedTabRequest;
import com.jxntv.feed.request.GetRecommendContentListRequest;
import com.jxntv.feed.request.HotCircleRequest;
import com.jxntv.feed.request.HotTopicRequest;
import com.jxntv.feed.request.LoadMoreShortVideoRequest;
import com.jxntv.feed.request.RecommendBannerRequest;
import com.jxntv.media.MediaPageSource;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.network.schedule.GVideoSchedulers;

import io.reactivex.rxjava3.core.Observable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * feed仓库类
 */
public class FeedRepository extends BaseDataRepository {

    /**
     * TAG
     **/
    private static final String TAG = "FeedRepository";
    /**
     * 短视频详情页cursor
     */
    private Map<String, String> mLastDetailCursorMap = new HashMap<>();

    private volatile static FeedRepository singleInstance = null;

    private FeedRepository() {
    }

    public static FeedRepository getInstance() {
        if (singleInstance == null) {
            synchronized (FeedRepository.class) {
                if (singleInstance == null) {
                    singleInstance = new FeedRepository();
                }
            }
        }
        return singleInstance;
    }

    /**
     * 获取tab信息
     *
     * @param mediaType 媒体类型
     * @return 获取的tab信息
     */
    @NonNull
    public Observable<List<TabItemInfo>> getTabsInfo(@Constant.TabMediaType int mediaType) {
        return new NetworkData<List<TabItemInfo>>(mEngine) {

            @Override
            protected BaseRequest<List<TabItemInfo>> createRequest() {
                return new FeedTabRequest(mediaType);
            }

            @Override
            protected void saveData(List<TabItemInfo> infoList) {
                TabItemDataManager.getInstance().saveTabs(infoList, mediaType);
            }
        }.asObservable(GVideoSchedulers.IO_PRIORITY_USER);
    }

    /**
     * 获取tab数据
     *
     * @param tabId       tabId
     * @param handleStick 是否处理置顶逻辑，true时将返回数据中置顶数据移动到首位，
     *                    false时忽略置顶数据；约定首页加载时处理置顶数据，加载更多时忽略置顶数据；
     * @param isInitial   是否为初始化，
     * @return
     */
    @NonNull
    public Observable<FeedResponse> loadTabData(String tabId, final boolean handleStick, boolean isInitial) {
        KernelSharedPrefs kernelSharedPrefs = KernelSharedPrefs.getInstance();
        // 初始化需要清空cursor
        if (isInitial) {
            kernelSharedPrefs.putString(tabId, null);
        }
        String cursor = kernelSharedPrefs.getString(tabId, "");
        return new NetworkData<FeedResponse>(mEngine) {

            @Override
            protected BaseRequest<FeedResponse> createRequest() {
                return new FeedTabDataRequest(tabId, cursor);
            }

            @Override
            protected void saveData(FeedResponse response) {
                handleCheckMediaModel(tabId, response, handleStick);
            }
        }.asObservable(GVideoSchedulers.IO_PRIORITY_NORMAL);
    }

    /**
     * 检查feed数据模型
     *
     * @param tabId       tabId
     * @param response    返回数据
     * @param handleStick 是否处理置顶逻辑，true时将返回数据中置顶数据移动到首位，false时忽略置顶数据；约定首页加载时处理置顶数据，加载更多时忽略置顶数据；
     */
    private void handleCheckMediaModel(String tabId, FeedResponse response, final boolean handleStick) {
        if (response == null) {
            return;
        }
        List<MediaModel> MediaModels = response.list;
        String cursor = response.cursor;
        KernelSharedPrefs.getInstance().putString(tabId, cursor);

        Iterator<MediaModel> it = MediaModels.iterator();
        MediaModel model;
        boolean hasFoundStick = false;
        while (it.hasNext()) {
            model = it.next();
            if (model == null || !model.isValid()) {
                it.remove();
                continue;
            }
            if (MediaType.MediaTypeCheck.isNeedCheckData(model.getMediaType())) {
                model.updateInteract();
            }
            if (handleStick && !hasFoundStick) {
                if (model.isStick()) {
                    hasFoundStick = true;
                    response.stickFeedModel = model;
                    it.remove();
                }
            } else {
                model.setIsStick(false);
            }
            model.tabId = tabId;
            if (tabId.equals(TabItemDataManager.TAB_ID_NEWS)) {
                model.showMediaPageSource = MediaPageSource.PageSource.NEWS;
            }
            if (model.getPendant() != null) {
                model.getPendant().contentId = model.getId();
                model.getPendant().extendType = TextUtils.isEmpty(model.getPendant().actUrl) ? 0 : 1;
            }
            if (model.feedRecommendModelList != null && !model.feedRecommendModelList.isEmpty()) {
                for (RecommendModel recommendModel : model.feedRecommendModelList) {
                    recommendModel.contentId = model.getId();
                    recommendModel.extendType = TextUtils.isEmpty(recommendModel.actUrl) ? 0 : 1;
                }
            }

        }
        response.hasStick = hasFoundStick;
    }

    @NonNull
    public Observable<FeedResponse> loadTabDetail(boolean refresh, String tabId) {
        String cursor;
        if (refresh) {
            mLastDetailCursorMap.remove(tabId);
            cursor = null;
        } else {
            cursor = mLastDetailCursorMap.get(tabId);
        }

        return new NetworkData<FeedResponse>(mEngine) {

            @Override
            protected BaseRequest<FeedResponse> createRequest() {
                return new FeedTabDetailRequest(tabId, cursor);
            }

            @Override
            protected void saveData(FeedResponse response) {
                if (response != null) {
                    mLastDetailCursorMap.put(tabId, response.cursor);
                }
            }
        }.asObservable();
    }

    public Observable<FeedResponse> loadMoreShortVideo(
            String mediaId,
            int pageNumber,
            int pageSize
    ) {
        return new NetworkData<FeedResponse>(mEngine) {

            @Override
            protected BaseRequest<FeedResponse> createRequest() {
                return new LoadMoreShortVideoRequest(mediaId,pageNumber,pageSize);
            }

            @Override
            protected void saveData(FeedResponse response) {
            }
        }.asObservable();
    }


    @NonNull
    public Observable<BannerModel> getBannerList(int locationId) {
        return new NetworkData<BannerModel>(mEngine) {
            @Override
            protected BaseRequest<BannerModel> createRequest() {
                RecommendBannerRequest request = new RecommendBannerRequest();
                request.setScene(locationId > 0 ? 1 : 0);
                request.setLocationId(locationId);
                return request;
            }

            @Override
            protected void saveData(BannerModel model) {
            }
        }.asObservable();
    }

    @NonNull
    public Observable<BannerModel> getBannerList(int locationId,int scene) {
        return new NetworkData<BannerModel>(mEngine) {
            @Override
            protected BaseRequest<BannerModel> createRequest() {
                RecommendBannerRequest request = new RecommendBannerRequest();
                request.setScene(scene);
                request.setLocationId(locationId);
                return request;
            }

            @Override
            protected void saveData(BannerModel model) {
            }
        }.asObservable();
    }

    /**
     * 获取动态列表
     *
     * @param pageNumber 分页编号
     * @param pageSize   分页大小
     */
    @NonNull
    public Observable<ListWithPage<MediaModel>> getRecommendContentList(
            int pageNumber,
            int pageSize,
            boolean isRefresh
    ) {
        return new NetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                GetRecommendContentListRequest request = new GetRecommendContentListRequest();
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                request.setIsRefresh(isRefresh);
                request.setNeedComment(true);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> mediaListWithPage) {
                Iterator<MediaModel> it = mediaListWithPage.getList().iterator();
                while (it.hasNext()) {
                    MediaModel model = it.next();
                    if (model == null || !model.isValid()) {
                        it.remove();
                        continue;
                    }
                    model.updateInteract();
                }

            }
        }.asObservable();
    }

    /**
     * 获取动态列表
     *
     * @param pageNumber 分页编号
     * @param pageSize   分页大小
     */
    @NonNull
    public Observable<ListWithPage<Circle>> getHotCircleList(int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<Circle>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<Circle>> createRequest() {
                HotCircleRequest request = new HotCircleRequest();
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<Circle> listWithPage) {
            }
        }.asObservable();
    }

    /**
     * 获取动态列表
     *
     * @param pageNumber 分页编号
     * @param pageSize   分页大小
     */
    @NonNull
    public Observable<ListWithPage<TopicDetail>> getHotTopicList(int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<TopicDetail>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<TopicDetail>> createRequest() {
                HotTopicRequest request = new HotTopicRequest();
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<TopicDetail> listWithPage) {
            }
        }.asObservable();
    }
}
