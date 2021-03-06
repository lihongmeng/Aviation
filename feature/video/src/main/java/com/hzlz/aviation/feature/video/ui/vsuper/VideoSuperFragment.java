package com.hzlz.aviation.feature.video.ui.vsuper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COMMENT_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COMMENT_TYPE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SEND_VIDEO_DATA_TO_PLAY;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.ui.detail.comment.CommentFragment;
import com.hzlz.aviation.feature.video.ui.detail.recommend.RecommendFragment;
import com.hzlz.aviation.feature.video.ui.detail.series.SeriesFragment;
import com.hzlz.aviation.feature.video.ui.vsuper.info.VideoSuperInfoViewModel;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfo;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.view.tab.GVideoSmartTabLayout;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.VideoSuperFragmentBinding;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * ??????????????????
 */
public class VideoSuperFragment extends BaseFragment<VideoSuperFragmentBinding> {

    // ?????????Fragment?????????
    private static final int VIDEO_SUPER_FRAGMENT_SIZE = 2;

    // VideoSuperViewModel
    private VideoSuperViewModel viewModel;

    // ?????????
    private GVideoView superPlayerView;

    // ??????????????????????????????ViewPager???????????????????????????????????????
    private boolean startComment;

    // ????????????
    private int commentType;

    // ??????Id
    private long commentId;

