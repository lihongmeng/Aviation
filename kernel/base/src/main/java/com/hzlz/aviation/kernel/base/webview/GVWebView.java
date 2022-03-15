package com.hzlz.aviation.kernel.base.webview;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.library.widget.widget.GVideoWebView;

/**
 * 今视频 WebView 基类
 *
 *
 * @since 2020-02-07 10:51
 */
public final class GVWebView extends GVideoWebView {
  //<editor-fold desc="构造函数">
  private final String NET_ERROR = "net::ERR_INTERNET_DISCONNECTED";

  public GVWebView(@NonNull Context context) {
    super(context);
    init();
  }

  public GVWebView(@NonNull Context context, @NonNull AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public GVWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public GVWebView(
      @NonNull Context context,
      @Nullable AttributeSet attrs,
      int defStyleAttr,
      boolean privateBrowsing) {
    super(context, attrs, defStyleAttr, privateBrowsing);
    init();
  }
  //</editor-fold>
  //<editor-fold desc="初始化">
  @SuppressLint({ "AddJavascriptInterface", "SetJavaScriptEnabled" })
  private void init() {
    // Web 设置
    WebSettings settings = getSettings();
    if (settings != null) {
      // 启动 JavaScript
      settings.setJavaScriptEnabled(true);
      // 设置默认文本编码
      settings.setDefaultTextEncodingName("utf-8");
      // 自适应
      settings.setUseWideViewPort(true);
      settings.setLoadWithOverviewMode(true);
      // 缩放
      settings.setSupportZoom(true);
      settings.setBuiltInZoomControls(true);
      settings.setDisplayZoomControls(false);
      // 缓存
      settings.setCacheMode(WebSettings.LOAD_DEFAULT);
      // 可以访问文件
      settings.setAllowFileAccess(true);
      // 支持 Js 打开新窗口
      settings.setJavaScriptCanOpenWindowsAutomatically(true);
      // 支持自动加载图片
      settings.setLoadsImagesAutomatically(true);
      // 打开DOM
      settings.setDomStorageEnabled(true);

      getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

    }
    // 不启用滚动条
    setVerticalScrollBarEnabled(false);
    setHorizontalScrollBarEnabled(false);
    // 硬件加速
    setLayerType(View.LAYER_TYPE_NONE,null);
    // 设置 WebViewClient
    setWebViewClient(new GVWebViewClient());
    // 设置 WebChromeClient
    setWebChromeClient(new GVWebChromeClient());
    // 添加 Js 接口
    addJavascriptInterface(new GVJavaScriptInterface(), "GVJsBridge");
  }

  private final class GVWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (!URLUtil.isNetworkUrl(url)) {
        if (view != null && url != null) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
          if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
            view.getContext().startActivity(intent);
          }
        }
        return true;
      }
      return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      if (mOnPageListener!=null){
        mOnPageListener.onPageFinished(view);
      }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      super.onReceivedError(view, errorCode, description, failingUrl);
      if (TextUtils.equals(description,NET_ERROR) || errorCode == 404 || errorCode == 500){
        loadUrl("about:blank");
        if (mOnPageListener!=null){
          mOnPageListener.netError();
        }
      }
    }

  }

  private final class GVWebChromeClient extends WebChromeClient {
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
        JsPromptResult result) {
      JavaScriptCommunicationHandler.HandleResult
          handled = JavaScriptCommunicationDispatcher.getInstance().dispatch(view, message);
      if (handled != null && handled.handled) {
        result.confirm(handled.result);
        return true;
      }
      return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
      super.onProgressChanged(view, newProgress);
      if (onProgressChangedListener!=null){
        onProgressChangedListener.onProgressChanged(newProgress);
      }
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
      mFilePathCallbacks = filePathCallback;
      if (openFileChooserListener!=null) {
        Intent intent = null;
        if (fileChooserParams!=null) {
          intent = fileChooserParams.createIntent();
        }
        if (intent == null) {
          intent = new Intent(Intent.ACTION_GET_CONTENT)
                                   .addCategory(Intent.CATEGORY_OPENABLE)
                                   .setType("*/*")
                                   .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        openFileChooserListener.openFileChooser(intent);
      }
      return true;
    }
  }

  private onPageListener mOnPageListener;
  public void setPageListener(onPageListener listener){
    this.mOnPageListener = listener;
  }
  public interface onPageListener{
    void onPageFinished(WebView view);
    void netError();
  }

  private onProgressChangedListener onProgressChangedListener;
  public void setProgressChangedListener(onProgressChangedListener listener){
    this.onProgressChangedListener = listener;
  }
  public interface onProgressChangedListener{
    void onProgressChanged(int newProgress);
  }

  private onFileChooserListener openFileChooserListener;
  public void setOpenFileChooserListener(onFileChooserListener listener){
    this.openFileChooserListener = listener;
  }

  public interface onFileChooserListener{
    void openFileChooser(Intent i);
  }


  private ValueCallback<Uri[]> mFilePathCallbacks;
  /**
   * 文件选择结果处理
   */
  public void setOpenFileResult(int requestCode, int resultCode, @Nullable Intent data){
    if (mFilePathCallbacks ==null){
      return;
    }
    if (resultCode == RESULT_OK && data!=null){
        Uri uri = data.getData();
        ClipData clipData = data.getClipData();
        if (uri!=null) {
          mFilePathCallbacks.onReceiveValue(new Uri[]{uri});
        }else if (clipData!=null && clipData.getItemCount() > 0){
          Uri[] strings = new Uri[clipData.getItemCount()];
          for (int i = 0; i < clipData.getItemCount(); i ++){
            strings[i] = clipData.getItemAt(i).getUri();
          }
          mFilePathCallbacks.onReceiveValue(strings);
        }else {
          mFilePathCallbacks.onReceiveValue(null);
        }
    }else {
      mFilePathCallbacks.onReceiveValue(null);
    }
    mFilePathCallbacks = null;
  }

  //</editor-fold>

  //<editor-fold desc="JS 接口">

  /**
   * 今视频 JavaScript 接口
   */
  private final class GVJavaScriptInterface {
    /**
     * 调用原生方法
     *
     * @param uri URI
     */
    @JavascriptInterface
    public void callNativeMethod(@NonNull String uri) {
      JavaScriptCommunicationDispatcher.getInstance().dispatch(GVWebView.this, uri);
    }
  }
  //</editor-fold>
}
