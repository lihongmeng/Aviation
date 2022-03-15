package com.hzlz.aviation.feature.video.ui.detail.series;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hzlz.aviation.feature.video.ui.detail.DetailAdapter;
import com.hzlz.aviation.feature.video.ui.viewholder.SeriesViewHolder;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.feature.video.databinding.VideoSuperSeriesItemBinding;

public class SeriesAdapter extends DetailAdapter<VideoModel, SeriesViewHolder> {
  public SeriesAdapter(Context context) {
    super(context);
  }

  @Override public SeriesViewHolder onCreateVH(ViewGroup parent, int viewType) {
    SeriesViewHolder vh = new SeriesViewHolder(
        VideoSuperSeriesItemBinding.inflate(mInflater, parent, false));
    return vh;
  }

  @Override public void onBindVH(SeriesViewHolder holder, int position) {
    VideoModel VideoModel = mList.get(position);

    holder.getBinding().setSeries(VideoModel);
    holder.getBinding().setAdapter(this);
    holder.getBinding().executePendingBindings();
  }
  public void onItemClick(View v, VideoModel VideoModel) {
    mActionLiveData.postValue(new ActionModel<>(VideoModel, ACTION_ITEM));
  }
}
