package com.hzlz.aviation.feature.account.ui.ugc.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.feature.account.BR;
import com.hzlz.aviation.feature.account.databinding.ViewItemAdapterCircleBinding;
import com.hzlz.aviation.feature.account.model.UgcAuthorModel;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.ioc.PluginManager;

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
