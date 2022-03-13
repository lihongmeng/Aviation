package com.jxntv.base;

import android.app.Application;
import android.view.View;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.placeholder.PlaceholderListener;
import com.jxntv.network.NetworkUtils;
import com.jxntv.network.response.APIPage;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * 刷新加载更多 ViewModel 基类
 *
 *
 * @since 2020-02-28 15:50
 */
public abstract class BaseRefreshLoadMoreViewModel extends BaseViewModel implements
    PlaceholderListener {
  //<editor-fold desc="属性">
  @Nullable
  private BaseDataBindingAdapter mDataBindingAdapter;
  @NonNull
  protected LocalPage mLocalPage = new LocalPage();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public BaseRefreshLoadMoreViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 创建 Adapter
   *
   * @return Adapter
   */
  @NonNull
  protected abstract BaseDataBindingAdapter<? extends IAdapterModel> createAdapter();
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 获取 Adapter
   *
   * @return Adapter
   */
  @NonNull
  public BaseDataBindingAdapter getAdapter() {
    if (mDataBindingAdapter == null) {
      mDataBindingAdapter = createAdapter();
      mDataBindingAdapter.setPlaceHolderListener(this);
    }
    return mDataBindingAdapter;
  }

  /**
   * 刷新
   *
   * @param view 刷新加载更多视图
   */
  public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {

  }

  /**
   * 加载更多
   *
   * @param view 刷新加载更多视图
   */
  public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
  }

  //</editor-fold>

  //<editor-fold desc="占位视图处理">
  @Override
  public void onReload(@NonNull View view) {
    // 找到父 View 是 IGVideoRefreshLoadMoreView
    ViewParent parent = view.getParent();
    while (parent != null) {
      if (parent instanceof IGVideoRefreshLoadMoreView) {
        onRefresh((IGVideoRefreshLoadMoreView) parent);
        break;
      }
      parent = parent.getParent();
    }
  }

  @Override
  public final void onLogin(@NonNull View view) {

  }
  //</editor-fold>

  //<editor-fold desc="分页">
  protected static final class LocalPage {
    //<editor-fold desc="常量">
    private static final int DEFAULT_PAGE_SIZE = 10;
    //</editor-fold>

    //<editor-fold desc="属性">
    private int mPageNumber;
    private int mPageSize;
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public LocalPage() {
      rest();
    }
    //</editor-fold>

    //<editor-fold desc="Getter">

    public int getPageNumber() {
      return mPageNumber;
    }

    public int getPageSize() {
      return mPageSize;
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public void acc() {
      mPageNumber++;
    }

    public void rest() {
      mPageNumber = 1;
      mPageSize = DEFAULT_PAGE_SIZE;
    }
    //</editor-fold>
  }
  //</editor-fold>

  //<editor-fold desc="Observer">

  /**
   * 加载更多观察者
   *
   * @param <T> 泛型，表示 List 内容的具体类型
   */
  @SuppressWarnings("WeakerAccess")
  protected class GVideoRefreshObserver<T extends IAdapterModel>
      extends GVideoResponseObserver<ListWithPage<T>> {
    //<editor-fold desc="属性">
    @NonNull
    protected IGVideoRefreshLoadMoreView mRefreshLoadMoreView;
    //</editor-fold>

    //<editor-fold desc="构造函数">

    public GVideoRefreshObserver(@NonNull IGVideoRefreshLoadMoreView refreshLoadMoreView) {
      mRefreshLoadMoreView = refreshLoadMoreView;
    }

    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected final void onSuccess(@NonNull ListWithPage<T> result) {
      // 成功
      onSuccess(result.getList());
      // 检测分页
      processPage(result.getPage());
    }

    @Override
    protected void onFailed(@NonNull Throwable throwable) {
      super.onFailed(throwable);
      if (NetworkUtils.isNetworkConnected()) {
        if (throwable instanceof TimeoutException ||
            throwable instanceof SocketTimeoutException) {
          showToast(R.string.all_network_not_available);
          getAdapter().showNetworkNotAvailablePlaceholder();
        } else {
          getAdapter().showErrorPlaceholder();
        }
      } else {
        getAdapter().showNetworkNotAvailablePlaceholder();
      }
      // 刷新完毕
      mRefreshLoadMoreView.finishGVideoRefresh();
      // 加载完毕
      mRefreshLoadMoreView.finishGVideoLoadMore();
    }
    //</editor-fold>

    //<editor-fold desc="可重写方法">

    /**
     * 获取 Adapter
     *
     * @return Adapter
     */
    @SuppressWarnings("unchecked")
    protected BaseDataBindingAdapter<T> getAdapter() {
      return BaseRefreshLoadMoreViewModel.this.getAdapter();
    }

    protected void onSuccess(@Nullable List<T> dataList) {
      filterData(dataList);
      // 添加数据
      getAdapter().replaceData(dataList);
      // 重置分页
      mLocalPage.rest();
      // 更新刷新完毕
      mRefreshLoadMoreView.finishGVideoRefresh();
    }

    /**
     * 过滤data数据，默认实现为当list不为null时，过滤为其中为空的数据
     */
    protected void filterData(@Nullable List<T> dataList) {
      if (dataList == null) {
        return;
      }
      Iterator<T> iterator = dataList.iterator();
      T t;
      while (iterator.hasNext()) {
        t = iterator.next();
        if (t == null) {
          iterator.remove();
        }
      }
    }

    protected void processPage(@NonNull APIPage page) {
      if (page.hasNextPage()) {
        // 自动累加页数
        mLocalPage.acc();
        // 设置可以加载更多
        mRefreshLoadMoreView.enableGVideoLoadMore(true);
        // 隐藏没有更多数据
        getAdapter().hideNoMoreData();
      } else {
        // 设置无法加载更多
        mRefreshLoadMoreView.enableGVideoLoadMore(false);
        // 显示没有更多数据占位
        getAdapter().showNoMoreData();
      }
    }
    //</editor-fold>
  }

  /**
   * 加载更多观察者
   *
   * @param <T> 泛型，表示 List 内容的具体类型
   */
  protected class GVideoLoadMoreObserver<T extends IAdapterModel> extends GVideoRefreshObserver<T> {
    //<editor-fold desc="构造函数">
    public GVideoLoadMoreObserver(@NonNull IGVideoRefreshLoadMoreView refreshLoadMoreView) {
      super(refreshLoadMoreView);
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">

    @Override
    protected void onSuccess(@Nullable List<T> dataList) {
      filterData(dataList);
      // 添加数据
      getAdapter().addData(dataList);
      // 更新加载完毕
      mRefreshLoadMoreView.finishGVideoLoadMore();
    }
    //</editor-fold>
  }

  /**
   * 只有刷新的观察者
   *
   * @param <T> 泛型，表示 List 内容的具体类型
   */
  protected class GVideoOnlyRefreshObserver<T extends IAdapterModel>
      extends GVideoResponseObserver<List<T>> {

    //<editor-fold desc="属性">
    @NonNull
    protected IGVideoRefreshLoadMoreView mRefreshLoadMoreView;
    //</editor-fold>

    //<editor-fold desc="构造函数">

    public GVideoOnlyRefreshObserver(@NonNull IGVideoRefreshLoadMoreView refreshLoadMoreView) {
      mRefreshLoadMoreView = refreshLoadMoreView;
    }

    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected void onSuccess(@NonNull List<T> list) {
      getAdapter().replaceData(list);
      mRefreshLoadMoreView.finishGVideoRefresh();
    }

    @Override
    protected void onFailed(@NonNull Throwable throwable) {
      super.onFailed(throwable);
      if (NetworkUtils.isNetworkConnected()) {
        if (throwable instanceof TimeoutException ||
            throwable instanceof SocketTimeoutException) {
          showToast(R.string.all_network_not_available);
          getAdapter().showNetworkNotAvailablePlaceholder();
        } else {
          getAdapter().showErrorPlaceholder();
        }
      } else {
        getAdapter().showNetworkNotAvailablePlaceholder();
      }
      mRefreshLoadMoreView.finishGVideoRefresh();
    }

    //</editor-fold>

    //<editor-fold desc="可重写方法">

    /**
     * 获取 Adapter
     *
     * @return Adapter
     */
    @SuppressWarnings("unchecked")
    protected BaseDataBindingAdapter<T> getAdapter() {
      return BaseRefreshLoadMoreViewModel.this.getAdapter();
    }
    //</editor-fold>
  }
  //</editor-fold>
}
