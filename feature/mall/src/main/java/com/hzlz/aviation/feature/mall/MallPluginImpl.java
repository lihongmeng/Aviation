package com.hzlz.aviation.feature.mall;

import com.hzlz.aviation.feature.mall.fragment.HomeMallFragment;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.plugin.MallPlugin;

public class MallPluginImpl implements MallPlugin {
    @Override
    public BaseFragment getHomeMallFragment() {
        return new HomeMallFragment();
    }
}
