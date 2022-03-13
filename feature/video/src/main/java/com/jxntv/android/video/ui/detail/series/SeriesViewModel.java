package com.jxntv.android.video.ui.detail.series;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;

import com.jxntv.android.video.repository.SeriesRepository;
import com.jxntv.android.video.ui.detail.DetailModelList;
import com.jxntv.android.video.ui.detail.DetailViewModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.android.video.VideoHelper;
import com.jxntv.stat.StatPid;
import io.reactivex.rxjava3.core.Observable;

public class SeriesViewModel extends DetailViewModel<VideoModel> {
  private SeriesRepository repository = new SeriesRepository();

  public SeriesViewModel(@NonNull Application application) {
    super(application);
  }

  @Override
  protected Observable<DetailModelList<VideoModel>> createLoadObservable(String columnId, int page) {
    //此处传入的mediaId 为 对应资源的columnId
    return repository.loadSeries(columnId, page).map(seriesResponse -> {
          if (seriesResponse == null) return null;
          DetailModelList<VideoModel> list = new DetailModelList<>(seriesResponse.getList(),
              seriesResponse.getPage().hasNextPage());
          return list;
        });
  }

  public void navigateToVideoFragment(View view, VideoModel videoModel, String fromPid) {
    boolean fromDetail = TextUtils.equals(fromPid, StatPid.DETAIL);
    VideoHelper.handleSeriesClick(
            view,
            videoModel,
            fromDetail
    );
  }
}
