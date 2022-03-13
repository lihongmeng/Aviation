package com.jxntv.account.ui.ugc;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.UserAuthor;
import com.jxntv.account.repository.AuthorRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.network.exception.GVideoAPIException;
import com.jxntv.network.exception.GVideoCode;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

/**
 * UGC 页面 ViewModel
 *
 */
public final class UgcViewModel extends UgcBaseViewModel {


  public UgcViewModel(@NonNull Application application) {
    super(application);
  }

  public void back(@NonNull View view) {
    ((Activity)view.getContext()).finish();
  }

  public UgcDataBinding getDataBinding() {
    mUgcDataBinding.setEditVisible(false);
    return mUgcDataBinding;
  }


}
