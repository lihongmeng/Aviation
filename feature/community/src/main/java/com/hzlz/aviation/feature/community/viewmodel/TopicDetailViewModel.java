package com.hzlz.aviation.feature.community.viewmodel;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.CIRCLE;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.IS_QA;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.TOPIC_DETAIL;
import static com.hzlz.aviation.kernel.stat.stat.StatPid.PID_TOPIC_DETAIL;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.feature.community.CircleRepository;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.dialog.DefaultEnsureDialog;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.base.model.circle.CircleJoinStatus;
import com.hzlz.aviation.kernel.base.model.circle.TopicDetail;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.utils.DefaultPageNumberUtil;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IRecyclerModel;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageViewModel;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.Observable;

public class TopicDetailViewModel extends MediaPageViewModel {

    /**
     * ??????????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????Circle??????
     * ??????????????????
     */
    public Circle circle;

    // CircleRepository
    private final CircleRepository circleRepository = new CircleRepository();
    /**
     * ??????????????????????????????,????????????????????????
     */
    private final DefaultPageNumberUtil defaultPageNumberUtil = new DefaultPageNumberUtil();

    // ??????????????????
    public CheckThreadLiveData<TopicDetail> topicDetail = new CheckThreadLiveData<>();

    // ????????????????????????????????????????????????????????????
    public final CheckThreadLiveData<Boolean> autoRefreshLiveData = new CheckThreadLiveData<>();

    public String key;

    // ??????????????????????????????????????????????????????
    private DefaultEnsureDialog needJoinCommunityDialog;

    public TopicDetailViewModel(@NonNull Application application) {
        super(application);
        autoRefreshLiveData.setValue(true);
        defaultPageNumberUtil.initSp(PID_TOPIC_DETAIL);
    }

    /**
     * ???????????????????????????????????????
     */
    public Observable<ShortVideoListModel> loadMoreShortData() {
        return null;
    }

    public void checkNetworkAndLoginStatus() {
        if (!NetworkUtils.isNetworkConnected()) {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
            mMediaModelList.clear();
            mAdapter.refreshData(mMediaModelList);
        } else {
            updatePlaceholderLayoutType(PlaceholderType.NONE);
            autoRefreshLiveData.setValue(true);
        }
    }


