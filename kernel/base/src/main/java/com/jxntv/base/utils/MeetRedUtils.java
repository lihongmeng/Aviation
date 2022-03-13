package com.jxntv.base.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.jxntv.base.R;
import com.jxntv.base.view.HomeMessageLayout;
import com.jxntv.base.view.ViewPagerTab;
import com.jxntv.base.view.tab.GVideoSmartTabLayout;

public class MeetRedUtils {

    public static void updateToNormal(ViewPagerTab viewPagerTab){
        viewPagerTab.updateToNormal();
    }

    public static void updateToNormal(
            ViewPagerTab viewPagerTab,
            HomeMessageLayout messageLayout
    ){
        viewPagerTab.updateToNormal();
        messageLayout.updateToNormal();
    }

    public static void updateToNormal(
            Context context,
            View topBackground,
            View searchBackground,
            ImageView searchIcon,
            ImageView scanIcon,
            GVideoSmartTabLayout gVideoSmartTabLayout,
            HomeMessageLayout messageLayout
            ){
        if (context == null) {
            return;
        }
        topBackground.setBackground(ContextCompat.getDrawable(context, R.color.color_ffffff));
        searchBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.feed_search_drawable));
        searchIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_search_search_grey));
        scanIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_search_scan_grey));

        gVideoSmartTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(context, R.color.color_e4344e));
        gVideoSmartTabLayout.updateAllTabTextSizeAndColor();
        messageLayout.updateToNormal();

    }

    public static void updateToRed(ViewPagerTab viewPagerTab){
        viewPagerTab.updateToRed();
    }

    public static void updateToRed(
            ViewPagerTab viewPagerTab,
            HomeMessageLayout messageLayout
    ){
        viewPagerTab.updateToRed();
        messageLayout.updateToRed();
    }

    public static void updateToRed(
            Context context,
            View topBackground,
            View searchBackground,
            ImageView searchIcon,
            ImageView scanIcon,
            GVideoSmartTabLayout gVideoSmartTabLayout,
            HomeMessageLayout messageLayout
    ){
        if (context == null) {
            return;
        }
        topBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_tab_chiness_red_home));
        searchBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_solid_ffffff_30_corners_200dp));
        searchIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_search_search_white));
        scanIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_search_scan_white));

        gVideoSmartTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(context, R.color.color_ffe4a8));
        gVideoSmartTabLayout.updateAllTabTextSizeAndColor();
        messageLayout.updateToRed();

    }

    public static void updateToSpringFestival(ViewPagerTab viewPagerTab){
        viewPagerTab.updateToSpringFestival();
    }

    public static void updateToSpringFestival(
            ViewPagerTab viewPagerTab,
            HomeMessageLayout messageLayout
    ){
        viewPagerTab.updateToSpringFestival();
        messageLayout.updateToSpringFestival();
    }

    public static void updateToSpringFestival(
            Context context,
            View topBackground,
            View searchBackground,
            ImageView searchIcon,
            ImageView scanIcon,
            GVideoSmartTabLayout gVideoSmartTabLayout,
            HomeMessageLayout messageLayout
    ){
        if (context == null) {
            return;
        }
        topBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_tab_spring_festival_home));
        searchBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_solid_ffffff_30_corners_200dp));
        searchIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_search_search_white));
        scanIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_search_scan_white));

        gVideoSmartTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(context, R.color.color_ffe4a8));
        gVideoSmartTabLayout.updateAllTabTextSizeAndColor();
        messageLayout.updateToRed();

    }

}
