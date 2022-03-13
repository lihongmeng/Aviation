package com.jxntv.base.plugin;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.base.callback.StringListResultListener;

import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;

/**
 * 文件仓库接口<br/>
 * 泛型 String 表示文件 id
 *
 * @since 2020-03-04 17:00
 */
public interface IFileRepository {

    // C端用户头像
    int FILE_BUSINESS_TYPE_USER_HEADER_PIC = 1;

    // 身份证头像
    int FILE_BUSINESS_TYPE_ID_CARD_HEADER_PIC = 2;

    // 认证号头像
    int FILE_BUSINESS_TYPE_VERIFY_HEADER_PIC = 3;

    // 认证号背景
    int FILE_BUSINESS_TYPE_VERIFY_BACKGROUND = 4;

    // 运营素材展示图、音频、视频
    int FILE_BUSINESS_TYPE_OPERATE_DISPLAY = 5;

    // 音视频封面图
    int FILE_BUSINESS_TYPE_MEDIA_COVER = 6;

    // 直播封面
    int FILE_BUSINESS_TYPE_AUDIO = 7;

    // 图文图片
    int FILE_BUSINESS_TYPE_IMAGE = 8;

    //<editor-fold desc="上传文件">

    /**
     * 上传文件
     *
     * @param fileBusinessType 文件业务类型
     * @param fileUri          文件 uri
     * @param contentType      文件 ContentType
     */
    @NonNull
    Observable<String> doUploadImageFile(
            int fileBusinessType,
            @NonNull Uri fileUri,
            @Nullable String contentType
    );

    /**
     * 上传文件
     *
     * @param fileBusinessType 文件业务类型
     * @param filePath         文件路径
     * @param contentType      文件 ContentType
     */
    @NonNull
    Observable<String> doUploadImageFile(
            int fileBusinessType,
            @NonNull String filePath,
            @Nullable String contentType
    );

    /**
     * 批量上传文件
     *
     * @param fileBusinessType 文件业务类型
     * @param filePathList     文件路径集合
     * @param contentType      文件 ContentType
     */
    void batchUpLoadFile(
            int fileBusinessType,
            List<String> result,
            @NonNull List<String> filePathList,
            @Nullable String contentType,
            StringListResultListener resultListener
    );

    /**
     * 上传图片文件
     *
     * @param fileBusinessType 文件业务类型
     * @param file             文件
     * @param scheduler        调度器
     */
    @NonNull
    Observable<String> doUploadImageFile(
            int fileBusinessType,
            @NonNull File file,
            Scheduler scheduler
    );

    /**
     * 上传文件
     *
     * @param fileBusinessType 文件业务类型
     * @param file             文件
     * @param contentType      文件 ContentType
     */
    @NonNull
    Observable<String> doUploadImageFile(
            int fileBusinessType,
            @NonNull File file,
            @Nullable String contentType
    );

    /**
     * 上传文件
     *
     * @param fileBusinessType 文件业务类型
     * @param fileBytes        文件字节
     * @param contentType      文件 ContentType
     */
    @NonNull
    Observable<String> doUploadImageFile(
            int fileBusinessType,
            @NonNull byte[] fileBytes,
            @NonNull String contentType
    );

    //</editor-fold>

    //<editor-fold desc="文件下载">

    /**
     * 下载文件<br/>
     * 泛型为下载进度 : [0,1]
     *
     * @param fileUrl 文件 url
     * @param creator 文件创建器
     */
    @NonNull
    Observable<Float> downloadFile(@NonNull String fileUrl, @NonNull IFileCreator creator);
    //</editor-fold>
}
