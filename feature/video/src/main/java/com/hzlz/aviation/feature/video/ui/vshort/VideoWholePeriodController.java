package com.hzlz.aviation.feature.video.ui.vshort;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.hzlz.aviation.kernel.base.tag.TagTextHelper;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.R;
import com.hzlz.aviation.kernel.liteav.controller.GVideoControllerBase;
import com.hzlz.aviation.feature.video.databinding.LayoutVideoWholePeriodControllerBinding;

/**
 * 小窗控制器
 */
public class VideoWholePeriodController extends GVideoControllerBase {
  private LayoutVideoWholePeriodControllerBinding mBinding;
  public VideoWholePeriodController(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VideoWholePeriodController(Context context) {
    super(context);
  }

  @Override protected View makeControllerView() {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    mBinding = LayoutVideoWholePeriodControllerBinding.inflate(inflater);
    mRoot = mBinding.getRoot();
    //mSimpleProgress = mProgressBinding.simpleProgress;

    mGestureVideoProgressLayout = mBinding.videoProgressLayout;
    mReplay = mBinding.ivReplay;
    mBack = mBinding.ivBack;
    share = mBinding.share;
    screenProjection = mBinding.screenProjection;

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

  @Override
  public void setShareVisible(int mode) {
      mBinding.share.setVisibility(mode == LiteavConstants.TITLE_MODE_FULL ? GONE : VISIBLE);
  }

  @Override
  public void setScreenProjectionVisible(int mode) {
    mBinding.screenProjection.setVisibility(mode == LiteavConstants.TITLE_MODE_FULL ? GONE : VISIBLE);
  }


  @Override public void updateTitle(String title, int tag) {
    TagTextHelper.createTagTitle(getContext(), mBinding.tvTitle, title, tag,
            getContext().getResources().getColor(R.color.big_img_title_color));
  }

  LayoutVideoWholePeriodControllerBinding getBinding() {
    return mBinding;
  }

}
