package com.hzlz.aviation.feature.account.ui.relation;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentRelationBinding;
import com.hzlz.aviation.feature.account.ui.relation.fans.FansFragment;
import com.hzlz.aviation.feature.account.ui.relation.follow.FollowFragment;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * UGC 好友关系界面
 *
 */
@SuppressWarnings("FieldCanBeLocal")
public final class RelationFragment extends BaseFragment<FragmentRelationBinding> {
  //<editor-fold desc="属性">
  private RelationViewModel mRelationViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected boolean enableLazyLoad() {
    return false;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_relation;
  }

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override
  protected void initView() {
    FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
        getChildFragmentManager(), FragmentPagerItems.with(requireContext())
        .add(R.string.follow, FollowFragment.class, getArguments())
        .add(R.string.fans, FansFragment.class, getArguments())
        .create()
    );
    mBinding.viewPager.setAdapter(adapter);
    mBinding.tabLayout.setViewPager(mBinding.viewPager);

    if (getArguments() != null) {
      RelationFragmentArgs args = RelationFragmentArgs.fromBundle(getArguments());
      int start = args.getStart();
      mBinding.viewPager.setCurrentItem(start);
    }
    if (isFullTransparent()){
      ImmersiveUtils.setStatusBarIconColor(this,true);
      mBinding.rootLayout.setPadding(0, WidgetUtils.getStatusBarHeight(),0,0);
    }
  }

  @Override
  protected void bindViewModels() {
    mRelationViewModel = bingViewModel(RelationViewModel.class);
    mBinding.setViewModel(mRelationViewModel);
  }

  @Override
  protected void loadData() {
  }
  //</editor-fold>
}
