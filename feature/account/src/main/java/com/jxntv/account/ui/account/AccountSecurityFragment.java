package com.jxntv.account.ui.account;

import static com.jxntv.base.Constant.SP_LOGIN_HAS_VERIFICATE;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentAccountSecurityBinding;
import com.jxntv.account.model.User;
import com.jxntv.base.BackPressHandler;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.sharedprefs.KernelSharedPrefs;
import com.jxntv.dialog.GVideoBottomSheetItemDialog;
import com.jxntv.stat.StatPid;

/**
 * 账号与安全界面
 *
 *
 * @since 2020-01-19 17:07
 */
@SuppressWarnings("FieldCanBeLocal")
public final class AccountSecurityFragment extends BaseFragment<FragmentAccountSecurityBinding>
    implements GVideoBottomSheetItemDialog.OnItemSelectedListener {
  //<editor-fold desc="属性">
  private AccountSecurityViewModel mAccountSecurityViewModel;
  //
  private GVideoBottomSheetItemDialog mLogoutDialog;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_account_security;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.fragment_profile_drawer_account_and_security);
  }

  @Override
  public String getPid() {
    return StatPid.ACCOUNT_SECURITY;
  }

  @Override
  protected void bindViewModels() {
    mAccountSecurityViewModel = bingViewModel(AccountSecurityViewModel.class);
    mBinding.setViewModel(mAccountSecurityViewModel);
    mAccountSecurityViewModel.getUserLiveData().observe(this, new NotNullObserver<User>() {
      @Override
      protected void onModelChanged(@NonNull User user) {
        mBinding.setUser(user.getUserObservable());
      }
    });
    mAccountSecurityViewModel.getLogoutDialogLiveData()
        .observe(this, new NotNullObserver<Boolean>() {
          @Override
          protected void onModelChanged(@NonNull Boolean show) {
            if (show) {
              showLogoutDialog();
            }
          }
        });
  }

  @Override
  protected void loadData() {
    mAccountSecurityViewModel.loadData();
  }

  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void showLogoutDialog() {
    if (mLogoutDialog == null) {
      mLogoutDialog = new GVideoBottomSheetItemDialog.Builder(requireContext())
          .addItem(R.string.all_account_switch_account)
          .addItem(R.string.all_account_logout)
          .cancel(R.string.dialog_back)
          .itemSelectedListener(this)
          .build();
    }
    if (!mLogoutDialog.isShowing()) {
      mLogoutDialog.show();
    }
  }
  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onItemSelected(@NonNull GVideoBottomSheetItemDialog dialog, int position) {
    switch (position) {
      case 0:
        KernelSharedPrefs.getInstance().putBoolean(SP_LOGIN_HAS_VERIFICATE, true);
        mAccountSecurityViewModel.switchAccount(this);
        break;
      case 1:
        mAccountSecurityViewModel.logout(this);
        break;
      default:
        break;
    }
  }
  //</editor-fold>
}
