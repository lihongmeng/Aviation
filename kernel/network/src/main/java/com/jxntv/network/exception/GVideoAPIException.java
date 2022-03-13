package com.jxntv.network.exception;

import com.google.gson.JsonElement;

/**
 * 接口异常类<br/>
 * <p>
 * 一般情况公司的接口响应格式如下 :
 * <pre>
 *   {
 *     "code" : 0,
 *     "message" : "",
 *     "data" : {}
 *   }
 * </pre>
 * 或者如下 :
 * <pre>
 *   {
 *     "errorCode" : 0,
 *     "errmsg" : "",
 *     "result" : {}
 *   }
 * </pre>
 * 或者如下 :
 * <pre>
 *   {
 *     "code" : 0,
 *     "message" : "",
 *     "result" : {}
 *   }
 * </pre>
 * <p>
 * 不管哪一个格式，统一处理错误,
 * API 状态使用 mCode{@link GVideoAPIException#getCode()} ()} 表示，
 * API 错误消息用 message{@link GVideoAPIException#getMessage()} 表示
 *
 * @since 2020-01-06 11:50
 */
public final class GVideoAPIException extends Exception {

    //<editor-fold desc="属性">

    private int mCode;

    private JsonElement result;

    //</editor-fold>

    //<editor-fold desc="构造函数">

    public GVideoAPIException(int code, String message, JsonElement result) {
        super(message);
        mCode = code;
        this.result = result;
    }

    //</editor-fold>

    //<editor-fold desc="Getter">

    /**
     * 获取 API 接口响应模型中表示 API 状态的标识，字段可能为 code 也可能为其他
     *
     * @return API 接口响应模型中表示 API 状态的标识
     */
    public int getCode() {
        return mCode;
    }

    public JsonElement getResult() {
        return result;
    }

    //</editor-fold>
}