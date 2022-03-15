package com.hzlz.aviation.feature.community.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.adapter.BaseFragmentVpAdapter;
import com.hzlz.aviation.kernel.base.model.circle.MyCircle;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;

import java.util.concurrent.TimeUnit;

public class HomeCommunityViewModel extends BaseViewModel {

    // CircleRepository
    private final CircleRepository circleRepository;
    public BaseFragmentVpAdapter circleVpAdapter;

    public CheckThreadLiveData<MyCircle> myCircle = new CheckThreadLiveData<>();

    public HomeCommunityViewModel(@NonNull Application application) {
        super(application);
        circleRepository = new CircleRepository();
    }

    public void initData() {
        circleRepository.myCircle()
                .timeout(CircleRepository.LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new GVideoResponseObserver<MyCircle>() {

                               @Override
                               protected void onRequestStart() {
                                   super.onRequestStart();
                                   if (isDataNull()) {
                                       updatePlaceholderLayoutType(PlaceholderType.LOADING);
                                   }
                               }

                               @Override
                               protected void onSuccess(@NonNull MyCircle netData) {
                                   myCircle.setValue(netData);
                                   updatePlaceholderLayoutType(PlaceholderType.NONE);
                               }

                               @Override
                               public void onFailed(Throwable throwable) {
                                   myCircle.setValue(null);
                                   updatePlaceholderLayoutType(PlaceholderType.NONE);
                               }
                           }
                );
    }

    private boolean isDataNull() {
        return circleVpAdapter == null || circleVpAdapter.getCount() <= 0;
    }

}
