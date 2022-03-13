package com.jxntv.base.webview;

import android.webkit.WebView;
import androidx.annotation.NonNull;

/**
 * JavaScript 通信处理器接口
 *
 *
 * @since 2020-02-07 10:58
 */
public interface JavaScriptCommunicationHandler {
  /**
   * H5 协议处理结果
   */
  class HandleResult {
    /** 是否拦截处理 */
    boolean handled;
    /** 返回给H5的结果 */
    String result;

    public HandleResult(boolean handled, String result) {
      this.handled = handled;
      this.result = result;
    }
  }
  /**
   * 处理
   *
   * @param webView WebView
   * @param uri URI
   * @return 处理结果
   */
  HandleResult handle(@NonNull WebView webView, @NonNull String uri);
}
