package com.jxntv.android.liteav.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.jxntv.android.liteav.GVideoSoundView;
import com.jxntv.android.liteav.R;
import com.jxntv.base.databinding.LayoutQualityCommentBinding;
import com.jxntv.base.model.QualityComment;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.view.ImageRecyclerView;

import java.util.List;

/**
 * 优质评论布局
 */
public class QualityCommentLayout extends ConstraintLayout {

    private Context context;
    private LayoutQualityCommentBinding binding;
    // 播放按钮点击
    private OnClickListener playOnClickListener;

    public QualityCommentLayout(@NonNull Context context) {
        this(context, null);
    }

    public QualityCommentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QualityCommentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
        initViews();
    }

    private void initVars(Context context) {
        this.context = context;
    }

    private void initViews() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_quality_comment,
                this,
                true
        );
    }

    public void update(QualityComment qualityComment) {
        if (qualityComment == null) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        // 评论内容
        binding.textContent.setText(getContent(qualityComment.commentUser, qualityComment.content));

        binding.container.removeAllViews();

        // 填充图片或语音内容
        boolean isImageListEmpty = qualityComment.imageList == null || qualityComment.imageList.isEmpty();
        boolean isSoundDataEmpty = TextUtils.isEmpty(qualityComment.soundContent) && TextUtils.isEmpty(qualityComment.soundUrl);
        if (isImageListEmpty && isSoundDataEmpty) {
            binding.container.setVisibility(GONE);
        } else {
            binding.container.setVisibility(VISIBLE);
            if (isImageListEmpty) {
                addSound(qualityComment.soundUrl, qualityComment.soundContent, qualityComment.length);
            } else {
                addImage(qualityComment.imageList, qualityComment.oriImageList);
            }
        }

    }

    public GVideoSoundView getGVideoSoundView() {
        View childView = binding.container.getChildAt(0);
        if (childView instanceof GVideoSoundView) {
            return (GVideoSoundView) childView;
        }
        return null;
    }

    public void addImage(List<String> imageList, List<String> oriImageList) {
        ImageRecyclerView imageRecyclerView = new ImageRecyclerView(context);
        imageRecyclerView.setImagesData(imageList, oriImageList);
        imageRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        binding.container.addView(imageRecyclerView);
    }

    public void addSound(String soundUrl, String soundContent, long length) {
        GVideoSoundView gVideoSoundView = new GVideoSoundView(context);
        gVideoSoundView.isShowDelete(false);
        gVideoSoundView.isEnableTextChange(true);
        gVideoSoundView.setSoundText(soundContent);
        gVideoSoundView.setSoundUrl(soundUrl);
        gVideoSoundView.setTotalSecondTime(length);
        gVideoSoundView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        binding.container.addView(gVideoSoundView);
        if (playOnClickListener != null) {
            gVideoSoundView.setPlayOnClickListener(playOnClickListener);
        }
    }

    public void setPlayOnClickListener(OnClickListener listener) {
        this.playOnClickListener = listener;
    }

    private SpannableString getContent(AuthorModel commentUser, String content) {
        String result = "";
        boolean needShowAuthor = false;
        String authorName = "";
        if (commentUser != null) {
            authorName = commentUser.getName();
            if (!TextUtils.isEmpty(authorName)) {
                needShowAuthor = true;
                result += (authorName + ": ");
            }
        }
        if (!TextUtils.isEmpty(content)) {
            result += content;
        }
        SpannableString resultSpannable = new SpannableString(result);

        if (needShowAuthor) {
            resultSpannable.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    authorName.length() + 1,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
        return resultSpannable;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (visibility != VISIBLE && binding != null) {
            View childView = binding.container.getChildAt(0);
            if (childView instanceof GVideoSoundView) {
                ((GVideoSoundView) childView).stop();
            }
            return;
        }
        super.onVisibilityChanged(changedView, visibility);
    }
}
