package com.jxntv.base.adapter;

import android.util.SparseIntArray;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据绑定多种 Item 类型的 Adapter 基类
 *
 * @since 2020-01-16 16:57
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseMultiItemDataBindingAdapter<T extends IAdapterModel>
        extends RecyclerView.Adapter<DataBindingViewHolder> {
    //<editor-fold desc="属性">
    @NonNull
    protected ArrayList<T> mDataList;
    @NonNull
    private SparseIntArray mItemTypes = new SparseIntArray(4);
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public BaseMultiItemDataBindingAdapter() {
        mDataList = new ArrayList<>();
    }
    //</editor-fold>

    //<editor-fold desc="抽象方法">

    /**
     * 绑定数据
     *
     * @param holder   ViewHolder
     * @param position 当前位置
     */
    protected abstract void bindData(@NonNull DataBindingViewHolder holder, int position);
    //</editor-fold>

    //<editor-fold desc="内部实现">

    /**
     * 添加 Item 类型
     *
     * @param type        Item 类型
     * @param layoutResId 布局 id
     */
    protected void addItemType(int type, @LayoutRes int layoutResId) {
        mItemTypes.put(type, layoutResId);
    }

    /**
     * 检测位置
     *
     * @param position 位置
     * @return true : 位置在列表之内
     */
    protected boolean checkPosition(int position) {
        return checkPosition(mDataList, position);
    }

    /**
     * 检测位置
     *
     * @param dataList 数据列表
     * @param position 位置
     * @return true : 位置在列表之内
     */
    protected boolean checkPosition(@NonNull List dataList, int position) {
        return position >= 0 && position < dataList.size();
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @NonNull
    @Override
    public DataBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = mItemTypes.get(viewType);
        if (layoutResId == 0) {
            throw new NullPointerException("cannot find layout id for the viewType : " + viewType);
        }
        return new DataBindingViewHolder(parent, layoutResId);
    }

    @Override
    public void onBindViewHolder(@NonNull DataBindingViewHolder holder, int position) {
        bindData(holder, position);
        holder.binding.executePendingBindings();
    }

    @Override
    public final void onBindViewHolder(
            @NonNull DataBindingViewHolder holder,
            int position,
            @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }
    //</editor-fold>

    //<editor-fold desc="API">

    /**
     * 获取数据列表
     *
     * @return 数据列表
     */
    public List<T> getData() {
        return mDataList;
    }
    // 只有 add, remove, move, replace, 没有 update, update 通过数据绑定完成

    /**
     * 添加数据
     *
     * @param dataList 数据列表
     */
    public void addData(@Nullable List<T> dataList) {
        addData(mDataList.size(), dataList);
    }

    /**
     * 添加数据
     *
     * @param index    数据的位置
     * @param dataList 数据列表
     */
    public void addData(int index, @Nullable List<T> dataList) {
        if (index < 0 || index > mDataList.size() || dataList == null || dataList.isEmpty()) {
            return;
        }
        // 扩容
        int size = dataList.size();
        mDataList.ensureCapacity(mDataList.size() + size);
        // 添加数据
        mDataList.addAll(index, dataList);
        // 更新数据模型的 position
        size = mDataList.size();
        for (int i = index; i < size; i++) {
            mDataList.get(i).setModelPosition(i);
        }
        // 通知 Adapter 数据发生变化
        notifyItemRangeInserted(index, dataList.size());
    }

    /**
     * 移除数据
     *
     * @param index 数据在列表中的下标
     */
    public void removeData(int index) {
        removeData(index, 1);
    }

    /**
     * 移除数据
     *
     * @param index 数据在列表中的下标
     * @param count 移除的数量
     */
    public void removeData(int index, int count) {
        int size = mDataList.size();
        if (index < 0 || index > size - 1 || index + count > size) {
            return;
        }
        // 移除数据
        for (int i = 0; i < count; i++) {
            mDataList.remove(index);
        }
        size = mDataList.size();
        // 更新数据模型的 position
        for (int i = index; i < size; i++) {
            mDataList.get(i).setModelPosition(i);
        }
        // 通知 Adapter 数据移除
        notifyItemRangeRemoved(index, count);
    }

    /**
     * 移动数据
     *
     * @param fromIndex from 下标
     * @param toIndex   to 下标
     */
    public void moveData(int fromIndex, int toIndex) {
        int lastIndex = mDataList.size() - 1;
        if (fromIndex < 0
                || fromIndex > lastIndex
                || toIndex < 0
                || toIndex > lastIndex
                || fromIndex == toIndex) {
            return;
        }
        // 移动数据
        T from = mDataList.get(fromIndex);
        T to = mDataList.get(toIndex);
        mDataList.set(fromIndex, to);
        mDataList.set(toIndex, from);
        // 更新数据模型的 position
        from.setModelPosition(toIndex);
        to.setModelPosition(fromIndex);
        // 通知 Adapter 数据发生变化
        notifyItemMoved(fromIndex, toIndex);
    }

    /**
     * 替换数据
     *
     * @param dataList 数据列表
     */
    public void replaceData(@Nullable List<T> dataList) {
        // 判断列表是否为空
        int oldSize = mDataList.size();
        int newSize = dataList == null ? 0 : dataList.size();
        if (oldSize == 0 && newSize == 0) {
            return;
        }
        // 替换数据
        mDataList.clear();
        if (newSize > 0) {
            mDataList.ensureCapacity(newSize);
            mDataList.clear();
            mDataList.addAll(dataList);
            // 更新数据模型的 position
            newSize = mDataList.size();
            for (int i = 0; i < newSize; i++) {
                T t = mDataList.get(i);
                if (t == null) {
                    continue;
                }
                t.setModelPosition(i);
            }
            // 计算 size 差
            int sizeDiff = newSize - oldSize;
            // 更新 Item
            notifyItemRangeChanged(0, newSize);
            if (sizeDiff < 0) {
                // 移除 Item
                notifyItemRangeRemoved(newSize, -sizeDiff);
            } else if (sizeDiff > 0) {
                // 添加 Item
                notifyItemRangeInserted(newSize, sizeDiff);
            }
        } else {
            // 移除所有 Item
            notifyItemRangeRemoved(0, oldSize);
        }
    }
    //</editor-fold>
}
