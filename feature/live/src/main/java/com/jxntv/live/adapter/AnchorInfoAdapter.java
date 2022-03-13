package com.jxntv.live.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.live.R;
import com.jxntv.live.databinding.ItemMicroConnectRequestListBinding;
import com.jxntv.live.liveroom.roomutil.commondef.AnchorInfo;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import java.util.Collections;
import java.util.Comparator;

public class AnchorInfoAdapter extends BaseRecyclerAdapter<AnchorInfo, BaseRecyclerViewHolder<ItemMicroConnectRequestListBinding>> {

    private AnchorInfoAdapterClickListener anchorInfoAdapterClickListener;

    public AnchorInfoAdapter(Context context) {
        super(context);
    }

    /**
     * 刷新数据
     */
    public void updateAnchorInfo(AnchorInfo anchorInfo){
        for (int i = 0;i< mList.size();i++){
            if (TextUtils.equals(mList.get(i).userid,anchorInfo.userid)){
                mList.remove(i);
                break;
            }
        }
        mList.add(anchorInfo);
        sortList();
    }

    public void setInvalidAnchorInfo(String userId){
        for (int i = 0;i< mList.size();i++){
            if (TextUtils.equals(mList.get(i).userid,userId)){
                mList.get(i).connectState = AnchorInfo.CONNECT_INVALID;
                break;
            }
        }
        sortList();
    }

    /**
     * 对列表进行排序
     */
    private void sortList(){
        Collections.sort(mList, new Comparator<AnchorInfo>() {
            @Override
            public int compare(AnchorInfo anchorInfo, AnchorInfo t1) {
                if (anchorInfo.connectState < t1.connectState){
                    return -1;
                }else if (anchorInfo.connectState == t1.connectState){
                    if (!TextUtils.isEmpty(anchorInfo.applyRequestReason)){
                        return -1;
                    }else if (!TextUtils.isEmpty(t1.applyRequestReason)){
                        return 1;
                    }else if (anchorInfo.time < t1.time){
                        return -1;
                    }else {
                        return 1;
                    }
                }else {
                    return 1;
                }
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public BaseRecyclerViewHolder<ItemMicroConnectRequestListBinding> onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder<>(ItemMicroConnectRequestListBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder<ItemMicroConnectRequestListBinding> viewHolder, int position) {
        ItemMicroConnectRequestListBinding binding = viewHolder.getBinding();
        if (binding == null) {
            return;
        }
        Context context = binding.content.getContext();
        final AnchorInfo anchorInfo = mList.get(position);
        if (anchorInfo == null) {
            return;
        }

        if (!TextUtils.isEmpty(anchorInfo.userAvatar)) {
            ImageLoaderManager.loadHeadImage(binding.header, anchorInfo.userAvatar);
        } else {
            binding.header.setImageResource(R.drawable.ic_default_avatar);
        }

        binding.name.setText(TextUtils.isEmpty(anchorInfo.userName) ? "" : anchorInfo.userName);

        binding.mark.setVisibility(anchorInfo.isUserAuthentication ? VISIBLE : GONE);

        binding.content.setText(anchorInfo.applyRequestReason);
        binding.content.setVisibility(TextUtils.isEmpty(anchorInfo.applyRequestReason) ? GONE : VISIBLE);

        int operatorColorId,operatorText,bgColor,strokeColor;
        binding.operator.setEnabled(false);
        if (anchorInfo.connectState == AnchorInfo.CONNECT_DEFAULT) {
            operatorColorId = R.color.color_e4344e;
            operatorText = R.string.accept;
            bgColor = R.color.color_ffffff;
            strokeColor = R.color.color_e4344e;
            binding.operator.setEnabled(true);
        } else if (anchorInfo.connectState == AnchorInfo.CONNECT_AGREE) {
            operatorColorId = R.color.color_999999;
            operatorText = R.string.already_accept;
            bgColor = R.color.color_ffffff;
            strokeColor = R.color.color_cccccc;
        } else {
            operatorColorId = R.color.color_cccccc;
            operatorText = R.string.already_invalid;
            bgColor = R.color.color_f2f2f2;
            strokeColor = R.color.color_f2f2f2;
        }
        binding.operator.setText(mContext.getString(operatorText));
        binding.operator.setTextColor(ContextCompat.getColor(context,operatorColorId));
        binding.operator.getHelper().setBackgroundColorNormal(ContextCompat.getColor(context,bgColor))
                .setBorderColorNormal(ContextCompat.getColor(context,strokeColor))
                .setBorderWidthNormal(context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_1DP))
                .setCornerRadius(context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_12DP));

        binding.operator.setTag(position);
        binding.operator.setOnClickListener(v -> {
            if (anchorInfoAdapterClickListener != null) {
                anchorInfoAdapterClickListener.onClick(mList.get(position), position);
            }
        });
    }

    public interface AnchorInfoAdapterClickListener {
        void onClick(AnchorInfo anchorInfo, int position);
    }

    public void setOnClickListener(AnchorInfoAdapterClickListener anchorInfoAdapterClickListener) {
        this.anchorInfoAdapterClickListener = anchorInfoAdapterClickListener;
    }
}
