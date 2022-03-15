package com.hzlz.aviation.feature.account.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.request.DeleteCompositionRequest;
import com.hzlz.aviation.feature.account.request.PublishRequest;
import com.hzlz.aviation.kernel.base.plugin.ICompositionRepository;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;

public final class CompositionRepository extends BaseDataRepository implements
        ICompositionRepository {
    @NonNull
    @Override
    public Observable<Boolean> deleteMedia(@NonNull String mediaId) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                DeleteCompositionRequest request = new DeleteCompositionRequest();
                request.setMediaId(mediaId);
                return request;
            }
        }.asObservable().map(o -> {
            if (o != null) {
                GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE, String.class).post(mediaId);
            }
            return o != null;
        });
    }

    @Override
    public Observable<Integer> publish(
            boolean isPublic,
            String introduction,
            String imageId,
            String videoId,
            String gatherId
    ) {
        return new OneTimeNetworkData<Integer>(mEngine) {
            @Override
            protected BaseRequest<Integer> createRequest() {
                PublishRequest request = new PublishRequest();
                request.setPublic(isPublic);
                request.setIntroduction(TextUtils.isEmpty(introduction) ? "" : introduction);
                request.setIntroduction(TextUtils.isEmpty(imageId) ? "" : imageId);
                request.setIntroduction(TextUtils.isEmpty(videoId) ? "" : videoId);
                request.setIntroduction(TextUtils.isEmpty(gatherId) ? "" : gatherId);
                return request;
            }
        }.asObservable();
    }

}
