package com.hzlz.aviation.feature.home.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieCompositionFactory;
import com.hzlz.aviation.feature.home.R;
import com.hzlz.aviation.feature.home.model.HomeTabInfo;
import com.hzlz.aviation.feature.home.splash.repository.SplashRepository;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;
import com.hzlz.aviation.kernel.base.utils.StorageUtils;
import com.hzlz.aviation.kernel.base.utils.ZipUtils;
import com.hzlz.aviation.kernel.base.view.GvideoLottieAnimationView;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.FileUtils;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/12/27
 * desc : 主页菜单工具类
 **/
public class HomeTabUtils {

    private static boolean openHomeSwitch = false;
    private static Integer openHomeSwitchInteger = null;

    private static HomeTabUtils utils;
    private static String filePath;
    private static SharedPrefsWrapper prefsWrapper;

    public static HomeTabUtils getInstance() {
        if (utils == null) {
            utils = new HomeTabUtils();
            filePath = StorageUtils.getConfigDirectory().getAbsolutePath() + File.separator + "menuTab";
            prefsWrapper = new SharedPrefsWrapper("HomeTabUtils");
            openHomeSwitch = prefsWrapper.getBoolean("openHomeSwitch", false);
            openHomeSwitchInteger = prefsWrapper.getInt("openHomeSwitchInteger", 0);
        }
        StorageUtils.getVideoDirectory();
        return utils;
    }

    public void setOpenHomeSwitch(boolean homeSwitch,Integer tabType) {
        openHomeSwitch = homeSwitch;
        openHomeSwitchInteger = tabType;
        prefsWrapper.putBoolean("openHomeSwitch", openHomeSwitch);
        prefsWrapper.putInt("openHomeSwitchInteger", openHomeSwitchInteger);
    }

    public void initRecordButton(GVideoImageView imageView) {
        if (openHomeSwitchInteger != null && openHomeSwitchInteger == 1) {
            imageView.setImageResource(R.drawable.home_tab_record_drawable2);
        } else {
            imageView.setImageResource(R.drawable.home_tab_record_drawable);
        }
    }


    /**
     * 设置菜单控件
     */
    public void setLottieRes(GvideoLottieAnimationView lottie, String downLoadUrl, int position) {
        //        if (!setResFormSDCard(lottie, downLoadUrl)) {
        //            downloadRes(downLoadUrl);
        //            lottie.setImageAssetsFolder("defaultTab/images"+ position);
        //            lottie.setAnimation("defaultTab/data.json"+position);
        //        lottie.setScale(ImageView.ScaleType.FIT_XY);
        if (openHomeSwitchInteger != null && openHomeSwitchInteger == 1) {
            try {
                String path = "configTab/tab_" + position;
                GVideoRuntime.getAppContext().getAssets().open(path + "/data.json");
                lottie.setImageAssetsFolder(path + "/images");
                lottie.setAnimation(path + "/data.json");
            } catch (IOException e) {
                e.printStackTrace();
                lottie.setImageAssetsFolder("defaultTab/tab_" + position + "/images");
                lottie.setAnimation("defaultTab/tab_" + position + "/data.json");
            }
        }else {
            lottie.setImageAssetsFolder("defaultTab/tab_" + position + "/images");
            lottie.setAnimation("defaultTab/tab_" + position + "/data.json");
        }
    }

    /**
     * 从sdk卡中获取显示资源
     */
    private boolean setResFormSDCard(LottieAnimationView lottie, String downloadUrl) {
        FileInputStream fis = null;
        String json = prefsWrapper.getString(downloadUrl, "") + File.separator + "data0.json";
        String images = prefsWrapper.getString(downloadUrl, "") + File.separator + "images";
        File JSON_FILE = new File(json);
        File IMAGES_FILES = new File(images);
        if (JSON_FILE.exists()) {
            try {
                fis = new FileInputStream(JSON_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (fis == null || ! IMAGES_FILES.exists()) {
            return false;
        }

        final String absolutePath = IMAGES_FILES.getAbsolutePath();
        // 设置动画文件夹代理类
        lottie.setImageAssetDelegate(asset -> {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = true;
            opts.inDensity = ScreenUtils.getScreenDensityDpi(GVideoRuntime.getAppContext());
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(absolutePath + File.separator + asset.getFileName(), opts);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        });

        LottieCompositionFactory.fromJsonInputStream(fis, json).addListener(composition -> {
            lottie.setComposition(composition);
            lottie.setFrame(1);
        });
        return true;
    }

    /**
     * 下载zip资源文件,并解压到指定目录
     */
    private void downloadRes(String downloadUrl) {
        String path = filePath + File.separator + System.currentTimeMillis() + ".zip";

        FileDownloader.getImpl().create(downloadUrl).setPath(path).setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                String unzipPath = filePath + File.separator + "res_" + System.currentTimeMillis();
                try {
                    List<File> files = ZipUtils.unzipFile(path, path);
                    if (files != null && files.size() > 0) {
                        prefsWrapper.putString(downloadUrl, unzipPath);
                    }
                    FileUtils.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {

            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        }).start();

    }

    public void initOpenHomeSwitch(){

        new SplashRepository().getHomeTabsSwitch().subscribe(new BaseResponseObserver<HomeTabInfo>() {
            @Override
            protected void onRequestData(HomeTabInfo tabModel) {
                setOpenHomeSwitch(tabModel.isTabSwitch(),tabModel.getTabType());
                if (listener!=null){
                    listener.init();
                }
            }

            @Override
            protected void onRequestError(Throwable throwable) {
            }
        });
    }

    public TabListener listener;
    public void setTabListener(TabListener listener){
        this.listener = listener;
    }
    public interface TabListener{
        void init();
    }


}
