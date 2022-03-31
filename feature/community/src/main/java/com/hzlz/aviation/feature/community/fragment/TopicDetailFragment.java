package com.hzlz.aviation.feature.community.fragment;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.TOPIC_DETAIL;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_IS_START_WITH_ACTIVITY;
import static com.hzlz.aviation.kernel.base.utils.DefaultPageNumberUtil.MIN;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.CirclePluginImpl;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.FragmentTopicDetailBinding;
import com.hzlz.aviation.feature.community.viewmodel.TopicDetailViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.DefaultPageNumberUtil;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

import io.reactivex.rxjava3.core.Observable;

public class TopicDetailFragment extends MediaPageFragment<FragmentTopicDetailBinding> {

    private TopicDetailViewModel viewModel;
    private boolean isStartWithActivity;

    @Override
    public String getPid() {
        return StatPid.PID_TOPIC_DETAIL;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected boolean enableImmersive() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            isStartWithActivity = arguments.getBoolean(EXTRA_IS_START_WITH_ACTIVITY, false);
        }
        return isStartWithActivity;
    }

    @Override
    public String getChannelId() {
        return "";
    }

    @Override
    public void onLeftBackPressed(@NonNull View view) {
        if (isStartWithActivity) {
            finishActivity();
        } else {
            super.onLeftBackPressed(view);
        }
    }

    @Override
    public void onRightOperationPressed(@NonNull View view) {
        if (PluginManager.get(SharePlugin.class).isCanShare()) {
            viewModel.onShareClicked(view);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        mTabItemId = TopicDetailFragment.class.getName();
        setToolbarTitle(getString(R.string.topic_detail_with_well));
        if (PluginManager.get(SharePlugin.class).isCanShare()) {
            setRightOperationImage(R.drawable.ic_common_share_top_black);
        }
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(TopicDetailViewModel.class);
        if (viewModel == null) {
            finishActivity();
            return;
        }
        viewModel.updateTabId(getTabId());
        mBinding.setViewModel(viewModel);

        Bundle arguments = getArguments();
        if (arguments != null) {
            viewModel.topicDetail.setValue(arguments.getParcelable(TOPIC_DETAIL));
            isStartWithActivity = arguments.getBoolean(EXTRA_IS_START_WITH_ACTIVITY, false);
        }

        viewModel.key = TOPIC_DETAIL + "_" +getTopicDetailId();

        viewModel.autoRefreshLiveData.observe(this, autoRefresh -> viewModel.onRefresh(mBinding.refreshLayout));

        viewModel.topicDetail.observe(this, topicDetail -> showTopicInfo());
    }

    private long getTopicDetailId() {
        if (viewModel == null || viewModel.topicDetail == null) {
            return -1;
        }
        TopicDetail topicDetailValue = viewModel.topicDetail.getValue();
        return topicDetailValue == null ? -1 : topicDetailValue.id;
    }

    @Override
    protected void checkLoginStatus() {
        if (viewModel == null) {
            return;
        }
        viewModel.loadTopicInfo();
    }

    @Override
    public void onReload(@NonNull View view) {
        viewModel.checkNetworkAndLoginStatus();
    }

    @Override
    public Observable<ShortVideoListModel> loadMoreShortData(boolean refresh) {
        return viewModel.loadMoreShortData();
    }

    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return viewModel.createShortListModel(mediaModel);
    }

    @Override
    protected void loadData() {

        // 监听登录和登出
        listenEvent();

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE).observe(
                this, o -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.loadTopicInfo();
                }
        );

        // 发布成功后会收到通知，刷新当前页面
        GVideoEventBus.get(REFRESH_PAGE_BECAUSE_PUBLISH, String.class).observe(
                this,
                startPublishFrom -> {
                    if (mBinding == null
                            || viewModel == null
                            || !TextUtils.equals(startPublishFrom, Constant.START_PUBLISH_FROM.TOPIC_DETAIL)) {
                        return;
                    }
                    new DefaultPageNumberUtil().saveDefaultPageNumber(TOPIC_DETAIL + "_" + getTopicDetailId(), MIN);
                    viewModel.onRefresh(mBinding.refreshLayout);
                }
        );

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus();

    }

    private void showTopicInfo() {
        TopicDetail topicDetail = viewModel.topicDetail.getValue();
        if (topicDetail == null) {
            return;
        }

        if (TextUtils.isEmpty(topicDetail.content)) {
            mBinding.header.topicName.setText("");
        } else {
            mBinding.header.topicName.setText("# " + topicDetail.content + " #");
        }

        if (viewModel.circle == null || TextUtils.isEmpty(viewModel.circle.getName())) {
            mBinding.header.circleLayout.setVisibility(View.GONE);
        } else {
            mBinding.header.circleLayout.setVisibility(View.VISIBLE);
            ((AviationTextView) (mBinding.header.circleLayout.findViewById(R.id.circle_name))).setText(viewModel.circle.getName());
            mBinding.header.circleLayout.setOnClickListener(v -> {
                if (viewModel.circle == null) {
                    return;
                }
                new CirclePluginImpl().navigationToCircleDetail(v, viewModel.circle);
            });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic_detail;
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
    }

    @Override
    protected Long getTenantId() {
        if (viewModel == null || viewModel.circle == null) {
            return null;
        }
        return viewModel.circle.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (viewModel == null || viewModel.circle == null) {
            return "";
        }
        return viewModel.circle.tenantName;
    }

}