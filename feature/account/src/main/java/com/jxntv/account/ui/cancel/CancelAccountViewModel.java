package com.jxntv.account.ui.cancel;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.navigation.Navigation;
import com.jxntv.account.R;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;

/**
 * 注销账户 ViewModel
 *
 *
 * @since 2020-02-07 15:47
 */
public final class CancelAccountViewModel extends BaseViewModel {
  //<editor-fold desc="属性">
  @NonNull
  public ObservableBoolean enableConfirm = new ObservableBoolean(false);
  //
  @NonNull
  private UserRepository mUserRepository = new UserRepository();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public CancelAccountViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void readCheck(@NonNull ImageView view) {
    Object tag = view.getTag();
    if (tag == null || (tag instanceof Boolean && !(Boolean) tag)) {
      view.setTag(true);
      view.setImageResource(R.drawable.ic_cancel_account_checked);
      enableConfirm.set(true);
    } else {
      view.setTag(false);
      view.setImageResource(R.drawable.ic_cancel_account_un_checked);
      enableConfirm.set(false);
    }
  }

  public void showCancelAccountContract(@NonNull View view) {
    H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
    if (h5Plugin != null && h5Plugin.getCancellationAgreementUrl() != null) {
      final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
      if (webViewPlugin != null) {
        Bundle arguments = new Bundle();
        arguments.putString("url", h5Plugin.getCancellationAgreementUrl());
        //arguments.putString("title", ResourcesUtils.getString(
        //    R.string.all_cancel_account_contract
        //));
        webViewPlugin.startWebViewFragment(view, arguments);
      }
    } else {
      showToast(R.string.all_account_check_network);
    }
  }

  public void confirm(@NonNull View view) {
    mUserRepository.cancelAccount().subscribe(new GVideoResponseObserver<Object>() {
      @Override
      protected void onSuccess(@NonNull Object o) {
        showToast(R.string.all_account_cancel_account_successfully);
        Navigation.findNavController(view).popBackStack(R.id.accountSecurityFragment, true);
      }
    });
  }

  public void cancel(@NonNull View view) {
    Navigation.findNavController(view).popBackStack(R.id.accountSecurityFragment, true);
  }
  //</editor-fold>
}
