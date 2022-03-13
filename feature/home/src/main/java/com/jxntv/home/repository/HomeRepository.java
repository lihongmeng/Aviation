package com.jxntv.home.repository;

import androidx.annotation.NonNull;

import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.home.request.BannerPopRequest;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.request.BaseRequest;

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
