package com.hzlz.aviation.feature.watchtv.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CHANNEL_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.DATE_DAY;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.DATE_MONTH;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.DATE_YEAR;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.INDEX;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.PLAY_TYPE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.VIDEO_MODEL;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SCREEN_PROJECTION_FAILED;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SELECT_SCREEN_PROJECTION;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TO_WATCH_TV_PLAY_TYPE.BUILD_SELF;
import static com.hzlz.aviation.kernel.base.model.anotation.MediaType.WATCH_TV;
import static com.hzlz.aviation.kernel.base.model.anotation.PlayerType.GVIDEO;
import static com.hzlz.aviation.library.util.DateUtils.YYYY_MM_DD_HH_MM_SS;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.feature.watchtv.adapter.WatchTvChannelDetailChannelAdapter;
import com.hzlz.aviation.feature.watchtv.adapter.WatchTvChannelDetailTimeAdapter;
import com.hzlz.aviation.feature.watchtv.entity.ChannelTvManifest;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.adapter.BaseFragmentVpAdapter;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.model.video.WatchTvChannel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.screenprojection.DLNAHelper;
import com.hzlz.aviation.kernel.base.view.ScreenProjectionLayout;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.controller.WatchTvChannelDetailController;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.feature.watchtv.R;
import com.hzlz.aviation.feature.watchtv.databinding.FragmentWatchTvChannelDetailBinding;
import com.liuwei.android.upnpcast.NLUpnpCastManager;
import com.liuwei.android.upnpcast.controller.CastObject;
import com.liuwei.android.upnpcast.controller.ICastEventListener;
import com.liuwei.android.upnpcast.device.CastDevice;

import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ????????????????????????
 */
@SuppressWarnings("rawtypes")
public class WatchTvChannelDetailFragment extends BaseFragment<FragmentWatchTvChannelDetailBinding> {

    // WatchTvChannelDetailViewModel
    private WatchTvChannelDetailViewModel viewModel;

    // ??????????????????Fragment??????
    private final List<BaseFragment> fragmentArrayList = new ArrayList<>();

    // ?????????View
    private GVideoView superPlayerView;

    // ?????????????????????LayoutManager
    private LinearLayoutManager tvChannelLinearManager;

    // ???????????????????????????????????????????????????????????????????????????
    private Handler handler;

    // ??????????????????????????????????????????????????????????????????
    private ScreenProjectionLayout screenProjectionLayout;

    // ????????????????????????
    private final BackPressHandler mBackPressHandler = () -> {
        if (superPlayerView != null) {
            return superPlayerView.handleBackPressed();
        }
        return false;
    };

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
        viewModel.isVisible = true;
        ImmersiveUtils.enterImmersive(this, Color.BLACK, false);

        // ????????????????????????????????????????????????????????????
        if (superPlayerView != null && !isScreenProjectionLayoutVisible()) {
            viewModel.resume(superPlayerView);
        }

        // ?????????????????????????????????????????????
        NLUpnpCastManager.getInstance().addCastEventListener(iCastEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
        viewModel.isVisible = false;

        // ?????????????????????????????????
        if (superPlayerView != null) {
            viewModel.pause(superPlayerView);
        }

        // ???????????????????????????
        viewModel.statPlay();

        // ???????????????
        NLUpnpCastManager.getInstance().removeCastEventListener(iCastEventListener);
    }

