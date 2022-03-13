package com.jxntv.account.ui.pgc;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.Media;
import com.jxntv.account.repository.AuthorRepository;
import com.jxntv.account.repository.InteractionRepository;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.MediaPageSource;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageViewModel;
import com.jxntv.network.exception.GVideoAPIException;
import com.jxntv.network.exception.GVideoCode;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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
                                    showToast(com.jxntv.media.R.string.all_network_not_available);
                                    loadComplete();
                                    return;
                                }
                                showToast(com.jxntv.media.R.string.all_nor_more_data);
                                loadComplete();
                            }
                        });
            }
        };
    }


}
