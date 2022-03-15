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
 * 横视频详情页
 */
public class VideoSuperFragment extends BaseFragment<VideoSuperFragmentBinding> {

    // 下方子Fragment的数量
    private static final int VIDEO_SUPER_FRAGMENT_SIZE = 2;

    // VideoSuperViewModel
    private VideoSuperViewModel viewModel;

    // 播放器
    private GVideoView superPlayerView;

    // 上层指定，是否需要在ViewPager渲染完毕后显示评论相关数据
    private boolean startComment;

    // 评论类型
    private int commentType;

    // 评论Id
    private long commentId;

    // 评论Fragment
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
     * 处理并加载数据
     * <p>
     * 几种情况
     * 1、上层传递完整的VideoModel数据进来
     * 2、上层传递完整的VideoModel数据进来，但是不能用（新闻专题）
     * 3、上层传递不完整的VideoModel数据进来，点击推送，里面只有Id
     *
     * @param videoModel VideoModel数据
     */
    private void dealVideoModelAndLoad(VideoModel videoModel) {
        if (videoModel == null || viewModel == null) {
            return;
        }
        String mediaId = videoModel.getId();
        if (TextUtils.isEmpty(mediaId)) {
            return;
        }

        // 不管哪种情况，先存在viewModel中
        viewModel.videoModel = videoModel;

        // 如果没有播放地址，说明是从推送启动的本页面，直接取详情接口的数据
        if (TextUtils.isEmpty(videoModel.getUrl())) {
            viewModel.loadInfo(mediaId);
            return;
        }

        // 先加载数据
        loadVideoModel(videoModel);

        // 由于情况2的存在，上层的数据虽然完整，但是部分字段不可信
        // 所以先网络拉取，在获取到之后判断是否要单独处理某些字段
        // 例如新闻专题传递的VideoModel数据需要单独处理title字段
        viewModel.loadInfo(mediaId);
    }

    /**
     * 根据VideoModel中的数据填充界面数据
     *
     * @param videoModel VideoModel
     */
    private void loadVideoModel(VideoModel videoModel) {
        String mediaId = videoModel.getId();
        if (TextUtils.isEmpty(mediaId)) {
            return;
        }

        // 先根据VideoModel中的数据填充界面数据
        viewModel.statFromModel = new StatFromModel(
                mediaId,
                getPid(),
                "",
                viewModel.fromPid,
                viewModel.fromChannelId
        );

        // 加载下方子Fragment
        initViewPager(videoModel, startComment);

        // 初始化子Fragment顶部的TabLayout
        initSliding(mBinding.sliding, startComment);

        // 将VideoSuperInfoViewModel传输到XML文件中
        VideoSuperInfoViewModel videoSuperInfoViewModel = new VideoSuperInfoViewModel(
                GVideoRuntime.getApplication(),
                videoModel
        );
        videoSuperInfoViewModel.setStatFromModel(viewModel.statFromModel);
        mBinding.info.setViewModel(videoSuperInfoViewModel);
        mBinding.info.setVideoObservable(videoModel.getObservable());

        // 加载作者相关信息
        AuthorModel authorModel = videoModel.getAuthor();
        if (authorModel != null) {
            mBinding.info.setAuthorObservable(authorModel.getObservable());
        }

        // 初始化播放器并开始播放视频数据
        int playerType = videoModel.getPlayerType();
        superPlayerView = new GVideoView(getContext(), playerType);
        mBinding.detail.addView(superPlayerView, mBinding.playerContainer.getLayoutParams());
        superPlayerView.setAutoFollowSystemRotation(true);
//        mBinding.detail.postDelayed(() -> superPlayerView.registerGravitySensor(), 2000);
        viewModel.setVideoListener(superPlayerView);
        viewModel.start(superPlayerView);

        // 加载外链相关数据
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

        // 当viewModel通过网络加载最新的视频数据，并且判定该数据需要渲染到界面
        // 就会触发当下回调的执行
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

        // 目前sliding size写死为2
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

    // 初始化下方操作框相关监听
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
