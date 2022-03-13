package com.jxntv.base.view.tab;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jxntv.base.R;
import com.jxntv.base.datamanager.ThemeDataManager;
import com.jxntv.base.view.tab.GVideoSmartTabLayout;
import com.jxntv.utils.ResourcesUtils;
import com.jxntv.widget.GVideoTextView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * @author huangwei
 * date : 2022/1/24
 * desc :
 **/
public class GvideoMessageTab extends SmartTabLayout implements SmartTabLayout.TabProvider{


    //<editor-fold desc="属性">
    private boolean mUseDefaultStyle = false;
    private int mTabCount;
    private int mDefaultTextSize;
    private int mSelectedTextSize;

    private int currentPosition;

    public GvideoMessageTab(@NonNull Context context) {
        super(context);
        setDefaultStyle();
    }

    public GvideoMessageTab(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDefaultStyle();
    }

    public GvideoMessageTab(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefaultStyle();
    }

    public void resetCustomTabView(){
        setCustomTabView(this);
    }

    public void setUpdateUnread(int position, int unreadCount){
        if (position < mTabCount){
            View tab = getTabAt(position);
            View view = tab.findViewById(R.id.tab_unread);
            if (view instanceof GVideoTextView || view instanceof TextView){
                if (unreadCount > 0) {
                    ((TextView) view).setText(unreadCount + "");
                    view.setVisibility(VISIBLE);
                }else {
                    view.setVisibility(GONE);
                }
            }
        }

    }


    /**
     * 使用默认样式
     *
     * @param useDefaultStyle true : 使用；false : 不使用
     */
    public void useDefaultStyle(boolean useDefaultStyle) {
        mUseDefaultStyle = useDefaultStyle;
        if (mUseDefaultStyle) {
            // 文本颜色
            setDefaultTabTextColor(ResourcesUtils.getColorStateList(R.drawable.all_tab_layout_default_text_color));
        }
    }

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
            setDefaultTabTextColor(ResourcesUtils.getColorStateList(R.drawable.all_tab_layout_default_text_color));
        }
    }
    //</editor-fold>

    //<editor-fold desc="方法重写">
    @Override
    public void setViewPager(ViewPager viewPager) {
        super.setViewPager(viewPager);

        if (viewPager != null) {
            mTabCount = viewPager.getAdapter() == null ? 0 : viewPager.getAdapter().getCount();
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                    updateAllTabTextSizeAndColor();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        updateAllTabTextSizeAndColor();
    }

    /**
     * 更新所有Tab的字体大小和颜色
     */
    public void updateAllTabTextSizeAndColor() {
        if (mTabCount <= 0) {
            return;
        }
        for (int index = 0; index < mTabCount; index++) {
            View tab = getTabAt(index);
            boolean isCurrent = index == currentPosition;
            View view = tab.findViewById(R.id.tab_title);
            if (view instanceof GVideoTextView || view instanceof TextView){
                TextView textView = (TextView) view;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, isCurrent ? mSelectedTextSize : mDefaultTextSize);
                textView.setTypeface(Typeface.defaultFromStyle(isCurrent ? Typeface.BOLD : Typeface.NORMAL));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_333333));
            }
        }
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View tab = inflater.inflate(R.layout.view_message_notification_tab_item, container, false);
        GVideoTextView title = tab.findViewById(R.id.tab_title);
        title.setText(adapter.getPageTitle(position));
        return tab;
    }
}