    private final ICastEventListener iCastEventListener = new ICastEventListener() {

        @Override
        public void onConnecting(@NonNull CastDevice castDevice) {
            showLogDebug("ScreenProjection", "????????????????????????...");
        }

        @Override
        public void onConnected(CastDevice castDevice, TransportInfo transportInfo, MediaInfo mediaInfo, int i) {
            showLogDebug("ScreenProjection", "????????????????????????...");
            if (castDevice == null) {
                return;
            }
            DLNAHelper.getInstance().setCurrentDevice(castDevice);

            // ????????????
            NLUpnpCastManager.getInstance().cast(
                    CastObject.newInstance(
                            viewModel.watchTvPlayUrl,
                            castDevice.getId(),
                            castDevice.getName()
                    ).setDuration(60 * 2)
            );

            // ???????????????????????????
            if (superPlayerView == null) {
                return;
            }
            superPlayerView.pause();
        }

        @Override
        public void onDisconnect() {
            showLogDebug("ScreenProjection", "????????????????????????...");
            DLNAHelper.getInstance().setNeedPlayUrl("");
            DLNAHelper.getInstance().setCurrentDevice(null);
        }

        @Override
        public void onCast(CastObject castObject) {
            showLogDebug("ScreenProjection", "????????????...");

            // ?????????????????????????????????????????????
            // ????????????????????????????????????????????????????????????
            NLUpnpCastManager.getInstance().start();

            screenProjectionLayout.setScreenName(castObject.name);
            screenProjectionLayout.setVisibility(VISIBLE);
        }

        @Override
        public void onStart() {
            showLogDebug("ScreenProjection", "????????????...");
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

    @Override
    public void onDestroyView() {
        if (superPlayerView != null) {
            viewModel.destroy(superPlayerView);
        }
        super.onDestroyView();
    }

    /**
     * ??????????????????????????????????????????????????????
     * ??????????????????????????????????????????{@link WatchTvChannelDetailViewModel#startPlayTv}?????????
     * ??????????????????
     */
    private void initScreenProjectionLayout() {
        Activity activity = getActivity();
        if (activity == null || viewModel == null) {
            return;
        }


        DLNAHelper dlnaHelper = DLNAHelper.getInstance();
        boolean isSameWithCurrentPlayUrl
                = dlnaHelper.isSameWithCurrentPlayUrl(viewModel.watchTvPlayUrl);
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

            CastDevice castDevice = DLNAHelper.getInstance().getCurrentDevice();

            if (!isCurrentPlayEmpty && castDevice != null) {
                DLNAHelper.getInstance().setNeedPlayUrl(viewModel.watchTvPlayUrl);
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
                    WATCH_TV,
                    "",
                    superPlayerView.getPlayerProcess(),
                    viewModel.mPlayDuration,
                    StatPid.WATCH_TV_CHANNEL_DETAIL,
                    "??????????????????"
            );
        }
    };

    private final View.OnClickListener onBackClickListener = v -> finishActivity();

    @Override
    public String getPid() {
        return StatPid.WATCH_TV_CHANNEL_DETAIL;
    }

    @Override
    public VideoModel getVideoModel() {
        return null;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_watch_tv_channel_detail;
    }

    @Override
    protected void initView() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);

