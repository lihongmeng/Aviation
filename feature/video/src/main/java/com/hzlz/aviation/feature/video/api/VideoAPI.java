package com.hzlz.aviation.feature.video.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.kernel.network.BaseUrlChangedCallback;
import com.hzlz.aviation.kernel.network.NetworkManager;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface VideoAPI {
  final class Instance {
    @NonNull
    private static volatile VideoAPI sInstance;

    static {
      // 监听 BaseUrl 改变
      NetworkManager.getInstance().addBaseUrlChangedCallback(new BaseUrlChangedCallback() {
        @Override
        public void onBaseUrlChanged() {
          sInstance = createAPI();
        }
      });
      sInstance = createAPI();
    }

    private static VideoAPI createAPI() {
      return NetworkManager.getInstance().getRetrofit().create(VideoAPI.class);
    }

    @NonNull
    public static VideoAPI get() {
      return sInstance;
    }
  }

  @GET("api/media/{mediaId}")
  Observable<Response<JsonElement>> loadMedia(
      @Path("mediaId") String mediaId
  );
  @GET("api/media/columns/{columnId}")
  Observable<Response<JsonElement>> loadSeries(
      @Path("columnId") String columnId,
      //@Query("pageNum") int pageNum,
      //@Query("pageSize") int pageSize
      @QueryMap Map<String, Object> parameters
  );

  @GET("api/advert")
  Observable<Response<JsonElement>> loadRecommend(
      @Query("mediaId") String mediaId
      //@Query("pageNum") int pageNum,
      //@Query("pageSize") int pageSize
  );

  @GET("api/comment/list/{mediaId}")
  Observable<Response<JsonElement>> loadComment(
      @Path("mediaId") String mediaId,
      //@Query("pageNum") int pageNum,
      //@Query("pageSize") int pageSize
      @QueryMap Map<String, Object> parameters
  );
  @POST("api/comment")
  Observable<Response<JsonElement>> comment(
      //@Query("mediaId") String mediaId,
      //@Query("content") String content
      @Body Map<String, Object> parameters
  );
  @DELETE("api/comment/{commentId}")
  Observable<Response<JsonElement>> deleteComment(
      @Path("commentId") String commentId
  );
  @POST("api/comment/reply")
  Observable<Response<JsonElement>> reply(
      //@Query("type") int type,
      //@Query("toId") long toId,
      //@Query("content") String content,
      @Body Map<String, Object> parameters
  );
  @DELETE("api/comment/reply/{replyId}")
  Observable<Response<JsonElement>> deleteReply(
      @Path("replyId") long replyId
  );

  @POST("api/comment/praise")
  Observable<Response<JsonElement>> commentPraise(@Body Map<String, Object> parameters);

  @GET("api/live/broadcast/{mediaId}")
  Observable<Response<JsonElement>> loadAtyLiveDetail(@Path("mediaId") String mediaId);

  @GET("api/live/broadcast/comment")
  Observable<Response<JsonElement>> loadAtyLiveComment(@Query("mediaId") String mediaId, @Query("fetchTime") String fetchTime);

  @POST("api/live/broadcast")
  Observable<Response<JsonElement>> commentAtyLive(@Body Map<String, Object> parameters);

  @POST("api/live/broadcast/like/{mediaId}")
  Observable<Response<JsonElement>> likeAtyLive(@Path("mediaId") String mediaId);


  @GET("/api/media/list/special-tag/{id}")
  Observable<Response<JsonElement>> loadSpecialList(@Path("id") String id,@QueryMap Map<String, Object> parameters);

  @GET("/api/media/list/content-list/{id}")
  Observable<Response<JsonElement>> loadNewsContentList(@Path("id") String id,@QueryMap Map<String, Object> parameters);



}

