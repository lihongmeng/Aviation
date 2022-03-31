package com.hzlz.aviation.kernel.base.view.recyclerview;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

/**
 * 没有更多了
 */
public class NoMoreDataViewHolder extends BaseRecyclerViewHolder {

    /** viewHolder持有的text view，用于更新文字UI */
    private TextView mNoMoreDataTextView;

    /**
     * 构造方法
     */
    public NoMoreDataViewHolder(@NonNull ViewGroup parent, ContextThemeWrapper wrapper, int stringRes) {
        super(createView(parent, stringRes, wrapper));
        if (itemView instanceof TextView) {
            mNoMoreDataTextView = (TextView) itemView;
        }
    }

    /**
     * 更新no more data文字
     *
     * @param stringRes 待更新的文字res
     */
    public void updateNoMoreDataText(int stringRes) {
        if (mNoMoreDataTextView != null) {
            mNoMoreDataTextView.setText(stringRes);
        }
    }

    public void setPadding(int left, int top, int right, int bottom){
        if (mNoMoreDataTextView!=null){
            mNoMoreDataTextView.setPadding(left, top, right, bottom);
        }
    }

    /**
     * 生成视图
     *
     * @param parent        父布局
     * @param stringRes     待更新的文字res
     * @param wrapper       环境wrapper
     */
    private static View createView(@NonNull ViewGroup parent, int stringRes, ContextThemeWrapper wrapper) {
        if (wrapper == null) {
            wrapper = new ContextThemeWrapper(
                parent.getContext(), R.style.NoMoreDataDefaultStyle
            );
        }
        AviationTextView textView = new AviationTextView(wrapper, null);
        textView.setText(stringRes);
        textView.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.color_cccccc));
        textView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return textView;
    }
}
