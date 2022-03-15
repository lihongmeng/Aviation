package com.hzlz.aviation.feature.home.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.hzlz.aviation.feature.home.R;
import com.hzlz.aviation.feature.home.databinding.LayoutUpdateForceDialogBinding;
import com.hzlz.aviation.kernel.base.model.update.UpdateModel;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.widget.dialog.GVideoCenterDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.text.DecimalFormat;

public class UpdateForceDialog extends GVideoCenterDialog {
    private static final String DOWNLOAD_PATH_NAME = "update";
    private static final int MAX_PROGRESS = 100;
    /**
     * 持有的dataBind
     */
    private LayoutUpdateForceDialogBinding mLayoutBinding;
    private boolean isFinishDownLoad = false;
    private String apkLocalPath;
    private Context context;

    /**
     * 构造方法
     */
    public UpdateForceDialog(@NonNull Context context, UpdateModel updateModel) {
        super(context);
        this.context = context;
        mLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.layout_update_force_dialog, null, false);
        setContentView(mLayoutBinding.getRoot(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        setCanceledOnTouchOutside(false);
        mLayoutBinding.progressHorizontal.setMax(MAX_PROGRESS);
        mLayoutBinding.version.setText(String.format("(V%s)", updateModel.version));
        mLayoutBinding.content.setText(updateModel.releaseNotes);
        mLayoutBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinishDownLoad && !TextUtils.isEmpty(apkLocalPath)){
                    install(apkLocalPath);
                }else {
                    mLayoutBinding.confirm.setVisibility(View.GONE);
                    mLayoutBinding.updateText.setVisibility(View.VISIBLE);
                    mLayoutBinding.progressHorizontal.setVisibility(View.VISIBLE);
                    mLayoutBinding.percent.setVisibility(View.VISIBLE);
                    download(updateModel);
                    isFinishDownLoad = false;
                }
            }
        });
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    private void download(UpdateModel updateModel) {

        BaseDownloadTask downloadTask = FileDownloader.getImpl()
                .create(updateModel.downloadUrl)
                .setPath(updateModel.getAPKPath())
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        float progress = (float) soFarBytes / totalBytes;
                        progress = progress * MAX_PROGRESS;
                        if (progress > 0) {
                            mLayoutBinding.progressHorizontal.setProgress((int) progress);
                            DecimalFormat df = new DecimalFormat("##0.0");
                            mLayoutBinding.percent.setText(df.format(progress)+"%");
                            LogUtils.e(mLayoutBinding.percent.getText().toString());
                        }
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        mLayoutBinding.progressHorizontal.setProgress(MAX_PROGRESS);
                        mLayoutBinding.percent.setText("100%");
                        mLayoutBinding.percent.setVisibility(View.GONE);
                        mLayoutBinding.updateText.setVisibility(View.GONE);
                        mLayoutBinding.progressHorizontal.setVisibility(View.GONE);
                        mLayoutBinding.confirm.setText(R.string.install_bt_confirm);
                        mLayoutBinding.confirm.setVisibility(View.VISIBLE);
                        apkLocalPath = task.getPath();
                        install(task.getPath());
                        isFinishDownLoad = true;
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtils.e(e.getMessage());
                        new UpdateErrorDialog(context, view -> {
                            mLayoutBinding.confirm.performClick();
                        }).show();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {

                    }
                });
        downloadTask.start();

//    FileDownloadUtils.downloadFile(updateModel.downloadUrl, new IFileCreator() {
//      @NonNull @Override public File createFile() {
//        File base = new File(getContext().getFilesDir(), DOWNLOAD_PATH_NAME);
//        File file = new File(base, updateModel.bundleId + updateModel.version);
//        FileUtils.createNewFileSafely(file);
//        return file;
//      }
//    }, new FileDownloadCallback() {
//      @Override public void onProgress(@NonNull String fileUrl, float progress) {
//        if (TextUtils.equals(updateModel.downloadUrl, fileUrl)) {
//          int barProgress = (int) (progress * MAX_PROGRESS);
//          mLayoutBinding.progressHorizontal.setProgress(barProgress);
//        }
//      }
//
//      @Override public void onSuccess(@NonNull String fileUrl) {
//        if (TextUtils.equals(updateModel.downloadUrl, fileUrl)) {
//          install(updateModel);
//        }
//      }
//
//      @Override public void onError(@NonNull String fileUrl, @NonNull Throwable e) {
//
//      }
//
//      @Override public void onCancel(@NonNull String fileUrl) {
//
//      }
//    });
    }

    private void install(String fileUrl) {
        File apkFile = new File(fileUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getContext(), FilePlugin.AUTHORITY, apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        getContext().startActivity(intent);
    }

}
