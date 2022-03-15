package com.hzlz.aviation.kernel.network.response;

/**
 * API 分页接口
 *
 *
 * @since 2020-03-01 22:02
 */
public interface APIPage {
  /**
   * 是否有下一页
   *
   * @return true : 有下一页；false : 没有下一页
   */
  boolean hasNextPage();

  /**
   * 数据总数
   */
  int getTotal();

  int getPageNum();
}
