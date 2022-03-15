package com.hzlz.aviation.feature.account.ui.ugc.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.hzlz.aviation.feature.account.BR;
import com.hzlz.aviation.feature.account.databinding.ViewItemAdapterFollowBinding;
import com.hzlz.aviation.feature.account.model.UgcAuthorModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.ioc.PluginManager;

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