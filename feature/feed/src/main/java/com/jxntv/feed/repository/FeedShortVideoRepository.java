package com.jxntv.feed.repository;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.model.video.VideoModel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * feed短视频仓库类
 */
public class FeedShortVideoRepository {

    /** 持有的单例 */
    private volatile static FeedShortVideoRepository sInstance = null;
    /** 持有的tabid 对应的短视频列表数据 */
    private ConcurrentHashMap<String, List<VideoModel>> mShortVideoModelsList = new ConcurrentHashMap<>();

    /**
     * 构造函数
     */
    public static FeedShortVideoRepository getInstance() {
        if (sInstance == null) {
            synchronized (FeedShortVideoRepository.class) {
                if (sInstance == null) {
                    sInstance = new FeedShortVideoRepository();
                }
            }
        }
        return sInstance;
    }

    /**
     * 根据tabId 获取List
     */
    @Nullable
    public List<VideoModel> getShortVideoList(String tabId) {
        return mShortVideoModelsList.get(tabId);
    }

    /**
     * 获取对应的cursor
     *
     * @param models    列表
     * @param address   待校验的address
     */
    public int getCurrentCursor(List<VideoModel> models, int address) {
        if (models == null) {
            return 0;
        }
        VideoModel model;
        for (int i = 0; i < models.size(); i++) {
            model = models.get(i);
            if (model.getMemoryAddress() == address) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 更新本地list
     */
    public void updateShortVideoList(String tabId, @NonNull List<VideoModel> list, boolean isAdd) {
        if (TextUtils.isEmpty(tabId)) {
            return;
        }
        if (isAdd) {
            List<VideoModel> tempList = mShortVideoModelsList.get(tabId);
            if (tempList != null) {
                tempList.addAll(list);
                return;
            }
        }
        mShortVideoModelsList.put(tabId, list);
    }
}
