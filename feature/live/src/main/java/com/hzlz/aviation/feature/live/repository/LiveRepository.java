package com.hzlz.aviation.feature.live.repository;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.live.api.LiveApi;
import com.hzlz.aviation.feature.live.model.LiveDetailModel;
import com.hzlz.aviation.feature.live.model.OpenLiveResultModel;
import com.hzlz.aviation.feature.live.model.PlatformMessageModel;
import com.hzlz.aviation.feature.live.requset.CheckHasLiveRequest;
import com.hzlz.aviation.feature.live.requset.GetLiveReviewListRequest;
import com.hzlz.aviation.feature.live.requset.GetPlayingLiveListRequest;
import com.hzlz.aviation.feature.live.requset.IMConfigRequest;
import com.hzlz.aviation.feature.live.requset.LiveMessageRequest;
import com.hzlz.aviation.feature.live.requset.OpenLiveRequest;
import com.hzlz.aviation.feature.live.requset.PlatformListRequest;
import com.hzlz.aviation.feature.live.requset.StartConnectMicroRequest;
import com.hzlz.aviation.kernel.base.model.ImConfigModel;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public class LiveRepository extends BaseDataRepository {

    /**
     * 获取直播权限
     */
    public Observable<Object> getHasLivePermission() {

        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return LiveApi.Instance.get().hasLivePermission();
                    }
                };
            }
        }.asObservable();
    }


    /**
     * 获取当前账号所属入驻号
     */
    public Observable<List<PlatformMessageModel>> getPlatformList() {

        return new OneTimeNetworkData<List<PlatformMessageModel>>(mEngine) {
            @Override
            protected BaseRequest<List<PlatformMessageModel>> createRequest() {

                return new PlatformListRequest();
            }
        }.asObservable();
    }

    /**
     * 开启直播
     *
     * @param platformId  入驻号id
     * @param roomName    房间名称
     * @param thumbId     图片id
     * @param description 描述
     */
    public Observable<OpenLiveResultModel> getStartLive(int platformId, String roomName, String thumbId,
                                                        String description, int liveType) {

        return new OneTimeNetworkData<OpenLiveResultModel>(mEngine) {
            @Override
            protected BaseRequest<OpenLiveResultModel> createRequest() {
                OpenLiveRequest request = new OpenLiveRequest();
                request.setParameters(platformId, roomName, thumbId, description, liveType);
                return request;
            }
        }.asObservable();
    }

    /**
     * 获取IM配置
     */
    public Observable<ImConfigModel> getIMConfig(String userId) {

        return new OneTimeNetworkData<ImConfigModel>(mEngine) {
            @Override
            protected BaseRequest<ImConfigModel> createRequest() {
                IMConfigRequest configRequest = new IMConfigRequest();
                configRequest.setId(userId);
                return configRequest;
            }
        }.asObservable();

    }


    /**
     * 获取直播信息
     */
    public Observable<LiveDetailModel> getLiveMessage(String mediaId) {

        return new OneTimeNetworkData<LiveDetailModel>(mEngine) {
            @Override
            protected BaseRequest<LiveDetailModel> createRequest() {
                LiveMessageRequest request = new LiveMessageRequest();
                request.setId(mediaId);
                return request;
            }
        }.asObservable();

    }


    /**
     * 直播结束
     *
     * @param viewSum 浏览人次
     * @param digg    点赞数
     */
    public Observable<Object> uploadLiveEndMessage(int viewSum, int digg) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("viewSum", viewSum);
        parameters.put("digg", digg);

        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return LiveApi.Instance.get().uploadLiveEndMessage(parameters);
                    }
                };
            }
        }.asObservable();
    }


    /**
     * 检查是否有断开的直播
     */
    public Observable<OpenLiveResultModel> checkHasLive() {

        return new OneTimeNetworkData<OpenLiveResultModel>(mEngine) {
            @Override
            protected BaseRequest<OpenLiveResultModel> createRequest() {
                return new CheckHasLiveRequest();
            }
        }.asObservable();

    }


    public Observable<Object> startConnectMicro(int connectVideo, String mediaId) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                StartConnectMicroRequest startConnectMicroRequest = new StartConnectMicroRequest();
                startConnectMicroRequest.setConnectVideo(connectVideo);
                startConnectMicroRequest.setMediaId(mediaId);
                return startConnectMicroRequest;
            }
        }.asObservable();
    }

    public Observable<ListWithPage<MediaModel>> getLiveReviewList(int pageNum, int pageSize) {
        return new OneTimeNetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                GetLiveReviewListRequest getLiveReviewListRequest = new GetLiveReviewListRequest();
                getLiveReviewListRequest.setPageNum(pageNum);
                getLiveReviewListRequest.setPageSize(pageSize);
                return getLiveReviewListRequest;
            }
        }.asObservable();
    }

    public Observable<List<MediaModel>> getPlayingLiveList() {
        return new NetworkData<List<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<List<MediaModel>> createRequest() {
                return new GetPlayingLiveListRequest();
            }

            @Override
            protected void saveData(List<MediaModel> mediaModelList) {

            }
        }.asObservable();
    }

}
