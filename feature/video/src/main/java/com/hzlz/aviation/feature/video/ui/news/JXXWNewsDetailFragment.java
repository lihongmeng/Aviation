package com.hzlz.aviation.feature.video.ui.news;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.video.Constants;
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
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.FragmentJxxwNewsDetailBinding;

import java.util.Date;

public class JXXWNewsDetailFragment extends BaseFragment<FragmentJxxwNewsDetailBinding> {

    // VideoSuperViewModel
    private JXXWNewsDetailViewModel viewModel;

    // 播放器
    private GVideoView superPlayerView;

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
        viewModel.setVisible(true);
        if (superPlayerView != null) {
            viewModel.resume(superPlayerView);
        }
        ImmersiveUtils.enterImmersive(this, Color.BLACK, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
        viewModel.setVisible(false);
        viewModel.statPlay();
        if (superPlayerView != null) {
            viewModel.pause(superPlayerView);
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
        return R.layout.fragment_jxxw_news_detail;
    }

    @Override
    protected void initView() {

        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO, String.class)
                .observe(this, o -> superPlayerView.setMute(true));

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME, String.class)
                .observe(this, o -> superPlayerView.setMute(false));

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN, String.class).observe(
                this,
                o -> loadData()
        );

        mBinding.favorite.setOnClickListener(v -> {
            if (viewModel == null) {
                return;
            }
            viewModel.dealFavoriteClick(v);
        });

        mBinding.wx.setOnClickListener(v -> PluginManager.get(SharePlugin.class).startWXShare(
                getActivity(),
                viewModel.getShareModel(),
                viewModel.getStatFromModel()
        ));

        mBinding.wxCircle.setOnClickListener(v -> PluginManager.get(SharePlugin.class).startWXCircleShare(
                getActivity(),
                viewModel.getShareModel(),
                viewModel.getStatFromModel()
        ));

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
        // 如果有，先加载现有
        if (!TextUtils.isEmpty(videoModel.getUrl())) {
            loadVideoModel(videoModel);
        }

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
        if (TextUtils.isEmpty(mediaId) || mBinding == null) {
            return;
        }

        loadVideoBasicInfo(videoModel);

        // 先根据VideoModel中的数据填充界面数据
        viewModel.statFromModel = new StatFromModel(
                mediaId,
                getPid(),
                "",
                viewModel.fromPid,
                viewModel.fromChannelId
        );

        // 初始化播放器并开始播放视频数据
        int playerType = videoModel.getPlayerType();
        superPlayerView = new GVideoView(getContext(), playerType);
        mBinding.detail.addView(superPlayerView, mBinding.playerContainer.getLayoutParams());
        superPlayerView.setAutoFollowSystemRotation(true);
//        mBinding.detail.postDelayed(() -> superPlayerView.registerGravitySensor(), 2000);
        viewModel.setVideoListener(superPlayerView);
        viewModel.start(superPlayerView);

    }

    private void loadVideoBasicInfo(@NonNull VideoModel videoModel) {
        String mediaId = videoModel.getId();
        if (TextUtils.isEmpty(mediaId) || mBinding == null) {
            return;
        }
        String title = viewModel.videoModel.getTitle();
        mBinding.title.setText(TextUtils.isEmpty(title) ? "" : title);

        Date date = viewModel.videoModel.getCreateDate();
        mBinding.time.setText(date == null ? "" : DateUtils.friendlyTime(viewModel.videoModel.getCreateDate()));

        AuthorModel authorModel = viewModel.videoModel.getAuthor();
        if (authorModel == null) {
            mBinding.source.setText("");
        } else {
            String name = authorModel.getName();
            mBinding.source.setText(TextUtils.isEmpty(name) ? "" : name);
        }

        int pv = viewModel.videoModel.getPv();
        mBinding.readCount.setText(pv <= 0 ? "" : "阅读 " + pv);

        String description = viewModel.videoModel.getDescription();
        mBinding.description.setText(TextUtils.isEmpty(description) ? "" : description);

        boolean isFavor = videoModel.getIsFavor() > 0;
        mBinding.favorite.setIconImageResource(
                isFavor ? R.drawable.ic_news_like_red : R.drawable.ic_news_like);


    }

    @Override
    protected void bindViewModels() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        viewModel = bingViewModel(JXXWNewsDetailViewModel.class);
        mBinding.setViewModel(viewModel);

        viewModel.videoModel = arguments.getParcelable(Constants.EXTRA_VIDEO_MODEL);
        if (viewModel.videoModel == null) {
            return;
        }

        viewModel.fromPid = arguments.getString(Constant.EXTRA_FROM_PID);
        viewModel.fromChannelId = arguments.getString(Constant.EXTRA_FROM_CHANNEL_ID);

        // 当viewModel通过网络加载最新的视频数据，并且判定该数据需要渲染到界面
        // 就会触发当下回调的执行
        viewModel.videoModelLiveData.observe(this, o -> {
            if (viewModel == null) {
                return;
            }
            loadVideoModel(viewModel.videoModel);
        });

        viewModel.backPressLiveData.observe(this, o -> {
            try {
                if (!Navigation.findNavController(superPlayerView).navigateUp()) {
                    requireActivity().finish();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        viewModel.updateLiveData.observe(this, title -> {
                    if (mBinding == null
                            || viewModel == null
                            || viewModel.videoModel == null) {
                        return;
                    }
                    loadVideoBasicInfo(viewModel.videoModel);
                }
        );

    }

    @Override
    protected void loadData() {
        if (viewModel == null) {
            return;
        }
        dealVideoModelAndLoad(viewModel.videoModel);
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

}
