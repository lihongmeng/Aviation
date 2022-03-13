package com.jxntv.account.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.account.BR;
import com.jxntv.account.R;
import com.jxntv.account.databinding.ItemHomeFollowBinding;
import com.jxntv.account.model.Author;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;

public class HomeFollowAdapter extends BaseDataBindingAdapter<Author> {

    private Listener listener;

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_home_follow;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        if (context == null) {
            return;
        }
        Author author = mDataList.get(position);
        if (author == null) {
            return;
        }
        holder.bindData(BR.position, author.getModelPosition());
        holder.bindData(BR.adapter, this);

        ItemHomeFollowBinding binding = (ItemHomeFollowBinding) (holder.binding);
        if (binding == null) {
            return;
        }

        // 头像
        String avatar = author.getAvatar();
        if (avatar != null && !TextUtils.isEmpty(avatar)) {
            ImageLoaderManager.loadHeadImage(binding.header, avatar);
        } else {
            binding.header.setImageResource(R.drawable.ic_default_avatar);
        }

        // 点击item默认跳转到个人主页
        binding.rootLayout.setTag(author);
        binding.rootLayout.setOnClickListener(view -> startPersonalInfo(view));

        // 名称
        String name = author.getName();
        if (TextUtils.isEmpty(name)) {
            binding.name.setText("");
        } else {
            binding.name.setText(name);
        }


        String authenticationIntro = "";
        boolean isAuthentication = author.isAuthentication();
        if (isAuthentication) {
            authenticationIntro = author.getAuthenticationIntro();
        } else {
            authenticationIntro = author.getIntro();
        }
        if (TextUtils.isEmpty(authenticationIntro)) {
            binding.introduction.setText("");
        } else {
            binding.introduction.setText(authenticationIntro);
        }
        binding.headerAuthentication.setVisibility(isAuthentication ? View.VISIBLE : View.GONE);

        // 关注取关按钮
        binding.followCancel.setTag(author);
        binding.followCancel.setOnClickListener(view -> {
            if (listener == null) {
                return;
            }
            listener.onFollowCancelClick(view, (Author) view.getTag());
        });
        binding.followCancel.setSelected(author.isFollow());
    }

    private void startPersonalInfo(View view) {
        Object object = view.getTag();
        if (object == null) {
            return;
        }
        PluginManager.get(AccountPlugin.class).startPgcActivity(view, (Author) object);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onFollowCancelClick(View view, Author author);
    }

    public boolean hasFollowAtLeastOne() {
        if (mDataList.isEmpty()) {
            return false;
        }
        for (Author author : mDataList) {
            if (author == null || !author.isFollow()) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 单独更新数据源中的一项
     *
     * @param target   目标数据
     * @param isFollow 想要更改的数据
     */
    public void updateSingle(Author target, boolean isFollow) {
        if (target == null || mDataList.isEmpty()) {
            return;
        }
        String targetId = target.getId();
        if (TextUtils.isEmpty(targetId)) {
            return;
        }
        for (int index = 0; index < mDataList.size(); index++) {
            Author author = mDataList.get(index);
            if (author == null || !targetId.equals(author.getId())) {
                continue;
            }
            author.setFollow(isFollow);
            notifyItemChanged(index);
            return;
        }
    }


}
