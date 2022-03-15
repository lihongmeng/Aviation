package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.LayoutFeedListSortBinding;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

import java.util.ArrayList;
import java.util.List;

public class FeedListSortLayout extends GVideoLinearLayout {

    private Context context;
    private LayoutInflater layoutInflater;
    private LayoutFeedListSortBinding binding;
    private int currentIndex;
    private List<String> contentList = new ArrayList<>();
    private OnChangeListener onChangeListener;

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public FeedListSortLayout(Context context) {
        this(context, null);
    }

    public FeedListSortLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedListSortLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
        initViews();
    }

    private void initVars(Context context) {
        this.context = context;
    }

    private void initViews() {
        layoutInflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.layout_feed_list_sort,
                this,
                true
        );
        binding.content.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex >= contentList.size()) {
                currentIndex = 0;
            }
            updateIndex(currentIndex);
            if (onChangeListener != null) {
                onChangeListener.change(currentIndex);
            }
        });
    }

    public void updateIndex(int value) {
        int childCount = binding.content.getChildCount();
        if (childCount <= 0) {
            return;
        }
        currentIndex=value;
        for (int index = 0; index < childCount; index++) {
            GVideoTextView itemView = (GVideoTextView) binding.content.getChildAt(index);
            if (index == currentIndex) {
                itemView.setBackgroundResource(R.drawable.shape_solid_ffffff_corners_200dp);
                itemView.setTextColor(ContextCompat.getColor(context, R.color.color_333333));
            } else {
                itemView.setBackgroundResource(R.color.color_f2f2f2);
                itemView.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
            }
        }
    }

    public void updateContentList(List<String> contentList) {
        this.contentList = contentList;
        binding.content.removeAllViews();
        if (contentList == null || contentList.isEmpty()) {
            currentIndex = 0;
            return;
        }
        int contentListLength = contentList.size();
        if (currentIndex >= contentListLength) {
            currentIndex = contentListLength - 1;
        }
        for (int index = 0; index < contentList.size(); index++) {
            GVideoTextView itemView = (GVideoTextView) layoutInflater.inflate(R.layout.item_feed_list_sort, null, false);
            itemView.setText(contentList.get(index));
            if (index == currentIndex) {
                itemView.setBackgroundResource(R.drawable.shape_solid_ffffff_corners_200dp);
                itemView.setTextColor(ContextCompat.getColor(context, R.color.color_333333));
            } else {
                itemView.setBackgroundResource(R.color.color_f2f2f2);
                itemView.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
            }
            binding.content.addView(itemView);
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public interface OnChangeListener {
        void change(int index);
    }

}
