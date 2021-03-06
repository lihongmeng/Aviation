package com.hzlz.aviation.feature.video.repository;


import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.video.api.VideoAPI;
import com.hzlz.aviation.feature.video.model.LiveCommentModel;
import com.hzlz.aviation.feature.video.model.LiveDetailModel;
import com.hzlz.aviation.feature.video.request.AtyLiveCommentRequest;
import com.hzlz.aviation.feature.video.request.AtyLiveDetailRequest;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc :
 */
public class LiveAtyRepository extends BaseDataRepository {

    private long requestCommentTime = 0;

    public Observable<LiveDetailModel> loadLiveDetail(String mediaId) {

        return new OneTimeNetworkData<LiveDetailModel>(mEngine) {
            @Override
            protected BaseRequest<LiveDetailModel> createRequest() {
                AtyLiveDetailRequest request = new AtyLiveDetailRequest();
                request.setMediaId(mediaId);
                return request;
            }

            @Override
            protected void saveData(LiveDetailModel liveDetailModel) {
                super.saveData(liveDetailModel);

            }
        }.asObservable();
    }


    /**
     * 获取直播评论
     */
    public Observable<LiveCommentModel> loadCommentData(String mediaId,String requestCommentTime) {

        return new OneTimeNetworkData<LiveCommentModel>(mEngine) {
            @Override
            protected BaseRequest<LiveCommentModel> createRequest() {
                AtyLiveCommentRequest liveCommentRequest = new AtyLiveCommentRequest();
                liveCommentRequest.setParameters(mediaId,requestCommentTime);
                return liveCommentRequest;
            }

            @Override
            protected void saveData(LiveCommentModel commentModel) {
                super.saveData(commentModel);
            }
        }.asObservable();
    }

    /**
     * 评论
     */
    public Observable<Object> commentLive(String mediaId,String comment) {

        HashMap<String,Object> parameters = new HashMap<>();
        parameters.put("mediaId",mediaId);
        parameters.put("msg",comment);

        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return VideoAPI.Instance.get().commentAtyLive(parameters);
                    }
                };
            }
        }.asObservable();
    }

    /**
     * 点赞直播
     */
    public Observable<Object> likeLive(String mediaId) {

        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return VideoAPI.Instance.get().likeAtyLive(mediaId);
                    }
                };
            }
        }.asObservable();
    }

}
