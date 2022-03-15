package com.hzlz.aviation.kernel.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 热门话题
 */
public class HotTopicLayout extends GVideoLinearLayout {

    private Context context;
    private final List<TopicDetail> dataSource = new ArrayList<>();
    private ClickListener clickListener;

    public HotTopicLayout(Context context) {
        this(context, null);
    }

    public HotTopicLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HotTopicLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
        initViews();
    }

    private void initVars(Context context) {
        this.context = context;
    }

    private void initViews() {
        setOrientation(VERTICAL);
    }

    @SuppressLint("SetTextI18n")
    public void updateDataSource(List<TopicDetail> netData) {
        this.dataSource.clear();
        removeAllViews();
        if (netData == null || netData.isEmpty()) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        dataSource.addAll(netData);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        addView(layoutInflater.inflate(R.layout.item_circle_detail_hot_topic_title, this, false));

        int size = dataSource.size();

        for (int index = 0; index < size; index++) {
            TopicDetail topicDetail = dataSource.get(index);
            if (topicDetail == null) {
                continue;
            }
            View contentView = layoutInflater.inflate(R.layout.item_circle_detail_hot_topic_content, this, false);

            // 话题名称
            GVideoTextView topicName = contentView.findViewById(R.id.topic_name);
            topicName.setText(topicDetail.content);

            if (size == 1) {
                contentView.setBackgroundResource(R.drawable.shape_solid_eff8ff_f9fdff_top_bottom_10dp);
            } else {
                if (index == 0) {
                    contentView.setBackgroundResource(R.drawable.shape_solid_eff8ff_f9fdff_top_10dp);
                } else if (index == (size - 1)) {
                    contentView.setBackgroundResource(R.drawable.shape_solid_eff8ff_f9fdff_bottom_10dp);
                } else {
                    contentView.setBackgroundResource(R.drawable.shape_solid_eff8ff_f9fdff);
                }
            }

            addView(contentView);

            // 分割线
            if (index != size - 1) {
                View middleDivider = layoutInflater.inflate(R.layout.divider_height_1px_f2f2f2_margin_15dp, this, false);
                addView(middleDivider);
            } else {
                addView(layoutInflater.inflate(R.layout.divider_height_8dp_ffffff, this, false));
                addView(layoutInflater.inflate(R.layout.item_circle_detail_hot_topic_divider_10dp, this, false));
            }

            contentView.setTag(topicDetail);
            contentView.setOnClickListener(v -> {
                if (clickListener == null) {
                    return;
                }
                Object object = v.getTag();
                if (object == null) {
                    return;
                }
                clickListener.clickItemLayout(v, (TopicDetail) object);
            });

        }
    }

    private int getColorResourceIdByIndex(int index) {
        switch (index) {
            case 0:
                return R.color.color_ff5959;
            case 1:
                return R.color.color_ff9c59;
            case 2:
                return R.color.color_ffc759;
            default:
                return R.color.color_333333;
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void clickItemLayout(View view, TopicDetail topicDetail);
    }


}
