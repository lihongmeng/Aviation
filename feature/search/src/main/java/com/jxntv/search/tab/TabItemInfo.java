package com.jxntv.search.tab;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.jxntv.search.R;
import com.jxntv.search.SearchRuntime;
import com.jxntv.search.model.SearchType;
import com.jxntv.search.page.fragment.SearchCommonFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * tab info
 */
public class TabItemInfo {

    /** tab标签名 */
    public String tabName;
    /** tab type */
    public int tabType;

    /**
     * 构造函数
     */
    public TabItemInfo(String tabName, int tabType) {
        this.tabName = tabName;
        this.tabType = tabType;
    }

    /**
     * 获取默认结果list
     */
    public static List<TabItemInfo> getDefaultSearchResultTabList() {
        List<TabItemInfo> list = new ArrayList<>();
        Context context = SearchRuntime.getAppContext();
        list.add(new TabItemInfo(context.getResources().getString(R.string.tab_all),
                SearchType.CATEGORY_ALL));
        list.add(new TabItemInfo(context.getResources().getString(R.string.tab_authors),
                SearchType.CATEGORY_AUTHORS));
        list.add(new TabItemInfo(context.getResources().getString(R.string.tab_community),
                SearchType.CATEGORY_COMMUNITY));
        list.add(new TabItemInfo(context.getResources().getString(R.string.tab_news),
                SearchType.CATEGORY_NEWS));
        list.add(new TabItemInfo(context.getResources().getString(R.string.tab_programmes),
                SearchType.CATEGORY_PROGRAM));

//        list.add(new TabItemInfo(context.getResources().getString(R.string.tab_moment),
//                SearchType.CATEGORY_MOMENT));
        return list;
    }

    /**
     * 根据type获取fragment
     */
    public static Class<? extends Fragment> getFragmentClassByType(@NonNull int tabType) {
        switch (tabType) {
            default:
                return SearchCommonFragment.class;
        }
    }

}
