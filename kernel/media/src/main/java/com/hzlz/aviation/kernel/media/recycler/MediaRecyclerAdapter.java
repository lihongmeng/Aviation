package com.hzlz.aviation.kernel.media.recycler;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerHeaderFooterAdapter;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.template.IMediaTemplate;
import com.hzlz.aviation.kernel.media.template.MediaFactory;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;

/**
 * media recyclerview适配器
 */
public class MediaRecyclerAdapter extends BaseRecyclerHeaderFooterAdapter<MediaModel, MediaRecyclerVH> {

    /** 是否为暗黑模式 */
    protected boolean mIsDarkMode = false;
    /** tab id */
    protected String mTabId;
    /** 父容器 */
    protected ViewGroup mParentViewGroup;
    /** fragment id */
    protected String mFragmentId;

    protected int mShowLastPosition = -1;

    /**
     * 构造函数
     */
    public MediaRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public MediaRecyclerVH onCreateVH(ViewGroup parent, int viewType) {
        IMediaTemplate v = MediaFactory.createInstance(mContext, viewType, parent);
        return new MediaRecyclerVH(v);
    }


    @Override
    public void onBindVH(MediaRecyclerVH mediaRecyclerVH, int position) {
        final MediaModel model = mList.get(position);
        model.viewPosition = position;
        final IMediaTemplate view = mediaRecyclerVH.getFeedTemplate();

        view.setViewGroup(mParentViewGroup);
        view.update(model, mIsDarkMode, mFragmentId,position);
        mShowLastPosition = position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList == null || position < getHeaderViewCount() || position >= getHeaderViewCount() + mList.size()) {
            return super.getItemViewType(position);
        }
        return mList.get(position - getHeaderViewCount()).getMediaType();
    }

    /**
     * 是否可以播放
     *
     * @param position  待检查的位置
     * @return 是否可以播放
     */
    public boolean canPlay(int position) {
        //列表最后一个不可自动播放
        if (position<0 || position >= mList.size()) {
            return false;
        }
        MediaModel model = mList.get(position);
        return model != null && model.isCanAutoPlay();
    }

    /**
     * 设置暗黑模式
     *
     * @param isDarkMode    是否为暗黑模式
     */
    public void setIsDarkMode(boolean isDarkMode) {
        mIsDarkMode = isDarkMode;
    }

    /**
     * 设置tabId
     */
    public void setTabId (String tabId) {
        mTabId = tabId;
    }

    /**
     * 设置fragment id
     */
    public void setFragmentId(String fragmentId) {
        mFragmentId = fragmentId;
    }

    /**
     * 设置父布局
     */
    public void setViewGroup(ViewGroup group) {
        mParentViewGroup = group;
    }

    /**
     * 删除数据
     * @return 删除后数据
     */
    public int remove(String mediaId){
        for (int i=0;i<mList.size();i++){
            if (TextUtils.equals(mediaId,mList.get(i).getId())){
                mList.remove(i);
                notifyDataSetChanged();
            }
        }
        return mList.size();
    }

    public void enterPosition(int position,String pid){
        if (position < mList.size()) {
            GVideoSensorDataManager.getInstance().postExposure(mList.get(position), pid, true);
        }
    }

    public void exitPosition(int position,String pid){
        if (position < mList.size()) {
            GVideoSensorDataManager.getInstance().postExposure(mList.get(position), pid, false);
        }
    }

    /**
     * 屏幕中最后一个显示的位置
     */
    public int getLastPosition(){
        return mShowLastPosition;
    }

}
