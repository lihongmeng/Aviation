package com.jxntv.watchtv.adapter;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.utils.DateUtils;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.watchtv.R;
import com.jxntv.watchtv.databinding.ItemWatchTvChannelDetailTimeBinding;

import java.util.ArrayList;
import java.util.List;

public class WatchTvChannelDetailTimeAdapter extends BaseRecyclerAdapter<Long, BaseRecyclerViewHolder> {

    // 数据源
    private final List<Long> dataSource = new ArrayList<>();

    // 当前选中的位置
    private int currentIndex = 0;

    // color_333333
    private final int color_333333;

    // color_999999
    private final int color_999999;

    // Item点击事件
    private OnItemClickListener onItemClickListener;

    public WatchTvChannelDetailTimeAdapter(Context context) {
        super(context);
        long currentTime = System.currentTimeMillis();
        dataSource.add(currentTime);
        for (int index = 1; index < 7; index++) {
            dataSource.add(currentTime + index * 24 * 60 * 60 * 1000);
        }
        color_333333 = ResourcesUtils.getColor(R.color.color_333333);
        color_999999 = ResourcesUtils.getColor(R.color.color_999999);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(ItemWatchTvChannelDetailTimeBinding.inflate(mInflater, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindVH(BaseRecyclerViewHolder viewHolder, int position) {
        ItemWatchTvChannelDetailTimeBinding mBinding = (ItemWatchTvChannelDetailTimeBinding) viewHolder.getBinding();
        if (mBinding == null) {
            return;
        }
        Long time = dataSource.get(position);
        if (time == null) {
            return;
        }
        int month = DateUtils.getMonth(time) + 1;
        String monthResult = String.valueOf(month);
        if (month < 10) {
            monthResult = "0" + monthResult;
        }
        int day = DateUtils.getDay(time);
        String dayResult = String.valueOf(day);
        if (day < 10) {
            dayResult = "0" + dayResult;
        }

        mBinding.date.setText(monthResult + "/" + dayResult);

        if (position == 0) {
            mBinding.week.setText(R.string.today);
        } else {
            int week = DateUtils.getWeek(time);
            switch (week) {
                case MONDAY:
                    mBinding.week.setText(R.string.monday);
                    break;
                case TUESDAY:
                    mBinding.week.setText(R.string.tuesday);
                    break;
                case WEDNESDAY:
                    mBinding.week.setText(R.string.wednesday);
                    break;
                case THURSDAY:
                    mBinding.week.setText(R.string.thursday);
                    break;
                case FRIDAY:
                    mBinding.week.setText(R.string.friday);
                    break;
                case SATURDAY:
                    mBinding.week.setText(R.string.saturday);
                    break;
                case SUNDAY:
                    mBinding.week.setText(R.string.sunday);
                    break;
            }
        }

        if (currentIndex == position) {
            mBinding.week.setTextColor(color_333333);
            mBinding.date.setTextColor(color_333333);
        } else {
            mBinding.week.setTextColor(color_999999);
            mBinding.date.setTextColor(color_999999);
        }

        mBinding.root.setTag(position);
        mBinding.root.setOnClickListener(
                view -> {
                    Object tag = view.getTag();
                    if (tag == null) {
                        return;
                    }
                    int clickIndex = (Integer) tag;
                    if (onItemClickListener != null && currentIndex != clickIndex) {
                        onItemClickListener.onClick(clickIndex);
                    }
                    currentIndex = clickIndex;
                    notifyDataSetChanged();
                }
        );

    }

    public interface OnItemClickListener {
        void onClick(int clickIndex);
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updatePosition(int position) {
        if (currentIndex == position) {
            return;
        }
        currentIndex = position;
        notifyDataSetChanged();
    }
}
