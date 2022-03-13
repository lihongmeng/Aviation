package com.jxntv.feed.frame.recycler;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jxntv.feed.template.FeedFactory;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaRecyclerAdapter;
import com.jxntv.media.recycler.MediaRecyclerVH;
import com.jxntv.media.template.IMediaTemplate;

import java.util.List;

/**
 * feed recyclerview适配器
 */
public class FeedRecyclerAdapter extends MediaRecyclerAdapter {

    /**
     * 构造函数
     */
    public FeedRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public MediaRecyclerVH onCreateVH(ViewGroup parent, int viewType) {
        IMediaTemplate v = FeedFactory.createInstance(mContext, viewType, parent);
        return new MediaRecyclerVH(v);
    }

    @Override
    public void onBindVH(MediaRecyclerVH FeedRecyclerVH, int position) {
        final MediaModel model = mList.get(position);
        model.viewPosition = position;
        final IMediaTemplate view = FeedRecyclerVH.getFeedTemplate();

        view.setViewGroup(mParentViewGroup);
        view.update(model, mIsDarkMode, mFragmentId,position);
        mShowLastPosition = position;
    }

    @Override
    public void refreshData(@NonNull List<MediaModel> data) {
        data.addAll(mList);
        super.refreshData(data);
        int changeItemPosition = onUpdateTopStick();
        if (changeItemPosition < mList.size() && changeItemPosition >= 0) {
            notifyItemChanged(changeItemPosition);
        }
    }

    @Override
    public void loadMoreData(@NonNull List<MediaModel> data) {
        super.loadMoreData(data);
        int changeItemPosition = onUpdateTopStick();
        if (changeItemPosition < mList.size() && changeItemPosition >= 0) {
            notifyItemChanged(changeItemPosition);
        }
    }

    /**
     * 更新本地置顶数据
     */
    private int onUpdateTopStick() {
        int result = -1;
        for (int i = 1; i < mList.size(); i++) {
            MediaModel media = mList.get(i);
            if (media.isStick()) {
                result = i;
                media.setIsStick(false);
                break;
            }
        }
        return result;
    }
}
