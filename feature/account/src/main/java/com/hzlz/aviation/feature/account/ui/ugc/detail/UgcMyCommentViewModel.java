package com.hzlz.aviation.feature.account.ui.ugc.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.model.UgcMyCommentModel;
import com.hzlz.aviation.feature.account.repository.FavoriteRepository;
import com.hzlz.aviation.feature.account.ui.ugc.adapter.UgcMyCommentAdapter;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.List;

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
