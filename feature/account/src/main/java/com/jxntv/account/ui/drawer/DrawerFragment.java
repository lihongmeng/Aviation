package com.jxntv.account.ui.drawer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentDrawerBinding;
import com.jxntv.account.model.User;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.base.plugin.TestPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.event.entity.DrawerLayoutData;
import com.jxntv.ioc.PluginManager;

/**
 * 抽屉界面
 *
 *
 * @since 2020-01-17 14:48
 */
@SuppressWarnings("FieldCanBeLocal")
public final class DrawerFragment extends BaseFragment<FragmentDrawerBinding> {
  //<editor-fold desc="属性">
  private DrawerViewModel mDrawerViewModel;
  @Nullable
  private ObjectAnimator mCacheClearAnimator;
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_drawer;
  }

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override
  protected void initView() {
  }

  @Override
  protected void bindViewModels() {
    // 环境切换相关 Destination
    PluginManager.get(TestPlugin.class).addDestination(this);

    mDrawerViewModel = bingViewModel(DrawerViewModel.class);
    mBinding.setViewModel(mDrawerViewModel);
    //
    mDrawerViewModel.getUserLiveData().observe(this, new NotNullObserver<User>() {
      @Override
      protected void onModelChanged(@NonNull User user) {
        mBinding.setUser(user.getUserObservable());
      }
    });
    mDrawerViewModel.getCloseDrawerLiveData().observe(this, new NotNullObserver<Boolean>() {
      @Override
      protected void onModelChanged(@NonNull Boolean close) {
        if (close) {
          closeDrawer();
        }
      }
    });
    mDrawerViewModel.getShowCacheLoadingLiveData().observe(this, new NotNullObserver<Boolean>() {
      @Override
      protected void onModelChanged(@NonNull Boolean show) {
        showCacheLoading(show);
      }
    });

    GVideoEventBus.get(AccountPlugin.EVENT_UPDATE_UNREAD_MESSAGE_COUNT).observe(this, o -> {
      if (mDrawerViewModel.hasUnreadMessageNotification.get()) {
        mDrawerViewModel.checkUnreadMessageCount();
      }
    });
  }

  @Override
  protected void loadData() {
    listenEvent();
    mDrawerViewModel.loadData(getActivity());
  }
  //</editor-fold>

  //<editor-fold desc="生命周期">

  @Override
  public void onDestroyView() {
    if (mCacheClearAnimator != null) {
      mCacheClearAnimator.cancel();
    }
    super.onDestroyView();
  }

  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void closeDrawer() {
    GVideoEventBus.get(HomePlugin.EVENT_HOME_DRAWER)
            .post(new DrawerLayoutData(false,getPageName()));
  }

  private void showCacheLoading(boolean show) {
    mBinding.cellViewClearCache.showRightText(!show);
    mBinding.cellViewClearCache.showRightIcon(show);
    // 启动/停止动画
    ImageView rightIcon = mBinding.cellViewClearCache.getRightIconImageView();
    if (rightIcon != null) {
      if (mCacheClearAnimator != null) {
        mCacheClearAnimator.cancel();
      }
      if (show) {
        mCacheClearAnimator = ObjectAnimator.ofFloat(rightIcon, "rotation", 0, 360);
        mCacheClearAnimator.setDuration(1200L);
        mCacheClearAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mCacheClearAnimator.setRepeatMode(ValueAnimator.RESTART);
        mCacheClearAnimator.start();
      }
    }
  }

  /**
   * 监听事件
   */
  private void listenEvent() {
    // 登录
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
        this, o -> mDrawerViewModel.onLogin()
    );
    // 登出
    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observe(
        this, o -> mDrawerViewModel.onLogout()
    );
    // 账户冻结
    GVideoEventBus.get(AccountPlugin.EVENT_ACCOUNT_FREEZE).observe(
        this, o -> mDrawerViewModel.onAccountFreeze()
    );
    // 消息通知小红点
    GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class)
        .observe(this, count -> {
          mDrawerViewModel.updateHadMessageNotification(count>0);
        });
  }
  //</editor-fold>
}
