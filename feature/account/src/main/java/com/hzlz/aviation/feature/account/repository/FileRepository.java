package com.hzlz.aviation.feature.account.repository;

import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.model.GVideoFile;
import com.hzlz.aviation.feature.account.request.CreateFileRequest;
import com.hzlz.aviation.feature.account.request.DownloadFileRequest;
import com.hzlz.aviation.feature.account.request.HeadFileRequest;
import com.hzlz.aviation.feature.account.request.UploadFileRequest;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.callback.StringListResultListener;
import com.hzlz.aviation.kernel.base.plugin.IFileCreator;
import com.hzlz.aviation.kernel.base.plugin.IFileRepository;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 文件仓库类
 *
 * @since 2020-03-04 17:20
 */
public final class FileRepository extends BaseDataRepository implements IFileRepository {

    interface ResultListListener {
        void onSuccess(List<String> result);
    }

    //<editor-fold desc="方法实现">

    @NonNull
    @Override
    public Observable<String> doUploadImageFile(int fileBusinessType, @NonNull Uri fileUri,
                                                @Nullable String contentType) {
        return doUploadImageFile(fileBusinessType, fileUri, contentType, GVideoSchedulers.IO_PRIORITY_USER);
    }

    @NonNull
    @Override
    public Observable<String> doUploadImageFile(int fileBusinessType, @NonNull String filePath,
                                                @Nullable String contentType) {
        return doUploadImageFile(fileBusinessType, filePath, contentType, GVideoSchedulers.IO_PRIORITY_USER);
    }

    @Override
    public void batchUpLoadFile(int fileBusinessType, List<String> result, @NonNull List<String> filePathList,
                                @Nullable String contentType, StringListResultListener resultListener) {
        doUploadImageFile(fileBusinessType, filePathList.get(result.size()), contentType,
                GVideoSchedulers.IO_PRIORITY_USER
        ).subscribe(new BaseViewModel.BaseGVideoResponseObserver<String>() {
            @Override
            protected void onRequestData(String imgId) {
                result.add(imgId);
                if (result.size() < filePathList.size()) {
                    batchUpLoadFile(fileBusinessType, result, filePathList, contentType, resultListener);
                    return;
                }
                resultListener.result(result);
            }

            @Override
            protected void onRequestError(Throwable throwable) {
                result.add("");
                if (result.size() < filePathList.size()) {
                    batchUpLoadFile(fileBusinessType, result, filePathList, contentType, resultListener);
                    return;
                }
                resultListener.result(result);
            }
        });
    }

    @NonNull
    @Override
    public Observable<String> doUploadImageFile(int fileBusinessType, @NonNull File file, Scheduler scheduler) {
        return doUploadImageFile(fileBusinessType, file, null, scheduler);
    }

    @NonNull
    @Override
    public Observable<String> doUploadImageFile(int fileBusinessType, @NonNull File file,
                                                @Nullable String contentType) {
        return doUploadImageFile(fileBusinessType, file, contentType, GVideoSchedulers.IO_PRIORITY_USER
        );
    }

    @NonNull
    @Override
    public Observable<String> doUploadImageFile(int fileBusinessType, @NonNull byte[] fileBytes,
                                                @NonNull String contentType) {
        return doUploadImageFile(fileBusinessType, fileBytes, contentType, GVideoSchedulers.IO_PRIORITY_USER);
    }

    @NonNull
    @Override
    public Observable<Float> downloadFile(@NonNull String fileUrl, @NonNull IFileCreator creator) {
        return doDownloadFile(fileUrl, creator);
    }
    //</editor-fold>

    //<editor-fold desc="内部方法">

