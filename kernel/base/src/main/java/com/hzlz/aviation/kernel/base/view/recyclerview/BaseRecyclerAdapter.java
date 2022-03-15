package com.hzlz.aviation.kernel.base.view.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * recyclerViewAdapter基类
 *
 * @since 2020.1.17
 */
public abstract class BaseRecyclerAdapter<T, VH extends BaseRecyclerViewHolder>
        extends RecyclerView.Adapter<VH> {

    /** 当前持有的上下文对象 */
    protected Context mContext;
    /** 当前持有数据源 */
    protected List<T> mList;
    /** 持有的layoutInflater */
    protected LayoutInflater mInflater;

    private RecyclerView mRecyclerView;

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.mInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return onCreateVH(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int position) {
        onBindVH(vh, position);
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
        return super.getItemViewType(position);
    }

    /**
     * 刷新数据
     *
     * @param data 数据源
     */
    public void refreshData(@NonNull List<T> data) {
        refreshDataWithDiff(data);
    }

    /**
     * 加载更多到列表顶部
     *
     * @param data 加载的新数据
     */
    public void loadMoreData(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.addAll(0, mList);
        refreshDataWithDiff(data);
    }


    /**
     * 加载更多到列表底部
     *
     * @param data 加载的新数据
     */
    public void addMoreData(List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.addAll(mList);
        refreshDataWithDiff(data);
    }

    /**
     * 刷新data数据
     *
     * @param data 加载的新数据
     */
    private void refreshDataWithDiff(List<T> data) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() {
                return mList.size();
            }

            @Override
            public int getNewListSize() {
                return data == null ? 0 : data.size();
            }

            @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                T oldItem = mList.get(oldItemPosition);
                T newItem = data.get(newItemPosition);
                return oldItem == newItem || (oldItem != null && oldItem.hashCode() == newItem.hashCode());
            }

            @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                T oldItem = mList.get(oldItemPosition);
                T newItem = data.get(newItemPosition);
                return Objects.equals(oldItem, newItem);
            }
        });

        mList.clear();
        mList.addAll(data);

        result.dispatchUpdatesTo(new ListUpdateCallback() {
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position + getHeaderViewCount(),count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position+ getHeaderViewCount(),count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition + getHeaderViewCount(),toPosition + getHeaderViewCount());
            }

            @Override
            public void onChanged(int position, int count, @Nullable Object payload) {
                notifyItemRangeChanged(position + getHeaderViewCount(),count,payload);
            }
        });
    }

    public int getFooterViewCount() {
        return 0;
    }

    public int getHeaderViewCount() {
        return 0;
    }

    /**
     * 清空数据
     */
    public void clearList() {
        int size = mList.size();
        mList.clear();
        notifyItemRangeRemoved(0, size);
    }

}
