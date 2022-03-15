package com.hzlz.aviation.feature.feed.template.view;

import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.feed.view.FeedPageFragment;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;

class StatHelper {
  private static final String TAG = StatHelper.class.getSimpleName();
  private String mFragmentId;

  public StatHelper(String mFragmentId) {
    this.mFragmentId = mFragmentId;
  }

  void statMedia(MediaModel mediaModel) {
    FeedPageFragment fragment =
        (FeedPageFragment) MediaFragmentManager.getInstance().getFragment(mFragmentId);
    if (!fragment.isFeedFragmentVisible()) return;

    android.util.Log.d(TAG, "statMedia = " + mediaModel.getTitle());
    StatFromModel stat = getStat(mediaModel);
    JsonObject ds = GVideoStatManager.getInstance().createDsContent(stat);
    StatEntity statEntity = StatEntity.Builder.aStatEntity()
        .withPid(stat.pid)
        .withEv(StatConstants.EV_MEDIA)
        .withDs(ds.toString())
        .withType(StatConstants.TYPE_SHOW_E)
        .build();
    GVideoStatManager.getInstance().stat(statEntity);
  }

  private StatFromModel getStat(MediaModel mediaModel) {
    Fragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
    String pid = "";
    String channelId = "";
    if (fragment instanceof MediaPageFragment) {
      pid = ((MediaPageFragment) fragment).getPid();
      channelId = ((MediaPageFragment) fragment).getChannelId();
    }
    String contentId = mediaModel.getId();
    String fromPid = "";
    String fromChannelId = "";
    StatFromModel stat = new StatFromModel(contentId, pid, channelId, fromPid, fromChannelId);
    return stat;
  }
}
