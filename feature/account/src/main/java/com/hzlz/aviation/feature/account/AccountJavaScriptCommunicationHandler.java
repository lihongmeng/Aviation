package com.hzlz.aviation.feature.account;

import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.webview.JavaScriptCommunicationHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * 账户模块 JavaScript 通信处理器
 *
 *
 * @since 2020-02-07 11:13
 */
public final class AccountJavaScriptCommunicationHandler implements JavaScriptCommunicationHandler {
  //<editor-fold desc="属性">
  @Nullable
  private Set<CancelAccountCallback> mCancelAccountCallbackSet;
  //</editor-fold>

  //<editor-fold desc="单例">
  private AccountJavaScriptCommunicationHandler() {

  }

  private static class Inner {
    @NonNull
    private static final AccountJavaScriptCommunicationHandler sInstance =
        new AccountJavaScriptCommunicationHandler();
  }

  @NonNull
  public static AccountJavaScriptCommunicationHandler getInstance() {
    return Inner.sInstance;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public HandleResult handle(@NonNull WebView webView, @NonNull String uri) {
    ParseResult result = parseUri(uri);
    if (result == null) {
      return null;
    }
    switch (result.mType) {
      case ParseResult.CANCEL_ACCOUNT:
        if (mCancelAccountCallbackSet != null && !mCancelAccountCallbackSet.isEmpty()) {
          for (CancelAccountCallback callback : mCancelAccountCallbackSet) {
            callback.agreeCancelAccount(webView, (boolean) result.mData);
          }
          return new HandleResult(true, "");
        }
        break;
      default:
        break;
    }
    return null;
  }

  private class ParseResult {
    //<editor-fold desc="常量">
    private static final int CANCEL_ACCOUNT = 1;
    //</editor-fold>

    //<editor-fold desc="属性">
    private int mType;
    @Nullable
    private Object mData;
    //</editor-fold>

    //<editor-fold desc="构造函数">
    private ParseResult(int type, @Nullable Object data) {
      mType = type;
      mData = data;
    }
    //</editor-fold>
  }

  /**
   * 解析 URI
   *
   * @param uri URI
   * @return uri 类型
   */
  @Nullable
  private ParseResult parseUri(@NonNull String uri) {
    return null;
  }
  //</editor-fold>

  //<editor-fold desc="接口">

  /**
   * 注销账户接口
   */
  public interface CancelAccountCallback {
    /**
     * 同意注销账户
     *
     * @param webView WebView
     * @param agree true : 同意；false : 不同意
     */
    void agreeCancelAccount(@NonNull WebView webView, boolean agree);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 添加账户注销接口
   *
   * @param callback 账户注销接口
   */
  public void addCancelAccountCallback(@Nullable CancelAccountCallback callback) {
    if (mCancelAccountCallbackSet == null) {
      mCancelAccountCallbackSet = new HashSet<>();
    }
    mCancelAccountCallbackSet.add(callback);
  }

  /**
   * 添加账户注销接口
   *
   * @param callback 账户注销接口
   */
  public void removeCancelAccountCallback(@Nullable CancelAccountCallback callback) {
    if (mCancelAccountCallbackSet != null && !mCancelAccountCallbackSet.isEmpty()) {
      mCancelAccountCallbackSet.remove(callback);
    }
  }

  //</editor-fold>
}
