package com.jxntv.account;

import androidx.annotation.NonNull;
import com.jxntv.account.repository.CompositionRepository;
import com.jxntv.base.plugin.CompositionPlugin;
import com.jxntv.base.plugin.ICompositionRepository;

public final class CompositionPluginImpl implements CompositionPlugin {
  @NonNull @Override public ICompositionRepository getCompositionRepository() {
    return new CompositionRepository();
  }

}
