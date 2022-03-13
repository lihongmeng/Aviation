package com.jxntv.base.plugin;

import androidx.annotation.NonNull;
import java.io.File;

/**
 * 文件创建接口
 *
 *
 * @since 2020-03-07 16:45
 */
public interface IFileCreator {
  @NonNull
  File createFile();
}