    public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
        loadRefreshData();
    }

    public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
        loadMoreData();
    }

    @Override
    protected IRecyclerModel<MediaModel> createModel() {
        return (page, loadListener) -> {

            loadTopicInfo();

            // ??????????????????????????????
            loadContentList(page);

        };
    }

    public void loadTopicInfo() {
        TopicDetail data = topicDetail.getValue();
        if (data == null) {
            return;
        }

        // ?????????????????????????????????
        circleRepository.topicDetail(data.id)
                .subscribe(new GVideoResponseObserver<TopicDetail>() {
                    @Override
                    protected void onSuccess(@NonNull TopicDetail netData) {
                        circle = new Circle(netData);
                        topicDetail.setValue(netData);
                        key = TOPIC_DETAIL + "_" + netData.id;

                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                    }
                });

    }

    public void loadContentList(int page) {
        TopicDetail topicDetail = this.topicDetail.getValue();
        if (topicDetail == null) {
            return;
        }
        circleRepository.loadTopicDetailContentList(
                topicDetail.id,
                page,
                DEFAULT_PAGE_COUNT
        ).subscribe(new GVideoResponseObserver<ListWithPage<MediaModel>>() {
            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if (mMediaModelList.isEmpty()) {
                    updatePlaceholderLayoutType(PlaceholderType.LOADING);
                }
            }

            @Override
            protected void onSuccess(@NonNull ListWithPage<MediaModel> mediaListWithPage) {

                defaultPageNumberUtil.saveDefaultPageNumber(key, currPage);
                currPage = defaultPageNumberUtil.getDefaultPageNumber(key);

                List<MediaModel> list = mediaListWithPage.getList();
                List<MediaModel> modelList = new ArrayList<>();
                for (MediaModel media : list) {
                    if (media.isValid()) {
                        MediaModel model = new MediaModel(media);
                        model.tabId = mTabId;
                        model.setPid(getPid());
                        //model.playState = 0;
                        //model.viewPosition = 0;
                        model.correspondVideoModelAddress = media.getMemoryAddress();
                        modelList.add(model);
                    }
                }
                if (loadType == LOAD_DATA_TYPE_REFRESH && !mediaListWithPage.getPage().hasNextPage()) {
                    defaultPageNumberUtil.saveDefaultPageNumber(key, DefaultPageNumberUtil.MIN);
                }
                if (modelList.size() <= 0) {
                    if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
                        showToast(R.string.all_nor_more_data);
                        defaultPageNumberUtil.saveDefaultPageNumber(key, DefaultPageNumberUtil.MIN);
                    }
                } else {
                    loadSuccess(modelList);
                }
                loadComplete();
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (throwable instanceof TimeoutException ||
                        throwable instanceof SocketTimeoutException) {
                    showToast(R.string.all_network_not_available);
                    loadComplete();
                    return;
                }
                showToast(R.string.all_nor_more_data);
                loadComplete();
            }
        });
    }

    public void onPublishClick(View view) {
        if (!UserManager.hasLoggedIn()) {
            AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
            if (accountPlugin != null) {
                accountPlugin.startLoginActivity(view.getContext());
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(getPid()),
                        ResourcesUtils.getString(R.string.publish)
                );
            }
            return;
        }
        if (circle == null) {
            return;
        }
        // if (!InteractDataObservable.getInstance().getJoinCircleObservable(circle.groupId).get()) {
        //     showCanPublishDialog(view);
        //     return;
        // }
        TopicDetail topicDetailData = topicDetail.getValue();
        if (topicDetailData == null) {
            return;
        }
        if (topicDetailData.join == null || !topicDetailData.join) {
            joinCircle(view);
            return;
        }
        showNetworkDialog("????????????...");
        circleRepository.canPublish(circle.groupId, topicDetailData.id)
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        hideNetworkDialog();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(CIRCLE,circle);
                        bundle.putParcelable(TOPIC_DETAIL,topicDetailData);
                        bundle.putBoolean(IS_QA,false);
                        PluginManager.get(RecordPlugin.class)
                                .startPublishFragmentUseActivity(view.getContext(), bundle);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        hideNetworkDialog();
                        showToast(throwable.getMessage());
                    }
                });
    }

    private void showNeedJoinCommunityDialog(Context context) {
        if (needJoinCommunityDialog == null) {
            needJoinCommunityDialog = new DefaultEnsureDialog(context);
            needJoinCommunityDialog.init(
                    v -> {
                        needJoinCommunityDialog.dismiss();
                        onPublishClick(v);
                    },
                    ResourcesUtils.getString(R.string.publish_join_community_tip),
                    ResourcesUtils.getString(R.string.alright)
            );
            needJoinCommunityDialog.showJoinCommunityStyle();
        }
        needJoinCommunityDialog.show();
    }

    /**
     * ????????????
     */
    public void joinCircle(View v) {
        circleRepository.joinCircle(circle.groupId, "")
                .subscribe(new BaseResponseObserver<Object>() {
                    @Override
                    protected void onRequestData(Object o) {
                        Context context = v.getContext();
                        if (context == null || circle == null) {
                            return;
                        }
                        circle.setJoin(true);

                        TopicDetail topicDetailValue = topicDetail.getValue();
                        if (topicDetailValue == null) {
                            return;
                        }
                        topicDetailValue.join = true;

                        // showToast(ResourcesUtils.getString(R.string.join_success));

                        showNeedJoinCommunityDialog(context);

                        GVideoEventBus.get(
                                Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE,
                                CircleJoinStatus.class
                        ).post(new CircleJoinStatus(circle.groupId, true));

                        GVideoSensorDataManager.getInstance().followCommunity(
                                circle.groupId,
                                circle.name,
                                circle.tenantId,
                                circle.tenantName,
                                circle.labels,
                                StatPid.getPageName(StatPid.PID_TOPIC_DETAIL),
                                null
                        );
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        GVideoSensorDataManager.getInstance().followCommunity(
                                circle.groupId,
                                circle.name,
                                circle.tenantId,
                                circle.tenantName,
                                circle.labels,
                                StatPid.getPageName(StatPid.PID_TOPIC_DETAIL),
                                throwable.getMessage()
                        );
                        showToast(ResourcesUtils.getString(R.string.join_failed));
                    }
                });

    }

    @Override
    public void loadFailure(Throwable throwable) {
        if (loadType == LOAD_DATA_TYPE_LOAD_MORE) {
            // ????????????????????????
            if (currPage > 1) {
                //?????????????????????????????????????????????
                currPage--;
                defaultPageNumberUtil.saveDefaultPageNumber(key, currPage);
            }
        }
        AsyncUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                mView.loadFailure(throwable);
            }
        });
    }

    /**
     * ???????????????
     */
    public void initialData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_INITIAL;
        currPage = defaultPageNumberUtil.getDefaultPageNumber(key) + 1;
        mModel.initialData(this);
    }

    /**
     * ????????????
     */
    public void loadRefreshData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_REFRESH;
        currPage = defaultPageNumberUtil.getDefaultPageNumber(key) + 1;
        mModel.loadData(currPage, this);
    }

    /**
     * ????????????????????????
     */
    public void loadMoreData() {
        if (!checkBeforeLoad()) {
            loadComplete();
            return;
        }
        loadType = LOAD_DATA_TYPE_LOAD_MORE;
        currPage++;
        mModel.loadData(currPage, this);
    }

    public void onShareClicked(View v) {
        if (topicDetail.getValue() == null) {
            return;
        }
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setTitle(topicDetail.getValue().content)
                .setDescription(v.getContext().getString(R.string.share_default_description))
                .setUrl(topicDetail.getValue().shareUrl)
                .setShowCreateBill(false)
                .build();
        PluginManager.get(SharePlugin.class).showShareDialog(v.getContext(), false,
                true, shareDataModel, null);
    }
}
