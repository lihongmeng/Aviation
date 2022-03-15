package com.hzlz.aviation.feature.account.ui.me;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.account.ui.ugc.UgcBaseViewModel;
import com.hzlz.aviation.kernel.base.plugin.ChatIMPlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.event.entity.DrawerLayoutData;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

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
