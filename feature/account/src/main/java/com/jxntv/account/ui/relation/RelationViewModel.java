package com.jxntv.account.ui.relation;

import android.app.Application;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import com.jxntv.base.BaseViewModel;

public final class RelationViewModel extends BaseViewModel {
  //<editor-fold desc="构造函数">
  public RelationViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>
  public void back(@NonNull View view) {
    Navigation.findNavController(view).popBackStack();
  }
}
