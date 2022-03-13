package com.jxntv.feed.api;

import com.google.gson.JsonElement;
import io.reactivex.rxjava3.core.Observable;
import java.util.Map;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Feed请求API
 */
public interface FeedApi {


    /**
     * 获取feed tab列表数据
     */
    @GET("api/media/channels/v2")
    Observable<Response<JsonElement>> loadFeedTabs(
            @Query(FeedApiConstants.CHANNEL_TYPE) String mediaType);

    /**
     * 获取首页feed数据
     */
    @GET("api/media/feed")
    Observable<Response<JsonElement>> loadHomeData(
            @QueryMap Map<String, Object> parameters);

    /**
     * 获取视听
     */
    @GET("api/media/feed-video")
    Observable<Response<JsonElement>> loadVideoAudio(
            @QueryMap Map<String, Object> parameters);

    /**
     * 获取媒体页feed数据
     */
    @GET("api/media/feed")
    Observable<Response<JsonElement>> loadTabData(
            @Query(FeedApiConstants.TYPE) int type,
            @Query(FeedApiConstants.CHANNEL_ID) String tabId,
            @Query(FeedApiConstants.CURSOR) String cursor);

    /**
     * 获取媒体页feed数据
     */
    @GET("api/media/feed-news")
    Observable<Response<JsonElement>> loadFeedData(
            @Query(FeedApiConstants.TYPE) int type,
            @Query(FeedApiConstants.CHANNEL_ID) String tabId,
            @Query(FeedApiConstants.CURSOR) String cursor);

    @GET("api/media/details")
    Observable<Response<JsonElement>> loadTabDetail(
        @Query(FeedApiConstants.CHANNEL_ID) String tabId,
        @Query(FeedApiConstants.CURSOR) String cursor);

    @GET("api/ai/recommend/video/similar/content")
    Observable<Response<JsonElement>> loadMoreShortVideo(@QueryMap Map<String, Object> parameters);

    // 热门圈子
    @GET("api/ai/recommend/group")
    Observable<Response<JsonElement>> hotCircle(@QueryMap Map<String, Object> parameters);

    // 热门圈子
    @GET("api/ai/recommend/topic")
    Observable<Response<JsonElement>> hotTopic(@QueryMap Map<String, Object> parameters);

    // 获取首页推荐内容列表
    @GET("api/ai/recommend/content")
    Observable<Response<JsonElement>> getHomeRecommendContentList(@QueryMap Map<String, Object> parameters);

    // 获取首页的Banner
    @GET("api/banners/scene/show")
    Observable<Response<JsonElement>> getBannerList(@QueryMap Map<String, Object> parameters);

}
