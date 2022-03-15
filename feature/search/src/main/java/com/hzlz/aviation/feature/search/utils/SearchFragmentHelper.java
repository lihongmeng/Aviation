package com.hzlz.aviation.feature.search.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.search.page.fragment.SearchShortVideoFragment;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * serach模块通过弱引用临时存储fragment，便于其他模块调用找到对应fragment
 */
public class SearchFragmentHelper {

    /** 持有的单例 */
    private volatile static SearchFragmentHelper sInstance = null;
    /** 持有fragment的map */
    private Map<String, WeakReference<SearchShortVideoFragment>> mShortFragmentHashMap;

    /**
     * 获取单例
     */
    public static SearchFragmentHelper getInstance() {
        if (sInstance == null) {
            synchronized (SearchFragmentHelper.class) {
                if (sInstance == null) {
                    sInstance = new SearchFragmentHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 构造函数
     */
    private SearchFragmentHelper() {
        mShortFragmentHashMap = new ConcurrentHashMap<>();
    }

    /**
     * 增加短视频页Fragment引用
     *
     * @param fragment  待存储引用的fragment
     */
    public void addShortVideoFragmentRef(SearchShortVideoFragment fragment) {
        if (fragment == null) {
            return;
        }
        mShortFragmentHashMap.put(fragment.getGvFragmentId(), new WeakReference<>(fragment));
    }

    /**
     * 根据id获取对应的短视频fragment
     *
     * @param id    fragment id
     * @return 返回对应的fragment
     */
    @Nullable
    public SearchShortVideoFragment getShortVideoFragment(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        WeakReference<SearchShortVideoFragment> ref = mShortFragmentHashMap.get(id);
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    /**
     * 释放本地资源
     */
    public static void release() {
        if (sInstance != null) {
            sInstance.realRelease();
        }
    }

    /**
     * 释放本地资源
     */
    private void realRelease() {
        mShortFragmentHashMap.clear();
        mShortFragmentHashMap = null;
        sInstance = null;
    }
}
