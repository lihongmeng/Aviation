package com.hzlz.aviation.feature.test.splashdata;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.feature.test.R;
import com.hzlz.aviation.feature.test.databinding.FragmentSplashdataBinding;

public final class SplashDataFragment extends BaseFragment<FragmentSplashdataBinding> {
  //<editor-fold desc="属性">
  private SplashDataViewModel mSplashDataViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_splashdata;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.splash_database);
    //showRightOperationTextView(true);
    //setRightOperationTextViewText("切换");
  }

  @Override
  protected void bindViewModels() {
    mSplashDataViewModel = bingViewModel(SplashDataViewModel.class);
    mBinding.setViewModel(mSplashDataViewModel);
    mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
  }

  @Override
  protected void loadData() {
    mBinding.refreshLayout.autoRefresh();
  }

  //</editor-fold>
}
