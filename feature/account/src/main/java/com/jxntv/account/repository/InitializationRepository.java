package com.jxntv.account.repository;

import androidx.annotation.NonNull;

import com.jxntv.account.model.InitializationConfig;
import com.jxntv.account.presistence.H5UrlManager;
import com.jxntv.account.request.GetInitializationConfigRequest;
import com.jxntv.base.Constant;
import com.jxntv.base.datamanager.LoginDataManager;
import com.jxntv.base.datamanager.ThemeDataManager;
import com.jxntv.base.model.update.UpdateModel;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.base.plugin.IInitializationPRepository;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * 初始化仓库
 *
 *
 * @since 2020-03-07 21:14
 */
public final class InitializationRepository extends BaseDataRepository implements
    IInitializationPRepository {
  /**
   * 获取初始化配置
   */
  @NonNull
  public Observable<InitializationConfig> getInitializationConfig() {
    return new NetworkData<InitializationConfig>(mEngine) {

      @Override
      protected BaseRequest<InitializationConfig> createRequest() {
        return new GetInitializationConfigRequest();
      }

      @Override
      protected void saveData(InitializationConfig config) {
        ThemeDataManager themeDataManager = ThemeDataManager.getInstance();
        themeDataManager.setThemeColorSwitch(config.getThemeColorSwitch());
        themeDataManager.setThemeColorSwitchInteger(config.getThemeColorType());
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CHANGE_THEME_COLOR).post(null);

        LoginDataManager.getInstance().setQuickLoginKey(config.getQuickLoginKey());

        H5UrlManager.updateH5Urls(config);
        UpdateModel updateModel = config.getUpdateModel();
        GVideoEventBus.get(HomePlugin.EVENT_UPDATE_MODEL, UpdateModel.class).post(updateModel);

        List<InitializationConfig.PlaceholderConfig> placeholderList = config.getPlaceholderList();
        List<String> searchWordList = new ArrayList<>();
        if (placeholderList != null && placeholderList.size() > 0) {
          for (InitializationConfig.PlaceholderConfig placeholderConfig : placeholderList) {
            searchWordList.add(placeholderConfig.getPlaceholder());
          }
        }
        GVideoEventBus.get(HomePlugin.EVENT_HOME_SEARCH_WORD, List.class).post(searchWordList);
        int selectTime = config.getSelectVideoMaxTime();
        PluginManager.get(RecordPlugin.class).updateSelectVideoMaxTime(selectTime);
        PluginManager.get(RecordPlugin.class).updateAudioConfig(config.getInitConfig().getNlpAppKey(), config.getInitConfig().getAliToken());
        PluginManager.get(SharePlugin.class).setShareConfig(config.getWeiXin(), config.getQQ(), config.getWeiBo());
      }
    }.asObservable();
  }

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public Observable<Object> getInitializationConfigure() {
    return getInitializationConfig().map(config -> config);
  }
  //</editor-fold>
}
