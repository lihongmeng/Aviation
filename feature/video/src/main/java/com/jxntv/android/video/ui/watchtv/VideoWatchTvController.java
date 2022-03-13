package com.jxntv.android.video.ui.watchtv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.jxntv.android.liteav.LiteavConstants;
import com.jxntv.android.liteav.R;
import com.jxntv.android.liteav.controller.GVideoControllerBase;
import com.jxntv.android.liteav.databinding.LiteavControllerSmallBinding;
import com.jxntv.android.video.databinding.LayoutVideoWholePeriodControllerBinding;
import com.jxntv.base.tag.TagTextHelper;

/**
 * 小窗控制器
 */
public class VideoWatchTvController extends GVideoControllerBase {

  private LiteavControllerSmallBinding mBinding;

  public VideoWatchTvController(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VideoWatchTvController(Context context) {
    super(context);
  }

  @Override protected View makeControllerView() {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    mBinding = LiteavControllerSmallBinding.inflate(inflater);
    mRoot = mBinding.getRoot();

    mGestureVideoProgressLayout = mBinding.videoProgressLayout;
    mReplay = mBinding.ivReplay;
    mBack = mBinding.ivBack;

    initControllerView(mRoot);

    return mRoot;
  }

  @Override public void setTitleVisible(@LiteavConstants.TitleMode int mode) {
    if (mode == LiteavConstants.TITLE_MODE_ONLY_BACK) {
      mBinding.ivBack.setVisibility(View.VISIBLE);
      mBinding.tvTitle.setVisibility(View.GONE);
    } else if (mode == LiteavConstants.TITLE_MODE_ONLY_TITLE) {
      mBinding.ivBack.setVisibility(View.GONE);
      mBinding.tvTitle.setVisibility(View.VISIBLE);
    } else if (mode == LiteavConstants.TITLE_MODE_NONE) {
      mBinding.ivBack.setVisibility(View.GONE);
      mBinding.tvTitle.setVisibility(View.GONE);
      mBinding.layoutTop.setVisibility(View.GONE);
    } else {
      mBinding.ivBack.setVisibility(View.VISIBLE);
      mBinding.tvTitle.setVisibility(View.VISIBLE);
      mBinding.layoutTop.setVisibility(View.VISIBLE);
    }
  }

  @Override public void updateTitle(String title, int tag) {
    TagTextHelper.createTagTitle(getContext(), mBinding.tvTitle, title, tag,
            getContext().getResources().getColor(R.color.big_img_title_color));
  }

  LiteavControllerSmallBinding getBinding() {
    return mBinding;
  }

}
