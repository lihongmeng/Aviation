package com.jxntv.pptv.ui.more;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.pptv.adapter.CategoryAdapter;
import com.jxntv.pptv.adapter.MoreMediaAdapter;
import com.jxntv.pptv.model.Category;
import com.jxntv.pptv.model.CategoryResponse;
import com.jxntv.pptv.model.Media;
import com.jxntv.pptv.repository.MoreMediaRepository;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MoreViewModel extends BaseRefreshLoadMoreViewModel
  implements CategoryAdapter.Listener, MoreMediaAdapter.Listener {
  /** 每页加载作品内容数，每行展示3个，这里选择3N个作为缺省加载数 */
  private static final int PAGE_SIZE = 12;
  @Nullable private Category mFromCategory;
  private Map<String, Category> mSelectedCategory = new HashMap<>();
  private MoreMediaAdapter mMoreMediaAdapter = new MoreMediaAdapter();
  private MoreMediaRepository mMoreMediaRepository = new MoreMediaRepository();
  private final CategoryDataBinding mCategoryDataBinding;
  private CheckThreadLiveData<Boolean> mAutoRefreshLiveData = new CheckThreadLiveData<>();
  private CheckThreadLiveData<Boolean> mScrollToTopLiveData = new CheckThreadLiveData<>();
  public ObservableBoolean mScrollToTopVisible = new ObservableBoolean(false);
  private int mItemHeight;

  public MoreViewModel(@NonNull Application application) {
    super(application);
    mCategoryDataBinding = new CategoryDataBinding(application, this);
    mMoreMediaAdapter.setListener(this);
  }

  void setCategory(Category fromCategory) {
    mFromCategory = fromCategory;
    if (fromCategory != null) {
      mSelectedCategory.put(fromCategory.getCategoryKey(), fromCategory);
    }
  }

  LiveData<Boolean> getAutoRefreshLiveData() {
    return mAutoRefreshLiveData;
  }

  LiveData<Boolean> getScrollToTopLiveData() {
    return mScrollToTopLiveData;
  }

  CategoryDataBinding getCategoryDataBinding() {
    return mCategoryDataBinding;
  }

  void handleScrollToTopVisible(@NonNull RecyclerView recyclerView, int newState, int scrollY) {
    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
      int height = 3 * calculateMediaItemHeight(recyclerView);
      mScrollToTopVisible.set(scrollY > height);
    }
  }

  void triggerRefreshMedia(@NonNull IGVideoRefreshLoadMoreView view) {
    mMoreMediaRepository.getMoreMediaList(mSelectedCategory, 1, PAGE_SIZE)
        .subscribe(new GVideoRefreshObserver<>(view));
  }

  @Override public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    mSelectedCategory.clear();
    setCategory(mFromCategory);

    mMoreMediaRepository.getMoreMediaList(mSelectedCategory, 1, PAGE_SIZE)
        .subscribe(new GVideoRefreshObserver<>(view));

    mMoreMediaRepository.getCategoryList(mFromCategory).subscribe(
        new GVideoResponseObserver<List<CategoryResponse>>() {
          @Override protected void onSuccess(@NonNull List<CategoryResponse> categoryResponses) {
            mCategoryDataBinding.setData(categoryResponses);
          }
        });
  }

  @Override public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
    mMoreMediaRepository.getMoreMediaList(mSelectedCategory, mLocalPage.getPageNumber(), PAGE_SIZE)
        .subscribe(new GVideoLoadMoreObserver<>(view));
  }

  @NonNull @Override protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
    return mMoreMediaAdapter;
  }

  public RecyclerView.LayoutManager getLayoutManager() {
    final GridLayoutManager glm = new GridLayoutManager(getApplication(), 3);
    return glm;
  }

  public void onScrollToTopClicked(@NonNull View v) {
    mScrollToTopLiveData.setValue(true);
    mScrollToTopVisible.set(false);
  }

  @Override public void onItemRootViewClicked(@NonNull View v, Category category) {
    if (category != null) {
      mSelectedCategory.put(category.getCategoryKey(), category);
      mAutoRefreshLiveData.setValue(true);
    }
  }

  @Override public void onItemRootViewClicked(@NonNull View v, @NonNull MoreMediaAdapter adapter,
      int itemAdapterPosition) {
    Media m = adapter.getData().get(itemAdapterPosition);
    PluginManager.get(DetailPagePlugin.class).dispatchToDetail(v.getContext(),m,null);
  }

  private int calculateMediaItemHeight(@NonNull RecyclerView recyclerView) {
    if (mItemHeight > 0) return mItemHeight;
    try {
      mItemHeight = recyclerView.getLayoutManager().getChildAt(0).getHeight();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mItemHeight;
  }
}
