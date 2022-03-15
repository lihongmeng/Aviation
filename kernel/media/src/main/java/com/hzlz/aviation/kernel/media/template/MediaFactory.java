package com.hzlz.aviation.kernel.media.template;

import android.content.Context;
import android.view.ViewGroup;

import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.media.template.view.MediaImageAndTxtTemplate;
import com.hzlz.aviation.kernel.media.template.view.MediaSoundTemplate;
import com.hzlz.aviation.kernel.media.template.view.MediaVerticalVideoTemplate;
import com.hzlz.aviation.kernel.media.template.view.MediaVideoTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.NewSpecialHorizontalScrollTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.NewTopBannerItemTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.NewsImageTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.NewsRightImageTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.NewsScrollTemplate;

/**
 * media 模板工厂类
 */
public class MediaFactory {

  /**
   * feed数据模型统一基类
   *
   * @param context 上下文环境
   * @param mediaType media type类型
   * @param parent 父布局
   */
  public static IMediaTemplate createInstance(Context context,
      @MediaType int mediaType,
      ViewGroup parent) {
    switch (mediaType) {
      case MediaType.LONG_VIDEO:
      case MediaType.HORIZONTAL_LIVE:
      case MediaType.IM_HORIZONTAL_LIVE:
        return new MediaVideoTemplate(context, parent);
      case MediaType.SHORT_VIDEO:
      case MediaType.VERTICAL_LIVE:
      case MediaType.IM_VERTICAL_LIVE:
        return new MediaVerticalVideoTemplate(context, parent);
      case MediaType.AUDIO_TXT:
      case MediaType.IMAGE_TXT:
        return new MediaImageAndTxtTemplate(context,parent);
      case MediaType.LONG_AUDIO:
      case MediaType.SHORT_AUDIO:
        return new MediaSoundTemplate(context, parent);
      case MediaType.NEWS_IMAGE:
      case MediaType.NEWS_LINK:
        return new NewsImageTemplate(context, parent);
      case MediaType.NEWS_RIGHT_IMAGE:
        return new NewsRightImageTemplate(context, parent);
      case MediaType.NEWS_SPECIAL_ITEM:
        return new NewTopBannerItemTemplate(context, parent);
      case MediaType.NEWS_SCROLL:
        return new NewsScrollTemplate(context, parent);
      case MediaType.NEWS_SPECIAL_HORIZONTAL_SCROLL:
        return new NewSpecialHorizontalScrollTemplate(context,parent);
      default:
        throw new RuntimeException("feedLayout = " + mediaType + " media layout invalid");
    }
  }

  /**
   * 当前media type是否合法
   *
   * @param mediaType 待校验的mediaType数据
   * @return 合法性
   */
  public static boolean isMediaTypeValid(int mediaType) {
    return MediaType.MediaTypeCheck.isMediaTypeValid(mediaType);
  }
}
