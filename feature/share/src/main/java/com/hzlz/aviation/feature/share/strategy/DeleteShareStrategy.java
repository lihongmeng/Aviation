package com.hzlz.aviation.feature.share.strategy;


import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureCancelDialog;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.CompositionPlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.share.R;

public class DeleteShareStrategy implements ShareStrategy {

    private Context mContext;
    private DefaultEnsureCancelDialog exitTipDialog;

    public DeleteShareStrategy(Context context) {
        mContext = context;
    }

    @Override
    public boolean canShare() {
        return !TextUtils.isEmpty(PluginManager.get(AccountPlugin.class).getToken());
    }

    @Override
    public void share(ShareDataModel dataModel) {
        if (!canShare()) {
            Toast.makeText(mContext, R.string.share_login_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(mContext)) {
            return;
        }

        String tip = "确认删除该内容?";
        if (!TextUtils.isEmpty(dataModel.extraData)) {
            tip = dataModel.extraData;
        }

        final VideoModel videoModel = dataModel.getVideoModel();
        final boolean isAnswerType = videoModel != null && videoModel.isQaAnswerType();

        if (exitTipDialog == null) {
            exitTipDialog = new DefaultEnsureCancelDialog(mContext);
            exitTipDialog.init(
                    v -> exitTipDialog.dismiss(),
                    v -> {
                        if (isAnswerType) {
                            String commentId = videoModel.getCommentId();
                            if (TextUtils.isEmpty(commentId)) {
                                return;
                            }

                            PluginManager.get(VideoPlugin.class)
                                    .deleteComment(commentId)
                                    .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Object>() {
                                        @Override
                                        protected void onRequestData(Object o) {
                                            if (exitTipDialog != null) {
                                                exitTipDialog.dismiss();
                                            }
                                            GVideoEventBus.get(SharePlugin.EVENT_COMPOSITION_DELETE, String.class).post(commentId);
                                            Toast.makeText(mContext, R.string.share_other_delete_success, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        protected void onRequestError(Throwable throwable) {
                                            Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {

                            String mediaId = dataModel.getMediaId();
                            if (TextUtils.isEmpty(mediaId)) {
                                return;
                            }

                            PluginManager.get(CompositionPlugin.class)
                                    .getCompositionRepository()
                                    .deleteMedia(mediaId)
                                    .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {
                                        @Override
                                        protected void onRequestData(Boolean o) {
                                            if (exitTipDialog != null) {
                                                exitTipDialog.dismiss();
                                            }
                                            Toast.makeText(mContext, R.string.share_other_delete_success, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        protected void onRequestError(Throwable throwable) {
                                            Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    },
                    "",
                    tip
            );
        }
        exitTipDialog.show();
    }
}
