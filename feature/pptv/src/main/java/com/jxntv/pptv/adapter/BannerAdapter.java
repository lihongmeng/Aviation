package com.jxntv.pptv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.jxntv.pptv.R;
import com.jxntv.pptv.databinding.AdapterBannerBinding;
import com.jxntv.pptv.model.Banner;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;
import java.util.ArrayList;
import java.util.List;

public final class BannerAdapter extends ViewPagerItemAdapter {
  private List<Banner> mBannerList = new ArrayList<>();
  private BannerAdapter(ViewPagerItems pages) {
    super(pages);
  }

  public static BannerAdapter create(Context context, List<Banner> bannerList) {
    ViewPagerItems.Creator creator = ViewPagerItems.with(context);
    // 收尾各增加一item，滑动到相应位置时，无动画跳转实现无限循环效果；
    int count = bannerList.size();
    if (count > 1) { // 单个banner 不滑动
      Banner start = bannerList.get(count - 1);
      Banner end = bannerList.get(0);
      bannerList.add(0, start);
      bannerList.add(end);
    }

    for (Banner banner : bannerList) {
      creator.add(banner.getName(), R.layout.adapter_banner);
    }

    ViewPagerItems pages =  creator.create();
    BannerAdapter adapter = new BannerAdapter(pages);
    adapter.setBannerList(bannerList);

    return adapter;
  }

  private void setBannerList(List<Banner> list) {
    mBannerList.clear();
    mBannerList.addAll(list);
  }

  public List<Banner> getBannerList() {
    return mBannerList;
  }

  @NonNull @Override public Object instantiateItem(@NonNull ViewGroup container, int position) {
     View v = (View) super.instantiateItem(container, position);
      AdapterBannerBinding binding = DataBindingUtil.bind(v);
      binding.setAdapter(this);
      binding.setPosition(position);
      binding.setBanner(mBannerList.get(position));
      binding.executePendingBindings();
     return v;
  }

  @Nullable private Listener mListener;
  public void setListener(Listener listener) {
    mListener = listener;
  }

  public void onItemRootViewClicked(@NonNull View v, int position) {
    if (mListener != null) {
      mListener.onItemRootViewClicked(v, this, position);
    }
  }

  public interface Listener {
    void onItemRootViewClicked(@NonNull View v, @NonNull BannerAdapter adapter, final int itemAdapterPosition);
  }
}
