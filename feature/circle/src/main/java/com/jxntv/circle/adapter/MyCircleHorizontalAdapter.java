package com.jxntv.circle.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.jxntv.account.model.User;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.circle.CircleHelper;
import com.jxntv.circle.CirclePluginImpl;
import com.jxntv.circle.CircleSharedPrefs;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.LayoutMyCircleHorizontalItemBinding;
import com.jxntv.circle.viewholder.MyCircleHorizontalHolder;
import com.jxntv.sensordata.GVideoSensorDataManager;

import java.util.List;


public class MyCircleHorizontalAdapter extends BaseRecyclerAdapter<Circle, MyCircleHorizontalHolder> {

    private int mShowLastPosition;

    public MyCircleHorizontalAdapter(Context context) {
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
    public MyCircleHorizontalHolder onCreateVH(ViewGroup parent, int viewType) {
        return new MyCircleHorizontalHolder(LayoutMyCircleHorizontalItemBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(MyCircleHorizontalHolder myCircleHorizontalHolder, int position) {
        LayoutMyCircleHorizontalItemBinding binding = myCircleHorizontalHolder.getBinding();
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
            CircleHelper.getInstance().loadCircleCover(mContext, circle.imageVO.url, binding.cover);
        }

        // 圈子名称
        binding.name.setText(circle.getName());

        // 是否有新的内容
        if (groupHasNewContent(circle.groupId, circle.lastPublish)) {
            binding.name.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(mContext, R.drawable.icon_red_dot_e4344e),
                    null,
                    null,
                    null
            );
        } else {
            binding.name.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
            );
        }

        myCircleHorizontalHolder.root.setOnClickListener(
                v -> {
                    new CirclePluginImpl().startCircleDetailWithActivity(mContext, circle, null);
                    binding.name.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            null
                    );
                }
        );

        mShowLastPosition = position;
    }

    private boolean groupHasNewContent(long groupId, long lastPublishTime) {
        User user = UserManager.getCurrentUser();
        String userId = user.getId();
        // 根本没登录，判断为有新内容，按逻辑来说不会出现这种情况
        if (TextUtils.isEmpty(userId)) {
            return true;
        }

        // 使用 userId_groupId 作为Key查找SP
        CircleSharedPrefs circleSharedPrefs = CircleSharedPrefs.getInstance();
        String key = userId + "_" + groupId;
        long lastScanTime = circleSharedPrefs.getLong(key, -1);

        // 如果此账号下，此圈子的时间信息没有保存过，说明是新内容
        if (lastScanTime <= 0) {
            circleSharedPrefs.putLong(
                    userId + "_" + groupId,
                    lastPublishTime
            );
            return true;
        }

        // 如果此账号下，此圈子最新动态的时间大于App最后一次浏览圈子的时间
        // 说明是新内容
        return lastPublishTime > lastScanTime;
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

    /**
     * 屏幕中最后一个显示的位置
     */
    public int getLastPosition(){
        return mShowLastPosition;
    }
}
