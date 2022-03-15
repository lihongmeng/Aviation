package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.OneKeyFollowModel;
import com.hzlz.aviation.feature.account.request.FollowRequest;
import com.hzlz.aviation.feature.account.request.GetAlreadyFollowContentListRequest;
import com.hzlz.aviation.feature.account.request.GetFansListRequest;
import com.hzlz.aviation.feature.account.request.GetFollowListRequest;
import com.hzlz.aviation.feature.account.request.GetRecommendFollowListRequest;
import com.hzlz.aviation.feature.account.request.OneKeyFollowRequest;
import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.share.FollowChangeModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.plugin.IFollowRepository;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @since 2020-02-10 21:29
 */
public final class InteractionRepository extends BaseDataRepository implements IFollowRepository {
    //<editor-fold desc="关注">

    /**
     * 获取关注列表
     *
     * @param pageNumber 分页编号
     * @param pageSize   分页大小
     */
    @NonNull
    public Observable<ListWithPage<Author>> getFollowList(Author author, int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<Author>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<Author>> createRequest() {
                GetFollowListRequest request = new GetFollowListRequest();
                request.setAuthor(author);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<Author> authorListWithPage) {
                if (authorListWithPage != null && authorListWithPage.getList() != null && !authorListWithPage.getList().isEmpty()) {
                    Iterator<Author> it = authorListWithPage.getList().iterator();
                    do {
                        Author author = it.next();
                        if (author != null) {
                            author.updateInteract();
                            continue;
                        }
                        it.remove();
                    } while (it.hasNext());
                }
            }
        }.asObservable();
    }

    /**
     * 获取粉丝列表
     *
     * @param pageNumber 分页编号
     * @param pageSize   分页大小
     */
    @NonNull
    public Observable<ListWithPage<Author>> getFansList(Author author, int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<Author>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<Author>> createRequest() {
                GetFansListRequest request = new GetFansListRequest();
                request.setAuthor(author);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<Author> authorListWithPage) {
                if (authorListWithPage != null && authorListWithPage.getList() != null && !authorListWithPage.getList().isEmpty()) {
                    Iterator<Author> it = authorListWithPage.getList().iterator();
                    do {
                        Author author = it.next();
                        if (author != null) {
                            author.updateInteract();
                            continue;
                        }
                        it.remove();
                    } while (it.hasNext());
                }
            }
        }.asObservable();
    }

    @NonNull
    @Override
    public Observable<Boolean> followAuthor(@NonNull String authorId, @AuthorType int authorType,
                                            String authorName,boolean follow) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                FollowRequest request = new FollowRequest();
                request.setAuthorId(authorId, authorType);
                request.setFollow(follow);
                return request;
            }
        }.asObservable().map(o -> {
            //发送广播，动态、关注列表、我的关注数目等需要变化，重新刷新列表
            GVideoEventBus.get(SharePlugin.EVENT_FOLLOW_CHANGE, FollowChangeModel.class)
                    .post(new FollowChangeModel(authorId, follow));
            //其他场景联动，由Observable监听处理
            InteractDataObservable.getInstance().setFollow(authorId, follow);
            GVideoSensorDataManager.getInstance().followAccount(follow, authorId, authorName, authorType,null);
            return follow;
        });
    }

    /**
     * 搜素关注列表
     *
     * @param author     某用户相关关注
     * @param input      搜索内容
     * @param pageNumber 分页编号
     * @param pageSize   分页大小
     */
    @NonNull
    public Observable<ListWithPage<Author>> searchFollow(String input, Author author, int pageNumber, int pageSize) {
        return new OneTimeNetworkData<ListWithPage<Author>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<Author>> createRequest() {
                GetFollowListRequest request = new GetFollowListRequest();
                request.setAuthor(author);
                request.setInput(input);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<Author> authorListWithPage) {
                if (authorListWithPage != null && authorListWithPage.getList() != null && !authorListWithPage.getList().isEmpty()) {
                    Iterator<Author> it = authorListWithPage.getList().iterator();
                    do {
                        Author author = it.next();
                        if (author != null && author.getName() != null && author.getName().contains(input)) {
                            author.updateInteract();
                            continue;
                        }
                        it.remove();
                    } while (it.hasNext());
                }
            }
        }.asObservable();
    }

    /**
     * 检查私信权限
     */
    @NonNull
    public Observable<Object> checkIMChatPermission(String userId){

        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return AccountAPI.Instance.get().checkIMChatPermission(userId);
                    }
                };
            }
        }.asObservable();
    }



    //</editor-fold>

    public Observable<List<Author>> getRecommendFollowList() {
        return new OneTimeNetworkData<List<Author>>(mEngine) {
            @Override
            protected BaseRequest<List<Author>> createRequest() {
                return new GetRecommendFollowListRequest();
            }

            @Override
            protected void saveData(List<Author> authorListWithPage) {
            }
        }.asObservable();
    }

    public Observable<ListWithPage<MediaModel>> getAlreadyFollowContentList(int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                GetAlreadyFollowContentListRequest request = new GetAlreadyFollowContentListRequest();
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                request.setNeedComment(true);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> mediaListWithPage) {
                if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
                    for (MediaModel media : mediaListWithPage.getList()) {
                        if(media==null){
                            continue;
                        }
                        media.updateInteract();
                    }
                }
            }
        }.asObservable();
    }

    public Observable<Object> oneKeyFollowRequest(List<Author> modelList) {
        return new NetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                OneKeyFollowRequest request = new OneKeyFollowRequest();
                request.setOneKeyFollowModelList(change(modelList));
                return request;
            }

            @Override
            protected void saveData(Object mediaListWithPage) {
            }
        }.asObservable();
    }

    public List<OneKeyFollowModel> change(List<Author> modelList) {
        List<OneKeyFollowModel> result = new ArrayList<>();
        for (Author author : modelList) {
            OneKeyFollowModel oneKeyFollowModel = new OneKeyFollowModel();
            oneKeyFollowModel.authorId = author.getId();
            oneKeyFollowModel.type = author.getType();
            result.add(oneKeyFollowModel);
        }
        return result;
    }

}
