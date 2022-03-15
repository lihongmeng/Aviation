package com.hzlz.aviation.feature.account.ui.relation.follow;

import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentFollowBinding;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.kernel.base.BackPressHandler;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.share.FollowChangeModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.util.SoftInputUtils;

/**
 * 关注界面
 *
 *
 * @since 2020-02-10 15:12
 */
@SuppressWarnings("FieldCanBeLocal")
public final class FollowFragment extends BaseFragment<FragmentFollowBinding> {
  //<editor-fold desc="属性">
  private FollowViewModel mFollowViewModel;
  private BackPressHandler mBackPressHandler = () -> hideKeyboard();
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override public void onResume() {
    super.onResume();
    ((BaseActivity)requireActivity()).registerBackPressHandler(mBackPressHandler);
  }

  @Override public void onPause() {
    super.onPause();
    ((BaseActivity)requireActivity()).unregisterBackPressHandler(mBackPressHandler);
  }

  @Override
  protected boolean enableLazyLoad() {
    return true;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_follow;
  }

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override
  protected void initView() {
    setupPlaceholderLayout(R.id.fragment_container);
  }

  @Override
  protected void bindViewModels() {
    mFollowViewModel = bingViewModel(FollowViewModel.class);
    Author author = getArguments() != null ? getArguments().getParcelable("author") : null;
    mFollowViewModel.setFromAuthor(author);
    mBinding.setViewModel(mFollowViewModel);
    //
    mFollowViewModel.getAutoRefreshLiveData().observe(this, new NotNullObserver<Boolean>() {
      @Override
      protected void onModelChanged(@NonNull Boolean autoRefresh) {
        if (autoRefresh) {
          mBinding.refreshLayout.autoRefresh();
          hideKeyboard();
        }
      }
    });
  }

  @Override
  protected void loadData() {
    // 监听事件
    listenEvent();
    // 检测登录状态
    mFollowViewModel.checkNetworkAndLoginStatus();
  }

  @Override public void onReload(@NonNull View view) {
    mFollowViewModel.checkNetworkAndLoginStatus();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 监听时间
   */
  private void listenEvent() {
    // 登录
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
        this, o -> mFollowViewModel.checkNetworkAndLoginStatus()
    );
    // 登出
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observe(
        this, o -> mFollowViewModel.checkNetworkAndLoginStatus()
    );
    //关注状态变化
    //可能从粉丝列表新关注了用户，需要刷新关注列表
    GVideoEventBus.get(SharePlugin.EVENT_FOLLOW_CHANGE, FollowChangeModel.class)
        .observe(this, followChangeModel -> {
          //if (!isVisible()) return;
          mFollowViewModel.checkFollowStatus(followChangeModel);
        });
  }

  private boolean hideKeyboard() {
    if (mBinding.editTextSearch.isFocused()) {
      if (getContext() != null) {
        SoftInputUtils.hideSoftInput(mBinding.editTextSearch.getWindowToken(), getContext());
      }

      mBinding.editTextSearch.clearFocus();
      mBinding.refreshLayout.requestFocus();

      return true;
    }
    return false;
  }


  //</editor-fold>
}
