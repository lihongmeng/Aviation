package com.hzlz.aviation.feature.feed.frame.databind;

import android.view.View;

import com.hzlz.aviation.feature.feed.FeedRuntime;
import com.hzlz.aviation.feature.feed.R;
import com.hzlz.aviation.feature.feed.template.view.FeedRecommendTemplate;
import com.hzlz.aviation.feature.feed.utils.FeedUtils;
import com.hzlz.aviation.kernel.base.model.video.RecommendModel;

/**
 * 推荐模板绑定模型
 */
public class FeedRecommendDataBind {

    /** 模板是为暗黑模式 */
    private boolean mIsDarkMode;
    /** 绑定的fragment id */
    private String mFragmentId;

    /**
     * 构造函数
     *
     * @param isDarkMode    是否为暗黑模式
     * @param fragmentId    绑定的fragmentId
     */
    public FeedRecommendDataBind(boolean isDarkMode, String fragmentId) {
        mIsDarkMode = isDarkMode;
        mFragmentId = fragmentId;
    }

    /**
     * 视图点击事件
     *
     * @param view      被点击的视图
     * @param model     被点击视图绑定的数据模型
     */
    public void onViewClick(View view, RecommendModel model) {
        FeedUtils.clickAdvert(view, model, mFragmentId);
        FeedRecommendTemplate.statAdvert(model, true, mFragmentId);
    }

    /**
     * 获取author名字对应的颜色
     *
     * @return 对应的颜色res
     */
    public int getAuthorNameColor() {
        int res = mIsDarkMode ? R.color.color_ffffff : R.color.color_212229;
        return FeedRuntime.getAppContext().getResources().getColor(res);
    }

    /**
     * 获取author source对应的颜色
     *
     * @return 对应的颜色res
     */
    public int getAuthorSourceColor() {
        int res = mIsDarkMode ? R.color.color_8a8c99 : R.color.color_a1a4b3;
        return FeedRuntime.getAppContext().getResources().getColor(res);
    }
}
