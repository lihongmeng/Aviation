package com.hzlz.aviation.feature.account;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.repository.AuthVisitRepository;
import com.hzlz.aviation.feature.account.repository.InitializationRepository;
import com.hzlz.aviation.kernel.base.plugin.IAuthRepository;
import com.hzlz.aviation.kernel.base.plugin.IInitializationPRepository;
import com.hzlz.aviation.kernel.base.plugin.InitializationPlugin;

/**
 * 初始化接口实现
 *
 *
 * @since 2020-03-07 21:21
 */
public final class InitializationPluginImpl implements InitializationPlugin {
  @NonNull
  @Override
  public IInitializationPRepository getInitializationPRepository() {
    return new InitializationRepository();
  }

  @NonNull @Override public IAuthRepository getAuthRepository() {
    return new AuthVisitRepository();
  }
}
