package com.hzlz.aviation.feature.account;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.repository.CompositionRepository;
import com.hzlz.aviation.kernel.base.plugin.CompositionPlugin;
import com.hzlz.aviation.kernel.base.plugin.ICompositionRepository;

public final class CompositionPluginImpl implements CompositionPlugin {
  @NonNull @Override public ICompositionRepository getCompositionRepository() {
    return new CompositionRepository();
  }

}
