package com.hzlz.aviation.kernel.base.plugin;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.Plugin;

/**
 * 文件接口
 *
 *
 * @since 2020-03-04 17:19
 */
public interface FilePlugin extends Plugin {
  String AUTHORITY = GVideoRuntime.getPackageName() +".provider";
  @NonNull
  IFileRepository getFileRepository();
}
