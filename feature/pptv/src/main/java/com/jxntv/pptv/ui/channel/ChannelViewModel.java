package com.jxntv.pptv.ui.channel;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.pptv.R;
import com.jxntv.pptv.adapter.BannerAdapter;
import com.jxntv.pptv.adapter.ChannelAdapter;
import com.jxntv.pptv.adapter.TagAdapter;
import com.jxntv.pptv.model.Banner;
import com.jxntv.pptv.model.Category;
import com.jxntv.pptv.model.Channel;
import com.jxntv.pptv.model.ChannelResponse;
import com.jxntv.pptv.model.Media;
import com.jxntv.pptv.model.Tag;
import com.jxntv.pptv.model.annotation.CategoryKey;
import com.jxntv.pptv.repository.ChannelRepository;
import com.jxntv.pptv.ui.more.MoreFragmentArgs;
import com.jxntv.widget.CirclePageIndicator;
import com.jxntv.widget.GVideoViewPager;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * PPTV频道页 ViewModel
 *
 */
public final class ChannelViewModel extends BaseViewModel
  implements BannerAdapter.Listener, TagAdapter.Listener, ChannelAdapter.Listener {
  private static final long BANNER_NEXT_PERIOD = 5000L;

  private TagAdapter mHotTagAdapter = new TagAdapter();
  private ChannelAdapter mChannelAdapter = new ChannelAdapter();
  private ChannelRepository mChannelRepository = new ChannelRepository();
  public ObservableBoolean mBannerVisible = new ObservableBoolean(false);
  public ObservableBoolean mTagVisible = new ObservableBoolean(false);
  public ObservableBoolean mPlateVisible = new ObservableBoolean(false);
  private CheckThreadLiveData<List<Banner>> mBannerAdapterLiveData = new CheckThreadLiveData<>();
  private CheckThreadLiveData<Object> mBannerNextLiveData = new CheckThreadLiveData<>();
  @Nullable private Timer mTimer;

  public ChannelViewModel(@NonNull Application application) {
    super(application);
    mHotTagAdapter.setListener(this);
    mChannelAdapter.setSectionListener(this);
  }

  @Override protected void onCleared() {
    cancelTimer();
  }

  private void cancelTimer() {
    if (mTimer != null) {
      mTimer.cancel();
    }
  }

  private void startTimer() {
    cancelTimer();
    mTimer = new Timer();
    mTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        mBannerNextLiveData.setValue(null);
      }
    }, BANNER_NEXT_PERIOD, BANNER_NEXT_PERIOD);
  }

  LiveData<List<Banner>> getBannerLiveData() {
    return mBannerAdapterLiveData;
  }

  LiveData<Object> getBannerNextLiveData() {
    return mBannerNextLiveData;
  }

  void onBannerScrollStateChanged(@NonNull GVideoViewPager viewPager, int state) {
    switch (state) {
      case ViewPager.SCROLL_STATE_IDLE:
        // 循环滑动逻辑，滑动到收尾位置时进行无动画跳转
        BannerAdapter adapter = (BannerAdapter) viewPager.getAdapter();
        if (adapter == null) return;
        int count = adapter.getCount();
        if (count <= 3) return; //单个Banner不支持循环滑动

        int startHookIndex = 0;
        int lastHookIndex = count - 1;
        int current = viewPager.getCurrentItem();
        if (current == startHookIndex) {
          viewPager.setCurrentItem(lastHookIndex - 1, false);
        } else if (current == lastHookIndex) {
          viewPager.setCurrentItem(startHookIndex + 1, false);
        }

        startTimer();
        break;
      case ViewPager.SCROLL_STATE_DRAGGING:
      case ViewPager.SCROLL_STATE_SETTLING:
        cancelTimer();
        break;
      default:
        break;
    }
  }

  void setBannerStartPosition(@NonNull GVideoViewPager viewPager, @NonNull CirclePageIndicator indicator) {
    BannerAdapter adapter = (BannerAdapter) viewPager.getAdapter();
    //viewPager.setAdapter(adapter);
    int count = adapter != null ? adapter.getCount() : 0;
    if (count > 3) {
      indicator.setViewPager(viewPager, 1);
      viewPager.setCurrentItem(1, false);
    }
  }

  void handleBannerNext(@NonNull GVideoViewPager viewPager) {
    int current = viewPager.getCurrentItem();
    viewPager.setCurrentItem(current + 1);
  }

  @NonNull public RecyclerView.Adapter getHotTagAdapter() {
    return mHotTagAdapter;
  }

  @NonNull public RecyclerView.LayoutManager getHotTagLayoutManager() {
    final LinearLayoutManager llm = new LinearLayoutManager(getApplication(),
        LinearLayoutManager.HORIZONTAL, false);
    return llm;
  }

  @NonNull public RecyclerView.Adapter getAdapter() {
    return mChannelAdapter;
  }

  @NonNull public RecyclerView.LayoutManager getLayoutManager() {
    final GridLayoutManager glm = new GridLayoutManager(getApplication(), 2);
    glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(final int position) {
        int type = mChannelAdapter.getSectionItemViewType(position);
        if (type == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
          return 2;
        }
        return 1;
      }
    });
    return glm;
  }

  public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    mChannelRepository.getChannelList()
        .subscribe(new GVideoResponseObserver<ChannelResponse>() {
          @Override protected void onAPIError(@NonNull Throwable throwable) {
            view.finishGVideoRefresh();
            updatePlaceholderLayoutType(PlaceholderType.ERROR);
          }

          @Override protected void onNetworkNotAvailableError(@NonNull Throwable throwable) {
            view.finishGVideoRefresh();
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
          }

          @Override protected void onSuccess(@NonNull ChannelResponse channelResponse) {
            view.finishGVideoRefresh();

            boolean bannerVisible = channelResponse.getHeader() != null && channelResponse.getHeader().size() > 0;
            boolean tagVisible = channelResponse.getTag() != null && channelResponse.getTag().size() > 0;
            boolean plateVisible = channelResponse.getPlate() != null && channelResponse.getPlate().size() > 0;
            if (!bannerVisible && !tagVisible && !plateVisible) {
              updatePlaceholderLayoutType(PlaceholderType.EMPTY);
              return;
            }

            updatePlaceholderLayoutType(PlaceholderType.NONE);
            mBannerVisible.set(bannerVisible);
            if (bannerVisible) {
              startTimer();
              mBannerAdapterLiveData.setValue(channelResponse.getHeader());
            }

            mTagVisible.set(tagVisible);
            if (tagVisible) {
              mHotTagAdapter.replaceData(channelResponse.getTag());
            }

            mPlateVisible.set(plateVisible);
            if (plateVisible) {
              mChannelAdapter.replace(channelResponse.getPlate());
            }

          }
        });
  }

  @Override public void onItemRootViewClicked(@NonNull View v, @NonNull BannerAdapter adapter,
      int itemAdapterPosition) {
    Banner banner = adapter.getBannerList().get(itemAdapterPosition);
    String url = banner.getActUrl();
    if (!TextUtils.isEmpty(url)) {
      Bundle bundle = new Bundle();
      bundle.putString("title", banner.getName());
      bundle.putString("url", url);
      PluginManager.get(WebViewPlugin.class).startWebViewFragment(v, bundle);
    } else {

      VideoModel model = VideoModel.Builder.aVideoModel()
          .withId(banner.getId())
          .withMediaType(banner.getType())
          .withCoverUrl(banner.getCover())
          .withTitle(banner.getName())
          .build();
      PluginManager.get(DetailPagePlugin.class).dispatchToDetail(v.getContext(),model,null);
    }
  }

  @Override public void onItemRootViewClicked(@NonNull View v, @NonNull TagAdapter adapter,
      int itemAdapterPosition) {
    Tag tag = adapter.getData().get(itemAdapterPosition);
    navigateToMore(v, tag, CategoryKey.TAG);
  }

  @Override public void onHeaderMoreButtonClicked(@NonNull View v, @NonNull Channel channel) {
    navigateToMore(v, channel, CategoryKey.PLATE);
  }

  @Override public void onItemRootViewClicked(@NonNull View v, @NonNull Channel channel,
      int itemAdapterPosition) {
    Media media = channel.getMedia().get(itemAdapterPosition);
    PluginManager.get(DetailPagePlugin.class).dispatchToDetail(v.getContext(),media,null);
  }

  private void navigateToMore(@NonNull View v, @NonNull Tag tag, @CategoryKey String categoryKey) {
    Category category = new Category();
    category.setId(tag.getId());
    category.setName(tag.getName());
    category.setCategoryKey(categoryKey);
    category.setCheck(true);
    Navigation.findNavController(v).navigate(
        R.id.more_nav_graph, new MoreFragmentArgs.Builder(category).build().toBundle());
  }
}
