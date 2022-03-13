package com.jxntv.account.api;

import androidx.annotation.NonNull;
import com.google.gson.JsonElement;
import com.jxntv.account.model.OneKeyFollowModel;
import com.jxntv.network.BaseUrlChangedCallback;
import com.jxntv.network.NetworkManager;
import com.jxntv.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Tag;
import retrofit2.http.Url;

/**
 * 账户相关 API
 *
 *
 * @since 2020-01-13 15:52
 */
public interface AccountAPI {
  final class Instance {
    @NonNull
    private static volatile AccountAPI sInstance;

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

    private static AccountAPI createAPI() {
      return NetworkManager.getInstance().getRetrofit().create(AccountAPI.class);
    }

    @NonNull
    public static AccountAPI get() {
      return sInstance;
    }
  }

  //<editor-fold desc="初始化">

  /**
   * 获取初始化配置
   */
  @GET("api/init/config")
  Observable<Response<JsonElement>> getInitializationConfig();
  //</editor-fold>

  //<editor-fold desc="授权">

  /**
   * 授权，同意协议
   */
  @POST("api/auth/visitor")
  Observable<Response<JsonElement>> authVisit(@Body Map<String, Object> parameters);
  //</editor-fold>



  //<editor-fold desc="验证码">

  /**
   * 发送短信验证码
   *
   * @param parameters 参数
   */
  @POST("api/code/sms")
  Observable<Response<JsonElement>> sendSmsCode(@Body Map<String, Object> parameters);

  //</editor-fold>

  //<editor-fold desc="用户">

  /**
   * 通过验证码登录
   *
   * @param parameters 参数
   */
  @POST("api/auth/sms")
  Observable<Response<JsonElement>> loginBySmsCode(@Body Map<String, Object> parameters);

  /**
   * 快速登录
   *
   * @param parameters 参数
   */
  @POST("api/auth/quick-login")
  Observable<Response<JsonElement>> quickLogin(@Body Map<String, Object> parameters);

  /**
   * 获取当前用户
   */
  @GET("api/user/info")
  Observable<Response<JsonElement>> getCurrentUser();

  /**
   * 通过验证码登录
   *
   * @param parameters 参数
   */
  @POST("api/auth/switch")
  Observable<Response<JsonElement>> switchAccount(@Body Map<String, Object> parameters);

  /**
   * 登出
   */
  @POST("api/auth/logout")
  Observable<Response<JsonElement>> logout();

  /**
   * 注销账户
   */
  @POST("/api/auth/delete")
  Observable<Response<JsonElement>> cancelAccount();

  /**
   * 换绑手机号
   *
   * @param parameters 参数
   */
  @POST("api/auth/rebind")
  Observable<Response<JsonElement>> rebindPhone(@Body Map<String, Object> parameters);

  /**
   * 修改用户信息
   *
   * @param parameters 参数
   */
  @POST("api/user/edit")
  Observable<Response<JsonElement>> modifyUser(@Body Map<String, Object> parameters);

  /**
   * 获取头像列表
   *
   * @return 头像列表
   */
  @GET("api/user/head/preset")
  Observable<Response<JsonElement>> getAvatarList();

  /**
   * 身份认证
   *
   * @param parameters 参数
   */
  @POST("api/user/identity/submit")
  Observable<Response<JsonElement>> authenticateIdCard(@Body Map<String, Object> parameters);

  /**
   * 获取地区信息
   */
  @GET("api/region/province/city")
  Observable<Response<JsonElement>> getRegionMessage();


  //</editor-fold>

  //<editor-fold desc="作者">

  /**
   * 通过 ID 获取UGC
   *
   * @param parameters 参数
   */
  @GET("api/authors/ugc")
  Observable<Response<JsonElement>> getUserAuthorById(@QueryMap Map<String, Object> parameters);

  /**
   * 通过 ID 获取PGC
   *
   * @param authorId 作者 id
   */
  @GET("api/authors/{authorId}")
  Observable<Response<JsonElement>> getAuthorById(@Path("authorId") String authorId,
      @QueryMap Map<String, Object> parameters);


