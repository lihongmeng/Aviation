package com.jxntv.live.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.R;
import com.jxntv.live.databinding.LayoutHomeLivePreviewItemSingleBinding;
import com.jxntv.media.model.MediaModel;

public class PreviewListAdapter extends BaseDataBindingAdapter<MediaModel> {

    private final View.OnClickListener onClickListener = view -> {
        Object object = view.getTag();
        if (object == null) {
            return;
        }
        PluginManager.get(DetailPagePlugin.class).dispatchToDetail(view.getContext(), (MediaModel) object, null);
    };

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_home_live_preview_item_single;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {

        LayoutHomeLivePreviewItemSingleBinding binding
                = (LayoutHomeLivePreviewItemSingleBinding) holder.binding;

        if (binding == null) {
            return;
        }

        MediaModel mediaModel = mDataList.get(position);

        ImageLoaderManager.loadImage(binding.cover, mediaModel.getCoverUrl(), false);
        String title = mediaModel.getTitle();
        title = TextUtils.isEmpty(title) ? "" : title;
        binding.title.setText(title);

        binding.root.setTag(mediaModel);
        binding.root.setOnClickListener(onClickListener);
    }
}
