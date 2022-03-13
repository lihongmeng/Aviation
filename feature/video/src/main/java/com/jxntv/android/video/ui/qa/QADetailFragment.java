package com.jxntv.android.video.ui.qa;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jxntv.android.liteav.controller.GVideoFeedWBController;
import com.jxntv.android.liteav.model.GVideoPlayerRenderMode;
import com.jxntv.android.video.R;
import com.jxntv.android.video.databinding.FragmentQaDetailBinding;
import com.jxntv.android.video.databinding.ViewQaDetailTopBinding;
import com.jxntv.android.video.ui.BaseCommentFragment;
import com.jxntv.android.video.ui.detail.comment.CommentAdapter;
import com.jxntv.android.video.ui.vtxt.ImageTxtAudioViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.model.circle.GroupInfo;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.base.utils.AnimUtils;
import com.jxntv.base.view.recyclerview.RecyclerViewVideoOnScrollListener;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.player.AudioPlayManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

/**
 * @author huangwei
 * date : 2021/9/1
 * desc : 问答详情页
 **/
public class QADetailFragment extends BaseCommentFragment<FragmentQaDetailBinding> {

    private ViewQaDetailTopBinding topBinding;
    private ImageTxtAudioViewModel imageTxtAudioViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qa_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        setRightOperationImage(R.drawable.media_toolbar_share);

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);
        mRefreshLayout.setEnableFooterFollowWhenNoMoreData(false);

        GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE).observe(
                this, o -> finishActivity()
        );
    }

    @Override
    public void onRightOperationPressed(@NonNull View view) {
        imageTxtAudioViewModel.onMoreClick(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindViewModels() {
        super.bindViewModels();
        LayoutInflater inflaters = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View topView = inflaters.inflate(R.layout.view_qa_detail_top, null);

        topBinding = DataBindingUtil.bind(topView);
        if (topBinding == null) {
            return;
        }

        ((CommentAdapter) mAdapter).addHeaderView(topView);
        commentViewModel.isQaType();

        imageTxtAudioViewModel = bingViewModel(ImageTxtAudioViewModel.class);
        imageTxtAudioViewModel.setStatFromModel(getStat());
        imageTxtAudioViewModel.loadData(mediaId);

        topBinding.setViewModel(imageTxtAudioViewModel);

        imageTxtAudioViewModel.videoModelLiveData.observe(this, o -> {
            imageTxtAudioViewModel.videoModel.setPid(getPid());

            this.videoModel = imageTxtAudioViewModel.videoModel;
            ((CommentAdapter) mAdapter).setStatFromModel(getStat(), videoModel);
            commentViewModel.setStatFromModel(getStat(), videoModel);
            if (videoModel.getGroupInfo() != null) {
                commentViewModel.setGroupId(videoModel.getGroupInfo().groupId);
            }
            topBinding.setVideoModel(videoModel);
            topBinding.setCommentModel(commentViewModel);
            mBinding.setViewModel(imageTxtAudioViewModel);
            mBinding.setCommentModel(commentViewModel);
            mBinding.setVideoModel(videoModel);
            mBinding.setVideoObservable(videoModel.getObservable());
            mBinding.setAuthorObservable(videoModel.getAuthor().getObservable());
            AuthorModel author = videoModel.getAuthor();
            if (author != null) {
                topBinding.setAuthorObservable(author.getObservable());
                mBinding.setAuthorObservable(author.getObservable());
            }
            topBinding.setVideoObservable(videoModel.getObservable());

            if (videoModel.isAudioTxt()) {
                topBinding.soundView.setSoundUrl(videoModel.getMediaUrls().get(0));
                topBinding.soundView.setTotalSecondTime(videoModel.getLength());
                topBinding.soundView.setSoundText(videoModel.getSoundContent());
                topBinding.soundView.setVideoModel(videoModel);
                topBinding.soundView.setPlayOnClickListener(view -> {
                    AudioPlayManager.getInstance().start(topBinding.soundView);
                });
            } else if (videoModel.isMedia()) {
                initVideoView(videoModel.getMediaUrls().get(0), videoModel.isVerticalMedia());
            }

//            mBinding.buttonFollow.setFollowButton(videoModel.getAuthor().isFollow());

            imageTxtAudioViewModel.updateToolBarStatus(videoModel);
            mRecyclerView.postDelayed(checkListItemRunnable, 300);

            if (TextUtils.isEmpty(videoModel.outShareUrl)) {
                topBinding.linkLayout.setVisibility(View.GONE);
            } else {
                topBinding.linkLayout.updateLinkTitle(TextUtils.isEmpty(videoModel.outShareTitle)
                        ? ResourcesUtils.getString(com.jxntv.media.R.string.share_link_default_tip) : videoModel.outShareTitle);
                topBinding.linkLayout.updateLinkValue(videoModel.outShareUrl);

                // 有的页面通过Group影响linkLayout的显示和隐藏，并且写在xml文件中，不好处理，例如竖视频详情页
                // 所以在重写setVisibility方法，加判断，如果title为空，即使调用setVisibility(VISIBLE)也没效果
                // 这里如果想要setVisibility(VISIBLE)生效，就先更新title和value
                topBinding.linkLayout.setVisibility(View.VISIBLE);
            }

        });

        imageTxtAudioViewModel.needFinish.observe(this, needFinish -> finishActivity());

        imageTxtAudioViewModel.followLiveData.observe(this, follow -> {
//            mBinding.buttonFollow.setFollowButton(follow);
        });

//        mBinding.back.setOnClickListener(view -> finishActivity());

        int dp52 = getResources().getDimensionPixelSize(R.dimen.DIMEN_52DP);
        mBinding.recycler.addOnScrollListener(new RecyclerViewVideoOnScrollListener(mBinding.recycler,
                new RecyclerViewVideoOnScrollListener.onScrolledPositionListener() {
                    @Override
                    public void onScrolled(int fistVisible, int lastVisible) {

                    }

                    @Override
                    public void onScrollStateChanged(int fistVisible, int lastVisible) {

                    }

                    @Override
                    public void onItemEnter(int position) {
                        if (position == 0) {
                            mBinding.layoutBottomComment.setVisibility(GONE);
                            mRefreshLayout.setPadding(0, 0, 0, 0);
                            AnimUtils.setHideAnimationDown(mBinding.layoutBottomComment,
                                    mBinding.layoutBottomComment, 300, 1f, 0f, null);
                        }
                    }

                    @Override
                    public void onItemExit(int position) {
                        if (position == 0) {
                            mBinding.layoutBottomComment.setVisibility(INVISIBLE);
                            AnimUtils.setShowAnimationUp(mBinding.layoutBottomComment,
                                    mBinding.layoutBottomComment, 300, 1f, () -> {
                                        mRefreshLayout.setPadding(0, 0, 0, dp52);
                                    });
                        }
                    }
                }));

        GVideoEventBus.get(Constant.EVENT_MSG.VIDEO_NEED_PAUSE).observe(this, o ->
                topBinding.videoView.seek(100));
    }


    @Override
    public String getPid() {
        return StatPid.DETAIL_QA;
    }


    /**
     * 初始化播放器
     *
     * @param url        播放地址
     * @param isVertical 是否竖视频
     */
    private void initVideoView(String url, boolean isVertical) {

        if (TextUtils.isEmpty(url)) {
            topBinding.videoView.setVisibility(GONE);
            return;
        }

        topBinding.videoView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN);
        if (isVertical) {
            int dp220 = getResources().getDimensionPixelSize(R.dimen.DIMEN_220DP);
            int dp320 = getResources().getDimensionPixelSize(R.dimen.DIMEN_320DP);
            //设置播放器大小
            ViewGroup.LayoutParams videoParams = topBinding.videoView.getLayoutParams();
            videoParams.width = dp220;
            videoParams.height = dp320;
            topBinding.videoView.setLayoutParams(videoParams);

            topBinding.videoView.setMediaController(new GVideoFeedWBController(getContext(), () -> {

            }));
        } else {
            topBinding.videoView.initDefaultControllerLayer();
        }

        topBinding.videoView.startPlay(url,videoModel);

        topBinding.videoView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                topBinding.videoView.pause();
            }
        });

    }

    @Override
    public VideoModel getVideoModel() {
        if (imageTxtAudioViewModel == null) {
            return null;
        }
        return imageTxtAudioViewModel.videoModel;
    }

    @Override
    public void onPause() {
        super.onPause();
        topBinding.videoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        topBinding.videoView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        topBinding.videoView.release();
    }

    @Override
    protected Long getTenantId() {
        if (imageTxtAudioViewModel == null
                || imageTxtAudioViewModel.videoModel == null) {
            return null;
        }
        GroupInfo groupInfo = imageTxtAudioViewModel.videoModel.getGroupInfo();
        if (groupInfo == null) {
            return null;
        }
        return groupInfo.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (imageTxtAudioViewModel == null
                || imageTxtAudioViewModel.videoModel == null) {
            return "";
        }
        GroupInfo groupInfo = imageTxtAudioViewModel.videoModel.getGroupInfo();
        if (groupInfo == null) {
            return "";
        }
        return groupInfo.tenantName;
    }

}
