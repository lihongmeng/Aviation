package com.jxntv.account.model;

import android.webkit.URLUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import com.jxntv.account.R;
import com.jxntv.utils.ResourcesUtils;

/**
 * 可观察的收藏模型
 *
 *
 * @since 2020-02-12 14:32
 */
public final class FavoriteObservable {
  //<editor-fold desc="属性">
  @NonNull
  private ObservableBoolean isVertical = new ObservableBoolean();
  @NonNull
  public ObservableField<String> coverUrl = new ObservableField<>();
  @NonNull
  public ObservableField<String> groupName = new ObservableField<>();
  @NonNull
  public ObservableField<String> count = new ObservableField<>();
  @NonNull
  public ObservableField<String> detailText = new ObservableField<>();
  //</editor-fold>

  //<editor-fold desc="API">

  void setCoverUrl(@Nullable String coverUrl) {
    if (URLUtil.isNetworkUrl(coverUrl)) {
      this.coverUrl.set(coverUrl);
    } else {
      this.coverUrl.set(null);
    }
  }

  void setGroupName(@Nullable String groupName) {
    this.groupName.set(groupName);
  }

  void setCount(@Nullable String groupName, int count) {
    if (groupName != null) {
      this.count.set(ResourcesUtils.getString(R.string.favorite_total, count, groupName));
    } else {
      this.count.set(String.valueOf(count));
    }
  }

  void setDetailText(@NonNull String detailText) {
    this.detailText.set(detailText);
  }

  void setIsVertical(boolean isVertical) {
    this.isVertical.set(isVertical);
  }
  //</editor-fold>
}
