package com.jxntv.circle.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.circle.BR;
import com.jxntv.circle.CircleHelper;
import com.jxntv.circle.CirclePluginImpl;
import com.jxntv.circle.CircleRepository;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.LayoutFindCircleItemBinding;
import com.jxntv.sensordata.GVideoSensorDataManager;

import java.util.List;

public class FindCircleItemAdapter extends BaseDataBindingAdapter<Circle> {

    private CircleRepository circleRepository;
    private Listener mListener;

    protected int mShowLastPosition = -1;

    public FindCircleItemAdapter() {
        circleRepository = new CircleRepository();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_find_circle_item;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        if (context == null) {
            return;
        }
        Circle circle = mDataList.get(position);
        if (circle == null) {
            return;
        }
        holder.bindData(BR.position, circle.getModelPosition());
        holder.bindData(BR.adapter, this);

        LayoutFindCircleItemBinding binding = (LayoutFindCircleItemBinding) (holder.binding);
        if (binding == null) {
            return;
        }

        // 封面图
        if (circle.imageVO != null
                && !TextUtils.isEmpty(circle.imageVO.url)) {
            CircleHelper.getInstance().loadCircleCover(context, "#cccccc",1,circle.imageVO.url, binding.cover);
        }

        binding.rootLayout.setTag(circle);
        binding.rootLayout.setOnClickListener(v -> {
            Object tag = v.getTag();
            if (tag == null) {
                return;
            }
            new CirclePluginImpl().startCircleDetailWithActivity(
                    v.getContext(),
                    circle,
                    null
            );
        });

        binding.name.setText(circle.getName());

        binding.introduction.setText(circle.getIntroduction());

        binding.join.setBackgroundResource(circle.isJoin() ? R.drawable.icon_enter_circle_short : R.drawable.icon_join);

        // if (circle.contentList == null) {
        //     binding.liveTopicLayout.setVisibility(View.GONE);
        // } else {
        //     binding.liveTopicLayout.setVisibility(View.VISIBLE);
        //
        //     // 直播、话题信息
        //     binding.liveTopicLayout.updateDataSource(circle, circle.contentList);
        //     binding.liveTopicLayout.setListener(
        //             (circleData, findCircleContent) -> {
        //                 switch (findCircleContent.type) {
        //                     case Constant.FindCircleContentType.LIVE:
        //                         PluginManager.get(DetailPagePlugin.class).dispatchToDetail(
        //                                 context,
        //                                 new VideoModel(findCircleContent),
        //                                 null
        //                         );
        //                         break;
        //                     case Constant.FindCircleContentType.TOPIC:
        //                         PluginManager.get(CirclePlugin.class).startTopicDetailWithActivity(
        //                                 context,
        //                                 circleData,
        //                                 findCircleContent.topic
        //                         );
        //                         break;
        //                     case Constant.FindCircleContentType.MOMENT:
        //                         Media media = new Media();
        //                         media.setId(findCircleContent.mediaId + "");
        //                         media.setMediaType(findCircleContent.mediaType);
        //                         media.setAnswerSquareId(findCircleContent.answerSquareId);
        //                         PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context, media, null);
        //                 }
        //             }
        //     );
        // }

        mShowLastPosition = position;

    }

    public interface Listener {
        void onJoinButtonClick(View view, Circle circle);

    }
    //</editor-fold>

    //<editor-fold desc="事件绑定">

    public void onJoinButtonClick(View view, int position) {
        if (mListener != null) {
            mListener.onJoinButtonClick(view, mDataList.get(position));
        }
    }


    public void setListener(@Nullable FindCircleItemAdapter.Listener listener) {
        mListener = listener;
    }

    public void enterPosition(int position, String pid) {
        if (position < getData().size()) {
            GVideoSensorDataManager.getInstance().circleExposure(getData().get(position), pid, true);
        }
    }

    public void exitPosition(int position, String pid) {
        if (position < getData().size()) {
            GVideoSensorDataManager.getInstance().circleExposure(getData().get(position), pid, false);
        }
    }

    /**
     * 屏幕中最后一个显示的位置
     */
    public int getLastPosition() {
        return mShowLastPosition;
    }

    public List<Circle> getDataSource() {
        return mDataList;
    }

}
