package com.jxntv.feed.frame.databind;

import android.view.View;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.feed.FeedRuntime;
import com.jxntv.feed.R;
import com.jxntv.feed.template.view.FeedRecommendTemplate;
import com.jxntv.feed.utils.FeedUtils;

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
        int res = mIsDarkMode ? R.color.t_color05 : R.color.t_color01;
        return FeedRuntime.getAppContext().getResources().getColor(res);
    }

    /**
     * 获取author source对应的颜色
     *
     * @return 对应的颜色res
     */
    public int getAuthorSourceColor() {
        int res = mIsDarkMode ? R.color.t_color04 : R.color.t_color03;
        return FeedRuntime.getAppContext().getResources().getColor(res);
    }
}
