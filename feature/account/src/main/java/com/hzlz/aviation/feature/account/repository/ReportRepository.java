package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.HasUnreadMessageNotificationResponse;
import com.hzlz.aviation.feature.account.request.HasNewMomentRequest;
import com.hzlz.aviation.feature.account.request.ReportRequest;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;

/**
 * 举报
 */
public final class ReportRepository extends BaseDataRepository {
    /**
     * 是否有新动态
     */
    @NonNull
    public Observable<HasUnreadMessageNotificationResponse> hasNewMoment() {
        return new OneTimeNetworkData<HasUnreadMessageNotificationResponse>(mEngine) {
            @Override
            protected BaseRequest<HasUnreadMessageNotificationResponse> createRequest() {
                return new HasNewMomentRequest();
            }
        }.asObservable();
    }

    /**
     * 获取动态列表
     *
     * @param type        举报类型 0-其他、1-不实信息、2-政治敏感、3-违法犯罪、4-金钱诈骗、5-侵犯未成年人、
     *                    6-垃圾广告、7-抄袭侵权、8-泄露隐私
     * @param contentType 内容类型：0-资源、1-评论、2-回复
     * @param mediaId     资源id、评论id
     */
    @NonNull
    public Observable<Object> report(int type, int contentType, String mediaId) {
        return new NetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                ReportRequest request = new ReportRequest();
                request.setParameters(type, contentType, mediaId);
                return request;
            }

            @Override
            protected void saveData(Object o) {

            }

        }.asObservable();
    }
}
