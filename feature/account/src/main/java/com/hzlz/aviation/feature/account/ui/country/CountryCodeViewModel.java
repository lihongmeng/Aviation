package com.hzlz.aviation.feature.account.ui.country;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.ICountry;
import com.hzlz.aviation.feature.account.repository.CountryRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;

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
            // if (true) {
            //   Log.i("TAG", t.toString());
            // }
          }
        });
  }
  //</editor-fold>
}
