package com.jxntv.media.template.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.jxntv.android.liteav.view.CoverView;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.tag.TagHelper;
import com.jxntv.media.BR;
import com.jxntv.media.R;
import com.jxntv.media.databind.MediaVideoDataBind;
import com.jxntv.media.databinding.MediaTplMusicBinding;
import com.jxntv.media.model.MediaModel;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * media 音频模板视图
 */
public class MediaSoundTemplate extends MediaBaseVideoTemplate {

  /** 当前数据模型 */
  private MediaTplMusicBinding mBinding;

  /**
   * 构造函数
   */
  public MediaSoundTemplate(Context context, ViewGroup parent) {
    super(context);
    mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.media_tpl_music,
        parent, false);
    topBinding = mBinding.feedToolbarTop;
    bottomBinding = mBinding.feedToolbarInclude;
    mPlayerView = mBinding.musicInclude.playerView;
    playerViewLayout = mBinding.musicInclude.playerViewLayout;
    initCommonView();
  }

  @Override
  public TextView getTitleView() {
    return mBinding.musicInclude.soundFeedTitle;
  }

  @Override
  public void update(@NonNull MediaModel feedModel, boolean isDarkMode, String fragmentId,int position) {
    if (feedModel.getMediaType() != MediaType.LONG_AUDIO
        && feedModel.getMediaType() != MediaType.SHORT_AUDIO) {
      return;
    }
    mBinding.musicInclude.setVariable(BR.feedModel, feedModel);

    if (mFeedVideoDataBind == null) {
      mFeedVideoDataBind = new MediaVideoDataBind(this, feedModel,fragmentId,position);
    }
    mBinding.musicInclude.setVariable(BR.videoBind, mFeedVideoDataBind);
    mBinding.musicInclude.executePendingBindings();

    super.update(feedModel, isDarkMode, fragmentId,position);
    updateQualityComment(feedModel);
    updateLinkLayout(feedModel);

    getRootLayout().requestLayout();
    getRootLayout().invalidate();
  }

  @Override
  protected ViewGroup getPlayerViewParent() {
    return mBinding.musicInclude.playerViewLayout;
  }

  @Override
  protected TextView getTitleTextView() {
    return mBinding.musicInclude.soundTitle;
  }

  @Override
  protected boolean handleInterceptFullScreenEvent() {
    if (mMediaModel == null) {
      return true;
    }
    if (mMediaModel.getMediaType() == MediaType.LONG_AUDIO) {
      return false;
    }
    handleNavigateToDetail(false);
    return true;
  }

  @Override
  public MediaModel getMediaModel() {
    return super.getMediaModel();
  }

  @Override
  public ViewDataBinding getDataBinding() {
    return mBinding;
  }

  @Override
  protected ViewGroup getRootLayout() {
    return mBinding.musicLayout;
  }

  @Override
  public void play() {
    super.play();
    int coverType = CoverView.SOUND_MUSIC;
    if (mMediaModel.getTagType() == TagHelper.FEED_TAG_LIVE) {
      coverType = CoverView.SOUND_FM;
    }
    mPlayerView.setSoundCover(mMediaModel.getCoverUrl(), coverType);

    mBinding.musicInclude.soundImg.setVisibility(GONE);
    mBinding.musicInclude.soundStatusImg.setVisibility(GONE);
    mBinding.musicInclude.videoBigImg.setVisibility(GONE);
    if (mMediaModel.showInPgc()) {
      mBinding.musicInclude.titleMask.setVisibility(GONE);
      mBinding.musicInclude.soundTitle.setVisibility(GONE);
    }
  }

  @Override public void resume() {
    super.resume();
    int coverType = CoverView.SOUND_MUSIC;
    if (mMediaModel.getTagType() == TagHelper.FEED_TAG_LIVE) {
      coverType = CoverView.SOUND_FM;
    }
    mPlayerView.setSoundCover(mMediaModel.getCoverUrl(), coverType);

    mBinding.musicInclude.soundImg.setVisibility(GONE);
    mBinding.musicInclude.soundStatusImg.setVisibility(GONE);
    mBinding.musicInclude.videoBigImg.setVisibility(GONE);
    if (mMediaModel.showInPgc()) {
      mBinding.musicInclude.titleMask.setVisibility(GONE);
      mBinding.musicInclude.soundTitle.setVisibility(GONE);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mBinding.musicInclude.soundImg.setVisibility(VISIBLE);
    mBinding.musicInclude.soundStatusImg.setVisibility(VISIBLE);
    mBinding.musicInclude.videoBigImg.setVisibility(VISIBLE);
    if (mMediaModel.showInPgc()) {
      mBinding.musicInclude.titleMask.setVisibility(VISIBLE);
      mBinding.musicInclude.soundTitle.setVisibility(VISIBLE);
    }
  }

  @Override
  public void stop() {
    super.stop();
    mBinding.musicInclude.soundImg.setVisibility(VISIBLE);
    mBinding.musicInclude.soundStatusImg.setVisibility(VISIBLE);
    mBinding.musicInclude.videoBigImg.setVisibility(VISIBLE);
    if (mMediaModel.showInPgc()) {
      mBinding.musicInclude.titleMask.setVisibility(VISIBLE);
      mBinding.musicInclude.soundTitle.setVisibility(VISIBLE);
    }
  }
}
