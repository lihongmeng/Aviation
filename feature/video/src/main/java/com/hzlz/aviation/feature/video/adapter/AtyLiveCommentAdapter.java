package com.hzlz.aviation.feature.video.adapter;


import android.app.Activity;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.hzlz.aviation.feature.video.BR;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.AdapterLiveCommentBinding;
import com.hzlz.aviation.feature.video.model.LiveCommentModel;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.plugin.PhotoPreviewPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc : 活动直播评论适配器
 **/
public class AtyLiveCommentAdapter extends BaseDataBindingAdapter<LiveCommentModel.CommentLstBean> {


    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {

        if (holder.binding instanceof AdapterLiveCommentBinding) {

            AdapterLiveCommentBinding binding = (AdapterLiveCommentBinding) holder.binding;
            LiveCommentModel.CommentLstBean commentLstBean = getData().get(position);

            boolean isPlacard = position==0&&TextUtils.isEmpty(commentLstBean.getName());
            binding.content.clear();
            binding.content
                    .addText(commentLstBean.getShowName(), R.color.color_8be6fe,null)
                    .addText(commentLstBean.getMessage(), isPlacard?R.color.color_8be6fe:R.color.color_ffffff,null);

            if (commentLstBean.getLinks()!=null) {
                for (int i = 0; i < commentLstBean.getLinks().size(); i++) {
                    String text = commentLstBean.getLinks().get(i).getText();
                    String link = commentLstBean.getLinks().get(i).getLink();
                    binding.content.addText(text, R.color.color_4189ff, new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            PluginManager.get(WebViewPlugin.class).startWebViewActivity(
                                    holder.itemView.getContext(),link , "");
                        }
                    });
                }
            }
            binding.content.showText();

            if (getData().get(position).getImages()!=null && getData().get(position).getImages().size()>0) {
                GridLayoutManager layoutManager = new GridLayoutManager(holder.itemView.getContext(), 3);
                binding.recycler.setLayoutManager(layoutManager);
                ImageAdapter imageAdapter = new ImageAdapter();
                imageAdapter.addData(getData().get(position).getImages());
                binding.recycler.setAdapter(imageAdapter);
                binding.recycler.setVisibility(View.VISIBLE);
            }else {
                binding.recycler.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.adapter_live_comment;
    }


    public class ImageAdapter extends BaseDataBindingAdapter<LiveCommentModel.CommentLstBean.ImagesBean>{

        @Override
        protected int getItemLayoutId() {
            return R.layout.adapter_imageview;
        }

        @Override
        protected void bindData(@NonNull DataBindingViewHolder holder, int position) {

            holder.bindData(BR.imgModel,mDataList.get(position));

            holder.itemView.setOnClickListener(view -> {
                Activity context = (Activity) holder.itemView.getContext();
                if (TextUtils.isEmpty(getData().get(position).getLink())){
                    PluginManager.get(PhotoPreviewPlugin.class).startPhotoViewActivity(context,holder.itemView,
                            getData().get(position).getUrl());
                }else {
                    PluginManager.get(WebViewPlugin.class).startWebViewActivity(context,getData().get(position).getLink(),"");
                }
            });

        }
    }





}