        // ??????????????????
        superPlayerView = new GVideoView(getContext(), GVIDEO);
        superPlayerView.setMediaController(new WatchTvChannelDetailController(activity));
        mBinding.detail.addView(superPlayerView, mBinding.playerContainer.getLayoutParams());
        superPlayerView.setAutoFollowSystemRotation(true);
//        mBinding.detail.postDelayed(() -> superPlayerView.registerGravitySensor(),2000);

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO, String.class).observe(
                this,
                o -> {
                    if (superPlayerView == null) {
                        return;
                    }
                    superPlayerView.setMute(true);
                }
        );

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME, String.class).observe(
                this,
                o -> {
                    if (superPlayerView == null) {
                        return;
                    }
                    superPlayerView.setMute(false);
                }
        );

        GVideoEventBus.get(SELECT_SCREEN_PROJECTION, CastObject.class)
                .observe(this, castObject -> {
                            if (mBinding == null
                                    || viewModel == null
                                    || castObject == null) {
                                return;
                            }
                            screenProjectionLayout.setScreenName(castObject.name);
                            screenProjectionLayout.setVisibility(VISIBLE);

                            GVideoSensorDataManager.getInstance().screencastResult(
                                    viewModel.videoModel,
                                    WATCH_TV,
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
                            WATCH_TV,
                            "",
                            "",
                            false
                    );
                }
        );

    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (fragmentArrayList.isEmpty()) {
                handler.postDelayed(runnable, 1000);
                return;
            }
            WatchTvChannelDetailListFragment watchTvChannelDetailListFragment
                    = (WatchTvChannelDetailListFragment) fragmentArrayList.get(0);
            ChannelTvManifest channelTvManifest = watchTvChannelDetailListFragment.getNextChannelTvManifest();
            if (channelTvManifest == null) {
                handler.postDelayed(runnable, 1000);
                return;
            }
            if (System.currentTimeMillis() < DateUtils.getDateLong(channelTvManifest.playTime, YYYY_MM_DD_HH_MM_SS)) {
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    @Override
    protected void bindViewModels() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        viewModel = bingViewModel(WatchTvChannelDetailViewModel.class);
        mBinding.setViewModel(viewModel);

        // ========================== ???ViewModel??????????????????????????? ==========================
        viewModel.startPlayTv.observe(
                this,
                o -> {
                    // ??????????????????
                    superPlayerView.release();
                    superPlayerView = new GVideoView(getContext(), GVIDEO);
                    viewModel.setVideoListener(superPlayerView);
                    superPlayerView.setMediaController(new WatchTvChannelDetailController(activity));
                    mBinding.detail.addView(superPlayerView, mBinding.playerContainer.getLayoutParams());
                    superPlayerView.setAutoFollowSystemRotation(true);
                    superPlayerView.setTitleModeInWindowMode(LiteavConstants.TITLE_MODE_ONLY_BACK);
                    superPlayerView.setCanResize(false);
                    superPlayerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION);
                    superPlayerView.startPlay(viewModel.watchTvPlayUrl);

                    if (viewModel.playType == BUILD_SELF) {
                        if (handler == null) {
                            handler = new Handler();
                        } else {
                            handler.removeCallbacksAndMessages(null);
                        }
                        handler.postDelayed(runnable, 1000);
                    }

                    initScreenProjectionLayout();

                    GVideoSensorDataManager.getInstance().enterContainsPage(
                            viewModel.videoModel,
                            WATCH_TV,
                            ""
                    );
                }
        );

        viewModel.onPlayBeginLiveData.observe(
                this, o -> {
                    if (superPlayerView != null && isScreenProjectionLayoutVisible()) {
                        superPlayerView.pause();
                    }
                }
        );

        viewModel.updateChannelList.observe(
                this,
                o -> {
                    if (viewModel.watchTvChannelList == null || viewModel.watchTvChannelList.isEmpty()) {
                        mBinding.channelList.setVisibility(GONE);
                        return;
                    }

                    mBinding.channelList.setVisibility(VISIBLE);

                    // ??????????????????????????????????????????????????????ChannelId?????????????????????
                    // ??????????????????????????????
                    if (viewModel.watchTvChannelDetailChannelAdapter == null) {
                        viewModel.watchTvChannelDetailChannelAdapter = new WatchTvChannelDetailChannelAdapter(
                                activity,
                                viewModel.channelId,
                                viewModel.watchTvChannelList
                        );
                        tvChannelLinearManager = new LinearLayoutManager(activity);
                        tvChannelLinearManager.setOrientation(RecyclerView.HORIZONTAL);
                        mBinding.channelList.setLayoutManager(tvChannelLinearManager);
                        mBinding.channelList.setAdapter(viewModel.watchTvChannelDetailChannelAdapter);
                        viewModel.watchTvChannelDetailChannelAdapter.setItemOnClickListener(
                                watchTvChannel -> {
                                    viewModel.channelId = watchTvChannel.id;
                                    refreshTimeAndViewPager();
                                }
                        );
                        smoothRecyclerViewToVisible(findListIndexByChannelId());
                    } else {
                        viewModel.watchTvChannelDetailChannelAdapter.updateDataSource(viewModel.watchTvChannelList);
                    }
                }
        );

        viewModel.fullscreenLiveData.observe(this, fullscreen -> {
            if (fullscreen) {
                mBinding.refreshLayout.setVisibility(View.GONE);
            } else {
                mBinding.refreshLayout.setVisibility(View.VISIBLE);
            }
        });

        viewModel.backPressLiveData.observe(
                this,
                o -> {
                    if (!Navigation.findNavController(superPlayerView).navigateUp()) {
                        requireActivity().finish();
                    }
                });

        viewModel.lockPortraitLiveData.observe(
                this,
                o -> ScreenUtils.setPortrait(activity)
        );

        // ===============================================================================

        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        viewModel.videoModel = arguments.getParcelable(VIDEO_MODEL);
        if (viewModel.videoModel != null) {
            viewModel.channelId = Long.parseLong(viewModel.videoModel.getId());
        }
        viewModel.playType = arguments.getInt(PLAY_TYPE, -1);
        initListAndViewPager();
        viewModel.initChannelData(mBinding.refreshLayout);
    }

    private boolean isScreenProjectionLayoutVisible() {
        return screenProjectionLayout != null && screenProjectionLayout.getVisibility() == VISIBLE;
    }

    @Override
    protected void loadData() {
    }

    // ????????????????????????Tab???Tab???????????????Fragment
    private void initListAndViewPager() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // ???????????????????????????????????????????????????Tab????????????
        viewModel.watchTvChannelDetailTimeAdapter = new WatchTvChannelDetailTimeAdapter(activity);
        // ??????Tab??????????????????Fragment??????
        viewModel.watchTvChannelDetailTimeAdapter.setOnClickListener(clickIndex -> mBinding.tvList.setCurrentItem(clickIndex));
        LinearLayoutManager timeListLinearManager = new LinearLayoutManager(activity);
        timeListLinearManager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.timeList.setLayoutManager(timeListLinearManager);
        mBinding.timeList.setAdapter(viewModel.watchTvChannelDetailTimeAdapter);

        // ????????????????????????????????????Fragment????????????ChannelId??????????????????
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        for (int index = 0; index < 7; index++) {
            long showTime = currentTime + index * 24 * 60 * 60 * 1000;
            calendar.setTime(new Date(showTime));

            // ??????????????????????????????Fragment
            WatchTvChannelDetailListFragment watchTvChannelDetailListFragment = new WatchTvChannelDetailListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(INDEX, index);
            bundle.putLong(CHANNEL_ID, viewModel.channelId);
            bundle.putInt(DATE_YEAR, calendar.get(Calendar.YEAR));
            bundle.putInt(DATE_MONTH, calendar.get(Calendar.MONTH) + 1);
            bundle.putInt(DATE_DAY, calendar.get(Calendar.DAY_OF_MONTH));
            watchTvChannelDetailListFragment.setArguments(bundle);
            fragmentArrayList.add(watchTvChannelDetailListFragment);
        }
        viewModel.tvListViewPagerAdapter = new BaseFragmentVpAdapter(getChildFragmentManager());
        mBinding.tvList.setAdapter(viewModel.tvListViewPagerAdapter);
        viewModel.tvListViewPagerAdapter.updateSource(fragmentArrayList);
        mBinding.tvList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // ??????Tab??????????????????Fragment??????
                viewModel.watchTvChannelDetailTimeAdapter.updatePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void smoothRecyclerViewToVisible(int position) {
        int firstVisibleIndex = tvChannelLinearManager.findFirstVisibleItemPosition();
        int lastVisibleIndex = tvChannelLinearManager.findLastVisibleItemPosition();
        if (position < firstVisibleIndex || position > lastVisibleIndex) {
            if (position > 1) {
                mBinding.channelList.scrollToPosition(position - 2);
            } else {
                mBinding.channelList.scrollToPosition(0);
            }
        }
    }

    private int findListIndexByChannelId() {
        if (viewModel.watchTvChannelList == null || viewModel.watchTvChannelList.isEmpty()) {
            return 0;
        }
        for (int index = 0; index < viewModel.watchTvChannelList.size(); index++) {
            WatchTvChannel watchTvChannel = viewModel.watchTvChannelList.get(index);
            if (watchTvChannel == null || watchTvChannel.id != viewModel.channelId) {
                continue;
            }
            return index;
        }
        return 0;
    }

    public void refreshTimeAndViewPager() {
        viewModel.watchTvChannelDetailTimeAdapter.updatePosition(0);
        mBinding.tvList.setCurrentItem(0);
        for (BaseFragment baseFragment : fragmentArrayList) {
            Bundle arguments = baseFragment.getArguments();
            arguments.putLong(CHANNEL_ID, viewModel.channelId);
            (baseFragment).setArguments(arguments);
            ((WatchTvChannelDetailListFragment) baseFragment).loadData(viewModel.channelId);
        }
        viewModel.initChannelData(mBinding.refreshLayout);
    }


}
