package com.jxntv.account.ui.ugc.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

import com.jxntv.account.BR;
import com.jxntv.account.databinding.ViewItemAdapterCircleBinding;
import com.jxntv.account.model.UgcAuthorModel;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.ioc.PluginManager;

/**
 * @author huangwei
 * date : 2021/6/11
 * desc : Ta加入的圈子
 **/
@SuppressWarnings({"unchecked", "rawtypes"})
public class UgcCircleAdapter extends BaseRecyclerAdapter<UgcAuthorModel, BaseRecyclerViewHolder> {

    public UgcCircleAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(
                ViewItemAdapterCircleBinding.inflate(mInflater, parent, false)
        );
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {
        ViewDataBinding binding= holder.getBinding();
        if(binding==null){
            return;
        }
        binding.setVariable(BR.model,mList.get(position));
        holder.itemView.setOnClickListener(view -> {
            Circle circle = new Circle();
            circle.groupId = Long.parseLong(mList.get(position).getGroupId());
            PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(
                    holder.itemView.getContext(),
                    circle,
                    null
            );
        });
    }

}
