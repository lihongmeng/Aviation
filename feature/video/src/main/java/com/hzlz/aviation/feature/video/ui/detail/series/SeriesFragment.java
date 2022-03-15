package com.hzlz.aviation.feature.video.ui.detail.series;

import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_ITEM;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.video.ui.detail.DetailFragment;
import com.hzlz.aviation.feature.video.ui.detail.DetailViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;

public class SeriesFragment extends DetailFragment<VideoModel> {
  private SeriesViewModel mSeriesViewModel;

  private String getColumnId() {
    String columnId = getArguments() != null ? getArguments().getString(Constant.EXTRA_COLUMN_ID) : "";
    return columnId;
  }

  @NonNull @Override protected BaseRecyclerAdapter<VideoModel, ?> createAdapter() {
    SeriesAdapter adapter = new SeriesAdapter(requireContext());
    adapter.mActionLiveData.observe(this,
        VideoModelActionModel -> {
          if (VideoModelActionModel.type == ACTION_ITEM) {
            String fromPid = getArguments() != null ? getArguments().getString(Constant.EXTRA_FROM_PID) : null;
            mSeriesViewModel.navigateToVideoFragment(mRecyclerView, VideoModelActionModel.model, fromPid);
          }
        });
    return adapter;
  }

  @Override protected void bindViewModels() {
    mSeriesViewModel = bingViewModel(SeriesViewModel.class);
    mSeriesViewModel.setMediaId(getColumnId());
    mSeriesViewModel.getNoMoreLiveData().observe(this, new Observer<Boolean>() {
      @Override public void onChanged(Boolean noMoreData) {
        mRefreshLayout.setNoMoreData(noMoreData);
      }
    });
  }

  @NonNull @Override protected DetailViewModel<VideoModel> getDetailViewModel() {
    return mSeriesViewModel;
  }
}
