package com.jxntv.account.ui.me;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jxntv.account.model.User;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.account.ui.ugc.UgcBaseViewModel;
import com.jxntv.base.plugin.ChatIMPlugin;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.event.entity.DrawerLayoutData;
import com.jxntv.ioc.PluginManager;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

public final class MeViewModel extends UgcBaseViewModel {

    public MeViewModel(@NonNull Application application) {
        super(application);
    }

    public void openDrawer() {
        GVideoEventBus.get(HomePlugin.EVENT_HOME_DRAWER)
                .post(new DrawerLayoutData(true,getPid()));
    }

    @Override
    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        if (UserManager.hasLoggedIn()){
            setAuthorId(UserManager.getCurrentUser().getId());
        }
        super.onRefresh(view);

        mUserRepository.getCurrentUser().subscribe(new GVideoResponseObserver<User>() {
            @Override
            protected void onSuccess(@NonNull User user) {
                PluginManager.get(ChatIMPlugin.class).updateProfile();
            }
        });
    }
}
