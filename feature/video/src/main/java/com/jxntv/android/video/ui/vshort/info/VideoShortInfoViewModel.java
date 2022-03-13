package com.jxntv.android.video.ui.vshort.info;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import com.google.gson.JsonObject;
import com.jxntv.android.video.Me;
import com.jxntv.android.video.R;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.FavoritePlugin;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.GVideoStatManager;
import com.jxntv.stat.StatConstants;
import com.jxntv.stat.StatPid;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.utils.ResourcesUtils;

public class VideoShortInfoViewModel extends BaseViewModel {
  private static final String TAG = VideoShortInfoViewModel.class.getName();
  public final MutableLiveData<Integer> mCommentLiveData = new MutableLiveData<>();

  private VideoModel mVideoModel;
  public VideoShortInfoViewModel(@NonNull Application application, VideoModel videoModel) {
    super(application);
    mVideoModel = videoModel;
  }

  private StatFromModel mStatFromModel;
  public void setStatFromModel(StatFromModel statFromModel) {
    mStatFromModel = statFromModel;
  }

  public void onBackPressed(View v) {
    if (!Navigation.findNavController(v).navigateUp()) {
      getActivityByView(v).finish();
    }
  }

  public void onAvatarClicked(View v) {
    PluginManager.get(AccountPlugin.class).startPgcActivity(v, mVideoModel.getAuthor());
  }

  public void onFollowClicked(View v) {
    if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
      GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
      GVideoSensorDataManager.getInstance().enterRegister(
              StatPid.getPageName(getPid()),
              ResourcesUtils.getString(R.string.follow)
      );
      return;
    }
    if (!NetworkTipUtils.checkNetworkOrTip(v.getContext())) {
      return;
    }
    ObservableBoolean followObservable = mVideoModel.getAuthor().getObservable().getIsFollow();
    AuthorModel authorModel = mVideoModel.getAuthor();
    PluginManager.get(AccountPlugin.class).getFollowRepository()
        .followAuthor(authorModel.getId(),authorModel.getType(),authorModel.getName(), !followObservable.get()).subscribe(new GVideoResponseObserver<Boolean>() {
      @Override protected void onSuccess(@NonNull Boolean result) {
        followObservable.set(result);
      }

      @Override
      public void onError(Throwable throwable) {
        super.onError(throwable);
        GVideoSensorDataManager.getInstance().followAccount(!followObservable.get(),authorModel.getId(),
                authorModel.getName(),authorModel.getType(),throwable.getMessage());
      }
    });
  }

  public void onStarClicked(View v) {
    if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
      GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
      GVideoSensorDataManager.getInstance().enterRegister(
              StatPid.getPageName(getPid()),
              ResourcesUtils.getString(R.string.like)
      );
      return;
    }
    if (!NetworkTipUtils.checkNetworkOrTip(v.getContext())) {
      return;
    }
    ObservableBoolean favorObservable = mVideoModel.getObservable().getIsFavor();
    statFavorite(mVideoModel, !favorObservable.get());
    String mediaId = mVideoModel.getId();
    PluginManager.get(FavoritePlugin.class).getFavoriteRepository()
        .favoriteMedia(mediaId, !favorObservable.get()).subscribe(new GVideoResponseObserver<Boolean>() {
      @Override protected void onSuccess(@NonNull Boolean result) {
        GVideoSensorDataManager.getInstance().favoriteContent(mVideoModel,
                mStatFromModel.pid,!favorObservable.get(),null);
        favorObservable.set(result);
      }

      @Override
      public void onError(Throwable throwable) {
        super.onError(throwable);
        GVideoSensorDataManager.getInstance().favoriteContent(mVideoModel,
                mStatFromModel.pid,!favorObservable.get(),throwable.getMessage());
      }
    });
  }

  public void onShareClicked(View v) {
    ObservableBoolean favorObservable = mVideoModel.getObservable().getIsFavor();
    ObservableBoolean followObservable = mVideoModel.getAuthor().getObservable().getIsFollow();
    String reportUrl = PluginManager.get(H5Plugin.class).getReportUrl() + "?mid=" + mVideoModel.getId();
    ShareDataModel shareDataModel = new ShareDataModel.Builder()
            .setVideoModel(mVideoModel)
            .setShowShare(mVideoModel.mediaStatus != null && mVideoModel.mediaStatus == 3)
            .setReportUrl(reportUrl)
            .setFavorite(favorObservable.get())
            .setFollow(followObservable.get())
            .setShowDelete(mVideoModel.getAuthor().isSelf()) //个人发布
            .setShowCreateBill(false)
            .build();
    PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), true, shareDataModel, mStatFromModel);
  }

  public void onCommentClicked(View v) {
    mCommentLiveData.setValue(0);
  }

  public void onTitleClicked(View v) {
    mCommentLiveData.setValue(0);
  }

  private FragmentActivity getActivityByView(@NonNull final View view) {
    Context context = view.getContext();
    while (context instanceof ContextWrapper) {
      if (context instanceof FragmentActivity) {
        return (FragmentActivity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    return null;
  }

  private void statFavorite(VideoModel videoModel, boolean favorite) {
    JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStatFromModel);
    ds.addProperty(StatConstants.DS_KEY_FAVORITE, favorite ? "1" : "0");
    StatEntity statEntity = StatEntity.Builder.aStatEntity()
        .withEv(StatConstants.EV_FAVORITE)
        .withDs(ds.toString())
        .withPid(mStatFromModel != null ? mStatFromModel.pid : "")
        .build();
    GVideoStatManager.getInstance().stat(statEntity);
  }
}
