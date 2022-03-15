package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.feature.account.model.UGCMenuTabModel;
import com.hzlz.aviation.feature.account.model.UgcAuthorModel;
import com.hzlz.aviation.feature.account.model.UserAuthor;
import com.hzlz.aviation.feature.account.request.GetAuthorByIdRequest;
import com.hzlz.aviation.feature.account.request.GetAuthorMediaListByIdRequest;
import com.hzlz.aviation.feature.account.request.GetMyCircleFollowListRequest;
import com.hzlz.aviation.feature.account.request.GetUserAuthorByIdRequest;
import com.hzlz.aviation.feature.account.request.MyFollowListRequest;
import com.hzlz.aviation.feature.account.request.UGCMenuTabRequest;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.plugin.ChatIMPlugin;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 作者仓库
 *
 *
 * @since 2020-03-10 22:34
 */
public final class AuthorRepository extends BaseDataRepository {

  /**
   * 获取关注列表
   */
  @NonNull
  public Observable<ListWithPage<UgcAuthorModel>> getMyCircleFollowList(String userId, int pageNum,boolean isFollow) {
    return new NetworkData<ListWithPage<UgcAuthorModel>>(mEngine) {

      @Override
      protected BaseRequest<ListWithPage<UgcAuthorModel>> createRequest() {
        GetMyCircleFollowListRequest request = new GetMyCircleFollowListRequest();
        request.setParameters(userId,pageNum,isFollow);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<UgcAuthorModel> listWithPage) {

      }
    }.asObservable();
  }

  /**
   * 首页关注，获取关注列表
   */
  @NonNull
  public Observable<ListWithPage<UgcAuthorModel>> getMyFollowList( int pageNum,int pageSize) {
    return new NetworkData<ListWithPage<UgcAuthorModel>>(mEngine) {

      @Override
      protected BaseRequest<ListWithPage<UgcAuthorModel>> createRequest() {
        MyFollowListRequest request = new MyFollowListRequest();
        request.setPageNumber(pageNum);
        request.setPageSize(pageSize);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<UgcAuthorModel> listWithPage) {
        for (UgcAuthorModel authorModel : listWithPage.getList()) {
          authorModel.updateInteract();
        }
      }
    }.asObservable();
  }


  /**
   * 通过 id 获取作者
   *
   * @param author 作者
   */
  @NonNull
  public Observable<Author> getAuthorById(@NonNull Author author) {
    return new NetworkData<Author>(mEngine) {

      @Override
      protected BaseRequest<Author> createRequest() {
        GetAuthorByIdRequest request = new GetAuthorByIdRequest();
        request.setAuthor(author);
        request.setAuthorId(author.getId());
        return request;
      }

      @Override
      protected void saveData(Author author) {
        author.updateInteract();
      }
    }.asObservable();
  }

  /**
   * 通过 id 获取UGC
   *
   * @param author 作者
   */
  @NonNull
  public Observable<UserAuthor> getUserAuthorById(@Nullable Author author) {
    return new NetworkData<UserAuthor>(mEngine) {

      @Override
      protected BaseRequest<UserAuthor> createRequest() {
        GetUserAuthorByIdRequest request = new GetUserAuthorByIdRequest();
        request.setAuthor(author);
        return request;
      }

      @Override
      protected void saveData(UserAuthor author) {
        author.updateInteract();
        // 创建 AuthorObservable
        author.getAuthorObservable();
      }
    }.asObservable();
  }


  public Observable<Map<String,Boolean>> getUserAuthorIsFollowMe(@Nullable Author author) {
    return new OneTimeNetworkData<UserAuthor>(mEngine) {

      @Override
      protected BaseRequest<UserAuthor> createRequest() {
        GetUserAuthorByIdRequest request = new GetUserAuthorByIdRequest();
        request.setAuthor(author);
        return request;
      }

    }.asObservable().map(o -> {
      Map<String, Boolean> map = new HashMap<>();
      map.put(Constant.MAP_STRING.IS_FOLLOW,o.isFollow());
      map.put(Constant.MAP_STRING.IS_FOLLOW_ME,o.isFollowMe());
      return map;
    });
  }


  @NonNull
  public Observable<ListWithPage<Media>> getAuthorMediaList( @NonNull Author author,
                                                             int pageNumber,
                                                             int pageSize){
    return getAuthorMediaList(author,0,pageNumber,pageSize);

  }
  /**
   * 获取作者列表
   *
   * @param author 作者
   * @param mediaTab 页签   0-全部、1-音视频、2-图文、3-语音、4-新闻、5-专题
   * @param pageNumber 分页编号
   * @param pageSize 分页大小
   */
  @NonNull
  public Observable<ListWithPage<Media>> getAuthorMediaList(
      @NonNull Author author,
      int mediaTab,
      int pageNumber,
      int pageSize) {
    return new NetworkData<ListWithPage<Media>>(mEngine) {
      @Override
      protected BaseRequest<ListWithPage<Media>> createRequest() {
        GetAuthorMediaListByIdRequest request = new GetAuthorMediaListByIdRequest();
        request.setAuthorId(author.getId());
        request.setAuthor(author);
        request.setMediaTab(mediaTab);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<Media> mediaListWithPage) {
        if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
          for (Media media : mediaListWithPage.getList()) {
            if(media==null){
              continue;
            }
            media.updateInteract();
          }
        }
      }
    }.asObservable();
  }


  /**
   * 获取im聊天官方账号列表
   *
   */
  @NonNull
  public void getPlatformAccountData() {

    new NetworkData<ListWithPage<String>>(mEngine){

      @Override
      protected BaseRequest<ListWithPage<String>> createRequest() {
        return new BaseGVideoRequest<ListWithPage<String>>() {
          @Override
          protected Observable<Response<JsonElement>> getResponseObservable() {
            return AccountAPI.Instance.get().getIMPlatformAccount();
          }
        };
      }

      @Override
      protected void saveData(ListWithPage<String> o) {
        if (o.getList()!=null){
          PluginManager.get(ChatIMPlugin.class).setPlatformAccountData(o.getList());
        }
      }
    }.asObservable().subscribe(new BaseResponseObserver<Object>() {
      @Override
      protected void onRequestData(Object o) {

      }

      @Override
      protected void onRequestError(Throwable throwable) {
        LogUtils.e(throwable.getMessage());
      }
    });
  }


  public Observable<UGCMenuTabModel> getUGCMenuTab(String userId){

    return new NetworkData<UGCMenuTabModel>(mEngine) {
      @Override
      protected BaseRequest<UGCMenuTabModel> createRequest() {
        UGCMenuTabRequest request = new UGCMenuTabRequest();
        request.setUserId(userId);
        return request;
      }

      @Override
      protected void saveData(UGCMenuTabModel ugcMenuTabModel) {

      }

    }.asObservable();
  }
}
