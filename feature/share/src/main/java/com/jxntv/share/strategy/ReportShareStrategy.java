package com.jxntv.share.strategy;

import android.content.Context;

import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.ioc.PluginManager;
import com.jxntv.share.BuildConfig;

public class ReportShareStrategy implements ShareStrategy {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = ReportShareStrategy.class.getSimpleName();

    private Context mContext;

    public ReportShareStrategy(Context context) {
        mContext = context;
    }

    @Override
    public boolean canShare() {
        return true;
    }

    @Override
    public void share(ShareDataModel dataModel) {
        if (!NetworkTipUtils.checkNetworkOrTip(mContext)) {
            return;
        }
        PluginManager.get(AccountPlugin.class).showReportDialog(
                mContext,
                dataModel.getMediaId(),
                dataModel.isContentIsComment() ? 1 : 0,
                dataModel.getPid()
        );
    }
}
