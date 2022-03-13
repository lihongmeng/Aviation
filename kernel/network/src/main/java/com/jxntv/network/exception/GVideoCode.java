package com.jxntv.network.exception;

/**
 * 错误码定义
 */
public class GVideoCode {
  //与接口协商错误码START
  /** 该PGC已被禁用code，涉及接口api/authors/{authorId} */
  public static final int CODE_PGC_FORBIDDEN = 2001;
  /** 账户被禁用code，涉及所有接口 */
  public static final int CODE_ACCOUNT_FREEZE = 2000;
  /** 因为没有加入圈子，不能评论、回复*/
  public static final int CODE_HAS_NOT_JOIN_CIRCLE= 50001;
  //与接口协商错误码END

  //本地错误码START
  public static final int CODE_OK = 0;
  /** 无网络 */
  public static final int CODE_NETWORK_NOT_AVAILABLE = -1;
  /** 数据为空 */
  public static final int CODE_EMPTY = -2;
  /** 用户未登陆 */
  public static final int CODE_USER_UN_LOGIN = -3;
  /** 接口忙 */
  public static final int CODE_BUSY = -4;
  /** 用户数据异常，无法执行请求 */
  public static final int CODE_USER_ERROR = -5;
  /** 接口超时 */
  public static final int CODE_TIME_OUT = -10;
  /** 没有下一页数据 */
  public static final int CODE_NO_MORE_NEXT_PAGE = -11;
  /** 参数为空 */
  public static final int CODE_PARAM_EMPTY = -100;
  /** 参数无效 */
  public static final int CODE_PARAM_ERROR = -101;
  /** 上传文件地址无效 */
  public static final int CODE_PARAM_UPLOAD_FILE_INVALID = -102;
  /** 解析对象不是正确json结构 */
  public static final int CODE_PARSE_JSON_ERROR = -200;
  /** 解析对象节点为空 */
  public static final int CODE_PARSE_EMPTY = -201;
  /** 解析对象初始化失败 */
  public static final int CODE_PARSE_INIT_FAILED = -202;
  //本地错误码END

  /** 邀请码或验证码已过期 */
  public static final int VALIDATE_CODE_EXPIRED = 1001;
  /** 邀请码或验证码错误 */
  public static final int VALIDATE_CODE_UNMATCHED = 1002;

}
