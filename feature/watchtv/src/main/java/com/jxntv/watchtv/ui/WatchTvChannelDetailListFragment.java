package com.jxntv.watchtv.ui;

import static com.jxntv.base.Constant.BUNDLE_KEY.CHANNEL_ID;
import static com.jxntv.base.Constant.BUNDLE_KEY.DATE_DAY;
import static com.jxntv.base.Constant.BUNDLE_KEY.DATE_MONTH;
import static com.jxntv.base.Constant.BUNDLE_KEY.DATE_YEAR;
import static com.jxntv.base.Constant.BUNDLE_KEY.INDEX;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jxntv.base.BaseFragment;
import com.jxntv.watchtv.R;
import com.jxntv.watchtv.adapter.WatchTvChannelDetailListAdapter;
import com.jxntv.watchtv.databinding.FragmentWatchTvChannelDetailListBinding;
import com.jxntv.watchtv.entity.ChannelTvManifest;

import java.util.ArrayList;

/**
 * 看电视频道详情页下方ViewPager的子Fragment
 */
public class WatchTvChannelDetailListFragment extends BaseFragment<FragmentWatchTvChannelDetailListBinding> {

    // WatchTvChannelDetailListViewModel
    private WatchTvChannelDetailListViewModel viewModel;

    // 内容适配器
    private WatchTvChannelDetailListAdapter adapter;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_watch_tv_channel_detail_list;
    }

    @Override
    protected void initView() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        setupPlaceholderLayout(R.id.empty_container);
        adapter = new WatchTvChannelDetailListAdapter(activity);
        mBinding.list.setAdapter(adapter);
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(WatchTvChannelDetailListViewModel.class);

        Bundle arguments = getArguments();
        if (arguments != null) {
            viewModel.channelId = arguments.getLong(CHANNEL_ID);
            viewModel.year = arguments.getInt(DATE_YEAR);
            viewModel.month = arguments.getInt(DATE_MONTH);
            viewModel.day = arguments.getInt(DATE_DAY);
            viewModel.index = arguments.getInt(INDEX);
        }

        initDataObserve();
    }


    // ViewPage配合Fragment List的组合方式，可能生命周期的问题
    // 对updateFragmentList监听会失效，所以可以重新绑定
    private void initDataObserve() {
        if (viewModel == null) {
            return;
        }
        viewModel.updateFragmentList.observe(this, o -> {
                    if (viewModel.channelTvManifestList == null) {
                        viewModel.channelTvManifestList = new ArrayList<>();
                    }
                    adapter.refreshData(viewModel.channelTvManifestList);

                    if (adapter.currentPlayPosition != 0) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mBinding.list.getLayoutManager();
                        if (linearLayoutManager == null) {
                            return;
                        }
                        linearLayoutManager.scrollToPositionWithOffset(adapter.currentPlayPosition - 1, 0);
                    }
                }
        );

        viewModel.getUpdatePlaceLiveData().observe(this, this::updatePlaceholderLayoutType);
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        if (viewModel == null) {
            return;
        }
        viewModel.channelId = bundle.getLong(CHANNEL_ID);
    }

    @Override
    protected void loadData() {
        if (viewModel == null) {
            return;
        }
        initDataObserve();
        viewModel.updateData();
    }

    @Override
    public void onReload(@NonNull View view) {
        super.onReload(view);
        if (viewModel == null) {
            return;
        }
        initDataObserve();
        viewModel.updateData();
    }

    protected void loadData(Long channelId) {
        if (viewModel == null) {
            return;
        }
        initDataObserve();
        viewModel.channelId = channelId;
        viewModel.updateData();
    }

    public ChannelTvManifest getNextChannelTvManifest() {
        return adapter.getNextChannelTvManifest();
    }

}
