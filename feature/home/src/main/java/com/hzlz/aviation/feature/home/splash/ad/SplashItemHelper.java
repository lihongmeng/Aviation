package com.hzlz.aviation.feature.home.splash.ad;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.home.splash.db.SplashAdRepository;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;
import com.hzlz.aviation.kernel.base.plugin.IFileCreator;
import com.hzlz.aviation.kernel.base.utils.FileDownloadCallback;
import com.hzlz.aviation.kernel.base.utils.FileDownloadUtils;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.EncryptUtils;
import com.hzlz.aviation.library.util.FileUtils;
import com.hzlz.aviation.library.util.async.GlobalExecutor;

import java.io.File;
import java.util.List;

/**
 * 闪屏物料list辅助类
 */
public class SplashItemHelper {
    private static final boolean DEBUG = true;
    private static final String TAG = SplashItemHelper.class.getSimpleName();

    /** 闪屏物料文件存放地址 */
    private static final String FILE_SPLASH = "splash";
    /** 闪屏物料tmp后缀 */
    private static final String SPLASH_TEMP_SUFFIX = ".tmp";

    public static List<SplashAdEntity> getSplashModelListSync() {
        return SplashAdRepository.getInstance().loadSplashList();
    }

    /**
     * 移除splash list
     *
     * @param list 待移除的list
     */
    public static void deleteSplashList(List<SplashAdEntity> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        GlobalExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (SplashAdEntity entity : list) {
                    if (DEBUG) {
                        Log.d(TAG, "delete Item = " + entity.title + " / " + entity.extendId);
                    }
                    File f = getSplashDataFile(entity.md5, entity.adSourceUrl);
                    if (f != null && f.exists() && f.isFile()) {
                        f.delete();
                    }
                }
                int deleteCount = SplashAdRepository.getInstance().removeList(list);
                if (DEBUG) {
                    Log.d(TAG, "deleteItemCount = " + deleteCount);
                }
            }
        }, "update splash list", GlobalExecutor.PRIORITY_BACKGROUND);
    }

    /**
     * 增添list
     *
     * @param list 待处理的list
     */
    public static void addSplashList(List<SplashAdEntity> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        GlobalExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SplashAdRepository.getInstance().addList(list);
            }
        }, "update splash list", GlobalExecutor.PRIORITY_BACKGROUND);
    }

    /**
     * 获取splash 数据临时file
     *
     * @param name  数据名
     * @param index 数据序号
     * @return 临时File
     */
    public static File getSplashDataTempFile(@NonNull String name, int index) {
        return new File(getSplashDataBaseDir(), name + index + SPLASH_TEMP_SUFFIX);

    }

    /**
     * 下载splash data
     *
     * @param model     待下载的数据模型
     * @param timeout   是否有超时时间
     * @param handler   结果处理
     */
    public static void downloadSplashData(@NonNull SplashAdEntity model, long timeout, ItemDownloadHandler handler) {
        File temp = SplashItemHelper.getSplashDataTempFile(model.title, 0);
        if (temp.exists()) {
            temp.delete();
        }

        File itemData = SplashItemHelper.getSplashDataFile(model.md5, model.adSourceUrl);
        if (itemData == null) {
            handler.onThrowable(new Throwable("error item data"));
            return;
        }
        if (itemData.exists()) {
            handler.onFileExist();
            return;
        }

        FileDownloadUtils.downloadFile(model.adSourceUrl, timeout, new IFileCreator() {
            @NonNull
            @Override
            public File createFile() {
                FileUtils.createNewFileSafely(temp);
                return temp;
            }
        }, new FileDownloadCallback() {
            @Override
            public void onProgress(@NonNull String fileUrl, float progress) {
                // 此处暂不处理
            }

            @Override
            public void onSuccess(@NonNull String fileUrl) {
                temp.renameTo(itemData);
                if (handler != null) {
                    handler.onDownloadFinish();
                }
                temp.delete();
            }

            @Override
            public void onError(@NonNull String fileUrl, @NonNull Throwable throwable) {
                // 拉取失败，退化到使用本地资源
                temp.delete();
                if (handler != null) {
                    handler.onThrowable(throwable);
                }
            }

            @Override
            public void onCancel(@NonNull String fileUrl) {
                // 此处暂不处理
            }
        });
    }

    /**
     * 检查下载的文件是否可用
     *
     * @param file  待检查的文件
     * @param md5   数据md5
     * @return 数据是否可用
     */
    private static boolean checkDownloadFile(File file, String md5) {
        if (file == null || !file.exists() || TextUtils.isEmpty(md5)) {
            return false;
        }
//        String checkMd5 = FileUtils.toMd5(file, false);
//        return TextUtils.equals(checkMd5, md5);
        return true;

    }

    /**
     * 数据下载结果接口
     */
    public interface ItemDownloadHandler {

        /**
         * 错误处理
         *
         * @param throwable 数据处理
         */
        public void onThrowable(Throwable throwable);

        /**
         * 接收文件（未完成）
         *
         * @param aFloat 当前下载文件百分比
         */
        public void onAccept(Float aFloat);

        /**
         * 下载文件完成
         */
        public void onDownloadFinish();

        /**
         * 下载文件完成
         */
        public void onFileExist();

    }

    /**
     * 获取splash list base文件地址
     *
     * @return splash list base文件地址
     */
    private static File getSplashDataBaseDir() {
        return new File(GVideoRuntime.getAppContext().getFilesDir(), FILE_SPLASH);
    }

    /**
     * 获取splash 数据file
     *
     * @param md5   数据md5
     * @param url   数据url
     * @return 文件名
     */
    public static File getSplashDataFile(String md5, String url) {
        if (/*TextUtils.isEmpty(md5) || */TextUtils.isEmpty(url)) {
            return null;
        }
        //int lastDotIndex = url.lastIndexOf(".");
        //int lastIndex = url.lastIndexOf("/");
        //if (lastIndex < 0 || lastIndex >= lastDotIndex) {
        //    return null;
        //}

        String urlMd5 = EncryptUtils.encryptMD5ToString(url);
        return new File(getSplashDataBaseDir(), urlMd5);
    }

}
