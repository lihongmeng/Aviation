package com.jxntv.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jxntv.base.R;
import com.jxntv.base.databinding.ItemPublishImageListBinding;
import com.jxntv.base.decoration.GapItemDecoration;
import com.jxntv.base.plugin.PhotoPreviewPlugin;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.widget.GVideoRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/4/6
 * desc : 9宫格图片recycler
 **/
public class PublishImageRecyclerView extends FrameLayout {

    // 删除、替换、添加等操作事件
    private OperationListener operationListener;

    private int maxShowCount = 9;

    public PublishImageRecyclerView(Context context) {
        this(context, null);
    }

    public PublishImageRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PublishImageRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    /**
     * 单张图片
     */
    public void setOneImageData(String imageUrl) {
        List<String> imageURls = new ArrayList<>();
        imageURls.add(imageUrl);
        setImagesData(imageURls);
    }

    public void setMaxShowCount(int maxShowCount) {
        this.maxShowCount = maxShowCount;
    }

    /**
     * 设置多张图片数据
     *
     * @param imageData 图片
     */
    @BindingAdapter("imageData")
    public static void setImagesData(PublishImageRecyclerView recyclerView, List<String> imageData) {
        if (recyclerView != null) {
            recyclerView.setImagesData(imageData);
        }
    }

    /**
     * 设置多张图片数据
     *
     * @param imageData 图片
     */
    public void setImagesData(List<String> imageData) {
        if (imageData == null || imageData.isEmpty()) {
            return;
        }
        int dp7 = ResourcesUtils.getIntDimens(R.dimen.DIMEN_7DP);
        ImageAdapter imageAdapter = new ImageAdapter(getContext());
        GVideoRecyclerView recyclerView;
        if (getChildAt(0) == null) {
            removeAllViews();
            recyclerView = new GVideoRecyclerView(getContext());
            addView(recyclerView, 0);
            //去重
            if (recyclerView.getItemDecorationCount() == 0) {
                GapItemDecoration decoration = new GapItemDecoration();
                decoration.setHorizontalGap(dp7);
                decoration.setVerticalGap(dp7);
                decoration.setFistRowTopGap(dp7);
                recyclerView.addItemDecoration(decoration);
            }
        } else {
            recyclerView = (GVideoRecyclerView) getChildAt(0);
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setPadding(-dp7, 0, 0, 0);
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.clearList();

        imageAdapter.addData(imageData);

    }

    private class ImageAdapter extends BaseRecyclerAdapter<String, BaseRecyclerViewHolder> {

        public ImageAdapter(Context context) {
            super(context);
        }

        public void addData(List<String> list) {
            mList.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }
            int size = mList.size();
            if (size > 0 && size < maxShowCount) {
                return size + 1;
            }
            return size;
        }

        @Override
        public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
            ItemPublishImageListBinding vh = ItemPublishImageListBinding.inflate(mInflater, parent, false);
            return new BaseRecyclerViewHolder(vh);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindVH(BaseRecyclerViewHolder holder, int position) {

            ItemPublishImageListBinding binding = (ItemPublishImageListBinding) holder.getBinding();
            if (binding != null) {

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.image.getLayoutParams();
                int dp96 = ResourcesUtils.getIntDimens(R.dimen.DIMEN_96DP);

                ConstraintLayout.LayoutParams replaceLayoutParams = (ConstraintLayout.LayoutParams) binding.replace.getLayoutParams();

                layoutParams.height = dp96;
                layoutParams.width = dp96;
                binding.image.setLayoutParams(layoutParams);

                replaceLayoutParams.width = dp96;
                binding.replace.setLayoutParams(replaceLayoutParams);

                Context context = holder.itemView.getContext();

                if (position == mList.size()) {
                    binding.image.setScaleType(ImageView.ScaleType.CENTER);
                    binding.image.setImageResource(R.drawable.icon_add);
                    binding.replace.setVisibility(GONE);
                    binding.delete.setVisibility(GONE);
                } else {
                    Glide.with(context)
                            .load(mList.get(position))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.image);

                    binding.replace.setVisibility(VISIBLE);
                    binding.delete.setVisibility(VISIBLE);

                    binding.delete.setTag(position);
                    binding.delete.setOnClickListener(view -> {
                        if (operationListener == null) {
                            return;
                        }
                        operationListener.delete((Integer) view.getTag());
                        if (mList.size()==1) {

                        }
                    });

                    binding.replace.setTag(position);
                    binding.replace.setOnClickListener(view -> {
                        if (operationListener == null) {
                            return;
                        }
                        operationListener.replace((Integer) view.getTag());
                    });
                }

                binding.image.setOnClickListener(view -> {
                    if (position == mList.size()) {
                        if (operationListener == null) {
                            return;
                        }
                        operationListener.add();
                    } else {
                        ArrayList<String> images = new ArrayList<>(mList);
                        PluginManager.get(PhotoPreviewPlugin.class).startPhotoViewActivity(getContext(),
                                binding.image, images, position);
                    }
                });

            }

        }
    }

    public interface OperationListener {
        void delete(int index);

        void replace(int index);

        void add();
    }

}
