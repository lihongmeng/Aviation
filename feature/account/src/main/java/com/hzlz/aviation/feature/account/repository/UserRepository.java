package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.AvatarInfo;
import com.hzlz.aviation.feature.account.model.LoginResponse;
import com.hzlz.aviation.feature.account.model.RegionModel;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.model.annotation.PrivacyRange;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.request.AuthenticateIdCardRequest;
import com.hzlz.aviation.feature.account.request.CancelAccountRequest;
import com.hzlz.aviation.feature.account.request.CheckUserJoinCommunityRequest;
import com.hzlz.aviation.feature.account.request.GetAvatarListRequest;
import com.hzlz.aviation.feature.account.request.GetCurrentUserRequest;
import com.hzlz.aviation.feature.account.request.IMPlatformAccountRequest;
import com.hzlz.aviation.feature.account.request.LoginBySmsCodeRequest;
import com.hzlz.aviation.feature.account.request.LogoutRequest;
import com.hzlz.aviation.feature.account.request.ModifyUserRequest;
import com.hzlz.aviation.feature.account.request.QuickLoginRequest;
import com.hzlz.aviation.feature.account.request.RebindPhoneRequest;
import com.hzlz.aviation.feature.account.request.RegionRequest;
import com.hzlz.aviation.feature.account.request.SwitchAccountRequest;
import com.hzlz.aviation.kernel.base.model.anotation.Gender;
import com.hzlz.aviation.kernel.base.plugin.ChatIMPlugin;
import com.hzlz.aviation.kernel.network.engine.INetworkEngine;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * ???????????????
 *
 *
 * @since 2020-01-13 16:04
 */
public final class UserRepository extends BaseDataRepository {

  //<editor-fold desc="API">

  /**
   * ??????????????????
   */
  @NonNull
  public Observable<User> getCurrentUser() {
    return getCurrentUser(true);
  }

  /**
   * ??????????????????
   *
   * @param synchronize ????????????
   */
  @NonNull
  public Observable<User> getCurrentUser(boolean synchronize) {
    return new UserResponseNetworkData(mEngine) {
      @Override
      protected User loadFromLocal() {
        return UserManager.getCurrentUser();
      }

      @Override
      protected boolean shouldFetchUp(User user) {
        //??????????????????????????????
        return synchronize && UserManager.hasLoggedIn();
      }

      @Override
      protected BaseRequest<User> createRequest() {
        return new GetCurrentUserRequest();
      }
    }.asObservable().map(user -> {
      // ??????????????????????????????????????????????????????????????????????????????
      if (synchronize) {
        return UserManager.getCurrentUser();
      }
      return user;
    });
  }

