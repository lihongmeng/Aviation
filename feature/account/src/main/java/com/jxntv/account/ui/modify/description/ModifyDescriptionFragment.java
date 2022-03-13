package com.jxntv.account.ui.modify.description;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentModifyDescriptionBinding;
import com.jxntv.base.BaseFragment;

/**
 * 修改个人介绍界面
 *
 *
 * @since 2020-02-05 16:24
 */
@SuppressWarnings("FieldCanBeLocal")
public final class ModifyDescriptionFragment
    extends BaseFragment<FragmentModifyDescriptionBinding> {
  //<editor-fold desc="属性">
  private ModifyDescriptionViewModel mModifyDescriptionViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_modify_description;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.fragment_modify_description_title);
    setRightOperationTextViewText(R.string.fragment_modify_description_done);
    showRightOperationTextView(true);
    openSoftKeyBoardDelay(mBinding.editTextDescription);
  }

  @Override
  protected void bindViewModels() {
    mModifyDescriptionViewModel = bingViewModel(ModifyDescriptionViewModel.class);
    mBinding.setBind(mModifyDescriptionViewModel.getDescriptionDataBinding());
    mModifyDescriptionViewModel.getEnableModifyDescriptionLiveData()
        .observe(this, new NotNullObserver<Boolean>() {
          @Override
          protected void onModelChanged(@NonNull Boolean enable) {
            enableRightOperationTextView(enable);
          }
        });
  }

  @Override
  protected void loadData() {
    // 获取参数
    String description = null;
    Bundle arguments = getArguments();
    if (arguments != null) {
      description = ModifyDescriptionFragmentArgs.fromBundle(arguments).getDescription();
    }
    mModifyDescriptionViewModel.setDescription(description);
  }

  @Override
  public void onRightOperationPressed(@NonNull View view) {
    super.onRightOperationPressed(view);
    mModifyDescriptionViewModel.modifyDescription(view);
  }

  //</editor-fold>
}
