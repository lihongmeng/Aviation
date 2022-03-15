package com.hzlz.aviation.kernel.base.utils;

import static android.os.Environment.MEDIA_MOUNTED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

import java.io.File;

/**
 *
 */
public final class StorageUtils {

    /**
     * 获取文件缓存路径
     *
     * @return
     */
    public static File getCacheDirectory() {
        return getCacheDirectory(GVideoRuntime.getAppContext(), "cache");
    }

    /**
     * 录音缓存路径
     */
    public static File getRecordDirectory() {
        return getFilesDirectory(GVideoRuntime.getAppContext(), "record");
    }

    /**
     * 录音视频路径
     */
    public static File getVideoDirectory() {
        return getFilesDirectory(GVideoRuntime.getAppContext(), "video");
    }

    /**
     * 配置文件路径
     */
    public static File getConfigDirectory() {
        return getFilesDirectory(GVideoRuntime.getAppContext(), "config");
    }

    /**
     * 获取相册路径
     */
    public static File getDCIMDirectory() {
        return getFilesDirectory(GVideoRuntime.getAppContext(), "DCIM");
    }

    private static File getCacheDirectory(Context context, String fileName) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = context.getExternalCacheDir();
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/" + fileName + "/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getFilesDirectory(Context context, String fileName) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appCacheDir = context.getExternalFilesDir(fileName);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/" + fileName + "/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * delete directory
     */
    public static boolean deleteFiles(File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory() && f.exists()) { // 判断是否存在
                    if (!f.delete()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * delete file
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else {
                String[] filePaths = file.list();

                if (filePaths != null) {
                    for (String path : filePaths) {
                        deleteFile(filePath + File.separator + path);
                    }
                }
                return file.delete();
            }
        }
        return true;
    }

    public static void install(String fileUrl, Activity activity) {
        File apkFile = new File(fileUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, FilePlugin.AUTHORITY, apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }
}
