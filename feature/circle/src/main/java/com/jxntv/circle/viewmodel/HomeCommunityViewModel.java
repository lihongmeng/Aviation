package com.jxntv.circle.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.adapter.BaseFragmentVpAdapter;
import com.jxntv.base.model.circle.MyCircle;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.circle.CircleRepository;

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
