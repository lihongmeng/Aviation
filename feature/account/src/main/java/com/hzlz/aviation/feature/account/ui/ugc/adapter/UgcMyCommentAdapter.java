package com.hzlz.aviation.feature.account.ui.ugc.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hzlz.aviation.feature.account.BR;
import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.ViewItemAdapterMyCommentBinding;
import com.hzlz.aviation.feature.account.model.UgcMyCommentModel;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.databind.MediaToolBarDataBind;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/6/11
 * desc :  Ta关注的人
 **/
public class UgcMyCommentAdapter extends BaseDataBindingAdapter<UgcMyCommentModel> {

    private String fragmentId;

    public void setFragmentId(String fragmentId) {
        this.fragmentId = fragmentId;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.view_item_adapter_my_comment;
    }

    @Override
    protected void bindData(@NonNull DataBindingViewHolder holder, int position) {

        UgcMyCommentModel commentModel = mDataList.get(position);
        MediaModel media = commentModel.getMedia();

        holder.binding.setVariable(BR.model, commentModel);
        holder.binding.setVariable(BR.author, commentModel.commentUser.getObservable());
        holder.binding.setVariable(BR.videoObservable, commentModel.getMedia().getObservable());

        media.showMediaPageSource = MediaPageSource.PageSource.MINE;
        media.setCreateDate(commentModel.commentDate);
        MediaToolBarDataBind dataBind = new MediaToolBarDataBind(false, media, fragmentId,position);
        dataBind.updateCircleTopicInfo(StatPid.UGC_COMPOSITION, media);
        dataBind.updateAuthorSourceText(media);
        holder.binding.setVariable(BR.toolBind, dataBind);
        holder.binding.executePendingBindings();

        holder.itemView.setOnClickListener(view -> {
            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(holder.itemView.getContext(),
                    mDataList.get(position).getMedia(), null);
        });

        if (holder.binding instanceof ViewItemAdapterMyCommentBinding) {

            ViewItemAdapterMyCommentBinding binding = (ViewItemAdapterMyCommentBinding) holder.binding;

            if (commentModel.getLength() > 0 && !TextUtils.isEmpty(commentModel.getSoundUrl())) {
                binding.soundView.setTotalSecondTime(commentModel.getLength());
                binding.soundView.setSoundUrl(commentModel.getSoundUrl());
                binding.soundView.setSoundText(commentModel.getSoundContent());
            }

            binding.soundView.setPlayOnClickListener(view -> {
                AudioPlayManager.getInstance().start(binding.soundView);
            });

            binding.targetMedia.setVisibility(View.GONE);
            binding.targetContent.setVisibility(View.VISIBLE);
            binding.targetImg.setVisibility(View.VISIBLE);
            switch (media.getMediaType()) {
                case MediaType.AUDIO_TXT:
                case MediaType.SHORT_AUDIO:
                case MediaType.LONG_AUDIO:
                    if (media.getAuthor() != null) {
                        binding.targetImg.setVisibility(View.GONE);
                        String text;
                        if (media.getMediaType() == MediaType.AUDIO_TXT) {
                            text = "语音";
                        } else {
                            text = "音频";
                        }
                        String contentAll = media.getAuthor().getName() + "：「" + text + "」";
                        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(contentAll);
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#" +
                                Integer.toHexString(ContextCompat.getColor(binding.targetContent.getContext(), R.color.color_006fbb))));
                        spannableBuilder.setSpan(colorSpan, contentAll.length() - 3, contentAll.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        binding.targetContent.setText(spannableBuilder);
                        binding.targetImgLayout.setVisibility(View.GONE);
                    }
                    break;
                case MediaType.LONG_VIDEO:
                case MediaType.SHORT_VIDEO:
                    binding.targetMedia.setVisibility(View.VISIBLE);
                    binding.targetContent.setText(media.getTitle());
                    loadImage(media.getCoverUrl(), binding.targetImg, binding.targetImgLayout);
                    break;
                case MediaType.IMAGE_TXT:
                case MediaType.NEWS_IMAGE:
                case MediaType.NEWS_RIGHT_IMAGE:
                    List<String> imageArrayList = media.getImageUrls();
                    String url = "";
                    if (imageArrayList != null && imageArrayList.size() > 0) {
                        url = imageArrayList.get(0);
                    }
                    String title = media.getContentThanTitle();

                    if (TextUtils.isEmpty(title)
                            && !TextUtils.isEmpty(media.outShareUrl)) {
                        loadImage(R.drawable.icon_link_blue,binding.targetImg,binding.targetImgLayout);
                        binding.targetContent.setText(TextUtils.isEmpty(media.outShareTitle)
                                ? ResourcesUtils.getString(R.string.share_link_default_tip) : media.outShareTitle);
                    } else {
                        loadImage(url, binding.targetImg, binding.targetImgLayout);
                        binding.targetContent.setText(media.getContentThanTitle());
                    }
                    break;
                default:
                    binding.targetContent.setVisibility(View.GONE);
                    binding.targetImgLayout.setVisibility(View.GONE);
            }

            new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    binding.soundView.stop();
                }
            };
        }
    }


    private void loadImage(String url, ImageView iv, View view) {
        if (TextUtils.isEmpty(url)) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return;
        }
        view.setVisibility(View.VISIBLE);
        iv.setVisibility(View.VISIBLE);
        ImageLoaderManager.loadImage(iv, url, false);
    }

    private void loadImage(int iconResourceId, ImageView iv, View view) {
        view.setVisibility(View.VISIBLE);
        iv.setVisibility(View.VISIBLE);
        iv.setImageResource(iconResourceId);
    }

}