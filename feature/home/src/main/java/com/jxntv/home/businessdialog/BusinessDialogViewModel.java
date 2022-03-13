package com.jxntv.home.businessdialog;

import static com.jxntv.stat.StatPid.HOME_RECOMMEND;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.home.repository.HomeRepository;

public class BusinessDialogViewModel extends BaseViewModel {

    public final CheckThreadLiveData<BannerModel.ImagesBean> imagesBeanLiveData =new CheckThreadLiveData<>();

    public BusinessDialogViewModel(@NonNull Application application) {
        super(application);
    }

    public void requireBusinessDialogData(){
        HomeRepository.getInstance()
                .getBannerTopList(Integer.parseInt(HOME_RECOMMEND))
                .subscribe(new GVideoResponseObserver<BannerModel>() {

                    @Override
                    protected void onSuccess(@NonNull BannerModel bannerModel) {
                        if (!BannerModel.isDataValid(bannerModel)) {
                            imagesBeanLiveData.setValue(null);
                            return;
                        }
                        BannerModel.ImagesBean imagesBean = bannerModel.getImages().get(0);
                        if (imagesBean == null) {
                            imagesBeanLiveData.setValue(null);
                            return;
                        }
                        imagesBeanLiveData.setValue(imagesBean);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                    }
                });
    }
}
