package com.hzlz.aviation.feature.account.ui.moment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.repository.MomentRepository;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.Observable;

/**
 * 动态界面 ViewModel
 *
 *
 * @since 2020-02-10 15:27
 *
 */
public final class MomentViewModel extends MediaPageViewModel {
  //<editor-fold desc="属性">
  @NonNull
  private CheckThreadLiveData<Boolean> mAutoRefreshLiveData = new CheckThreadLiveData<>();
  //
  @NonNull
  private MomentRepository mMomentRepository = new MomentRepository();
  //</editor-fold>

  //<editor-fold desc="构造函数">

  public MomentViewModel(@NonNull Application application) {
    super(application);
  }

  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull
  LiveData<Boolean> getAutoRefreshLiveData() {
    return mAutoRefreshLiveData;
  }

  void checkNetworkAndLoginStatus() {
    if (!NetworkUtils.isNetworkConnected()) {
      updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
      mMediaModelList.clear();
      mAdapter.refreshData(mMediaModelList);
    } else if (!UserManager.hasLoggedIn()) {
      updatePlaceholderLayoutType(PlaceholderType.UN_LOGIN);
      mMediaModelList.clear();
      mAdapter.refreshData(mMediaModelList);
    } else {
      updatePlaceholderLayoutType(PlaceholderType.NONE);
      mAutoRefreshLiveData.setValue(true);
    }
  }

  /** 短视频详情页中加载更多逻辑 */
  Observable<ShortVideoListModel> loadMoreShortData() {
    return null;
  }

  //</editor-fold>

  //<editor-fold desc="方法实现">

  public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    loadRefreshData();
  }

  public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
    loadMoreData();
  }

  @Override
  protected IRecyclerModel<MediaModel> createModel() {

    return new IRecyclerModel<MediaModel>() {
      @Override
      public void loadData(int page, RecyclerViewLoadListener<MediaModel> loadListener) {
        mMomentRepository.getMomentList(page, DEFAULT_PAGE_COUNT)
            .subscribe(new GVideoResponseObserver<ListWithPage<Media>>() {
              @Override protected void onRequestStart() {
                super.onRequestStart();
                if (mMediaModelList.isEmpty()) {
                  updatePlaceholderLayoutType(PlaceholderType.LOADING);
                }
              }

              @Override protected void onSuccess(@NonNull ListWithPage<Media> mediaListWithPage) {
                List<Media> list = mediaListWithPage.getList();
                List<MediaModel> modelList = new ArrayList<>();
                for (Media media : list) {
                  if (media.isValid()) {
                    MediaModel model = new MediaModel(media);
                    model.tabId = mTabId;
                    model.setPid(getPid());
                    //model.playState = 0;
                    //model.viewPosition = 0;
                    model.correspondVideoModelAddress = media.getMemoryAddress();
                    modelList.add(model);
                  }
                }
                loadSuccess(modelList);

                if (!mediaListWithPage.getPage().hasNextPage()) {
                  if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                      showToast(R.string.all_nor_more_data);
                  }
                }
                loadComplete();
              }

              @Override public void onFailed(Throwable throwable) {
                if (throwable instanceof TimeoutException ||
                    throwable instanceof SocketTimeoutException) {
                  showToast(R.string.all_network_not_available);
                  loadComplete();
                  return;
                }
                showToast(R.string.all_nor_more_data);
                loadComplete();
              }
            });
      }
    };
  }
  //</editor-fold>

}
