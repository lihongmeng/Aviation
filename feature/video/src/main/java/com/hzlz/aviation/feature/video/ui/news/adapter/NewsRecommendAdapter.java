package com.hzlz.aviation.feature.video.ui.news.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.hzlz.aviation.kernel.base.model.video.RecommendModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.video.databinding.AdapterNewsRecommendBinding;

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