  /**
   * 通过 ID 用户主页
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage")
  Observable<Response<JsonElement>> getUserHomePage(@Path("userId") String userId);

  /**
   * ugc 菜单
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/tab")
  Observable<Response<JsonElement>> getUgcMenuTabs(@Path("userId") String userId);

  /**
   * ugc 动态
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/posts")
  Observable<Response<JsonElement>> getUgcCompositionList(@Path("userId") String userId, @QueryMap Map<String, Object> parameters);

  /**
   * ugc 喜欢
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/favorites")
  Observable<Response<JsonElement>> getUgcFavoriteList(@Path("userId") String userId,@QueryMap Map<String, Object> parameters);


  /**
   * ugc 评论
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/comments")
  Observable<Response<JsonElement>> getUgcCommentList(@Path("userId") String userId,@QueryMap Map<String, Object> parameters);


  /**
   * ugc 提问
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/question")
  Observable<Response<JsonElement>> getUgcQuestionList(@Path("userId") String userId, @QueryMap Map<String, Object> parameters);


  /**
   * ugc 回答
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/answer")
  Observable<Response<JsonElement>> getUgcAnswerList(@Path("userId") String userId, @QueryMap Map<String, Object> parameters);


  /**
   * ugc 我的关注
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/follow/author")
  Observable<Response<JsonElement>> getUgcMyFollow(@Path("userId") String userId,@QueryMap Map<String, Object> parameters);


  /**
   * ugc 我加入的圈子
   *
   * @param userId 参数
   */
  @GET("api/user/{userId}/homepage/follow/group")
  Observable<Response<JsonElement>> getUgcMyCircle(@Path("userId") String userId,@QueryMap Map<String, Object> parameters);


  /**
   * 通过作者 id 获取作者资源列表
   *
   * @param authorId 作者 id
   * @param parameters 参数
   */
  @GET("api/media/list/{authorId}")
  Observable<Response<JsonElement>> getAuthorMediaList(
      @Path("authorId") String authorId,
      @QueryMap Map<String, Object> parameters
  );
  //</editor-fold>

  //<editor-fold desc="动态">

  /**
   * 是否有新动态
   * @return
   */
  @GET("api/interact/news/unread")
  Observable<Response<JsonElement>> hasNewMoment();
  /**
   * 获取动态列表
   *
   * @param parameters 参数
   */
  @GET("api/interact/news")
  Observable<Response<JsonElement>> getMomentList(@QueryMap Map<String, Object> parameters);

  //</editor-fold>

  //<editor-fold desc="关注">

  /**
   * 获取关注列表
   *
   * @param parameters 参数
   */
  @GET("api/interact/follow")
  Observable<Response<JsonElement>> getFlowList(@QueryMap Map<String, Object> parameters);

  /**
   * 获取粉丝列表
   *
   * @param parameters 参数
   */
  @GET("api/interact/fans")
  Observable<Response<JsonElement>> getFansList(@QueryMap Map<String, Object> parameters);


  /**
   * 关注
   *
   * @param parameters 参数
   */
  @POST("api/interact/follow")
  Observable<Response<JsonElement>> follow(@Body Map<String, Object> parameters);

  // 推荐关注列表
  @GET("api/interact/recommend/follow")
  Observable<Response<JsonElement>> getRecommendFollowList();

  // 已关注作者动态列表
  @GET("api/media/follow/resource/list")
  Observable<Response<JsonElement>> getAlreadyFollowContentList(@QueryMap Map<String, Object> parameters);

  // 一键关注
  @POST("api/interact/follow/batch")
  Observable<Response<JsonElement>> oneKeyFollow(@Body List<OneKeyFollowModel> oneKeyFollowModelList);

  // 我的关注列表（首页关注）
  @GET("api/interact/self/follow")
  Observable<Response<JsonElement>> myFollowList(@QueryMap Map<String, Object> parameters);

  //</editor-fold>

  //<editor-fold desc="收藏">

  /**
   * 获取收藏列表
   */
  @GET("api/interact/favorite/category")
  Observable<Response<JsonElement>> getFavoriteList(@QueryMap Map<String, Object> parameters);

