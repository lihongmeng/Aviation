package com.hzlz.aviation.feature.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.hzlz.aviation.feature.share.data.ShareDataManager;
import com.hzlz.aviation.feature.share.dialog.CreateBillDialog;
import com.hzlz.aviation.feature.share.utils.QQShareHelper;
import com.hzlz.aviation.feature.share.utils.WeiboShareHelper;
import com.hzlz.aviation.feature.share.widget.ShareDialog;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;


/**
 * 分享 plugin实现类
 */
public class SharePluginImpl implements SharePlugin {

    @Override
    public void showShareDialog(
            Context context,
            boolean isDarkMode,
            ShareDataModel model,
            StatFromModel stat
    ) {
        showShareDialog(context, isDarkMode, false, model, stat);
    }

    @Override
    public void showShareDialog(
            Context context,
            boolean isDarkMode,
            boolean isHideMore,
            ShareDataModel model,
            StatFromModel stat
    ) {
        new ShareDialog(context, isDarkMode, isHideMore, model, stat).show();
    }

    @Override
    public void showCreateBillDialog(Activity activity) {
        new CreateBillDialog(activity).show();
    }

    @Override
    public void doShareResultIntent(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(
                    requestCode,
                    resultCode,
                    data,
                    QQShareHelper.getHelper().qqShareListener
            );
        } else {
            WeiboShareHelper.getHelper().doResult(data);
        }
    }

    @Override
    public void setShareConfig(
            boolean isCanShareWeiXin,
            boolean isCanShareQQ,
            boolean isCanShareWeibo
    ) {
        ShareDataManager.setCanShareConfig(isCanShareWeiXin, isCanShareQQ, isCanShareWeibo);
    }

    @Override
    public boolean isCanShare() {
        return ShareDataManager.isCanShare();
    }

    @Override
    public View getShareView(Context context, ShareDataModel model, StatFromModel stat) {
        View view = ShareDataManager.getShareView(context, model, stat);
        if (view ==null){
            view = new View(context);
        }
        return view;
    }

    @Override
    public void startWXShare(Context context, ShareDataModel dataModel, StatFromModel stat) {
        ShareDataManager.startWXShare(context,dataModel,stat);
    }

    @Override
    public void startWXCircleShare(Context context, ShareDataModel dataModel, StatFromModel stat) {
        ShareDataManager.startWXCircleShare(context,dataModel,stat);
    }
}
