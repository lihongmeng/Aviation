package com.hzlz.aviation.kernel.base.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.AdapterImageRecyclerviewItemFallBinding;
import com.hzlz.aviation.kernel.base.model.video.ImageModel;
import com.hzlz.aviation.kernel.base.plugin.PhotoPreviewPlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes,NotifyDataSetChanged")
public class ImageFallAdapter extends BaseRecyclerAdapter<ImageModel, BaseRecyclerViewHolder> {

    private List<String> oriImageData;
    private Context context;

    public ImageFallAdapter(Context context) {
        super(context);
        this.context = context;
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
        return new BaseRecyclerViewHolder<>(AdapterImageRecyclerviewItemFallBinding.inflate(mInflater, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindVH(BaseRecyclerViewHolder holder, int position) {
        AdapterImageRecyclerviewItemFallBinding binding = (AdapterImageRecyclerviewItemFallBinding) holder.getBinding();
        if (binding == null) {
            return;
        }
        ImageModel model = mList.get(position);
        if (model.getWeight() > 0 && model.getHeight()>0){
            setImageLayoutParams(binding.image,binding.text,model.getHeight(),model.getWeight(),null);
            ImageLoaderManager.loadImage(binding.image,model.getUrl(),true);
        }else {
            Glide.with(context)
                            .asBitmap()
                            .load(model.getUrl())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(ContextCompat.getDrawable(context, R.drawable.media_default_cover_bg))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    setImageLayoutParams(binding.image,binding.text, resource.getHeight() ,resource.getWidth(),resource);
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
        }

        binding.image.setOnClickListener(view -> {
            ArrayList<String> images  = new ArrayList<>();
            if (oriImageData == null){
                for (int i = 0; i < mList.size(); i++){
                    images.add(mList.get(i).getUrl());
                }
            }else {
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

    private void setImageLayoutParams(ImageView imageView, TextView textView, int height, int width, Bitmap resource){

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        int dp180 = ResourcesUtils.getIntDimens(R.dimen.DIMEN_180DP);
        int dp430 = ResourcesUtils.getIntDimens(R.dimen.DIMEN_215DP) * 2;
        int dp320 = ResourcesUtils.getIntDimens(R.dimen.DIMEN_320DP);

        if (width / height > 8 / 3 || height / width > 6 / 3) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        if (height >= width) {
            layoutParams.width = dp320;
            height = Math.min(dp430, dp320 * height / resource.getWidth());
            height = Math.max(dp180, height);
            layoutParams.height = height;
        } else {
            layoutParams.height = dp180;
            layoutParams.width = dp320;
        }
        imageView.setLayoutParams(layoutParams);
        if (resource!=null) {
            imageView.setImageBitmap(resource);
        }
    }
}
