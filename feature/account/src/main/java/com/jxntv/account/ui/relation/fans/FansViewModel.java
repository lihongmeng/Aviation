package com.jxntv.account.ui.relation.fans;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.jxntv.account.adapter.FollowAdapter;
import com.jxntv.account.model.Author;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.repository.InteractionRepository;
import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.NetworkUtils;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

/**
 *
 * @since 2020-02-10 21:24
 */
public final class FansViewModel extends BaseRefreshLoadMoreViewModel
    implements FollowAdapter.WidgetListener {
  //<editor-fold desc="属性">
  private Author mFromAuthor;
  @NonNull
  private CheckThreadLiveData<Boolean> mAutoRefreshLiveData = new CheckThreadLiveData<>();
  @NonNull
  private InteractionRepository mInteractionRepository = new InteractionRepository();
  @NonNull
  private FollowAdapter mAdapter = new FollowAdapter();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public FansViewModel(@NonNull Application application) {
    super(application);
    mAdapter.setWidgetListener(this);
  }
  //</editor-fold>

  //<editor-fold desc="API">
  void setFromAuthor(Author author) {
    mFromAuthor = author;
  }
  @NonNull
  LiveData<Boolean> getAutoRefreshLiveData() {
    return mAutoRefreshLiveData;
  }

  void checkNetworkAndLoginStatus() {
    if (!NetworkUtils.isNetworkConnected()) {
      mAdapter.replaceData(null);
      mAdapter.hidePlaceholder();
      updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
    } else if (!UserManager.hasLoggedIn()) {
      mAdapter.replaceData(null);
      mAdapter.hidePlaceholder();
      updatePlaceholderLayoutType(PlaceholderType.UN_LOGIN);
    } else {
      updatePlaceholderLayoutType(PlaceholderType.NONE);
      mAutoRefreshLiveData.setValue(true);
    }
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
    return mAdapter;
  }

  @Override
  public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onRefresh(view);
    if (mAdapter.getData().isEmpty()) {
      mAdapter.showLoadingPlaceholder();
    }
    mInteractionRepository
        .getFansList(mFromAuthor, 1, mLocalPage.getPageSize())
        .subscribe(new GVideoRefreshObserver<>(view));
  }

  @Override
  public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onLoadMore(view);
    mInteractionRepository
        .getFansList(mFromAuthor, mLocalPage.getPageNumber(), mLocalPage.getPageSize())
        .subscribe(new GVideoLoadMoreObserver<>(view));
  }
  //</editor-fold>

  //<editor-fold desc="事件监听">
  @Override
  public void onItemClick(@NonNull View view, @NonNull FollowAdapter adapter, int position) {
    AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
    if (plugin != null) {
      Author author = adapter.getData().get(position);
      plugin.startPgcActivity(view, author);
    }
  }

  @Override
  public void follow(@NonNull View view, @NonNull FollowAdapter adapter, int position) {
    final Author author = adapter.getData().get(position);
    if (author.getId() != null) {
      if (!NetworkTipUtils.checkNetworkOrTip(view.getContext())) {
        return;
      }
      mInteractionRepository.followAuthor(author.getId(), author.getType(),author.getName(), !author.isFollow())
          .subscribe(new GVideoResponseObserver<Boolean>() {
            @Override
            protected void onSuccess(@NonNull Boolean followed) {
              author.setFollow(followed);
            }

            @Override
            public void onError(Throwable throwable) {
              super.onError(throwable);
              GVideoSensorDataManager.getInstance().followAccount(!author.isFollow(),author.getId(),
                      author.getName(),author.getType(),throwable.getMessage());
            }
          });
    }
  }
  //</editor-fold>
}