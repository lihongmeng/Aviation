package com.hzlz.aviation.feature.live.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.live.LiveManager;
import com.hzlz.aviation.feature.live.databinding.AdapterAudienceAvatarBinding;
import com.hzlz.aviation.feature.live.liveroom.live.entity.TCSimpleUserInfo;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.feature.live.BR;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc : 直播评论适配器
 **/
public class AudienceAvatarAdapter extends BaseDataBindingAdapter<TCSimpleUserInfo> {


    public AudienceAvatarAdapter() {
        mEnablePlaceholder = false;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        if (holder.binding instanceof AdapterAudienceAvatarBinding) {
            AdapterAudienceAvatarBinding binding = (AdapterAudienceAvatarBinding) holder.binding;
            if (TextUtils.isEmpty(getData().get(position).avatar)) {
                binding.avatar.setImageResource(R.drawable.ic_default_avatar);
            } else {
                holder.bindData(BR.userInfoModel, getData().get(position));
            }

            binding.avatar.setVisibility((getData().size()-1 - position)< 3 ? View.VISIBLE:View.GONE);
        }
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.adapter_audience_avatar;
    }

    public void addItem(TCSimpleUserInfo userInfo) {
        //过滤主播自己
        if (userInfo.userid.equals(LiveManager.getInstance().getUserId())) {
            return;
        }
        List<TCSimpleUserInfo> userInfos = new ArrayList<>();
        userInfos.add(userInfo);
        addData(userInfos);
        notifyDataSetChanged();
    }

    public void removeItem(TCSimpleUserInfo userInfo) {
        if (getData() == null) return;
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).userid.equals(userInfo.userid)) {
                removeData(i);
                break;
            }
        }
        hideNoMoreData();
        notifyDataSetChanged();
    }

    public void checkAvatar(int currentCount) {
        //保证显示人数和头像数量对应
        if (currentCount < 3) {
            if (currentCount < getData().size()) {
                int count = getData().size() - currentCount;
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        removeData(i);
                    }
                }
            } else if (currentCount > getData().size()) {
                int count = currentCount - getData().size();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        List<TCSimpleUserInfo> userInfos = new ArrayList<>();
                        TCSimpleUserInfo tcSimpleUserInfo = new TCSimpleUserInfo();
                        tcSimpleUserInfo.nickname = "游客";
                        userInfos.add(tcSimpleUserInfo);
                        addData(userInfos);
                    }
                }
            }
        }
    }

}
