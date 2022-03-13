package com.jxntv.account.ui.favortite;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.account.R;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.Media;
import com.jxntv.account.repository.FavoriteRepository;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.media.MediaPageSource;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageViewModel;
import com.jxntv.network.response.ListWithPage;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * 喜欢 ViewModel
 *
 * @since 2020-02-12 15:29
 */
public final class FavoriteViewModel extends MediaPageViewModel {

    private Author mFromAuthor;
    @NonNull
    private FavoriteRepository mFavoriteRepository = new FavoriteRepository();

    private CheckThreadLiveData<Boolean> favoriteData = new CheckThreadLiveData();

    public CheckThreadLiveData<Boolean> getFavoriteData(){
        return favoriteData;
    }

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {
        return new IRecyclerModel<MediaModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<MediaModel> loadListener) {

                mFavoriteRepository.getFavoriteDetailList(mFromAuthor,"1",20,page)
                        .subscribe(new GVideoResponseObserver<ListWithPage<Media>>() {

                            @Override
                            protected void onRequestStart() {
                                super.onRequestStart();
                                if (mMediaModelList.isEmpty()) {
                                    updatePlaceholderLayoutType(PlaceholderType.LOADING);
                                }
                            }

                            @Override
                            protected void onSuccess(@NonNull ListWithPage<Media> mediaListWithPage) {
                                List<Media> list = mediaListWithPage.getList();
                                List<MediaModel> modelList = new ArrayList<>();
                                for (Media media : list) {
                                    MediaModel model = new MediaModel(media);
                                    model.tabId = mTabId;
                                    model.setPid(getPid());
                                    model.showMediaPageSource = MediaPageSource.PageSource.PGC;
                                    model.correspondVideoModelAddress = media.getMemoryAddress();
                                    modelList.add(model);
                                }
                                if (modelList.size() <= 0) {
                                    if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                                        showToast(R.string.all_nor_more_data);
                                    }else if (page==1){

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

    void setFromAuthor(Author author) {
        mFromAuthor = author;
    }

}
