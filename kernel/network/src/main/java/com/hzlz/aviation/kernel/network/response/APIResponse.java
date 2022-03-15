package com.hzlz.aviation.kernel.network.response;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import retrofit2.Response;

/**
 * API 响应接口
 *
 *
 * @since 2020-03-01 22:00
 */
public interface APIResponse {
  /**
   * 获取异常
   *
   * @param response Http 响应
   * @return 如果有异常范围不返回 null
   */
  /*@Nullable*/
  Exception getError(@NonNull Response<JsonElement> response);

  /**
   * 获取具体数据
   *
   * @param response Http 响应
   * @return 如果有数据不返回 null
   */
  /*@Nullable*/
  JsonElement getData(@NonNull Response<JsonElement> response);

  /**
   * 获取分页
   *
   * @param response Http 响应（有些分页相关信息是放在 Header 里）
   * @return 如果有分页不返回 null
   */
  /*@Nullable*/
  APIPage getPage(@NonNull Response<JsonElement> response);
}
