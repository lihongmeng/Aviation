package com.hzlz.aviation.feature.watchtv;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.watchtv.entity.ChannelTvManifest;
import com.hzlz.aviation.feature.watchtv.entity.ColumnDetail;
import com.hzlz.aviation.feature.watchtv.entity.WatchTvChannelDetail;
import com.hzlz.aviation.feature.watchtv.request.GetChannelColumnListRequest;
import com.hzlz.aviation.feature.watchtv.request.GetChannelInfoRequest;
import com.hzlz.aviation.feature.watchtv.request.GetChannelListRequest;
import com.hzlz.aviation.feature.watchtv.request.GetChannelTvManifestRequest;
import com.hzlz.aviation.feature.watchtv.request.GetColumnAllListRequest;
import com.hzlz.aviation.feature.watchtv.request.GetColumnDetailRequest;
import com.hzlz.aviation.feature.watchtv.request.GetHotTvListRequest;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.model.video.WatchTvChannel;
import com.hzlz.aviation.kernel.base.utils.SecretUtils;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.util.DeviceId;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class WatchTvRepository extends BaseDataRepository {

    @NonNull
    public Observable<ListWithPage<WatchTvChannel>> getChannelList(
            String type,
            int pageNumber,
            int pageSize
    ) {
        return new NetworkData<ListWithPage<WatchTvChannel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<WatchTvChannel>> createRequest() {
                GetChannelListRequest request = new GetChannelListRequest();
                request.setType(type);
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<WatchTvChannel> watchTvChannelListWithPage) {
            }
        }.asObservable();
    }

    @NonNull
    public Observable<WatchTvChannelDetail> getChannelInfo(long channelId) {
        return new NetworkData<WatchTvChannelDetail>(mEngine) {
            @Override
            protected BaseRequest<WatchTvChannelDetail> createRequest() {
                GetChannelInfoRequest request = new GetChannelInfoRequest();
                request.setChannelId(channelId);
                return request;
            }

            @Override
            protected void saveData(WatchTvChannelDetail watchTvChannelDetail) {
            }
        }.asObservable();
    }

    @NonNull
    public Observable<ListWithPage<WatchTvChannel>> getChannelColumnList(long channelId, int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<WatchTvChannel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<WatchTvChannel>> createRequest() {
                GetChannelColumnListRequest request = new GetChannelColumnListRequest();
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                request.setChannelId(channelId);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<WatchTvChannel> watchTvChannelListWithPage) {
            }
        }.asObservable();
    }

    @NonNull
    public Observable<List<ChannelTvManifest>> getChannelTvManifest(long channelId, String playDate) {
        return new NetworkData<List<ChannelTvManifest>>(mEngine) {
            @Override
            protected BaseRequest<List<ChannelTvManifest>> createRequest() {
                GetChannelTvManifestRequest request = new GetChannelTvManifestRequest();
                request.setChannelId(channelId);
                request.setPlayDate(playDate);
                return request;
            }

            @Override
            protected void saveData(List<ChannelTvManifest> channelTvManifestList) {
            }
        }.asObservable();
    }

    @NonNull
    public Observable<ListWithPage<VideoModel>> getColumnAllList(String columnId, String programId, int pageNumber) {
        return new NetworkData<ListWithPage<VideoModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<VideoModel>> createRequest() {
                GetColumnAllListRequest request = new GetColumnAllListRequest();
                request.setColumnId(columnId);
                if (!TextUtils.isEmpty(programId)){
                    request.setProgramId(programId);
                }
                request.setPageNumber(pageNumber);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<VideoModel> mediaModelListWithPage) {
            }
        }.asObservable();
    }

    @NonNull
    public Observable<ListWithPage<WatchTvChannel>> getHotTvList(int pageNumber, int pageSize) {
        return new NetworkData<ListWithPage<WatchTvChannel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<WatchTvChannel>> createRequest() {
                GetHotTvListRequest request = new GetHotTvListRequest();
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<WatchTvChannel> watchTvChannelListWithPage) {
            }
        }.asObservable();
    }

    /**
     * 获取看电视的鉴权token
     *
     * @param context    Context
     * @param getAuthUrl 用于获取鉴权的地址
     * @param callback   接口响应回调
     */
    public void getWatchTvAuthentication(Context context, String getAuthUrl, Callback callback) {
        if (TextUtils.isEmpty(getAuthUrl)) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = packageInfo == null ? "" : packageInfo.versionName;

        long time = System.currentTimeMillis();
        String token = "com.sobey.cloud.view.jiangxi" + "android" + "jxntv" + time;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(getAuthUrl).build();

        HttpUrl httpUrl = request.url().newBuilder()
                .addQueryParameter("type", "android")
                .addQueryParameter("t", time + "")
                .addQueryParameter("time", time + "")
                .addQueryParameter("token", SecretUtils.md5(token))
                .addQueryParameter("device_id", DeviceId.get())
                .addQueryParameter("app_version", versionName)
                .addQueryParameter("siteid", "10001")
                .build();

        request = new Request.Builder()
                .header("User-Agent", WebSettings.getDefaultUserAgent(context) + "/tvcVersion" + versionName)
                .url(httpUrl)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 获取栏目信息
     * @param columnId
     * @return
     */
    @NonNull
    public Observable<ColumnDetail> getColumnDetail(String columnId) {
        return new NetworkData<ColumnDetail>(mEngine) {
            @Override
            protected BaseRequest<ColumnDetail> createRequest() {
                GetColumnDetailRequest request = new GetColumnDetailRequest();
                request.setColumnId(columnId);
                return request;
            }

            @Override
            protected void saveData(ColumnDetail detail) {
                if (detail != null && detail.getGroup() != null) {
                    InteractDataObservable.getInstance().setJoinCircle(detail.getGroup().groupId, detail.getGroup().isJoin());
                }
            }
        }.asObservable();
    }

}
