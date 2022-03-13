package com.jxntv.account.ui.ugc.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.account.R;
import com.jxntv.account.model.Author;
import com.jxntv.account.model.Media;
import com.jxntv.account.model.UgcMyCommentModel;
import com.jxntv.account.repository.FavoriteRepository;
import com.jxntv.account.ui.ugc.adapter.UgcMyCommentAdapter;
import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.recycler.MediaPageViewModel;
import com.jxntv.network.response.ListWithPage;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 */
public final class UgcMyCommentViewModel extends BaseRefreshLoadMoreViewModel {

    private AuthorModel mFromAuthor;

    public UgcMyCommentAdapter adapter = new UgcMyCommentAdapter();

    @NonNull
    private FavoriteRepository mFavoriteRepository = new FavoriteRepository();

    public UgcMyCommentViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return adapter;
    }


    @Override
    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onRefresh(view);
        mFavoriteRepository.getUgcMyCommentList(mFromAuthor,1)
                .subscribe(new GVideoRefreshObserver<UgcMyCommentModel>(view){
                    @Override
                    protected void onSuccess(@Nullable List<UgcMyCommentModel> dataList) {
                        super.onSuccess(dataList);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
    }


    @Override
    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onLoadMore(view);
        mFavoriteRepository.getUgcMyCommentList(mFromAuthor,mLocalPage.getPageNumber()).subscribe(new GVideoLoadMoreObserver<>(view));
    }

    void setFromAuthor(AuthorModel author) {
        mFromAuthor = author;
    }


}
