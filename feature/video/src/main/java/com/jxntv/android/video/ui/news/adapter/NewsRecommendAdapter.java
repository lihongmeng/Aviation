package com.jxntv.android.video.ui.news.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jxntv.android.video.databinding.AdapterNewsRecommendBinding;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.AppManager;

/**
 * @author huangwei
 * date : 2021/5/20
 * desc : 新闻推荐
 **/

public class NewsRecommendAdapter extends BaseRecyclerAdapter<RecommendModel, BaseRecyclerViewHolder<AdapterNewsRecommendBinding>> {

    public NewsRecommendAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder<AdapterNewsRecommendBinding> onCreateVH(ViewGroup parent, int viewType) {

        return new BaseRecyclerViewHolder<>(AdapterNewsRecommendBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder<AdapterNewsRecommendBinding> binding, int position) {

        binding.getBinding().setModel(mList.get(position));

        binding.itemView.setOnClickListener(view -> {

            VideoModel model = VideoModel.Builder.aVideoModel()
                    .withId(mList.get(position).mediaId)
                    .withMediaType(mList.get(position).mediaType)
                    .withSpecialId(mList.get(position).specialId)
                    .build();

            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(binding.itemView.getContext(), model,null);

        });

    }


}
