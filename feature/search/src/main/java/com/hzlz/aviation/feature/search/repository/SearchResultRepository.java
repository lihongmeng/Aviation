package com.hzlz.aviation.feature.search.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hzlz.aviation.feature.search.model.SearchModel;
import com.hzlz.aviation.feature.search.utils.SearchUtils;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果仓库类
 */
public class SearchResultRepository {

    /** 持有的单例 */
    private volatile static SearchResultRepository sInstance = null;
    /** 本地存储的短视频列表 */
    private List<VideoModel> mShortVideoModelList = new ArrayList<>();

    private MutableLiveData<SearchModel> mSearchLiveData = new MutableLiveData<>();

    private SearchModel mEmptySearchModel;
    /**
     * 构造函数
     */
    public static SearchResultRepository getInstance() {
        if (sInstance == null) {
            synchronized (SearchResultRepository.class) {
                if (sInstance == null) {
                    sInstance = new SearchResultRepository();
                }
            }
        }
        return sInstance;
    }

    public LiveData<SearchModel> getLiveData() {
      return mSearchLiveData;
    }

    /**
     * 刷新搜索数据
     *
     * @param model 搜索模板数据
     */
    public synchronized void refreshSearchModel(SearchModel model) {
        if (model != null && model.mNews != null) {
            mShortVideoModelList = SearchUtils.buildVideoModelList(model.mNews);
        }
        if (model != null) {
          mSearchLiveData.postValue(model);
        }
    }

    /**
     * 更新短视频资源
     *
     * @param videoModelList 待添加的短视频数据
     */
    public synchronized void updateShortVideo(List<VideoModel> videoModelList) {
        if (videoModelList != null) {
            mShortVideoModelList.addAll(videoModelList);
        }
    }

    /**
     * 获取短视频资源
     *
     * @return 短视频列表
     */
    public synchronized List<VideoModel> getShortVideo() {
        return mShortVideoModelList;
    }

    public void changeToEmptyResult() {
        if (mEmptySearchModel == null) {
            createEmptySearchModel();
        }
        refreshSearchModel(mEmptySearchModel);
    }

    private void createEmptySearchModel() {
        SearchModel searchModel = new SearchModel();
        searchModel.mAuthors = new ArrayList<>();
        searchModel.mAll = new ArrayList<>();
        searchModel.mCommunities = new ArrayList<>();
        searchModel.mProgrammes = new ArrayList<>();
        searchModel.mNews = new ArrayList<>();
        mEmptySearchModel = searchModel;
    }

}
