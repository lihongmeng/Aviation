package com.jxntv.account.request;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.account.api.AccountAPI;
import com.jxntv.base.utils.StorageUtils;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;
import top.zibin.luban.Luban;

/**
 * 上传文件请求
 *
 * @since 2020-03-04 17:42
 */
public final class UploadFileRequest extends BaseRequest<Response<Void>> {

    private int MIN_SIZE = 1024;
    private String cachePath;

    //<editor-fold desc="常量">

    private static final String DEFAULT_FILE_CONTENT_TYPE = "application/octet-stream";

    //</editor-fold>

    //<editor-fold desc="属性">

    @Nullable
    private String mUploadUrl;

    @Nullable
    private RequestBody mFileRequestBody;

    //</editor-fold>

    //<editor-fold desc="设置参数">

    /**
     * 设置文件上传地址
     *
     * @param uploadUrl 上传地址
     */
    public void setUploadUrl(@NonNull String uploadUrl) {
        if (!URLUtil.isNetworkUrl(uploadUrl)) {
            throw new IllegalArgumentException();
        }
        mUploadUrl = uploadUrl;
    }

    /**
     * 设置文件
     *
     * @param fileObject  文件对象
     * @param contentType 文件 ContentType
     */
    public void setFile(@NonNull Object fileObject, @Nullable String contentType) {
        String ct =  (TextUtils.isEmpty(contentType)) ? DEFAULT_FILE_CONTENT_TYPE : contentType;
        String cache = StorageUtils.getCacheDirectory().getAbsolutePath();
        cachePath = null;
        // 文件路径
        if (fileObject instanceof String) {
            File file = new File((String) fileObject);
            if (!file.exists()) {
                throw new NullPointerException("file not exist");
            }
            try {
                List<File> files = Luban.with(GVideoRuntime.getAppContext()).ignoreBy(MIN_SIZE).setTargetDir(cache)
                        .load((String) fileObject).get();
                if (files!=null && files.size()>0){
                    file = files.get(0);
                    cachePath = file.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileRequestBody = RequestBody.create(MediaType.get(ct), file);
        }
        // 文件
        else if (fileObject instanceof File) {
            File file = (File) fileObject;
            if (!file.exists()) {
                throw new NullPointerException("file not exist");
            }
            try {
                List<File> files = Luban.with(GVideoRuntime.getAppContext()).ignoreBy(MIN_SIZE).setTargetDir(cache)
                        .load((File) fileObject).get();
                if (files!=null && files.size()>0){
                    file = files.get(0);
                    cachePath = file.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileRequestBody = RequestBody.create(MediaType.get(ct), file);
        }
        // 文件字节数组
        else if (fileObject instanceof byte[]) {
            mFileRequestBody = RequestBody.create(MediaType.get(ct), (byte[]) fileObject);
        }
        // 文件 uri
        else if (fileObject instanceof Uri) {
            InputStream is = null;
            Buffer buffer = null;
            try {
                is = GVideoRuntime.getApplication()
                        .getContentResolver()
                        .openInputStream((Uri) fileObject);
                if (is == null) {
                    throw new NullPointerException("file not exist");
                }
                buffer = new Buffer();
                mFileRequestBody = RequestBody.create(
                        MediaType.get(ct), buffer.readFrom(is).readByteArray()
                );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new NullPointerException("file not exist");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (buffer != null) {
                    buffer.close();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (mFileRequestBody == null) {
            throw new UnsupportedOperationException();
        }
    }

    public String getCachePath(){
        LogUtils.e(cachePath);
        return cachePath;
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    public Observable<Response<Void>> getObservable() {
        if (mUploadUrl == null || mFileRequestBody == null) {
            throw new NullPointerException();
        }
        return AccountAPI.Instance.get().uploadFile(mUploadUrl, mFileRequestBody, "omitAppHeader");
    }
    //</editor-fold>
}
