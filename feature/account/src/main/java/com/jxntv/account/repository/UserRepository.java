package com.jxntv.account.repository;

import androidx.annotation.NonNull;

import com.jxntv.account.model.AvatarInfo;
import com.jxntv.account.model.LoginResponse;
import com.jxntv.account.model.RegionModel;
import com.jxntv.account.model.User;
import com.jxntv.account.model.annotation.PrivacyRange;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.request.AuthenticateIdCardRequest;
import com.jxntv.account.request.CancelAccountRequest;
import com.jxntv.account.request.CheckUserJoinCommunityRequest;
import com.jxntv.account.request.GetAvatarListRequest;
import com.jxntv.account.request.GetCurrentUserRequest;
import com.jxntv.account.request.IMPlatformAccountRequest;
import com.jxntv.account.request.LoginBySmsCodeRequest;
import com.jxntv.account.request.LogoutRequest;
import com.jxntv.account.request.ModifyUserRequest;
import com.jxntv.account.request.QuickLoginRequest;
import com.jxntv.account.request.RebindPhoneRequest;
import com.jxntv.account.request.RegionRequest;
import com.jxntv.account.request.SwitchAccountRequest;
import com.jxntv.base.model.anotation.Gender;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.engine.INetworkEngine;
import com.jxntv.network.observer.BaseResponseObserver;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.utils.LogUtils;

import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户仓库类
 *
 *
 * @since 2020-01-13 16:04
 */
public final class UserRepository extends BaseDataRepository {

  //<editor-fold desc="API">

  /**
   * 获取当前用户
   */
  @NonNull
  public Observable<User> getCurrentUser() {
    return getCurrentUser(true);
  }

  /**
   * 获取当前用户
   *
   * @param synchronize 是否同步
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
        //已登陆用户才请求数据
        return synchronize && UserManager.hasLoggedIn();
      }

      @Override
      protected BaseRequest<User> createRequest() {
        return new GetCurrentUserRequest();
      }
    }.asObservable().map(user -> {
      // 同步返回当前用户，保证所有页面的当前用户都是一个实例
      if (synchronize) {
        return UserManager.getCurrentUser();
      }
      return user;
    });
  }

  /**
   * 通过短线验证码登录
   *
   * @param phoneNumber 手机号
   * @param countyCode 国家代码
   * @param smsCode 短信验证码
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
   * 快速登录
   *
   * @param countyCode 国家代码
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
   * 切换账号
   *
   * @param phoneNumber 手机号
   * @param countyCode 国家代码
   * @param smsCode 短信验证码
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
   * 通过短线验证码登录
   *
   * @param phoneNumber 手机号
   * @param countyCode 国家代码
   * @param smsCode 短信验证码
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
   * 获取头像列表
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
   * 登出
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
   * 注销账户
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
   * 身份证认证
   *
   * @param idCardNumber 身份证号码
   * @param idCardFrontFileId 身份证正面图片文件 id
   * @param idCardBackFileId 身份证背面图片文件 id
   * @param idCardSelfFileId 身份证手持图片文件 id
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
   * 修改用户昵称
   *
   * @param nickname 昵称
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
   * 修改用户性别
   *
   * @param gender 性别
   * @param genderRange 性别范围
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
   * 修改用户生日
   *
   * @param birthday 生日
   * @param birthdayRange 生日范围
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
   * 修改用户地区
   *
   * @param provinceId 省份id
   * @param cityId 城市id
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
   * 修改头像列表
   *
   * @param avatarFileId 头像 id
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
   * 修改用户简介
   *
   * @param description 简介
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
   * 获取地区信息
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

  //<editor-fold desc="子类">
  private static abstract class UserResponseNetworkData extends NetworkData<User> {

    //<editor-fold desc="构造函数">

    /**
     * @param engine 网络引擎 {@link INetworkEngine}
     */
    private UserResponseNetworkData(INetworkEngine engine) {
      super(engine);
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">
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

    //<editor-fold desc="构造函数">

    /**
     * @param engine 网络引擎 {@link INetworkEngine}
     */
    private LoginResponseNetworkData(INetworkEngine engine) {
      super(engine);
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">

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
    //<editor-fold desc="构造函数">

    /**
     * @param engine 网络引擎 {@link INetworkEngine}
     */
    private DeleteUserNetworkData(INetworkEngine engine) {
      super(engine);
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">
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
   * 获取im聊天官方账号列表
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
