package com.jxntv.base.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.base.R;
import com.jxntv.base.permission.PermissionCallback;
import com.jxntv.base.permission.PermissionManager;
import com.jxntv.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;

public class BitmapSaveLocalUtils {

    private Context context;
    private Bitmap bitmap;
    private ResultListener resultListener;

    public interface ResultListener{
        void onSuccess();
        void onFailed();
    }

    public BitmapSaveLocalUtils(Context context, Bitmap bitmap,ResultListener resultListener) {
        this.context = context;
        this.bitmap = bitmap;
        this.resultListener = resultListener;
    }

    public void saveImage() {
        PermissionManager.requestPermissions(context, new PermissionCallback() {
            @Override
            public void onPermissionGranted(@NonNull Context context) {
                saveImageReal();
            }

            @Override
            public void onPermissionDenied(@NonNull Context context, @Nullable String[] grantedPermissions, @NonNull String[] deniedPermission) {
                showToast(context.getString(R.string.refuse_permission_write_read));
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void showToast(@NonNull String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private void saveImageReal() {
        if (bitmap == null) {
            if(resultListener!=null){
                resultListener.onFailed();
            }
            return;
        }
        File imageRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (imageRoot == null) {
            imageRoot = new File("/storage/emulated/0/Pictures");
            if (!imageRoot.exists()) {
                imageRoot = StorageUtils.getDCIMDirectory();
            }
        }
        String pathUrl = imageRoot.getAbsolutePath() + "/JinShiPin";
        boolean createResult = FileUtils.createDir(pathUrl);
        if (!createResult) {
            if(resultListener!=null){
                resultListener.onFailed();
            }
            return;
        }
        File result = new File(imageRoot.getAbsolutePath() + "/" + +System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(result);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            if(resultListener!=null){
                resultListener.onFailed();
            }
            return;
        }

        //通知刷新相册
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(result);
        intent.setData(uri);
        context.sendBroadcast(intent);

        if(resultListener!=null){
            resultListener.onSuccess();
        }

    }

}


