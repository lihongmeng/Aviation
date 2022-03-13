package com.jxntv.base.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.jxntv.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class BaseFragmentVpAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> baseFragmentList = new ArrayList<>();

    public BaseFragmentVpAdapter(FragmentManager fm) {
        super(fm);
    }

    public void updateSource(List<BaseFragment> baseFragmentList) {
        if (baseFragmentList == null) {
            this.baseFragmentList.clear();
        } else {
            this.baseFragmentList = baseFragmentList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return baseFragmentList == null ? 0 : baseFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return baseFragmentList.get(position);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
