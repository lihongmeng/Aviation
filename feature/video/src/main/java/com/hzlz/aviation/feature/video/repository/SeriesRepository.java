package com.hzlz.aviation.feature.video.repository;

import com.hzlz.aviation.feature.video.request.LoadSeriesRequest;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

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
