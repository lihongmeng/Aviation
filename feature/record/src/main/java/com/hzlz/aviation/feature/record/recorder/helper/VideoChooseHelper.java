package com.hzlz.aviation.feature.record.recorder.helper;

import static com.hzlz.aviation.feature.record.recorder.helper.RecordSharedPrefs.KEY_SELECT_VIDEO_MAX_TIME;
import static com.hzlz.aviation.feature.record.recorder.helper.RecordSharedPrefs.SELECT_VIDEO_MAX_TIME_DEFAULT;

import android.text.TextUtils;

import com.hzlz.aviation.feature.record.recorder.data.ImageVideoEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 视频选择辅助类
 */
public class VideoChooseHelper {

    /**
     * 视频选择数量上限
     */
    private static final int VIDEO_NUM_LIMIT = 1;
    /**
     * 图片选择数量上限
     */
    public static final int DEFAULT_MAX_SELECT_IMAGE_NUM = 9;  // 默认情况下的阀值
    public static int IMAGE_NUM_LIMIT = DEFAULT_MAX_SELECT_IMAGE_NUM; // 全局变量，可能会被改变
    /**
     * 视频选择时间上限
     */
    private long mChooseTotalTimeLime;
    /**
     * 辅助时间，用于容错
     */
    private static final long HELP_TIME = 1;
    /**
     * 用于时长转换 1000
     */
    private static final int ONE_THOUSAND = 1000;
    /**
     * 当前持有的视频数据模型
     */
    private List<ImageVideoEntity> mImageVideoEntities = new ArrayList<>();
    private long mTotalSelectTime = 0;
    /**
     * 物料选择监听器
     */
    private ItemSelectListener mItemSelectListener;
    /**
     * 当前预览视频物料
     */
    private ImageVideoEntity mPreviewImageVideoEntity;
    /**
     * 持有的单例
     */
    private volatile static VideoChooseHelper sInstance = null;

    private File mChooseResultFile = null;

    /**
     * 单例获取
     */
    public static VideoChooseHelper getInstance() {
        if (sInstance == null) {
            synchronized (VideoChooseHelper.class) {
                if (sInstance == null) {
                    sInstance = new VideoChooseHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 内部构造函数
     */
    private VideoChooseHelper() {
        int chooseLimit = RecordSharedPrefs.getInstance().getInt(KEY_SELECT_VIDEO_MAX_TIME, SELECT_VIDEO_MAX_TIME_DEFAULT);
        if (chooseLimit <= 0) {
            chooseLimit = SELECT_VIDEO_MAX_TIME_DEFAULT;
        }
        mChooseTotalTimeLime = (chooseLimit + HELP_TIME) * ONE_THOUSAND;
    }

    /**
     * 设置物料选取监听器
     *
     * @param listener 物料选取监听器
     */
    public void setItemSelectListener(ItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    /**
     * 增加选择视频
     *
     * @param entity 待增加的视频
     * @return 是否增加成功
     */
    public boolean addVideoEntity(ImageVideoEntity entity) {
        if (entity.isVideo && mImageVideoEntities.size() >= VIDEO_NUM_LIMIT) {
            return false;
        }
        if (!entity.isVideo && mImageVideoEntities.size() >= IMAGE_NUM_LIMIT) {
            return false;
        }
        if (!mImageVideoEntities.contains(entity)) {
            mImageVideoEntities.add(entity);
            mTotalSelectTime += entity.duration;
            entity.selectPosition = mImageVideoEntities.size();
            mChooseResultFile = null;
        }
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemNumChanged(mImageVideoEntities.size(), mTotalSelectTime);
        }
        return true;
    }

    /**
     * 移出选择视频
     *
     * @param entity 待移除的视频
     * @return 是否移除成功
     */
    public boolean removeVideoEntity(ImageVideoEntity entity) {
        Iterator<ImageVideoEntity> iterator = mImageVideoEntities.iterator();
        ImageVideoEntity imageVideoEntity;
        boolean hasFind = false;
        while (iterator.hasNext()) {
            imageVideoEntity = iterator.next();
            if (hasFind) {
                imageVideoEntity.selectPosition -= 1;
                continue;
            }
            if (imageVideoEntity == entity) {
                imageVideoEntity.selectPosition = -1;
                hasFind = true;
                iterator.remove();
                mTotalSelectTime -= imageVideoEntity.duration;
                mChooseResultFile = null;
            }
        }
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemNumChanged(mImageVideoEntities.size(), mTotalSelectTime);
        }
        return hasFind;
    }

    /**
     * 清空选择列表
     */
    public void clearSelectList() {
        mImageVideoEntities.clear();
        mTotalSelectTime = 0;
        mChooseResultFile = null;
    }

    /**
     * 设置预览视频
     *
     * @param entity 待预览的视频
     */
    public void setPreviewVideoEntity(ImageVideoEntity entity) {
        mPreviewImageVideoEntity = entity;
    }

    /**
     * 获取选择视频
     *
     * @return 当前预览视频
     */
    public ImageVideoEntity getPreviewVideoEntity() {
        return mPreviewImageVideoEntity;
    }

    /**
     * 获取选择视频
     *
     * @return 当前选择视频集
     */
    public List<ImageVideoEntity> getSelectEntity() {
        return mImageVideoEntities;
    }

    public ArrayList<String> getSelectAddressList() {
        ArrayList<String> result = new ArrayList<>();
        if (mImageVideoEntities == null) {
            return result;
        }
        for (ImageVideoEntity imageVideoEntity : mImageVideoEntities) {
            if (imageVideoEntity == null
                    || TextUtils.isEmpty(imageVideoEntity.path)) {
                continue;
            }
            result.add(imageVideoEntity.path);
        }
        return result;
    }

    public File getResultFile() {
        return mChooseResultFile;
    }

    public void updateResultFile(File result) {
        mChooseResultFile = result;
    }

    public boolean checkSelectAvailable() {
        return mTotalSelectTime < mChooseTotalTimeLime;
    }

    public boolean checkCanSelectImage(){
        return !(mImageVideoEntities.size() >= IMAGE_NUM_LIMIT);
    }

    /**
     * 物料选取监听器
     */
    public interface ItemSelectListener {

        /**
         * 已选择物料数量变化
         *
         * @param num  当前物料数量
         * @param time 当前选择总时长
         */
        void onItemNumChanged(int num, long time);
    }

}
