package com.hzlz.aviation.feature.account.ui.country;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentCountryCodeBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

/**
 * 国家代码界面
 *
 *
 * @since 2020-01-14 10:07
 */
@SuppressWarnings("FieldCanBeLocal")
public final class CountryCodeFragment extends BaseFragment<FragmentCountryCodeBinding> {
  //<editor-fold desc="属性">
  private CountryCodeViewModel mCountryCodeViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_country_code;
  }

  @Override
  protected void initView() {
  }

  @Override
  protected void bindViewModels() {
    mCountryCodeViewModel = bingViewModel(CountryCodeViewModel.class);
  }

  @Override
  protected void loadData() {

  }

  @Override
  public String getPid() {
    return StatPid.COUNTRY_CODE;
  }

  //</editor-fold>
}
