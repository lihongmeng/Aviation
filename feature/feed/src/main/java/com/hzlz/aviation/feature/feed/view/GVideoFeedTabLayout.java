package com.hzlz.aviation.feature.feed.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;

import com.hzlz.aviation.feature.feed.R;
import com.hzlz.aviation.kernel.base.plugin.SearchPlugin;
import com.hzlz.aviation.kernel.base.view.HomeMessageLayout;
import com.hzlz.aviation.kernel.base.view.tab.GVideoSmartTabLayout;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.AviationImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * feed头部tab layout统一模板
 */
public class GVideoFeedTabLayout extends LinearLayout implements View.OnClickListener {

    /** 单tab，长搜索栏 */
    public static final int TYPE_FEED_TAB_LONG_SEARCH = 0;
    /** 多tab，仅有搜索图标  */
    public static final int TYPE_FEED_TAB_SEARCH_ICON = 1;
    /** 多tab，仅有搜索图标，暗色 */
    public static final int TYPE_FEED_TAB_SEARCH_ICON_DARK = 2;
    /** 多tab, 无搜索图标， */
    public static final int TYPE_FEED_TAB_NONE_SEARCH_ICON = 3;

    @IntDef({TYPE_FEED_TAB_LONG_SEARCH, TYPE_FEED_TAB_SEARCH_ICON, TYPE_FEED_TAB_SEARCH_ICON_DARK,TYPE_FEED_TAB_NONE_SEARCH_ICON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FeedTabBarType {
    }

    /** 搜索图布局 */
    private AviationImageView mSearchImg;
    /** 侧边栏 */
    private HomeMessageLayout messageLayout;
    /** tab layout */
    private GVideoSmartTabLayout mTabLayout;

    private String pid;

    /**
     * 构造方法
     */
    public GVideoFeedTabLayout(Context context) {
        super(context);
        initView();
    }

    /**
     * 构造方法
     */
    public GVideoFeedTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 构造方法
     */
    public GVideoFeedTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 更改视图类型
     *
     * @param type  视图类型
     */
    public void changeType(@FeedTabBarType int type) {
        switch (type) {
            case TYPE_FEED_TAB_SEARCH_ICON: {
                setBackgroundColor(getResources().getColor(R.color.color_ffffff));
                break;
            }
            case TYPE_FEED_TAB_SEARCH_ICON_DARK: {

                if (mSearchImg != null) {
                    mSearchImg.setImageResource(R.drawable.feed_single_search_icon_dark);
                }
                setBackgroundColor(getResources().getColor(R.color.color_1b1c1f));
                mTabLayout.setDefaultTabTextColor(ResourcesUtils.getColorStateList(
                        R.drawable.tab_layout_dark_text_color
                ));
            }
            case TYPE_FEED_TAB_NONE_SEARCH_ICON:
                setBackgroundColor(getResources().getColor(R.color.color_ffffff));
                if (mSearchImg != null) {
                    mSearchImg.setVisibility(GONE);
                }
                break;
            default:
                break;
        }
    }

    public void setUnreadCount(int unreadCount) {
        messageLayout.setUnreadCount(unreadCount);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setOrientation(HORIZONTAL);
        View root = inflate(getContext(), R.layout.gvideo_tab_slide_layout, this);

        mSearchImg = root.findViewById(R.id.search_img);
        messageLayout = root.findViewById(R.id.plus_img);
        mTabLayout = root.findViewById(R.id.gvideo_tabs);

        mSearchImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginManager.get(SearchPlugin.class).navigateToSearchActivity(getContext());
            }
        });

    }

    /**
     * 获取tablayout布局
     */
    public GVideoSmartTabLayout getTabLayout() {
        return mTabLayout;
    }

    public String getPid() {
        return TextUtils.isEmpty(pid) ? "" : pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
        if (messageLayout == null) {
            return;
        }
        messageLayout.setPageName(pid);
    }
}
