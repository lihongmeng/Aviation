package com.hzlz.aviation.feature.home;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GravityCompat;

import com.google.android.material.tabs.TabLayout;
import com.hzlz.aviation.feature.home.adapter.HomePageAdapter;
import com.hzlz.aviation.feature.home.databinding.FragmentHomeBinding;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.plugin.WatchTvPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.event.entity.DrawerLayoutData;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.widget.widget.AviationImageView;
import com.hzlz.aviation.library.widget.widget.AviationTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页默认fragment
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    /**
     * 首页page 适配器
     */
    protected HomePageAdapter mPagerAdapter;
    /**
     * 是否为暗黑模式
     */
    private boolean mIsDarkMode = false;

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
    private final int screenSize = ScreenUtils.getAppScreenWidth(GVideoRuntime.getAppContext());

    /**
     * 侧边栏宽度比例
     */
    private static final float DRAWER_RATE = 55f / 72f;

    /**
     * 底部菜单
     */
    private final List<TabLayout.Tab> tabList = new ArrayList<>();

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

        mBinding.viewPager.setCurrentItem(0);
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

        mBinding.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginManager.get(RecordPlugin.class).startPublishFragmentUseActivity(getActivity(),null);
            }
        });
    }

    @Override
    protected void bindViewModels() {
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
            ((AviationImageView)view.findViewById(R.id.tab_icon)).getDrawable().setTint(ResourcesUtils.getColor(isSelected ? R.color.color_e6e6e6 : R.color.color_e6e6e6_50));
            ((AviationTextView)view.findViewById(R.id.tab_text)).setTextColor(ResourcesUtils.getColor(isSelected ? R.color.color_e6e6e6 : R.color.color_e6e6e6_50));

        }
    }

    /**
     * 初始化bottom bar
     */
    private void initBottomBar() {
        tabList.clear();
        initTabView(0, R.drawable.home_tab_feed_drawable, R.string.home_page);
        initTabView(1, R.drawable.home_tab_live_drawable, R.string.community);
        initTabView(2, R.drawable.home_tab_community_drawable, R.string.good_thing);
        initTabView(3, R.drawable.home_tab_person_drawable, R.string.mine);
        setSelectedTab(tabList.get(0));
    }

    /**
     * 初始化tab视图
     */
    private void initTabView(int index, int drawableId, int textId) {
        TabLayout.Tab tab = mBinding.tabLayout.getTabAt(index);
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
        layoutParams.width = screenSize / 4;
        layoutParams.rightMargin = 0;
        layoutParams.leftMargin = 0;
        ((TextView) view.findViewById(R.id.tab_text)).setText(textId);
        ((AviationImageView) view.findViewById(R.id.tab_icon)).setImageResource(drawableId);
        tabList.add(tab);
    }

    /**
     * 获取背景色
     *
     * @param isDarkMode 是否为暗黑模式
     */
    private int getBackgroundColor(boolean isDarkMode) {
        return getColor(isDarkMode ? R.color.color_1b1c1f : R.color.color_ffffff);

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

}
