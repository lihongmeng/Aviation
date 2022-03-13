package com.jxntv.pptv.ui.channel;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.pptv.R;
import com.jxntv.pptv.adapter.BannerAdapter;
import com.jxntv.pptv.databinding.FragmentChannelBinding;
import com.jxntv.pptv.model.Banner;
import java.util.List;

/**
 * PPTV频道页
 *
 */
public final class ChannelFragment extends BaseFragment<FragmentChannelBinding> {
  //<editor-fold desc="属性">
  private ChannelViewModel mChannelViewModel;
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override protected boolean enableLazyLoad() {
    return true;
  }

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override protected void updatePlaceholderLayoutType(int type) {
    super.updatePlaceholderLayoutType(type);
    if (type == PlaceholderType.NONE) {
      mBinding.refreshLayout.setVisibility(View.VISIBLE);
    } else {
      mBinding.refreshLayout.setVisibility(View.GONE);
    }
  }

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_channel;
  }
  
  @Override
  protected void initView() {
    setupPlaceholderLayout(R.id.fragment_container);

    mBinding.banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
      }

      @Override public void onPageScrollStateChanged(int state) {
        // 等待滑动结束后进行判断，在onPageSelected中setCurrentItem会跳变
        mChannelViewModel.onBannerScrollStateChanged(mBinding.banner, state);
      }
    });
  }

  @Override
  protected void bindViewModels() {
    mChannelViewModel = bingViewModel(ChannelViewModel.class);
    mBinding.setViewModel(mChannelViewModel);

    mChannelViewModel.getBannerLiveData().observe(this, new NotNullObserver<List<Banner>>() {
      @Override protected void onModelChanged(@NonNull List<Banner> bannerList) {
        BannerAdapter adapter = createBannerAdapter(getContext(), bannerList);
        mBinding.banner.setAdapter(adapter);
        mChannelViewModel.setBannerStartPosition(mBinding.banner, mBinding.indicator);
      }
    });
    mChannelViewModel.getBannerNextLiveData().observe(this,
        o -> mChannelViewModel.handleBannerNext(mBinding.banner));
  }

  @Override
  protected void loadData() {
    mBinding.refreshLayout.autoRefresh();
  }

  @Override public void onReload(@NonNull View view) {
    mBinding.refreshLayout.autoRefresh();
  }

  private BannerAdapter createBannerAdapter(Context context, List<Banner> bannerList) {
    BannerAdapter adapter = BannerAdapter.create(context, bannerList);
    adapter.setListener(mChannelViewModel);
    return adapter;
  }
  //</editor-fold>
}
