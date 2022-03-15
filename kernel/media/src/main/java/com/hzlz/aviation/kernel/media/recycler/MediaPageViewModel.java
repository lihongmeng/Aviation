package com.hzlz.aviation.kernel.media.recycler;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewModel;
import com.hzlz.aviation.kernel.media.MediaConfig;
import com.hzlz.aviation.kernel.media.MediaConstants;
import com.hzlz.aviation.kernel.media.model.MediaModel;

import java.util.ArrayList;
import java.util.List;

/**
 * media page页面处理逻辑，主要处理评论、关注、收藏等状态变化时数据刷新，数据拉取等逻辑交由子类各自实现；
 */
public abstract class MediaPageViewModel extends BaseRecyclerViewModel<MediaModel> {

    protected int DEFAULT_PAGE_COUNT = MediaConstants.DEFAULT_PAGE_COUNT;

    /**
     * 当前tabId
     */
    protected String mTabId;

    /**
     * 当前tab名称
     */
    protected String tabName;

    /**
     * 当前数据持有列表，更新评论、关注、收藏等状态时使用
     */
    protected List<MediaModel> mMediaModelList = new ArrayList<>();

    public MediaPageViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 更新tab id
     *
     * @param tabId 待更新的tabId
     */
    public void updateTabId(String tabId) {
        mTabId = tabId;
    }

    public void updateTabName(String tabName) {
        this.tabName = tabName;
    }

    /**
     * 尝试自动播放下一个
     *
     * @param position 待播放位置
     */
    public void tryAutoPlayNext(int position) {
        if (!MediaConfig.isSupportAutoPlayNext()) {
            return;
        }
        if (mAdapter instanceof MediaRecyclerAdapter) {
            MediaRecyclerAdapter adapter = (MediaRecyclerAdapter) mAdapter;
            if (mView instanceof MediaPageFragment && adapter.canPlay(position + 1)) {
                ((MediaPageFragment) mView).playNext(position + 1);
            }
            // 预加载，当前视频播放结束时，加载下一页内容
            if (position + 2 >= mAdapter.getItemCount()) {
                loadMoreData();
            }
        }
    }

    @Override
    public void loadSuccess(List<MediaModel> list) {
        super.loadSuccess(list);
        if (loadType != LOAD_DATA_TYPE_LOAD_MORE) {
            mMediaModelList.clear();
        }
        //TODO 图片预加载
        // if (list!=null && list.size()>0){
        //     for (MediaModel model:list){
        //         if (TextUtils.isEmpty(model.getCoverUrl())){
        //             ImageLoaderManager.preload(GVideoRuntime.getAppContext(),model.getCoverUrl());
        //         }
        //         if (model.getImageUrls()!=null){
        //             for (int i =0;i<model.getImageUrls().size();i++){
        //                 ImageLoaderManager.preload(GVideoRuntime.getAppContext(),model.getImageUrls().get(i));
        //             }
        //         }
        //     }
        // }
        mMediaModelList.addAll(list);
    }

    /**
     * 点击短视频进入详情页，传入列表
     * 希望自定义，可以重写此方法
     */
    public ShortVideoListModel createShortListModel(MediaModel mediaModel) {
        if (mMediaModelList != null && !mMediaModelList.isEmpty()) {
            List<VideoModel> shortList = new ArrayList<>();
            int i = 0;
            int cursor = 0;
            for (MediaModel media : mMediaModelList) {
                if (media != null && media.isShortMedia()) {
                    shortList.add(media);
                    if (media.correspondVideoModelAddress == mediaModel.correspondVideoModelAddress) {
                        cursor = i;
                    }
                    i++;
                }
            }
            return ShortVideoListModel.Builder.aFeedModel()
                    .withList(shortList)
                    .withCursor(String.valueOf(cursor))
                    .withLoadMore(false)
                    .build();
        }
        return null;
    }
}
