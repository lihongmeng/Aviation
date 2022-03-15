package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.content.Intent;

import com.hzlz.aviation.library.ioc.Plugin;

/**
 * H5 外链分发逻辑
 */
public interface H5EntryPlugin extends Plugin {
    /**
     * 今视频统一外链scheme
     */
    // TODO: 2022/3/15  
    String SCHEME = "";
    /**
     * 分发业务给详情页处理
     */
    String HOST_DETAIL = "detail";
    /**
     * 分发业务给PGC处理
     */
    String HOST_PGC = "pgc";
    /**
     * 分发业务给UGC处理
     */
    String HOST_UGC = "ugc";
    /**
     * 分发业务给Video处理
     */
    String HOST_VIDEO = "video";
    /**
     * 分发业务给FM处理
     */
    String HOST_FM = "fm";
    /**
     * 电视直播详情页
     */
    String HOST_HOME = "home";
    /**
     * 电视直播详情页
     */
    String HOST_LIVE= "live";
    /**
     * 在app内打开某web落地页，参数为gv_url jinshipin://web?gv_url=URLEcode(realUrl)
     */
    String HOST_WEB = "web";
    /**
     * 离线推送消息
     */
    String HOST_IM_PUSH = "im_push";
    /**
     * 通知详情
     */
    String HOST_NOTIFICATION = "notification";

    /**
     * 江西新闻联播特殊详情页
     */
    String HOST_JX_NEWS = "jxxwlb";

    String OPPO_ACTION = "com.hzlz.aviation.feature.action.oppo.push";

    /**
     * 根据Intent中Uri的类型，做相应的处理
     *
     * @param context Context
     * @param intent Intent数据
     * @return 分发处理结果
     */
    boolean dispatch(Context context, Intent intent);

    String getScheme();
}
