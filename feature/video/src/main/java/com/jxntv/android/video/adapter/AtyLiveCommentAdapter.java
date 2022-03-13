package com.jxntv.android.video.adapter;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.GridLayoutManager;
import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.AdapterLiveCommentBinding;
import com.jxntv.android.video.model.LiveCommentModel;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.plugin.PhotoPreviewPlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;

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
                    .addText(commentLstBean.getShowName(), R.color.c_8BE6FE,null)
                    .addText(commentLstBean.getMessage(), isPlacard?R.color.c_8BE6FE:R.color.white,null);

            if (commentLstBean.getLinks()!=null) {
                for (int i = 0; i < commentLstBean.getLinks().size(); i++) {
                    String text = commentLstBean.getLinks().get(i).getText();
                    String link = commentLstBean.getLinks().get(i).getLink();
                    binding.content.addText(text, R.color.c_4189FF, new ClickableSpan() {
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
