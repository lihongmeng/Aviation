package com.hzlz.aviation.feature.search.adapter.history;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.hzlz.aviation.feature.search.SearchViewModel;
import com.hzlz.aviation.feature.search.db.entity.SearchHistoryEntity;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.hzlz.aviation.feature.search.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索历史adapter
 */
public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.Holder> {

    /** 持有的搜索模型 */
    private SearchViewModel mSearchViewModel;
    /** 上下文环境 */
    private Context mContext;
    /** 持有的搜索模型 */
    private List<SearchHistoryEntity> mList = new ArrayList<>();

    /**
     * 构造函数
     */
    public SearchHistoryAdapter(Context context) {
        mContext = context;
    }

    /**
     * 更新数据模型
     *
     * @param viewModel     数据模型
     */
    public void updateViewModel(SearchViewModel viewModel) {
        mSearchViewModel = viewModel;
    }

    /**
     * 更新数据list
     *
     * @param list  搜索历史模型模型列表
     */
    public void updateList(List<SearchHistoryEntity> list) {
        if (list != null) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AviationTextView textView = new AviationTextView(mContext);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.t_font04));
        textView.setTextColor(mContext.getResources().getColor(R.color.color_212229));
        textView.setBackgroundResource(R.drawable.history_list_drawable);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        int paddingLandScape = mContext.getResources().getDimensionPixelSize(R.dimen.search_history_text_padding_landscape);
        ViewCompat.setPaddingRelative(textView, paddingLandScape, 0, paddingLandScape, 0);
        FlexboxLayoutManager.LayoutParams layoutParams = new FlexboxLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, mContext.getResources().getDimensionPixelOffset(R.dimen.search_history_text_height));
        int margin = mContext.getResources().getDimensionPixelSize(R.dimen.search_history_list_margin);
        layoutParams.setMargins(0, 0, margin, margin);
        textView.setLayoutParams(layoutParams);
        return new Holder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.textView.setText(mList.get(position).getSearchWord());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof AviationTextView && mSearchViewModel != null) {
                    CharSequence sequence = ((AviationTextView) view).getText();
                    if (sequence != null && !TextUtils.isEmpty(sequence.toString())) {
                        mSearchViewModel.onHistoryWordItemClick(sequence.toString());
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * adapter对应的holder
     */
    public class Holder extends RecyclerView.ViewHolder {

        /** 持有text view */
        final TextView textView;

        /**
         * 构造函数
         */
        private Holder(TextView v) {
            super(v);
            textView = v;
        }
    }
}
