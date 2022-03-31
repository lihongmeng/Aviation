package com.hzlz.aviation.kernel.media.template;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.model.QuestionModel;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.span.CenterAlignImageSpan;
import com.hzlz.aviation.kernel.base.utils.SpannableStringUtils;
import com.hzlz.aviation.kernel.liteav.AviationSoundView;
import com.hzlz.aviation.kernel.liteav.service.AudioLivePlayHelper;
import com.hzlz.aviation.kernel.media.MediaConfig;
import com.hzlz.aviation.kernel.media.MediaConstants;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.MediaRuntime;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databinding.MediaToolbarLayoutBottomAudiovisualBinding;
import com.hzlz.aviation.kernel.media.databinding.MediaToolbarLayoutBottomBinding;
import com.hzlz.aviation.kernel.media.databinding.MediaToolbarLayoutTopBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.kernel.media.player.IMediaPlayer;
import com.hzlz.aviation.kernel.media.player.MediaPlayManager;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

/**
 * Media 线性布局基础控件
 */
public abstract class MediaBaseTemplate implements IMediaTemplate, IMediaPlayer {

    /**
     * 持有的视图基础控件
     */
    protected MediaTemplateImpl mMediaTemplate;
    /**
     * 持有的Media数据
     */
    protected MediaModel mMediaModel;
    /**
     * toolbar 视图数据绑定
     */
    protected MediaToolbarLayoutTopBinding topBinding;
    /**
     * toolbar 视图数据绑定
     */
    protected MediaToolbarLayoutBottomBinding bottomBinding;
    /**
     * toolbar 视听特殊
     */
    protected MediaToolbarLayoutBottomAudiovisualBinding bottomAudiovisualBinding;
    /**
     * 当前的view group
     */
    protected ViewGroup mViewGroup;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;
    /**
     * 是否为夜间模式
     */
    private boolean mIsDarkMode;

