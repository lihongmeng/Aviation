package com.jxntv.base.view.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

/**
 * 可添加header footer  recyclerViewAdapter基类
 *
 */
public abstract class BaseRecyclerHeaderFooterAdapter<T, VH extends BaseRecyclerViewHolder>  extends BaseRecyclerAdapter<T, VH> {

    private final int HEADER_VIEW_TYPE = 579999999;
    private final int FOOTER_VIEW_TYPE = 579999998;
    private List<View> mHeaderViewList;
    private List<View> mFooterViewList;

    public BaseRecyclerHeaderFooterAdapter(Context context) {
        super(context);
        mHeaderViewList = new ArrayList<>();
        mFooterViewList = new ArrayList<>();
    }


    @Override
    public int getItemCount() {
        return getCount() + mHeaderViewList.size() + mFooterViewList.size();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            return (VH) new HeaderViewHolder(linearLayout);
        } else if (viewType == FOOTER_VIEW_TYPE) {
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            return (VH) new FooterViewHolder(linearLayout);
        } else {
            return onCreateVH(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int position) {
        if (HEADER_VIEW_TYPE == getItemViewType(position)) {
            ((HeaderViewHolder)vh).bindItem(position);
        } else if (FOOTER_VIEW_TYPE == getItemViewType(position)) {
            ((FooterViewHolder)vh).bindItem(position - mHeaderViewList.size() - getCount());
        } else {
            int norPosition = position - mHeaderViewList.size();
            onBindVH(vh, norPosition);
        }
    }

    public int getCount() {
        return mList.size();
    }

    /**
     * 创建 View Holder
     *
     * @param parent   父视图
     * @param viewType item类型
     * @return ViewHolder
     */
    public abstract VH onCreateVH(ViewGroup parent, int viewType);

    /**
     * 绑定 View Holder
     *
     * @param vh       view holder
     * @param position 对应位置
     */
    public abstract void onBindVH(VH vh, int position);

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaderViewList.size()) {
            return HEADER_VIEW_TYPE;
        } else if (position >= mHeaderViewList.size() + getCount()) {
            return FOOTER_VIEW_TYPE;
        } else {
            return super.getItemViewType(position - mHeaderViewList.size());
        }
    }

    public int getFooterViewCount() {
        return mFooterViewList.size();
    }

    public int getHeaderViewCount() {
        return mHeaderViewList.size();
    }

    public void addHeaderView(View view) {
        mHeaderViewList.add(view);
        notifyDataSetChanged();
    }

    public void addFooterView(View view) {
        mFooterViewList.add(view);
        notifyDataSetChanged();
    }


    public void removeHeaderView(View view) {
        for (int i = 0; i < mHeaderViewList.size(); i++) {
            if (view == mHeaderViewList.get(i)) {
                mHeaderViewList.remove(i);
                i--;
            }
        }
        notifyDataSetChanged();
    }

    public void removeFooterView(View view) {
        for (int i = 0; i < mFooterViewList.size(); i++) {
            if (view == mFooterViewList.get(i)) {
                mFooterViewList.remove(i);
                i--;
            }
        }
        notifyDataSetChanged();
    }

    class HeaderViewHolder extends BaseRecyclerViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        public void bindItem(int position) {
            LinearLayout container = (LinearLayout) itemView;
            container.removeAllViews();
            if (null != mHeaderViewList.get(position).getParent()) {
                LinearLayout parent = (LinearLayout) mHeaderViewList.get(position).getParent();
                parent.removeAllViews();
            }
            container.addView(mHeaderViewList.get(position));
        }
    }

    class FooterViewHolder extends BaseRecyclerViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }

        public void bindItem(int position) {
            LinearLayout container = (LinearLayout) itemView;
            container.removeAllViews();
            if (null != mFooterViewList.get(position).getParent()) {
                LinearLayout parent = (LinearLayout) mFooterViewList.get(position).getParent();
                parent.removeAllViews();
            }
            container.addView(mFooterViewList.get(position));
        }
    }


}
