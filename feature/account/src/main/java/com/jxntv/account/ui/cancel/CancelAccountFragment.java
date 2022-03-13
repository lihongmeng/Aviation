package com.jxntv.account.ui.cancel;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentCancelAccountBinding;
import com.jxntv.base.BaseFragment;
import com.jxntv.stat.StatPid;

/**
 * 注销账户界面
 *
 *
 * @since 2020-02-07 11:50
 */
@SuppressWarnings("FieldCanBeLocal")
public final class CancelAccountFragment extends BaseFragment<FragmentCancelAccountBinding> {
  //<editor-fold desc="属性">
  private CancelAccountViewModel mCancelAccountViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_cancel_account;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.cancel_account);
  }

  @Override
  protected void bindViewModels() {
    mCancelAccountViewModel = bingViewModel(CancelAccountViewModel.class);
    mBinding.setViewModel(mCancelAccountViewModel);
  }

  @Override
  protected void loadData() {

  }

  @Override
  public String getPid() {
    return StatPid.CANCEL_ACCOUNT;
  }

  //</editor-fold>
}
