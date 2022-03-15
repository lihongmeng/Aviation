package com.hzlz.aviation.feature.community.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.feature.community.CirclePluginImpl;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.adapter.MyCircleHorizontalAdapter;
import com.hzlz.aviation.feature.community.databinding.FragmentMyCircleBinding;
import com.hzlz.aviation.feature.community.databinding.LayoutMyCircleTopBinding;
import com.hzlz.aviation.feature.community.viewmodel.HomeMyCommunityViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.RecyclerViewVideoOnScrollListener;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.media.recycler.MediaRecyclerAdapter;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

import java.util.ArrayList;

/**
 * 首页我的社区页面
 */
public final class HomeMyCommunityFragment extends MediaPageFragment<FragmentMyCircleBinding> {

    // ViewModel
    private HomeMyCommunityViewModel viewModel;

    // 横向社区列表适配器
    private MyCircleHorizontalAdapter myCircleHorizontalAdapter;

    // 顶部的横向社区列表
    private LayoutMyCircleTopBinding topBinding;

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_circle;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getChannelId() {
        return "";
    }

    @Override
    public String getPid() {
        return StatPid.CIRCLE_MINE;
    }

    @Override
    protected void initView() {
        init(getGvFragmentId(), false);
        super.initView();

        topBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_my_circle_top, null, false);
        ((MediaRecyclerAdapter) mAdapter).addHeaderView(topBinding.getRoot());

        mTabItemId = HomeMyCommunityFragment.class.getName();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        myCircleHorizontalAdapter = new MyCircleHorizontalAdapter(getContext());
        topBinding.groupList.setLayoutManager(layoutManager);
        topBinding.groupList.setAdapter(myCircleHorizontalAdapter);
    }

    @Override
    protected void bindViewModels() {
        viewModel = super.bingViewModel(HomeMyCommunityViewModel.class);
        viewModel.updateTabId(getTabId());
        mBinding.setViewModel(viewModel);

        viewModel.circleList.observe(
                this,
                circleList -> {
                    if (circleList == null) {
                        circleList = new ArrayList<>();
                    }
                    myCircleHorizontalAdapter.updateSource(circleList);
                    if (circleList.isEmpty()) {
                        topBinding.groupList.setVisibility(GONE);
                    } else {
                        topBinding.groupList.setVisibility(VISIBLE);
                    }

                }
        );

        mBinding.recyclerView.addOnScrollListener(new RecyclerViewVideoOnScrollListener(mBinding.recyclerView,
                new RecyclerViewVideoOnScrollListener.onScrolledPositionListener() {
                    @Override
                    public void onScrolled(int fistVisible, int lastVisible) {
                        GVideoEventBus.get(Constant.EVENT_MSG.COLLAPSE_AUDIO_FLOAT_VIEW).post(null);
                    }

                    @Override
                    public void onScrollStateChanged(int fistVisible, int lastVisible) {
                        mFistVisible = fistVisible;
                        mLastVisible = lastVisible;
                    }

                    @Override
                    public void onItemEnter(int position) {
                        postExposure(position, position, true);
                    }

                    @Override
                    public void onItemExit(int position) {
                        postExposure(position, position, false);
                    }
                }));

    }

    @Override
    protected void loadData() {
        // 监听登录和登出
        listenEvent();

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus(mBinding.refreshLayout);

    }

    @Override
    public void onReload(@NonNull View view) {
        viewModel.checkNetworkAndLoginStatus(mBinding.refreshLayout);
    }

    @Override
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return viewModel.createShortListModel(mediaModel);
    }

    @Override
    protected void checkLoginStatus() {
        viewModel.checkNetworkAndLoginStatus(mBinding.refreshLayout);
    }

    /**
     * 监听时间
     */
    protected void listenEvent() {
        super.listenEvent();

        // 收到通知刷新我的圈子数据
        GVideoEventBus.get(CirclePluginImpl.UPDATE_MY_CIRCLE_LIST)
                .observe(this, object -> viewModel.loadMyCircle());

        GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE).observe(this, o -> loadData());

    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        postExposure(mFistVisible, mLastVisible, true);
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        postExposure(mFistVisible, mLastVisible, false);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        postExposure(mFistVisible, mLastVisible, true);
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        postExposure(mFistVisible, mLastVisible, false);

    }


    //------- 埋点相关  --------
    private int mFistVisible = -1, mLastVisible = -1;

    /**
     * 内容曝光处处理
     *
     * @param start  起始位置
     * @param end    结束位置
     * @param isShow 是否显示
     */
    private void postExposure(int start, int end, boolean isShow) {
        if (start == -1 && end == -1 && myCircleHorizontalAdapter != null) {
            start = 0;
            end = myCircleHorizontalAdapter.getLastPosition();
        }
        if (start < 0 || end < 0 || end < start) {
            return;
        }
        for (int i = start; i <= end; i++) {
            if (isShow) {
                myCircleHorizontalAdapter.enterPosition(i, getPid());
            } else {
                myCircleHorizontalAdapter.exitPosition(i, getPid());
            }
        }
    }

}
