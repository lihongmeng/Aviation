package com.jxntv.android.video.ui.vshort;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import com.jxntv.android.liteav.controller.VideoControllerView;
import com.jxntv.android.video.databinding.VideoShortControllerBinding;

public class VideoShortController extends VideoControllerView {
  private static final int sDefaultTimeout = -1;
  private VideoShortControllerBinding mProgressBinding;
  public VideoShortController(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VideoShortController(Context context) {
    super(context);
  }

  @Override protected View makeControllerView() {
    //super.makeControllerView();
    mProgressBinding = VideoShortControllerBinding.inflate(LayoutInflater.from(getContext()));
    mRoot = mProgressBinding.getRoot();
    mSimpleProgress = mProgressBinding.simpleProgress;
    initControllerView(mRoot);
    return mRoot;
  }

  @Override public void show() {
    super.show(sDefaultTimeout);
  }

  VideoShortControllerBinding getBinding() {
    return mProgressBinding;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    return false;
  }
}
