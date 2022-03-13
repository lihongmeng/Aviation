package com.jxntv.base.plugin;

import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.network.response.ListWithPage;

import io.reactivex.rxjava3.core.Observable;

public interface MediaRepositoryInterface {

    Observable<VideoModel> loadMedia(String mediaId);

}
