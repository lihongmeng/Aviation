package com.jxntv.account.ui.composition;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import com.jxntv.account.Utils;
import com.jxntv.account.adapter.CompositionAdapter;
import com.jxntv.account.model.Author;
import com.jxntv.account.repository.AuthorRepository;
import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

/**
 * composition 页面 ViewModel
 *
 */
public final class CompositionViewModel extends BaseRefreshLoadMoreViewModel
    implements CompositionAdapter.Listener {
  /** 每页加载作品内容数，每行展示3个，这里选择3N个作为缺省加载数 */
  private static final int PAGE_SIZE = 12;
  /** UGC uid */
  private Author mFromAuthor;
  @NonNull
  private CompositionAdapter mCompositionAdapter = new CompositionAdapter();
  @NonNull
  private AuthorRepository mAuthorRepository = new AuthorRepository();

  public CompositionViewModel(@NonNull Application application) {
    super(application);

    mCompositionAdapter.setListener(this);
  }

  @NonNull @Override protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
    return mCompositionAdapter;
  }

  /** 设置UGC uid */
  void setFromAuthor(Author author) {
    mFromAuthor = author;
  }

  void loadData(@NonNull IGVideoRefreshLoadMoreView view) {
    onRefresh(view);
  }

  public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    if (mFromAuthor == null) {
      return;
    }
    mAuthorRepository
        .getAuthorMediaList(mFromAuthor, 1, PAGE_SIZE)
        .subscribe(new GVideoRefreshObserver<>(view));
  }

  public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
    if (mFromAuthor == null) {
      return;
    }
    mAuthorRepository
        .getAuthorMediaList(mFromAuthor, mLocalPage.getPageNumber(), PAGE_SIZE)
        .subscribe(new GVideoLoadMoreObserver<>(view));
  }

  @Override
  public void onItemClick(@NonNull View view, @NonNull CompositionAdapter adapter, int position) {
    Bundle bundle = new Bundle();
    bundle.putString(VideoPlugin.EXTRA_FROM_PID, getPid());
    Utils.startVideo(view.getContext(), adapter.getData(), adapter.getData().get(position), bundle);
  }

}
