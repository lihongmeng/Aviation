package com.jxntv.feed.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jxntv.base.R;
import com.jxntv.base.databinding.ItemHotCircleBinding;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.view.CircleTopicLiveLayout;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.viewholder.HotCircleViewHolder;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.utils.SizeUtils;

import java.util.List;

public class HotCircleAdapter extends BaseRecyclerAdapter<Circle, HotCircleViewHolder> {

    private Listener hotCircleAdapterListener;
    private CircleTopicLiveLayout.Listener circleTopicLiveLayoutListener;

    private int mShowLastPosition = -1;

    public HotCircleAdapter(Context context) {
        super(context);
    }

    public void updateSource(List<Circle> dataSource) {
        mList.clear();
        if (dataSource != null) {
            mList.addAll(dataSource);
        }
        notifyDataSetChanged();
    }

    @Override
    public HotCircleViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new HotCircleViewHolder(ItemHotCircleBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(HotCircleViewHolder hotCircleViewHolder, int position) {
        ItemHotCircleBinding binding = hotCircleViewHolder.getBinding();
        if (binding == null) {
            return;
        }
        final Circle circle = mList.get(position);
        if (circle == null) {
            return;
        }

        // 封面图
        if (circle.imageVO != null
                && !TextUtils.isEmpty(circle.imageVO.url)) {
            Glide.with(mContext)
                    .load(circle.imageVO.url)
                    .apply(new RequestOptions().transform(
                            new CenterCrop(),
                            new RoundedCorners(SizeUtils.dp2px(5))
                    ))
                    .into(binding.cover);
        }

        // 圈子名称
        binding.name.setText(circle.getName());

        // 圈子红人
        HotCircleFamousAdapter hotCircleFamousAdapter = new HotCircleFamousAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.famousCircleLayout.setLayoutManager(linearLayoutManager);
        binding.famousCircleLayout.setAdapter(hotCircleFamousAdapter);
        if (circle.userList!=null) {
            hotCircleFamousAdapter.refreshData(circle.userList);
        }

        // 直播、话题信息
        binding.circleTopicLive.updateDataSource(circle, circle.contentList);
        binding.circleTopicLive.setListener(circleTopicLiveLayoutListener);

        // 加入、进入圈子按钮
        if (!circle.isJoin()) {
            binding.joinEnter.setBackgroundResource(R.drawable.icon_join_circle);
        } else {
            binding.joinEnter.setBackgroundResource(R.drawable.icon_enter_circle_long);
        }

        binding.joinEnter.setTag(circle);
        binding.joinEnter.setOnClickListener(v -> {
            if (hotCircleAdapterListener == null) {
                return;
            }
            Object object = v.getTag();
            if (object == null) {
                return;
            }
            hotCircleAdapterListener.clickJoinEnter(v, (Circle) object);
        });

        binding.root.setTag(circle);
        binding.root.setOnClickListener(v -> {
            if (hotCircleAdapterListener == null) {
                return;
            }
            Object object = v.getTag();
            if (object == null) {
                return;
            }
            hotCircleAdapterListener.clickCircleItem(v, (Circle) object);
        });

        mShowLastPosition = position;

    }

    public String getJoinButtonText(int position) {
        Circle circle = mList.get(position);
        if (circle == null) {
            return "";
        }
        if (circle.isJoin()) {
            return ResourcesUtils.getString(R.string.join);
        } else {
            return ResourcesUtils.getString(R.string.enter);
        }
    }

    public void setCircleTopicLiveLayoutListener(CircleTopicLiveLayout.Listener circleTopicLiveLayoutListener) {
        this.circleTopicLiveLayoutListener = circleTopicLiveLayoutListener;
    }

    public void setHotCircleAdapterListener(Listener hotCircleAdapterListener) {
        this.hotCircleAdapterListener = hotCircleAdapterListener;
    }

    public interface Listener {

        /**
         * 究竟是显示加入，还是进入
         * 本类也是根据Circle中的join属性判断
         * 故回调直接将Circle传出去
         * 上层需要自己根据Circle中的join属性判断
         *
         * @param circle Circle
         */
        void clickJoinEnter(View view, Circle circle);

        /**
         * 点击进入圈子详情页的点击事件
         *
         * @param circle Circle
         */
        void clickCircleItem(View view, Circle circle);

    }

    public void enterPosition(int position,String pid){
        if (position < mList.size()) {
            GVideoSensorDataManager.getInstance().circleExposure(mList.get(position), pid, true);
        }
    }

    public void exitPosition(int position,String pid){
        if (position < mList.size()) {
            GVideoSensorDataManager.getInstance().circleExposure(mList.get(position), pid, false);
        }
    }

    public int getShowLastPosition(){
        return mShowLastPosition;
    }

}