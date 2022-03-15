package com.hzlz.aviation.kernel.base.plugin;

import com.hzlz.aviation.kernel.base.model.video.VideoModel;

import io.reactivex.rxjava3.core.Observable;

public interface MediaRepositoryInterface {

    Observable<VideoModel> loadMedia(String mediaId);

}
