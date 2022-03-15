package com.hzlz.aviation.feature.video.ui.detail;

import android.content.Context;

import androidx.annotation.IntDef;
import androidx.lifecycle.MutableLiveData;

import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerHeaderFooterAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class DetailAdapter<CommentModel, VH extends BaseRecyclerViewHolder> extends BaseRecyclerHeaderFooterAdapter<CommentModel, VH> {
  public final MutableLiveData<ActionModel<CommentModel>> mActionLiveData = new MutableLiveData<>();
  public DetailAdapter(Context context) {
    super(context);
  }

  public static final int ACTION_ITEM = 1;
  public static final int ACTION_AVATAR = 10;
  public static final int ACTION_FROM_USER = 11;
  public static final int ACTION_TO_USER = 12;
  public static final int ACTION_REMOVE = 20;
  public static final int ACTION_REPLY = 30;
  public static final int ACTION_REPORT = 40;
  public static final int ACTION_PRAISE = 50;
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ACTION_ITEM, ACTION_AVATAR, ACTION_REMOVE, ACTION_REPLY, ACTION_FROM_USER, ACTION_TO_USER,ACTION_REPORT,ACTION_PRAISE})
  public @interface ACTION {}
  public static final class ActionModel<CommentModel> {
    public final CommentModel model;
    public final @ACTION int type;

    public ActionModel(CommentModel model, @ACTION int type) {
      this.model = model;
      this.type = type;
    }
  }
}
