package com.hzlz.aviation.feature.live.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;
import com.hzlz.aviation.feature.live.R;

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
