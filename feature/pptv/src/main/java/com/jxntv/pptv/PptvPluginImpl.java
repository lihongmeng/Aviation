package com.jxntv.pptv;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.plugin.PptvPlugin;
import com.jxntv.pptv.ui.channel.ChannelFragment;

public final class PptvPluginImpl implements PptvPlugin {
  @Override public BaseFragment getPptvFragment() {
    return new ChannelFragment();
  }

  @Override public void addDestinations(@NonNull BaseFragment fragment) {
    fragment.addDestination(R.navigation.channel_nav_graph);
    fragment.addDestination(R.navigation.more_nav_graph);
  }
}