    // ??????Fragment
    private CommentFragment commentFragment;

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        commentId = bundle.getLong(COMMENT_ID);
        commentType = bundle.getInt(COMMENT_TYPE);
    }

    private final BackPressHandler mBackPressHandler = () -> {
        if (superPlayerView != null) {
            return superPlayerView.handleBackPressed();
        }
        return false;
    };

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
        if (viewModel!=null){
            viewModel.setVisible(true);
        }
        if (superPlayerView != null) {
            viewModel.resume(superPlayerView);
        }
        ImmersiveUtils.enterImmersive(this, Color.BLACK, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
        if (viewModel != null) {
            viewModel.setVisible(false);
            viewModel.statPlay();
            if (superPlayerView != null) {
                viewModel.pause(superPlayerView);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (superPlayerView != null) {
            viewModel.destroy(superPlayerView);
        }
    }

    @Override
    public String getPid() {
        return StatPid.DETAIL;
    }

    @Override
    public VideoModel getVideoModel() {
        if (viewModel == null) {
            return null;
        }
        return viewModel.videoModel;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_super_fragment;
    }

    @Override
    protected void initView() {

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);

        initInputPanelListener();

        GVideoEventBus.get(Constants.EXTRA_COMMENT)
                .observe(this, o -> {
                    if (!isVisible()) return;
                    mBinding.viewPager.setCurrentItem(0);
                });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO, String.class)
                .observe(this, o -> superPlayerView.setMute(true));

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME, String.class)
                .observe(this, o -> superPlayerView.setMute(false));

        GVideoEventBus.get(SEND_VIDEO_DATA_TO_PLAY, VideoModel.class)
                .observe(this, this::dealVideoModelAndLoad);

    }

    /**
     * ?????????????????????
     * <p>
     * ????????????
     * 1????????????????????????VideoModel????????????
     * 2????????????????????????VideoModel????????????????????????????????????????????????
     * 3???????????????????????????VideoModel??????????????????????????????????????????Id
     *
     * @param videoModel VideoModel??????
     */
    private void dealVideoModelAndLoad(VideoModel videoModel) {
        if (videoModel == null || viewModel == null) {
            return;
        }
        String mediaId = videoModel.getId();
        if (TextUtils.isEmpty(mediaId)) {
            return;
        }

        // ??????????????????????????????viewModel???
        viewModel.videoModel = videoModel;

        // ????????????????????????????????????????????????????????????????????????????????????????????????
        if (TextUtils.isEmpty(videoModel.getUrl())) {
            viewModel.loadInfo(mediaId);
            return;
        }

        // ???????????????
        loadVideoModel(videoModel);

        // ????????????2?????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????????????????????????????????????????????????????
        // ???????????????????????????VideoModel????????????????????????title??????
        viewModel.loadInfo(mediaId);
    }

    /**
     * ??????VideoModel??????????????????????????????
     *
     * @param videoModel VideoModel
     */
    private void loadVideoModel(VideoModel videoModel) {
        String mediaId = videoModel.getId();
        if (TextUtils.isEmpty(mediaId)) {
            return;
        }

        // ?????????VideoModel??????????????????????????????
        viewModel.statFromModel = new StatFromModel(
                mediaId,
                getPid(),
                "",
                viewModel.fromPid,
                viewModel.fromChannelId
        );

        // ???????????????Fragment
        initViewPager(videoModel, startComment);

        // ????????????Fragment?????????TabLayout
        initSliding(mBinding.sliding, startComment);

        // ???VideoSuperInfoViewModel?????????XML?????????
        VideoSuperInfoViewModel videoSuperInfoViewModel = new VideoSuperInfoViewModel(
                GVideoRuntime.getApplication(),
                videoModel
        );
        videoSuperInfoViewModel.setStatFromModel(viewModel.statFromModel);
        mBinding.info.setViewModel(videoSuperInfoViewModel);
        mBinding.info.setVideoObservable(videoModel.getObservable());

        // ????????????????????????
        AuthorModel authorModel = videoModel.getAuthor();
        if (authorModel != null) {
            mBinding.info.setAuthorObservable(authorModel.getObservable());
        }

        // ?????????????????????????????????????????????
        int playerType = videoModel.getPlayerType();
        superPlayerView = new GVideoView(getContext(), playerType);
        mBinding.detail.addView(superPlayerView, mBinding.playerContainer.getLayoutParams());
        superPlayerView.setAutoFollowSystemRotation(true);
//        mBinding.detail.postDelayed(() -> superPlayerView.registerGravitySensor(), 2000);
        viewModel.setVideoListener(superPlayerView);
        viewModel.start(superPlayerView);

        // ????????????????????????
        if (TextUtils.isEmpty(videoModel.outShareUrl)) {
            mBinding.info.dividerTop.setVisibility(GONE);
            mBinding.info.linkLayout.setVisibility(GONE);
        } else {
            mBinding.info.dividerTop.setVisibility(VISIBLE);
            mBinding.info.linkLayout.updateLinkTitle(TextUtils.isEmpty(videoModel.outShareTitle)
                    ? ResourcesUtils.getString(R.string.share_link_default_tip) : videoModel.outShareTitle);
            mBinding.info.linkLayout.updateLinkValue(videoModel.outShareUrl);
            mBinding.info.linkLayout.setVisibility(VISIBLE);
        }
    }


    @Override
    protected void bindViewModels() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        viewModel = bingViewModel(VideoSuperViewModel.class);
        viewModel.videoModel = arguments.getParcelable(Constants.EXTRA_VIDEO_MODEL);
        if (viewModel.videoModel == null) {
            return;
        }
        startComment = arguments.getBoolean(Constants.EXTRA_COMMENT);
        viewModel.fromPid = arguments.getString(Constant.EXTRA_FROM_PID);
        viewModel.fromChannelId = arguments.getString(Constant.EXTRA_FROM_CHANNEL_ID);

        // ???viewModel????????????????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????
        viewModel.videoModelLiveData.observe(this, o ->{
            if(viewModel==null){
                return;
            }
            loadVideoModel(viewModel.videoModel);
        });

        viewModel.titleLiveData.observe(this,title-> mBinding.info.title.setContentText(title));

        viewModel.fullscreenLiveData.observe(this, fullscreen -> {
            if (fullscreen) {
                mBinding.refreshLayout.setVisibility(GONE);
                mBinding.inputPanel.getRoot().setVisibility(GONE);
            } else {
                mBinding.refreshLayout.setVisibility(VISIBLE);
                mBinding.inputPanel.getRoot().setVisibility(VISIBLE);
            }
        });


        viewModel.backPressLiveData.observe(this, o -> {
            try {
                if (!Navigation.findNavController(superPlayerView).navigateUp()) {
                    requireActivity().finish();
                }
            }catch (Exception exception){
                exception.printStackTrace();
            }
        });

    }

    @Override
    protected void loadData() {
        if (viewModel == null) {
            return;
        }
        dealVideoModelAndLoad(viewModel.videoModel);
    }

    private void initViewPager(VideoModel videoModel, boolean startComment) {
        if (videoModel == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MEDIA_ID, videoModel.getId());
        bundle.putString(Constant.EXTRA_FROM_PID, viewModel.getStatFromModel().fromPid);
        bundle.putString(Constant.EXTRA_FROM_CHANNEL_ID, viewModel.getStatFromModel().fromChannelId);
        bundle.putBoolean(Constants.EXTRA_CAN_COMMENT, videoModel.isCanComment());
        bundle.putLong(COMMENT_ID, commentId);
        bundle.putInt(COMMENT_TYPE, commentType);

        // ??????sliding size?????????2
        FragmentPagerItems.Creator creator = FragmentPagerItems.with(requireContext())
                .add(R.string.comment, CommentFragment.class, bundle)
                .add(R.string.related_recommend, RecommendFragment.class, bundle);

        if (!TextUtils.isEmpty(videoModel.getColumnId())) {
            bundle.putString(Constant.EXTRA_COLUMN_ID, videoModel.getColumnId());
            creator.add(R.string.previous_review, SeriesFragment.class, bundle);
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), creator.create()
        );

        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(adapter.getCount());
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeTabTextStyle(VIDEO_SUPER_FRAGMENT_SIZE, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        int position = startComment ? 0 : 1;
        mBinding.viewPager.setCurrentItem(position);

        mBinding.viewPager.post(() -> {
            commentFragment = (CommentFragment) adapter.getPage(0);
        });
    }

    private void initSliding(GVideoSmartTabLayout sliding, boolean startComment) {
        sliding.useDefaultStyle(false);
        sliding.setEnableChangeTextSize(false);
        sliding.setDefaultTabTextColor(ResourcesUtils.getColorStateList(R.drawable.video_tab_layout_text_color));
        sliding.setViewPager(mBinding.viewPager);
        changeTabTextStyle(VIDEO_SUPER_FRAGMENT_SIZE, startComment ? 0 : 1);
    }

    private void changeTabTextStyle(int size, int position) {
        for (int index = 0; index < size; index++) {
            View tab = mBinding.sliding.getTabAt(index);
            if (tab instanceof TextView) {
                ((TextView) tab).setTypeface(Typeface.defaultFromStyle(
                        index == position ? Typeface.BOLD : Typeface.NORMAL));
            }
        }
    }

    // ????????????????????????????????????
    private void initInputPanelListener() {
        mBinding.inputPanel.audioComment.setOnClickListener(
                v -> {
                    if (checkCanComment()) {
                        showToast(R.string.video_comment_cant_comment);
                        return;
                    }
                    if (commentFragment == null) {
                        return;
                    }
                    commentFragment.onClickShowAudioDialog();
                });

        mBinding.inputPanel.commentTxt.setOnClickListener(
                v -> {
                    if (checkCanComment()) {
                        showToast(R.string.video_comment_cant_comment);
                        return;
                    }
                    if (commentFragment == null) {
                        return;
                    }
                    commentFragment.onClickShowTxtInputDialog();
                });

        mBinding.inputPanel.imageComment.setOnClickListener(
                v -> {
                    if (checkCanComment()) {
                        showToast(R.string.video_comment_cant_comment);
                        return;
                    }
                    if (commentFragment == null) {
                        return;
                    }
                    commentFragment.onClickShowImageDialog();
                });
    }

    private boolean checkCanComment() {
        if (viewModel==null||viewModel.videoModel == null) {
            return true;
        }
        boolean canComment = viewModel.videoModel.isCanComment();
        if (!canComment) {
            showToast(R.string.video_comment_cant_comment);
            return true;
        }
        return false;
    }

    @Override
    public void onReload(@NonNull View view) {
        super.onReload(view);
        loadData();
    }

    @Override
    protected Long getTenantId() {
        if (viewModel == null
                || viewModel.videoModel == null) {
            return null;
        }
        GroupInfo groupInfo=viewModel.videoModel.getGroupInfo();
        if(groupInfo==null){
            return null;
        }
        return groupInfo.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (viewModel == null
                || viewModel.videoModel == null) {
            return "";
        }
        GroupInfo groupInfo=viewModel.videoModel.getGroupInfo();
        if(groupInfo==null){
            return "";
        }
        return groupInfo.tenantName;
    }
}
