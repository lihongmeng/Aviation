package com.hzlz.aviation.feature.account.ui.modify.description;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.repository.UserRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.utils.StringUtils;

/**
 * 修改个人介绍 ViewModel
 *
 *
 * @since 2020-02-05 16:26
 */
public final class ModifyDescriptionViewModel extends BaseViewModel {
  //<editor-fold desc="属性">
  @NonNull
  private ModifyDescriptionDataBinding mDescriptionDataBinding = new ModifyDescriptionDataBinding();
  @NonNull
  private UserRepository mUserRepository = new UserRepository();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public ModifyDescriptionViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull
  ModifyDescriptionDataBinding getDescriptionDataBinding() {
    return mDescriptionDataBinding;
  }

  @NonNull
  LiveData<Boolean> getEnableModifyDescriptionLiveData() {
    return mDescriptionDataBinding.getEnableModifyDescriptionLiveData();
  }

  void setDescription(@Nullable String description) {
    mDescriptionDataBinding.setDescription(description);
  }

  void modifyDescription(@NonNull View view) {
    String desp = mDescriptionDataBinding.description.get();
    desp = StringUtils.filterWhiteSpace(desp);
    mUserRepository.modifyUserDescription(desp)
        .subscribe(new GVideoResponseObserver<User>() {
          @Override protected boolean isShowToast() {
            return false;
          }

          @Override
          protected void onSuccess(@NonNull User user) {
            Navigation.findNavController(view).popBackStack();
          }
          @Override protected void onFailed(@NonNull Throwable throwable) {
            showToast(R.string.all_network_not_available_action_tip);
            Navigation.findNavController(view).popBackStack();
          }
        });
  }
  //</editor-fold>
}