  /**
   * ???????????????????????????
   *
   * @param phoneNumber ?????????
   * @param countyCode ????????????
   * @param smsCode ???????????????
   */
  @NonNull
  public Observable<LoginResponse> loginBySmsCode(
      @NonNull String phoneNumber,
      @NonNull String countyCode,
      @NonNull String smsCode) {
    return new LoginResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<LoginResponse> createRequest() {
        LoginBySmsCodeRequest request = new LoginBySmsCodeRequest();
        request.setPhoneNumber(phoneNumber);
        request.setCountryCode(countyCode);
        request.setSmsCode(smsCode);
        request.setDistinctId(GVideoSensorDataManager.getInstance().getAnonymousId());
        return request;
      }
    }.asObservable();
  }

  /**
   * ????????????
   *
   * @param countyCode ????????????
   */
  @NonNull
  public Observable<LoginResponse> quickLogin(
          @NonNull String countyCode,
          @NonNull String token) {
    return new LoginResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<LoginResponse> createRequest() {
        QuickLoginRequest request = new QuickLoginRequest();
        request.setToken(token);
        request.setCountryCode(countyCode);
        request.setDistinctId(GVideoSensorDataManager.getInstance().getAnonymousId());
        return request;
      }
    }.asObservable();
  }

  /**
   * ????????????
   *
   * @param phoneNumber ?????????
   * @param countyCode ????????????
   * @param smsCode ???????????????
   */
  @NonNull
  public Observable<LoginResponse> switchAccount(
      @NonNull String phoneNumber,
      @NonNull String countyCode,
      @NonNull String smsCode) {
    return new LoginResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<LoginResponse> createRequest() {
        SwitchAccountRequest request = new SwitchAccountRequest();
        request.setPhoneNumber(phoneNumber);
        request.setCountryCode(countyCode);
        request.setSmsCode(smsCode);
        return request;
      }
    }.asObservable();
  }

  /**
   * ???????????????????????????
   *
   * @param phoneNumber ?????????
   * @param countyCode ????????????
   * @param smsCode ???????????????
   */
  @NonNull
  public Observable<User> rebindPhone(
      @NonNull String phoneNumber,
      @NonNull String countyCode,
      @NonNull String smsCode) {
    return new UserResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<User> createRequest() {
        RebindPhoneRequest request = new RebindPhoneRequest();
        request.setPhoneNumber(phoneNumber);
        request.setCountryCode(countyCode);
        request.setSmsCode(smsCode);
        return request;
      }
    }.asObservable();
  }

  /**
   * ??????????????????
   */
  @NonNull
  public Observable<ArrayList<AvatarInfo>> getAvatarList() {
    return new OneTimeNetworkData<ArrayList<AvatarInfo>>(mEngine) {

      @Override
      protected BaseRequest<ArrayList<AvatarInfo>> createRequest() {
        return new GetAvatarListRequest();
      }
    }.asObservable();
  }

  /**
   * ??????
   */
  @NonNull
  public Observable<Object> logout() {
    return new DeleteUserNetworkData(mEngine) {
      @Override
      protected BaseRequest<Object> createRequest() {
        return new LogoutRequest();
      }
    }.asObservable();
  }

  /**
   * ????????????
   */
  @NonNull
  public Observable<Object> cancelAccount() {
    return new DeleteUserNetworkData(mEngine) {
      @Override
      protected BaseRequest<Object> createRequest() {
        return new CancelAccountRequest();
      }
    }.asObservable();
  }

  /**
   * ???????????????
   *
   * @param idCardNumber ???????????????
   * @param idCardFrontFileId ??????????????????????????? id
   * @param idCardBackFileId ??????????????????????????? id
   * @param idCardSelfFileId ??????????????????????????? id
   */
  @NonNull
  public Observable<User> authenticateIdCard(
      @NonNull String idCardNumber,
      @NonNull String idCardFrontFileId,
      @NonNull String idCardBackFileId,
      @NonNull String idCardSelfFileId) {
    return new UserResponseNetworkData(mEngine) {
      @Override
      protected BaseRequest<User> createRequest() {
        AuthenticateIdCardRequest request = new AuthenticateIdCardRequest();
        request.setIdCardNumber(idCardNumber);
        request.setIdCardFrontImageFileId(idCardFrontFileId);
        request.setIdCardBackImageFileId(idCardBackFileId);
        request.setIdCardSelfImageFileId(idCardSelfFileId);
        return request;
      }
    }.asObservable();
  }

  /**
   * ??????????????????
   *
   * @param nickname ??????
   */
  @NonNull
  public Observable<User> modifyUserNickname(@NonNull String nickname) {
    return new UserResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<User> createRequest() {
        ModifyUserRequest request = new ModifyUserRequest();
        request.setNickname(nickname);
        return request;
      }
    }.asObservable();
  }

  /**
   * ??????????????????
   *
   * @param gender ??????
   * @param genderRange ????????????
   */
  @NonNull
  public Observable<User> modifyUserGender(@Gender int gender, @PrivacyRange int genderRange) {
    return new UserResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<User> createRequest() {
        ModifyUserRequest request = new ModifyUserRequest();
        request.setGender(gender, genderRange);
        return request;
      }
    }.asObservable();
  }

  /**
   * ??????????????????
   *
   * @param birthday ??????
   * @param birthdayRange ????????????
   */
  @NonNull
  public Observable<User> modifyUserBirthday(
      @NonNull Date birthday,
      @PrivacyRange int birthdayRange) {
    return new UserResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<User> createRequest() {
        ModifyUserRequest request = new ModifyUserRequest();
        request.setBirthday(birthday, birthdayRange);
        return request;
      }
    }.asObservable();
  }


  /**
   * ??????????????????
   *
   * @param provinceId ??????id
   * @param cityId ??????id
   */
  @NonNull
  public Observable<User> modifyRegion(int provinceId,String province,int cityId, String city) {
    return new UserResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<User> createRequest() {
        ModifyUserRequest request = new ModifyUserRequest();
        request.setRegion(provinceId,province, cityId, city);
        return request;
      }
    }.asObservable();
  }


  /**
   * ??????????????????
   *
   * @param avatarFileId ?????? id
   */
  @NonNull
  public Observable<User> modifyUserAvatar(@NonNull String avatarFileId) {
    return new UserResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<User> createRequest() {
        ModifyUserRequest request = new ModifyUserRequest();
        request.setAvatar(avatarFileId);
        return request;
      }
    }.asObservable();
  }

  /**
   * ??????????????????
   *
   * @param description ??????
   */
  @NonNull
  public Observable<User> modifyUserDescription(@NonNull String description) {
    return new UserResponseNetworkData(mEngine) {

      @Override
      protected BaseRequest<User> createRequest() {
        ModifyUserRequest request = new ModifyUserRequest();
        request.setDescription(description);
        return request;
      }
    }.asObservable();
  }

  @NonNull
  public Observable<Boolean> checkUserJoinCommunity(String userId, long communityId) {
    return new OneTimeNetworkData<Boolean>(mEngine) {
      @Override
      protected BaseRequest<Boolean> createRequest() {
        CheckUserJoinCommunityRequest request = new CheckUserJoinCommunityRequest();
        request.setJid(userId);
        request.setGroupId(communityId);
        return request;
      }
    }.asObservable();
  }

  /**
   * ??????????????????
   *
   */
  @NonNull
  public Observable<List<RegionModel>> getRegionData() {
    return new OneTimeNetworkData(mEngine) {

      @Override
      protected BaseRequest<List<RegionModel>> createRequest() {
        return new RegionRequest();
      }
    }.asObservable();
  }

  //</editor-fold>

  //<editor-fold desc="??????">
  private static abstract class UserResponseNetworkData extends NetworkData<User> {

    //<editor-fold desc="????????????">

    /**
     * @param engine ???????????? {@link INetworkEngine}
     */
    private UserResponseNetworkData(INetworkEngine engine) {
      super(engine);
    }
    //</editor-fold>

    //<editor-fold desc="????????????">
    @Override
    protected void saveData(User user) {
      UserManager.saveUser(user);
    }

    @Override
    protected void processDataOnMainThread(User user) {
      super.processDataOnMainThread(user);
      UserManager.getCurrentUser().update(user);
    }

    //</editor-fold>
  }

  private static abstract class LoginResponseNetworkData extends NetworkData<LoginResponse> {

    //<editor-fold desc="????????????">

    /**
     * @param engine ???????????? {@link INetworkEngine}
     */
    private LoginResponseNetworkData(INetworkEngine engine) {
      super(engine);
    }
    //</editor-fold>

    //<editor-fold desc="????????????">

    @Override
    protected void saveData(LoginResponse loginResponse) {
      UserManager.saveUser(loginResponse.getUser());
      UserManager.saveToken(loginResponse.getToken());
    }

    @Override
    protected void processDataOnMainThread(LoginResponse loginResponse) {
      super.processDataOnMainThread(loginResponse);
      UserManager.getCurrentUser().update(loginResponse.getUser());
    }
    //</editor-fold>
  }

  private static abstract class DeleteUserNetworkData extends NetworkData<Object> {
    //<editor-fold desc="????????????">

    /**
     * @param engine ???????????? {@link INetworkEngine}
     */
    private DeleteUserNetworkData(INetworkEngine engine) {
      super(engine);
    }
    //</editor-fold>

    //<editor-fold desc="????????????">
    @Override
    protected void saveData(Object o) {
      UserManager.deleteCurrentUser();
      UserManager.saveToken(null);
    }

    @Override
    protected void processDataOnMainThread(Object o) {
      super.processDataOnMainThread(o);
      UserManager.getCurrentUser().update(new User());
    }
    //</editor-fold>
  }

  /**
   * ??????im????????????????????????
   *
   */
  @NonNull
  public void getPlatformAccountData() {

    new NetworkData<List<String>>(mEngine){

      @Override
      protected BaseRequest<List<String>> createRequest() {
        return new IMPlatformAccountRequest();
      }

      @Override
      protected void saveData(List<String> strings) {
        if (strings!=null) {
          PluginManager.get(ChatIMPlugin.class).setPlatformAccountData(strings);
        }
      }
    }.asObservable().subscribe(new BaseResponseObserver<Object>() {
      @Override
      protected void onRequestData(Object o) {

      }

      @Override
      protected void onRequestError(Throwable throwable) {
        LogUtils.e(throwable.getMessage());
      }
    });
  }
  //</editor-fold>
}
