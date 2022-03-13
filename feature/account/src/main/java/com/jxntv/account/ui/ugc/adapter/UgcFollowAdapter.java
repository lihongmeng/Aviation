package com.jxntv.account.ui.ugc.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jxntv.account.BR;
import com.jxntv.account.databinding.ViewItemAdapterFollowBinding;
import com.jxntv.account.model.UgcAuthorModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.ioc.PluginManager;

/**
 * @author huangwei
 * date : 2021/6/11
 * desc :  Ta关注的人
 **/
public class UgcFollowAdapter extends BaseRecyclerAdapter<UgcAuthorModel, BaseRecyclerViewHolder> {

    public UgcFollowAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {

        ViewItemAdapterFollowBinding vh = ViewItemAdapterFollowBinding.inflate(mInflater,
                parent, false);

        return new BaseRecyclerViewHolder(vh);
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {

        holder.getBinding().setVariable(BR.model,mList.get(position));

        holder.itemView.setTag(mList.get(position));
        holder.itemView.setOnClickListener(view -> PluginManager.get(AccountPlugin.class).startPgcActivity(holder.itemView, (AuthorModel) holder.itemView.getTag()));

    }
}