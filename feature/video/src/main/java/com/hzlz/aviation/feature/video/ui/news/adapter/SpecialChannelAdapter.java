package com.hzlz.aviation.feature.video.ui.news.adapter;


import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.model.video.SpecialTagModel;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.AdapterSpecialChannnelBinding;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc : 专题详情标签
 **/
public class SpecialChannelAdapter extends BaseDataBindingAdapter<SpecialTagModel> {

    @Override
    protected int getItemLayoutId() {
        return R.layout.adapter_special_channnel;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {

        if (holder.binding instanceof AdapterSpecialChannnelBinding){
            ((AdapterSpecialChannnelBinding)holder.binding).channel.setText(mDataList.get(position).getName());
        }
        holder.itemView.setOnClickListener(view -> {
            if (listener!=null){
                listener.onItemRootViewClicked(mDataList.get(position));
            }
        });
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    private Listener listener;
    public interface Listener {

        void onItemRootViewClicked(SpecialTagModel tagModel);
    }
}
