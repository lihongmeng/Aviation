package com.jxntv.home.repository;

import androidx.annotation.NonNull;
import com.jxntv.base.model.update.UpdateModel;
import com.jxntv.base.sp.SharedPrefsWrapper;
import com.jxntv.base.utils.StorageUtils;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.LocalData;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.stat.StatUtils;

import java.io.File;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

/**
 * 更新数据仓库类
 */
public class UpdateRepository extends BaseDataRepository {
    /** 是否是测试状态 */
    private static final boolean IS_TEST = false;
    private static final long CHECK_INTERVAL = 24 * 60 * 60 * 1000L;
    private static final int MAX_DISMISS_TIMES = 3;
    private static final String PREF_KEY_LAST_CHECK_TIME = "last_check_time";
    private static final String PREF_KEY_UPDATE_DISMISS_TIMES = "update_dismiss_times";

    private SharedPrefsWrapper mSharedPref = new SharedPrefsWrapper("update");
    private boolean isInCheckInterval(String version) {
        long mLastCheckTime = mSharedPref.getLong(PREF_KEY_LAST_CHECK_TIME+version, 0);
        long currentCheckTime = System.currentTimeMillis();
        if (currentCheckTime - mLastCheckTime < CHECK_INTERVAL) {
            return true;
        }
        return false;
    }

    private boolean isMaxUpdateVersion(String version) {
        String localVersion = StatUtils.getAppVersionName(GVideoRuntime.getAppContext()).replace(".","");
        String updateVersion = version.replace(".","");

        return localVersion.compareTo(updateVersion) < 0;

    }

    private void saveCheckTime(String version) {
        long currentCheckTime = System.currentTimeMillis();
        mSharedPref.putLong(PREF_KEY_LAST_CHECK_TIME+version, currentCheckTime);
    }

    private boolean isMaxDismissTimes(UpdateModel model) {
        String key = PREF_KEY_UPDATE_DISMISS_TIMES + model.bundleId + model.version;
        int times = mSharedPref.getInt(key, 0);
        if (times >= MAX_DISMISS_TIMES) {
            return true;
        }
        return false;
    }
    public void saveDismissTime(UpdateModel model) {
        String key = PREF_KEY_UPDATE_DISMISS_TIMES + model.bundleId + model.version;
        int times = mSharedPref.getInt(key, 0);
        mSharedPref.putInt(key, ++times);
    }

    /**
     * 拉取更新信息
     */
    @NonNull
    public Observable<UpdateModel> getUpdateMsg(UpdateModel updateModel) {
        Observable<UpdateModel> observable = new LocalData<UpdateModel>(mEngine) {
            @Override protected UpdateModel loadFromLocal() {
                return updateModel;
            }
        }.asObservable();
        return mapModel(observable);
    }

    private Observable<UpdateModel> mapModel(Observable<UpdateModel> model) {
        return model.map(new Function<UpdateModel, UpdateModel>() {
            @Override public UpdateModel apply(UpdateModel updateModel) throws Exception {
                if (updateModel != null && updateModel.isValid()) {

                    if (!isMaxUpdateVersion(updateModel.version)){
                        return new UpdateModel();
                    }

                    if (updateModel.forceUpdate) {
                        return updateModel;
                    }

                    String filePath = updateModel.getAPKPath();
                    //如果本地有安装包
                    if (new File(filePath).exists()){
                        UpdateModel model = new UpdateModel();
                        model.bundleId = updateModel.bundleId +"_local";
                        model.version = updateModel.version;
                        if (!isMaxDismissTimes(model)) {
                            return updateModel;
                        }
                    }

                    if (isInCheckInterval(updateModel.version)) {
                        return new UpdateModel();
                    }
                    saveCheckTime(updateModel.version);
                    if (isMaxDismissTimes(updateModel)) {
                        return new UpdateModel();
                    }

                    return updateModel;
                }
                return new UpdateModel();
            }
        });
    }
}
