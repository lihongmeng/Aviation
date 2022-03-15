package com.hzlz.aviation.feature.account.adapter;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.BR;
import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.AdapterOneColumnMediaBinding;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;
import com.hzlz.aviation.kernel.base.tag.TagTextHelper;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

/**
 *
 * @since 2020-02-12 20:36
 */
public final class OneColumnMediaAdapter extends AbstractMediaAdapter {
  //<editor-fold desc="属性">
  private Author mFromAuthor;
  public OneColumnMediaAdapter() {
    super();
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getItemLayoutId() {
    return R.layout.adapter_one_column_media;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    super.bindData(holder, position);
    Media media = mDataList.get(position);
    if (media.getAuthor() != null) {
      holder.bindData(com.hzlz.aviation.feature.account.BR.author, media.getAuthor().getObservable());
    }
    if (holder.binding instanceof AdapterOneColumnMediaBinding) {
      AdapterOneColumnMediaBinding mediaBinding = (AdapterOneColumnMediaBinding) holder.binding;
      TagTextHelper.createTagTitle(
          GVideoRuntime.getApplication(), mediaBinding.textViewTag, media.getTagType()
      );
    }

    holder.bindData(BR.fromAuthor, mFromAuthor);
  }
  //</editor-fold>

  public void setFromAuthor(Author fromAuthor) {
    mFromAuthor = fromAuthor;
  }
}
