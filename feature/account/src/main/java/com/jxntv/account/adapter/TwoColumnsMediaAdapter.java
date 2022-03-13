package com.jxntv.account.adapter;

import androidx.annotation.NonNull;
import com.jxntv.account.BR;
import com.jxntv.account.R;
import com.jxntv.account.databinding.AdapterTwoColumnMediaBinding;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.Media;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.base.tag.TagTextHelper;
import com.jxntv.runtime.GVideoRuntime;

/**
 *
 * @since 2020-02-12 20:36
 */
public final class TwoColumnsMediaAdapter extends AbstractMediaAdapter {
  //<editor-fold desc="属性">
  private Author mFromAuthor;
  public TwoColumnsMediaAdapter() {
    super();
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getItemLayoutId() {
    return R.layout.adapter_two_column_media;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    super.bindData(holder, position);
    Media media = mDataList.get(position);
    if (media == null) {
      return;
    }
    if (media.getAuthor() != null) {
      holder.bindData(com.jxntv.account.BR.author, media.getAuthor().getObservable());
    }
    if (holder.binding instanceof AdapterTwoColumnMediaBinding) {
      AdapterTwoColumnMediaBinding mediaBinding = (AdapterTwoColumnMediaBinding) holder.binding;
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
