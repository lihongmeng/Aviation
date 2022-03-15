package com.hzlz.aviation.feature.share.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.feature.share.model.ShareItemModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.feature.share.R;
import com.hzlz.aviation.feature.share.databinding.LayoutShareItemBinding;
import com.hzlz.aviation.feature.share.databinding.LayoutShareItemBindingImpl;
import com.hzlz.aviation.feature.share.databinding.LayoutShareItemNoTextBinding;

/**
 * @author huangwei
 * date : 2021/12/3
 * desc :
 **/
public class ShareSlideAdapter extends BaseRecyclerAdapter<ShareItemModel, BaseRecyclerViewHolder> {

    private boolean mIsDarkMode = false;
    private final Runnable mDismissRunnable;

    private boolean hasBottomText;

    /**
     * 构造函数
     */
    public ShareSlideAdapter(Context context, boolean isDarkMode, Runnable dismissRunnable) {
        super(context);
        mIsDarkMode = isDarkMode;
        mDismissRunnable = dismissRunnable;
        hasBottomText = true;
    }

    /**
     * 构造函数
     */
    public ShareSlideAdapter(Context context,boolean hasBottomText, boolean isDarkMode, Runnable dismissRunnable) {
        super(context);
        mIsDarkMode = isDarkMode;
        mDismissRunnable = dismissRunnable;
        this.hasBottomText = hasBottomText;
    }


    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        if (hasBottomText) {
            return new ShareSlideVH(LayoutShareItemBinding.inflate(mInflater, parent, false));
        }else {
            return new ShareSlideVH(LayoutShareItemNoTextBinding.inflate(mInflater, parent, false));
        }
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder slideVH, int position) {
        ShareItemModel model = mList.get(position);
        if (slideVH.getBinding() instanceof LayoutShareItemBindingImpl) {
            LayoutShareItemBindingImpl binding = (LayoutShareItemBindingImpl) slideVH.getBinding();
            binding.shareMenuText.setText(model.title);
            binding.shareMenuIcon.setImageDrawable(ContextCompat.getDrawable(mContext, model.drawableRes));
            if (mIsDarkMode) {
                binding.shareMenuText.setTextColor(mContext.getResources().getColor(R.color.t_color05));
            }
        }else if (slideVH.getBinding() instanceof LayoutShareItemNoTextBinding){
            LayoutShareItemNoTextBinding binding = (LayoutShareItemNoTextBinding) slideVH.getBinding();
            binding.shareMenuIcon.setImageDrawable(ContextCompat.getDrawable(mContext, model.drawableRes));
        }

        slideVH.getBinding().getRoot().setOnClickListener(v -> {
            if (mDismissRunnable != null) {
                mDismissRunnable.run();
            }
            if (model.clickListener != null) {
                model.clickListener.onClick(v);
            }
        });
    }
}

/**
 * 横滑数据模型
 */
class ShareSlideVH extends BaseRecyclerViewHolder {
    public ShareSlideVH(ViewDataBinding binding) {
        super(binding);
    }
}