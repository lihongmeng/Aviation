package com.hzlz.aviation.feature.account.ui.favortite.detail;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.Utils;
import com.hzlz.aviation.feature.account.adapter.AbstractMediaAdapter;
import com.hzlz.aviation.feature.account.adapter.OneColumnMediaAdapter;
import com.hzlz.aviation.feature.account.adapter.TwoColumnsMediaAdapter;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.feature.account.repository.FavoriteRepository;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.share.FavoriteChangeModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.sensordata.utils.InteractType;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.ArrayList;

/**
 * 收藏详情界面
 *
 *
 * @since 2020-02-12 20:34
 */
public final class FavoriteDetailViewModel extends BaseRefreshLoadMoreViewModel
    implements AbstractMediaAdapter.Listener {
  //<editor-fold desc="属性">
  @NonNull
  private CheckThreadLiveData<String> mTitleLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Integer> mPaddingLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<RecyclerView.LayoutManager> mLayoutManagerLiveData =
      new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Boolean> mCancelFavoriteDialogLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Boolean> mReloadLiveData = new CheckThreadLiveData<>();
  //
  @Nullable
  private AbstractMediaAdapter mMediaAdapter;
  @NonNull
  private FavoriteRepository mFavoriteRepository = new FavoriteRepository();
  //
  private Author mFromAuthor;
  @Nullable
  private String mFavoriteId;
  @Nullable
  private ArrayList<Media> mShotMediaList;
  private int mPendingCancelMediaPosition = -1;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public FavoriteDetailViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull
  LiveData<String> getTitleLiveData() {
    return mTitleLiveData;
  }

  @NonNull
  LiveData<Integer> getPaddingLiveData() {
    return mPaddingLiveData;
  }

  @NonNull
  LiveData<RecyclerView.LayoutManager> getLayoutManagerLiveData() {
    return mLayoutManagerLiveData;
  }

  @NonNull
  LiveData<Boolean> getCancelFavoriteDialogLiveData() {
    return mCancelFavoriteDialogLiveData;
  }

  @NonNull
  LiveData<Boolean> getReloadLiveData() {
    return mReloadLiveData;
  }


  void processArgs(@NonNull Context context, @NonNull FavoriteDetailFragmentArgs args) {
    // 处理参数
    mTitleLiveData.setValue(args.getTitle());
    mFavoriteId = args.getFavoriteId();
    mFromAuthor = args.getAuthor();
    switch (args.getDisplayType()) {
      // 双列
      case 1: {
        mMediaAdapter = new TwoColumnsMediaAdapter();
        ((TwoColumnsMediaAdapter) mMediaAdapter).setFromAuthor(mFromAuthor);

        mPaddingLiveData.setValue(
            (int) ResourcesUtils.getDimens(R.dimen.padding_fragment_favorite_detail_for_grid_layout)
        );
        mLayoutManagerLiveData.setValue(new GridLayoutManager(context, 2));
      }
      break;
      // 单列
      case 0:
      default: {
        mMediaAdapter = new OneColumnMediaAdapter();
        ((OneColumnMediaAdapter) mMediaAdapter).setFromAuthor(mFromAuthor);
        mLayoutManagerLiveData.setValue(new LinearLayoutManager(context));
      }
      break;
    }
    mMediaAdapter.setListener(this);
  }

  void checkFavoriteStatus(FavoriteChangeModel favoriteChangeModel) {
    if (mMediaAdapter == null) return;
    boolean changed = false;
    for(Media media : mMediaAdapter.getData()) {
      if (media != null && TextUtils.equals(media.getId(), favoriteChangeModel.mediaId)) {
        changed = true;
        break;
      }
    }
    //取消收藏其中某个视频
    if (changed && !favoriteChangeModel.favorite) {
      mMediaAdapter.removeData(mPendingCancelMediaPosition);
      mPendingCancelMediaPosition = -1;
    } else {
      //新加入某个收藏视频
      mReloadLiveData.setValue(true);
    }
  }

  void cancelFavorite() {
    if (mFavoriteId == null || mMediaAdapter == null || mPendingCancelMediaPosition == -1) {
      return;
    }
    Media media = mMediaAdapter.getData().get(mPendingCancelMediaPosition);
    if (media.getId() == null) {
      return;
    }
    if (!NetworkTipUtils.checkNetworkOrTip(getApplication())) {
      return;
    }
    statFavorite(media.getId());
    mFavoriteRepository.favoriteMedia(media.getId(), false)
        .subscribe(new GVideoResponseObserver<Object>() {
          @Override
          protected boolean isShowNetworkDialog() {
            return true;
          }

          @Override
          protected int getNetworkDialogTipTextResId() {
            return R.string.canceling_favorite;
          }

          @Override
          protected void onSuccess(@NonNull Object o) {
            mMediaAdapter.removeData(mPendingCancelMediaPosition);
            mPendingCancelMediaPosition = -1;
            GVideoSensorDataManager.getInstance().clickContent(media,getPid(), InteractType.FAVORITE_CANCEL,
                    null,null);
          }

          @Override
          protected void onFailed(@NonNull Throwable throwable) {
            super.onFailed(throwable);
            mPendingCancelMediaPosition = -1;
            GVideoSensorDataManager.getInstance().clickContent(media,getPid(), InteractType.FAVORITE_CANCEL,
                    null,throwable.getMessage());
          }
        });
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @NonNull
  @Override
  protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
    if (mMediaAdapter == null) {
      throw new NullPointerException("adapter is null");
    }
    return mMediaAdapter;
  }

  @Override
  public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onRefresh(view);
    if (mFavoriteId == null) {
      return;
    }
    mFavoriteRepository
        .getFavoriteDetailList(mFromAuthor, mFavoriteId, 1, mLocalPage.getPageSize())
        .subscribe(new GVideoRefreshObserver<>(view));
  }

  @Override
  public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onLoadMore(view);
    if (mFavoriteId == null) {
      return;
    }
    mFavoriteRepository
        .getFavoriteDetailList(mFromAuthor, mFavoriteId, mLocalPage.getPageNumber(), mLocalPage.getPageSize())
        .subscribe(new GVideoLoadMoreObserver<>(view));
  }



  //</editor-fold>

  //<editor-fold desc="控件事件监听">

  @Override
  public void onItemClick(@NonNull View view, @NonNull RecyclerView.Adapter adapter, int position) {
    if (mMediaAdapter == null) {
      return;
    }
    Bundle bundle = new Bundle();
    bundle.putString(VideoPlugin.EXTRA_FROM_PID, getPid());
    Utils.startVideo(view.getContext(), mMediaAdapter.getData(), mMediaAdapter.getData().get(position), bundle);
  }

  @Override
  public void onUserClick(@NonNull View view, @NonNull RecyclerView.Adapter adapter, int position) {
    AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
    if (plugin != null && mMediaAdapter != null) {
      Media media = mMediaAdapter.getData().get(position);
      if (media != null && media.getAuthor() != null) {
        plugin.startPgcActivity(view, media.getAuthor());
      }
    }
  }

  @Override
  public void onMore(@NonNull View view, @NonNull RecyclerView.Adapter adapter, int position) {
    mPendingCancelMediaPosition = position;
    mCancelFavoriteDialogLiveData.setValue(true);
  }
  //</editor-fold>


  private void statFavorite(String mediaId) {
    String contentId = mediaId;
    String channelId = "";
    String fromPid = "";
    String fromChannelId = "";
    StatFromModel stat = new StatFromModel(contentId, getPid(), channelId, fromPid, fromChannelId);
    JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
    ds.addProperty(StatConstants.DS_KEY_FAVORITE, "0");
    StatEntity statEntity = StatEntity.Builder.aStatEntity()
        .withEv(StatConstants.EV_FAVORITE)
        .withDs(ds.toString())
        .withPid(stat.pid)
        .build();
    GVideoStatManager.getInstance().stat(statEntity);
  }
}
