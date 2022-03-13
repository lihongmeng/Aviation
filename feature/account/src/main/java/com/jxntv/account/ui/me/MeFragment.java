package com.jxntv.account.ui.me;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentMeBinding;
import com.jxntv.account.model.UGCMenuTabModel;
import com.jxntv.account.model.UserAuthor;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.ui.ugc.UGCInitHelper;
import com.jxntv.account.ui.ugc.adapter.UgcCircleAdapter;
import com.jxntv.account.utils.oneKeyLogin.OneKeyLoginUtils;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.model.circle.CircleJoinStatus;
import com.jxntv.base.model.share.FollowChangeModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;

/**
 * 我的界面
 *
 * @since 2020-02-10 14:38
 */
@SuppressWarnings("FieldCanBeLocal")
public final class MeFragment extends BaseFragment<FragmentMeBinding> {

    private MeViewModel mMeViewModel;
    private UgcCircleAdapter circleAdapter;
    private UGCInitHelper initHelper;

    private boolean isShowUGCView = true;

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initImmersive() {

    }

    @Override
    protected void updatePlaceholderLayoutType(int type) {
        super.updatePlaceholderLayoutType(type);
        if (type == PlaceholderType.NONE) {
            mBinding.imageViewDrawerHolder.setVisibility(View.GONE);
            mBinding.refreshLayout.setVisibility(View.VISIBLE);
        } else {
            mBinding.imageViewDrawerHolder.setVisibility(View.VISIBLE);
            mBinding.refreshLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initView() {
        ImmersiveUtils.setStatusBarIconColor(this, !UserManager.hasLoggedIn());
        initHelper = new UGCInitHelper(mBinding.viewPager, mBinding.appBarLayout.tabLayout);
        int statusBarHeight = WidgetUtils.getStatusBarHeight();
        mBinding.closeToolbar.setPadding(0, statusBarHeight, 0, 0);
        mBinding.rightDrawer.setPadding(0, statusBarHeight, 0, 0);
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
            isShowUGCView = mBinding.appBarLayout.layoutDetailUser.getGlobalVisibleRect(rect);
            ImmersiveUtils.setStatusBarIconColor(this, !isShowUGCView);
        });

        boolean isCanShare = PluginManager.get(SharePlugin.class).isCanShare();
        mBinding.openShare.setVisibility(isCanShare ? View.VISIBLE : View.GONE);
        mBinding.closeShare.setVisibility(isCanShare ? View.VISIBLE : View.GONE);

        String pid = getPageName();
        mBinding.imageViewDrawerHolder.setPageName(pid);
        mBinding.openMessageLayout.setPageName(pid);
        mBinding.closeMessageLayout.setPageName(pid);
    }

    @Override
    protected void bindViewModels() {
        mMeViewModel = bingViewModel(MeViewModel.class);
        if (UserManager.hasLoggedIn()) {
            mMeViewModel.setAuthorId(UserManager.getCurrentUser().getId());
        }
        mBinding.setViewModel(mMeViewModel);
        mBinding.appBarLayout.setBinding(mMeViewModel.getDataBinding());

        mMeViewModel.setPid(getPageName());


        mMeViewModel.getAutoRefreshLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refresh) {
                if (refresh) {
                    mMeViewModel.onRefresh(mBinding.refreshLayout);
                }
            }
        });

        mMeViewModel.getAuthorLiveData().observe(this, new NotNullObserver<UserAuthor>() {
            @Override
            protected void onModelChanged(@NonNull UserAuthor author) {
                mBinding.setAuthor(author.getAuthorObservable());
                mBinding.appBarLayout.setUser(author);
                if (!initHelper.hasInit()) {
                    UGCMenuTabModel model = new UGCMenuTabModel();
                    model.setCommentTab(true);
                    model.setFavoriteTab(true);
                    model.setQuestionTab(true);
                    model.setAnswerTab(true);
                    initHelper.setUserAuthor(author);
                    initHelper.setUGCMenuTabModel(model);
                    if (initHelper.initTab(getChildFragmentManager())) {
                        mBinding.viewPager.setCurrentItem(0);
                    }
                } else {
                    initHelper.reloadData();
                }
            }
        });

        mMeViewModel.getMyCircleList().observe(this, ugcAuthorModels -> {
            circleAdapter.clearList();
            circleAdapter.refreshData(ugcAuthorModels);
        });

