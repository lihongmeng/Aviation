package com.hzlz.aviation.feature.share.strategy;

import android.content.Context;

import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.library.ioc.PluginManager;

public class ReportShareStrategy implements ShareStrategy {
    private static final boolean DEBUG = true;
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
