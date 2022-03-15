package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.FindCircleContent;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

import java.util.ArrayList;
import java.util.List;

public class CircleTopicLiveLayout extends GVideoLinearLayout {

    private Context context;
    private final List<FindCircleContent> dataSource = new ArrayList<>();
    private Listener listener;
    private LayoutInflater layoutInflater;

    public CircleTopicLiveLayout(Context context) {
        this(context, null);
    }

    public CircleTopicLiveLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTopicLiveLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVar(context);
    }

    private void initVar(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void updateDataSource(final Circle circle, List<FindCircleContent> dataSource) {
        this.dataSource.clear();
        removeAllViews();
        if (dataSource == null || dataSource.isEmpty()) {
            return;
        }
        this.dataSource.addAll(dataSource);
        int size = this.dataSource.size();

        for (int index = 0; index < size; index++) {
            FindCircleContent findCircleContent = this.dataSource.get(index);
            if (findCircleContent == null) {
                continue;
            }
            GVideoTextView itemView;
            switch (findCircleContent.type) {
                case Constant.FindCircleContentType.LIVE:
                    itemView = (GVideoTextView) LayoutInflater.from(context)
                            .inflate(R.layout.layout_find_circle_item_live, null);
                    itemView.setText(findCircleContent.getLiveSpannableString());
                    break;
                case Constant.FindCircleContentType.TOPIC:
                    itemView = (GVideoTextView) LayoutInflater.from(context)
                            .inflate(R.layout.layout_find_circle_item_topic, null);
                    itemView.setText(findCircleContent.getTopicSpannableString());
                    break;
                default:
                    itemView = (GVideoTextView) LayoutInflater.from(context)
                            .inflate(R.layout.layout_find_circle_item_topic, null);
                    itemView.setText(findCircleContent.getMomentSpannableString());
            }

            itemView.setTag(findCircleContent);
            itemView.setOnClickListener(
                    v -> {
                        if (listener == null) {
                            return;
                        }
                        Object tag = v.getTag();
                        if (tag == null) {
                            return;
                        }
                        listener.itemClick(circle, (FindCircleContent) tag);
                    });
            addView(
                    itemView,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            SizeUtils.dp2px(40)
                    )
            );

            // 分割线
            if (index != size - 1) {
                View middleDivider = layoutInflater.inflate(R.layout.divider_height_1px_f2f2f2, this, false);
                addView(middleDivider);
            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void itemClick(Circle circle, FindCircleContent findCircleContent);
    }

}
