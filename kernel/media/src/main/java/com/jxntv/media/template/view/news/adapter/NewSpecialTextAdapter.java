package com.jxntv.media.template.view.news.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.BR;
import com.jxntv.media.databinding.NewsTplAdapterSpecialTextItemBinding;
import com.jxntv.media.model.MediaModel;
import com.jxntv.utils.AppManager;

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