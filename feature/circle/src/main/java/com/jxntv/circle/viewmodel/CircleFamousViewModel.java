package com.jxntv.circle.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.account.presistence.UserManager;
import com.jxntv.base.BaseRefreshLoadMoreViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.circle.CircleRepository;
import com.jxntv.circle.adapter.CircleFamousAdapter;
import com.jxntv.network.NetworkUtils;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;


import static com.jxntv.media.MediaConstants.DEFAULT_PAGE_COUNT;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class CircleFamousViewModel extends BaseRefreshLoadMoreViewModel {

    public Long groupId;
    public CircleFamousAdapter adapter;
    private CircleRepository circleRepository;
    public CheckThreadLiveData<Boolean> mAutoRefreshLiveData = new CheckThreadLiveData<>();

    public CircleFamousViewModel(@NonNull Application application) {
        super(application);
        circleRepository = new CircleRepository();
        adapter = new CircleFamousAdapter();
    }

    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return adapter;
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            adapter.replaceData(null);
            adapter.showNetworkNotAvailablePlaceholder();
        }else {
            adapter.hidePlaceholder();
            mAutoRefreshLiveData.setValue(true);
        }
    }

    @Override
    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onRefresh(view);
        circleRepository.circleFamousList(
                groupId,
                1,
                DEFAULT_PAGE_COUNT
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoRefreshObserver<>(view));
    }

    @Override
    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onLoadMore(view);
        if (adapter.getData().isEmpty()) {
            adapter.showLoadingPlaceholder();
        }
        circleRepository.circleFamousList(
                groupId,
                mLocalPage.getPageNumber(),
                DEFAULT_PAGE_COUNT
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GVideoLoadMoreObserver<>(view));
    }

}