  /**
   * 获取收藏详情列表
   *
   * @param favoriteId 收藏 id
   * @param parameters 参数
   */
  @GET("api/interact/favorite/{favoriteId}")
  Observable<Response<JsonElement>> getFavoriteDetailList(
      @Path("favoriteId") String favoriteId,
      @QueryMap Map<String, Object> parameters
  );

  /**
   * 收藏资源
   *
   * @param parameters 参数
   */
  @POST("api/interact/favorite")
  Observable<Response<JsonElement>> favoriteMedia(@Body Map<String, Object> parameters);

  /**
   * 收藏资源
   *
   * @param parameters 参数
   */
  @POST("api/interact/favorite/cancel")
  Observable<Response<JsonElement>> cancelFavorite(@Body Map<String, Object> parameters);


  /**
   * 举报功能
   *
   * @param parameters 参数
   * @return
   */
  @POST("api/interact/report")
  Observable<Response<JsonElement>> report(@Body Map<String, Object> parameters);
  //</editor-fold>

  //<editor-fold desc="消息通知">

  /**
   * 是否有未读消息
   */
  @GET("api/notification/unread")
  Observable<Response<JsonElement>> hasUnreadMessageNotification();

  /**
   * 获取消息通知列表
   *
   * @param parameters 参数
   */
  @GET("api/notification/list")
  Observable<Response<JsonElement>> getMessageNotificationList(
      @QueryMap Map<String, Object> parameters
  );

  /**
   * 获取消息通知详情列表
   *
   * @param parameters 参数
   */
  @GET("api/notification/list/{sourceId}/detail")
  Observable<Response<JsonElement>> getMessageNotificationDetailList(
      @Path("sourceId") int sourceId,
      @QueryMap Map<String, Object> parameters
  );
  //</editor-fold>

  //<editor-fold desc="文件">

  /**
   * 创建文件
   *
   * @param parameters 参数
   */
  @POST("api/file/upload/address")
  Observable<Response<JsonElement>> createFile(@Body Map<String, Object> parameters);

  /**
   * 上传文件
   *
   * @param url 上传地址
   * @param body 文件请求体
   * @param tag 请求标记
   */
  @PUT
  Observable<Response<Void>> uploadFile(
      @Url String url,
      @Body RequestBody body,
      @Tag String tag
  );

  /**
   * Head 文件
   *
   * @param url 文件 Url
   * @param tag 请求标识
   */
  @GET
  Observable<Response<Void>> headFile(@Url String url, @Tag String tag);

  /**
   * 下载文件
   *
   * @param url 文件 Url
   * @param range range
   * @param tag 请求标识
   */
  @GET
  @Streaming
  Observable<ResponseBody> downloadFile(
      @Url String url,
      @Header("Range") String range,
      @Tag String tag
  );

  /**
   * 发布作品
   * @param parameters 作品相关参数
   * @return
   */
  @POST("api/media/ugc")
  Observable<Response<JsonElement>> publish(@Body Map<String, Object> parameters);

  @DELETE("api/media/ugc/{mediaId}")
  Observable<Response<JsonElement>> deleteComposition(@Path("mediaId") String mediaId);

  // 校验是否需要邀请码
  @GET("api/invite/check")
  Observable<Response<JsonElement>> checkNeedInviteCode(@QueryMap Map<String, Object> parameters);

  // 核销邀请码
  @POST("api/invite/write-off")
  Observable<Response<JsonElement>> verificationInviteCode(@Body Map<String, Object> parameters);

  /**
   * 当前账号是否有私信权限
   */
  @GET("api/im/user/auth")
  Observable<Response<JsonElement>> checkIMChatPermission(@Query("userId") String userId);

  /**
   * 获取IM官方账号
   */
  @GET("api/im/official-user/list")
  Observable<Response<JsonElement>> getIMPlatformAccount();

  // 检查用户是否加入社区
  @GET("api/group/check/join")
  Observable<Response<JsonElement>> checkUserJoinCommunity(@QueryMap Map<String, Object> parameters);

  //</editor-fold>
}
