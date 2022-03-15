package com.hzlz.aviation.feature.account.ui.setting;

import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentSettingBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetItemDialog;

/**
 * 设置界面
 *
 *
 * @since 2020-02-19 14:17
 */
public final class SettingFragment extends BaseFragment<FragmentSettingBinding>
    implements GVideoBottomSheetItemDialog.OnItemSelectedListener {
  //<editor-fold desc="属性">
  private SettingViewModel mSettingViewModel;
  @Nullable
  private Dialog mDialogAutoPlay;
  @Nullable
  private Dialog mDialogAutoPlayNextContent;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getLayoutId() {
    return R.layout.fragment_setting;
  }

  @Override
  protected void initView() {
    setToolbarTitle(R.string.general_setting);
  }

  @Override
  protected void bindViewModels() {
    mSettingViewModel = bingViewModel(SettingViewModel.class);
    //
    mBinding.setViewModel(mSettingViewModel);
    //
    getLifecycle().addObserver(mSettingViewModel);
    //
    mSettingViewModel.getAutoPlayLiveData()
        .observe(this, new NotNullObserver<Boolean>() {
          @Override
          protected void onModelChanged(@NonNull Boolean show) {
            if (show) {
              showAutoPlayDialog();
            }
          }
        });
    mSettingViewModel.getAutoPlayNextContentLiveData()
        .observe(this, new NotNullObserver<Boolean>() {
          @Override
          protected void onModelChanged(@NonNull Boolean show) {
            if (show) {
              showAutoPlayNextContentDialog();
            }
          }
        });
  }

  @Override
  protected void loadData() {
    mSettingViewModel.init();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  private void showAutoPlayDialog() {
    if (mDialogAutoPlay == null) {
      mDialogAutoPlay = new GVideoBottomSheetItemDialog.Builder(requireContext())
          .addItem(R.string.auto_play_in_any_network)
          .addItem(R.string.auto_play_only_in_wifi)
          .addItem(R.string.not_auto_play)
          .cancel(R.string.dialog_back)
          .itemSelectedListener(this)
          .build();
    }
    if (!mDialogAutoPlay.isShowing()) {
      mDialogAutoPlay.show();
    }
  }

  private void showAutoPlayNextContentDialog() {
    if (mDialogAutoPlayNextContent == null) {
      mDialogAutoPlayNextContent = new GVideoBottomSheetItemDialog.Builder(requireContext())
          .addItem(R.string.auto_play_next_content_in_any_network)
          .addItem(R.string.auto_play_next_content_only_in_wifi)
          .addItem(R.string.not_auto_play_next_content)
          .cancel(R.string.dialog_back)
          .itemSelectedListener(this)
          .build();
    }
    if (!mDialogAutoPlayNextContent.isShowing()) {
      mDialogAutoPlayNextContent.show();
    }
  }

  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onItemSelected(@NonNull GVideoBottomSheetItemDialog dialog, int position) {
    if (dialog.equals(mDialogAutoPlay)) {
      switch (position) {
        case 0:
          mSettingViewModel.autoPlay();
          break;
        case 1:
          mSettingViewModel.autoPlayOnlyInWifi();
          break;
        case 2:
          mSettingViewModel.notAutoPlay();
          break;
        default:
          break;
      }
      return;
    }
    if (dialog.equals(mDialogAutoPlayNextContent)) {
      switch (position) {
        case 0:
          mSettingViewModel.autoPlayNextContent();
          break;
        case 1:
          mSettingViewModel.autoPlayNextContentOnlyInWifi();
          break;
        case 2:
          mSettingViewModel.notAutoPlayNextContent();
          break;
        default:
          break;
      }
    }
  }
  //</editor-fold>

  @Override
  public String getPid() {
    return StatPid.WHOLE_PERIOD_DETAIL;
  }

}
