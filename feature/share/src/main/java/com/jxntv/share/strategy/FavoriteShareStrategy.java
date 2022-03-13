package com.jxntv.share.strategy;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


import com.jxntv.base.BaseViewModel;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.FavoritePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.base.utils.ToastUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.share.R;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class FavoriteShareStrategy implements ShareStrategy {

    private Context mContext;

    public FavoriteShareStrategy(Context context) {
        mContext = context;
    }

    @Override
    public boolean canShare() {
        if (TextUtils.isEmpty(PluginManager.get(AccountPlugin.class).getToken())) {
            return false;
        }
        return true;
    }

    @Override
    public void share(ShareDataModel model) {

    }

    public void share(ShareDataModel dataModel, String pid) {
        if (!canShare()) {
            Toast.makeText(mContext, R.string.share_login_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(mContext)) {
            return;
        }
        String mediaId = dataModel.getMediaId();
        boolean favorite = !dataModel.isFavorite();
        if (dataModel.isContentIsComment()) {
            PluginManager.get(VideoPlugin.class).commentPraise(mediaId, favorite,
                    new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
                        @Override
                        protected void onRequestData(Boolean aBoolean) {
                            super.onRequestData(aBoolean);
                        }
                    });
        } else {
            PluginManager.get(FavoritePlugin.class).getFavoriteRepository()
                    .favoriteMedia(mediaId, favorite)
                    .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {

                        @Override
                        protected void onRequestData(Object o) {
                            super.onRequestData(o);
                            GVideoSensorDataManager.getInstance().favoriteContent(dataModel.getVideoModel(), pid, !favorite, null);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            if (throwable instanceof TimeoutException ||
                                    throwable instanceof SocketTimeoutException ||
                                    throwable instanceof UnknownHostException) {
                                ToastUtils.showShort(R.string.all_network_not_available_action_tip);
                                return;
                            }
                            ToastUtils.showShort(throwable.getMessage());
                            GVideoSensorDataManager.getInstance().favoriteContent(dataModel.getVideoModel(), pid, !favorite, throwable.getMessage());
                        }
                    });
        }
    }
}
