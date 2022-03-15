package com.hzlz.aviation.feature.account.adapter;

import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.BUTTON_LINK;
import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.FOLLOW;
import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.IMAGE_LINK;
import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.INTERACTIVE;
import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.TEXT;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.BR;
import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.MessageNotificationDetail;
import com.hzlz.aviation.feature.account.model.annotation.NotificationType;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;

/**
 * 消息通知详情适配器
 *
 *
 * @since 2020-03-12 11:07
 */
public final class MessageNotificationDetailAdapter extends BaseDataBindingAdapter<MessageNotificationDetail> {

  //<editor-fold desc="属性">
  @Nullable
  private Listener mListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">

  public MessageNotificationDetailAdapter() {
    super();
    addItemType(TEXT, R.layout.adapter_message_notification_detail_text);
    addItemType(BUTTON_LINK, R.layout.adapter_message_notification_detail_button);
    addItemType(IMAGE_LINK, R.layout.adapter_message_notification_detail_image);
    addItemType(INTERACTIVE, R.layout.adapter_message_notification_detail_comment);
    addItemType(FOLLOW, R.layout.adapter_message_notification_detail_follow);
  }

  //</editor-fold>

  //<editor-fold desc="API">

  public void setListener(@Nullable Listener listener) {
    mListener = listener;
  }

  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getItemLayoutId() {
    return 0;
  }

  @Override
  protected int getDataItemViewType(int position) {
    MessageNotificationDetail detail = mDataList.get(position);
    switch (detail.getMsgType()){
      case NotificationType.INTERACTIVE:
      case NotificationType.QA:
        return INTERACTIVE;
      case NotificationType.FOLLOW:
        return FOLLOW;
    }
    return detail.getType();
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    MessageNotificationDetail detail = mDataList.get(position);
    if (TextUtils.equals(detail.getSourceId(), "0")) {
      detail.setAvatarIcon(R.drawable.ic_notification);
    }
    holder.bindData(BR.position, detail.getModelPosition());
    holder.bindData(BR.adapter, this);
    holder.bindData(BR.detail, detail.getMessageNotificationDetailObservable());
  }
  //</editor-fold>

  //<editor-fold desc="接口">
  public interface Listener {
    void onItemClick(
        @NonNull MessageNotificationDetailAdapter adapter,
        @NonNull View view,
        int position
    );

    void onButtonLinkClicked(
        @NonNull MessageNotificationDetailAdapter adapter,
        @NonNull View view,
        int position
    );

    void onImageLinkClicked(@NonNull MessageNotificationDetailAdapter adapter,
        @NonNull View view,
        int position
    );

    void onAvatarClicked(@NonNull MessageNotificationDetailAdapter adapter,
                            @NonNull View view,
                            int position
    );

    void onAttentionClicked(@NonNull MessageNotificationDetailAdapter adapter,View view, int position);
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void onItemClicked(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onItemClick(this, view, position);
    }
  }

  public void onButtonLinkClicked(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onButtonLinkClicked(this, view, position);
    }
  }

  public void onImageLinkClicked(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onImageLinkClicked(this, view, position);
    }
  }

  public void onAvatarClicked(@NonNull View view, int position){
    if (mListener != null) {
      mListener.onAvatarClicked(this, view, position);
    }
  }

  /**
   * 关注按钮点击
   */
  public void onAttentionClicked(View view, int position) {
    if (mListener != null) {
      mListener.onAttentionClicked(this, view, position);
    }
  }

  //</editor-fold>
}
