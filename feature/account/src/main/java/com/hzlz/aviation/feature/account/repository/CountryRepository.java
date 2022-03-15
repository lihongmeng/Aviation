package com.hzlz.aviation.feature.account.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.Country;
import com.hzlz.aviation.feature.account.model.ICountry;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.LocalData;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.PhoneUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.reactivex.rxjava3.core.Observable;

/**
 * 国家仓库类
 *
 *
 * @since 2020-01-14 16:23
 */
public final class CountryRepository extends BaseDataRepository {
  //<editor-fold desc="属性">
  private static final String[] SPECIAL_COUNTRY_NAME = new String[] {
      "台湾", "臺灣", "Taiwan", "香港", "Hong Kong", "澳门", "澳門", "Macao"
  };
  private static final String[] CHINA = new String[] {
      "中国", "中國", "China"
  };
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 获取国家列表
   */
  public Observable<List<? extends ICountry>> getCountryList() {
    return new LocalData<List<? extends ICountry>>(mEngine) {
      @Override
      protected List<? extends ICountry> loadFromLocal() {
        return doGetCountryList();
      }
    }.asObservable();
  }

  @NonNull
  private List<? extends ICountry> doGetCountryList() {
    PhoneNumberUtil util = PhoneUtils.getPhoneNumberUtils(GVideoRuntime.getApplication());
    Set<String> supportedRegions = util.getSupportedRegions();
    int size = supportedRegions == null ? 0 : supportedRegions.size();
    List<Country> list = new ArrayList<>(size);
    if (size > 0) {
      Locale defaultLocale = Locale.getDefault();
      for (String region : supportedRegions) {
        int code = util.getCountryCodeForRegion(region);
        Locale locale = new Locale(defaultLocale.getLanguage(), region);
        String displayCountryCode = "+" + code;
        String countryName = locale.getDisplayCountry();
        for (String name : SPECIAL_COUNTRY_NAME) {
          if (name.equals(countryName)) {
            if (defaultLocale.getLanguage().equals(Locale.CHINA.getLanguage())) {
              countryName += "（" + CHINA[0] + "）";
            } else if (defaultLocale.getLanguage().equals(Locale.TAIWAN.getLanguage())) {
              countryName += "（" + CHINA[1] + "）";
            } else if (defaultLocale.getLanguage().equals(Locale.US.getLanguage())) {
              countryName += "( " + CHINA[2] + " )";
            }
          }
        }
        Log.i("TAG", countryName + " " + displayCountryCode);
        list.add(new Country(
            displayCountryCode,
            countryName,
            countryName + " " + displayCountryCode
        ));
      }
    }
    return list;
  }
  //</editor-fold>
}
