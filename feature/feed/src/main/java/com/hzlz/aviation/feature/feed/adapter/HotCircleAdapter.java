package com.hzlz.aviation.feature.feed.adapter;

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
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.ItemHotCircleBinding;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.view.CircleTopicLiveLayout;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.viewholder.HotCircleViewHolder;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.SizeUtils;

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

        // ?????????
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

        // ????????????
        binding.name.setText(circle.getName());

        // ????????????
        HotCircleFamousAdapter hotCircleFamousAdapter = new HotCircleFamousAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.famousCircleLayout.setLayoutManager(linearLayoutManager);
        binding.famousCircleLayout.setAdapter(hotCircleFamousAdapter);
        if (circle.userList!=null) {
            hotCircleFamousAdapter.refreshData(circle.userList);
        }

        // ?????????????????????
        binding.circleTopicLive.updateDataSource(circle, circle.contentList);
        binding.circleTopicLive.setListener(circleTopicLiveLayoutListener);

        // ???????????????????????????
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
         * ????????????????????????????????????
         * ??????????????????Circle??????join????????????
         * ??????????????????Circle?????????
         * ????????????????????????Circle??????join????????????
         *
         * @param circle Circle
         */
        void clickJoinEnter(View view, Circle circle);

        /**
         * ??????????????????????????????????????????
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