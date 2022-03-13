package com.jxntv.feed;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.jxntv.base.BaseFragment;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * feed通过弱引用临时存储fragment，便于其他模块调用找到对应fragment
 */
public class FeedFragmentManager {

    /** 持有的单例 */
    private volatile static FeedFragmentManager sInstance = null;
    /** 持有fragment的map */
    private Map<String, WeakReference<BaseFragment>> mFragmentHashMap;

    /**
     * 获取manager单例
     */
    public static FeedFragmentManager getInstance() {
        if (sInstance == null) {
            synchronized (FeedFragmentManager.class) {
                if (sInstance == null) {
                    sInstance = new FeedFragmentManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 构造函数
     */
    private FeedFragmentManager() {
        mFragmentHashMap = new ConcurrentHashMap<>();
    }

    /**
     * 增加Fragment引用
     *
     * @param fragment  待存储引用的fragment
     */
    public void addFragmentRef(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        mFragmentHashMap.put(fragment.getGvFragmentId(), new WeakReference<>(fragment));
    }

    /**
     * 根据id获取对应的fragment
     *
     * @param id    fragment id
     * @return 返回对应的fragment
     */
    @Nullable
    public BaseFragment getFragment(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        WeakReference<BaseFragment> ref = mFragmentHashMap.get(id);
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
        mFragmentHashMap.clear();
        mFragmentHashMap = null;
        sInstance = null;
    }
}
