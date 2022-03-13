package com.jxntv.android.video.ui.news.adapter;



import androidx.annotation.NonNull;

import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.AdapterSpecialChannnelBinding;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.model.video.SpecialTagModel;
import com.jxntv.base.utils.StringUtils;

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
