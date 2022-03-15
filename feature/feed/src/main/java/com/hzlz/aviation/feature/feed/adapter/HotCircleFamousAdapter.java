package com.hzlz.aviation.feature.feed.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.ItemHotCircleFamousBinding;
import com.hzlz.aviation.kernel.base.model.circle.CircleFamous;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.viewholder.HotCircleFamousViewHolder;

public class HotCircleFamousAdapter extends BaseRecyclerAdapter<CircleFamous, HotCircleFamousViewHolder> {

    public HotCircleFamousAdapter(Context context) {
        super(context);
    }

    @Override
    public HotCircleFamousViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new HotCircleFamousViewHolder(ItemHotCircleFamousBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(HotCircleFamousViewHolder hotCircleFamousViewHolder, int position) {
        ItemHotCircleFamousBinding binding = hotCircleFamousViewHolder.getBinding();
        if (binding == null) {
            return;
        }
        final CircleFamous circleFamous = mList.get(position);
        if (circleFamous == null) {
            return;
        }

        if (circleFamous.avatar != null && !TextUtils.isEmpty(circleFamous.avatar.url)) {
            Glide.with(mContext)
                    .load(circleFamous.avatar.url)
                    .centerCrop()
                    .into(binding.header);
        } else {
            Glide.with(mContext)
                    .load(ContextCompat.getDrawable(mContext, R.drawable.ic_default_avatar))
                    .centerCrop()
                    .into(binding.header);
        }

        if (circleFamous.isAuthentication) {
            binding.mark.setVisibility(View.VISIBLE);
        } else {
            binding.mark.setVisibility(View.GONE);
        }

    }
}
