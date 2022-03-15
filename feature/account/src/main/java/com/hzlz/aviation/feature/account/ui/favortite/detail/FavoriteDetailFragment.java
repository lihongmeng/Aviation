package com.hzlz.aviation.feature.account.ui.favortite.detail;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentFavoriteDetailBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.model.share.FavoriteChangeModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetItemDialog;

/**
 * 收藏详情界面
 *
 *
 * @since 2020-02-12 20:29
 */
public final class FavoriteDetailFragment extends BaseFragment<FragmentFavoriteDetailBinding>
    implements
    GVideoBottomSheetItemDialog.OnItemSelectedListener {
  //<editor-fold desc="属性">
  private FavoriteDetailViewModel mFavoriteDetailViewModel;
  @Nullable
  private Dialog mDialogCancelFavorite;
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override public String getPid() {
    return StatPid.FAVORITE;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_favorite_detail;
  }

  @Override
  protected void initView() {

  }

  @Override
  protected void bindViewModels() {
    mFavoriteDetailViewModel = bingViewModel(FavoriteDetailViewModel.class);
    // title
    mFavoriteDetailViewModel.getTitleLiveData().observe(this, new NotNullObserver<String>() {
      @Override
      protected void onModelChanged(@NonNull String title) {
        setToolbarTitle(title);
      }
    });
    // padding
    mFavoriteDetailViewModel.getPaddingLiveData().observe(this, new NotNullObserver<Integer>() {
      @Override
      protected void onModelChanged(@NonNull Integer padding) {
        mBinding.recyclerView.setPadding(padding, 0, padding, 0);
      }
    });
    // layoutManager
    mFavoriteDetailViewModel.getLayoutManagerLiveData()
        .observe(this, new NotNullObserver<RecyclerView.LayoutManager>() {
          @Override
          protected void onModelChanged(@NonNull RecyclerView.LayoutManager layoutManager) {
            mBinding.recyclerView.setLayoutManager(layoutManager);
          }
        });
    // itemDecoration
    mFavoriteDetailViewModel.getCancelFavoriteDialogLiveData()
        .observe(this, new NotNullObserver<Boolean>() {
          @Override
          protected void onModelChanged(@NonNull Boolean show) {
            if (show) {
              showCancelFavoriteDialog();
            }
          }
        });
    mFavoriteDetailViewModel.getReloadLiveData()
        .observe(this, new NotNullObserver<Boolean>() {
          @Override protected void onModelChanged(@NonNull Boolean reload) {
            if (reload) {
              onReload(mBinding.refreshLayout);
            }
          }
        });
  }

  @Override
  protected void loadData() {
    listenEvent();
    // 处理参数
    Bundle argument = getArguments();
    if (argument != null) {
      mFavoriteDetailViewModel.processArgs(
          requireContext(), FavoriteDetailFragmentArgs.fromBundle(argument)
      );
    }
    // 设置 ViewModel
    mBinding.setViewModel(mFavoriteDetailViewModel);
    // 加载数据
    mBinding.refreshLayout.autoRefresh();
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void showCancelFavoriteDialog() {
    if (mDialogCancelFavorite == null) {
      mDialogCancelFavorite = new GVideoBottomSheetItemDialog.Builder(requireContext())
          .addItem(R.string.cancel_favorite)
          .cancel(R.string.dialog_back)
          .itemSelectedListener(this)
          .build();
    }
    if (!mDialogCancelFavorite.isShowing()) {
      mDialogCancelFavorite.show();
    }
  }
  private void listenEvent() {
    //收藏状态变化
    GVideoEventBus.get(SharePlugin.EVENT_FAVORITE_CHANGE, FavoriteChangeModel.class)
        .observe(this, favoriteChangeModel -> {
          //if (!isVisible()) return;
          mFavoriteDetailViewModel.checkFavoriteStatus(favoriteChangeModel);
        });
  }
  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  public void onItemSelected(@NonNull GVideoBottomSheetItemDialog dialog, int position) {
    if (dialog.equals(mDialogCancelFavorite)) {
      if (position == 0) {
        mFavoriteDetailViewModel.cancelFavorite();
      }
    }
  }
  //</editor-fold>
}
