package com.jxntv.circle.fragment;

import static com.jxntv.base.Constant.BUNDLE_KEY.COMMUNITY_NAME;
import static com.jxntv.base.Constant.BUNDLE_KEY.TENANT_ID;
import static com.jxntv.base.Constant.BUNDLE_KEY.TENANT_NAME;
import static com.jxntv.base.Constant.EXTRA_GATHER_ID;
import static com.jxntv.base.Constant.EXTRA_GROUP_ID;
import static com.jxntv.base.Constant.EXTRA_PID;
import static com.jxntv.base.Constant.EXTRA_TYPE;
import static com.jxntv.base.plugin.CirclePlugin.CIRCLE_HOTTEST_FRAGMENT_UPDATE;
import static com.jxntv.stat.StatPid.CIRCLE_DETAIL;

import android.os.Bundle;
import android.text.TextUtils;

import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.utils.DefaultPageNumberUtil;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.FragmentCircleDetailListBinding;
import com.jxntv.circle.viewmodel.CircleDetailListViewModel;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.stat.StatPid;

public class CircleDetailListFragment extends MediaPageFragment<FragmentCircleDetailListBinding> {

    private CircleDetailListViewModel viewModel;
    private String pid;

    @Override
    protected void initView() {
        init(getGvFragmentId(), false);
        super.initView();
        initListener();
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(CircleDetailListViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            viewModel.gatherId = bundle.getLong(EXTRA_GATHER_ID, -999);
            viewModel.groupId = bundle.getLong(EXTRA_GROUP_ID, -999);
            viewModel.key = CIRCLE_DETAIL + "_" + viewModel.groupId + "_" + viewModel.gatherId;
            viewModel.communityName = bundle.getString(COMMUNITY_NAME, "");
            viewModel.tenantId = bundle.getLong(TENANT_ID,-1);
            viewModel.tenantName = bundle.getString(TENANT_NAME,"");
            viewModel.type = bundle.getInt(EXTRA_TYPE);

            pid = bundle.getString(EXTRA_PID, "").trim();
            pid = pid.replaceAll("\u00A0", "");
            if (!TextUtils.isEmpty(viewModel.communityName)) {
                pid = "社区" + "-" + viewModel.communityName + "-" + pid;
            }
        }
        StatFromModel stat = new StatFromModel();
        stat.pid = pid;
        viewModel.statFromModel = stat;

        viewModel.updateTabId(getTabId());
        mBinding.setViewModel(viewModel);

    }

    @Override
    protected void loadData() {
        super.loadData();
    }

    @Override
    protected void checkLoginStatus() {
        viewModel.checkNetworkAndLoginStatus();
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle_detail_list;
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
    public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
        return viewModel.createShortListModel(mediaModel);
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
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        initListener();
    }

    private void initListener() {
        GVideoEventBus.get(CIRCLE_HOTTEST_FRAGMENT_UPDATE).observe(
                this,
                sortType -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.sortType = (int) sortType;
                    viewModel.loadRefreshData();
                }
        );
    }

    public void loadRefreshDataAndResetPage(){
        if(viewModel==null){
            return;
        }
        new DefaultPageNumberUtil().saveDefaultPageNumber(viewModel.key, DefaultPageNumberUtil.MIN);
        viewModel.loadRefreshData();
    }

    public void loadRefreshData(){
        if(viewModel==null){
            return;
        }
        viewModel.loadRefreshData();
    }

    @Override
    public String getFragmentLabel() {
        return StatPid.CIRCLE_DETAIL;
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    protected Long getTenantId() {
        if (viewModel == null) {
            return null;
        }
        return viewModel.tenantId;
    }

    @Override
    protected String getTenantName() {
        if (viewModel == null) {
            return "";
        }
        return viewModel.tenantName;
    }
}
