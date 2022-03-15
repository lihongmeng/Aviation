package com.hzlz.aviation.feature.watchtv.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COLUMN_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.NEED_SEND_VIDEO_DATA_ONCE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.PROGRAM_ID;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SCREEN_PROJECTION_FAILED;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SELECT_SCREEN_PROJECTION;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SEND_VIDEO_DATA_TO_PLAY;
import static com.hzlz.aviation.kernel.base.model.anotation.MediaType.COLLECTION_DETAIL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.ui.detail.comment.CommentFragment;
import com.hzlz.aviation.feature.video.ui.vshort.VideoWholePeriodController;
import com.hzlz.aviation.feature.video.ui.vsuper.info.VideoSuperInfoViewModel;
import com.hzlz.aviation.feature.watchtv.entity.ColumnGroupInfo;
import com.hzlz.aviation.feature.watchtv.ui.select.SelectCollectionFragment;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.GroupInfo;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.screenprojection.DLNAHelper;
import com.hzlz.aviation.kernel.base.view.ScreenProjectionLayout;
import com.hzlz.aviation.kernel.base.view.tab.GVideoSmartTabLayout;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.feature.watchtv.R;
import com.hzlz.aviation.feature.watchtv.databinding.FragmentWatchTvWholePeriodDetailBinding;
import com.liuwei.android.upnpcast.NLUpnpCastManager;
import com.liuwei.android.upnpcast.controller.CastObject;
import com.liuwei.android.upnpcast.controller.ICastEventListener;
import com.liuwei.android.upnpcast.device.CastDevice;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;

/**
 * 整期详情页
 */
public class WatchTvWholePeriodDetailFragment extends BaseFragment<FragmentWatchTvWholePeriodDetailBinding> {

    /**
     * 本页面下方包含多少个子Fragment
     */
    private static final int VIDEO_SUPER_FRAGMENT_SIZE = 2;

    // WatchTvWholePeriodDetailViewModel
    private WatchTvWholePeriodDetailViewModel viewModel;

    // 播放器
    private GVideoView superPlayerView;

    private ScreenProjectionLayout screenProjectionLayout;

    private CommentFragment commentFragment;
    private String columnId, programId, fromPid;

