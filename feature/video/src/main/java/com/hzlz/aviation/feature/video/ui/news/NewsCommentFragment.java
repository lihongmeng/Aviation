package com.hzlz.aviation.feature.video.ui.news;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.recycler.BaseRecyclerFragment;
import com.hzlz.aviation.kernel.base.view.GVideoFooterView;
import com.hzlz.aviation.kernel.base.view.GVideoHeaderView;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.FragmentNewsCommentListBinding;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * @author huangwei
 * date : 2021/5/20
 * desc : 新闻评论
 **/
public class NewsCommentFragment extends BaseRecyclerFragment<CommentModel, FragmentNewsCommentListBinding> implements OnRefreshLoadMoreListener {

    private CommentViewModel commentViewModel;
    private String mediaId;
    private VideoModel videoModel;
    private CommentAdapter commentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_comment_list;
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
        return true;
    }

    @Override
    protected RefreshFooter createRefreshFooterView() {
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        return new GVideoFooterView(getActivity());
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

    @NonNull
    @Override
    protected BaseRecyclerAdapter<CommentModel, ?> createAdapter() {
        commentAdapter = new CommentAdapter(requireContext(), mediaId);
        commentAdapter.setStatFromModel(getStat(), videoModel);
        commentAdapter.mActionLiveData.observe(this,
                actionModel -> {
                    if (actionModel.type == DetailAdapter.ACTION_AVATAR
                            || actionModel.type == DetailAdapter.ACTION_TO_USER) {
                        PluginManager.get(AccountPlugin.class).startPgcActivity(
                                mBinding.recycler,
                                actionModel.model.commentUser
                        );
                    } else if (actionModel.type == DetailAdapter.ACTION_REMOVE) {
                        commentViewModel.deleteComment(actionModel.model);
                    } else if (actionModel.type == DetailAdapter.ACTION_REPLY) {
                        commentViewModel.showInputAllDialog(
                                getContext(),
                                RecordPlugin.DIALOG_TXT,
                                actionModel.model
                        );
                    } else if (actionModel.type == DetailAdapter.ACTION_REPORT) {
                        PluginManager.get(AccountPlugin.class).showReportDialog(
                                getContext(),
                                String.valueOf(actionModel.model.primaryId),
                                1,
                                videoModel.getPid()
                        );
                    } else if (actionModel.type == DetailAdapter.ACTION_PRAISE) {
                        commentViewModel.praiseComment(actionModel.model);
                    }
                });
        return commentAdapter;
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        mediaId = bundle.getString(Constants.EXTRA_MEDIA_ID);
        videoModel = bundle.getParcelable(Constants.EXTRA_VIDEO_MODEL);
    }

    @Override
    protected void initView() {
        super.initView();
        setToolbarTitle("全部评论");
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);
        mRefreshLayout.setEnableFooterFollowWhenNoMoreData(false);

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
    protected void bindViewModels() {
        commentViewModel = bingViewModel(CommentViewModel.class);
        commentViewModel.setMediaId(mediaId);
        commentViewModel.setCanComment(true);
        commentViewModel.setStatFromModel(getStat(), videoModel);
        commentViewModel.getNoMoreLiveData().observe(this, noMoreData -> mRefreshLayout.setNoMoreData(noMoreData));

        mBinding.inputPanel.setViewModel(commentViewModel);

    }

    @Override
    protected void loadData() {

        onRefresh(mBinding.refreshLayout);

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
    public void onLeftBackPressed(@NonNull View view) {
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

}
