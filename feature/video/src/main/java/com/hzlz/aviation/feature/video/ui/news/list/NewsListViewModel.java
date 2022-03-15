package com.hzlz.aviation.feature.video.ui.news.list;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.video.repository.MediaRepository;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.library.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/5/20
 * desc : 专题
 **/
public class NewsListViewModel extends MediaPageViewModel {

    public final String SPECIAL_LIST = "special_list";
    public final String NEWS_LIST = "news_list";

    private MediaRepository repository = new MediaRepository();

    private CheckThreadLiveData<VideoModel> videoModel = new CheckThreadLiveData<>();

    private String id;
    private boolean isSpecial = false;

    public CheckThreadLiveData<VideoModel> getVideoModel() {
        return videoModel;
    }

    public NewsListViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected IRecyclerModel createModel() {
        return new IRecyclerModel<MediaModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<MediaModel> loadListener) {
                loadNewsData(page);
            }

            @Override
            public void initialData(RecyclerViewLoadListener<MediaModel> loadListener) {
                mAdapter.clearList();
                loadNewsData(1);
            }
        };
    }

    public void loadData(String mediaId) {
        if(!NetworkUtils.isNetworkConnected()){
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            return;
        }
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
        repository.loadMedia(mediaId).subscribe(new GVideoResponseObserver<VideoModel>() {
            @Override
            protected void onSuccess(@NonNull VideoModel videoModel) {
                getVideoModel().setValue(videoModel);
                updatePlaceholderLayoutType(PlaceholderType.NONE);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                updatePlaceholderLayoutType(PlaceholderType.ERROR);
            }
        });
    }

    /**
     * 是否是专题
     */
    public void setSpecial(boolean isSpecial){
        this.isSpecial = isSpecial;
    }

    public void refresh(String id){
        this.id = id;
        loadNewsData(1);
    }

    private void loadNewsData(int currentPageNum){
        if (TextUtils.isEmpty(id)){
            return;
        }
        if (currentPageNum == 1 && (mMediaModelList == null || mMediaModelList.size() == 0 ) && !isSpecial) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        repository.loadNewsList(id,currentPageNum,isSpecial)
                .subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    protected void onSuccess(@NonNull ListWithPage<MediaModel> netData) {
                        List<MediaModel> mediaModelList = netData.getList();
                        mediaModelList = mediaModelList == null ? new ArrayList<>() : mediaModelList;

                        if (currentPageNum != 1) {
                           loadSuccess(mediaModelList);
                        } else {
                            for (int i = 0; i < mediaModelList.size(); i++) {
                                MediaModel mediaModel = mediaModelList.get(i);
                                if (mediaModel == null) {
                                    continue;
                                }
                                mediaModel.setPid(getPid());
                                mediaModel.tabId = (!isSpecial ? NEWS_LIST : SPECIAL_LIST) + id;
                                mediaModel.showMediaPageSource = MediaPageSource.PageSource.NEWS;
                            }
                            mAdapter.clearList();
                            loadSuccess(mediaModelList);
                            if (isSpecial && mAdapter.getItemCount() == 0) {
                                //专题没有数据时不显示空布局
                                updatePlaceholderLayoutType(PlaceholderType.NONE);
                            }
                        }
                        loadComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        LogUtils.e(throwable.getMessage());
                        updatePlaceholderLayoutType(PlaceholderType.NONE);
                    }
                });
    }

}
