package com.hzlz.aviation.feature.community.adapter;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.LayoutSearchTopicItemBinding;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;

public class SearchTopicAdapter extends BaseDataBindingAdapter<TopicDetail> {

    private Listener listener;

    public SearchTopicAdapter() {

    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        TopicDetail topicDetail = mDataList.get(position);
        if (topicDetail == null) {
            return;
        }
        LayoutSearchTopicItemBinding binding = (LayoutSearchTopicItemBinding) (holder.binding);
        if (binding == null) {
            return;
        }
        binding.content.setText(topicDetail.content);
        binding.content.setTag(topicDetail);
        binding.content.setOnClickListener(v -> {
            Object tag = v.getTag();
            if (tag == null || listener == null) {
                return;
            }
            listener.onItemClick(topicDetail);
        });
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_search_topic_item;
    }

    public interface Listener {
        void onItemClick(TopicDetail topicDetail);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
