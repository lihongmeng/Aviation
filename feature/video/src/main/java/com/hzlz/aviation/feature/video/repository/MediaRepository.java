package com.hzlz.aviation.feature.video.repository;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.video.request.NewsListRequest;
import com.hzlz.aviation.feature.video.request.VideoBaseRequest;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.MediaRepositoryInterface;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Response;

public class MediaRepository extends BaseDataRepository implements MediaRepositoryInterface {

    public Observable<VideoModel> loadMedia(String mediaId) {
        return new OneTimeNetworkData<VideoModel>(mEngine) {
            @Override
            protected BaseRequest<VideoModel> createRequest() {
                return new VideoBaseRequest<VideoModel>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return getVideoAPI().loadMedia(mediaId);
                    }
                };
            }

            @Override
            protected void saveData(VideoModel videoModel) {
                if (videoModel == null) {
                    return;
                }
                videoModel.updateInteract();
            }
        }.asObservable();
    }

    public Observable<ShortVideoListModel> loadShortMedia(String mediaId) {
        return loadMedia(mediaId).map(new Function<VideoModel, ShortVideoListModel>() {
            @Override
            public ShortVideoListModel apply(VideoModel videoModel) throws Exception {
                List<VideoModel> videos = new ArrayList<>();
                videos.add(videoModel);
                ShortVideoListModel fm = ShortVideoListModel.Builder.aFeedModel()
                        .withLoadMore(false)
                        .withCursor("")
                        .withList(videos)
                        .build();
                return fm;
            }
        });
    }


    /**
     * 获取内容列表
     *
     * @param isSpecial 是否是专题
     * @return
     */
    public Observable<ListWithPage<MediaModel>> loadNewsList(String id, int pageNum, boolean isSpecial) {
        return new OneTimeNetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                NewsListRequest request = new NewsListRequest();
                request.setParameters(id, pageNum);
                request.setSpecial(isSpecial);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> mediaListWithPage) {
            }
        }.asObservable();
    }

}
