package com.hzlz.aviation.feature.home.splash.binding;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.home.R;
import com.hzlz.aviation.feature.home.splash.ad.SplashItemHelper;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;
import com.hzlz.aviation.feature.home.splash.utils.SplashConstants;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

/**
 * 闪屏数据 data binding
 */
public class SplashDataBinding {

    /** 倒计时状态：普通 */
    public static final int COUNT_DOWN_STATUS_NORMAL = 0;
    /** 倒计时状态：停止 */
    public static final int COUNT_DOWN_STATUS_FINISH = 1;
    /** 倒计时状态：广告点击打断 */
    public static final int COUNT_DOWN_STATUS_INTERRUPT_AD = 2;
    /** 倒计时状态：跳过点击打断 */
    public static final int COUNT_DOWN_STATUS_INTERRUPT_CLICK = 3;
    /** 倒计时状态：跳过点击打断，开启详情页 */
    public static final int COUNT_DOWN_STATUS_INTERRUPT_CLICK_DETAILS = 4;

    @IntDef({COUNT_DOWN_STATUS_NORMAL, COUNT_DOWN_STATUS_FINISH, COUNT_DOWN_STATUS_INTERRUPT_AD, COUNT_DOWN_STATUS_INTERRUPT_CLICK,COUNT_DOWN_STATUS_INTERRUPT_CLICK_DETAILS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CountDownStatus {
    }

    /** 默认倒计时时间 */
    private static final int DEFAULT_COUNT_DOWN_TIME = 3;
    /** timer 间隔时间 */
    private static final long COUNT_DOWN_TIME_PERIOD = 1000L;

    /** 倒计时时间 */
    public ObservableField<Integer> countDownTime = new ObservableField<>();
    /** 倒计时状态 */
    public ObservableField<Integer> countDownStatus = new ObservableField<>();
    /** 倒计时显示状态 */
    public ObservableInt countDownVisible = new ObservableInt(View.INVISIBLE);
    /** 左上角广告角标可见性 */
    public ObservableInt adVisible = new ObservableInt(View.INVISIBLE);
    /** 下方logo可见行，半屏广告 */
    public ObservableInt logoVisible = new ObservableInt(View.GONE);
//    /** Splash底图 */
    public ObservableField<Drawable> splashBg = new ObservableField<>();

//    public CheckThreadLiveData<Drawable> splashBg = new CheckThreadLiveData<>();

    /** 本地倒计时用timer */
    private Timer mTimer;
    /** 本地倒计时时间 */
    private int mTimerCountdown;

    private SplashAdEntity mSplashModel;


    public void checkSplash() {
        Observable.create(new ObservableOnSubscribe<Drawable>() {
            @Override public void subscribe(ObservableEmitter<Drawable> e) throws Exception {
                List<SplashAdEntity> list = SplashItemHelper.getSplashModelListSync();
                if (list != null && !list.isEmpty()) {
                    for (SplashAdEntity entity : list) {
                        if (entity != null && entity.sourceType == SplashConstants.SPLASH_SOURCE_TYPE_SPLASH) {
                            File f = SplashItemHelper.getSplashDataFile(entity.md5, entity.adSourceUrl);
                            if (f != null && f.exists() && f.isFile()) {
                                Drawable bg = Drawable.createFromPath(f.getAbsolutePath());
                                e.onNext(bg);
                                return;
                            }
                        }
                    }
                }
                e.onError(null);
            }
        }).subscribeOn(GVideoSchedulers.IO_PRIORITY_USER).subscribe(
            new BaseViewModel.BaseGVideoResponseObserver<Drawable>() {
                @Override protected void onRequestData(Drawable drawable) {
                    splashBg.set(drawable);
                }

                @Override protected void onRequestError(Throwable throwable) {
                    Drawable bg = GVideoRuntime.getAppContext().getResources()
                        .getDrawable(R.drawable.home_splash);
                    splashBg.set(bg);
                }
            });
    }

    /**
     * 开始倒计时
     *
     * @param model  广告数据模型
     */
    public void startCountDown(@NonNull SplashAdEntity model) {
        adVisible.set(model.isAd > 0 ? View.VISIBLE : View.INVISIBLE);
        logoVisible.set(model.showStyle == SplashConstants.SPLASH_SHOW_STYLE_LOGO ? View.VISIBLE : View.GONE);

        mSplashModel = model;
        final int totalTime = model.durationSec > 0 ? model.durationSec : DEFAULT_COUNT_DOWN_TIME;
        final int countTime = model.countDownSec > totalTime ?
                totalTime : (model.countDownSec > 0 ? model.countDownSec : totalTime);
        cancelCountDown(COUNT_DOWN_STATUS_NORMAL);
        mTimer = new Timer();
        mTimerCountdown = countTime;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                countDownVisible.set(View.VISIBLE);
                if (mTimerCountdown == 0) {
                    cancelCountDown(COUNT_DOWN_STATUS_FINISH);
                } else {
                    countDownTime.set(mTimerCountdown);
                }
                mTimerCountdown--;
            }
        }, (totalTime - countTime) * COUNT_DOWN_TIME_PERIOD, COUNT_DOWN_TIME_PERIOD);

    }

    /**
     * 取消倒计时，更新倒计时状态
     *
     * @param statue    当前待更新的状态
     */
    public void cancelCountDown(@CountDownStatus int statue) {
        if (mTimer != null) {
            mTimer.cancel();
        }
        countDownStatus.set(statue);
    }

    /**
     * 跳过按钮点击事件
     */
    public void onClickSkip() {
        cancelCountDown(COUNT_DOWN_STATUS_INTERRUPT_CLICK);
    }

    public void onClickAd(View v) {
        if (mSplashModel == null) {
            return;
        }
        if (!NetworkUtils.isNetworkConnected()) {
            return;
        }
        String mediaId = mSplashModel.mediaId;
        if (!TextUtils.isEmpty(mSplashModel.adUrl)) {
            PluginManager.get(WebViewPlugin.class).startWebViewActivity(v.getContext(),
                    mSplashModel.adUrl, mSplashModel.title);

            statSplash(mSplashModel);
            cancelCountDown(COUNT_DOWN_STATUS_INTERRUPT_CLICK_DETAILS);
        } else if (!TextUtils.isEmpty(mediaId)) {
//            boolean shortVideo = mSplashModel.mediaType == MediaType.SHORT_VIDEO
//                || mSplashModel.mediaType == MediaType.SHORT_AUDIO;
//            Bundle extras = new Bundle();
//            extras.putString(VideoPlugin.EXTRA_FROM_PID, StatPid.SPLASH);
//            if (shortVideo) {
//                VideoModel vm = VideoModel.Builder.aVideoModel().withId(mediaId).build();
//                List<VideoModel> l = new ArrayList<>();
//                l.add(vm);
//                ShortVideoListModel model = ShortVideoListModel.Builder.aFeedModel().withList(l).build();
//                PluginManager.get(VideoPlugin.class).startShortVideoActivity(v.getContext(), model, extras);
//            } else {
//                VideoModel videoModel = VideoModel.Builder.aVideoModel().withId(mediaId).build();
//                PluginManager.get(VideoPlugin.class).startLongVideoActivity(v.getContext(), videoModel, extras);
//            }

            Bundle extras = new Bundle();
            extras.putString(VideoPlugin.EXTRA_FROM_PID, StatPid.SPLASH);
            VideoModel vm = VideoModel.Builder.aVideoModel().withId(mediaId).withMediaType(mSplashModel.mediaType).build();
            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(v.getContext(),vm,extras);

            statSplash(mSplashModel);

            cancelCountDown(COUNT_DOWN_STATUS_INTERRUPT_CLICK_DETAILS);
        }
    }
    private void statSplash(SplashAdEntity entity) {
        String extendId = entity.extendId;
        String extendName = entity.title;
        String isClick = TextUtils.isEmpty(entity.adUrl) ? "0" : "1";
        JsonObject ds = new JsonObject();
        ds.addProperty(StatConstants.DS_KEY_EXTEND_ID, extendId);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_NAME, extendName);
        ds.addProperty(StatConstants.DS_KEY_IS_CLICK, isClick);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
            .withEv(StatConstants.EV_SPLASH)
            .withDs(ds.toString())
            .withPid(StatPid.SPLASH)
            .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }
}
