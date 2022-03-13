package com.jxntv.account;

import androidx.annotation.NonNull;
import com.jxntv.account.repository.AuthVisitRepository;
import com.jxntv.account.repository.InitializationRepository;
import com.jxntv.base.plugin.IAuthRepository;
import com.jxntv.base.plugin.IInitializationPRepository;
import com.jxntv.base.plugin.InitializationPlugin;

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
