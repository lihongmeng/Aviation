package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.InitializationConfig;
import com.hzlz.aviation.feature.account.presistence.H5UrlManager;
import com.hzlz.aviation.feature.account.request.GetInitializationConfigRequest;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.datamanager.LoginDataManager;
import com.hzlz.aviation.kernel.base.datamanager.ThemeDataManager;
import com.hzlz.aviation.kernel.base.model.update.UpdateModel;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.base.plugin.IInitializationPRepository;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.library.ioc.PluginManager;

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
