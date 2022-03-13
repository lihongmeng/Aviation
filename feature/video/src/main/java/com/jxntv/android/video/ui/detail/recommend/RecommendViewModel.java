package com.jxntv.android.video.ui.detail.recommend;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;

import com.jxntv.android.video.repository.RecommendRepository;
import com.jxntv.android.video.ui.detail.DetailModelList;
import com.jxntv.android.video.ui.detail.DetailViewModel;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.android.video.VideoHelper;
import com.jxntv.stat.StatPid;
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
