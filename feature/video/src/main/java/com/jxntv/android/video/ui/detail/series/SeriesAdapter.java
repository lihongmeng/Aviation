package com.jxntv.android.video.ui.detail.series;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.jxntv.android.video.databinding.VideoSuperSeriesItemBinding;
import com.jxntv.android.video.ui.detail.DetailAdapter;
import com.jxntv.android.video.ui.viewholder.SeriesViewHolder;
import com.jxntv.base.model.video.VideoModel;

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