    /**
     * 上传文件
     *
     * @param fileBusinessType 文件业务类型
     * @param fileObject       文件对象（文件路径，文件，文件uri, 文件字节数组等）
     * @param contentType      文件 ContentType
     */
    @NonNull
    private Observable<String> doUploadImageFile(int fileBusinessType, @NonNull Object fileObject,
                                                 @Nullable String contentType) {
        return doUploadImageFile(fileBusinessType, fileObject, contentType, GVideoSchedulers.IO_PRIORITY_BACKGROUND
        );
    }

    /**
     * 上传图片文件
     *
     * @param fileBusinessType   文件业务类型
     * @param fileObject         文件对象（文件路径，文件，文件uri, 文件字节数组等）
     * @param contentType        文件 ContentType
     * @param subscribeScheduler 选择对应的调度器
     */
    @NonNull
    private Observable<String> doUploadImageFile(int fileBusinessType, @NonNull Object fileObject,
                                                 @Nullable String contentType, Scheduler subscribeScheduler) {
        // 创建文件
        return new OneTimeNetworkData<GVideoFile>(mEngine) {
            @Override
            protected BaseRequest<GVideoFile> createRequest() {
                CreateFileRequest request = new CreateFileRequest();
                request.setFileBusinessType(fileBusinessType);
                return request;
            }
        }.asObservable(subscribeScheduler)
                // 上传文件
                .flatMap((Function<GVideoFile, ObservableSource<String>>)
                        gVideoFile -> {
                            return new OneTimeNetworkData<Response<Void>>(mEngine) {
                                @Override
                                protected BaseRequest<Response<Void>> createRequest() {
                                    UploadFileRequest request = new UploadFileRequest();
                                    request.setUploadUrl(gVideoFile.getUploadUrl());
                                    request.setFile(fileObject, contentType);
                                    return request;
                                }
                            }.asObservable().map(response -> {
                                // 判断请求是否成功
                                if (response.isSuccessful()) {
                                    return gVideoFile.getId();
                                }
                                ResponseBody errorBody = response.errorBody();
                                String message = response.message();
                                if (errorBody != null) {
                                    message = errorBody.string();
                                }
                                throw new IOException(message);
                            });
                        });
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件 url
     * @param creator 文件创建器
     */
    @SuppressWarnings("ConstantConditions")
    private Observable<Float> doDownloadFile(@NonNull String fileUrl, @NonNull IFileCreator creator) {
        // Head 请求判断文件长度
        return new OneTimeNetworkData<Response<Void>>(mEngine) {
            @Override
            protected BaseRequest<Response<Void>> createRequest() {
                HeadFileRequest request = new HeadFileRequest();
                request.setUrl(fileUrl);
                return request;
            }
        }.asObservable()
                .subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND)
                .flatMap((Function<Response<Void>, ObservableSource<Float>>) headResponse -> {
                    // 判断请求是否成功
                    if (!headResponse.isSuccessful()) {
                        String message = headResponse.message();
                        ResponseBody errorBody = headResponse.errorBody();
                        if (errorBody != null) {
                            message = errorBody.string();
                        }
                        throw new IOException(message);
                    }
                    // 获取文件长度
                    Headers headers = headResponse.headers();
                    final long contentLength = Long.parseLong(headers.get("Content-Length"));
                    // 获取文件长度和写入长度
                    final File file = creator.createFile();
                    final DownloadFileCounter counter = new DownloadFileCounter(file);
                    long fileLength = file.length();
                    long writtenLength = counter.readWrittenLength();
                    // 判断写入长度是否和 Content-Length 大小相等
                    // 因为使用的 RandomAccessFile, 所以只要调用了 setLength 就会导致 fileLength == contentLength
                    // 这里相当于简易判断文件是否是同一个文件（不保证一定是同一个文件）
                    if (fileLength != 0 && fileLength == contentLength && writtenLength == contentLength) {
                        // 关闭计数器
                        counter.close();
                        return Observable.create(emitter -> {
                            // 进度 100
                            emitter.onNext(1.0f);
                            // 通知下载结束
                            emitter.onComplete();
                        });
                    }
                    // 下载文件
                    return new OneTimeNetworkData<ResponseBody>(mEngine) {
                        @Override
                        protected BaseRequest<ResponseBody> createRequest() {
                            // 计算 RangeStart
                            long fl = file.length();
                            long wl = 0;
                            try {
                                wl = counter.readWrittenLength();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            long rangeStart = fl == 0 ? 0 : fl != contentLength ? 0 : wl;
                            if (rangeStart != wl) {
                                try {
                                    counter.writeReadLength(rangeStart);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            // 创建请求
                            DownloadFileRequest request = new DownloadFileRequest();
                            request.setUrl(fileUrl);
                            request.setRangeStart(rangeStart);
                            return request;
                        }
                    }.asObservable()
                            .subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND)
                            .flatMap((Function<ResponseBody, ObservableSource<Float>>)
                                    downloadResponse -> Observable.create(emitter -> {
                                        // 获取已写入长度
                                        long seekPosition = counter.readWrittenLength();
                                        // 随机写文件
                                        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                                        randomAccessFile.setLength(contentLength);
                                        randomAccessFile.seek(seekPosition);
                                        // 获取文件字节
                                        byte[] bytes = new byte[2048];
                                        InputStream is = downloadResponse.byteStream();
                                        int readCount;
                                        long writeCount = seekPosition;
                                        while ((readCount = is.read(bytes)) != -1) {
                                            // 写入文件
                                            randomAccessFile.write(bytes, 0, readCount);
                                            // 计算进度
                                            writeCount += readCount;
                                            float progress = (float) writeCount / contentLength;
                                            emitter.onNext(progress);
                                            // 更新写入长度
                                            counter.writeReadLength(writeCount);
                                        }
                                        // 关闭计数器
                                        counter.close();
                                        // 通知下载结束
                                        emitter.onComplete();
                                    }))
                            .doOnError(throwable -> counter.close())
                            .doOnComplete(counter::close)
                            .observeOn(GVideoSchedulers.MAIN);
                });
    }

    /**
     * 下载文件计数
     */
    private static final class DownloadFileCounter {
        //<editor-fold desc="属性">
        @NonNull
        private RandomAccessFile mCounterFile;
        //</editor-fold>

        //<editor-fold desc="构造函数">
        @SuppressWarnings({"CharsetObjectCanBeUsed", "ResultOfMethodCallIgnored"})
        private DownloadFileCounter(@NonNull File downloadFile) throws IOException {
            if (!downloadFile.exists()) {
                downloadFile.createNewFile();
            }
            String counterName = "." + Base64.encodeToString(
                    downloadFile.getName().getBytes("UTF-8"), Base64.URL_SAFE
            ).trim();
            File counterFile = new File(downloadFile.getParentFile(), counterName);
            if (!counterFile.exists()) {
                counterFile.createNewFile();
            }
            mCounterFile = new RandomAccessFile(counterFile, "rw");
        }
        //</editor-fold>

        //<editor-fold desc="API">

        /**
         * 读取已写的长度
         *
         * @return 已写的长度
         * @throws IOException 异常
         */
        private long readWrittenLength() throws IOException {
            byte[] bytes = new byte[8];
            mCounterFile.seek(0);
            int read = mCounterFile.read(bytes);
            if (read != -1) {
                return Long.parseLong(new String(bytes, 0, read));
            }
            return 0;
        }

        /**
         * 写入读入的长度
         *
         * @param length 读入的长度
         * @throws IOException 异常
         */
        private void writeReadLength(long length) throws IOException {
            byte[] bytes = String.valueOf(length).getBytes();
            mCounterFile.setLength(bytes.length);
            mCounterFile.seek(0);
            mCounterFile.write(bytes);
        }

        /**
         * 关闭
         */
        private void close() throws IOException {
            mCounterFile.close();
        }
        //</editor-fold>
    }
    //</editor-fold>
}
