package com.hzlz.aviation.feature.home.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.home.request.BannerPopRequest;
import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;

public class HomeRepository extends BaseDataRepository {

    private volatile static HomeRepository singleInstance = null;

    private HomeRepository(){
    }

    public static HomeRepository getInstance(){
        if (singleInstance == null) {
            synchronized (HomeRepository.class) {
                if (singleInstance == null) {
                    singleInstance = new HomeRepository();
                }
            }
        }
        return singleInstance;
    }

    @NonNull
    public Observable<BannerModel> getBannerTopList(long locationId) {
        return new NetworkData<BannerModel>(mEngine) {
            @Override
            protected BaseRequest<BannerModel> createRequest() {
                BannerPopRequest request = new BannerPopRequest();
                // 设置0即可，可以不做理会
                request.setScene(0);
                request.setLocationId(locationId);
                return request;
            }

            @Override
            protected void saveData(BannerModel model) {
            }
        }.asObservable();
    }

}
