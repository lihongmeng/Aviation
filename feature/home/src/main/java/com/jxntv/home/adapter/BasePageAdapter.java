package com.jxntv.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.jxntv.base.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页page适配器基类
 */
public abstract class BasePageAdapter extends FragmentPagerAdapter {

    /** 当前持有的多个fragment */
    protected final List<BaseFragment> mFragmentList;
    /** fragment管理器 */
    protected final FragmentManager mFragmentManager;
    /** 当前视图id */
    protected final int mViewId;

    /**
     * 构造方法
     */
    public BasePageAdapter(FragmentManager fm, int viewId, int count) {
        super(fm);
        mFragmentManager = fm;
        mViewId = viewId;
        mFragmentList = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            mFragmentList.add(null);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment = mFragmentList.get(position);
        if (fragment != null) {
            return fragment;
        }
        fragment = (BaseFragment) mFragmentManager
                .findFragmentByTag(makeFragmentName(mViewId, getItemId(position)));
        if (fragment == null) {
            fragment = createFragment(position);
        }
        mFragmentList.set(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * 根据位置创建对应的fragment
     *
     * @param position  对应的位置
     */
    protected abstract BaseFragment createFragment(int position);

    /**
     * 用于获取对应fragment
     */
    public static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
