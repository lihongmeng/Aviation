package com.jxntv.home.adapter;

import androidx.fragment.app.FragmentManager;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.FeedPlugin;
import com.jxntv.base.plugin.LivePlugin;
import com.jxntv.base.plugin.WatchTvPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;

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
                return PluginManager.get(LivePlugin.class).getHomeLiveFragment();
            case 2:
                return PluginManager.get(CirclePlugin.class).getCircleFragment();
            case 3:
                return PluginManager.get(AccountPlugin.class).getMeFragment();
            default:
                break;
        }
        throw new RuntimeException("invalid position!!!");
    }
}
