package com.jxntv.account.ui.country;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import com.jxntv.account.BuildConfig;
import com.jxntv.account.model.ICountry;
import com.jxntv.account.repository.CountryRepository;
import com.jxntv.base.BaseViewModel;
import java.util.List;

/**
 * 国家代码 ViewModel
 *
 *
 * @since 2020-01-14 15:54
 */
public final class CountryCodeViewModel extends BaseViewModel {
  //<editor-fold desc="属性">
  @NonNull
  private CountryRepository mCountryRepository = new CountryRepository();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public CountryCodeViewModel(@NonNull Application application) {
    super(application);
    mCountryRepository
        .getCountryList()
        .subscribe(new GVideoResponseObserver<List<? extends ICountry>>() {
          @Override
          protected void onSuccess(@NonNull List<? extends ICountry> t) {
            if (BuildConfig.DEBUG) {
              Log.i("TAG", t.toString());
            }
          }
        });
  }
  //</editor-fold>
}
