package com.hzlz.aviation.feature.account.ui.pgc;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.feature.account.repository.AuthorRepository;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


/**
 * PGC 页面 ViewModel
 */
public final class PgcContentViewModel extends MediaPageViewModel {
    /**
     * PGC uid
     */
    private String mAuthorId;
    private int mAuthorType;
    private int mMediaTab;
    @NonNull
    private AuthorRepository mAuthorRepository = new AuthorRepository();

    public PgcContentViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 设置PGC uid
     */
    public void setAuthorId(String authorId, int authorType,int mediaTab) {
        mAuthorId = authorId;
        mAuthorType = authorType;
        mMediaTab = mediaTab;
    }

//    public void onRefresh() {
//        loadRefreshData();
//    }
//
//    public void onLoadMore() {
//        loadMoreData();
//    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {

        return new IRecyclerModel<MediaModel>() {
            @Override
            public void loadData(int page, RecyclerViewLoadListener<MediaModel> loadListener) {

                if (mMediaModelList==null || mMediaModelList.size()==0){
                    updatePlaceholderLayoutType(PlaceholderType.LOADING);
                }
                Author author = new Author();
                author.setId(mAuthorId);
                author.setType(mAuthorType);
                mAuthorRepository
                        .getAuthorMediaList(author,mMediaTab, page, DEFAULT_PAGE_COUNT)
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
                                    //model.playState = 0;
                                    //model.viewPosition = 0;
                                    model.correspondVideoModelAddress = media.getMemoryAddress();
                                    modelList.add(model);
                                }
                                loadSuccess(modelList);
                                loadComplete();
                            }

                            @Override
                            public void onFailed(Throwable throwable) {
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


}
