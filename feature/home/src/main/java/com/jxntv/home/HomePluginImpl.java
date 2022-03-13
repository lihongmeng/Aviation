package com.jxntv.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.home.repository.HomeRepository;
import com.jxntv.utils.AppManager;

import io.reactivex.rxjava3.core.Observable;

/**
 * home模块跳转实现类
 */
public class HomePluginImpl implements HomePlugin {

    public void startBlankActivity(Context context){
        if(context==null){
            return;
        }
        context.startActivity( new Intent(context, SplashActivity.class));
    }

    @Override
    public void navigateToHomeActivity(@NonNull Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void navigateToHomePersonFragment(@NonNull Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(KEY_TRANS_TAB, TRANS_PERSONAL_TAB);
        context.startActivity(intent);
    }

    @Override
    public boolean hasHomeActivity(Context context, Bundle bundle) {
        if (AppManager.getAppManager().getActivity(HomeActivity.class) == null){
            Intent intent = new Intent(context,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtras(bundle);
            context.startActivity(intent);
//            PluginManager.get(ChatIMPlugin.class).startActivity("HomeActivity",bundle);
            return false;
        }
        return true;
    }

    @Override
    public void restartApp(@NonNull Context context) {

        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public Observable<BannerModel> getBannerTopList(long locationId) {
        return HomeRepository.getInstance().getBannerTopList(locationId);
    }

}
