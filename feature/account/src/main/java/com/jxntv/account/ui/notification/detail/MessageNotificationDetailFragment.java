package com.jxntv.account.ui.notification.detail;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentMessageNotificationDetailBinding;
import com.jxntv.base.BaseFragment;

/**
 * 消息通知详情界面
 *
 *
 * @since 2020-03-11 16:43
 */
public final class MessageNotificationDetailFragment extends BaseFragment<FragmentMessageNotificationDetailBinding> {
  //<editor-fold desc="属性">
  private MessageNotificationDetailViewModel mMessageNotificationDetailViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_message_notification_detail;
  }

  @Override
  protected void initView() {

  }

  @Override
  protected void bindViewModels() {
    mMessageNotificationDetailViewModel = bingViewModel(MessageNotificationDetailViewModel.class);
    mBinding.setViewModel(mMessageNotificationDetailViewModel);
    //
    mMessageNotificationDetailViewModel.getTitleLiveData().observe(this, new NotNullObserver<String>() {
          @Override
          protected void onModelChanged(@NonNull String title) {
            setToolbarTitle(title);
          }
        });
  }

  @Override
  protected void loadData() {
    Bundle arguments = getArguments();
    if (arguments == null) {
      NavHostFragment.findNavController(this).popBackStack();
      return;
    }
    // 处理参数
    mMessageNotificationDetailViewModel.processArguments(
        MessageNotificationDetailFragmentArgs.fromBundle(arguments)
    );
    // 加载数据
    mBinding.refreshLayout.autoRefresh();
  }



  //</editor-fold>
}
