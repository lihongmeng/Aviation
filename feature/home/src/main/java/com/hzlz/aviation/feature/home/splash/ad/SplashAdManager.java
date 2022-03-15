package com.hzlz.aviation.feature.home.splash.ad;

import static com.hzlz.aviation.library.util.async.GlobalExecutor.PRIORITY_BACKGROUND;

import com.hzlz.aviation.feature.home.splash.db.SplashAdRepository;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;
import com.hzlz.aviation.feature.home.splash.repository.SplashRepository;
import com.hzlz.aviation.feature.home.splash.utils.SplashConstants;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.library.util.FileUtils;
import com.hzlz.aviation.library.util.async.GlobalExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Function;

/**
 * 闪屏广告manager
 */
public class SplashAdManager {
    /** 没有任何广告时，缺省底图展示时间 */
    public static final int SPLASH_DEFAULT_TIME_OUT = 1700;
    /** 底图消失动画时长300ms */
    public static final int SPLASH_DEFAULT_ANIM_TIME = 0;

    /** 持有的单例 */
    private volatile static SplashAdManager sInstance = null;
    /** 拉取列表等待阈值 */
    private static final long FETCH_NET_LIST_TIME_LIMIT = 500;
    /** 拉取资源等待阈值 */
    private static final long FETCH_NET_SOURCE_TIME_LIMIT = 500;
    /** 是否已经成功拉取更新过网络数据 */
    private boolean hasFetchFromNet = false;
    /** 持有的数据仓库 */
    private SplashRepository mSplashRepository = new SplashRepository();

