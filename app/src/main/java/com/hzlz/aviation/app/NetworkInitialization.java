package com.hzlz.aviation.app;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.hzlz.aviation.kernel.base.environment.EnvironmentManager;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.network.NetworkManager;
import com.hzlz.aviation.kernel.network.exception.GVideoAPIException;
import com.hzlz.aviation.kernel.network.response.APIPage;
import com.hzlz.aviation.kernel.network.response.APIResponse;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.DeviceId;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络初始化
 *
 *
 * @since 2020-01-13 17:04
 */
public final class NetworkInitialization {
  //<editor-fold desc="属性">
  private static final Retrofit sRetrofit;
  private static final Gson sGson;
  private static final HttpLoggingInterceptor LOG_INTERCEPTOR;
  //</editor-fold>

  //<editor-fold desc="初始化">
  static {
    // 拦截器
    LOG_INTERCEPTOR = new HttpLoggingInterceptor(message -> {
      if (true) {
        Log.i("HTTP_LOG", message);
      }
    });
    LOG_INTERCEPTOR.setLevel(HttpLoggingInterceptor.Level.BODY);
    //
    sGson = new GsonBuilder().setLenient()
        .registerTypeAdapter(Date.class, new TimestampTypeAdapter())
        .serializeNulls()
        .create();
    sRetrofit = new Retrofit.Builder()
        .client(new OkHttpClient.Builder()
            .addInterceptor(new AuthorizationInterceptor())
            .addInterceptor(new GzipRequestInterceptor())
            .addInterceptor(LOG_INTERCEPTOR)
            .build()
        )
        .baseUrl(EnvironmentManager.getInstance().getCurrentAPIUrl())
        .addConverterFactory(GsonConverterFactory.create(sGson))
        .addCallAdapterFactory(
            RxJava3CallAdapterFactory.createWithScheduler(GVideoSchedulers.IO_PRIORITY_BACKGROUND)
        )
        .build();
  }
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private NetworkInitialization() {

  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 初始化
   *
   * @param application Application
   */
  static void initialize(@NonNull Application application) {
    NetworkManager.with(application)
        .retrofit(sRetrofit)
        .gson(sGson)
        .addResponseType(GVideoResponse.class)
        .init();
  }
  //</editor-fold>

  //<editor-fold desc="响应">
  private static final class GVideoResponse implements APIResponse {
    //<editor-fold desc="属性">
    @SerializedName(value = "code", alternate = { "status" })
    private Integer mCode;
    @SerializedName(value = "result", alternate = { "data" })
    private JsonElement mData;
    @SerializedName(value = "message", alternate = { "msg" })
    private String mMessage;
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    public Exception getError(@NonNull Response<JsonElement> response) {
      if (mCode != null && mCode != 0) {
        return new GVideoAPIException(mCode, mMessage,mData);
      }
      return null;
    }

    @Override
    public JsonElement getData(@NonNull Response<JsonElement> response) {
      return mData;
    }

    @Override
    public APIPage getPage(@NonNull Response<JsonElement> response) {
      if (mData instanceof JsonObject) {
        GVideoPage page = NetworkManager.getInstance().getGson().fromJson(
            mData, GVideoPage.class
        );

        if (page != null && page.ok()) {
          return page;
        }
      }
      return null;
    }
    //</editor-fold>
  }

  // 分页
  private static final class GVideoPage implements APIPage {
    //<editor-fold desc="属性">
    @SerializedName("hasMore")
    @Nullable
    private Boolean mHasMore;
    @SerializedName("pageNum")
    @Nullable
    private Integer mPageNum;
    @SerializedName("pageSize")
    @Nullable
    private Integer mPageSize;
    @SerializedName("total")
    @Nullable
    private Integer mTotal;
    //</editor-fold>

    //<editor-fold desc="API">
    private boolean ok() {
      return mHasMore != null || (mPageNum != null && mPageSize != null && mTotal != null);
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    public boolean hasNextPage() {
      if (mHasMore != null) {
        return mHasMore;
      }
      if (mPageNum != null && mPageSize != null && mTotal != null) {
        return mPageNum * mPageSize < mTotal;
      }
      return false;
    }

    @Override
    public int getTotal() {
      return mTotal;
    }

    @Override
    public int getPageNum() {
      return mPageNum;
    }

    //</editor-fold>
  }
  //</editor-fold>

  private static final class AuthorizationInterceptor implements Interceptor {

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
      Request oldRequest = chain.request();
      // 检测 tag 是否忽略 App 的 Header
      String tag = oldRequest.tag(String.class);
      if ("omitAppHeader".equals(tag)) {
        return chain.proceed(oldRequest);
      }
      Request.Builder requestBuilder = oldRequest.newBuilder();
      // appVersion
      requestBuilder.addHeader("appVersion", GVideoRuntime.getVersionName());
      // channelType
      requestBuilder.addHeader("channelType", GVideoRuntime.getChannelType());
      // os
      requestBuilder.addHeader("os", "Android");
      // device
      requestBuilder.addHeader("device", DeviceId.get());
      // token
      AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
      if (plugin != null) {
        String token = plugin.getToken();
        if (token != null) {
          String authorization = "Bearer " + token;
          requestBuilder.addHeader("Authorization", authorization);
//          LogUtils.d("Authorization -->> " + authorization);
        }
      }
      return chain.proceed(requestBuilder.build());
    }
  }
  private static final class GzipRequestInterceptor implements Interceptor {
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
      Request originalRequest = chain.request();
      String tag = originalRequest.tag(String.class);
      if (!TextUtils.equals(tag, "gzip")) {
        return chain.proceed(originalRequest);
      }

      String header = originalRequest.header("Content-Encoding");
      if (originalRequest.body() == null || header != null) {
        return chain.proceed(originalRequest);
      }

      Request compressedRequest = originalRequest.newBuilder()
          .header("Content-Encoding", "gzip")
          .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body())))
          .build();
      return chain.proceed(compressedRequest);
    }
    private RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
      final Buffer buffer = new Buffer();
      requestBody.writeTo(buffer);
      return new RequestBody() {
        @Override
        public MediaType contentType() {
          return requestBody.contentType();
        }

        @Override
        public long contentLength() {
          return buffer.size();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
          sink.write(buffer.snapshot());
        }
      };
    }
    private RequestBody gzip(final RequestBody body) {
      return new RequestBody() {
        @Override
        public MediaType contentType() {
          return body.contentType();
        }

        @Override
        public long contentLength() {
          return -1; // 无法提前知道压缩后的数据大小
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
          BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
          body.writeTo(gzipSink);
          gzipSink.close();
        }
      };
    }
  }
  //<editor-fold desc="自定义 Json">
  private static final class TimestampTypeAdapter extends TypeAdapter<Date> {

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
      if (value == null) {
        out.nullValue();
      } else {
        out.value(value.getTime());
      }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
      JsonToken peek = in.peek();
      if (peek == JsonToken.NULL) {
        in.nextNull();
        return null;
      }
      if (peek == JsonToken.NUMBER) {
        try {
          long time = in.nextLong();
          return new Date(time);
        } catch (NumberFormatException | IOException ignore) {
          // do nothing
        }
      }
      return null;
    }
  }
  //</editor-fold>
}
