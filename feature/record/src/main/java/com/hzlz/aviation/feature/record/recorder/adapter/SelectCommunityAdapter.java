package com.hzlz.aviation.feature.record.recorder.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hzlz.aviation.feature.record.databinding.ItemSelectPublishCommunityBinding;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.feature.record.R;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class SelectCommunityAdapter extends BaseDataBindingAdapter<Circle> {

    // Context
    private Context context;

    // 当前选中的
    public Long selectedGroupId;

    private OnSelectListener onSelectListener;

    public SelectCommunityAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_select_publish_community;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
        Circle circle = mDataList.get(position);
        if (circle == null) {
            return;
        }
        ItemSelectPublishCommunityBinding binding = (ItemSelectPublishCommunityBinding) holder.binding;
        if (binding == null) {
            return;
        }

        binding.root.setOnClickListener(onClickListener);
        binding.selectSelected.setOnClickListener(onClickListener);

        if (circle.groupId < 0) {
            Glide.with(context)
                    .load(ContextCompat.getDrawable(context, R.drawable.icon_publish_not_select_community))
                    .centerCrop()
                    .into(binding.cover);
            binding.root.setTag(circle);
            binding.selectSelected.setTag(circle);
            binding.selectSelected.setBackgroundResource(R.drawable.shape_soild_e4344e_coners_200dp);
            binding.name.setText(ResourcesUtils.getString(R.string.do_not_publish_to_community));
            binding.selectSelected.setText(ResourcesUtils.getString(R.string.select));
            binding.selectSelected.setTextColor(ContextCompat.getColor(context, R.color.color_ffffff));
            return;
        }

        // 封面图
        if (circle.imageVO != null
                && !TextUtils.isEmpty(circle.imageVO.url)) {
            Glide.with(context)
                    .load(circle.imageVO.url)
                    .apply(new RequestOptions().transform(
                            new CenterCrop(),
                            new RoundedCorners(SizeUtils.dp2px(5))
                    ))
                    .into(binding.cover);
            binding.recommendCommunityTitle.setVisibility(GONE);

        }

        // 圈子名称
        binding.name.setText(circle.getName());
        boolean isJoin = circle.isJoin();

        // 第一条是“不发布到社区”，隐藏标题
        if (position == 0) {
            binding.recommendCommunityTitle.setVisibility(GONE);
        } else {

            // 如果上一条是已加入的社区，就展示标题，否则隐藏
            Circle lastCircle = mDataList.get(position - 1);

            // 如果已经加入，并且上一条的id小于0，需要显示标题
            if (isJoin && lastCircle.groupId < 0) {
                binding.recommendCommunityTitle.setVisibility(VISIBLE);
                binding.recommendCommunityTitle.setText(ResourcesUtils.getString(R.string.already_joined_community));
            } else if (!isJoin && (lastCircle.groupId < 0 || lastCircle.isJoin())) {
                // 如果未加入，并且上一条已加入或者上一条的id小于0，需要显示标题
                binding.recommendCommunityTitle.setVisibility(VISIBLE);
                binding.recommendCommunityTitle.setText(ResourcesUtils.getString(R.string.recommend_community));
            } else {
                // 其他情况直接隐藏
                binding.recommendCommunityTitle.setVisibility(GONE);
            }
        }

        // 选择、已选择按钮
        boolean isSelected = (selectedGroupId != null && selectedGroupId == circle.groupId);
        binding.root.setTag(circle);
        binding.selectSelected.setTag(circle);
        binding.selectSelected.setBackgroundResource(
                isSelected ? R.drawable.shape_solid_f2f2f2_corners_200dp : R.drawable.shape_soild_e4344e_coners_200dp
        );
        binding.selectSelected.setText(
                isSelected ? ResourcesUtils.getString(R.string.already_selected) : ResourcesUtils.getString(R.string.select)
        );
        binding.selectSelected.setTextColor(
                isSelected ? ContextCompat.getColor(context, R.color.color_cccccc) : ContextCompat.getColor(context, R.color.color_ffffff)
        );
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object object = v.getTag();
            if (object == null || onSelectListener == null) {
                return;
            }
            Circle circle = (Circle) object;
            if (circle.groupId < 0 || circle.isJoin()) {
                onSelectListener.result(v, circle);
            } else {
                PluginManager.get(CirclePlugin.class).joinCircle(
                        circle.groupId, "", new BaseResponseObserver<Object>() {
                            @Override
                            protected void onRequestData(Object o) {
                                ToastUtils.showShort("已加入" + circle.name + "社区");
                                circle.setJoin(true);
                                onSelectListener.result(v, circle);
                                if(circle!=null){
                                    GVideoSensorDataManager.getInstance().followCommunity(
                                            circle.groupId,
                                            circle.name,
                                            circle.tenantId,
                                            circle.tenantName,
                                            circle.labels,
                                            StatPid.PUBLISH_COMPOSITION,
                                            ""
                                    );
                                }
                            }

                            @Override
                            protected void onRequestError(Throwable throwable) {
                                String errorMessage;
                                if (throwable instanceof TimeoutException ||
                                        throwable instanceof SocketTimeoutException ||
                                        throwable instanceof UnknownHostException) {
                                    errorMessage="网络异常";
                                    ToastUtils.showShort(R.string.all_network_not_available_action_tip);
                                }else{
                                    errorMessage=throwable.getMessage();
                                    ToastUtils.showShort(errorMessage);
                                }
                                if(circle!=null){
                                    GVideoSensorDataManager.getInstance().followCommunity(
                                            circle.groupId,
                                            circle.name,
                                            circle.tenantId,
                                            circle.tenantName,
                                            circle.labels,
                                            StatPid.PUBLISH_COMPOSITION,
                                            errorMessage
                                    );
                                }
                            }
                        }
                );
            }
        }
    };

    public interface OnSelectListener {
        void result(View view, Circle circle);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public void updateCircle(Circle circle) {
        if (circle == null) {
            selectedGroupId = null;
            return;
        }
        selectedGroupId = circle.groupId;
    }

    public boolean isDataSourceEmpty() {
        return mDataList.size() <= 1;
    }
}
