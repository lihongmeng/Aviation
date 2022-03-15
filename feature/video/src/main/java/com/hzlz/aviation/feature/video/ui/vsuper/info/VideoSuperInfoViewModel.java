package com.hzlz.aviation.feature.video.ui.vsuper.info;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;

import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.video.R;

public class VideoSuperInfoViewModel extends BaseViewModel {
  private static final String TAG = VideoSuperInfoViewModel.class.getName();

  private VideoModel mVideoModel;
  public VideoSuperInfoViewModel(@NonNull Application application, VideoModel videoModel) {
    super(application);
    mVideoModel = videoModel;
  }
  private StatFromModel mStatFromModel;
  public void setStatFromModel(StatFromModel stat) {
    mStatFromModel = stat;
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
    AuthorModel author = mVideoModel.getAuthor();
    PluginManager.get(AccountPlugin.class).getFollowRepository()
        .followAuthor(author.getId(), author.getType(), author.getName(),!followObservable.get()).subscribe(new GVideoResponseObserver<Boolean>() {
      @Override protected void onSuccess(@NonNull Boolean result) {
        followObservable.set(result);
      }

      @Override
      public void onError(Throwable throwable) {
        super.onError(throwable);
        GVideoSensorDataManager.getInstance().followAccount(!author.isFollow(),author.getId(),
                author.getName(),author.getType(),throwable.getMessage());
      }
    });
  }

  public void onShareClicked(View v) {
    ObservableBoolean favoriteObservable = mVideoModel.getObservable().getIsFavor();
    ObservableBoolean followObservable =null;
    if (mVideoModel.getAuthor() != null) {
      followObservable = mVideoModel.getAuthor().getObservable().getIsFollow();
    }
    String reportUrl = PluginManager.get(H5Plugin.class).getReportUrl() + "?mid=" + mVideoModel.getId();
    ShareDataModel shareDataModel = new ShareDataModel.Builder()
            .setVideoModel(mVideoModel)
            .setShowShare(mVideoModel.mediaStatus != null && mVideoModel.mediaStatus == 3)
            .setReportUrl(reportUrl)
            .setFavorite(favoriteObservable.get())
            .setFollow(followObservable != null && followObservable.get())
            .setShowDelete(false)
            .setShowCreateBill(false)
            .build();
    PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), false, shareDataModel, mStatFromModel);
  }

  public void onMoreClicked(View v) {
    onShareClicked(v);
  }
}
