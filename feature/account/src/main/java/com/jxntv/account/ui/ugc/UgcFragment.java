package com.jxntv.account.ui.ugc;

import static com.jxntv.base.Constant.EXTRA_AUTHOR_ID;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentUgcBinding;
import com.jxntv.account.ui.ugc.adapter.UgcCircleAdapter;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;
import com.ruffian.library.widget.helper.RBaseHelper;

/**
 * UGC 页面
 */
public final class UgcFragment extends BaseFragment<FragmentUgcBinding> {

    private UgcViewModel mUgcViewModel;
    private UgcCircleAdapter circleAdapter;
    private boolean isShowUGCView = true;
    private UGCInitHelper initHelper;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    public String getPid() {
        return StatPid.UGC;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ugc;
    }

    @Override
    protected void updatePlaceholderLayoutType(int type) {
        super.updatePlaceholderLayoutType(type);
        if (type == PlaceholderType.NONE) {
            mBinding.refreshLayout.setVisibility(View.VISIBLE);
        } else {
            mBinding.refreshLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initView() {
        ImmersiveUtils.enterImmersiveFullTransparent(getActivity());
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        initHelper = new UGCInitHelper(mBinding.viewPager, mBinding.appBarLayout.tabLayout);
        int statusBarHeight = WidgetUtils.getStatusBarHeight();
        mBinding.closeLeft.setPadding(0, statusBarHeight, 0, 0);
        int minHeight = getContext().getResources().getDimensionPixelOffset(R.dimen.DIMEN_34DP) + statusBarHeight;
        mBinding.appBarLayout.appbarContent.setMinimumHeight(minHeight);

        setupPlaceholderLayout(R.id.fragment_container);
        mBinding.refreshLayout.setEnableLoadMore(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mBinding.appBarLayout.recyclerCircle.setLayoutManager(layoutManager);
        circleAdapter = new UgcCircleAdapter(getContext());
        mBinding.appBarLayout.recyclerCircle.setAdapter(circleAdapter);

        mBinding.appBarLayout.tabLayout.updateTextSize(R.dimen.sp_13, R.dimen.sp_15);

        mBinding.appBarLayout.appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            Rect rect = new Rect();
            boolean isShow = mBinding.appBarLayout.layoutDetailUser.getGlobalVisibleRect(rect);
            if (isShowUGCView != isShow) {
                isShowUGCView = isShow;
                ImmersiveUtils.setStatusBarIconColor(this, !isShowUGCView);
            }
        });

        mBinding.share.setVisibility(PluginManager.get(SharePlugin.class).isCanShare() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void bindViewModels() {
        mUgcViewModel = bingViewModel(UgcViewModel.class);
        String authorId = getArguments() != null ? getArguments().getString(EXTRA_AUTHOR_ID) : "";
        mUgcViewModel.setAuthorId(authorId);

        mBinding.setViewModel(mUgcViewModel);
        mBinding.appBarLayout.setBinding(mUgcViewModel.getDataBinding());

        mUgcViewModel.getAuthorLiveData().observe(this, author -> {
            Context context = getContext();
            if (author != null
                    && mBinding != null
                    && context != null
            ) {
                mBinding.setAuthor(author.getAuthorObservable());
                mBinding.appBarLayout.setUser(author);
                if (!initHelper.hasInit()) {
                    initHelper.setUserAuthor(author);
                    if (initHelper.initTab(getChildFragmentManager())) {
                        mBinding.viewPager.setCurrentItem(0);
                    }
                } else {
                    initHelper.reloadData();
                }

                RBaseHelper rBaseHelper = mBinding.appBarLayout.followBg.getHelper();
                if (rBaseHelper == null) {
                    return;
                }
                if (!author.isSelf()
                        && !author.isFollow()) {
                    rBaseHelper.setBackgroundColorNormal(ContextCompat.getColor(context, R.color.color_e4344e));
                    rBaseHelper.setBorderColorNormal(ContextCompat.getColor(context, R.color.color_000000_100));
                } else {
                    rBaseHelper.setBackgroundColorNormal(ContextCompat.getColor(context, R.color.color_ffffff_20));
                    rBaseHelper.setBorderColorNormal(ContextCompat.getColor(context, R.color.color_ffffff_40));
                }

            }
        });

        mUgcViewModel.getMyCircleList().observe(this, ugcAuthorModels -> {
            circleAdapter.refreshData(ugcAuthorModels);
        });

        mUgcViewModel.getUGCMenuTabModelLiveData().observe(this, ugcMenuTabModel -> {
            initHelper.setUGCMenuTabModel(ugcMenuTabModel);
            initHelper.initTab(getChildFragmentManager());
        });

        mUgcViewModel.getDataBinding().isFollowLiveData.observe(this, value -> {
            Context context = getContext();
            if (mBinding == null || context == null) {
                return;
            }
            RBaseHelper rBaseHelper = mBinding.appBarLayout.followBg.getHelper();
            if (rBaseHelper == null) {
                return;
            }
            rBaseHelper.setBackgroundColorNormal(value?ContextCompat.getColor(context, R.color.color_ffffff_20):ContextCompat.getColor(context, R.color.color_e4344e));
            rBaseHelper.setBorderColorNormal(value?ContextCompat.getColor(context, R.color.color_ffffff_40):ContextCompat.getColor(context, R.color.color_000000_100));
        });

    }

    @Override
    protected void loadData() {
        mUgcViewModel.onRefresh(mBinding.refreshLayout);
    }

    @Override
    public void onReload(@NonNull View view) {

        mUgcViewModel.onRefresh(mBinding.refreshLayout);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (initHelper != null) {
            initHelper.onFragmentPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initHelper != null) {
            initHelper.onFragmentResume();
        }
        if (getContext() != null) {
            ImmersiveUtils.enterImmersiveFullTransparent(getActivity(), !isShowUGCView);
        }
    }
}
