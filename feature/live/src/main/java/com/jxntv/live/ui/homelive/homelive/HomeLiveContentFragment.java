package com.jxntv.live.ui.homelive.homelive;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jxntv.stat.StatPid.HOME_LIVE;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.live.R;
import com.jxntv.live.adapter.PlayingLiveAdapter;
import com.jxntv.live.databinding.FragmentHomeLiveContentBinding;
import com.jxntv.sensordata.GVideoSensorDataManager;

public class HomeLiveContentFragment extends BaseFragment<FragmentHomeLiveContentBinding> {

    private HomeLiveContentViewModel viewModel;

    private final PlayingLiveAdapter.Listener listener = (view, author) -> {
        if (author == null) {
            showToast(getString(R.string.join_failed));
            return;
        }
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
            if (plugin != null) {
                plugin.startLoginActivity(view.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        getPid(),
                        "点击直播-直播"
                );
            }
            return;
        }
        viewModel.follow(author);
    };

    @Override
    public String getPid() {
        return HOME_LIVE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_live_content;
    }

    @Override
    protected void initView() {
        setupPlaceholderLayout(R.id.empty_container);
    }

    @Override
    protected void bindViewModels() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        viewModel = bingViewModel(HomeLiveContentViewModel.class);
        mBinding.setViewModel(viewModel);

        GVideoEventBus.get(Constant.EVENT_MSG.BACK_TOP).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (isUIVisible()) {
                    mBinding.recyclerView.scrollToPosition(0);
                    mBinding.refreshLayout.autoRefresh();
                }
            }
        });

        viewModel.updateLiveReviewTitle.observe(
                this,
                value -> mBinding.titleLiveReview.setVisibility(value ? VISIBLE : GONE)
        );

        viewModel.loadPlayingData.observe(
                this,
                setNoMoreData -> viewModel.playingLiveAdapter.updateDataSource(viewModel.playLiveList)
        );

        viewModel.playingLiveAdapter = new PlayingLiveAdapter(activity);
        viewModel.playingLiveAdapter.setListener(listener);
        mBinding.living.setAdapter(viewModel.playingLiveAdapter);

        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GVideoEventBus.get(Constant.EVENT_MSG.COLLAPSE_AUDIO_FLOAT_VIEW).post(null);
            }
        });
    }

    @Override
    protected void loadData() {
        viewModel.onRefresh(mBinding.refreshLayout);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

        if (mBinding != null
                && System.currentTimeMillis() - leaveTime > Constant.CONFIG.AUTO_REFRESH_TIME) {
            mBinding.recyclerView.scrollToPosition(0);
            mBinding.refreshLayout.autoRefresh();
            leaveTime = System.currentTimeMillis();
        }
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public void onReload(@NonNull View view) {
        super.onReload(view);
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
        viewModel.onRefresh(mBinding.refreshLayout);
    }
}
