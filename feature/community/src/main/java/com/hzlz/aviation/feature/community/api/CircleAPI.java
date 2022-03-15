package com.hzlz.aviation.feature.community.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.kernel.network.BaseUrlChangedCallback;
import com.hzlz.aviation.kernel.network.NetworkManager;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 账户相关 API
 *
 * @since 2020-01-13 15:52
 */
public interface CircleAPI {

    final class Instance {
        @NonNull
        private static volatile CircleAPI sInstance;

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

        private static CircleAPI createAPI() {
            return NetworkManager.getInstance().getRetrofit().create(CircleAPI.class);
        }

        @NonNull
        public static CircleAPI get() {
            return sInstance;
        }

    }

    // 评论列表
    @GET("api/group/content/comment")
    Observable<Response<JsonElement>> commentList(@Body Map<String, Object> parameters);

    // 话题详情页信息流
    @GET("api/ai/recommend/topic/content")
    Observable<Response<JsonElement>> topicDetailContentList(@QueryMap Map<String, Object> parameters);

    // 圈子红人列表
    @GET("api/group/manage")
    Observable<Response<JsonElement>> circleFamousList(@QueryMap Map<String, Object> parameters);

    // 圈子权限校验
    @GET("api/group/manage/permission")
    Observable<Response<JsonElement>> circlePermissionVerify(@QueryMap Map<String, Object> parameters);

    // 圈子审核列表
    @GET("api/group/audit/page")
    Observable<Response<JsonElement>> circleVerifyList(@QueryMap Map<String, Object> parameters);

    // 审核状态变更
    @PUT("api/group/audit/status")
    Observable<Response<JsonElement>> verifyStatusChange(@QueryMap Map<String, Object> parameters);

    // 圈子编辑
    @PUT("api/group")
    Observable<Response<JsonElement>> circleEdit(@Body Map<String, Object> parameters);

    // 申请加入圈子
    @GET("api/group/apply")
    Observable<Response<JsonElement>> applyJoinCircle(@QueryMap Map<String, Object> parameters);

    // 圈子详情
    @GET("api/group/detail")
    Observable<Response<JsonElement>> circleDetail(@QueryMap Map<String, Object> parameters);

    // 发现圈子内容列表
    @GET("api/group/discover/content")
    Observable<Response<JsonElement>> findCircleContentList(@QueryMap Map<String, Object> parameters);

    // 发现圈子标签列表
    @GET("api/group/discover/tags")
    Observable<Response<JsonElement>> findCircleTagList();

    // 我的圈子
    @GET("api/group/my")
    Observable<Response<JsonElement>> myCircle();

    // 我的圈子-下方内容列表
    @GET("api/group/my/content")
    Observable<Response<JsonElement>> myCircleUnderContent(@QueryMap Map<String, Object> parameters);

    // 圈子下拉列表
    @GET("api/group/page")
    Observable<Response<JsonElement>> circleDownPullList(@QueryMap Map<String, Object> parameters);

    // 话题创建
    @POST("api/group/topic")
    Observable<Response<JsonElement>> topicCreate(@Body Map<String, Object> parameters);

    // 话题编辑
    @PUT("api/group/topic")
    Observable<Response<JsonElement>> topicEdit(@Body Map<String, Object> parameters);

    // 话题详情
    @GET("api/group/topic/detail")
    Observable<Response<JsonElement>> topicDetail(@QueryMap Map<String, Object> parameters);

    // 话题检索
    @GET("api/group/topic/search")
    Observable<Response<JsonElement>> topicSearch(@QueryMap Map<String, Object> parameters);

    // 退出圈子
    @PUT("api/group/exit")
    Observable<Response<JsonElement>> exitCircle(@Body Map<String, Object> parameters);

    // 判断是否可以发布
    @GET("api/group/content/permission")
    Observable<Response<JsonElement>> canPublish(@QueryMap Map<String, Object> parameters);

    // 圈子组件列表
    @GET("api/group/gather/tags")
    Observable<Response<JsonElement>> circleModuleList(@Query("groupId") long groupId);

    // 圈子组件内容列表搜索
    @GET("api/group/content/gather/search")
    Observable<Response<JsonElement>> circleModuleListContent(@QueryMap Map<String, Object> parameters);

    // 圈子详情热聊
    @GET("api/ai/recommend/group/content")
    Observable<Response<JsonElement>> circleDetailHot(@QueryMap Map<String, Object> parameters);

    // 获取需要置顶的直播
    @GET("api/group/top/live")
    Observable<Response<JsonElement>> circleTopLive(@QueryMap Map<String, Object> parameters);

    // 放心爱活动信息
    @GET("api/activity/{id}")
    Observable<Response<JsonElement>> getActivityData(@Path("id") String id);

    // 放心爱签到接口
    @POST("api/activity/member/sign")
    Observable<Response<JsonElement>> fxaSign(@Body Map<String, Object> parameters);

    // 放心爱配对接口
    @POST("api/activity/member/pair")
    Observable<Response<JsonElement>> fxaPair(@Body Map<String, Object> parameters);

    // 问答广场列表
    @POST("api/answer/square")
    Observable<Response<JsonElement>> getQAGroupList(@Body Map<String, Object> parameters);

    // 问答老师列表
    @GET("api/group/{group_id}/mentor")
    Observable<Response<JsonElement>> getQATeacherList(@Path("group_id") String id,@QueryMap Map<String, Object>parameters );

    // 动态发布圈子筛选列表
    @GET("api/group/option/list")
    Observable<Response<JsonElement>> getRecommendCommunity(@QueryMap Map<String, Object> parameters);

}
