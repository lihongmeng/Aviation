package com.jxntv.base.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jxntv.base.R;
import com.jxntv.base.databinding.AdapterImageRecyclerviewItemNineBinding;
import com.jxntv.base.model.video.ImageModel;
import com.jxntv.base.plugin.PhotoPreviewPlugin;
import com.jxntv.base.view.ImageRecyclerView;
import com.jxntv.base.view.recyclerview.BaseRecyclerAdapter;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.ResourcesUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes,NotifyDataSetChanged,UseCompatLoadingForDrawables")
public class ImageNineAdapter extends BaseRecyclerAdapter<ImageModel, BaseRecyclerViewHolder> {

    private List<String> oriImageData;
    private int imageType;
    private Context context;

    public ImageNineAdapter(Context context, int imageType) {
        super(context);
        this.context = context;
        this.imageType = imageType;
    }

    public void addData(List<ImageModel> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOriImageData(List<String> list) {
        this.oriImageData = list;
    }

    @Override
    public BaseRecyclerViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new BaseRecyclerViewHolder<>(AdapterImageRecyclerviewItemNineBinding.inflate(mInflater,
                parent, false));
    }

    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {
        AdapterImageRecyclerviewItemNineBinding binding = (AdapterImageRecyclerviewItemNineBinding) holder.getBinding();
        if (binding == null) {
            return;
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.image.getLayoutParams();

        ImageModel model = mList.get(position);
        if (mList.size() == 1) {
            //单张图片获取宽高设置控件大小
            if (model.getWeight() > 0 && model.getHeight() > 0) {
                setImageLayoutParams(binding.image, model.getHeight(), model.getWeight(), null);
                ImageLoaderManager.loadImage(binding.image, model.getUrl(), true);
            } else {
                Glide.with(context)
                                .asBitmap()
                                .load(model.getUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(ContextCompat.getDrawable(context, R.drawable.media_default_cover_bg))
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        setImageLayoutParams(binding.image, resource.getHeight(), resource.getWidth(), resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
            }

        } else {
            int imageSizeHeight, imageSizeWidth;
            switch (imageType) {
                case ImageRecyclerView.ImageSize.small:
                    imageSizeHeight = ResourcesUtils.getIntDimens(R.dimen.DIMEN_67DP);
                    imageSizeWidth = ResourcesUtils.getIntDimens(R.dimen.DIMEN_67DP);
                    break;
                default:
                    imageSizeHeight = ResourcesUtils.getIntDimens(R.dimen.DIMEN_96DP);
                    imageSizeWidth = ResourcesUtils.getIntDimens(R.dimen.DIMEN_96DP);
            }

            layoutParams.height = imageSizeHeight;
            layoutParams.width = imageSizeWidth;
            binding.image.setLayoutParams(layoutParams);
            ImageLoaderManager.loadImage(binding.image, model.getUrl(), true);
        }

        binding.image.setOnClickListener(view -> {
            ArrayList<String> images = new ArrayList<>();
            if (oriImageData == null) {
                for (int i = 0; i < mList.size(); i++) {
                    images.add(mList.get(i).getUrl());
                }
            } else {
                images.addAll(oriImageData);
            }
            PluginManager.get(PhotoPreviewPlugin.class).startPhotoViewActivity(
                            context,
                            binding.image,
                            images,
                            position
            );
        });

    }


    private void setImageLayoutParams(ImageView imageView, int height, int width, Bitmap resource) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
        int dp171 = ResourcesUtils.getIntDimens(R.dimen.DIMEN_171DP);

        if (height > width) {
            if (height > dp171) {
                width = width * dp171 / height;
                height = dp171;
            }
        } else {
            if (width > dp171) {
                height = height * dp171 / width;
                width = dp171;
            }
        }
        layoutParams.height = height;
        layoutParams.width = width;
        imageView.setLayoutParams(layoutParams);
        if (resource != null) {
            imageView.setImageBitmap(resource);
        }
    }
}