//        mMeViewModel.getUGCMenuTabModelLiveData().observe(this, ugcMenuTabModel -> {
//            initHelper.setUGCMenuTabModel(ugcMenuTabModel);
//            initHelper.initTab(getChildFragmentManager());
//        });

    }

    @Override
    protected void loadData() {
        listenEvent();
        mMeViewModel.checkNetworkAndLoginStatus();
    }

    @Override
    public void onReload(@NonNull View view) {
        mMeViewModel.checkNetworkAndLoginStatus();
    }
    //</editor-fold>

    /**
     * 退出登录Observer
     */
    Observer<Object> logoutObserver = o -> {
        initHelper.clear();
        mMeViewModel.checkNetworkAndLoginStatus();
    };

    private void listenEvent() {
        // 登录
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
                this, o -> {
                    initHelper.clear();
                    mMeViewModel.checkNetworkAndLoginStatus();
                    PluginManager.get(ChatIMPlugin.class).updateProfile();
                }
        );
        // 登出
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observeForever(logoutObserver);
        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class)
                .observe(this, readCount -> {
                    mBinding.openMessageLayout.setUnreadCount(readCount);
                    mBinding.closeMessageLayout.setUnreadCount(readCount);
                });

        //关注数字变化
        GVideoEventBus.get(SharePlugin.EVENT_FOLLOW_CHANGE, FollowChangeModel.class).observe(this,
                new NotNullObserver<FollowChangeModel>() {
                    @Override
                    protected void onModelChanged(@NonNull FollowChangeModel followChangeModel) {
                        mMeViewModel.checkNetworkAndLoginStatus();
                    }
                });

        // 圈子加入、退出状态发生变化
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class)
                .observe(this, o -> mMeViewModel.checkNetworkAndLoginStatus());


//        //评论
//        GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_ADD).observe(this, o -> {
//            UGCMenuTabModel model = initHelper.getUGCMenuTabModel();
//            if (model!=null){
//                if (!model.isCommentTab()){
//                    model.setCommentTab(true);
//                    initHelper.setUGCMenuTabModel(model);
//                    initHelper.initTab(getChildFragmentManager());
//                }
//            }
//        });
//
//        //喜欢
//        GVideoEventBus.get(SharePlugin.EVENT_FAVORITE_CHANGE, FavoriteChangeModel.class).observe(this, o -> {
//            UGCMenuTabModel model = initHelper.getUGCMenuTabModel();
//            if (model!=null){
//                if (!model.isFavoriteTab()){
//                    model.setFavoriteTab(true);
//                    initHelper.setUGCMenuTabModel(model);
//                    initHelper.initTab(getChildFragmentManager());
//                }
//            }
//        });
//
//        //提问
//        GVideoEventBus.get(Constant.EVENT_MSG.COMPOSITION_ADD_QUESTION).observe(this, o -> {
//            UGCMenuTabModel model = initHelper.getUGCMenuTabModel();
//            if (model!=null){
//                if (!model.isQuestionTab()){
//                    model.setQuestionTab(true);
//                    initHelper.setUGCMenuTabModel(model);
//                    initHelper.initTab(getChildFragmentManager());
//                }
//            }
//        });
//
//        //回答
//        GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_ADD_QA).observe(this, o -> {
//            UGCMenuTabModel model = initHelper.getUGCMenuTabModel();
//            if (model!=null){
//                if (!model.isAnswerTab()){
//                    model.setAnswerTab(true);
//                    initHelper.setUGCMenuTabModel(model);
//                    initHelper.initTab(getChildFragmentManager());
//                }
//            }
//        });
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        if (initHelper != null) {
            initHelper.onFragmentPause();
        }

    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        if (initHelper != null) {
            initHelper.onFragmentResume();
        }
        Context context = getContext();
        if (context != null) {
            ImmersiveUtils.setStatusBarIconColor(this, !isShowUGCView || !UserManager.hasLoggedIn());
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if (initHelper != null) {
            initHelper.onFragmentPause();
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (initHelper != null) {
            initHelper.onFragmentResume();
        }
        if (getContext() != null) {
            ImmersiveUtils.enterImmersiveFullTransparent(getActivity(), !UserManager.hasLoggedIn());
        }
    }

    @Override
    public String getPid() {
        return StatPid.MINE;
    }

    @Override
    protected void onVisible() {
        super.onVisible();

        if (mBinding != null && UserManager.hasLoggedIn() && System.currentTimeMillis() - leaveTime > Constant.CONFIG.AUTO_REFRESH_TIME) {
            leaveTime = System.currentTimeMillis();
            // StaticParams.timeToLateNeedUpdate = false;
            mBinding.refreshLayout.autoRefresh();
        }

    }

    @Override
    public void onDestroy() {
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).removeObserver(logoutObserver);
        super.onDestroy();
    }
}
