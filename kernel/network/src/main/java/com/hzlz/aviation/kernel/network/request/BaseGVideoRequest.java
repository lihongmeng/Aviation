package com.hzlz.aviation.kernel.network.request;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.kernel.network.NetworkManager;
import com.hzlz.aviation.kernel.network.response.APIPage;
import com.hzlz.aviation.kernel.network.response.APIResponse;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 请求基类
 *
 * @param <T> 请求响应具体模型泛型
 *
 * @since 2020-01-06 16:17
 */
public abstract class BaseGVideoRequest<T> extends BaseRequest<T> {
  //<editor-fold desc="属性">
  private static final Object EMPTY = new Object();
  /*@NotNull*/
  private Type mResponseType;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  protected BaseGVideoRequest() {
    // 设置默认类型
    mResponseType = Object.class;
    // 判断是否手动设置类型
    TypeToken typeToken = getResponseTypeToken();
    if (typeToken != null) {
      mResponseType = typeToken.getType();
    } else {
      // 获取泛型类型
      Type geniusSuperclass = getClass().getGenericSuperclass();
      if (geniusSuperclass instanceof ParameterizedType) {
        mResponseType = ((ParameterizedType) geniusSuperclass).getActualTypeArguments()[0];
      }
    }
  }
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 获取响应的 Observable {@link Observable}实例
   *
   * @return 响应的 Observable {@link Observable}实例
   */
  protected abstract Observable<Response<JsonElement>> getResponseObservable();

  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 是否为分页请求
   *
   * @return true : 是；false : 不是
   */
  protected boolean isPageRequest() {
    return false;
  }

  /**
   * 获取响应类型<br/>
   * 作用 : <br/>
   * 在多次继承后，可能获取泛型 T 不是最终需要的类型，或者直接获取不到泛型，
   * 这时候就需要实现次方法获取具体模型的响应类型
   *
   * @return 响应类型
   */
  /*@Nullable*/
  protected TypeToken getResponseTypeToken() {
    return null;
  }

  /**
   * 进一步处理 data 模型对象<br/>
   * 主要为了处理如下类型响应
   * <pre>
   *  {
   *    "code" : 0,
   *    "data" : {
   *      "user" : {
   *
   *      }
   *    }
   *  }
   * </pre>
   * 其实我只需要 user 对象，但是又被 API 包了一层
   *
   * @param data data Json 对象
   * @return 最终的响应模型
   */
  /*@NonNull*/
  protected JsonElement furtherProcessData(/*@NonNull*/ JsonElement data) {
    if (data == null) {
      throw new NullPointerException();
    }
    return data;
  }

  /**
   * 工具方法，用于获取 JsonElement 的成员 Json 对象
   *
   * @param jsonElement jsonElement
   * @param memberName 成员名称
   * @return JsonElement 的成员 Json 对象
   */
  /*@NonNull*/
  protected JsonElement getMemberJsonElement(
      /*@NonNull*/ JsonElement jsonElement,
      /*@NonNull*/ String memberName) {
    if (jsonElement == null || memberName == null) {
      throw new NullPointerException();
    }
    return jsonElement.getAsJsonObject().get(memberName);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public final Observable<T> getObservable() {
    return getResponseObservable().map(this::convert);
  }

  //</editor-fold>

  //<editor-fold desc="私有方法">

  /**
   * 将 Response 转换成具体的响应模型
   *
   * @param response 响应 {@link Response}
   * @return 具体的响应模型
   */
  @SuppressWarnings("unchecked")
  private T convert(Response<JsonElement> response) throws Exception {
    // 判断请求是否成功
    if (response.isSuccessful()) {
      // 日志回调
      if (NetworkManager.getInstance().getLogger() != null) {
        NetworkManager.getInstance().getLogger().convert(this, response.body(), mResponseType);
      }
      // 判断响应状态码
      int code = response.code();
      if (code == 200 && response.body() != null) {
        // 处理响应
        Object result = processResponse(response, response.body(), false);
        if (result == null) {
          throw new RuntimeException("unsupported response type");
        }
        return (T) result;
      } else {
        return getEmptyDataModel();
      }
    }
    // 响应失败，即 Http Code 码大于等于 300
    ResponseBody errorBody = response.errorBody();
    if (errorBody != null) {
      String responseString = errorBody.string();
      Object result = processResponse(response, responseString, true);
      if (result == null) {
        throw new Exception(responseString);
      }
    }
    throw new Exception(response.message());
  }

  /**
   * 获取空的数据模型
   *
   * @param <Model> 泛型
   * @return 空的数据模型
   */

  @SuppressWarnings("unchecked")
  private <Model> Model getEmptyDataModel() throws InstantiationException, IllegalAccessException {
    if (mResponseType == null || Object.class.equals(mResponseType)) {
      return (Model) EMPTY;
    } else {
      return (Model) ((Class) mResponseType).newInstance();
    }
  }

  /**
   * 处理响应
   *
   * @param httpResponse Http 响应
   * @param json Json 对象 / json 字符串
   */
  @SuppressWarnings("unchecked")
  private Object processResponse(Response<JsonElement> httpResponse, Object json,
      boolean onlyCheckError) throws Exception {
    if (!(json instanceof JsonElement || json instanceof String)) {
      return null;
    }
    List<Class<? extends APIResponse>> responseTypeList =
        NetworkManager.getInstance().getResponseTypeList();
    if (responseTypeList == null || responseTypeList.isEmpty()) {
      return null;
    }
    Gson gson = NetworkManager.getInstance().getGson();
    for (Class<? extends APIResponse> type : responseTypeList) {
      // 解析响应
      APIResponse apiResponse;
      if (json instanceof JsonElement) {
        apiResponse = gson.fromJson((JsonElement) json, type);
      } else {
        apiResponse = gson.fromJson((String) json, type);
      }
      if (apiResponse == null) {
        continue;
      }
      // 判断是否有错误
      Exception error = apiResponse.getError(httpResponse);
      if (error != null) {
        throw error;
      }
      if (onlyCheckError) {
        continue;
      }
      // 获取具体数据
      JsonElement data = apiResponse.getData(httpResponse);
      // 获取分页
      APIPage page = null;
      if (isPageRequest()) {
        page = apiResponse.getPage(httpResponse);
        if (page == null) {
          continue;
        }
      }
      if (data == null && page == null) {
        continue;
      }
      // 获取响应结果（程序关心的具体模型）
      Object result = null;
      if (data != null) {
        data = furtherProcessData(data);
        // 判断 data 是否为 JsonNull
        if (!(data instanceof JsonNull) && !Object.class.equals(mResponseType)) {
          result = gson.fromJson(data, mResponseType);
        }
      }
      // 判断响应结果是否为 null
      if (result == null) {
        // 判断是否为分页
        if (page != null) {
          // 直接解析 json, 排除 data 为 JsonNull 的情况
          if (!(data instanceof JsonNull) && json instanceof JsonArray) {
            result = gson.fromJson((JsonArray) json, mResponseType);
          }
          // 默认空列表
          if (result == null) {
            result = gson.fromJson("[]", mResponseType);
          }
        } else {
          // 直接解析 json, 排除 data 为 JsonNull 的情况
          if (!(data instanceof JsonNull)
              && !Object.class.equals(mResponseType)
              && json instanceof JsonElement) {
            result = gson.fromJson((JsonElement) json, mResponseType);
          }
          // 获取空的数据模型
          if (result == null) {
            result = getEmptyDataModel();
          }
        }
      }
      if (page == null) {
        return result;
      }
      return new ListWithPage((List) result, page);
    }
    return null;
  }
  //</editor-fold>
}
