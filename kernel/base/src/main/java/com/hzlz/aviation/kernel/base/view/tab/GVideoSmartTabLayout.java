package com.hzlz.aviation.kernel.base.view.tab;

import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.MEET;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.NORMAL;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.SPRING_FESTIVAL;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.datamanager.ThemeDataManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.AviationTextView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * SmartTabLayout 扩展
 *
 * @since 2020-02-10 14:58
 */
public final class GVideoSmartTabLayout extends SmartTabLayout {
    //<editor-fold desc="属性">
    private boolean mUseDefaultStyle = false;
    private boolean mEnableChangeTextSize = true;
    private int mTabCount;
    private int mDefaultTextSize;
    private int mSelectedTextSize;

    private int currentPosition;
    private boolean isNeedDealMeetRed = false;

    //</editor-fold>

    //<editor-fold desc="构造函数">
    public GVideoSmartTabLayout(@NonNull Context context) {
        super(context);
        setDefaultStyle();
    }

    public GVideoSmartTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDefaultStyle();
    }

    public GVideoSmartTabLayout(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        setDefaultStyle();
    }
    //</editor-fold>

    //<editor-fold desc="API">

    /**
     * 使用默认样式
     *
     * @param useDefaultStyle true : 使用；false : 不使用
     */
    public void useDefaultStyle(boolean useDefaultStyle) {
        mUseDefaultStyle = useDefaultStyle;
        if (mUseDefaultStyle) {
            // 文本颜色
            setDefaultTabTextColor(ResourcesUtils.getColorStateList(
                    R.drawable.all_tab_layout_default_text_color
            ));
        }
    }

    /**
     * 启用改变文本大小
     *
     * @param enableChangeTextSize true : 启用；false : 不启用
     */
    public void setEnableChangeTextSize(boolean enableChangeTextSize) {
        mEnableChangeTextSize = enableChangeTextSize;
    }
    //</editor-fold>

    public void updateTextSize(@DimenRes int normal, @DimenRes int selected) {
        mDefaultTextSize = (int) ResourcesUtils.getDimens(normal);
        mSelectedTextSize = (int) ResourcesUtils.getDimens(selected);
    }

    //<editor-fold desc="内部方法">
    private void setDefaultStyle() {
        mDefaultTextSize = (int) ResourcesUtils.getDimens(R.dimen.text_size_tab_layout_default);
        mSelectedTextSize = (int) ResourcesUtils.getDimens(R.dimen.text_size_tab_layout_selected);
        if (mUseDefaultStyle) {
            // 文本颜色
            setDefaultTabTextColor(ResourcesUtils.getColorStateList(
                    R.drawable.all_tab_layout_default_text_color
            ));
        }
    }
    //</editor-fold>

    //<editor-fold desc="方法重写">
    @Override
    public void setViewPager(ViewPager viewPager) {
        super.setViewPager(viewPager);

        if (mEnableChangeTextSize && viewPager != null) {
            mTabCount = viewPager.getAdapter() == null ? 0 : viewPager.getAdapter().getCount();
            viewPager.addOnPageChangeListener(new ChangeTextSizeViewPagerListener());
            currentPosition = 0;
            updateAllTabTextSizeAndColor();
        }

        updateTabText(viewPager);
    }
    //</editor-fold>

    //<editor-fold desc="控件事件监听">
    private class ChangeTextSizeViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            if (mEnableChangeTextSize) {
                updateAllTabTextSizeAndColor();
            }
        }
    }

    /**
     * 更新所有Tab的字体大小和颜色
     */
    public void updateAllTabTextSizeAndColor() {
        if (mTabCount <= 0) {
            return;
        }
        ThemeDataManager themeDataManager = ThemeDataManager.getInstance();
        int themeColorSwitchInt=themeDataManager.getThemeColorSwitchInteger();
        for (int index = 0; index < mTabCount; index++) {
            View tab = getTabAt(index);
            if (!(tab instanceof TextView)) {
                continue;
            }
            boolean isCurrent = index == currentPosition;
            ((TextView) tab).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    isCurrent ? mSelectedTextSize : mDefaultTextSize
            );
            ((TextView) tab).setTypeface(
                    Typeface.defaultFromStyle(isCurrent ? Typeface.BOLD : Typeface.NORMAL));

            if(!isNeedDealMeetRed){
                continue;
            }

            switch (themeColorSwitchInt){
                case MEET:
                case SPRING_FESTIVAL:
                    ((TextView) tab).setTextColor(
                            ContextCompat.getColor(
                                    getContext(),
                                    isCurrent ? R.color.color_ffe4a8 : R.color.color_ffffff
                            )
                    );
                    break;
                case NORMAL:
                default:
                    ((TextView) tab).setTextColor(
                            ContextCompat.getColor(getContext(), R.color.color_333333)
                    );
            }
        }
    }

    private void updateTabText(ViewPager viewPager) {
        if (mTabCount > 0 && viewPager != null && viewPager.getAdapter() != null) {
            for (int i = 0; i < mTabCount; i++) {
                View tab = getTabAt(i);
                if (tab instanceof TextView || tab instanceof AviationTextView) {
                    CharSequence title = viewPager.getAdapter().getPageTitle(i);
                    ((TextView) tab).setText(title);
                }
            }
        }
    }

    public void setNeedDealMeetRed(boolean needDealMeetRed) {
        isNeedDealMeetRed = needDealMeetRed;
    }

    //</editor-fold>


}
