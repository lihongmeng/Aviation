package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.feature.account.model.UgcMyCommentModel;
import com.hzlz.aviation.feature.account.request.FavoriteMediaRequest;
import com.hzlz.aviation.feature.account.request.GetFavoriteDetailListRequest;
import com.hzlz.aviation.feature.account.request.GetUgcContentListRequest;
import com.hzlz.aviation.feature.account.request.GetUgcMyCommentListRequest;
import com.hzlz.aviation.feature.account.ui.ugc.detail.UgcContentType;
import com.hzlz.aviation.kernel.base.model.share.FavoriteChangeModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.plugin.IFavoriteRepository;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.util.Iterator;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 *
 * @since 2020-02-12 15:38
 */
public final class FavoriteRepository extends BaseDataRepository implements IFavoriteRepository {

  @NonNull
  public Observable<ListWithPage<MediaModel>> getUgcContentList(AuthorModel author, int pageNum, @UgcContentType int type) {
    return new NetworkData<ListWithPage<MediaModel>>(mEngine) {

      @Override
      protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
        GetUgcContentListRequest request = new GetUgcContentListRequest();
        request.setParameters(author.getId(),pageNum, type);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<MediaModel> favoriteList) {
        Iterator<MediaModel> it = favoriteList.getList().iterator();
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

  @NonNull
  public Observable<ListWithPage<UgcMyCommentModel>> getUgcMyCommentList(AuthorModel author, int pageNum) {
    return new NetworkData<ListWithPage<UgcMyCommentModel>>(mEngine) {

      @Override
      protected BaseRequest<ListWithPage<UgcMyCommentModel>> createRequest() {
        GetUgcMyCommentListRequest request = new GetUgcMyCommentListRequest();
        request.setParameters(author.getId(),pageNum);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<UgcMyCommentModel> favoriteList) {
        Iterator<UgcMyCommentModel> it = favoriteList.getList().iterator();
        while (it.hasNext()) {
          UgcMyCommentModel commentModel = it.next();
          if(commentModel==null){
            continue;
          }
          MediaModel mediaModel=commentModel.getMedia();
          if(mediaModel==null){
            it.remove();
            continue;
          }
          mediaModel.updateInteract();
        }
      }
    }.asObservable();
  }


  /**
   * 获取收藏详情列表
   *
   * @param favoriteId 收藏 id
   * @param pageNumber 分页编号
   * @param pageSize 分页大小
   */
  @NonNull
  public Observable<ListWithPage<Media>> getFavoriteDetailList(
      Author author,
      @NonNull String favoriteId,
      int pageNumber,
      int pageSize) {
    return new NetworkData<ListWithPage<Media>>(mEngine) {
      @Override
      protected BaseRequest<ListWithPage<Media>> createRequest() {
        GetFavoriteDetailListRequest request = new GetFavoriteDetailListRequest();
        request.setAuthor(author);
        request.setFavoriteId(favoriteId);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<Media> mediaListWithPage) {
        // 生成 MediaObservable
        List<Media> mediaList = mediaListWithPage.getList();
        for (Media media : mediaList) {
          if(media==null){
            continue;
          }
          media.updateInteract();
        }
      }
    }.asObservable();
  }

  /**
   * 收藏资源
   *
   * @param mediaId 资源 id
   * @param favorite 是否收藏
   */
  @NonNull
  public Observable<Boolean> favoriteMedia(
      @NonNull String mediaId,
      boolean favorite) {
    return new OneTimeNetworkData<Integer>(mEngine) {
      @Override
      protected BaseRequest<Integer> createRequest() {
        FavoriteMediaRequest request = new FavoriteMediaRequest();
        request.setMediaId(mediaId);
        request.setFavorite(favorite);
        return request;
      }
    }.asObservable().map(o -> {
      GVideoEventBus.get(SharePlugin.EVENT_FAVORITE_CHANGE, FavoriteChangeModel.class)
          .post(new FavoriteChangeModel(mediaId, favorite));
      InteractDataObservable.getInstance().setFavorite(mediaId, favorite);
      InteractDataObservable.getInstance().setFavorCount(mediaId, o);
      return favorite;
    });
  }

}
