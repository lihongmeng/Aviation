package com.hzlz.aviation.feature.account.ui.modify.nickname;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.repository.UserRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.utils.StringUtils;

/**
 * 修改昵称 ViewModel
 *
 *
 * @since 2020-01-21 16:53
 */
public final class ModifyNicknameViewModel extends BaseViewModel {
  //<editor-fold desc="属性">
  @NonNull
  public ModifyNicknameDataBinding dataBinding = new ModifyNicknameDataBinding();

  @NonNull
  private UserRepository mUserRepository = new UserRepository();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public ModifyNicknameViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 设置昵称
   *
   * @param nickname 昵称
   */
  void setNickname(@Nullable String nickname) {
    dataBinding.setNickname(nickname);
  }

  @NonNull
  LiveData<Boolean> getModifyNicknameLivaData() {
    return dataBinding.getModifyNicknameLiveData();
  }

  @SuppressWarnings("ConstantConditions")
  void modifyNickname(@NonNull View view) {
    String nickname = dataBinding.nickname.get();
    nickname = StringUtils.filterWhiteSpace(nickname);
    mUserRepository.modifyUserNickname(nickname)
        .subscribe(new GVideoResponseObserver<User>() {
          @Override
          protected void onSuccess(@NonNull User user) {
            Navigation.findNavController(view).popBackStack();
          }
        });
  }
  //</editor-fold>
}
