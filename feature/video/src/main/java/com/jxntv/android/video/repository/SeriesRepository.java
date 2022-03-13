package com.jxntv.android.video.repository;

import com.jxntv.android.video.request.LoadSeriesRequest;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.response.ListWithPage;
import io.reactivex.rxjava3.core.Observable;

public class SeriesRepository extends BaseDataRepository {
  private static final int PAGE_SIZE = 10;
  public Observable<ListWithPage<VideoModel>> loadSeries(String columnId, int pageNum) {
    return new OneTimeNetworkData<ListWithPage<VideoModel>>(mEngine) {
      @Override protected BaseRequest<ListWithPage<VideoModel>> createRequest() {
        LoadSeriesRequest request = new LoadSeriesRequest();
        request.setColumnId(columnId);
        request.setPageNumber(pageNum);
        request.setPageSize(PAGE_SIZE);
        return request;
      }
    }.asObservable();
  }

}
