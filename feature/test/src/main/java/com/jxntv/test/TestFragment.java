package com.jxntv.test;

import com.jxntv.base.BaseFragment;
import com.jxntv.test.databinding.FragmentTestBinding;

/**
 * Test 界面
 *
 */
public final class TestFragment extends BaseFragment<FragmentTestBinding> {
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TAG = TestFragment.class.getSimpleName();
  private TestViewModel mTestViewModel;
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_test;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.test_title);
  }

  @Override
  protected void bindViewModels() {
    mTestViewModel = bingViewModel(TestViewModel.class);
    mBinding.setViewModel(mTestViewModel);
  }

  @Override
  protected void loadData() {
  }

}
