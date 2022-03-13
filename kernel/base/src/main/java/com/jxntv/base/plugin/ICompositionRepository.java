package com.jxntv.base.plugin;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Observable;

/**
 * 作品接口
 */
public interface ICompositionRepository {
  /**
   * 删除个人发布的资源
   *
   * @param mediaId 资源 id
   */
  @NonNull
  Observable<Boolean> deleteMedia(@NonNull String mediaId);

  /**
   * 发布资源
   *
   * @param isPublic      是否为公开发布
   * @param introduction  资源介绍
   * @param imageId       资源图片id
   * @param videoId       资源视频id
   */
  Observable<Integer> publish(
          boolean isPublic,
          String introduction,
          String imageId,
          String videoId,
          String gatherId
  );

}
