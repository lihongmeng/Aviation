package com.hzlz.aviation.feature.home.splash.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.home.model.HomeTabInfo;
import com.hzlz.aviation.feature.home.splash.api.SplashApi;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;
import com.hzlz.aviation.feature.home.splash.request.HomeTabRequest;
import com.hzlz.aviation.feature.home.splash.request.InstallChannelRequest;
import com.hzlz.aviation.feature.home.splash.request.SplashRequest;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.LocalData;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 闪屏仓库类
 */
public class SplashRepository extends BaseDataRepository {

    /** 是否是测试状态 */
    private static final boolean IS_TEST = false;

    /**
     * 拉取splash物料列表
     */
    @NonNull
    public Observable<List<SplashAdEntity>> getSplashItemList() {

        if (IS_TEST) {
            return new LocalData<List<SplashAdEntity>>(mEngine) {
                @Override
                protected List<SplashAdEntity> loadFromLocal() {
                    return loadData();
                }
            }.asObservable();
        }

        return new NetworkData<List<SplashAdEntity>>(mEngine) {
            @Override
            protected BaseRequest<List<SplashAdEntity>> createRequest() {
                return new SplashRequest();
            }

            @Override
            protected void saveData(List<SplashAdEntity> infoList) {

              List<SplashAdEntity> filter = new ArrayList<>();
              if (infoList != null && !infoList.isEmpty()) {
                for (SplashAdEntity entity : infoList) {
                  if (entity != null && !TextUtils.isEmpty(entity.adSourceUrl)) {
                    filter.add(entity);
                  }
                }
              }
              infoList.clear();
              infoList.addAll(filter);
            }
        }.asObservable(GVideoSchedulers.IO_PRIORITY_USER);
    }

    /**
     * 临时获取数据
     */
    public List<SplashAdEntity> loadData() {

        List<SplashAdEntity> list = new ArrayList<>();
        list.add(createImgAdEntity());
        list.add(createVideoAdEntity());
        return list;
    }

    /**
     * 临时获取数据
     */
    public SplashAdEntity createImgAdEntity() {
        SplashAdEntity entity = new SplashAdEntity();
        entity.adSourceUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1579897046140&di=6fa790eb6f17afb97e6dcde0279b2a7a&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201510%2F05%2F20151005084011_ZPBUk.jpeg";
        entity.sourceType = 1;
        entity.startTime = new Date();
        entity.endTime = new Date(entity.startTime.getTime() + 100000000);
        entity.weight = 1;
        entity.durationSec = 5;
        entity.countDownSec= 3;
        entity.md5 = "md5";
        entity.title="test_img";
        return entity;
    }

    /**
     * 临时获取数据
     */
    public SplashAdEntity createVideoAdEntity() {
        SplashAdEntity entity = new SplashAdEntity();
        entity.adSourceUrl = "https://cdn.v.pdnews.cn/photo_video/ZGRjNmNkYzhjMzg4ZTAxMTdmN2E0ZWQ0NTFmNDdlOTI=.mp4";
        entity.sourceType = 2;
        entity.startTime = new Date();
        entity.endTime = new Date(entity.startTime.getTime() + 100000000);
        entity.weight = 1;
        entity.durationSec = 5;
        entity.countDownSec= 3;

        entity.md5 = "md5";
        entity.title="test_video";

        return entity;

    }


    /**
     * 上传安装渠道信息
     * @param channelId 渠道id
     */
    public Observable<String> uploadInstallChannel(@NonNull String channelId) {

        return new NetworkData<String>(mEngine) {

            @Override
            protected BaseRequest<String> createRequest() {
                InstallChannelRequest request = new InstallChannelRequest();
                request.setChannelId(channelId);
                return request;
            }

            @Override
            protected void saveData(String o) {

            }
        }.asObservable();
    }

    /**
     * 主页特殊菜单是否打开（临时）
     */
    public Observable<HomeTabInfo> getHomeTabsSwitch() {

        return new NetworkData<HomeTabInfo>(mEngine) {
            @Override
            protected BaseRequest<HomeTabInfo> createRequest() {
                return new HomeTabRequest();
            }

            @Override
            protected void saveData(HomeTabInfo homeTabModel) {

            }
        }.asObservable();
    }


    /**
     * 埋点，上报游客匿名id
     *
     */
    public void uploadAnonymousId() {

        new NetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return SplashApi.Instance.get().tourist(GVideoSensorDataManager.getInstance().getAnonymousId());
                    }
                };
            }

            @Override
            protected void saveData(Object o) {

            }
        }.asObservable().subscribe(new BaseResponseObserver<Object>() {
            @Override
            protected void onRequestData(Object o) {

            }

            @Override
            protected void onRequestError(Throwable throwable) {

            }
        });
    }
}
