package com.jxntv.media.template.view.news.adapter;

import android.view.ViewGroup;

import androidx.databinding.library.baseAdapters.BR;

import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.R;
import com.jxntv.media.model.MediaModel;
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
