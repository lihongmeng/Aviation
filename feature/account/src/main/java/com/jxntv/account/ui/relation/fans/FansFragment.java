package com.jxntv.account.ui.relation.fans;

import android.view.View;
import androidx.annotation.NonNull;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentFansBinding;
import com.jxntv.account.model.Author;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.event.GVideoEventBus;

/**
 * 关注界面
 *
 *
 * @since 2020-02-10 15:12
 */
@SuppressWarnings("FieldCanBeLocal")
public final class FansFragment extends BaseFragment<FragmentFansBinding> {
  //<editor-fold desc="属性">
  private FansViewModel mFansViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected boolean enableLazyLoad() {
    return true;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_fans;
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
    mFansViewModel = bingViewModel(FansViewModel.class);
    Author author = getArguments() != null ? getArguments().getParcelable("author") : null;
    mFansViewModel.setFromAuthor(author);
    mBinding.setViewModel(mFansViewModel);
    //
    mFansViewModel.getAutoRefreshLiveData().observe(this, new NotNullObserver<Boolean>() {
      @Override
      protected void onModelChanged(@NonNull Boolean autoRefresh) {
        if (autoRefresh) {
          mBinding.refreshLayout.autoRefresh();
        }
      }
    });
  }

  @Override
  protected void loadData() {
    // 监听事件
    listenEvent();
    // 检测登录状态
    mFansViewModel.checkNetworkAndLoginStatus();
  }

  @Override public void onReload(@NonNull View view) {
    mFansViewModel.checkNetworkAndLoginStatus();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 监听时间
   */
  private void listenEvent() {
    // 登录
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
        this, o -> mFansViewModel.checkNetworkAndLoginStatus()
    );
    // 登出
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observe(
        this, o -> mFansViewModel.checkNetworkAndLoginStatus()
    );
  }
  //</editor-fold>
}
