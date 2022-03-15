package com.hzlz.aviation.feature.account.ui.ugc.detail;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.repository.FavoriteRepository;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 */
public final class UgcContentViewModel extends MediaPageViewModel {

    private AuthorModel mFromAuthor;
    private @UgcContentType int contentType;

    @NonNull
    private FavoriteRepository mFavoriteRepository = new FavoriteRepository();

    public UgcContentViewModel(@NonNull Application application) {
        super(application);
        contentType = UgcContentType.COMPOSITION;
    }

    public void setContentType(@UgcContentType int contentType){
        this.contentType = contentType;
    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {
        return new IRecyclerModel<MediaModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<MediaModel> loadListener) {

                mFavoriteRepository.getUgcContentList(mFromAuthor,page,contentType)
                        .subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {

                            @Override
                            protected void onRequestStart() {
                                super.onRequestStart();
                                if (mMediaModelList.isEmpty()) {
                                    updatePlaceholderLayoutType(PlaceholderType.LOADING);
                                }
                            }

                            @Override
                            protected void onSuccess(@NonNull ListWithPage<MediaModel> mediaListWithPage) {

                                List<MediaModel> modelList = mediaListWithPage.getList();
                                for (int i=0;i<modelList.size();i++){
                                    MediaModel mediaModel=modelList.get(i);
                                    if(mediaModel==null){
                                        continue;
                                    }
                                    mediaModel.setPid(getPid());
                                    mediaModel.tabId = mTabId;
                                    if (contentType==UgcContentType.COMPOSITION){
                                        mediaModel.showMediaPageSource = MediaPageSource.PageSource.MINE;
                                    }else if (contentType == UgcContentType.ANSWER){
                                        mediaModel.showMediaPageSource = MediaPageSource.PageSource.MINE_ANSWER;
                                    }
                                }
                                if (modelList.size() <= 0) {
                                    if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                                        showToast(R.string.all_nor_more_data);
                                    }
                                    if (page==1){
                                        loadSuccess(modelList);
                                    }
                                } else {
                                    loadSuccess(modelList);
                                }
                                loadComplete();
                            }

                            @Override
                            protected void onFailed(@NonNull Throwable throwable) {
                                if (throwable instanceof TimeoutException ||
                                        throwable instanceof SocketTimeoutException) {
                                    showToast(R.string.all_network_not_available);
                                    loadComplete();
                                    return;
                                }
                                showToast(R.string.all_nor_more_data);
                                loadComplete();
                            }
                        });


            }

        };
    }

    void setFromAuthor(AuthorModel author) {
        mFromAuthor = author;
    }


}
