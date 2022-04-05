package com.hzlz.aviation.feature.live.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.live.databinding.AdapterLiveChatBinding;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCChatEntity;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc : 直播评论适配器
 **/
public class LiveChatAdapter extends BaseDataBindingAdapter<TCChatEntity> {

    public LiveChatAdapter() {
        mEnablePlaceholder = false;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        if (holder.binding instanceof AdapterLiveChatBinding) {
            AdapterLiveChatBinding binding = (AdapterLiveChatBinding) holder.binding;
            TCChatEntity chatEntity = getData().get(position);
            binding.content.clear();

            String senderName = chatEntity.getSenderName();
            if (TextUtils.isEmpty(chatEntity.grpSendName)) {
                binding.content.addText(chatEntity.getContent(), R.color.color_f0c871, null);
            } else {
                binding.content
                        .addText(senderName, R.color.color_8be6fe, null)
                        .addText(chatEntity.getContent(), R.color.color_ffffff, null);
            }
            binding.content.showText();
        }
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.adapter_live_chat;
    }


    public void addData(TCChatEntity chatEntity) {
        if (TextUtils.isEmpty(chatEntity.getContent()) && TextUtils.isEmpty(chatEntity.getSenderName())) {
            return;
        }
        if (getData().size() > 999) {
            removeData(0);
        }
        List<TCChatEntity> chatEntities = new ArrayList<>();
        chatEntities.add(chatEntity);
        super.addData(chatEntities);
    }

}
