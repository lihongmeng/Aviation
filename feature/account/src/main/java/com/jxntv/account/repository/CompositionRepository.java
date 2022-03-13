package com.jxntv.account.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.account.request.DeleteCompositionRequest;
import com.jxntv.account.request.PublishRequest;
import com.jxntv.base.plugin.ICompositionRepository;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

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
