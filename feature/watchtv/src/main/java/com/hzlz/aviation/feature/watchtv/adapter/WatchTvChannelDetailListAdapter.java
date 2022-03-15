package com.hzlz.aviation.feature.watchtv.adapter;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.library.util.DateUtils.HH_MM;
import static com.hzlz.aviation.library.util.DateUtils.YYYY_MM_DD_HH_MM_SS;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.watchtv.entity.ChannelTvManifest;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.watchtv.R;
import com.hzlz.aviation.feature.watchtv.databinding.ItemWatchTvChannelDetailListBinding;

import java.util.List;

public class WatchTvChannelDetailListAdapter extends BaseRecyclerAdapter<ChannelTvManifest, BaseRecyclerViewHolder> {

    // 当前播放的位置
    public int currentPlayPosition = -1;

    // color_333333
    private final int color333333 = ResourcesUtils.getColor(R.color.color_333333);

    // color_999999
    private final int color999999 = ResourcesUtils.getColor(R.color.color_999999);

    @Override
    public void refreshData(@NonNull List<ChannelTvManifest> data) {
        long currentTime = System.currentTimeMillis();
        for (int index = 0; index < data.size(); index++) {
            ChannelTvManifest channelTvManifest = data.get(index);
            Long playTimeLong = DateUtils.getDateLong(
                    channelTvManifest.playTime, YYYY_MM_DD_HH_MM_SS);
            if (playTimeLong != null && currentTime < playTimeLong) {
                currentPlayPosition = index - 1;
                break;
            }
        }
        super.refreshData(data);
    }

    public boolean isPlayLast() {
        if (mList == null || mList.isEmpty()) {
            return true;
        }
        return currentPlayPosition == mList.size() - 1;
    }

    public ChannelTvManifest getNextChannelTvManifest() {
        if (isPlayLast()) {
            return null;
        }
        return mList.get(currentPlayPosition + 1);
    }

    public WatchTvChannelDetailListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder(
                ItemWatchTvChannelDetailListBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder viewHolder, int position) {
        ItemWatchTvChannelDetailListBinding mBinding = (ItemWatchTvChannelDetailListBinding) viewHolder.getBinding();
        if (mBinding == null) {
            return;
        }
        ChannelTvManifest channelTvManifest = mList.get(position);

        Long playTimeLong = DateUtils.getDateLong(
                channelTvManifest.playTime, YYYY_MM_DD_HH_MM_SS);
        if (playTimeLong == null) {
            mBinding.time.setText("");
        } else {
            mBinding.time.setText(DateUtils.changeTimeLongToString(playTimeLong, HH_MM));
        }

        mBinding.name.setText(channelTvManifest.programName);

        Resources resources=mContext.getResources();
        if (currentPlayPosition == position) {
            mBinding.wave.setVisibility(VISIBLE);
            mBinding.wave.applyAnimation();

            mBinding.time.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mBinding.time.setTextColor(color333333);

            mBinding.name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mBinding.name.setTextColor(color333333);
            mBinding.name.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimension(R.dimen.sp_15));
        } else {
            mBinding.wave.setVisibility(GONE);
            mBinding.time.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mBinding.name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mBinding.name.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimension(R.dimen.sp_13));

