package com.jxntv.account.ui.composition;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentCompositionBinding;
import com.jxntv.account.model.Author;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.stat.StatPid;

/**
 * 作品 页面
 *
 */
public final class CompositionFragment extends BaseFragment<FragmentCompositionBinding> {
  //<editor-fold desc="属性">
  private CompositionViewModel mCompositionViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override
  protected boolean enableLazyLoad() {
    return true;
  }

  @Override public String getPid() {
    return StatPid.COMPOSITION;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_composition;
  }

  @Override protected void initView() {
    mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
  }

  @Override protected void loadData() {
    mCompositionViewModel.loadData(mBinding.refreshLayout);
    listenEvent();
  }

  /**
   * 监听刷新事件
   */
  private void listenEvent() {
    GVideoEventBus.get(AccountPlugin.EVENT_REFRESH_DATA).observe(this, new Observer<Object>() {
      @Override public void onChanged(Object o) {
        mCompositionViewModel.loadData(mBinding.refreshLayout);
      }
    });
  }

  @Override
  protected void bindViewModels() {
    Author author = getArguments() != null ? getArguments().getParcelable("author") : null;
    mCompositionViewModel = bingViewModel(CompositionViewModel.class);
    mCompositionViewModel.setFromAuthor(author);

    mBinding.setViewModel(mCompositionViewModel);
  }

  @Override public void onReload(@NonNull View view) {
    if (mCompositionViewModel != null) {
      mCompositionViewModel.onRefresh(mBinding.refreshLayout);
    }
  }

  //</editor-fold>

}
