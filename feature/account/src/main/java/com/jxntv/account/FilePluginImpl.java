package com.jxntv.account;

import androidx.annotation.NonNull;
import com.jxntv.account.repository.FileRepository;
import com.jxntv.base.plugin.FilePlugin;
import com.jxntv.base.plugin.IFileRepository;

/**
 * 文件接口实现
 *
 *
 * @since 2020-03-04 18:21
 */
public final class FilePluginImpl implements FilePlugin {
  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public IFileRepository getFileRepository() {
    return new FileRepository();
  }
  //</editor-fold>
}
