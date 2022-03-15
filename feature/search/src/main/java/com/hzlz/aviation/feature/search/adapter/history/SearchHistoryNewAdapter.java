package com.hzlz.aviation.feature.search.adapter.history;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.hzlz.aviation.feature.search.SearchViewModel;
import com.hzlz.aviation.feature.search.databinding.ItemViewHistoryBinding;
import com.hzlz.aviation.feature.search.db.entity.SearchHistoryEntity;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

/**
 * 搜索历史adapter
 */
public class SearchHistoryNewAdapter extends BaseRecyclerAdapter<SearchHistoryEntity, BaseRecyclerViewHolder> {

    /** 持有的搜索模型 */
    private SearchViewModel mSearchViewModel;
    public SearchHistoryNewAdapter(Context context) {
        super(context);
    }

    /**
     * 更新数据模型
     *
     * @param viewModel     数据模型
     */
    public void updateViewModel(SearchViewModel viewModel) {
        mSearchViewModel = viewModel;
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {

        ItemViewHistoryBinding vh = ItemViewHistoryBinding.inflate(mInflater, parent, false);

        return new BaseRecyclerViewHolder(vh);
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {

        if (holder.getBinding() instanceof ItemViewHistoryBinding){
            ItemViewHistoryBinding binding = (ItemViewHistoryBinding) holder.getBinding();
            binding.textView.setText((mList.get(position).getSearchWord()));
            binding.textView.setOnClickListener(view -> {
                if (view instanceof GVideoTextView && mSearchViewModel != null) {
                    CharSequence sequence = ((GVideoTextView) view).getText();
                    if (sequence != null && !TextUtils.isEmpty(sequence.toString())) {
                        mSearchViewModel.onHistoryWordItemClick(sequence.toString());
                    }
                }
            });

            binding.iconClose.setOnClickListener(view -> {
                mSearchViewModel.removeSearchHistory(mList.get(position));
            });
        }
    }

}
