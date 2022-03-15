package com.hzlz.aviation.kernel.network.repository;

import com.hzlz.aviation.kernel.network.engine.INetworkEngine;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Function;

/**
 * 有边界的数据，统一处理数据获取<br/>
 * <br/>
 * 获取逻辑如下：
 * <ul>
 * <li>先从本地读取（可以是数据也可以是本地文件）</li>
 * <li>判断是否取消从网络获取，如果本地没有数据，默认从网络获取</li>
 * <li>从网路获取完数据进行数据保存</li>
 * </ul>
 *
 * @param <T> 数据模型泛型
 * @since 2020-01-03 20:49
 */
public abstract class BoundData<T> {
    //<editor-fold desc="常量">
    // 标记需要从网络获取数据
    private static final Object FETCH_UP = new Object();
    // 网络引擎，用于网络请求
    private INetworkEngine mEngine;
    //</editor-fold>

    //<editor-fold desc="构造函数">

    /**
     * @param engine 网络引擎 {@link INetworkEngine}
     */
    public BoundData(INetworkEngine engine) {
        mEngine = engine;
    }
    //</editor-fold>

    //<editor-fold desc="抽象方法">

    /**
     * 从本地加载数据
     *
     * @return 数据模型
     */
    protected abstract T loadFromLocal();

    /**
     * 是否需要从网络获取
     *
     * @param t 数据模型
     * @return true : 需要从网络获取
     */
    protected abstract boolean shouldFetchUp(T t);

    /**
     * 创建请求 {@link BaseRequest}
     *
     * @return 请求
     */
    protected abstract BaseRequest<T> createRequest();

    /**
     * 保存从网络获取的数据
     *
     * @param t t 从网络获取的数据
     */
    protected abstract void saveData(T t);

    //</editor-fold>

    //<editor-fold desc="内部 API">

    /**
     * 当本地数据为 null 的是否从网络获取数据
     *
     * @return true : 从网络获取数据 ; 不从网络获取数据
     */
    protected boolean fetchUpWhenLocalDataIsNull() {
        return true;
    }

    /**
     * 在主线程处理数据
     *
     * @param t t 数据模型
     */
    protected void processDataOnMainThread(T t) {

    }
    //</editor-fold>

    //<editor-fold desc="API">

    /**
     * 将 BoundData {@link BoundData} 转换成 Observable {@link Observable}
     *
     * @return Observable 实例
     */
    @SuppressWarnings("unchecked")
    public Observable<T> asObservable() {
        return asObservable(GVideoSchedulers.IO_PRIORITY_BACKGROUND);
    }

    /**
     * 将 BoundData {@link BoundData} 转换成 Observable {@link Observable}
     *
     * @param subscribeScheduler 线程调取器
     * @return Observable 实例
     */
    public Observable<T> asObservable(Scheduler subscribeScheduler) {
        return Observable
                .create(emitter -> {
                    T data = loadFromLocal();
                    if (data != null) {
                        // 发送从本地获取的数据
                        emitter.onNext(data);
                        // 判断是否需要从网络获取数据
                        if (shouldFetchUp(data)) {
                            emitter.onNext(FETCH_UP);
                        }
                    } else {
                        // 如果本地没有数据判断是否需要从网络获取数据
                        if (fetchUpWhenLocalDataIsNull()) {
                            emitter.onNext(FETCH_UP);
                        }
                    }
                    // 发送完成
                    emitter.onComplete();
                })
                // 切换至子线程
                .subscribeOn(subscribeScheduler)
                .flatMap((Function<Object, ObservableSource<T>>) object -> {
                    // 判断上游传递的数据是否为从网络获取数据
                    if (!object.equals(FETCH_UP)) {
                        return Observable.just((T) object);
                    }
                    // 创建请求并执行请求
                    BaseRequest<T> request = createRequest();
                    return mEngine.executeRequest(request).map(t -> {
                        // 保存数据
                        saveData(t);
                        return t;
                    });
                })
                // 切换至主线程
                .observeOn(AndroidSchedulers.mainThread())
                .map(t -> {
                    processDataOnMainThread(t);
                    return t;
                });
    }
    //</editor-fold>
}
