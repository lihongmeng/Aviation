package com.hzlz.aviation.feature.account.ui.moment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentMomentBinding;
import com.hzlz.aviation.kernel.base.model.share.FollowChangeModel;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

import io.reactivex.rxjava3.core.Observable;

/**
 * 动态界面
 *
 *
 * @since 2020-02-10 15:12
 */
@SuppressWarnings("FieldCanBeLocal")
public final class MomentFragment extends MediaPageFragment<FragmentMomentBinding> {
  //<editor-fold desc="属性">
  private MomentViewModel mMomentViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected boolean enableLazyLoad() {
    return true;
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_moment;
  }

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override public String getChannelId() {
    return "";
  }

  @Override public String getPid() {
    return StatPid.MOMENT;
  }

  @Override protected void initView() {
    init(getGvFragmentId(), false);
    super.initView();
  }

  @Override
  protected void bindViewModels() {
    mMomentViewModel = super.bingViewModel(MomentViewModel.class);
    mMomentViewModel.updateTabId(getTabId());

    if (mBinding instanceof FragmentMomentBinding) {
      ((FragmentMomentBinding) mBinding).setViewModel(mMomentViewModel);
      mMomentViewModel.getAutoRefreshLiveData().observe(this, new Observer<Boolean>() {
        @Override public void onChanged(Boolean autoRefresh) {
          if (autoRefresh) {
            ((FragmentMomentBinding) mBinding).refreshLayout.autoRefresh();
          }
        }
      });
    }
  }

  @Override
  protected void loadData() {
    //super.loadData();
    // 监听登录和登出
    listenEvent();
    // 检测登录状态
    mMomentViewModel.checkNetworkAndLoginStatus();
  }

  @Override public void onReload(@NonNull View view) {
    mMomentViewModel.checkNetworkAndLoginStatus();
  }

  @Override public Observable<ShortVideoListModel> loadMoreShortData(boolean refresh) {
    return mMomentViewModel.loadMoreShortData();
  }

  @Override public ShortVideoListModel getShortListModel(MediaModel mediaModel) {
    return mMomentViewModel.createShortListModel(mediaModel);
  }

  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 监听时间
   */
  protected void listenEvent() {
    super.listenEvent();
    //关注状态变化，需要强刷当前页面，否则页面page无法处理；
    GVideoEventBus.get(SharePlugin.EVENT_FOLLOW_CHANGE, FollowChangeModel.class)
        .observe(this, new Observer<FollowChangeModel>() {
          @Override public void onChanged(FollowChangeModel followChangeModel) {
            //mMomentViewModel.loadRefreshData();
            ((FragmentMomentBinding) mBinding).refreshLayout.autoRefresh();
          }
        });
  }

  @Override protected void checkLoginStatus() {
    mMomentViewModel.checkNetworkAndLoginStatus();
  }
  //</editor-fold>
}
