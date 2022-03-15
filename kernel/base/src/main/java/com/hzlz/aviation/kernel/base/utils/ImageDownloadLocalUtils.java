package com.hzlz.aviation.kernel.base.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoBottomSheetItemDialog;

import java.io.File;

/**
 * @author huangwei
 * date : 2021/6/19
 * desc : 图片保存到本地
 **/
public class ImageDownloadLocalUtils {

    private Context context;
    private String url;
    private GVideoBottomSheetItemDialog dialog;

    public ImageDownloadLocalUtils(Context context) {
        this.context = context;
    }

    public ImageDownloadLocalUtils(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    public void setDownLoadUrl(String url){
        this.url = url;
    }

    public void showSaveDialog() {

        if (dialog == null) {
            dialog = new GVideoBottomSheetItemDialog.Builder(context)
                    .addItem("保存图片")
                    .cancel(R.string.dialog_cancel)
                    .itemSelectedListener((dialog, position) -> checkStoragePermission())
                    .build();
        }
        dialog.show();
    }


    private void checkStoragePermission(){

        PermissionManager.requestPermissions(context, new PermissionCallback() {
            @Override
            public void onPermissionGranted(@NonNull Context context) {
                downLoadFile();
            }

            @Override
            public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                showToast(context.getString(R.string.refuse_permission_write_read));
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    private void downLoadFile(){

        if (TextUtils.isEmpty(url)){
            return;
        }

        FileDownloadUtils.downloadFile(url, () -> {
            File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (dcim==null){
                dcim = new File("/storage/emulated/0/Pictures");
                if (!dcim.exists()) {
                    dcim = StorageUtils.getDCIMDirectory();
                }
            }
            String pathUrl = dcim.getAbsolutePath()+"/JinShiPin";
            createDir(pathUrl);
            File file = new File(dcim.getAbsolutePath()+ "/"+ +System.currentTimeMillis()+".jpg");
            //通知刷新相册
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
            return file;
        }, new FileDownloadCallback() {
            @Override
            public void onProgress(@NonNull String fileUrl, float progress) {

            }

            @Override
            public void onSuccess(@NonNull String fileUrl) {
                showToast("图片已保存至相册");
            }

            @Override
            public void onError(@NonNull String fileUrl, @NonNull Throwable e) {
                LogUtils.e(e.getMessage());
            }

            @Override
            public void onCancel(@NonNull String fileUrl) {

            }
        });
    }


    private int createDir(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return 1;
        }
        if (!dirPath.endsWith(File.separator)) {
            //不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
            dirPath = dirPath + File.separator;
        }
        //创建文件夹
        if (dir.mkdirs()) {
            return 0;
        }
        return -1;
    }

    private void showToast(@NonNull String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


}


