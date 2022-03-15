package com.hzlz.aviation.feature.video.repository;

import android.text.TextUtils;

import com.hzlz.aviation.feature.video.request.LoadRecommendRequest;
import com.hzlz.aviation.kernel.base.model.video.RecommendModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class RecommendRepository extends BaseDataRepository {

  public Observable<List<RecommendModel>> loadRecommend(String mediaId) {
    return loadRecommend(mediaId,1);
  }

  public Observable<List<RecommendModel>> loadRecommend(String mediaId, int pageNum) {
    return new OneTimeNetworkData<List<RecommendModel>>(mEngine) {
      @Override protected BaseRequest<List<RecommendModel>> createRequest() {
        LoadRecommendRequest request = new LoadRecommendRequest();
        request.setMediaId(mediaId);
        return request;
      }

      @Override protected void saveData(List<RecommendModel> recommendModels) {
        super.saveData(recommendModels);
        if (recommendModels != null && !recommendModels.isEmpty()) {
          for (RecommendModel recommendModel : recommendModels) {
            recommendModel.contentId = mediaId;
            recommendModel.extendType = TextUtils.isEmpty(recommendModel.actUrl) ? 0 : 1;
          }
        }
      }
    }.asObservable();
  }

}
