package com.jxntv.base.plugin;

import androidx.annotation.NonNull;

import com.jxntv.base.BuildConfig;
import com.jxntv.ioc.Plugin;
import com.jxntv.runtime.GVideoRuntime;

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
