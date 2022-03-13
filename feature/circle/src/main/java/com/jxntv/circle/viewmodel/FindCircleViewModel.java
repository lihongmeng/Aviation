package com.jxntv.circle.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseViewModel;
import com.jxntv.circle.CircleRepository;
import com.jxntv.circle.databinding.FindCircleObservable;
import com.jxntv.base.model.circle.CircleTag;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FindCircleViewModel extends BaseViewModel {

    private CircleRepository circleRepository;

    public FindCircleObservable findCircleObservable;

    public FindCircleViewModel(@NonNull Application application) {
        super(application);
        circleRepository = new CircleRepository();
        findCircleObservable = new FindCircleObservable();
    }

    public void startLoadData() {
        circleRepository.findCircleTagList()
                .timeout(CircleRepository.LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new GVideoResponseObserver<List<CircleTag>>() {
                    @Override
                    protected void onSuccess(@NonNull List<CircleTag> dataList) {
                        if (CircleTag.isDataChange(findCircleObservable.circleTagList.getValue(), dataList)) {
                            findCircleObservable.circleTagList.setValue(dataList);
                            List<CircleTag> current = findCircleObservable.circleTagList.getValue();
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {

                    }
                });

    }

}
