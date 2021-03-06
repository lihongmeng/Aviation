package com.hzlz.aviation.feature.home.update;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.hzlz.aviation.feature.home.HomeActivity;
import com.hzlz.aviation.feature.home.R;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

public class DownloadService extends Service {
  private static final String EXTRA_URL = "url";
  private static final String EXTRA_NAME = "name";
  private static final String DOWNLOAD_PATH_NAME = "update";

  private static final int MAX_PROGRESS = 100;
  private static final int NOTIFICATION_INTERVAL = 10;
  private int mNotificationId = 1000;
  private String notificationId = "download";
  private String notificationName = "downloadTask";

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    String url = intent.getStringExtra(EXTRA_URL);
    String name = intent.getStringExtra(EXTRA_NAME);
    startDownload(url, name);
    return super.onStartCommand(intent, flags, startId);
  }

  public static void start(Context c, String url, String name) {
    Intent intent = new Intent(c, DownloadService.class);
    intent.putExtra(EXTRA_URL, url);
    intent.putExtra(EXTRA_NAME, name);
    c.startService(intent);
  }

  private void startDownload(String url, String path) {

    BaseDownloadTask downloadTask = FileDownloader.getImpl().create(url)
            .setPath(path)
            .setListener(new FileDownloadListener() {
      @Override
      protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

      }

      @Override
      protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        float progress = (float) soFarBytes / totalBytes;
        progress = progress * MAX_PROGRESS;
        if (progress > 0) {
          getNotificationManager().notify(mNotificationId, getNotification(getString(R.string.update_title), (int) progress));
        }
      }

      @Override
      protected void completed(BaseDownloadTask task) {
        getNotificationManager().notify(mNotificationId, getNotification(getString(R.string.update_title), MAX_PROGRESS));
        install(task.getPath());
        getNotificationManager().cancel(mNotificationId);
        stopForeground(true);
      }

      @Override
      protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

      }

      @Override
      protected void error(BaseDownloadTask task, Throwable e) {
        getNotificationManager().notify(mNotificationId, getNotification(getString(R.string.update_title_error), 0));
      }

      @Override
      protected void warn(BaseDownloadTask task) {

      }
    });
    downloadTask.start();

//    FileDownloadUtils.downloadFile(url, new IFileCreator() {
//      @NonNull @Override public File createFile() {
//        File file = getFileFromUrl(name);
//        FileUtils.createNewFileSafely(file);
//        LogUtils.e(file.getAbsolutePath());
//        return file;
//      }
//    }, new FileDownloadCallback() {
//      private int mCurrentProgress;
//
//      @Override public void onProgress(@NonNull String fileUrl, float progress) {
//        int p = (int) (MAX_PROGRESS * progress);
//        if (TextUtils.equals(fileUrl, url)) {
//          if (p >= MAX_PROGRESS || p - mCurrentProgress > 0) {
//            getNotificationManager().notify(mNotificationId, getNotification(getString(R.string.update_title), p));
//            mCurrentProgress = p;
//          }
//        }
//      }
//
//      @Override public void onSuccess(@NonNull String fileUrl) {
//        if (TextUtils.equals(fileUrl, url)) {
//          install(name);
//          getNotificationManager().cancel(mNotificationId);
//          stopForeground(true);
//        }
//      }
//
//      @Override public void onError(@NonNull String fileUrl, @NonNull Throwable e) {
//        if (TextUtils.equals(fileUrl, url)) {
//          getNotificationManager().notify(mNotificationId,
//              getNotification(getString(R.string.update_title_error), 0));
//          getNotificationManager().cancel(mNotificationId);
//        }
//      }
//
//      @Override public void onCancel(@NonNull String fileUrl) {
//        if (TextUtils.equals(fileUrl, url)) {
//          getNotificationManager().cancel(mNotificationId);
//          stopForeground(true);
//        }
//      }
//    });

    getNotificationManager();
    startForeground(mNotificationId, getNotification(getString(R.string.update_title), 0));
  }

  private NotificationManager getNotificationManager() {
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(notificationId, notificationName,
              NotificationManager.IMPORTANCE_DEFAULT);
      channel.setSound(null,null);
      notificationManager.createNotificationChannel(channel);
    }
    return notificationManager;
  }

  /**
   * ?????????????????????
   * ?????????????????????????????? Notification
   *
   * Notification
   * ??? startForeground() ???????????????????????????
   * ??? NotificationManager.notify() ?????????????????????
   */
  private Notification getNotification(String title, int progress) {

    Intent intent = new Intent(this, HomeActivity.class);
    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

    //??????Notification ??? ?????????Builder??? ??????????????????set()??????
    // ????????????????????????builder.build()????????? ?????? Notification ?????????
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationId);
    builder.setSmallIcon(R.drawable.ic_launcher);
    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
    builder.setContentIntent(pi);
    builder.setContentTitle(title);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(notificationId);
    }

    if (progress > 0) {
      //???progress???????????????0???????????????????????????
      builder.setContentText(progress + "%");
      builder.setProgress(MAX_PROGRESS, progress, false);//??????????????????????????????????????????????????????????????????????????????????????????
    }

    return builder.build();
  }

  private void install(String path) {
    File apkFile = new File(path);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      Uri contentUri = FileProvider.getUriForFile(this, FilePlugin.AUTHORITY, apkFile);
      intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
    } else {
      intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
    }
    startActivity(intent);
  }
}
