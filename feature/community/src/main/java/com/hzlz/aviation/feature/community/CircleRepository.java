package com.hzlz.aviation.feature.community;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.model.FXAModel;
import com.hzlz.aviation.feature.community.request.ApplyJoinCircleRequest;
import com.hzlz.aviation.feature.community.request.CanPublishRequest;
import com.hzlz.aviation.feature.community.request.CircleDetailHotRequest;
import com.hzlz.aviation.feature.community.request.CircleDetailRequest;
import com.hzlz.aviation.feature.community.request.CircleEditRequest;
import com.hzlz.aviation.feature.community.request.CircleFamousListRequest;
import com.hzlz.aviation.feature.community.request.CircleModuleListContentRequest;
import com.hzlz.aviation.feature.community.request.CircleModuleListRequest;
import com.hzlz.aviation.feature.community.request.CircleTopLiveRequest;
import com.hzlz.aviation.feature.community.request.ExitCircleRequest;
import com.hzlz.aviation.feature.community.request.FXADataRequest;
import com.hzlz.aviation.feature.community.request.FXAPairRequest;
import com.hzlz.aviation.feature.community.request.FXASignRequest;
import com.hzlz.aviation.feature.community.request.FindCircleContentListRequest;
import com.hzlz.aviation.feature.community.request.FindCircleTagListRequest;
import com.hzlz.aviation.feature.community.request.GetRecommendCommunityRequest;
import com.hzlz.aviation.feature.community.request.MyCircleRequest;
import com.hzlz.aviation.feature.community.request.MyCircleUnderContentListRequest;
import com.hzlz.aviation.feature.community.request.QAAnswerListRequest;
import com.hzlz.aviation.feature.community.request.QAGroupListRequest;
import com.hzlz.aviation.feature.community.request.TopicDetailContentListRequest;
import com.hzlz.aviation.feature.community.request.TopicDetailRequest;
import com.hzlz.aviation.feature.community.request.TopicSearchRequest;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleDetail;
import com.hzlz.aviation.kernel.base.model.circle.CircleFamous;
import com.hzlz.aviation.kernel.base.model.circle.CircleModule;
import com.hzlz.aviation.kernel.base.model.circle.CircleTag;
import com.hzlz.aviation.kernel.base.model.circle.MyCircle;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.util.Iterator;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public final class CircleRepository extends BaseDataRepository {

    public static final int LOAD_DATA_TIME_LIMIT = 10;

    @NonNull
    public Observable<ListWithPage<MediaModel>> myCircleUnderContent(int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                MyCircleUnderContentListRequest request = new MyCircleUnderContentListRequest();
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                request.setNeedComment(true);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> mediaListWithPage) {
                if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
                    for (MediaModel media : mediaListWithPage.getList()) {
                        if (media == null) {
                            continue;
                        }
                        media.updateInteract();
                    }
                }
            }
        }.asObservable();
    }

    public Observable<ListWithPage<Circle>> findCircleContentList(
            final Long labelId,
            final int pageNum,
            final int pageSize
    ) {
        return new NetworkData<ListWithPage<Circle>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<Circle>> createRequest() {
                FindCircleContentListRequest request = new FindCircleContentListRequest();
                request.setLabel(labelId);
                request.setPageNum(pageNum);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<Circle> circleListWithPage) {
                if (circleListWithPage != null && circleListWithPage.getList() != null) {
                    for (Circle circle : circleListWithPage.getList()) {
                        if (circle == null) {
                            continue;
                        }
                        circle.updateInteract();
                    }
                }
            }
        }.asObservable();
    }

    @NonNull
    public Observable<MyCircle> myCircle() {
        return new NetworkData<MyCircle>(mEngine) {
            @Override
            protected BaseRequest<MyCircle> createRequest() {
                return new MyCircleRequest();
            }

            @Override
            protected void saveData(MyCircle circleTags) {

            }
        }.asObservable();
    }

    @NonNull
    public Observable<List<CircleTag>> findCircleTagList() {
        return new NetworkData<List<CircleTag>>(mEngine) {
            @Override
            protected BaseRequest<List<CircleTag>> createRequest() {
                return new FindCircleTagListRequest();
            }

            @Override
            protected void saveData(List<CircleTag> circleTags) {

            }
        }.asObservable();
    }

    public Observable<Object> joinCircle(long groupId, String reason) {
        return new NetworkData<Object>(mEngine) {

            @Override
            protected BaseRequest<Object> createRequest() {
                ApplyJoinCircleRequest applyJoinCircleRequest = new ApplyJoinCircleRequest();
                applyJoinCircleRequest.setGroupId(groupId);
                applyJoinCircleRequest.setReason(reason);
                return applyJoinCircleRequest;
            }

            @Override
            protected void saveData(Object o) {

            }
        }.asObservable().map(o -> {
            InteractDataObservable.getInstance().setJoinCircle(String.valueOf(groupId), true);
            return o;
        });
    }


    public Observable<Object> canPublish(long groupId, Long topicId) {
        return new NetworkData<Object>(mEngine) {

            @Override
            protected BaseRequest<Object> createRequest() {
                CanPublishRequest canPublishRequest = new CanPublishRequest();
                canPublishRequest.setGroupId(groupId);
                if (topicId != null) {
                    canPublishRequest.setTopicId(topicId);
                }
                return canPublishRequest;
            }

            @Override
            protected void saveData(Object o) {
            }
        }.asObservable();
    }

    public Observable<Object> exitCircle(long groupId) {
        return new NetworkData<Object>(mEngine) {

            @Override
            protected BaseRequest<Object> createRequest() {
                ExitCircleRequest exitCircleRequest = new ExitCircleRequest();
                exitCircleRequest.setGroupId(groupId);
                return exitCircleRequest;
            }

            @Override
            protected void saveData(Object o) {

            }
        }.asObservable().map(o -> {
            InteractDataObservable.getInstance().setJoinCircle(String.valueOf(groupId), false);
            return o;
        });
    }

    public Observable<ListWithPage<MediaModel>> loadTopicDetailContentList(
            Long topicId,
            int pageNum,
            int pageSize
    ) {
        return new NetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                TopicDetailContentListRequest request = new TopicDetailContentListRequest();
                request.setTopicId(topicId);
                request.setPageNum(pageNum);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> mediaListWithPage) {
                if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
                    for (MediaModel media : mediaListWithPage.getList()) {
                        if (media == null) {
                            continue;
                        }
                        media.updateInteract();
                    }
                }
            }
        }.asObservable();
    }

    @NonNull
    public Observable<CircleDetail> circleDetail(long groupId) {
        return new NetworkData<CircleDetail>(mEngine) {
            @Override
            protected BaseRequest<CircleDetail> createRequest() {
                CircleDetailRequest circleDetailRequest = new CircleDetailRequest();
                circleDetailRequest.setGroupId(groupId);
                return circleDetailRequest;
            }

            @Override
            protected void saveData(CircleDetail circleTags) {
                if (circleTags != null) {
                    circleTags.updateInteract();
                }
            }
        }.asObservable();
    }

    @NonNull
    public Observable<TopicDetail> topicDetail(long topicId) {
        return new NetworkData<TopicDetail>(mEngine) {
            @Override
            protected BaseRequest<TopicDetail> createRequest() {
                TopicDetailRequest circleDetailRequest = new TopicDetailRequest();
                circleDetailRequest.setTopicId(topicId);
                return circleDetailRequest;
            }

            @Override
            protected void saveData(TopicDetail circleTags) {

            }
        }.asObservable();
    }

    public Observable<ListWithPage<TopicDetail>> searchTopic(String query, long groupId, int pageNum, int pageSize) {
        return new NetworkData<ListWithPage<TopicDetail>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<TopicDetail>> createRequest() {
                TopicSearchRequest request = new TopicSearchRequest();
                request.setKey(query);
                request.setGroupId(groupId);
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<TopicDetail> mediaListWithPage) {
            }
        }.asObservable();
    }

    public Observable<Object> updateCircleInfo(@NonNull Circle circle) {
        return new NetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                CircleEditRequest circleEditRequest = new CircleEditRequest();
                circleEditRequest.setGroupId(circle.groupId);
                circleEditRequest.setIntroduction(circle.introduction);
                circleEditRequest.setName(circle.name);
                if (circle.imageVO != null) {
                    circleEditRequest.setCover(circle.imageVO.ossId);
                }
                return circleEditRequest;
            }

            @Override
            protected void saveData(Object object) {

            }
        }.asObservable();
    }

    public Observable<ListWithPage<CircleFamous>> circleFamousList(long groupId, int pageNum, int pageSize) {
        return new NetworkData<ListWithPage<CircleFamous>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<CircleFamous>> createRequest() {
                CircleFamousListRequest request = new CircleFamousListRequest();
                request.setGroupId(groupId);
                request.setPageNum(pageNum);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<CircleFamous> famousListWithPage) {
            }
        }.asObservable();
    }

    public Observable<List<CircleModule>> circleModuleList(long groupId) {
        return new NetworkData<List<CircleModule>>(mEngine) {
            @Override
            protected BaseRequest<List<CircleModule>> createRequest() {
                CircleModuleListRequest circleEditRequest = new CircleModuleListRequest();
                circleEditRequest.setGroupId(groupId);
                return circleEditRequest;
            }

            @Override
            protected void saveData(List<CircleModule> object) {

            }
        }.asObservable();
    }

    @NonNull
    public Observable<ListWithPage<MediaModel>> circleModuleListContent(
            long gatherId,
            int pageNumber,
            int pageSize
    ) {
        return new NetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                CircleModuleListContentRequest request = new CircleModuleListContentRequest();
                request.setGatherId(gatherId);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> mediaListWithPage) {
                if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
                    for (MediaModel media : mediaListWithPage.getList()) {
                        if (media == null) {
                            continue;
                        }
                        media.updateInteract();
                    }
                }
            }
        }.asObservable();
    }

    @NonNull
    public Observable<ListWithPage<MediaModel>> circleDetailHot(
            long groupId,
            int sortType,
            int pageNumber,
            int pageSize
    ) {
        return new NetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                CircleDetailHotRequest request = new CircleDetailHotRequest();
                request.setGroupId(groupId);
                request.setSortType(sortType);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                request.setNeedComment(true);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> mediaListWithPage) {
                if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
                    for (MediaModel media : mediaListWithPage.getList()) {
                        if (media == null) {
                            continue;
                        }
                        media.updateInteract();
                    }
                }
            }
        }.asObservable();
    }

    @NonNull
    public Observable<List<MediaModel>> circleTopLive(long groupId) {
        return new NetworkData<List<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<List<MediaModel>> createRequest() {
                CircleTopLiveRequest request = new CircleTopLiveRequest();
                request.setGroupId(groupId);
                return request;
            }

            @Override
            protected void saveData(List<MediaModel> mediaModelList) {
                if (mediaModelList == null) {
                    return;
                }
                for (MediaModel media : mediaModelList) {
                    if (media == null) {
                        continue;
                    }
                    media.updateInteract();
                }
            }
        }.asObservable();
    }

    @NonNull
    public Observable<FXAModel> getFXAData(String id) {
        return new NetworkData<FXAModel>(mEngine) {
            @Override
            protected BaseRequest<FXAModel> createRequest() {
                FXADataRequest request = new FXADataRequest();
                request.setId(id);
                return request;
            }

            @Override
            protected void saveData(FXAModel model) {

            }

        }.asObservable();
    }


    /**
     * 放心爱签到
     *
     * @param memberId 参加活动的用户id
     * @return
     */
    @NonNull
    public Observable<Object> fxaSign(String memberId) {

        return new NetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                FXASignRequest request = new FXASignRequest();
                request.setId(memberId);
                return request;
            }

            @Override
            protected void saveData(Object o) {

            }
        }.asObservable();
    }


    /**
     * 放心爱匹配
     *
     * @param memberId    参加活动的用户id
     * @param inputNumber 输入的id
     * @return
     */
    @NonNull
    public Observable<Object> fxaPair(String memberId, String inputNumber) {

        return new NetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                FXAPairRequest request = new FXAPairRequest();
                request.setParameters(memberId, inputNumber);
                return request;
            }

            @Override
            protected void saveData(Object o) {

            }
        }.asObservable();
    }


    @NonNull
    public Observable<ListWithPage<MediaModel>> getQAGroupList(String groupId, int pageNum) {

        return new NetworkData<ListWithPage<MediaModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<MediaModel>> createRequest() {
                QAGroupListRequest request = new QAGroupListRequest();
                request.setParameters(groupId, pageNum);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<MediaModel> list) {
                Iterator<MediaModel> it = list.getList().iterator();
                MediaModel model;
                while (it.hasNext()) {
                    model = it.next();
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
    public Observable<ListWithPage<AuthorModel>> getQATeacherList(String groupId, int pageNum) {

        return new NetworkData<ListWithPage<AuthorModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<AuthorModel>> createRequest() {
               return new QAAnswerListRequest(groupId, pageNum);
            }

            @Override
            protected void saveData(ListWithPage<AuthorModel> list) {

            }
        }.asObservable();
    }

    @NonNull
    public Observable<ListWithPage<Circle>> getRecommendCommunity(
            int pageNum,
            int pageSize,
            Long firstGroupId
    ) {

        return new NetworkData<ListWithPage<Circle>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<Circle>> createRequest() {
                GetRecommendCommunityRequest request = new GetRecommendCommunityRequest();
                request.setPageNumber(pageNum);
                request.setPageSize(pageSize);
                if (firstGroupId != null) {
                    request.setFirstGroupId(firstGroupId);
                }
                return request;
            }

            @Override
            protected void saveData(ListWithPage<Circle> list) {
            }
        }.asObservable();
    }

}
