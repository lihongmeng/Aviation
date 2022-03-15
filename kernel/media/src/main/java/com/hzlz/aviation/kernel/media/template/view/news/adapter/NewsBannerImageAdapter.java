package com.hzlz.aviation.kernel.media.template.view.news.adapter;

import android.view.ViewGroup;

import androidx.databinding.library.baseAdapters.BR;

import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/12/2
 * desc :
 **/
public class NewsBannerImageAdapter extends BannerAdapter<MediaModel, DataBindingViewHolder> {

    public NewsBannerImageAdapter(List<MediaModel> datas) {
        super(datas);
    }

    @Override
    public DataBindingViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder(parent, R.layout.news_tpl_adapter_banner_image);
    }

    @Override
    public void onBindView(DataBindingViewHolder holder, MediaModel data, int position, int size) {

        holder.binding.setVariable(BR.feedModel, data);
        holder.itemView.setOnClickListener(view -> {
            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(holder.itemView.getContext(), data,
                            null);
        });
    }
}
