package com.hzlz.aviation.feature.account.ui.notification.detail;

import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.BUTTON_LINK;
import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.IMAGE_LINK;
import static com.hzlz.aviation.feature.account.model.annotation.MessageNotificationType.TEXT;

import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hzlz.aviation.feature.account.adapter.MessageNotificationDetailAdapter;
import com.hzlz.aviation.feature.account.model.MessageNotificationDetail;
import com.hzlz.aviation.feature.account.model.MessageNotificationDetailObservable;
import com.hzlz.aviation.feature.account.model.annotation.NotificationType;
import com.hzlz.aviation.feature.account.repository.MessageNotificationRepository;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.utils.NetworkTipUtils;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

/**
 * 消息通知详情列表 ViewModel
 *
 * @since 2020-03-12 09:47
 */
public final class MessageNotificationDetailViewModel extends BaseRefreshLoadMoreViewModel
        implements MessageNotificationDetailAdapter.Listener {
    //<editor-fold desc="属性">
    private int mSourceId;
    //
    @NonNull
    private CheckThreadLiveData<String> mTitleLiveData = new CheckThreadLiveData<>();
    //
    @NonNull
    private MessageNotificationDetailAdapter mAdapter = new MessageNotificationDetailAdapter();
    //
    @NonNull
    private MessageNotificationRepository mMessageNotificationRepository =
            new MessageNotificationRepository();
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public MessageNotificationDetailViewModel(@NonNull Application application) {
        super(application);
        mAdapter.setListener(this);
    }
    //</editor-fold>

    //<editor-fold desc="API">

    @NonNull
    LiveData<String> getTitleLiveData() {
        return mTitleLiveData;
    }

    /**
     * 处理参数
     *
     * @param args 参数
     */
    void processArguments(@NonNull MessageNotificationDetailFragmentArgs args) {
        mSourceId = args.getMsgType();
        mTitleLiveData.setValue(args.getTitle());
    }
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @NonNull
    @Override
    protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
        return mAdapter;
    }

    @Override
    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onRefresh(view);
        mMessageNotificationRepository
                .getMessageNotificationDetailList(mSourceId, 1, mLocalPage.getPageSize())
                .subscribe(new GVideoRefreshObserver<>(view));
    }

    @Override
    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        super.onLoadMore(view);
        mMessageNotificationRepository
                .getMessageNotificationDetailList(
                        mSourceId, mLocalPage.getPageNumber(), mLocalPage.getPageSize()
                )
                .subscribe(new GVideoLoadMoreObserver<>(view));
    }

    //</editor-fold>

    //<editor-fold desc="控件事件监听">
    @Override
    public void onItemClick(@NonNull MessageNotificationDetailAdapter adapter, @NonNull View view, int position) {
        MessageNotificationDetail detail = mAdapter.getData().get(position);
        switch (detail.getMsgType()) {
            case NotificationType.INTERACTIVE:
            case NotificationType.QA:
                if (detail.getComment() != null) {
                    VideoModel videoModel = new VideoModel();
                    Bundle extras = new Bundle();
                    if (detail.getComment().getCommentId() > 0) {
                        extras.putInt(VideoPlugin.EXTRA_START, VideoPlugin.START_COMMENT);
                        extras.putLong(VideoPlugin.EXTRA_START_COMMENT_ID, detail.getComment().getCommentId());
                        extras.putInt(VideoPlugin.EXTRA_START_COMMENT_TYPE, detail.getComment().getType());
                    }
                    videoModel.setId(detail.getComment().getMediaId());
                    videoModel.setMediaType(detail.getComment().getMediaType());
                    videoModel.setAnswerSquareId(detail.getAnswerSquareId());
                    PluginManager.get(DetailPagePlugin.class).dispatchToDetail(view.getContext(), videoModel, extras);
                }
                break;
            case NotificationType.SYSTEM:
                startToWebViewFragment(view, mAdapter.getData().get(position));
                break;
        }
    }

    @Override
    public void onButtonLinkClicked(@NonNull MessageNotificationDetailAdapter adapter, @NonNull View view, int position) {
        startToWebViewFragment(view, mAdapter.getData().get(position));
    }

    @Override
    public void onImageLinkClicked(@NonNull MessageNotificationDetailAdapter adapter, @NonNull View view, int position) {
        startToWebViewFragment(view, mAdapter.getData().get(position));
    }

    @Override
    public void onAvatarClicked(@NonNull MessageNotificationDetailAdapter adapter, @NonNull View view, int position) {
        MessageNotificationDetail detail = mAdapter.getData().get(position);
        AuthorModel authorModel = new AuthorModel();
        assert detail.getSourceId() != null;
        authorModel.setId(detail.getSourceId());
        authorModel.setType(detail.getMsgType() == NotificationType.PLATFORM ? AuthorType.PGC : AuthorType.UGC);
        PluginManager.get(AccountPlugin.class).startPgcActivity(view, authorModel);
    }

    @Override
    public void onAttentionClicked(@NonNull MessageNotificationDetailAdapter adapter, View view, int position) {
        if (!NetworkTipUtils.checkNetworkOrTip(view.getContext())) {
            return;
        }
        MessageNotificationDetail detail = mAdapter.getData().get(position);
        MessageNotificationDetailObservable observable = detail.getMessageNotificationDetailObservable();
        assert detail.getSourceId() != null;
        String name = detail.getTitle();
        String id = detail.getSourceId();
        int type = detail.getMsgType() == NotificationType.PLATFORM ? AuthorType.PGC : AuthorType.UGC;
        PluginManager.get(AccountPlugin.class).getFollowRepository()
                .followAuthor(id, type, name, !observable.isFollow.get()).subscribe(
                new BaseViewModel.BaseGVideoResponseObserver<Boolean>() {

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        GVideoSensorDataManager.getInstance().followAccount(!observable.isFollow.get(), id,
                                name, type, throwable.getMessage());
                    }
                });
    }

    //</editor-fold>

    //<editor-fold desc="内部方法">

    /**
     * 跳转 WebView 界面
     *
     * @param view   被点击的 View
     * @param detail 消息详情 {@link MessageNotificationDetail}
     */
    private void startToWebViewFragment(@NonNull View view, @NonNull MessageNotificationDetail detail) {
        switch (detail.getType()) {
            case BUTTON_LINK:
            case IMAGE_LINK:
                break;
            case TEXT:
            default:
                return;
        }
        String linkUrl = detail.getLinkUrl();
        if (TextUtils.isEmpty(linkUrl)) {
            return;
        }
        WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
        if (webViewPlugin == null) {
            return;
        }
        Bundle arguments = new Bundle();
        arguments.putString("url", linkUrl);
        webViewPlugin.startWebViewFragment(view, arguments);
    }
    //</editor-fold>
}
