package com.hzlz.aviation.feature.video.ui;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COMMENT_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COMMENT_TYPE;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.ui.detail.DetailAdapter;
import com.hzlz.aviation.feature.video.ui.detail.comment.CommentAdapter;
import com.hzlz.aviation.feature.video.ui.detail.comment.CommentViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.recycler.BaseRecyclerFragment;
import com.hzlz.aviation.kernel.base.view.GVideoFooterView;
import com.hzlz.aviation.kernel.base.view.GVideoHeaderView;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.hzlz.aviation.feature.video.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * @author huangwei
 * date : 2021/9/1
 * desc : 评论fragment基类
 **/
public abstract class BaseCommentFragment<T extends ViewDataBinding> extends BaseRecyclerFragment<CommentModel, T>
        implements OnRefreshLoadMoreListener {

    protected CommentViewModel commentViewModel;
    protected String mediaId;
    protected VideoModel videoModel;

    //是否需要滑动到第一条评论
    private boolean isNeedShowComment;
    //置顶评论类型
    private int commentType;
    //置顶评论id
    private long commentId;

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        videoModel = bundle.getParcelable(Constant.EXTRA_VIDEO_MODEL);
        mediaId = bundle.getString(Constants.EXTRA_MEDIA_ID);
        isNeedShowComment = bundle.getBoolean(Constants.EXTRA_COMMENT, false);
        commentId = bundle.getLong(COMMENT_ID);
        commentType = bundle.getInt(COMMENT_TYPE);

    }

    @NonNull
    @Override
    protected BaseRecyclerAdapter<CommentModel, ?> createAdapter() {
        CommentAdapter adapter = new CommentAdapter(requireContext(), mediaId);
        adapter.setStatFromModel(getStat(), videoModel);
        adapter.setPromptCommentId(commentId, commentType);
        adapter.mActionLiveData.observe(
                this,
                actionModel -> {
                    AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                    if (actionModel.type == DetailAdapter.ACTION_AVATAR
                            || actionModel.type == DetailAdapter.ACTION_TO_USER) {
                        accountPlugin.startPgcActivity(
                                mBinding.getRoot(), actionModel.model.commentUser
                        );
                    } else if (actionModel.type == DetailAdapter.ACTION_REMOVE) {
                        commentViewModel.deleteComment(actionModel.model);
                    } else if (actionModel.type == DetailAdapter.ACTION_REPLY) {
                        commentViewModel.showInputAllDialog(
                                getContext(), RecordPlugin.DIALOG_TXT, actionModel.model
                        );
                    } else if (actionModel.type == DetailAdapter.ACTION_REPORT) {
                        accountPlugin.showReportDialog(
                                getContext(),
                                String.valueOf(actionModel.model.primaryId),
                                1,
                                videoModel == null ? "" : videoModel.getPid()
                        );
                    } else if (actionModel.type == DetailAdapter.ACTION_PRAISE) {
                        commentViewModel.praiseComment(actionModel.model);
                        // showToast("点赞 "+ actionModel.model.commentUser.getName());
                    }
                });
        return adapter;
    }

    @Override
    protected void bindViewModels() {

        commentViewModel = bingViewModel(CommentViewModel.class);
        commentViewModel.setMediaId(mediaId);
        commentViewModel.setComment(commentId, commentType,false);
        commentViewModel.setCanComment(true);
        commentViewModel.setStatFromModel(getStat(), videoModel);

        commentViewModel.mNoMoreLiveData.observe(this, noMoreData -> {
            mRefreshLayout.finishGVideoLoadMore();
            mRefreshLayout.finishGVideoRefresh();
            mRefreshLayout.setNoMoreData(noMoreData);
        });

        commentViewModel.mIsEmptyLiveData.observe(this, noData -> {
            if (noData) {
                if (mAdapter instanceof CommentAdapter && mAdapter.getFooterViewCount()==0) {
                    ((CommentAdapter) mAdapter).addFooterView(getFootView());
                    mRefreshLayout.setEnableLoadMore(false);
                }
            } else {
                ((CommentAdapter) mAdapter).removeFooterView(getFootView());
                mRefreshLayout.setEnableLoadMore(true);
            }
        });

    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    protected boolean enableRefresh() {
        return false;
    }

    @Override
    protected int initRecyclerViewId() {
        return R.id.recycler;
    }

    @Override
    protected int initPlaceHolderId() {
        return R.id.placeholder;
    }

    @Override
    protected int initRefreshViewId() {
        return R.id.refreshLayout;
    }

    @Override
    protected RefreshFooter createRefreshFooterView() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return new GVideoFooterView(activity);
    }

    @Override
    protected RefreshHeader createRefreshHeaderView() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return new GVideoHeaderView(activity);
    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        super.onLeftBackPressed(view);
        getActivity().finish();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        commentViewModel.loadMoreData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        commentViewModel.loadRefreshData();
    }

    protected StatFromModel getStat() {
        String pid = getPid();
        String channelId = "";
        String fromPid = getArguments() != null ? getArguments().getString(Constant.EXTRA_FROM_PID) : "";
        String fromChannelId = getArguments() != null ? getArguments().getString(Constant.EXTRA_FROM_CHANNEL_ID) : "";
        return new StatFromModel(mediaId, pid, channelId, fromPid, fromChannelId);
    }

    private int count = 30;
    protected final Runnable checkListItemRunnable = new Runnable() {

        @Override
        public void run() {
            if (isNeedShowComment && mRecyclerView.getChildCount() > 0) {
                mRecyclerView.post(() -> mRecyclerView.smoothScrollToPosition(1));
                isNeedShowComment = false;
            } else {
                if (count > 0) {
                    count--;
                    mRecyclerView.postDelayed(checkListItemRunnable, 300);
                }
            }
        }
    };

    @Override
    protected void loadData() {

        onRefresh(mRefreshLayout);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0 && itemCount == 1) {
                    mRecyclerView.scrollToPosition(0);
                    GVideoEventBus.get(Constants.EXTRA_COMMENT).post(0);
                }
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qa_detail;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
        ImmersiveUtils.enterImmersive(this, Color.WHITE, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
        AudioPlayManager.getInstance().release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioPlayManager.getInstance().release();
    }

    private AviationTextView footerView;
    private View getFootView(){
        if (footerView==null){
            footerView = new AviationTextView(getContext());
            int d60 = getResources().getDimensionPixelOffset(R.dimen.DIMEN_60DP);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,d60);
            footerView.setGravity(Gravity.CENTER);
            if(videoModel != null && videoModel.getAnswerSquareId()>0){
                footerView.setText("暂时还没有回答，开始写第一个回答");
            }else {
                footerView.setText(R.string.all_not_found_comment);
            }
            footerView.setTextSize(14);
            footerView.setLayoutParams(layoutParams);
        }
        return footerView;
    }

}