    private boolean isFirstInit = true;

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        columnId = bundle.getLong(COLUMN_ID) + "";
        long id = bundle.getLong(PROGRAM_ID);
        programId = id > 0 ? String.valueOf(id) : "";
        fromPid = bundle.getString(Constant.EXTRA_FROM_PID);
    }

    private final BackPressHandler backPressHandler = () -> {

        if (viewModel != null) {
            viewModel.dealBack(superPlayerView);
        }

        if (superPlayerView != null) {
            return superPlayerView.handleBackPressed();
        }
        return false;
    };

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();

        ((BaseActivity) requireActivity()).registerBackPressHandler(backPressHandler);
        viewModel.setVisible(true);
        if (superPlayerView != null && !isScreenProjectionLayoutVisible()) {
            viewModel.resume(superPlayerView);
        }
        ImmersiveUtils.enterImmersive(this, Color.BLACK, false);

        NLUpnpCastManager nlUpnpCastManager = NLUpnpCastManager.getInstance();
        nlUpnpCastManager.addCastEventListener(iCastEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();

        ((BaseActivity) requireActivity()).unregisterBackPressHandler(backPressHandler);
        viewModel.setVisible(false);
        viewModel.statPlay();
        if (superPlayerView != null) {
            viewModel.pause(superPlayerView);
        }

        NLUpnpCastManager.getInstance().removeCastEventListener(iCastEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (superPlayerView != null) {
            viewModel.destroy(superPlayerView);
        }
    }

    private final ICastEventListener iCastEventListener = new ICastEventListener() {

        @Override
        public void onConnecting(CastDevice castDevice) {
            showLogDebug("ScreenProjection", "正在链接电视服务...");
        }

        @Override
        public void onConnected(CastDevice castDevice, TransportInfo transportInfo, MediaInfo mediaInfo, int i) {
            showLogDebug("ScreenProjection", "链接电视服务成功...");
            if (castDevice == null) {
                return;
            }
            DLNAHelper.getInstance().setCurrentDevice(castDevice);
            NLUpnpCastManager.getInstance().cast(
                    CastObject.newInstance(
                            DLNAHelper.getInstance().getNeedPlayUrl(),
                            castDevice.getId(),
                            castDevice.getName()
                    ).setDuration(60 * 2)
            );
            if (superPlayerView == null) {
                return;
            }
            superPlayerView.pause();
        }

        @Override
        public void onDisconnect() {
            showLogDebug("ScreenProjection", "电视服务链接断开...");
            DLNAHelper.getInstance().setCurrentDevice(null);
        }

        @Override
        public void onCast(CastObject castObject) {
            if (screenProjectionLayout!=null) {
                showLogDebug("ScreenProjection", "投屏成功...");
                NLUpnpCastManager.getInstance().start();
                screenProjectionLayout.setScreenName(castObject.name);
                screenProjectionLayout.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onStart() {
            showLogDebug("ScreenProjection", "播放成功...");
        }

        @Override
        public void onPause() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onSeekTo(long l) {
        }

        @Override
        public void onError(String s) {

        }

        @Override
        public void onVolume(int i) {
        }

        @Override
        public void onBrightness(int i) {
        }

        @Override
        public void onUpdatePositionInfo(PositionInfo positionInfo) {
        }
    };

    private void initScreenProjectionLayout() {
        Activity activity = getActivity();
        if (activity == null || viewModel == null) {
            return;
        }

        DLNAHelper dlnaHelper = DLNAHelper.getInstance();
        boolean isSameWithCurrentPlayUrl
                = dlnaHelper.isSameWithCurrentPlayUrl(viewModel.videoModel.getUrl());
        boolean isCurrentPlayEmpty = dlnaHelper.isCurrentPlayEmpty();

        if (screenProjectionLayout == null) {
            screenProjectionLayout = new ScreenProjectionLayout(activity);
            screenProjectionLayout.setEndClickListener(onEndClickListener);
            screenProjectionLayout.setOnBackClickListener(onBackClickListener);
            screenProjectionLayout.setVisibility(
                    isSameWithCurrentPlayUrl && !isCurrentPlayEmpty ? VISIBLE : GONE);
        } else {
            screenProjectionLayout = new ScreenProjectionLayout(activity);
            screenProjectionLayout.setEndClickListener(onEndClickListener);
            screenProjectionLayout.setOnBackClickListener(onBackClickListener);
            screenProjectionLayout.setVisibility(!isCurrentPlayEmpty ? VISIBLE : GONE);

            String playUrl = viewModel.videoModel.getUrl();
            CastDevice castDevice = DLNAHelper.getInstance().getCurrentDevice();

            if (!isCurrentPlayEmpty && castDevice != null) {
                DLNAHelper.getInstance().setNeedPlayUrl(playUrl);
                NLUpnpCastManager.getInstance().connect(castDevice);
            }
        }
        mBinding.detail.addView(screenProjectionLayout, mBinding.screenProjectionContainer.getLayoutParams());
    }

    private final View.OnClickListener onEndClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DLNAHelper.getInstance().setCurrentPlayUrl("");
            DLNAHelper.getInstance().setNeedPlayUrl("");
            DLNAHelper.getInstance().setCurrentDevice(null);
            DLNAHelper.getInstance().stop();
            screenProjectionLayout.setVisibility(GONE);
            superPlayerView.resume();

            if (viewModel == null || superPlayerView == null) {
                return;
            }
            GVideoSensorDataManager.getInstance().endScreencast(
                    viewModel.videoModel,
                    null,
                    COLLECTION_DETAIL,
                    "",
                    superPlayerView.getPlayerProcess(),
                    viewModel.playDuration,
                    StatPid.WHOLE_PERIOD_DETAIL,
                    "点击结束按钮"
            );
        }
    };

    private final View.OnClickListener onBackClickListener = v -> finishActivity();

    @Override
    public String getPid() {
        return StatPid.WHOLE_PERIOD_DETAIL;
    }

    @Override
    public VideoModel getVideoModel() {
        return viewModel.videoModel;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_watch_tv_whole_period_detail;
    }

    @Override
    protected void initView() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);

        initInputPanel();

        mBinding.circleLayout.setOnClickListener(view -> {
            Object object = view.getTag();
            if (object == null) {
                return;
            }
            PluginManager.get(CirclePlugin.class)
                    .startCircleDetailWithActivity(activity, new Circle((GroupInfo) object), null);
        });

        mBinding.buttonFollow.setOnClickListener(view -> {
            Object object = view.getTag();
            if (object == null) {
                return;
            }
            CirclePlugin circlePlugin = PluginManager.get(CirclePlugin.class);
            GroupInfo groupInfo = (GroupInfo) object;
            if (viewModel.followLiveData.getValue()) {
                circlePlugin.startCircleDetailWithActivity(activity, new Circle(groupInfo), null);
            } else {
                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                if (! accountPlugin.hasLoggedIn()) {
                    accountPlugin.startLoginActivity(activity);
                    GVideoSensorDataManager.getInstance().enterRegister(StatPid.getPageName(getPid()),
                                    ResourcesUtils.getString(R.string.follow));
                    return;
                }
                circlePlugin.joinCircle(new Circle(groupInfo), getPid(), false).subscribe(a -> {
                    groupInfo.setJoin(true);
                    mBinding.circleLayout.setTag(groupInfo);
                    mBinding.buttonFollow.setTag(groupInfo);
                    initFollowButton(true);
                });
            }
        });

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO, String.class).observe(
                this,
                o -> superPlayerView.pause()
        );

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO, String.class).observe(
                this,
                o -> superPlayerView.pause()
        );

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME, String.class).observe(
                this,
                o -> superPlayerView.resume()
        );

        GVideoEventBus.get(SEND_VIDEO_DATA_TO_PLAY, VideoModel.class).observe(
                this,
                videoModel -> {

                    long videoTotalTime = superPlayerView == null ? 0 : superPlayerView.getDuration();

                    // 切换视频数据之前，先处理播放时间计算并上报
                    viewModel.dealLastVideoPlayEnd(videoTotalTime);

                    viewModel.videoModel = videoModel;
                    viewModel.statFromModel = new StatFromModel(
                            videoModel.getId(),
                            getPid(),
                            "",
                            fromPid,
                            ""
                    );
                    viewModel.statPlay();
                    startPlay(videoModel, viewModel.statFromModel);
                    initScreenProjectionLayout();
                    GVideoSensorDataManager.getInstance().enterContainsPage(videoModel, COLLECTION_DETAIL, "");
                });

        //        GVideoEventBus.get(SEND_GROUP_INFO, GroupInfo.class)
        //                .observe(this, this::loadCircleInfo);

        GVideoEventBus.get(SELECT_SCREEN_PROJECTION, CastObject.class).observe(this, castObject -> {
                            if (superPlayerView == null || viewModel == null || castObject == null) {
                                return;
                            }
                            superPlayerView.pause();
                            screenProjectionLayout.setScreenName(castObject.name);
                            screenProjectionLayout.setVisibility(VISIBLE);
                            GVideoSensorDataManager.getInstance().screencastResult(
                                    viewModel.videoModel,
                                    COLLECTION_DETAIL,
                                    "",
                                    castObject.name,
                                    true
                            );
                        }
                );

        GVideoEventBus.get(SCREEN_PROJECTION_FAILED).observe(this, o -> {
                    if (mBinding == null || viewModel == null) {
                        return;
                    }
                    GVideoSensorDataManager.getInstance().screencastResult(
                            viewModel.videoModel,
                            COLLECTION_DETAIL,
                            "",
                            "",
                            false
                    );
                }
        );

    }

    private boolean isScreenProjectionLayoutVisible() {
        return screenProjectionLayout != null && screenProjectionLayout.getVisibility() == VISIBLE;
    }

    private void loadCircleInfo(ColumnGroupInfo groupInfo) {
        mBinding.circleLayout.setTag(groupInfo);
        mBinding.buttonFollow.setTag(groupInfo);

        if (groupInfo == null) {
            mBinding.circleLayout.setVisibility(GONE);
        } else {
            if (groupInfo.getMentors() != null && groupInfo.getMentors().size() > 0) {
                Circle circle = new Circle();
                circle.groupId = groupInfo.groupId;
                View view = PluginManager.get(CirclePlugin.class).getQAAnswerMessageView(getContext(), circle,
                                groupInfo.getGather(), groupInfo.getMentors().get(0), getPid());
                mBinding.qaLayout.removeAllViews();
                mBinding.qaLayout.addView(view);
                mBinding.qaLayout.setVisibility(VISIBLE);
            } else {
                mBinding.qaLayout.setVisibility(GONE);
            }
            mBinding.circleLayout.setVisibility(VISIBLE);
            mBinding.circleName.setText(groupInfo.getGroupName());
            mBinding.circleIntroduction.setText(groupInfo.getGroupIntroduction());
            Glide.with(this).load(groupInfo.coverUrl).apply(
                            new RequestOptions().transform(new RoundedCorners(SizeUtils.dp2px(8)))).into(
                            mBinding.circleHeader);
            initFollowButton(groupInfo.isJoin());
        }
    }

    private void initFollowButton(boolean join) {
        Context context = mBinding.getRoot().getContext();
        int followColor = ContextCompat.getColor(context, R.color.color_e4344e);
        int normalColor = ContextCompat.getColor(context, R.color.color_7f7f7f);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_add_red);
        int dp10 = mBinding.getRoot().getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_10DP);
        if (drawable != null) {
            drawable.setBounds(0, 0, dp10, dp10);
        }
        if (join) {
            mBinding.buttonFollow.getHelper().setBorderColorNormal(normalColor);
            mBinding.buttonText.setTextColor(normalColor);
            mBinding.buttonText.setText(R.string.enter_community);
            mBinding.buttonText.setCompoundDrawables(null, null, null, null);
            mBinding.buttonText.setTextColor(normalColor);
        } else {
            mBinding.buttonFollow.getHelper().setBorderColorNormal(followColor);
            mBinding.buttonText.setTextColor(followColor);
            mBinding.buttonText.setText(R.string.join_community);
            mBinding.buttonText.setCompoundDrawables(drawable, null, null, null);
            mBinding.buttonText.setTextColor(followColor);
        }

    }

    private void startPlay(VideoModel videoModel, StatFromModel statFromModel) {
        if (superPlayerView != null) {
            superPlayerView.release();
        }
        VideoSuperInfoViewModel videoSuperInfoViewModel = new VideoSuperInfoViewModel(
                GVideoRuntime.getApplication(),
                videoModel
        );
        videoSuperInfoViewModel.setStatFromModel(statFromModel);
        mBinding.setViewModel(viewModel);
        mBinding.setVideoObservable(videoModel.getObservable());
        int playerType = videoModel.getPlayerType();
        superPlayerView = new GVideoView(getContext(), playerType);
        mBinding.detail.addView(superPlayerView, mBinding.playerContainer.getLayoutParams());
        superPlayerView.setAutoFollowSystemRotation(true);
//        mBinding.detail.postDelayed(() -> superPlayerView.registerGravitySensor(),2000);
        superPlayerView.setMediaController(new VideoWholePeriodController(getActivity()));
        viewModel.setVideoListener(superPlayerView);
        viewModel.loadMedia(superPlayerView, videoModel);
    }


    @Override
    protected void bindViewModels() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        viewModel = bingViewModel(WatchTvWholePeriodDetailViewModel.class);

        boolean mStartComment = arguments.getBoolean(Constants.EXTRA_COMMENT);

        viewModel.fullscreenLiveData.observe(this, fullscreen -> {
            if (fullscreen) {
                mBinding.refreshLayout.setVisibility(GONE);
                mBinding.inputPanel.getRoot().setVisibility(GONE);
            } else {
                mBinding.refreshLayout.setVisibility(VISIBLE);
                mBinding.inputPanel.getRoot().setVisibility(VISIBLE);
            }
        });

        viewModel.onPlayBeginLiveData.observe(
                this, o -> {
                    if (superPlayerView != null && isScreenProjectionLayoutVisible()) {
                        superPlayerView.pause();
                    }
                }
        );

        viewModel.backPressLiveData.observe(
                this,
                o -> {
                    if (!Navigation.findNavController(superPlayerView).navigateUp()) {
                        requireActivity().finish();
                    }
                });

        viewModel.lockPortraitLiveData.observe(this, o -> {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            ScreenUtils.setPortrait(getActivity());
        });

        viewModel.columnDetailLiveData.observe(this, detail -> {
            loadCircleInfo(detail.getGroup());
        });

        viewModel.followLiveData.observe(this, this::initFollowButton);

        initViewPager();
        initSliding(mBinding.sliding, mStartComment);
    }

    @Override
    protected void loadData() {
        viewModel.getColumnDetail(columnId);
    }

    private void initViewPager() {
        Bundle bundle = new Bundle();
        bundle.putString(COLUMN_ID, columnId);
        bundle.putString(PROGRAM_ID, programId);
        bundle.putBoolean(NEED_SEND_VIDEO_DATA_ONCE, true);

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(requireContext())
                .add(R.string.select_collection, SelectCollectionFragment.class, bundle)
                .add(R.string.comment, CommentFragment.class, bundle);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(),
                creator.create()
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
                mBinding.inputPanel.getRoot().setVisibility(position == 1 ? VISIBLE : GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mBinding.viewPager.post(() -> commentFragment = (CommentFragment) adapter.getPage(1));
    }

    private void initSliding(GVideoSmartTabLayout sliding, boolean startComment) {
        sliding.useDefaultStyle(false);
        sliding.setEnableChangeTextSize(false);
        sliding.setDefaultTabTextColor(ResourcesUtils.getColorStateList(R.drawable.video_tab_layout_text_color));
        sliding.setViewPager(mBinding.viewPager);
        changeTabTextStyle(VIDEO_SUPER_FRAGMENT_SIZE, startComment ? 0 : 1);
    }

    private void changeTabTextStyle(int size, int position) {
        for (int i = 0; i < size; i++) {
            View tab = mBinding.sliding.getTabAt(i);
            if (tab instanceof TextView) {
                ((TextView) tab).setTypeface(Typeface.defaultFromStyle(
                        i == position ? Typeface.BOLD : Typeface.NORMAL));
            }
        }
    }

    private void initInputPanel() {
        mBinding.inputPanel.audioComment.setOnClickListener(
                v -> {
                    if (checkCanNotComment()) {
                        showToast(com.hzlz.aviation.feature.video.R.string.video_comment_cant_comment);
                        return;
                    }
                    if (commentFragment == null) {
                        return;
                    }
                    commentFragment.onClickShowAudioDialog();
                });

        mBinding.inputPanel.commentTxt.setOnClickListener(
                v -> {
                    if (checkCanNotComment()) {
                        showToast(com.hzlz.aviation.feature.video.R.string.video_comment_cant_comment);
                        return;
                    }
                    if (commentFragment == null) {
                        return;
                    }
                    commentFragment.onClickShowTxtInputDialog();
                });

        mBinding.inputPanel.imageComment.setOnClickListener(
                v -> {
                    if (checkCanNotComment()) {
                        showToast(com.hzlz.aviation.feature.video.R.string.video_comment_cant_comment);
                        return;
                    }
                    if (commentFragment == null) {
                        return;
                    }
                    commentFragment.onClickShowImageDialog();
                });
    }

    private boolean checkCanNotComment() {
        if (viewModel.videoModel == null) {
            return true;
        }
        return !viewModel.videoModel.isCanComment();
    }

    @Override
    protected Long getTenantId() {
        if (viewModel == null
                || viewModel.videoModel == null) {
            return null;
        }
        GroupInfo groupInfo = viewModel.videoModel.getGroupInfo();
        if (groupInfo == null) {
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
        GroupInfo groupInfo = viewModel.videoModel.getGroupInfo();
        if (groupInfo == null) {
            return "";
        }
        return groupInfo.tenantName;
    }

    @Override
    protected String getCommunityName() {
        if (viewModel != null) {
            return viewModel.getCircleName();
        }
        return super.getCommunityName();
    }

}
