package com.jxntv.base.plugin;

import androidx.annotation.NonNull;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.network.response.ListWithPage;

import io.reactivex.rxjava3.core.Observable;

/**
 * 关注仓库
 *
 *
 * @since 2020-03-03 17:50
 */
public interface IFollowRepository {
  /**
   * 关注作者
   *
   * @param authorId 作者 id
   * @param authorType 该用户类型
   * @param authorName 作者名称
   * @param follow 是否关注；true : 关注 ; false : 取关
   */
  @NonNull
  Observable<Boolean> followAuthor(@NonNull String authorId, @AuthorType int authorType, String authorName, boolean follow);
}
