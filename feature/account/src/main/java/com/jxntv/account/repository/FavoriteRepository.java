package com.jxntv.account.repository;

import androidx.annotation.NonNull;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.Media;
import com.jxntv.account.model.UgcMyCommentModel;
import com.jxntv.account.request.FavoriteMediaRequest;
import com.jxntv.account.request.GetFavoriteDetailListRequest;
import com.jxntv.account.request.GetUgcContentListRequest;
import com.jxntv.account.request.GetUgcMyCommentListRequest;
import com.jxntv.account.ui.ugc.detail.UgcContentType;
import com.jxntv.base.model.share.FavoriteChangeModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.InteractDataObservable;
import com.jxntv.base.plugin.IFavoriteRepository;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.response.ListWithPage;
import io.reactivex.rxjava3.core.Observable;

import java.util.Iterator;
import java.util.List;

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
