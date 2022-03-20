package com.hzlz.aviation.feature.home.adapter;

import androidx.fragment.app.FragmentManager;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CirclePlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.MallPlugin;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;

/**
 * 首页page适配器
 */
public class HomePageAdapter extends BasePageAdapter {

    /**
     * 构造方法
     */
    public HomePageAdapter(FragmentManager fm, int viewId, int count) {
        super(fm, viewId, count);
    }

    /**
     * 根据位置创建对应的fragment
     *
     * @param position 对应的位置
     */
    protected BaseFragment createFragment(int position) {
        switch (position) {
            case 0:
                return PluginManager.get(FeedPlugin.class).getFeedFragment(StatPid.HOME);
            case 1:
                return PluginManager.get(CirclePlugin.class).getHomeCommunityFragment();
            case 2:
                return PluginManager.get(MallPlugin.class).getHomeMallFragment();
            case 3:
                return PluginManager.get(AccountPlugin.class).getMeFragment();
            default:
                break;
        }
        throw new RuntimeException("invalid position!!!");
    }
}
