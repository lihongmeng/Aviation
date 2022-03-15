package com.hzlz.aviation.kernel.media.template.view.news.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.kernel.media.BR;
import com.hzlz.aviation.kernel.media.databinding.NewsTplAdapterSpecialTextItemBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.library.ioc.PluginManager;

/**
 * @author huangwei
 * date : 2021/5/27
 * desc : 文字专题新闻
 **/
public class NewSpecialTextAdapter extends BaseRecyclerAdapter<MediaModel, BaseRecyclerViewHolder> {

    public NewSpecialTextAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {

        NewsTplAdapterSpecialTextItemBinding vh = NewsTplAdapterSpecialTextItemBinding.inflate(mInflater,
                parent, false);

        return new BaseRecyclerViewHolder(vh);
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {

        holder.getBinding().setVariable(BR.feedModel, mList.get(position));
        holder.itemView.setOnClickListener(view -> {
            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(holder.itemView.getContext(),
                    mList.get(position), null);
        });

    }

}