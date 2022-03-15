package com.hzlz.aviation.feature.video.ui.detail.recommend;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.video.VideoHelper;
import com.hzlz.aviation.feature.video.repository.RecommendRepository;
import com.hzlz.aviation.feature.video.ui.detail.DetailModelList;
import com.hzlz.aviation.feature.video.ui.detail.DetailViewModel;
import com.hzlz.aviation.kernel.base.model.video.RecommendModel;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

import io.reactivex.rxjava3.core.Observable;

public class RecommendViewModel extends DetailViewModel<RecommendModel> {
  private RecommendRepository repository = new RecommendRepository();

  public RecommendViewModel(@NonNull Application application) {
    super(application);
  }

  @Override protected Observable<DetailModelList<RecommendModel>> createLoadObservable(String mediaId,
      int page) {
    return repository.loadRecommend(mediaId, page).map(recommendResponse -> {
          if (recommendResponse == null) return null;
          DetailModelList<RecommendModel> list = new DetailModelList<>(recommendResponse,
              false);
          return list;
        });
  }

  public void navigateToVideoFragment(View view, RecommendModel recommendModel, String fromPid) {
    VideoHelper.handleAdvertClick(
            view,
            recommendModel,
            TextUtils.equals(fromPid, StatPid.DETAIL)
    );
  }

}
