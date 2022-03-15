package com.hzlz.aviation.kernel.base.webview;

import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaScript 通信分发器
 *
 *
 * @since 2020-02-07 11:00
 */
public final class JavaScriptCommunicationDispatcher {
  //<editor-fold desc="属性">
  @Nullable
  private List<JavaScriptCommunicationHandler> mHandlerList;
  //</editor-fold>

  //<editor-fold desc="单例">

  private JavaScriptCommunicationDispatcher() {

  }

  private static class Inner {
    @NonNull
    private static final JavaScriptCommunicationDispatcher sInstance =
        new JavaScriptCommunicationDispatcher();
  }

  /**
   * 获取 JavaScriptCommunicationDispatcher 实例
   *
   * @return JavaScriptCommunicationDispatcher 实例 {@link JavaScriptCommunicationDispatcher}
   */
  @NonNull
  public static JavaScriptCommunicationDispatcher getInstance() {
    return Inner.sInstance;
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 添加 JavaScript 通信处理器
   *
   * @param handler JavaScript 通信处理器 {@link JavaScriptCommunicationHandler}
   */
  @SuppressWarnings("UnusedReturnValue")
  public JavaScriptCommunicationDispatcher addHandler(
      @NonNull JavaScriptCommunicationHandler handler) {
    if (mHandlerList == null) {
      mHandlerList = new ArrayList<>();
    }
    mHandlerList.add(handler);
    return this;
  }

  /**
   * 分发
   *
   * @param webView WebView
   * @param uri URI
   */
  JavaScriptCommunicationHandler.HandleResult dispatch(@NonNull WebView webView, @NonNull String uri) {
    JavaScriptCommunicationHandler.HandleResult handleResult = null;
    if (mHandlerList != null) {
      for (JavaScriptCommunicationHandler handler : mHandlerList) {
        handleResult = handler.handle(webView, uri);
        if (handleResult != null && handleResult.handled) {
          break;
        }
      }
    }
    return handleResult;
  }
  //</editor-fold>
}
