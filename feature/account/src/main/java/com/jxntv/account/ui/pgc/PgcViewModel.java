package com.jxntv.account.ui.pgc;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jxntv.account.R;
import com.jxntv.account.model.Author;
import com.jxntv.account.repository.AuthorRepository;
import com.jxntv.account.repository.InteractionRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.exception.GVideoAPIException;
import com.jxntv.network.exception.GVideoCode;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;
import io.reactivex.Observable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * PGC 页面 ViewModel
 *
 */
public final class PgcViewModel extends BaseViewModel {
  /** PGC uid */
  private String mAuthorId;
  private int mAuthorType;
  @NonNull
  private CheckThreadLiveData<Author> mAuthorLiveDta = new CheckThreadLiveData<>();
  @NonNull
  private AuthorRepository mAuthorRepository = new AuthorRepository();
  @NonNull
  private InteractionRepository mInteractionRepository = new InteractionRepository();

  public PgcViewModel(@NonNull Application application) {
    super(application);
  }

  /** 设置PGC uid */
  public void setAuthorId(String authorId, int authorType) {
    mAuthorId = authorId;
    mAuthorType = authorType;
  }

  @NonNull
  LiveData<Author> getAuthorLiveData() {
    return mAuthorLiveDta;
  }
 // /** 短视频详情页中加载更多逻辑 */
 // Observable<ShortVideoListModel> loadMoreShortData() {
 //   return null;
 // }

  public void back(@NonNull View view) {
    ((Activity)view.getContext()).finish();
  }

  public void follow(@NonNull View view) {
    final Author author = mAuthorLiveDta.getValue();
    if (author == null || author.getId() == null) {
      return;
    }
    AccountPlugin accountPlugin=PluginManager.get(AccountPlugin.class);
    if (!accountPlugin.hasLoggedIn()) {
      accountPlugin.startLoginActivity(view.getContext());
      GVideoSensorDataManager.getInstance().enterRegister(
              StatPid.getPageName(getPid()),
              ResourcesUtils.getString(R.string.follow)
      );
      return;
    }
    if (!NetworkTipUtils.checkNetworkOrTip(view.getContext())) {
      return;
    }
    mInteractionRepository
        .followAuthor(author.getId(), author.getType(), author.getName(),!author.isFollow())
        .subscribe(new GVideoResponseObserver<Boolean>() {
          @Override
          protected void onSuccess(@NonNull Boolean isFollow) {
            author.setFollow(isFollow);
          }

          @Override
          public void onError(Throwable throwable) {
            super.onError(throwable);
            GVideoSensorDataManager.getInstance().followAccount(!author.isFollow(),author.getId(),
                    author.getName(),author.getType(),throwable.getMessage());
          }
        });
  }

  void loadData() {
    Author author = new Author();
    author.setId(mAuthorId);
    author.setType(mAuthorType);
    mAuthorRepository.getAuthorById(author).subscribe(new GVideoResponseObserver<Author>() {
      @Override
      protected void onSuccess(@NonNull Author author) {
        mAuthorLiveDta.setValue(author);
      }

      @Override protected void onAPIError(@NonNull Throwable throwable) {
        if (throwable instanceof GVideoAPIException) {
          //与服务器约定，该PGC已被禁用code
          if (((GVideoAPIException) throwable).getCode() == GVideoCode.CODE_PGC_FORBIDDEN) {
            Author author = new Author();
            author.setName(throwable.getMessage());
            mAuthorLiveDta.setValue(author);
          }
        }
      }
    });
  }

  public void onShareClicked(View v){
    if (mAuthorLiveDta.getValue()==null){
      return;
    }

    ShareDataModel shareDataModel = new ShareDataModel.Builder()
            .setTitle(mAuthorLiveDta.getValue().getName())
            .setDescription(v.getContext().getString(R.string.share_default_description))
            .setImage(mAuthorLiveDta.getValue().getAvatar())
            .setUrl(mAuthorLiveDta.getValue().getShareUrl())
            .setShowCreateBill(false)
            .build();
    PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), false,
            true, shareDataModel, null);
  }

}
