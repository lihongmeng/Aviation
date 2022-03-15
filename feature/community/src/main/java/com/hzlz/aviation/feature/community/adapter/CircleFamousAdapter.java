package com.hzlz.aviation.feature.community.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.BR;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.LayoutCircleFamousVerticalItemBinding;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.model.circle.CircleFamous;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;

public class CircleFamousAdapter extends BaseDataBindingAdapter<CircleFamous> {

    public CircleFamousAdapter() {
        super();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_circle_famous_vertical_item;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        if (context == null) {
            return;
        }
        CircleFamous circleFamous = mDataList.get(position);
        if (circleFamous == null) {
            return;
        }
        holder.bindData(BR.position, circleFamous.getModelPosition());

        LayoutCircleFamousVerticalItemBinding binding
                = (LayoutCircleFamousVerticalItemBinding) holder.binding;
        if (binding == null) {
            return;
        }

        binding.name.setText(circleFamous.nickname);

        if (circleFamous.avatar != null && !TextUtils.isEmpty(circleFamous.avatar.url)) {
            ImageLoaderManager.loadHeadImage(binding.header, circleFamous.avatar.url);
        } else {
            binding.header.setImageResource(R.drawable.ic_default_avatar);
        }

        if (circleFamous.isAuthentication) {
            binding.headerAuthentication.setVisibility(VISIBLE);
        } else {
            binding.headerAuthentication.setVisibility(View.GONE);
        }

        binding.root.setTag(new AuthorModel(circleFamous));
        binding.root.setOnClickListener(v -> {
            Object object = v.getTag();
            if (object == null) {
                return;
            }
            PluginManager.get(AccountPlugin.class).startPgcActivity(v, (AuthorModel) object);
        });

        if (circleFamous.roleType == Constant.CIRCLE_FAMOUS_TYPE.ADMIN) {
            binding.admin.setVisibility(VISIBLE);
            binding.admin.setText(R.string.admin);
        } else if (circleFamous.roleType == Constant.CIRCLE_FAMOUS_TYPE.CIRCLE_OWNER) {
            binding.admin.setVisibility(VISIBLE);
            binding.admin.setText(R.string.admin);
        } else {
            binding.admin.setVisibility(INVISIBLE);
        }

    }

}
