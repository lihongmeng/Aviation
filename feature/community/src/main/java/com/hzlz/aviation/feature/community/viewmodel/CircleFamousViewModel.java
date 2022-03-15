package com.hzlz.aviation.feature.community.viewmodel;

import static com.hzlz.aviation.kernel.media.MediaConstants.DEFAULT_PAGE_COUNT;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.feature.community.adapter.CircleFamousAdapter;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

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
