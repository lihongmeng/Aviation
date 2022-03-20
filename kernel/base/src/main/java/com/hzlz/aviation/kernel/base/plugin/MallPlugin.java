package com.hzlz.aviation.kernel.base.plugin;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.library.ioc.Plugin;

public interface MallPlugin extends Plugin {

   BaseFragment getHomeMallFragment();

}
