package com.hzlz.aviation.feature.account.ui.about;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;

/**
 * 关于界面 ViewModel
 *
 *
 * @since 2020-02-18 20:32
 */
public final class AboutViewModel extends BaseViewModel {
  //<editor-fold desc="属性">
  @NonNull
  public ObservableField<String> appVersion = new ObservableField<>();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public AboutViewModel(@NonNull Application application) {
    super(application);
    try {
      PackageInfo info = application.getPackageManager().getPackageInfo(
          application.getPackageName(), 0
      );
      if (info.versionName != null) {
        appVersion.set(ResourcesUtils.getString(R.string.formatted_version, info.versionName));
      }
    } catch (PackageManager.NameNotFoundException ignore) {
    }
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void showUserProtocol(@NonNull View view) {
    H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
    if (h5Plugin != null && h5Plugin.getLegalTermsUrl() != null) {
      final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
      if (webViewPlugin != null) {
        Bundle arguments = new Bundle();
        arguments.putString("url", h5Plugin.getUserAgreementUrl());
        arguments.putString("title", ResourcesUtils.getString(R.string.user_protocol));
        webViewPlugin.startWebViewFragment(view, arguments);
      }
    } else {
      showToast(R.string.all_account_check_network);
    }
  }

  public void showPrivacyPolicy(@NonNull View view) {
    H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
    if (h5Plugin != null && h5Plugin.getPrivacyPolicyUrl() != null) {
      final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
      if (webViewPlugin != null) {
        Bundle arguments = new Bundle();
        arguments.putString("url", h5Plugin.getPrivacyPolicyUrl());
        arguments.putString("title", ResourcesUtils.getString(R.string.privacy_policy));
        webViewPlugin.startWebViewFragment(view, arguments);
      }
    } else {
      showToast(R.string.all_account_check_network);
    }
  }

  public void showAppQrCode(@NonNull View view) {
    H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
    if (h5Plugin != null && h5Plugin.getAppQrCodeUrl() != null) {
      final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
      if (webViewPlugin != null) {
        Bundle arguments = new Bundle();
        arguments.putString("url", h5Plugin.getAppQrCodeUrl());
        arguments.putString("title", ResourcesUtils.getString(R.string.download_qr_code));
        webViewPlugin.startWebViewFragment(view, arguments);
      }
    } else {
      showToast(R.string.all_account_check_network);
    }
  }

  //</editor-fold>
}