    private View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
            onAttachedToWindow();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            onDetachedFromWindow();
        }
    };


    /**
     * 构造函数
     */
    public MediaBaseTemplate(Context context) {
        mMediaTemplate = new MediaTemplateImpl(context);
        DisplayMetrics dm = MediaRuntime.getAppContext().getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
    }

    @Override
    public void setOnChildViewClickListener(MediaTemplateImpl.OnChildViewClickListener listener) {
        mMediaTemplate.setOnChildViewClickListener(listener);
    }

    @Override
    public MediaModel getMediaModel() {
        return mMediaTemplate.getMediaModel();
    }

    @Override
    public ViewDataBinding getDataBinding() {
        return null;
    }

    @Override
    public void onClick(View v) {
        mMediaTemplate.onClick(v);
    }

    @Override
    public void update(@NonNull MediaModel MediaModel, boolean isDarkMode, String fragmentId, int position) {
        mMediaModel = MediaModel;
        mIsDarkMode = isDarkMode;
        mMediaTemplate.update(MediaModel, isDarkMode, fragmentId, position);

        // 初始化toolbar
        mMediaTemplate.initToolBarTop(MediaModel, topBinding, fragmentId, position);
        mMediaTemplate.initToolBarBottom(MediaModel, bottomBinding, fragmentId, position);
        mMediaTemplate.initToolbarBottomAudiovisual(MediaModel, bottomAudiovisualBinding, fragmentId, position);
        if (getRootLayout() != null) {
            getRootLayout().removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
            getRootLayout().addOnAttachStateChangeListener(mOnAttachStateChangeListener);
        }

        if (mMediaModel.showInSearch()
                && mMediaModel.getWords() != null
                && mMediaModel.getWords().size() > 0
                && getTitleView() != null
                && !TextUtils.isEmpty(mMediaModel.getTitle())) {
            getTitleView().setText(SpannableStringUtils.setSpanColor(mMediaModel.getTitle(),
                    mMediaModel.getWords(), R.color.color_e4344e));
        }
    }

    /**
     * 获取root layout。
     * <p>
     * 对应的root布局
     */
    protected abstract ViewGroup getRootLayout();


    protected abstract View getPlayView();

    public TextView getTitleView() {
        return null;
    }

    /**
     * 布局附着操作
     */
    protected void onAttachedToWindow() {
        MediaPlayManager.getInstance().registerMediaPlayer(mMediaModel.tabId, this);
    }

    /**
     * detach 操作
     */
    protected void onDetachedFromWindow() {
        mMediaModel.playState = MediaConstants.STATE_STOP;
        MediaPlayManager.getInstance().unRegisterMediaPlayer(mMediaModel.tabId, this);
    }

    @Override
    public boolean isSupportAutoPlay() {
        return MediaConfig.isSupportAutoPlay() && mMediaModel.isCanAutoPlay()
                //检测音频广播是否正在直播
                && !AudioLivePlayHelper.getInstance().isPlaying();
    }

    @Override
    public boolean isInPlayScale() {
//    Rect rect = new Rect();
//    getRootLayout().getLocalVisibleRect(rect);
//    if (rect.height() <= 0) {
//      return false;
//    }
//    int[] location = { Integer.MAX_VALUE, Integer.MAX_VALUE };
//    getRootLayout().getLocationInWindow(location);
//    float rate = 1.0f * location[1] / mScreenHeight;
//    boolean in = rate >= 0 && rate <= MediaConfig.AUTO_PLAY_SCREEN_RATIO;
//    return in;

        if (getPlayView() != null) {
            Rect rect = new Rect();
            boolean isVisible = getPlayView().getGlobalVisibleRect(rect);
            isVisible = isVisible && rect.bottom - rect.top >= getPlayView().getHeight();
//            isVisible = isVisible && rect.bottom - rect.top > getPlayView().getHeight() * 0.5;
//            isVisible = isVisible && rect.top < mScreenHeight / 3;
            return isVisible;
        } else {
            return false;
        }
    }


    @Override
    public boolean isPlaying() {
        return mMediaModel.playState == MediaConstants.STATE_PLAY;
    }

    @Override
    public void play() {
        mMediaModel.playState = MediaConstants.STATE_PLAY;
        MediaPlayManager.getInstance().onPlayStart(mMediaModel.tabId, this);
    }

    @Override
    public void stop() {
        mMediaModel.playState = MediaConstants.STATE_STOP;
    }

    @Override
    public void pause() {
        mMediaModel.playState = MediaConstants.STATE_PAUSE;
    }

    @Override
    public void resume() {
        mMediaModel.playState = MediaConstants.STATE_PLAY;
    }

    @Override
    public void setViewGroup(ViewGroup group) {
        mViewGroup = group;
    }

    @Override
    public int getPosition() {
        return mMediaModel.viewPosition;
    }

    @Override
    public MediaModel getPlayerMediaModel() {
        return mMediaModel;
    }

    /**
     * 处理跳转到详情页事件
     *
     * @param needShowComment 是否需要显示评论
     */
    protected void handleNavigateToDetail(boolean needShowComment) {
        mMediaTemplate.handleNavigateToDetail(needShowComment);
    }

    /**
     * 处理跳转到详情页事件
     */
    public void handleNavigateToDetail(Context context, String mFragmentId) {
        Fragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
        Bundle extras = new Bundle();
        if (fragment instanceof MediaPageFragment) {
            String pid = ((MediaPageFragment) fragment).getPid();
            String channelId = ((MediaPageFragment) fragment).getChannelId();
            extras.putString(VideoPlugin.EXTRA_FROM_PID, pid);
            extras.putString(VideoPlugin.EXTRA_FROM_CHANNEL_ID, channelId);
        }
        PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context, mMediaModel, extras);
    }

    @Override
    public void onFragmentSwitchVisible() {
        // 子类需要时实现
    }

    @Override
    public int getCurrentPosition() {
        return mMediaTemplate.mCurrentPosition;
    }

    public void updateLinkLayout(MediaModel model) {
        if (bottomBinding == null) {
            return;
        }
        if (model == null || TextUtils.isEmpty(model.outShareUrl)) {
            bottomBinding.linkLayout.setVisibility(View.GONE);
        } else {
            bottomBinding.linkLayout.updateLinkTitle(TextUtils.isEmpty(model.outShareTitle)
                    ? ResourcesUtils.getString(R.string.share_link_default_tip) : model.outShareTitle);
            bottomBinding.linkLayout.updateLinkValue(model.outShareUrl);

            // 有的页面通过Group影响linkLayout的显示和隐藏，并且写在xml文件中，不好处理，例如竖视频详情页
            // 所以在重写setVisibility方法，加判断，如果title为空，即使调用setVisibility(VISIBLE)也没效果
            // 这里如果想要setVisibility(VISIBLE)生效，就先更新title和value
            bottomBinding.linkLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updateQualityComment(MediaModel model) {
        if (bottomBinding == null) {
            return;
        }
        if (model.qualityComment == null) {
            bottomBinding.qualityComment.setVisibility(View.GONE);
        } else {
            bottomBinding.qualityComment.setVisibility(View.VISIBLE);
            bottomBinding.qualityComment.setPlayOnClickListener(v -> {
                AviationSoundView gVideoSoundView = bottomBinding.qualityComment.getGVideoSoundView();
                if (gVideoSoundView == null) {
                    return;
                }
                AudioPlayManager.getInstance().start(gVideoSoundView);
                MediaPlayManager.getInstance().stop(model.tabId);
            });
            bottomBinding.qualityComment.update(model.qualityComment);
        }
    }

    protected void updateQuestionLayout(AviationTextView content) {
        QuestionModel questionModel = mMediaModel.getQuestionVO();
        if (questionModel != null) {
            if (!TextUtils.isEmpty(questionModel.content)) {
                SpannableString result;
                if (!TextUtils.isEmpty(questionModel.title)) {
                    result = new SpannableString(questionModel.content);
                } else {
                    result = new SpannableString("a " + questionModel.content);
                    Drawable drawable = ContextCompat.getDrawable(
                            content.getContext(),
                            R.drawable.ic_question_yellow_white
                    );
                    if (drawable != null) {
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        CenterAlignImageSpan span = new CenterAlignImageSpan(drawable);
                        result.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    }
                }
                content.setText(result);
                content.setVisibility(VISIBLE);
            } else {
                content.setText("");
                content.setVisibility(GONE);
            }
        } else {
            content.setText("");
            content.setVisibility(GONE);
        }
    }

    /**
     * 设置新闻来源最大长度
     *
     * @param textView
     */
    public void setNewsSourceMaxWidth(TextView textView) {
        Context context = GVideoRuntime.getAppContext();
        int maxWidth = context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_265DP);
        if (mMediaModel.getMediaType() == MediaType.NEWS_RIGHT_IMAGE) {
            maxWidth = context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_165DP);
        }
        if (!TextUtils.isEmpty(mMediaModel.getContentLabel())) {
            maxWidth = maxWidth - context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_55DP);
        }
        if (DateUtils.friendlyTime(mMediaModel.getCreateDate()).length() > 5) {
            maxWidth = maxWidth - context.getResources().getDimensionPixelOffset(R.dimen.DIMEN_20DP);
        }
        textView.setMaxWidth(maxWidth);
    }
}
