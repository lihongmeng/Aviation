package com.jxntv.pptv.ui.more;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.base.BaseFragment;
import com.jxntv.pptv.R;
import com.jxntv.pptv.databinding.FragmentMoreBinding;
import com.jxntv.pptv.model.Category;

public final class MoreFragment extends BaseFragment<FragmentMoreBinding> {
  private MoreViewModel mMoreViewModel;
  /** 计算RecyclerView滑动距离，划出1页多展示"回到顶部"按钮 */
  private int mMediaRecyclerViewScrollY;

  @Override protected int getLayoutId() {
    return R.layout.fragment_more;
  }

  @Override protected void initView() {
    setToolbarTitle(R.string.fragment_more_title);

    mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        mMoreViewModel.handleScrollToTopVisible(recyclerView, newState, mMediaRecyclerViewScrollY);
      }

      @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        mMediaRecyclerViewScrollY += dy;
      }
    });
  }

  @Override protected void bindViewModels() {
    mMoreViewModel = bingViewModel(MoreViewModel.class);
    if (getArguments() != null) {
      Category fromCategory = MoreFragmentArgs.fromBundle(getArguments()).getCategory();
      mMoreViewModel.setCategory(fromCategory);
    }
    mBinding.setViewModel(mMoreViewModel);
    mBinding.setCategory(mMoreViewModel.getCategoryDataBinding());

    mMoreViewModel.getAutoRefreshLiveData().observe(this, refresh -> {
      if (refresh) {
        mMoreViewModel.triggerRefreshMedia(mBinding.refreshLayout);
      }
    });

    mMoreViewModel.getScrollToTopLiveData().observe(this, new Observer<Boolean>() {
      @Override public void onChanged(Boolean scrollToTop) {
        if (scrollToTop) {
          mBinding.recyclerView.scrollToPosition(0);
          mBinding.viewCategory.appbar.setExpanded(true, false);

          mMediaRecyclerViewScrollY = 0;
          mMoreViewModel.handleScrollToTopVisible(mBinding.recyclerView,
              RecyclerView.SCROLL_STATE_IDLE, mMediaRecyclerViewScrollY);
        }
      }
    });
  }

  @Override protected void loadData() {
    mBinding.refreshLayout.autoRefresh();
  }
}
