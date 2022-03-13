package com.jxntv.home;

import static com.jxntv.base.Constant.BUNDLE_KEY.IS_NEED_BROAD_HOME_TO_ME;
import static com.jxntv.base.Constant.BUNDLE_KEY.IS_QA;
import static com.jxntv.base.Constant.BUNDLE_KEY.START_VIDEO_TAB_ID;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.SHOW_HOME_TAB_FOLLOW;
import static com.jxntv.base.Constant.START_PUBLISH_FROM.HOME;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabLayout;
import com.jxntv.base.BackPressHandler;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.StaticParams;
import com.jxntv.base.dialog.BusinessDialog;
import com.jxntv.base.entity.PublishSataData;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.plugin.WatchTvPlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.base.view.GvideoLottieAnimationView;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.event.entity.DrawerLayoutData;
import com.jxntv.home.adapter.HomePageAdapter;
import com.jxntv.home.businessdialog.BusinessDialogViewModel;
import com.jxntv.home.databinding.FragmentHomeBinding;
import com.jxntv.home.utils.HomeTabUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.utils.ScreenUtils;
import com.jxntv.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页默认fragment
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    /**
     * 首页TAG
     */
    public final String TAG = "HomeFragment";
    /**
     * 首页page 适配器
     */
    protected HomePageAdapter mPagerAdapter;
    /**
     * 是否为暗黑模式
     */
    private boolean mIsDarkMode = false;

    private String mStartVideoTabId = "";
    private String mStartFmTabId = "";

    private BaseFragment currentBaseFragment;

    /**
     * 抽屉的Fragment
     * <p>
     * 保存实例是为了在收到广播打开\关闭Fragment时
     * 向里面传当前Fragment的pid
     * 抽屉的Fragment在一些神策上报的场景上需要
     */
    private BaseFragment drawerFragment;

    /**
     * 屏幕宽度
     */
    private int screenSize = ScreenUtils.getAppScreenWidth(GVideoRuntime.getAppContext());

    /**
     * 侧边栏宽度比例
     */
    private static final float DRAWER_RATE = 55f / 72f;

    /**
     * 底部菜单
     */
    private List<TabLayout.Tab> tabList = new ArrayList<>();

    private final Handler handler = new Handler();

    private BusinessDialog businessDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initImmersive() {

    }

    @Override
    protected void initView() {
        //因为个人主页需要全背景图，在此设置全透明背景，需根据情况要在子fragment中设置padding、状态栏图标颜色
        ImmersiveUtils.enterImmersiveFullTransparent(getActivity(), true);
        initViewPager();
        initTabLayout();
        initMeAlphaButton();

        mBinding.recordButton.setOnClickListener(v -> {
            Context context = v.getContext();
            if (context == null) {
                return;
            }
            // 未登录需要跳转到登录页面
            String token = PluginManager.get(AccountPlugin.class).getToken();
            if (TextUtils.isEmpty(token)) {
                PluginManager.get(AccountPlugin.class).startLoginActivity(v.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(StaticParams.currentStatPid),
                        ResourcesUtils.getString(R.string.publish)
                );
            } else {

                RecordPlugin recordPlugin = PluginManager.get(RecordPlugin.class);

                // 获取发布页面外链相关的白名单
                recordPlugin.initWhiteListData();

                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_QA, false);

                // 如果是平台运营人员，弹出选择弹窗
                // 如果不是，直接启动发布页
                if (PluginManager.get(AccountPlugin.class).getIsPlatformUser()) {
                    recordPlugin.showRecordDialog(context, bundle);
                } else {
                    recordPlugin.startPublishFragmentUseActivity(context, bundle);
                }
            }
        });

        mBinding.viewPager.setCurrentItem(0);

        HomeTabUtils.getInstance().setTabListener(this::initBottomBar);
        initBottomBar();
        listenEvent();

        int width = SizeUtils.getDensityWidth();
        int rightDrawerSize = (int) (width * DRAWER_RATE);
        ViewGroup.LayoutParams rightParams = mBinding.fragmentLayoutRightDrawer.getLayoutParams();
        rightParams.width = rightDrawerSize;
        mBinding.fragmentLayoutRightDrawer.setLayoutParams(rightParams);

        drawerFragment = (BaseFragment) PluginManager.get(AccountPlugin.class).getDrawerFragment();

        // 添加侧边栏 Fragment
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout_right_drawer, drawerFragment)
                .commit();
        mBinding.floatView.setCollapseEventBus(this);
    }

    @Override
    protected void bindViewModels() {
        BusinessDialogViewModel businessDialogViewModel = ViewModelProviders.of(this).get(BusinessDialogViewModel.class);
        businessDialogViewModel.imagesBeanLiveData.observe(
                this,
                imagesBean -> {
                    businessDialog = new BusinessDialog(
                            getActivity(),
                            imagesBean,
                            StatPid.HOME,
                            null
                    );
                    businessDialog.showImagesBeanData(imagesBean);
                }
        );
        businessDialogViewModel.requireBusinessDialogData();
    }

    @Override
    protected void loadData() {
        // 添加其他导航
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        PluginManager.get(CirclePlugin.class).addDestinations(this);
        PluginManager.get(WatchTvPlugin.class).addDestinations(this);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);
    }

    private void listenEvent() {

        // 消息中心（未读/已读）状态发生变化
        GVideoEventBus.get(AccountPlugin.EVENT_NEW_MOMENT, Boolean.class).observe(this,
                hasUnread -> {
                    if (mBinding == null) {
                        return;
                    }
                    TabLayout.Tab tab = mBinding.tabLayout.getTabAt(3);
                    if (tab == null) {
                        return;
                    }
                    View customView = tab.getCustomView();
                    if (customView == null) {
                        return;
                    }
                    View unReadView = customView.findViewById(R.id.tab_unread);
                    if (unReadView == null) {
                        return;
                    }
                    unReadView.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
                });

        // 侧边栏（隐藏/展示）状态发生变化
        GVideoEventBus.get(HomePlugin.EVENT_HOME_DRAWER, DrawerLayoutData.class).observe(
                this,
                drawerLayoutData -> {
                    if (drawerFragment != null) {
                        drawerFragment.setPageName(drawerLayoutData.pageName);
                    }
                    if (drawerLayoutData.isOpen) {
                        mBinding.drawerLayout.openDrawer(GravityCompat.END);
                    } else {
                        mBinding.drawerLayout.closeDrawer(GravityCompat.END);
                    }
                });

        // 打开消息中心
        GVideoEventBus.get(HomePlugin.EVENT_HOME_MESSAGE, String.class).observe(
                this,
                pid -> PluginManager.get(AccountPlugin.class).startNotificationFragment(
                        mBinding.drawerLayout,
                        pid
                ));

        // 跳转到"首页"TAB页
        GVideoEventBus.get(HomePlugin.EVENT_HOME_TAB, Bundle.class).observe(this,
                bundle -> {
                    if (bundle == null
                            || mBinding == null
                            || mPagerAdapter == null) {
                        return;
                    }
                    mStartVideoTabId = bundle.getString(START_VIDEO_TAB_ID, "");
                    dealShowHomeChildFragment("");
                });

        // 跳转到"直播"TAB页
        GVideoEventBus.get(HomePlugin.EVENT_LIVE_TAB, String.class).observe(this,
                tabId -> {
                    mStartFmTabId = tabId;
                    mBinding.viewPager.setCurrentItem(1);
                });

        // 跳转到"社区"TAB页
        GVideoEventBus.get(HomePlugin.EVENT_CIRCLE, Integer.class).observe(
                this,
                index -> {
                    StaticParams.circleFragmentInitIndex = index;
                    mBinding.viewPager.setCurrentItem(2);
                });

        // 跳转到"我的"TAB页
        GVideoEventBus.get(HomePlugin.EVENT_PERSONAL, String.class).observe(this,
                authorId -> mBinding.viewPager.setCurrentItem(3));

        // 启动PGC页面
        GVideoEventBus.get(HomePlugin.EVENT_PGC, String.class).observe(this,
                authorId -> {
                    AuthorModel author = AuthorModel.Builder.anAuthorModel()
                            .withId(authorId)
                            .withType(AuthorType.PGC)
                            .build();
                    PluginManager.get(AccountPlugin.class).startPgcActivity(mBinding.tabLayout, author);
                });

        // 启动UGC页面
        GVideoEventBus.get(HomePlugin.EVENT_UGC, String.class).observe(this,
                authorId -> {
                    AuthorModel author = AuthorModel.Builder.anAuthorModel()
                            .withId(authorId)
                            .withType(AuthorType.UGC)
                            .build();
                    PluginManager.get(AccountPlugin.class).startPgcActivity(mBinding.tabLayout, author);
                });

        // 发布成功后会收到通知，跳转到关注页
        GVideoEventBus.get(SHOW_HOME_TAB_FOLLOW, PublishSataData.class).observe(
                this,
                publishSataData -> {
                    Activity activity = getActivity();
                    if (publishSataData == null
                            || activity == null
                            || !TextUtils.equals(publishSataData.startPublishFrom, HOME)) {
                        return;
                    }
                    if (publishSataData.circle == null) {
                        dealShowHomeChildFragment(StatPid.HOME_FOLLOW);
                    } else {
                        PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(
                                activity,
                                publishSataData.circle,
                                null
                        );
                    }
                }
        );

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.HOME_TO_ME).observe(this,
                value -> {
                    if (mBinding != null) {
                        mBinding.viewPager.setCurrentItem(3);
                    }
                });

    }

    /**
     * 先跳转到Home页，再根据TabId跳转到指定页面
     *
     * @param tabId 页面对应Id
     */
    public void dealShowHomeChildFragment(String tabId) {
        if (mBinding == null
                || mPagerAdapter == null) {
            return;
        }
        mBinding.viewPager.setCurrentItem(0);
        if (TextUtils.isEmpty(tabId)) {
            return;
        }
        ((BaseFragment<?>) mPagerAdapter.getItem(0)).showFragmentWithTabId(tabId);
    }

    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        mPagerAdapter = new HomePageAdapter(getChildFragmentManager(), R.id.view_pager, 4) {
            @Override
            protected BaseFragment createFragment(int position) {
                BaseFragment fragment = super.createFragment(position);
                if (currentBaseFragment == null) {
                    currentBaseFragment = fragment;
                    currentBaseFragment.onTabResumeFragment();
                }
                return fragment;
            }
        };
        mBinding.viewPager.setAdapter(mPagerAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (currentBaseFragment != null) {
            currentBaseFragment.onFragmentResume();
        }
        mBinding.floatView.resumeRotate();
        GVideoEventBus.get(Constant.EVENT_MSG.HAS_BACK_HOME).post(null);
        GVideoEventBus.get(AccountPlugin.EVENT_UPDATE_UNREAD_MESSAGE_COUNT).post(null);
        ((BaseActivity) requireActivity()).registerBackPressHandler(mBackPressHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (currentBaseFragment != null) {
            currentBaseFragment.onFragmentPause();
        }
        ((BaseActivity) requireActivity()).unregisterBackPressHandler(mBackPressHandler);
    }

    private long lastClickTabTime = 0;

    /**
     * 初始化tabLayout
     */
    private void initTabLayout() {
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("", "onTabSelected -->>" + position);

                setSelectedTab(tab);
                lastClickTabTime = System.currentTimeMillis();
                // 通知清除未读小红点
                if (position == 3) {
                    GVideoEventBus.get(AccountPlugin.EVENT_NEW_MOMENT, Boolean.class).post(false);
                }

                if (currentBaseFragment != null) {
                    currentBaseFragment.onTabPauseFragment();
                }
                currentBaseFragment = (BaseFragment) mPagerAdapter.getItem(position);
                currentBaseFragment.onTabResumeFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                setSelectedTab(tab);
                if (System.currentTimeMillis() - lastClickTabTime > 1000) {
                    GVideoEventBus.get(Constant.EVENT_MSG.BACK_TOP).post(null);
                    lastClickTabTime = System.currentTimeMillis();
                }
            }
        });

    }

    /**
     * 设置选中tab
     *
     * @param tab
     */
    private void setSelectedTab(TabLayout.Tab tab) {
        for (TabLayout.Tab t : tabList) {
            setTabState(t, t.getPosition() == tab.getPosition());
        }
    }

    /**
     * 设置tab状态
     *
     * @param isSelected 按钮是否被选中
     */
    private void setTabState(TabLayout.Tab tab, boolean isSelected) {
        View view = tab.getCustomView();
        if (view != null) {
            GvideoLottieAnimationView lottieAnimationView = view.findViewById(R.id.tab_icon);
            if (lottieAnimationView != null) {
                if (isSelected) {
                    if (lottieAnimationView.getMaxFrame() > 0) {
                        lottieAnimationView.setMinFrame(1);
                    }
                    lottieAnimationView.playAnimation();
                } else {
                    lottieAnimationView.setMinFrame(0);
//                  未选中状态
                    lottieAnimationView.setFrame(0);
                }
            }
        }
    }

    private void initMeAlphaButton() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mBinding.me.getLayoutParams();
        layoutParams.width = screenSize / 5;
        mBinding.me.setLayoutParams(layoutParams);
        mBinding.me.setOnClickListener(v -> {
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (accountPlugin.hasLoggedIn()) {
                mBinding.viewPager.setCurrentItem(3);
            } else {
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_NEED_BROAD_HOME_TO_ME, true);
                accountPlugin.startLoginActivity(v.getContext(),bundle);
            }
        });
    }

    /**
     * 初始化bottom bar
     */
    private void initBottomBar() {
        tabList.clear();
        initTabView(mBinding.tabLayout.getTabAt(0), R.drawable.home_tab_feed_drawable, R.string.home_page, 0, 0);
        initTabView(mBinding.tabLayout.getTabAt(1), R.drawable.home_tab_live_drawable, R.string.live, screenSize / 10, 0);
        initTabView(mBinding.tabLayout.getTabAt(2), R.drawable.home_tab_community_drawable, R.string.community, 0, screenSize / 10);
        initTabView(mBinding.tabLayout.getTabAt(3), R.drawable.home_tab_person_drawable, R.string.mine, 0, 0);
        setSelectedTab(tabList.get(0));
        HomeTabUtils.getInstance().initRecordButton(mBinding.recordButton);
    }

    /**
     * 初始化tab视图
     */
    private void initTabView(TabLayout.Tab tab, int drawableId, int textId, int rightMargin, int leftMargin) {
        if (tab == null) {
            return;
        }
        tab.setCustomView(R.layout.home_tab_item_layout);
        View view = tab.getCustomView();
        if (view == null) {
            return;
        }
        View tabView = tab.view;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
        layoutParams.width = screenSize / 5;
        layoutParams.rightMargin = rightMargin;
        layoutParams.leftMargin = leftMargin;
        GvideoLottieAnimationView lottieAnimationView = view.findViewById(R.id.tab_icon);
        HomeTabUtils.getInstance().setLottieRes(lottieAnimationView, null, tab.getPosition());
//        ((ImageView) view.findViewById(R.id.tab_icon)).setImageResource(drawableId);
//        lottieAnimationView.setImageAssetsFolder("images");
//        lottieAnimationView.setAnimation("data.json");
//        lottieAnimationView.playAnimation();
        ((TextView) view.findViewById(R.id.tab_text)).setText(textId);
        tabList.add(tab);
    }

    /**
     * 更新夜间模式
     *
     * @param isDarkMode 是否为暗黑模式
     */
    private void updateDarkMode(boolean isDarkMode) {
        // ImmersiveUtils.enterImmersive(this, getBackgroundColor(isDarkMode), !isDarkMode);
        mBinding.tabLayout.setBackgroundColor(getBackgroundColor(isDarkMode));
        updateText(mBinding.tabLayout.getTabAt(0), isDarkMode);
        updateText(mBinding.tabLayout.getTabAt(1), isDarkMode);
        updateText(mBinding.tabLayout.getTabAt(3), isDarkMode);
        updateShadow(isDarkMode);

    }

    /**
     * 更新夜间模式文字
     *
     * @param tab    待处理的tab
     * @param isDark 是否为暗黑模式
     */
    private void updateText(TabLayout.Tab tab, boolean isDark) {
        if (tab == null) {
            return;
        }
        View view = tab.getCustomView();
        if (view == null) {
            return;
        }
        int color = isDark ? R.color.t_color05 : R.color.home_tab_text_color;
        ((TextView) view.findViewById(R.id.tab_text)).setTextColor(getColor(color));
    }

    /**
     * 更新shadow
     *
     * @param isDark 是否为暗黑模式
     */
    private void updateShadow(boolean isDark) {
        mBinding.shadow.setBackgroundColor(getColor(isDark ? R.color.home_background_color_dark : R.color.c_shadow01));
    }

    /**
     * 获取背景色
     *
     * @param isDarkMode 是否为暗黑模式
     */
    private int getBackgroundColor(boolean isDarkMode) {
        return getColor(isDarkMode ? R.color.home_background_color_dark : R.color.home_background_color);

    }

    @Override
    protected boolean enableImmersive() {
        return true;
    }

    @Override
    public int statusBarColor() {
        return getBackgroundColor(mIsDarkMode);
    }

    /**
     * 获取颜色
     *
     * @param color 待获取的颜色
     */
    private int getColor(int color) {
        return GVideoRuntime.getAppContext().getResources().getColor(color);
    }

    private long backTime;
    /**
     * 返回监听
     */
    private final BackPressHandler mBackPressHandler = new BackPressHandler() {
        @Override
        public boolean onBackPressed() {
            if (mBinding.viewPager.getCurrentItem() != 0) {
                mBinding.viewPager.setCurrentItem(0);
                return true;
            }
            if (System.currentTimeMillis() - backTime <= 3000) {
                finishActivity();
            } else {
                ToastUtils.showShort("再按一次退出" + getString(R.string.app_name));
                backTime = System.currentTimeMillis();
            }
            return true;
        }
    };

    @Override
    public String getPid() {
        if (currentBaseFragment == null) {
            return StatPid.HOME;
        }
        return currentBaseFragment.getPageName();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