    /**
     * 获取单例
     */
    public static SplashAdManager getInstance() {
        if (sInstance == null) {
            synchronized (SplashAdManager.class) {
                if (sInstance == null) {
                    sInstance = new SplashAdManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 私有构造函数
     */
    private SplashAdManager() {

    }

    /**
     * 尝试缓存splash data, 主页面会下载具体素材
     */
    public void tryCacheSplashDataItem() {
        GlobalExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<SplashAdEntity> list = SplashItemHelper.getSplashModelListSync();
                SplashItemHelper.deleteSplashList(checkSplashModel(list));
                handleTryCacheSplashData(list);
            }
        }, "splash ad cache", PRIORITY_BACKGROUND);
    }

    /**
     * 检查存储信息是否失效，会把失效item从list中移除
     *
     * @param list 待检查和处理的list
     * @return 待删除的list
     */
    private List<SplashAdEntity> checkSplashModel(List<SplashAdEntity> list) {
        List<SplashAdEntity> removeList = new ArrayList<>();
        if (list == null || list.size() <= 0) {
            return removeList;
        }
        Date time = new Date();
        Iterator<SplashAdEntity> iterator = list.iterator();
        SplashAdEntity entity;
        while (iterator.hasNext()) {
            entity = iterator.next();
            if (entity == null) {
                iterator.remove();
                continue;
            }
            if (entity.sourceType == SplashConstants.SPLASH_SOURCE_TYPE_SPLASH) {
                continue;
            }
            if (entity.startTime.after(time) || entity.endTime.before(time)) {
                removeList.add(entity);
                iterator.remove();
            }
        }
       return removeList;
    }

    /**
     * 尝试缓存数据
     *
     * @param splashAdEntities 待尝试缓存数据
     */
    private void handleTryCacheSplashData(List<SplashAdEntity> splashAdEntities) {
        final List<SplashAdEntity> localList = new ArrayList<>();
        if (splashAdEntities != null) {
            localList.addAll(splashAdEntities);
        }
        if (!hasFetchFromNet) {
            // 尝试网络拉取列表
            mSplashRepository.getSplashItemList().subscribe(
                new BaseViewModel.BaseGVideoResponseObserver<List<SplashAdEntity>>() {
                    @Override protected void onRequestData(List<SplashAdEntity> splashModels) {
                        // 若拉取成功，则尝试下载对应的资源
                        if (splashModels != null) {
                            SplashItemHelper.addSplashList(splashModels);
                            localList.addAll(splashModels);
                        }
                        handleTryDownloadItem(localList);
                    }

                    @Override protected void onRequestError(Throwable throwable) {

                    }
                });
        } else {
            handleTryDownloadItem(localList);
        }
    }

    /**
     * 尝试下载数据
     *
     * @param list  待下载数据列表
     */
    private void handleTryDownloadItem(final List<SplashAdEntity> list) {
        if (list == null) {
            return;
        }
        for (SplashAdEntity model : list) {
            if (model.sourceType == SplashConstants.SPLASH_SOURCE_TYPE_VIDEO) {
                // 视频资源，如果此时为非wifi情况，则不尝试拉取
                if (!NetworkUtils.isWifiNetworkConnected()) {
                    continue;
                }
            }
            SplashItemHelper.downloadSplashData(model, 0,
                new SplashItemHelper.ItemDownloadHandler() {
                    @Override
                    public void onThrowable(Throwable throwable) {
                    }

                    @Override
                    public void onAccept(Float aFloat) {
                    }

                    @Override
                    public void onDownloadFinish() {
                        // 下载成功则加入本次查询列表

                    }

                    @Override
                    public void onFileExist() {

                    }
                });
        }
    }

    /**
     * 请求开屏广告接口，更新本地数据；
     * 500ms以内返回素材列表，更新本地素材列表，随即下载一个或者随机一个有效素材进行展示；
     * 超时，则从历史素材列表中随机一个有效素材进行展示；
     * @param listener
     */
    public void loadSplash(SplashItemReadyListener listener) {
        mSplashRepository.getSplashItemList().timeout(FETCH_NET_LIST_TIME_LIMIT, TimeUnit.MILLISECONDS)
            .observeOn(GVideoSchedulers.IO_PRIORITY_USER)
            .map(new Function<List<SplashAdEntity>, List<SplashAdEntity>>() {
                @Override public List<SplashAdEntity> apply(List<SplashAdEntity> networkList)
                    throws Exception {
                    return updateLocalFile(networkList);
                }
            })
            .observeOn(GVideoSchedulers.MAIN)
            .subscribe(new BaseViewModel.BaseGVideoResponseObserver<List<SplashAdEntity>>() {
                @Override protected void onRequestData(List<SplashAdEntity> splashModels) {
                    // 若拉取成功，则尝试下载对应的资源
                    hasFetchFromNet = true;
                    loadSplashItem(listener, splashModels);
                }

                @Override protected void onRequestError(Throwable throwable) {
                    // 若超时或拉取失败，则使用本地数据查找可用
                    loadSplashItem(listener, null);
                }
            });
    }

    /**
     * 以网络请求数据更新本地缓存素材列表数据
     * @param networkList
     * @return
     * @throws Exception
     */
    private List<SplashAdEntity> updateLocalFile(List<SplashAdEntity> networkList)
        throws Exception {
        List<SplashAdEntity> deleteList = new ArrayList<>();
        List<SplashAdEntity> filterList = new ArrayList<>();

        Set<String> networkSet = new HashSet<>();
        if (networkList != null && !networkList.isEmpty()) {
            for (SplashAdEntity ad : networkList) {
                networkSet.add(ad.getHashKey());
            }
        }
        List<SplashAdEntity> localWaitList = SplashItemHelper.getSplashModelListSync();
        if (localWaitList != null && !localWaitList.isEmpty()) {
            for (SplashAdEntity local : localWaitList) {
                if (!networkSet.contains(local.getHashKey())) {
                    deleteList.add(local);
                } else {
                    filterList.add(local);
                }
            }
        }
        //以network返回数据为准，移除无效数据或者adSourceUrl变化素材与数据库；
        if (!deleteList.isEmpty()) {
            for (SplashAdEntity ad : deleteList) {
                File f = SplashItemHelper.getSplashDataFile(ad.md5, ad.adSourceUrl);
                FileUtils.delete(f);
            }
            SplashAdRepository.getInstance().removeList(deleteList);
        }

        if (networkList != null) {
            networkList.removeAll(filterList);
            //同时将新返回数据保存到数据库中，供后续下载；
            SplashAdRepository.getInstance().addList(networkList);
        }

        return networkList;
    }

    /**
     * 选择一个素材下载或者随机一个历史有效素材展示
     * @param listener
     * @param splashModels
     */
    private void loadSplashItem(SplashItemReadyListener listener, List<SplashAdEntity> splashModels) {
        Observable.create(new ObservableOnSubscribe<List<SplashAdEntity>>() {
            @Override public void subscribe(ObservableEmitter<List<SplashAdEntity>> e)
                throws Exception {
                List<SplashAdEntity> localWaitList = loadLocalSplashAdList();
                e.onNext(localWaitList);
            }
        }).subscribeOn(GVideoSchedulers.IO_PRIORITY_USER).observeOn(GVideoSchedulers.MAIN)
            .subscribe(new BaseViewModel.BaseGVideoResponseObserver<List<SplashAdEntity>>() {
                @Override
                protected void onRequestData(List<SplashAdEntity> localWaitList) {
                    int currentTotalWeight = getWeightFromSplashList(localWaitList);
                    tryLoadSourceFromNet(listener, localWaitList, currentTotalWeight, splashModels);
                }
            });
    }

    /**
     * 获取本地有效素材列表
     * @return
     */
    private List<SplashAdEntity> loadLocalSplashAdList() {
        List<SplashAdEntity> modelList = SplashItemHelper.getSplashModelListSync();
        List<SplashAdEntity> waitList = new ArrayList<>();
        for (SplashAdEntity model : modelList) {
            if (checkLocalModelAvailable(model)) {
                waitList.add(model);
            }
        }
        return waitList;
    }

    private int getWeightFromSplashList(List<SplashAdEntity> list) {
        int weight = 0;
        if (list != null && !list.isEmpty()) {
            for (SplashAdEntity ad : list) {
                weight += ad.weight;
            }
        }
        return weight;
    }

    /**
     * 判断本地缓存数据是否可用
     *
     * @param model 待检查的数据
     * @return 是否可用
     */
    private boolean checkLocalModelAvailable(SplashAdEntity model) {
        if (model == null) {
            return false;
        }
        Date time = new Date();
        // 检查过期时间
        if (model.startTime.after(time) || model.endTime.before(time)) {
            return false;
        }
        File localDataFile = SplashItemHelper.getSplashDataFile(model.md5, model.adSourceUrl);
        return FileUtils.isFile(localDataFile);
    }

    /**
     * 尝试从网络拉取资源
     *
     * @param listener              获取结果监听
     * @param localWaitList         本地获取的list
     * @param currentTotalWeight    当前weight和，用于后续判断广告选取
     * @param netSplashModels       获取的网络列表
     */
    private void tryLoadSourceFromNet(final SplashItemReadyListener listener, final List<SplashAdEntity> localWaitList,
                                      final int currentTotalWeight, List<SplashAdEntity> netSplashModels) {
        // 若拉取列表为空，退化为查找本地缓存
        if (netSplashModels == null || netSplashModels.size() == 0) {
            findAvailableSplashModel(listener, localWaitList, currentTotalWeight);
            return;
        }

        // 尝试找到第一个图片广告拉取
        for (SplashAdEntity model : netSplashModels) {
            if (model != null && model.sourceType == SplashConstants.SPLASH_SOURCE_TYPE_IMG) {
                SplashItemHelper.downloadSplashData(model, FETCH_NET_SOURCE_TIME_LIMIT, new SplashItemHelper.ItemDownloadHandler() {
                    @Override
                    public void onFileExist() {
                        // 存在本地文件的bad case，直接查找本地
                        findAvailableSplashModel(listener, localWaitList, currentTotalWeight);
                    }

                    @Override
                    public void onThrowable(Throwable throwable) {
                        findAvailableSplashModel(listener, localWaitList, currentTotalWeight);
                    }

                    @Override
                    public void onAccept(Float aFloat) {
                        findAvailableSplashModel(listener, localWaitList, currentTotalWeight);
                    }

                    @Override
                    public void onDownloadFinish() {
                        // 下载成功则加入本次查询列表
                        localWaitList.add(model);
                        findAvailableSplashModel(listener, localWaitList, currentTotalWeight + model.weight);
                    }
                });
                return;
            }
        }
        findAvailableSplashModel(listener, localWaitList, currentTotalWeight);
    }

    /**
     * 尝试寻找可用资源
     *
     * @param listener       获取结果监听
     * @param localList      获取的list
     * @param totalWeight    当前weight和，用于判断广告选取
     */
    private void findAvailableSplashModel(SplashItemReadyListener listener,
                                          List<SplashAdEntity> localList, int totalWeight) {
        // 1. 若待选取列表不可用，则直接返回无可用数据
        if (localList == null || localList.size() == 0) {
            if (listener != null) {
                listener.onNoneAvailableItem();
            }
            return;
        }
        // 2. 从list中根据权重随机选取返回
        int curWeight = 0;
        int random = new Random().nextInt(totalWeight) + 1;
        for (SplashAdEntity model : localList) {
            curWeight += model.weight;
            if (curWeight >= random) {
                if (listener != null) {
                    listener.onItemReady(model,
                            SplashItemHelper.getSplashDataFile(model.md5, model.adSourceUrl));
                }
                return;
            }
        }
    }

    /**
     * 获取结果监听器
     */
    public interface SplashItemReadyListener {

        /**
         * 成功找到item
         */
        public void onItemReady(SplashAdEntity model, File itemFile);

        /**
         * 未找到可用item
         */
        public void onNoneAvailableItem();
    }

}
