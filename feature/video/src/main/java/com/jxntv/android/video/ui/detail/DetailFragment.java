package com.jxntv.android.video.ui.detail;

import static com.jxntv.base.Constant.BUNDLE_KEY.COMMENT_ID;
import static com.jxntv.base.Constant.BUNDLE_KEY.COMMENT_TYPE;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.jxntv.android.video.BuildConfig;
import com.jxntv.android.video.Constants;
import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.VideoSuperListBinding;
import com.jxntv.base.Constant;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.recycler.BaseRecyclerFragment;
import com.jxntv.base.view.GVideoFooterView;
import com.jxntv.base.view.GVideoHeaderView;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.stat.StatPid;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

public abstract class DetailFragment<CommentModel> extends BaseRecyclerFragment<CommentModel, VideoSuperListBinding> {
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TAG = DetailFragment.class.getSimpleName();

  protected VideoModel videoModel;
  protected String mediaId;
  protected long commentId;
  protected int commentType;

  @Override
  protected void onArgumentsHandle(Bundle bundle) {
    super.onArgumentsHandle(bundle);
    videoModel = bundle.getParcelable(Constant.EXTRA_VIDEO_MODEL);
    mediaId = bundle.getString(Constants.EXTRA_MEDIA_ID);
    commentId = bundle.getLong(COMMENT_ID);
    commentType = bundle.getInt(COMMENT_TYPE);
  }

//  protected String getMediaId() {
//    String mMediaId = getArguments() != null ? getArguments().getString(Constants.EXTRA_MEDIA_ID) : "";
//    return mMediaId;
//  }
//
  @Override
  public VideoModel getVideoModel() {
    VideoModel videoModel = getArguments() != null ? getArguments().getParcelable(Constant.EXTRA_VIDEO_MODEL) : null;
    return videoModel;
  }
//
//  public long getCommentId(){
//    return getArguments() != null ? getArguments().getLong(Constants.EXTRA_COMMENT_ID) : 0;
//  }
//
//  public int getCommentType(){
//    return getArguments() != null ? getArguments().getInt(Constants.EXTRA_COMMENT_TYPE) : 0;
//  }

  @NonNull
  protected abstract DetailViewModel<CommentModel> getDetailViewModel();

  protected StatFromModel getStat() {
    String pid = getPid();
    String channelId = "";
    String fromPid = getArguments() != null ? getArguments().getString(Constant.EXTRA_FROM_PID) : "";
    String fromChannelId = getArguments() != null ? getArguments().getString(Constant.EXTRA_FROM_CHANNEL_ID) : "";
    StatFromModel stat = new StatFromModel(mediaId, pid, channelId, fromPid, fromChannelId);
    return stat;
  }

  @Override public String getPid() {
    return StatPid.DETAIL;
  }

  @Override protected boolean enableLazyLoad() {
    return false;
  }

  @Override protected boolean showToolbar() {
    return false;
  }

  @Override protected boolean enableRefresh() {
    return false;
  }

  @Override protected RefreshFooter createRefreshFooterView() {
    Activity activity = getActivity();
    if (activity == null) {
      return null;
    }
    return new GVideoFooterView(activity);
  }

  @Override protected RefreshHeader createRefreshHeaderView() {
    Activity activity = getActivity();
    if (activity == null) {
      return null;
    }
    return new GVideoHeaderView(activity);
  }

  @Override protected int initRecyclerViewId() {
    return R.id.recycler;
  }

  @Override protected int initPlaceHolderId() {
    return R.id.placeholder;
  }

  @Override protected int initRefreshViewId() {
    return R.id.refreshLayout;
  }

  @Override protected int getLayoutId() {
    return R.layout.video_super_list;
  }

  @Override protected void initView() {
    super.initView();
    mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
    mRefreshLayout.setEnableFooterFollowWhenNoMoreData(true);
  }

  @Override protected void loadData() {
    onRefresh(mBinding.refreshLayout);
  }

  @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
    getDetailViewModel().loadMoreData();
  }

  @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
    getDetailViewModel().loadRefreshData();
  }

  @Override
  protected int getPlaceHolderPaddingTop() {
    return GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.video_dp_12);
  }

  @Override
  protected boolean needShowPlaceHolderOnTop() {
    return true;
  }
}
