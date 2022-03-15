package com.hzlz.aviation.feature.account.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.BR;
import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.AvatarInfo;
import com.hzlz.aviation.feature.account.model.AvatarInfoObservable;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;

/**
 * 头像适配器
 *
 * @since 2020-02-24 20:51
 */
public final class AvatarAdapter extends BaseDataBindingAdapter<AvatarInfo> {
    //<editor-fold desc="属性">
    @Nullable
    private Listener mListener;

    private int currentSelectPosition;

    //</editor-fold>

    //<editor-fold desc="构造函数">
    public AvatarAdapter() {
        super();
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected int getItemLayoutId() {
        return R.layout.adapter_avatar;
    }

    public void setCurrentSelectPosition(int currentSelectPosition) {
        this.currentSelectPosition = currentSelectPosition;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        AvatarInfo info = mDataList.get(position);
        holder.bindData(BR.adapter, this);
        holder.bindData(BR.position, info.getModelPosition());
        holder.bindData(BR.avatarInfo, info.getAvatarInfoObservable());
    }

    @Override
    public void onBindViewHolder(@NonNull DataBindingViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    //</editor-fold>

    //<editor-fold desc="事件绑定">
    public void onItemClicked(@NonNull View view, int position) {

        if (position != currentSelectPosition) {
            AvatarInfoObservable avatarInfo = mDataList.get(currentSelectPosition).getAvatarInfoObservable();
            avatarInfo.checkVisibility.set(View.GONE);

            currentSelectPosition = position;
            AvatarInfoObservable currentAvatarInfo = mDataList.get(currentSelectPosition).getAvatarInfoObservable();
            currentAvatarInfo.checkVisibility.set(View.VISIBLE);
        }

        if (mListener != null) {
            mListener.onItemClick(view, this, position);
        }

    }
    //</editor-fold>

    //<editor-fold desc="接口">
    public interface Listener {
        void onItemClick(@NonNull View view, @NonNull AvatarAdapter adapter, int position);
    }
    //</editor-fold>
}
