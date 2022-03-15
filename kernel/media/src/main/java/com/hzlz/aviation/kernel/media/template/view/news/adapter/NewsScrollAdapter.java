package com.hzlz.aviation.kernel.media.template.view.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.library.baseAdapters.BR;

import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.kernel.media.databinding.NewsTplAdapterScrollItemBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/5/27
 * desc : 滚动新闻
 **/
public class NewsScrollAdapter extends BannerAdapter<MediaModel, BaseRecyclerViewHolder> {

    public NewsScrollAdapter(List<MediaModel> data) {
        super(data);
    }

    @Override
    public BaseRecyclerViewHolder onCreateHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = (LayoutInflater) parent.getContext().getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        NewsTplAdapterScrollItemBinding vh = NewsTplAdapterScrollItemBinding.inflate(mInflater,
                parent, false);

        return new BaseRecyclerViewHolder(vh);
    }

    @Override
    public void onBindView(BaseRecyclerViewHolder holder, MediaModel data, int position, int size) {

        holder.getBinding().setVariable(BR.feedModel,data);

    }
}
