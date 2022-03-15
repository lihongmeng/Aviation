package com.hzlz.aviation.feature.record.recorder.repository;

import com.hzlz.aviation.feature.record.recorder.request.GetPublishLinkWhiteListRequest;
import com.hzlz.aviation.feature.record.recorder.request.PublishRequest;
import com.hzlz.aviation.kernel.base.model.PublishLinkWhiteListItem;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class PublishRepository extends BaseDataRepository {

    public Observable<Integer> createVodVideoModel(
            String content,
            Integer fileType,
            String imageId,
            List<String> imageList,
            String introduction,
            boolean isPublic,
            String length,
            String soundContent,
            String videoId,
            String fileName,
            Long groupId,
            Long topicId,
            Integer playStyle,
            Long gatherId,
            boolean isQa,
            String answerId,
            String outShareTitle,
            String outShareUrl
    ) {
        return new NetworkData<Integer>(mEngine) {
            @Override
            protected BaseRequest<Integer> createRequest() {
                PublishRequest request = new PublishRequest();
                request.setContent(content);
                request.setFileType(fileType);
                request.setImageId(imageId);
                request.setImageList(imageList);
                request.setIntroduction(introduction);
                request.setIsPublic(isPublic);
                request.setLength(length);
                request.setSoundContent(soundContent);
                request.setVideoId(videoId);
                request.setFileName(fileName);
                request.setOutShareTitle(outShareTitle);
                request.setOutShareUrl(outShareUrl);
                request.setGroupId(groupId);
                request.setTopicId(topicId);
                request.setGatherId(gatherId);
                request.setPlayStyle(playStyle);
                request.setIsQa(isQa,answerId);
                return request;
            }

            @Override
            protected void saveData(Integer result) {
            }
        }.asObservable(GVideoSchedulers.IO_PRIORITY_USER);
    }

    public Observable<ArrayList<PublishLinkWhiteListItem>> getPublishLinkWhiteList() {
        return new NetworkData<ArrayList<PublishLinkWhiteListItem>>(mEngine) {
            @Override
            protected BaseRequest<ArrayList<PublishLinkWhiteListItem>> createRequest() {
                return new GetPublishLinkWhiteListRequest();
            }

            @Override
            protected void saveData(ArrayList<PublishLinkWhiteListItem> vodVideoCreateModel) {

            }
        }.asObservable(GVideoSchedulers.IO_PRIORITY_USER);
    }

}
