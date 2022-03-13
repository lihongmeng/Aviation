package com.jxntv.circle.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jxntv.base.model.circle.CircleFamous;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.circle.CirclePluginImpl;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.LayoutCircleFamousHorizontalItemBinding;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;

import java.util.List;

public class CircleFamousHorizontalAdapter extends BaseRecyclerAdapter<CircleFamous, BaseRecyclerViewHolder> {

    public long groupId;

    public CircleFamousHorizontalAdapter(Context context) {
        super(context);
    }

    public void updateGroupId(long groupId) {
        this.groupId = groupId;
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        LayoutCircleFamousHorizontalItemBinding v = LayoutCircleFamousHorizontalItemBinding.inflate(
                mInflater, parent, false);
        return new BaseRecyclerViewHolder(v);
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {

        if (holder.getBinding() instanceof LayoutCircleFamousHorizontalItemBinding) {
            CircleFamous circleFamous = mList.get(position);

            LayoutCircleFamousHorizontalItemBinding binding = (LayoutCircleFamousHorizontalItemBinding) holder.getBinding();
            binding.setModel(circleFamous);

            if (circleFamous.isAuthentication) {
                binding.headerAuthentication.setVisibility(VISIBLE);
            } else {
                binding.headerAuthentication.setVisibility(GONE);
            }

            binding.header.setTag(new AuthorModel(circleFamous));

            binding.header.setOnClickListener(v -> {
                Object object = v.getTag();
                if (object == null) {
                    return;
                }
                PluginManager.get(AccountPlugin.class).startPgcActivity(v, (AuthorModel) object);
            });

            binding.root.setOnClickListener(v -> {
                Object object = v.getTag();
                if (object == null) {
                    return;
                }
                PluginManager.get(AccountPlugin.class).startPgcActivity(v, (AuthorModel) object);
            });

            if (position == 0) {
                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) binding.root.getLayoutParams();
                layoutParams.leftMargin = 0;
                binding.root.setLayoutParams(layoutParams);
            }
        }

//        if (circleFamous.roleType == Constant.CIRCLE_FAMOUS_TYPE.ADMIN) {
//            holder.admin.setText(ResourcesUtils.getString(R.string.admin));
//            holder.admin.setVisibility(VISIBLE);
//        } else if (circleFamous.roleType == Constant.CIRCLE_FAMOUS_TYPE.CIRCLE_OWNER) {
//            holder.admin.setText(ResourcesUtils.getString(R.string.admin));
//            holder.admin.setVisibility(VISIBLE);
//        } else {
//            holder.admin.setText("");
//            holder.admin.setVisibility(GONE);
//        }

//        if (position > 0) {
//            int roleType = mList.get(position - 1).roleType;
//            if ((roleType == Constant.CIRCLE_FAMOUS_TYPE.ADMIN
//                    || roleType == Constant.CIRCLE_FAMOUS_TYPE.CIRCLE_OWNER)
//                    && (circleFamous.roleType != Constant.CIRCLE_FAMOUS_TYPE.ADMIN
//                    && circleFamous.roleType != Constant.CIRCLE_FAMOUS_TYPE.CIRCLE_OWNER)) {
//                holder.divider.setVisibility(VISIBLE);
//            } else {
//                holder.divider.setVisibility(GONE);
//            }
//        } else {
//            holder.divider.setVisibility(GONE);
//        }

    }

    public void refreshData(@NonNull List<CircleFamous> data) {
        if (data.size() > 3) {
            data = data.subList(0, 3);
        }
        if (mList.size() == data.size()) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).jid != mList.get(i).jid){
                    super.refreshData(data);
                    return;
                }
            }
        }else {
            super.refreshData(data);
        }
    }
}