            if (position < currentPlayPosition && currentPlayPosition > 0) {
                mBinding.time.setTextColor(color999999);
                mBinding.name.setTextColor(color999999);
            } else {
                mBinding.time.setTextColor(color333333);
                mBinding.name.setTextColor(color333333);
            }
        }
        showLeftProcessView(mBinding, position);

        mBinding.divider.setVisibility(position == mList.size() - 1 ? GONE : VISIBLE);
    }

    private void showLeftProcessView(ItemWatchTvChannelDetailListBinding mBinding, int position) {
        if (position == 0) {
            if (currentPlayPosition < 0) {
                showFirstFeature(mBinding);
            } else if (position == currentPlayPosition) {
                showFirstCurrent(mBinding);
            } else {
                showFirstHistory(mBinding);
            }
            return;
        }

        if (position == mList.size() - 1) {
            if (position == currentPlayPosition) {
                showLastCurrent(mBinding);
            } else {
                showLastFeature(mBinding);
            }
            return;
        }

        if (position == currentPlayPosition) {
            showCurrent(mBinding);
        } else if (position > currentPlayPosition) {
            showFuture(mBinding);
        } else {
            showHistory(mBinding);
        }
    }

    private void showFirstCurrent(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.other.setVisibility(INVISIBLE);
        mBinding.dividerOtherTop.setVisibility(INVISIBLE);
        mBinding.dividerOtherBottom.setVisibility(INVISIBLE);

        mBinding.current.setVisibility(VISIBLE);
        mBinding.dividerCurrentTop.setVisibility(INVISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(VISIBLE);
    }

    private void showFirstHistory(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.current.setVisibility(INVISIBLE);
        mBinding.dividerCurrentTop.setVisibility(INVISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(INVISIBLE);

        mBinding.other.setVisibility(VISIBLE);
        mBinding.dividerOtherTop.setVisibility(INVISIBLE);
        mBinding.dividerOtherBottom.setVisibility(VISIBLE);
        mBinding.dividerOtherBottom.setBackgroundResource(R.color.color_cccccc);
    }

    private void showFirstFeature(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.current.setVisibility(INVISIBLE);
        mBinding.dividerCurrentTop.setVisibility(INVISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(INVISIBLE);

        mBinding.other.setVisibility(VISIBLE);
        mBinding.dividerOtherTop.setVisibility(INVISIBLE);
        mBinding.dividerOtherBottom.setVisibility(VISIBLE);
        mBinding.dividerOtherBottom.setBackgroundResource(R.drawable.icon_watch_tv_channel_detail_list_dotted_line);
    }

    private void showCurrent(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.other.setVisibility(INVISIBLE);
        mBinding.dividerOtherTop.setVisibility(INVISIBLE);
        mBinding.dividerOtherBottom.setVisibility(INVISIBLE);

        mBinding.current.setVisibility(VISIBLE);
        mBinding.dividerCurrentTop.setVisibility(VISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(VISIBLE);
        mBinding.dividerCurrentBottom.setBackgroundResource(R.drawable.icon_watch_tv_channel_detail_list_dotted_line);

    }

    private void showHistory(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.current.setVisibility(INVISIBLE);
        mBinding.dividerCurrentTop.setVisibility(INVISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(INVISIBLE);

        mBinding.other.setVisibility(VISIBLE);
        mBinding.dividerOtherTop.setVisibility(VISIBLE);
        mBinding.dividerOtherBottom.setVisibility(VISIBLE);
        mBinding.dividerOtherTop.setBackgroundResource(R.color.color_cccccc);
        mBinding.dividerOtherBottom.setBackgroundResource(R.color.color_cccccc);
    }

    private void showFuture(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.current.setVisibility(INVISIBLE);
        mBinding.dividerCurrentTop.setVisibility(INVISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(INVISIBLE);

        mBinding.other.setVisibility(VISIBLE);
        mBinding.dividerOtherTop.setVisibility(VISIBLE);
        mBinding.dividerOtherBottom.setVisibility(VISIBLE);
        mBinding.dividerOtherTop.setBackgroundResource(R.drawable.icon_watch_tv_channel_detail_list_dotted_line);
        mBinding.dividerOtherBottom.setBackgroundResource(R.drawable.icon_watch_tv_channel_detail_list_dotted_line);
    }

    private void showLastCurrent(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.other.setVisibility(INVISIBLE);
        mBinding.dividerOtherTop.setVisibility(INVISIBLE);
        mBinding.dividerOtherBottom.setVisibility(INVISIBLE);

        mBinding.current.setVisibility(VISIBLE);
        mBinding.dividerCurrentTop.setVisibility(VISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(INVISIBLE);
    }

    private void showLastFeature(ItemWatchTvChannelDetailListBinding mBinding) {
        mBinding.current.setVisibility(INVISIBLE);
        mBinding.dividerCurrentTop.setVisibility(INVISIBLE);
        mBinding.dividerCurrentBottom.setVisibility(INVISIBLE);

        mBinding.other.setVisibility(VISIBLE);
        mBinding.dividerOtherTop.setVisibility(VISIBLE);
        mBinding.dividerOtherBottom.setVisibility(INVISIBLE);
        mBinding.dividerOtherTop.setBackgroundResource(R.drawable.icon_watch_tv_channel_detail_list_dotted_line);
    }

}
